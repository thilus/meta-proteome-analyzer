package de.mpa.db.neo4j.insert;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;

import com.compomics.util.Util;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.IndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;

import de.mpa.client.Client;
import de.mpa.client.ui.ClientFrame;
import de.mpa.db.neo4j.cypher.CypherQuery;
import de.mpa.db.neo4j.edges.RelationType;
import de.mpa.db.neo4j.io.GraphMLHandler;
import de.mpa.db.neo4j.nodes.NodeType;
import de.mpa.db.neo4j.properties.EdgeProperty;
import de.mpa.db.neo4j.properties.EnzymeProperty;
import de.mpa.db.neo4j.properties.ExperimentProperty;
import de.mpa.db.neo4j.properties.OntologyProperty;
import de.mpa.db.neo4j.properties.PathwayProperty;
import de.mpa.db.neo4j.properties.PeptideProperty;
import de.mpa.db.neo4j.properties.ProteinProperty;
import de.mpa.db.neo4j.properties.PsmProperty;
import de.mpa.db.neo4j.properties.TaxonProperty;
import de.mpa.db.neo4j.setup.GraphDatabase;
import de.mpa.model.analysis.UniProtUtilities;
import de.mpa.model.dbsearch.DbSearchResult;
import de.mpa.model.dbsearch.MetaProteinHit;
import de.mpa.model.dbsearch.PeptideHit;
import de.mpa.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.model.dbsearch.ProteinHit;
import de.mpa.model.dbsearch.UniProtEntryMPA;
import de.mpa.model.taxonomy.TaxonomyNode;

/**
 * GraphDatabaseHandler provides possibilities to insert data into the graph database.
 * 
 * @author Thilo Muth
 * @date 2014-12-11
 * @version 1.1.0
 *
 */
public class GraphDatabaseHandler {
	
	/**
	 * By default, the first operation on a TransactionalGraph will start a transaction automatically.
	 */
	private final TransactionalGraph graph;
	
	/**
	 *  An IndexableGraph is a graph that supports the manual indexing of its elements.
	 */
	private final IndexableGraph indexGraph;
	
	/**
	 * Graph database instance.
	 */
	private final GraphDatabase graphDb;
	
	/**
	 * Graph database execution engine.
	 */
	private final ExecutionEngine engine;
	
	/*
	 * Graph database indices.
	 */
	private Index<Vertex> experimentIndex;
	private Index<Vertex> proteinIndex;
	private Index<Vertex> peptideIndex;
	private Index<Vertex> psmIndex;
	private Index<Vertex> taxonomyIndex;
	private Index<Vertex> enzymeIndex;
	private Index<Vertex> pathwayIndex;
	private Index<Vertex> ontologyIndex;
	private Index<Edge> edgeIndex;

	/**
	 * Current experiment vertex.
	 */
	private Vertex currentExperimentVertex;
	
	/**
	 * Constructs the graph database by starting Neo4j engine and indices setup.
	 * @param graphDb
	 */
	public GraphDatabaseHandler(GraphDatabase graphDb) {
		this.graphDb = graphDb;
		GraphDatabaseService service = graphDb.getService();
        this.engine = new ExecutionEngine(service);
        this.indexGraph = new Neo4jGraph(service);
        this.graph = new Neo4jGraph(service);
        setupIndices();
	}
	
	/**
	 * Exports the graph to an GraphML output file.
	 */
	public void exportGraph(File outputFile) {
		Client client = Client.getInstance();
		String status = "FINISHED";
		client.firePropertyChange("new message", null, "EXPORTING GRAPHML FILE");
		client.firePropertyChange("indeterminate", false, true);
		try {
			GraphMLHandler.exportGraphML(this.graph, outputFile);
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			status = "FAILED";
		} 
		client.firePropertyChange("indeterminate", true, false);
		client.firePropertyChange("new message", null, "EXPORTING GRAPHML FILE " + status);
	}
	
