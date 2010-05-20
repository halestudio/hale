/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.views.mappingGraph;

import java.util.List;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.models.utils.SchemaItemService;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.definition.internal.BrowserTip;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelection;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;
import eu.esdihumboldt.hale.rcp.views.model.dialogs.PropertiesAction;

/**
 * MappingGraphViewer; generates the nodes and the connections.
 * 
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version
 */

public class MappingGraphView extends ViewPart implements ISelectionListener {

	/**
	 * The alignment service
	 */
	private AlignmentService alignmentService = null;

	/**
	 * The schema item service
	 */
	private SchemaItemService schemaItemService = null;

	/**
	 * The specific ID of this class
	 */
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.mappingGraph.MappingGraphView";

	/**
	 * Saves the last schemaSelection for refresh function
	 */
	SchemaSelection temporarySchemaSelection = null;

	/**
	 * Saves the last cellSelection for refresh function
	 */
	CellSelection temporaryCellSelection = null;

	/**
	 * The graph of zest
	 */
	private Graph graph;

	/**
	 * The selection of the chosen mode
	 */
	int schemaSelectionInt = 1;

	/**
	 * The mappingGraphModel contains the nodes
	 */
	MappingGraphModel mappingGraphModel;
	
	/**
	 * The mappingGraphNodeRenderer creates the nodes
	 */
	MappingGraphNodeRenderer mappingGraphNodeRenderer;
	
	/**
	 * Creates the view
	 * @param parent is the parent composite
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(final Composite parent) {
		// get alignment service
		this.alignmentService = (AlignmentService) PlatformUI.getWorkbench()
				.getService(AlignmentService.class);

		// get schema service
		this.schemaItemService = (SchemaItemService) PlatformUI.getWorkbench()
				.getService(SchemaItemService.class);

		this.temporarySchemaSelection = new SchemaSelection();
		this.temporaryCellSelection = new CellSelection();
		this.mappingGraphModel = new MappingGraphModel();
		this.mappingGraphNodeRenderer = new MappingGraphNodeRenderer(this.mappingGraphModel, this);
		
		// Instantiate the Buttons
		org.eclipse.swt.layout.GridLayout gridLayout2 = new GridLayout();
		org.eclipse.swt.layout.GridData gridData4 = new org.eclipse.swt.layout.GridData();
		org.eclipse.swt.layout.GridData gridData5 = new org.eclipse.swt.layout.GridData();
		org.eclipse.swt.layout.GridData gridData6 = new org.eclipse.swt.layout.GridData();
		Image image = HALEActivator
				.getImageDescriptor("icons/refresh.gif").createImage(); //$NON-NLS-1$

		final Button button1 = new Button(parent, SWT.TOGGLE);
		final Button button2 = new Button(parent, SWT.TOGGLE);
		final Button button3 = new Button(parent, SWT.TOGGLE);

		button1.setText("Show All Cells");
		button1.setToolTipText("Shows all existing Cells");
		button1.setImage(image);
		button1.setLayoutData(gridData4);
		button1.setSelection(true);

		// Button Listener
		button1.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				MappingGraphView.this.schemaSelectionInt = 1;
				button1.setSelection(true);
				button2.setSelection(false);
				button3.setSelection(false);
				MappingGraphView.this.selectionChanged(null,
						MappingGraphView.this.temporarySchemaSelection);
			}
		});

		button2.setText("Schema Explorer Syncronize");
		button2.setToolTipText("Syncronize with Schema Explorer");
		button2.setImage(image);
		button2.setLayoutData(gridData5);

		// Button Listener
		button2.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				MappingGraphView.this.schemaSelectionInt = 2;
				button1.setSelection(false);
				button2.setSelection(true);
				button3.setSelection(false);
				MappingGraphView.this.selectionChanged(null,
						MappingGraphView.this.temporarySchemaSelection);
			}
		});

		button3.setText("Mapping Syncronize");
		button3.setToolTipText("Syncronize with Mapping");
		button3.setImage(image);
		button3.setLayoutData(gridData6);

		// Button Listener
		button3.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				MappingGraphView.this.schemaSelectionInt = 3;
				button1.setSelection(false);
				button2.setSelection(false);
				button3.setSelection(true);
				MappingGraphView.this.selectionChanged(null,
						MappingGraphView.this.temporaryCellSelection);
			}
		});

		// Layout Alignment
		parent.setLayout(gridLayout2);
		gridLayout2.numColumns = 3;
		gridLayout2.makeColumnsEqualWidth = false;
		gridData4.horizontalAlignment = GridData.BEGINNING;
		gridData4.verticalAlignment = GridData.CENTER;
		gridData4.grabExcessHorizontalSpace = false;
		gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		gridData5.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData5.grabExcessHorizontalSpace = false;
		gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;

		// register as selection listener
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this);

		// Instantiate the graph
		this.graph = new Graph(parent, SWT.NONE);
		this.graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		gridData.verticalAlignment = GridData.FILL;
		this.graph.setLayoutData(gridData);
		
		// setting the MouseListener for the entity node
		this.setNodeMouseListener();
		
	}

	/**
	 * Generates the browserToolTips
	 * 
	 * @param graphNode
	 *            get the generated browserToolTip
	 */
	public void generateBrowserToolTip(GraphNode graphNode) {

		BrowserTip browserTip = new BrowserTip(400, 400, true);

		List<String> filterList;
		List<IParameter> parameterList;

		// Get the filters
		filterList = this.mappingGraphModel.getFilters(((Cell) graphNode.getData()));

		/**
		 * For Entity 2
		 */
		if (((Cell) graphNode.getData()).getEntity1().getTransformation() == null) {
			// Parameter strings are added to the List
			parameterList = ((Cell) graphNode.getData()).getEntity2()
					.getTransformation().getParameters();
		}

		/**
		 * For Entity 1
		 */
		else {
			// Parameter strings are added to the List
			parameterList = ((Cell) graphNode.getData()).getEntity1()
					.getTransformation().getParameters();
		}

		// Filter and Parameters gets transformed into one string
		String toolTipText = "";
		if (!filterList.isEmpty()) {
			toolTipText = toolTipText + "Filter : " + "\r\n";
			toolTipText = toolTipText + this.mappingGraphModel.listToString(filterList);
		}
		if (!parameterList.isEmpty()) {
			toolTipText = toolTipText + "Parameter : " + "\r\n";
			toolTipText = toolTipText
					+ this.mappingGraphModel.parameterListToString(parameterList);
		}

		// BrowserToolTip gets created
		Point pos = this.graph.getViewport().getViewLocation();
		browserTip.showToolTip(MappingGraphView.this.graph, graphNode
				.getLocation().x
				- pos.x, graphNode.getLocation().y - pos.y + 20, toolTipText);
	}

