package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ui.Busyable;
import de.mpa.client.ui.ButtonTabbedPane;
import de.mpa.client.ui.CheckBoxTreeTableNode;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ConfirmFileChooser;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.RolloverButtonUI;
import de.mpa.client.ui.SortableCheckBoxTreeTable;
import de.mpa.client.ui.SortableCheckBoxTreeTableNode;
import de.mpa.client.ui.SortableTreeTableModel;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.dialogs.GraphQueryDialog;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.graphdb.cypher.CypherQuery;
import de.mpa.graphdb.io.QueryResultExporter;

public class GraphDatabaseResultPanel extends JPanel implements Busyable {
	
	/**
	 * Sortable checkbox treetable instance.
	 */
	private SortableCheckBoxTreeTable resultsTreeTbl;
	
	/**
	 * Graph database query dialog button.
	 */
	private JButton queryBtn;
	
	/**
	 * GraphDatabaseResultPanel instance.
	 */
	private GraphDatabaseResultPanel resultDatabaseResultPnl;
	
	/**
	 * Column identifiers.
	 */
	private List<String> columnIdentifiers;
	
	/**
	 * Last chosen CypherQuery.
	 */
	private CypherQuery lastCypherQuery = null;
	
	/**
	 * Export query results button.
	 */
	private JButton exportBtn;
	
	/**
	 * The GraphDatabaseResultPanel.
	 * @param clientFrame The client frame.
	 */
	public GraphDatabaseResultPanel() {
		this.resultDatabaseResultPnl = this;
		initComponents();
	}
	