	/**
	 * Sets the data object.
	 * @param data the data object to set.
	 */
	public void setData(DbSearchResult data) {
		ArrayList<ProteinHit> metaProteinHits = data.getAllProteinHits();
		Client client = Client.getInstance();
		client.firePropertyChange("new message", null, "ADDING " + "Experiment".toUpperCase() + " TO GRAPHDB");
		client.firePropertyChange("resetall", -1L, Long.valueOf(metaProteinHits.size()));
		
		// Add experiment
        addExperiment("Experiment", "Project");
		// Add protein hits
		for (ProteinHit proteinHit : metaProteinHits) {
            addProtein(proteinHit);
			client.firePropertyChange("progressmade", -1L, 0L);
		}
		// Add meta-proteins for processed results
//		if (!data.isRaw()) {
//			List<MetaProteinHit> metaProteins = data.getAllMetaProteins();
//			client.firePropertyChange("resetall", -1L, Long.valueOf(metaProteins.size()));
//			for (MetaProteinHit metaProtein : metaProteins) {
//                addMetaprotein(metaProtein);
//				client.firePropertyChange("progressmade", -1L, 0L);
//			}
//		}
		client.firePropertyChange("new message", null, "BUILDING GRAPH DATABASE FINISHED");
		
		// Stop the transaction
        stopTransaction();
		
	}
	
	/**
	 * Adds an experiment to the graph as vertex.
	 * @param experimentTitle Title of the experiment as identifier in the graphdb.
	 * @param projectTitle Title of the project.
	 */
	private void addExperiment(String experimentTitle, String projectTitle) {
        this.currentExperimentVertex = this.graph.addVertex(null);

        this.currentExperimentVertex.setProperty(ExperimentProperty.IDENTIFIER.toString(), experimentTitle);
        this.currentExperimentVertex.setProperty(ExperimentProperty.PROJECTTITLE.toString(), projectTitle);
		
		// Index the experiments by their title.
        this.experimentIndex.put(ProteinProperty.IDENTIFIER.toString(), experimentTitle, this.currentExperimentVertex);
		
		
	}
	
	/**
	 * Adds a protein to the graph as vertex.
	 * @param protHit Protein hit.
	 */
	private void addProtein(ProteinHit protHit) {
		// Get accession as identifier
		String accession = protHit.getAccession();
		
		Vertex proteinVertex = null;	
		Iterator<Vertex> proteinIterator = this.proteinIndex.get(ProteinProperty.IDENTIFIER.toString(), accession).iterator();
		if (proteinIterator.hasNext()) {
			proteinVertex = proteinIterator.next();
		} else {
			proteinVertex = this.graph.addVertex(null);
		}

		proteinVertex.setProperty(ProteinProperty.IDENTIFIER.toString(), accession);
		proteinVertex.setProperty(ProteinProperty.DESCRIPTION.toString(), protHit.getDescription());
		proteinVertex.setProperty(ProteinProperty.TAXONOMY.toString(), protHit.getTaxonomyNode().getName());
		proteinVertex.setProperty(ProteinProperty.MOLECULARWEIGHT.toString(), (Math.round(protHit.getMolecularWeight() * 100.0) / 100.0));
		proteinVertex.setProperty(ProteinProperty.COVERAGE.toString(), Util.roundDouble(protHit.getCoverage() * 100.0, 2));
		proteinVertex.setProperty(ProteinProperty.SPECTRALCOUNT.toString(), protHit.getSpectralCount());		
		
		// Index the proteins by their accession
        this.proteinIndex.put(ProteinProperty.IDENTIFIER.toString(), accession, proteinVertex);
		
		// Connect protein node to experiment node.
        this.addEdge(proteinVertex, this.currentExperimentVertex, RelationType.BELONGS_TO_EXPERIMENT);
		
		// Add taxonomy
        addTaxonomy(protHit, proteinVertex);
		
		// Add peptides
        addPeptides(protHit.getPeptideHitList(), proteinVertex);
		
		// Add enzyme numbers
		UniProtEntryMPA uniprotEntry = protHit.getUniProtEntry();
		// Null check is needed, as if no (reduced) UniProt Entry is provided, enzymes, ontologies, pathways and taxonomies can be skipped
		if (uniprotEntry != null) {
			
			List<String> ecNumbers = uniprotEntry.getEcnumbers();
            addEnzymeNumbers(ecNumbers, proteinVertex);
			
			// Add pathways.		
			List<String> koNumbers = uniprotEntry.getKonumbers();
            addPathways(koNumbers, proteinVertex);
			
			// Add ontologies
            addOntologies(protHit, proteinVertex);
		}
	}
	