	/**
	 * Sets the mouse listener for all nodes
	 */
	private void setNodeMouseListener() {
		this.graph.addListener(SWT.MouseDoubleClick, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Point pos = MappingGraphView.this.getGraph().getViewport()
						.getViewLocation();
				int x = event.x + pos.x;
				int y = event.y + pos.y;

				if (!MappingGraphView.this.mappingGraphModel.getSourceNodeList().isEmpty()) {
					for (GraphNode graphNode : MappingGraphView.this.mappingGraphModel.getSourceNodeList()) {
						if (graphNode.getNodeFigure().containsPoint(x, y)) {
							PropertiesAction propertiesAction = new PropertiesAction(
									(SchemaItem) graphNode.getData());
							propertiesAction.run();
						}
					}
				}
				if (!MappingGraphView.this.mappingGraphModel.getTargetNodeList().isEmpty()) {
					for (GraphNode graphNode : MappingGraphView.this.mappingGraphModel.getTargetNodeList()) {
						if (graphNode.getNodeFigure().containsPoint(x, y)) {
							PropertiesAction propertiesAction = new PropertiesAction(
									(SchemaItem) graphNode.getData());
							propertiesAction.run();
						}
					}
				}
				if (!MappingGraphView.this.mappingGraphModel.getEntityNodeList().isEmpty()) {
					for (GraphNode graphNode : MappingGraphView.this.mappingGraphModel.getEntityNodeList()) {
						if (graphNode.getNodeFigure().containsPoint(x, y)) {
							generateBrowserToolTip(graphNode);
						}
					}
				}
			}
		});
	}

	/**
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		//
	}

	/**
	 * selectionChanged() method is called when user interacts in the Schema
	 * Explorer. The chosen schemaItems will be drawn with their cell
	 * connections at the Graph Mapping window. All grapNodes and connections
	 * will be cached in same named ArrayLists.
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		SchemaSelection schemaSelection = null;
		CellSelection cellSelection = null;

		if (selection instanceof SchemaSelection) {
			schemaSelection = (SchemaSelection) selection; // SchemaSelectionHelper.getSchemaSelection();
			this.temporarySchemaSelection = schemaSelection;
		}
		
		if (selection instanceof CellSelection) {
			cellSelection = (CellSelection) selection;
			this.temporaryCellSelection = cellSelection;
		}

		//Schema handling from here -->
		if (schemaSelection != null) {

			// if the schemaSelectionInt is 1, all cells of the MappingView
			// will be drawn
			if (this.schemaSelectionInt == 1) {

				//Schema will be built
				SchemaSelection schemaSelectionAll = new SchemaSelection();
				SchemaItem sourceRoot = this.schemaItemService
						.getRoot(SchemaType.SOURCE);
				SchemaItem targetRoot = this.schemaItemService
						.getRoot(SchemaType.TARGET);
				this.mappingGraphModel.rekursiveGetChildren(schemaSelectionAll, true, sourceRoot);
				this.mappingGraphModel.rekursiveGetChildren(schemaSelectionAll, false,targetRoot);
				schemaSelection = schemaSelectionAll;

				// Resets the arrays before the nodes going to be redrawn
				this.mappingGraphModel.arrayReset();

				// Draws the source and target nodes
				this.mappingGraphNodeRenderer.drawNodesOnlyWithConnection(schemaSelection);

				// Makes the stepping of the Nodes
				this.mappingGraphNodeRenderer.makeNodeStepping();

				/**
				 * Source and target Nodes will be scanned for accordances at
				 * the Entities. After that these accordances (called cells)
				 * will be drawn and connected by lines in the middle of the
				 * Mapping Graph. In the end the cells and the connections will
				 * be cached in the ArrayLists.
				 */
				if (!this.mappingGraphModel.getSourceNodeList().isEmpty()
						&& !this.mappingGraphModel.getTargetNodeList().isEmpty()) {
					for (GraphNode sourceGraphNode : this.mappingGraphModel.getSourceNodeList()) {
						SchemaItem sourceSchemaItem = (SchemaItem) sourceGraphNode
								.getData();
						this.mappingGraphModel.setSourceGraphNode(sourceGraphNode);
						for (GraphNode targetGraphNode : this.mappingGraphModel.getTargetNodeList()) {
							String cellName = null;
							SchemaItem targetSchemaItem = (SchemaItem) targetGraphNode
									.getData();
							this.mappingGraphModel.setTargetGraphNode(targetGraphNode);
							Cell resultCell = null;

							resultCell = ((Cell) this.alignmentService.getCell(
									sourceSchemaItem.getEntity(),
									targetSchemaItem.getEntity()));
							if (resultCell != null) {
								if (resultCell.getEntity1().getTransformation() == null) {
									cellName = resultCell.getEntity2()
											.getTransformation().getService()
											.getLocation();
								} else {
									cellName = resultCell.getEntity1()
											.getTransformation().getService()
											.getLocation();
								}
								String[] tempSplit = cellName.split("\\.");
								String graphConnectionNodeName = tempSplit[tempSplit.length - 1];

								// Checks the old connections and takes the
								// right one
								this.mappingGraphModel.checkForExistingConnections(
										graphConnectionNodeName, resultCell);
								// Creates a new entity node if there is no old
								// one
								this.mappingGraphNodeRenderer.createNewEntityNode(
										graphConnectionNodeName, resultCell);
								// Creates the connections between the nodes.
								this.mappingGraphNodeRenderer.createGraphConnections();
								this.mappingGraphModel.getEntityNodeList().add(this.mappingGraphModel.getEntityNode());
							}
						}
					}
				}
			}
			
			// if the schemaSelectionInt is 2, the selection in the
			// SchemaExplorer will be drawn
			if (this.schemaSelectionInt == 2) {
				// Resets the arrays before the nodes going to be redrawn
				this.mappingGraphModel.arrayReset();

				// Draws the source and target nodes
				this.mappingGraphNodeRenderer.drawNodes(schemaSelection);

				// Makes the stepping of the Nodes
				this.mappingGraphNodeRenderer.makeNodeStepping();

				/**
				 * Source and target Nodes will be scanned for accordances at
				 * the Entities. After that these accordances (called cells)
				 * will be drawn and connected by lines in the middle of the
				 * Mapping Graph. In the end the cells and the connections will
				 * be cached in the ArrayLists.
				 */
				if (!this.mappingGraphModel.getSourceNodeList().isEmpty()
						&& !this.mappingGraphModel.getTargetNodeList().isEmpty()) {
					for (GraphNode sourceGraphNode : this.mappingGraphModel.getSourceNodeList()) {
						SchemaItem sourceSchemaItem = (SchemaItem) sourceGraphNode
								.getData();
						this.mappingGraphModel.setSourceGraphNode(sourceGraphNode);
						for (GraphNode targetGraphNode : this.mappingGraphModel.getTargetNodeList()) {
							String cellName = null;
							SchemaItem targetSchemaItem = (SchemaItem) targetGraphNode
									.getData();
							this.mappingGraphModel.setTargetGraphNode(targetGraphNode);
							Cell resultCell = null;

							resultCell = ((Cell) this.alignmentService.getCell(
									sourceSchemaItem.getEntity(),
									targetSchemaItem.getEntity()));
							if (resultCell != null) {
								if (resultCell.getEntity1().getTransformation() == null) {
									cellName = resultCell.getEntity2()
											.getTransformation().getService()
											.getLocation();
								} else {
									cellName = resultCell.getEntity1()
											.getTransformation().getService()
											.getLocation();
								}
								String[] tempSplit = cellName.split("\\.");
								String graphConnectionNodeName = tempSplit[tempSplit.length - 1];

								// Checks the old connections and takes the
								// right one
								this.mappingGraphModel.checkForExistingConnections(
										graphConnectionNodeName, resultCell);
								// Creates a new entity node if there is no old
								// one
								this.mappingGraphNodeRenderer.createNewEntityNode(
										graphConnectionNodeName, resultCell);
								// Creates the connections between the nodes.
								this.mappingGraphNodeRenderer.createGraphConnections();
								this.mappingGraphModel.getEntityNodeList().add(this.mappingGraphModel.getEntityNode());
							}
						}
					}
				}
			}
		}
		
		//Cell handling from here -->
		
		// if the schemaSelectionInt is 3, the selected cell in the
		// MappingView will be drawn
		if (this.schemaSelectionInt == 3) {

			if(this.temporaryCellSelection != null){
				
				//Schema will be built
				SchemaSelection schemaSelectionAll = new SchemaSelection();
				for(SchemaItem schemaItem : this.temporaryCellSelection.getCellInfo().getSourceItems()){
					schemaSelectionAll.addSourceItem(schemaItem);
				}
				for(SchemaItem schemaItem : this.temporaryCellSelection.getCellInfo().getTargetItems()){
					schemaSelectionAll.addTargetItem(schemaItem);
				}
				schemaSelection = schemaSelectionAll;

				// Resets the arrays before the nodes going to be redrawn
				this.mappingGraphModel.arrayReset();

				// Draws the source and target nodes
				this.mappingGraphNodeRenderer.drawNodesOnlyWithConnection(schemaSelection);

				// Makes the stepping of the Nodes
				this.mappingGraphNodeRenderer.makeNodeStepping();

				/**
				 * Source and target Nodes will be scanned for accordances at
				 * the Entities. After that these accordances (called cells)
				 * will be drawn and connected by lines in the middle of the
				 * Mapping Graph. In the end the cells and the connections will
				 * be cached in the ArrayLists.
				 */
				if (!this.mappingGraphModel.getSourceNodeList().isEmpty()
						&& !this.mappingGraphModel.getTargetNodeList().isEmpty()) {
					for (GraphNode sourceGraphNode : this.mappingGraphModel.getSourceNodeList()) {
						SchemaItem sourceSchemaItem = (SchemaItem) sourceGraphNode
								.getData();
						this.mappingGraphModel.setSourceGraphNode(sourceGraphNode);
						for (GraphNode targetGraphNode : this.mappingGraphModel.getTargetNodeList()) {
							String cellName = null;
							SchemaItem targetSchemaItem = (SchemaItem) targetGraphNode
									.getData();
							this.mappingGraphModel.setTargetGraphNode(targetGraphNode);
							Cell resultCell = null;

							resultCell = ((Cell) this.alignmentService.getCell(
									sourceSchemaItem.getEntity(),
									targetSchemaItem.getEntity()));
							if (resultCell != null) {
								if (resultCell.getEntity1().getTransformation() == null) {
									cellName = resultCell.getEntity2()
											.getTransformation().getService()
											.getLocation();
								} else {
									cellName = resultCell.getEntity1()
											.getTransformation().getService()
											.getLocation();
								}
								String[] tempSplit = cellName.split("\\.");
								String graphConnectionNodeName = tempSplit[tempSplit.length - 1];

								// Checks the old connections and takes the
								// right one
								this.mappingGraphModel.checkForExistingConnections(
										graphConnectionNodeName, resultCell);
								// Creates a new entity node if there is no old
								// one
								this.mappingGraphNodeRenderer.createNewEntityNode(
										graphConnectionNodeName, resultCell);
								// Creates the connections between the nodes.
								this.mappingGraphNodeRenderer.createGraphConnections();
								this.mappingGraphModel.getEntityNodeList().add(this.mappingGraphModel.getEntityNode());
							}
						}
					}
				}
			}
		}	
	}

	/**
	 * @return The graph
	 */
	public Graph getGraph() {
		return this.graph;
	}

	/**
	 * @return The alignmentService
	 */
	public AlignmentService getAlignmentService() {
		return this.alignmentService;
	}
}