	/**
	 * Initialize the components.
	 */
	private void initComponents() {
		CellConstraints cc = new CellConstraints();
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, f:p, 5dlu"));
		
        // Build the spectrum overview panel
        JPanel graphDbPanel = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));

		JXTitledPanel graphDbTtlPnl = PanelConfig.createTitledPanel("GraphDB Query Results", graphDbPanel);
		
		// Setup the table
		setupQueryResultsTable();
		
		JPanel buttonPnl = new JPanel(new FormLayout("p, c:5dlu, p, 3dlu", "f:20px"));
		buttonPnl.setOpaque(false);
		
		exportBtn = new JButton(IconConstants.EXCEL_EXPORT_ICON);
		exportBtn.setRolloverIcon(IconConstants.EXCEL_EXPORT_ROLLOVER_ICON);
		exportBtn.setPressedIcon(IconConstants.EXCEL_EXPORT_PRESSED_ICON);
		exportBtn.setToolTipText("Export Table Contents");
		exportBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(exportBtn));
		
		exportBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent evt) {
				exportQueryResultsButtonTriggered();
			}
		});
		exportBtn.setEnabled(false);
		
		queryBtn = new JButton(IconConstants.GO_DB_ICON);
		queryBtn.setRolloverIcon(IconConstants.GO_DB_ROLLOVER_ICON);
		queryBtn.setPressedIcon(IconConstants.GO_DB_PRESSED_ICON);
		queryBtn.setToolTipText("Perform Graph Database Query");
		queryBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(queryBtn));
		
		queryBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent evt) {
				new GraphQueryDialog(ClientFrame.getInstance(), resultDatabaseResultPnl, "GraphDB Query Dialog", true);
			}
		});
		
		buttonPnl.add(exportBtn, CC.xy(1, 1));
		buttonPnl.add(new JSeparator(SwingConstants.VERTICAL), CC.xy(2, 1));
		buttonPnl.add(queryBtn, CC.xy(3, 1));
		
		graphDbTtlPnl.setRightDecoration(buttonPnl);
		JScrollPane firstDimResultsTblScp = new JScrollPane(resultsTreeTbl);
		firstDimResultsTblScp.setPreferredSize(new Dimension(400, 210));
		
		firstDimResultsTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		graphDbPanel.add(firstDimResultsTblScp,cc.xy(2, 2));
		
	    this.add(graphDbTtlPnl, cc.xyw(2, 2, 3));
	}
	
    /**
     * Executed when the graphDb results export button is triggered. Via a file chooser the user can select the destination of the CSV file.
     */
	protected void exportQueryResultsButtonTriggered() {

		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				JFileChooser chooser = new ConfirmFileChooser();
				chooser.setFileFilter(Constants.CSV_FILE_FILTER);
				chooser.setAcceptAllFileFilterUsed(false);
				int returnVal = chooser.showSaveDialog(ClientFrame.getInstance());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selFile = chooser.getSelectedFile();
					if (selFile != null) {
						String filePath = selFile.getPath();
						if (!filePath.toLowerCase().endsWith(".tsv")) {
							filePath += ".tsv";
						}
						exportQueryResults(filePath);
					}
				}
				return null;
			}
		}.execute();
	}
	
	/**
	 * Methods for exporting the query result.
	 * @param filePath Path to export TSV file.
	 * @throws IOException
	 */
	private void exportQueryResults(String filePath) throws IOException {
		Client client = Client.getInstance();
		String status = "FINISHED";
		client.firePropertyChange("new message", null, "EXPORTING QUERY RESULTS FILE");
		client.firePropertyChange("indeterminate", false, true);
		try {
			QueryResultExporter.exportResults(filePath,	resultsTreeTbl);
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			status = "FAILED";
		} 
		client.firePropertyChange("indeterminate", true, false);
		client.firePropertyChange("new message", null, "EXPORTING QUERY RESULTS FILE " + status);
	}
	
	/**
     * This method sets the spectra table up.
     */
    private void setupQueryResultsTable() {
        // Initialize query results tree table.
		resultsTreeTbl = new SortableCheckBoxTreeTable(
				new SortableTreeTableModel(new SortableCheckBoxTreeTableNode()) {
					{
						setColumnIdentifiers(Arrays
								.asList(new String[] { " " }));
					}
				});
        
        // Single selection only
        resultsTreeTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTreeTbl.setSelectionBackground(new Color(130, 207, 250));

        // Add nice striping effect
        resultsTreeTbl.addHighlighter(TableConfig.getSimpleStriping());
        resultsTreeTbl.getTableHeader().setReorderingAllowed(true);
        
        // Enables column control
        TableConfig.configureColumnControl(resultsTreeTbl);
    }

    /**
     * Method invoked when the Get Results button is pressed.
     */
    public void updateResults(ExecutionResult result) {
    	
        SortableCheckBoxTreeTableNode root = new SortableCheckBoxTreeTableNode();

    	List<String> resultColumns = result.columns();
    	columnIdentifiers = new ArrayList<String>();
    	
		boolean first = true;
		boolean isEmpty = true;
		for (Map<String, Object> map : result) {
    		isEmpty = false;
			// Fill column identifiers list
    		if (first) {
    			for (String col : resultColumns) {
    				Node graphNode = (Node) map.get(col);
    				for (String key : graphNode.getPropertyKeys()) {
    					if (!columnIdentifiers.contains(key)) {
        					columnIdentifiers.add(key);
    					}
    				}
				}
    		}
			
    		// iterate map and chain together elements as tree nodes
    		SortableCheckBoxTreeTableNode parentNode = null;
    		for (String col : resultColumns) {
				Node graphNode = (Node) map.get(col);
    			SortableCheckBoxTreeTableNode childNode = this.convertGraphToTreeNode(graphNode);
    			if (parentNode != null) {
    				parentNode.add(childNode);
    			}
    			parentNode = childNode;
    		}
    		// get first element of node chain
    		TreePath newPath = parentNode.getPath();
    		
			Object newChild = newPath.getPathComponent(0);
    		
			// find node in existing tree to which (part of) the node chain shall be added
			CheckBoxTreeTableNode insertionNode =
					(CheckBoxTreeTableNode) this.findInsertionNode(root, (TreeTableNode) newChild);
    		
    		// determine insertion depth
    		int depth = insertionNode.getPath().getPathCount() - 1;
    		
    		// insert sub-chain as child of insertion node
    		insertionNode.add((MutableTreeTableNode) newPath.getPathComponent(depth));
    		first = false;
		}
		
		// Results are available.
		if (!isEmpty) {
			
			// force root to have a column count equal to the number of column identifiers
			root.setUserObjects(columnIdentifiers.toArray());
			
			// create new model using collected column identifiers
			SortableTreeTableModel model = new SortableTreeTableModel(root);
			model.setColumnIdentifiers(columnIdentifiers);
			
			// insert model into table
	    	resultsTreeTbl.setTreeTableModel(model);
	    	
	    	// Enable results export
	    	exportBtn.setEnabled(true);
	        
		} else {
			// Display notification that no results have been found
			JOptionPane.showMessageDialog(ClientFrame.getInstance(), "Found no results for query.");
			
			// Disable results export.
			exportBtn.setEnabled(false);
		}
	}
    
    /**
	 * Convenience method to recursively find an existing tree node inside a
	 * tree structure headed by the specified parent node into which the specified
	 * child node shall be inserted.
	 * 
	 * @param parent the root of the tree (or subtree) structure
	 * @param child2find the child to insert into the tree structure
	 * @return the insertion node
	 */
    private TreeTableNode findInsertionNode(TreeTableNode parent, TreeTableNode child2find) {
    	for (int i = 0; i < parent.getChildCount(); i++) {
			TreeTableNode treeChild = parent.getChildAt(i);
			// FIXME: exception is thrown, if child count == 0
			if (treeChild.equals(child2find) && child2find.getChildCount() > 0) {
				return findInsertionNode(treeChild, child2find.getChildAt(0));
			}
		}
    	return parent;
    }
    
    /**
     * Converts a graph-related node into a tree node for visualization.
     * @param node Graph database node
     * @return {@link SortableCheckBoxTreeTableNode}
     */
	private SortableCheckBoxTreeTableNode convertGraphToTreeNode(Node node) {
		Object[] values = new Object[columnIdentifiers.size()];
		for (String key : node.getPropertyKeys()) {
			int index = columnIdentifiers.indexOf(key);
			values[index] = node.getProperty(key);
		}
		SortableCheckBoxTreeTableNode tableNode = new SortableCheckBoxTreeTableNode(values) {
			@Override
			public boolean equals(Object obj) {
				if (obj instanceof TreeTableNode && obj != null) {
					TreeTableNode that = (TreeTableNode) obj;	
					//TODO: Check if this works correctly.
					if (this.getValueAt(0) != null) {
						return this.getValueAt(0).equals(that.getValueAt(0));
					}
				}
				return false;
			}
		};
		return tableNode;
	}
	
	/**
	 * This method sets the enabled state of the get results button.
	 */
	public void setResultsButtonEnabled(boolean enabled) {
		queryBtn.setEnabled(enabled);
	}
	
	/**
	 * Returns the last CypherQuery.
	 * @return Last chosen CypherQuery
	 */
	public CypherQuery getLastCypherQuery() {
		return lastCypherQuery;
	}
	
	/**
	 * Sets the last CypherQuery.
	 * @param lastCypherQuery Last chosen CypherQuery
	 */
	public void setLastCypherQuery(CypherQuery lastCypherQuery) {
		this.lastCypherQuery = lastCypherQuery;
	}

	@Override
	public boolean isBusy() {
		// we don't need this (yet)
		return false;
	}

	@Override
	public void setBusy(boolean busy) {
		ButtonTabbedPane tabPane = (ButtonTabbedPane) this.getParent();
		int index = tabPane.indexOfComponent(this);
		tabPane.setBusyAt(index, busy);
		tabPane.setEnabledAt(index, !busy);
	}
	
	/**
	 * Initializes the graph database.
	 */
	public void buildGraphDatabase() {
		new BuildGraphDatabaseTask().execute();
	}
	
	/**
	 * Worker implementation for building the graph database.
	 * @author A. Behne
	 */
	// TODO: re-locate worker to Client class?
	private class BuildGraphDatabaseTask extends SwingWorker {

		@Override
		protected Object doInBackground() {
			Thread.currentThread().setName("BuildGraphDBThread");
			
			try {
				Client.getInstance().setupGraphDatabaseContent();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void done() {
			// stop appearing busy
			GraphDatabaseResultPanel.this.setBusy(false);
		}
		
	}
	
}