	/**
	 * Adds a meta-protein to the graph as vertex.
	 * @param metaProteinHit
	 */
	private void addMetaprotein(MetaProteinHit metaProteinHit) {
		Vertex proteinVertex = null;
		// Get all proteins from one meta-protein.
		ArrayList<ProteinHit> proteinHits = metaProteinHit.getProteinHitList();
		Vertex metaProteinVertex = null;
		
		// Iterate the proteins to see whether there are already assigned meta-proteins.
		for (ProteinHit proteinHit : proteinHits) {
			Iterator<Vertex> proteinIterator = this.proteinIndex.get(ProteinProperty.IDENTIFIER.toString(), proteinHit.getAccession()).iterator();
			if (proteinIterator.hasNext()) {
				proteinVertex = proteinIterator.next();
			}
			
			// Case 1: The protein already belongs to a meta-protein (from another experiment).
			Iterator<Edge> edgesIterator = proteinVertex.getEdges(Direction.IN, "IS_METAPROTEIN_OF").iterator();
			if (edgesIterator.hasNext()) {
				Edge edge = edgesIterator.next();
				metaProteinVertex = edge.getVertex(Direction.OUT);
			} 
		}
		
		// Case 2: No meta-protein vertex has been existing: create a new one.
		if (metaProteinVertex == null) {
			metaProteinVertex = this.graph.addVertex(null);
			String accession = metaProteinHit.getAccession();
			String description = metaProteinHit.getDescription();
			int index = description.indexOf("OS=");
			if (index != -1) {
				description = description.substring(0, index);
			}
			metaProteinVertex.setProperty(ProteinProperty.IDENTIFIER.toString(), accession + " (" + description + ")");
			metaProteinVertex.setProperty(ProteinProperty.DESCRIPTION.toString(), metaProteinHit.getDescription());
			TaxonomyNode taxonomyNode = metaProteinHit.getTaxonomyNode();
			if (taxonomyNode != null) {
				metaProteinVertex.setProperty(ProteinProperty.TAXONOMY.toString(), taxonomyNode.getName());
			}
			metaProteinVertex.setProperty(ProteinProperty.PROTEINCOUNT.toString(), metaProteinHit.getProteinHitList().size());
			
			// Index the proteins by their accession.
            this.proteinIndex.put(ProteinProperty.IDENTIFIER.toString(), accession, metaProteinVertex);
		}	
		
		// Iterate the proteins again to connect the edges.
		for (ProteinHit proteinHit : proteinHits) {
			Iterator<Vertex> proteinIterator = this.proteinIndex.get(ProteinProperty.IDENTIFIER.toString(), proteinHit.getAccession()).iterator();
			if (proteinIterator.hasNext()) {
				proteinVertex = proteinIterator.next();
			}
			// Add edge between meta-protein and protein.
            this.addEdge(metaProteinVertex, proteinVertex, RelationType.IS_METAPROTEIN_OF);
		}
		// Connect meta-protein node to experiment node.
        this.addEdge(metaProteinVertex, this.currentExperimentVertex, RelationType.BELONGS_TO_EXPERIMENT);
	}
	
