package de.mpa.db.storager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.compomics.util.protein.Header;

import de.mpa.db.accessor.OmssahitTableAccessor;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Searchspectrum;
import de.proteinms.omxparser.OmssaOmxFile;
import de.proteinms.omxparser.util.MSHitSet;
import de.proteinms.omxparser.util.MSHits;
import de.proteinms.omxparser.util.MSPepHit;
import de.proteinms.omxparser.util.MSSpectrum;

/**
 * Stores the omssa results to the DB.
 * @author Thilo Muth
 *
 */
public class OmssaStorager extends BasicStorager {

	  /**
     * Variable holding an OmssaOmxFile.
     */
	private OmssaOmxFile omxFile; 
    
    /**
     * The Omssa output filename without the absolute path!
     */
    private String filename;
    
    /**
     * The file instance.
     */
    private File file;
    
    /**
     * The file instance.
     */
    private File qValueFile;
    
    
    /**
     * The Connection instance.
     */
    private Connection conn;
    
    private Map<Double, List<Double>> scoreQValueMap;

	private HashMap<String, Long> hitNumberMap;    
   
    /**
     * Default Constructor.
     */
    public OmssaStorager(Connection conn, File file, File qValueFile){
    	this.conn = conn;
    	this.file = file;
    	this.qValueFile = qValueFile;
    }

    /**
     * Loads the OmssaFile.
     * @param file
     */
    public void load() {
        omxFile = new OmssaOmxFile(file.getAbsolutePath());
        filename = file.getName();
    }

    /**
     * Stores results from the OMSSA search engine to the database.
     * @param conn
     * @throws IOException
     * @throws SQLException
     */
    public void store() throws IOException, SQLException {
        // Iterate over all the spectra
        HashMap<MSSpectrum, MSHitSet> results = omxFile.getSpectrumToHitSetMap();
    	Iterator<MSSpectrum> iterator = results.keySet().iterator();  	
    	List<Double> qvalues;
    	// HitIndex as key, xtandemID as value.
    	hitNumberMap = new HashMap<String, Long>();
        
    	while (iterator.hasNext()) {
    		// Get the next spectrum.
    	    MSSpectrum msSpectrum = iterator.next();   
    	    MSHitSet msHitSet = results.get(msSpectrum);
    	    List<MSHits> hitlist = msHitSet.MSHitSet_hits.MSHits;
    	    int hitnumber = 1;
    	    for (MSHits msHit : hitlist) {
    	    	HashMap<Object, Object> hitdata = new HashMap<Object, Object>(11);    	    	
    	    	
    	    	// Get the spectrum id for the given spectrumName for the OmssaFile    
    	    	String spectrumName = msSpectrum.MSSpectrum_ids.MSSpectrum_ids_E.get(0).toString();
    	    	long spectrumid = Searchspectrum.getSpectrumIdFromSpectrumName(spectrumName, true);
    	    	hitdata.put(OmssahitTableAccessor.FK_SPECTRUMID, spectrumid);
    	    	
    	    	// Get the MSPepHit (for the accession)
    	    	List<MSPepHit> pepHits = msHit.MSHits_pephits.MSPepHit;
                Iterator<MSPepHit> pepHitIterator = pepHits.iterator();                
                MSPepHit pepHit = pepHitIterator.next();               
    	    	
    	        // Get the peptide id
                long peptideID = PeptideAccessor.findPeptideIdfromSequence(msHit.MSHits_pepstring, conn);
                hitdata.put(OmssahitTableAccessor.FK_PEPTIDEID, peptideID);                
    	    	hitdata.put(OmssahitTableAccessor.HITSETNUMBER, Long.valueOf(msHitSet.MSHitSet_number));
    	    	hitdata.put(OmssahitTableAccessor.EVALUE, msHit.MSHits_evalue);
    	    	hitdata.put(OmssahitTableAccessor.PVALUE, msHit.MSHits_pvalue);
    	    	hitdata.put(OmssahitTableAccessor.CHARGE, Long.valueOf(msHit.MSHits_charge));
    	    	hitdata.put(OmssahitTableAccessor.MASS, msHit.MSHits_mass);
    	    	hitdata.put(OmssahitTableAccessor.THEOMASS, msHit.MSHits_theomass);    	    	
    	    	hitdata.put(OmssahitTableAccessor.START, msHit.MSHits_pepstart);
    	    	hitdata.put(OmssahitTableAccessor.END, msHit.MSHits_pepstop);
    	    	
    	    	Long proteinID;
    	    	 // Parse the FASTA header
                Header header = Header.parseFromFASTA(pepHit.MSPepHit_defline);
                String accession = header.getAccession();
                String description = header.getDescription();
                
                // The Protein
                ProteinAccessor protein = ProteinAccessor.findFromAttributes(accession, conn);
                if (protein == null) {	// protein not yet in database
						// Add new protein to the database
						protein = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, conn);
					} else {
						proteinID = protein.getProteinid();
						// check whether pep2prot link already exists, otherwise create new one
						Pep2prot pep2prot = Pep2prot.findLink(peptideID, proteinID, conn);
						if (pep2prot == null) {	// link doesn't exist yet
							// Link peptide to protein.
							pep2prot = Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
						}
				}
                
    	    	// TODO: remove protein from db schema.
    	    	hitdata.put(OmssahitTableAccessor.PROTEIN, pepHit.MSPepHit_defline);
    	    	
//    	        qvalues = scoreQValueMap.get(round(msHit.MSHits_pvalue, 5));    	       
//    	        // Check for assigned q-value
//                if(qvalues != null){
//                	hitdata.put(OmssahitTableAccessor.QVALUE, qvalues.get(1));
//                }
                
    	    	// Create the database object.
    	    	OmssahitTableAccessor omssahit = new OmssahitTableAccessor(hitdata);
    	    	omssahit.persist(conn);		
    	    	
    	    	// Get the omssahitid
                Long omssahitid = (Long) omssahit.getGeneratedKeys()[0];
                hitNumberMap.put(msHitSet.MSHitSet_number + "_" + hitnumber, omssahitid);
                hitnumber++;
			}    
    	    conn.commit();
        }
    }
    
    private void processQValues() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(qValueFile));
			scoreQValueMap = new HashMap<Double, List<Double>>();
			String nextLine;
			// Skip the first line
			reader.readLine();
			List<Double> qvalityList;
			// Iterate over all the lines of the file.
			while ((nextLine = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(nextLine, "\t");
				List<String> tokenList = new ArrayList<String>();
				qvalityList = new ArrayList<Double>();
				
				// Iterate over all the tokens
				while (tokenizer.hasMoreTokens()) {
					tokenList.add(tokenizer.nextToken());
				}				
				double score = round(Double.valueOf(tokenList.get(0)), 5);
				qvalityList.add(Double.valueOf(tokenList.get(1)));
				qvalityList.add(Double.valueOf(tokenList.get(2)));
				scoreQValueMap.put(score, qvalityList);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public double round(double d, int decimalPlace){
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
      }

	@Override
	public void run() {
		this.load();
		//this.processQValues();
		try {
			this.store();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		log.info("Omssa results stored to the DB.");
	}   
}