	/**
	 * Adds the taxonomic information derived from the protein hit to the graph.
	 * @param proteinHit ProteinHit object with taxonomic information.
	 * @param proteinVertex Involved protein vertex (outgoing edge).
	 */
	private void addTaxonomy(ProteinHit proteinHit, Vertex proteinVertex) {
		TaxonomyNode childNode = proteinHit.getTaxonomyNode();
		String species = childNode.toString();
		Vertex childVertex = null;
		Iterator<Vertex> taxonomyIterator = this.taxonomyIndex.get(TaxonProperty.IDENTIFIER.toString(), species).iterator();
		if (taxonomyIterator.hasNext()) {
			childVertex = taxonomyIterator.next();
		} else {
			// Create new vertex.
			childVertex = this.graph.addVertex(null);
			childVertex.setProperty(TaxonProperty.IDENTIFIER.toString(), species);
			childVertex.setProperty(TaxonProperty.TAXID.toString(), childNode.getID());
			childVertex.setProperty(TaxonProperty.RANK.toString(), childNode.getRank().toString());
			// Index the species by the species name.
            this.taxonomyIndex.put(TaxonProperty.IDENTIFIER.toString(), species, childVertex);
		}		
		// Add edge between protein and species.
        this.addEdge(proteinVertex, childVertex, RelationType.BELONGS_TO_TAXONOMY);
		
		Vertex parentVertex = null;	
		// Add complete taxonomy path.
		TaxonomyNode[] path = childNode.getPath();
		StringBuilder builder = new StringBuilder();
		for (int i = path.length-2; i >= 0; i--) {
			String taxon = path[i].toString();
			builder.append(path[i] +" :: ");
			Iterator<Vertex> iterator = this.taxonomyIndex.get(TaxonProperty.IDENTIFIER.toString(), taxon).iterator();
			if (iterator.hasNext()) {
				parentVertex = iterator.next();
			} else {
				// Create new vertex.
				parentVertex = this.graph.addVertex(null);
				parentVertex.setProperty(TaxonProperty.IDENTIFIER.toString(), taxon);
				parentVertex.setProperty(TaxonProperty.TAXID.toString(), path[i].getID());
				parentVertex.setProperty(TaxonProperty.RANK.toString(), path[i].getRank().toString());
				// Index the species by the species name.
                this.taxonomyIndex.put(TaxonProperty.IDENTIFIER.toString(), taxon, parentVertex);
			}		
			// Add edge between parent and child vertex
            this.addEdge(parentVertex, childVertex, RelationType.IS_ANCESTOR_OF);
			childVertex = parentVertex;
		}
	}
	
	/**
	 * Adds an enzyme number to the graph as vertex.
	 * @param ecNumber Enzyme number to be added.
	 * @param proteinVertex Involved protein vertex (outgoing edge).
	 */
	private void addEnzymeNumbers(List<String> ecNumbers, Vertex proteinVertex) {
		Vertex enzymeVertex = null;
		for (String ecNumber : ecNumbers) {
			Iterator<Vertex> vertexIterator =
                    this.enzymeIndex.get(EnzymeProperty.IDENTIFIER.toString(), ecNumber).iterator();
			if (vertexIterator.hasNext()) {
				enzymeVertex = vertexIterator.next();
			} else {
				// Split E.C. number string of the format '1.2.3.4'
				String[] ecTokens = ecNumber.split("\\.");
				for (int i = 0; i < ecTokens.length; i++) {
					// Check tokens, abort prematurely if '-' is found
					String ecToken = ecTokens[i];
					if ("-".equals(ecToken)) {
						break;
					}
					// Build partial E.C. number, e.g. '1.2.-.-'
					String ecFragment = ecTokens[0];
					for (int j = 1; j < ecTokens.length; j++) {
						ecFragment += "." + ((j <= i) ? ecTokens[j] : "-");
					}
					// Get existing vertex for (partial) E.C. number...
					Vertex temp = this.graph.getVertex(ecFragment);
					if (temp == null) {
						// ... or create new vertex.
						temp = this.graph.addVertex(ecFragment);
						temp.setProperty(EnzymeProperty.IDENTIFIER.toString(), ecFragment);
						// TODO set description property, fetch from intenz.xml (ftp://ftp.ebi.ac.uk/pub/databases/intenz/xml/)
					}
					// Index vertex by E.C. Number.
                    this.enzymeIndex.put(EnzymeProperty.IDENTIFIER.toString(), ecFragment, temp);
					
					// Link enzyme vertices
					if (i > 0) {
                        this.addEdge(enzymeVertex, temp, RelationType.IS_SUPERGROUP_OF);
					}
					
					enzymeVertex = temp;
				}
			}
			// Add edge between protein and enzyme number.
            this.addEdge(proteinVertex, enzymeVertex, RelationType.BELONGS_TO_ENZYME);
		}
	}
	
	/**
	 * Adds KEGG pathways to the database.
	 * @param xRefs List of database cross references.
	 * @param proteinVertex Involved protein vertex (outgoing edge).
	 */
	private void addPathways(List<String> koNumbers, Vertex proteinVertex) {
		Vertex pathwayVertex = null;
		for (String koNumber : koNumbers) {
			Iterator<Vertex> pathwayIterator = this.pathwayIndex.get(PathwayProperty.IDENTIFIER.toString(), koNumber).iterator();
			if (pathwayIterator.hasNext()) {
				pathwayVertex = pathwayIterator.next();
			} else {
				// Create new vertex.
				pathwayVertex = this.graph.addVertex(null);
				pathwayVertex.setProperty(PathwayProperty.IDENTIFIER.toString(), koNumber);
				//TODO: Add KEGG description + KEGG Number!
				// Index the pathway by the ID.
                this.pathwayIndex.put(PathwayProperty.IDENTIFIER.toString(), koNumber, pathwayVertex);
			}
			// Add edge between protein and pathway.
            this.addEdge(proteinVertex, pathwayVertex, RelationType.BELONGS_TO_PATHWAY);
		}
	}
	
	/**
	 * Adds ontologies to the graph database.
	 * @param protHit
	 * @param proteinVertex
	 */
	private void addOntologies(ProteinHit protHit, Vertex proteinVertex) {
		Map<String, UniProtUtilities.Keyword> ontologyMap = UniProtUtilities.ONTOLOGY_MAP;
		UniProtEntryMPA entry = protHit.getUniProtEntry();
		Vertex ontologyVertex = null;
		
		// Entry must be provided
		if (entry != null) {
			List<String> keywords = entry.getKeywords();			
			for (String keyword : keywords) {
				if (ontologyMap.containsKey(keyword)) {
					UniProtUtilities.KeywordCategory type = UniProtUtilities.KeywordCategory.valueOf(ontologyMap.get(keyword).getCategory());
					
					// Check if peptide is already contained in the graph.
					Iterator<Vertex> ontologyIterator =
                            this.ontologyIndex.get(OntologyProperty.IDENTIFIER.toString(), keyword).iterator();
					if (ontologyIterator.hasNext()) {
						ontologyVertex = ontologyIterator.next();
					} else {
						// Create new vertex.
						ontologyVertex = this.graph.addVertex(null);
						ontologyVertex.setProperty(OntologyProperty.IDENTIFIER.toString(), keyword);
						ontologyVertex.setProperty(OntologyProperty.TYPE.toString(), type.toString());
						
						// Index the proteins by their accession.
                        this.ontologyIndex.put(OntologyProperty.IDENTIFIER.toString(), keyword, ontologyVertex);
					}			
					
					// Add edge between protein and ontology.
					switch (type) {
					case BIOLOGICAL_PROCESS:
                        this.addEdge(proteinVertex, ontologyVertex, RelationType.INVOLVED_IN_BIOPROCESS);
						break;
					case CELLULAR_COMPONENT:
                        this.addEdge(proteinVertex, ontologyVertex, RelationType.BELONGS_TO_CELL_COMP);
						break;
					case MOLECULAR_FUNCTION:
                        this.addEdge(proteinVertex, ontologyVertex, RelationType.HAS_MOLECULAR_FUNCTION);
						break;	
					default:
                        this.addEdge(proteinVertex, ontologyVertex, RelationType.BELONGS_TO);
						break;
					}
					
				}
			}
		}
	}
	
	/**
	 * Adds peptides to the graph.
	 * @param peptideHitList The peptide list
	 */
	private void addPeptides(List<PeptideHit> peptideHitList, Vertex proteinVertex) {
		for (PeptideHit peptideHit : peptideHitList) {
			String sequence = peptideHit.getSequence();
			
			Vertex peptideVertex = null;
			// Check if peptide is already contained in the graph.
			Iterator<Vertex> peptideIterator = this.peptideIndex.get(PeptideProperty.IDENTIFIER.toString(), sequence).iterator();
			if (peptideIterator.hasNext()) {
				peptideVertex = peptideIterator.next();
			} else {
				// Create new vertex.
				peptideVertex = this.graph.addVertex(null);
				peptideVertex.setProperty(PeptideProperty.IDENTIFIER.toString(), sequence);
				String description = "Start: " + peptideHit.getStart() + " End: " + peptideHit.getEnd();
				peptideVertex.setProperty(PeptideProperty.DESCRIPTION.toString(), description);
				peptideVertex.setProperty(PeptideProperty.SPECTRALCOUNT.toString(), peptideHit.getSpectralCount());
				TaxonomyNode taxonomyNode = peptideHit.getTaxonomyNode();
				if (taxonomyNode != null) {
					peptideVertex.setProperty(PeptideProperty.TAXONOMY.toString(), taxonomyNode.getName());
				}
				peptideVertex.setProperty(PeptideProperty.MOLECULARWEIGHT.toString(), (Math.round(peptideHit.getMolecularWeight() * 100.0) / 100.0));
				peptideVertex.setProperty(PeptideProperty.PROTEINCOUNT.toString(), peptideHit.getProteinCount());
				
				// Index the proteins by their accession.
                this.peptideIndex.put(PeptideProperty.IDENTIFIER.toString(), sequence, peptideVertex);
			}			
			
			// Add edge between peptide and protein.
            this.addEdge(proteinVertex, peptideVertex, RelationType.HAS_PEPTIDE);
		    
			// Connect peptide taxon node name to peptide vertex.
//			addSpecies(peptideHit.getTaxonomyNode().getName(), peptideVertex);
			
			// Add PSMs.
            this.addPeptideSpectrumMatches(peptideHit.getPeptideSpectrumMatches(), peptideVertex);
			
			// Connect peptide node to experiment node.
            this.addEdge(peptideVertex, this.currentExperimentVertex, RelationType.BELONGS_TO_EXPERIMENT);
		}
	}
	
	/**
	 * Adds PSMs to the graph.
	 * @param peptideHitList The peptide list
	 */
	private void addPeptideSpectrumMatches(List<PeptideSpectrumMatch> spectrumMatches, Vertex peptideVertex) {
		for (PeptideSpectrumMatch sm : spectrumMatches) {
			Vertex psmVertex =  null;
			PeptideSpectrumMatch psm = (PeptideSpectrumMatch) sm;
			
			long spectrumID = psm.getSpectrumID();
			
			// Check if PSM is already contained in the graph.
			Iterator<Vertex> psmIterator = this.psmIndex.get(PsmProperty.SPECTRUMID.toString(), spectrumID).iterator();
			if (psmIterator.hasNext()) {
				psmVertex = psmIterator.next();
			} else {
				// Create new vertex.
				psmVertex = this.graph.addVertex(null);
//				psmVertex.setProperty(PsmProperty.SPECTRUMID.toString(), spectrumID);
				psmVertex.setProperty(PsmProperty.TITLE.toString(), psm.getTitle());
				psmVertex.setProperty(PsmProperty.VOTES.toString(), psm.getVotes());

				psmVertex.setProperty(PsmProperty.IDENTIFIER.toString(), spectrumID);
				
				// Index the proteins by their accession.
                this.psmIndex.put(PsmProperty.SPECTRUMID.toString(), spectrumID, psmVertex);
			}
            this.addEdge(psmVertex, peptideVertex, RelationType.IS_MATCH_IN);
			
			// Connect PSM node to experiment node.
            this.addEdge(psmVertex, this.currentExperimentVertex, RelationType.BELONGS_TO_EXPERIMENT);
		}
	}
	
	/**
	 * Adds an edge to the graph and creates a unique ID from both involved vertices.
	 * Also checks whether edge is already existing in the graph.
	 * @param outVertex The outgoing vertex.
	 * @param inVertex The incoming vertex.
	 * @param label The edge label.
	 */
	private void addEdge(Vertex outVertex, Vertex inVertex, RelationType relType) {
		String id = outVertex.getId()+ "_" + inVertex.getId();
		// Check if edge is not already contained in the graph.
		if (!this.edgeIndex.get(EdgeProperty.ID.toString(), id).iterator().hasNext()) {
			Edge edge = this.graph.addEdge(id, outVertex, inVertex, relType.name());
			edge.setProperty(EdgeProperty.LABEL.name(), relType.name());
            this.edgeIndex.put(EdgeProperty.ID.toString(), id, edge);
		} 
	}
	
	/**
	 * Method sets up the indices graph.
	 */
	public void setupIndices() {

		this.experimentIndex = this.indexGraph.getIndex(NodeType.EXPERIMENTS.toString(), Vertex.class);
		if (this.experimentIndex == null) {
			this.experimentIndex = this.indexGraph.createIndex(NodeType.EXPERIMENTS.toString(), Vertex.class);
			((TransactionalGraph) this.indexGraph).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		}


		// Protein index
		proteinIndex = indexGraph.getIndex(NodeType.PROTEINS.toString(), Vertex.class);

		// If not existent, create a new index.
		if (proteinIndex == null) {
			proteinIndex = indexGraph.createIndex(NodeType.PROTEINS.toString(), Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		}

		// Peptide Index
		peptideIndex = indexGraph.getIndex(NodeType.PEPTIDES.toString(), Vertex.class);
		// If not existent, create a new index.
		if (peptideIndex == null) {
			peptideIndex = indexGraph.createIndex(NodeType.PEPTIDES.toString(), Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		}

		// PSM index
		psmIndex = indexGraph.getIndex(NodeType.PSMS.toString(), Vertex.class);
		// If not existent, create a new index.
		if (psmIndex == null) {
			psmIndex = indexGraph.createIndex(NodeType.PSMS.toString(), Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		}

		// Species index
		taxonomyIndex = indexGraph.getIndex(NodeType.TAXA.toString(), Vertex.class);
		// If not existent, create a new index.
		if (taxonomyIndex == null) {
			taxonomyIndex = indexGraph.createIndex(NodeType.TAXA.toString(), Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		}

		// Enzyme index
		enzymeIndex = indexGraph.getIndex(NodeType.ENZYMES.toString(), Vertex.class);
		// If not existent, create a new index.
		if (enzymeIndex == null) {
			enzymeIndex = indexGraph.createIndex(NodeType.ENZYMES.toString(), Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		}

		// Pathway index
		pathwayIndex = indexGraph.getIndex(NodeType.PATHWAYS.toString(), Vertex.class);
		// If not existent, create a new index.
		if (pathwayIndex == null) {
			pathwayIndex = indexGraph.createIndex(NodeType.PATHWAYS.toString(), Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		}

		// Ontology index
		ontologyIndex = indexGraph.getIndex(NodeType.ONTOLOGIES.toString(), Vertex.class);
		// If not existent, create a new index.
		if (ontologyIndex == null) {
			ontologyIndex = indexGraph.createIndex(NodeType.ONTOLOGIES.toString(), Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		}

		// Edge index
		edgeIndex = indexGraph.getIndex("edges", Edge.class);
		// If not existent, create a new index.
		if (edgeIndex == null) {
			edgeIndex = indexGraph.createIndex("edges", Edge.class);
			((TransactionalGraph) indexGraph).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		}
	}

	/**
	 * Returns the graph database service.
	 * @return {@link GraphDatabaseService}
	 */
	public GraphDatabaseService getGraphDatabaseService() {
		return graphDb.getService();
	}

	/**
	 * Executes a cypher query {@link CypherQuery}.
	 * @param cypherQuery {@link CypherQuery}
	 * @return {@link ExecutionResult}
	 * @throws Exception
	 */
	public ExecutionResult executeCypherQuery(CypherQuery cypherQuery) throws Exception {
		// Validate query
		if (cypherQuery.isValid()) {
			return engine.execute(cypherQuery.toString());
		} else {
			throw new Exception("Invalid Cypher Query");
		}
	}

	/**
	 * Stops the transaction of a graph database.
	 */
	public void stopTransaction() {
		graph.stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		((TransactionalGraph)indexGraph).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		
	}
	
	/**
	 * Shuts down the graph database.
	 */
	public void shutDown() {
        this.graphDb.shutDown();
	}
	
}