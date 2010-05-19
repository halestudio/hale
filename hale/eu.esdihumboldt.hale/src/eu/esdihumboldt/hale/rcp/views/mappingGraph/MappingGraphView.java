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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.models.utils.SchemaItemService;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.definition.internal.BrowserTip;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelection;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationViewLabelProvider;
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
	private CellSelection temporaryCellSelection = null;

	/**
	 * The graph of zest
	 */
	Graph graph;

	/**
	 * The list which contains the source nodes
	 */
	ArrayList<GraphNode> sourceNodeList = new ArrayList<GraphNode>();

	/**
	 * The list which contains the target nodes
	 */
	ArrayList<GraphNode> targetNodeList = new ArrayList<GraphNode>();

	/**
	 * The list which contains the entity nodes
	 */
	ArrayList<GraphNode> entityNodeList = new ArrayList<GraphNode>();

	/**
	 * Contains a target node, if there is already one
	 */
	private GraphNode targetGraphNodeAlreadyThere = null;

	/**
	 * Contains a source node, if there is already one
	 */
	private GraphNode sourceGraphNodeAlreadyThere = null;

	/**
	 * Temporary save for the entity node
	 */
	private GraphNode entityNode = null;

	/**
	 * Temporary save for the source node
	 */
	private GraphNode sourceGraphNode = null;

	/**
	 * Temporary save for the target node
	 */
	private GraphNode targetGraphNode = null;

	/**
	 * The used display
	 */
	Display display = Display.getCurrent();

	/**
	 * The selection of the chosen mode
	 */
	int schemaSelectionInt = 1;

	/**
	 * Creates the view
	 * 
	 * @param parent
	 *            is the parent composite
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
						MappingGraphView.this.temporarySchemaSelection);
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
	 * Gets the filter attributes and writes them into a list
	 * 
	 * @param cell
	 *            is the cell which contains filters
	 * @return a List of Strings with Filters
	 */
	private List<String> getFilters(Cell cell) {
		List<String> filters = new ArrayList<String>();

		/**
		 * For Entity 2
		 */
		// Filter strings are added to the tooltipText-String
		if (cell.getEntity1().getTransformation() == null) {
			if (cell.getEntity2() instanceof FeatureClass) {
				if (((FeatureClass) cell.getEntity2())
						.getAttributeValueCondition() != null) {
					for (Restriction restriction : ((FeatureClass) cell
							.getEntity2()).getAttributeValueCondition()) {
						filters.add(restriction.getCqlStr() + "\r\n");
					}
				}
			} else if (cell.getEntity2() instanceof Property) {
				if (((FeatureClass) cell.getEntity2())
						.getAttributeValueCondition() != null) {
					for (Restriction restriction : ((Property) cell
							.getEntity2()).getValueCondition()) {
						filters.add(restriction.getCqlStr() + "\r\n");
					}
				}
			}
		}

		/**
		 * For Entity 1
		 */
		// Filter strings are added to the tooltipText-String
		else {
			if (cell.getEntity1() instanceof FeatureClass) {
				if (((FeatureClass) cell.getEntity1())
						.getAttributeValueCondition() != null) {
					for (Restriction restriction : ((FeatureClass) cell
							.getEntity1()).getAttributeValueCondition()) {
						filters.add(restriction.getCqlStr());
					}
				}
			} else if (cell.getEntity1() instanceof Property) {
				if (((Property) cell.getEntity1()).getValueCondition() != null) {
					for (Restriction restriction : ((Property) cell
							.getEntity1()).getValueCondition()) {
						filters.add(restriction.getCqlStr());
					}
				}
			}
		}
		return filters;
	}

	/**
	 * Converts a List into a String
	 * 
	 * @param list
	 *            which contains strings
	 * @return a String out of a List<String>
	 */
	private String listToString(List<String> list) {
		String toolTipText = "";

		if (!list.isEmpty()) {
			for (String stringtext : list) {
				toolTipText = toolTipText + stringtext + "\r\n";
			}
		}
		return toolTipText;
	}

	/**
	 * Converts a paramerterList into a String
	 * 
	 * @param parameterList
	 *            contains IParameters
	 * @return a String out of a List<IParameter>
	 */
	private String parameterListToString(List<IParameter> parameterList) {
		String toolTipText = "";

		if (!parameterList.isEmpty()) {
			for (IParameter parameter : parameterList) {
				toolTipText = toolTipText + parameter.getName() + " : ";
				toolTipText = toolTipText + parameter.getValue() + "\r\n";
			}
		}
		return toolTipText;
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
		filterList = getFilters(((Cell) graphNode.getData()));

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
			toolTipText = toolTipText + this.listToString(filterList);
		}
		if (!parameterList.isEmpty()) {
			toolTipText = toolTipText + "Parameter : " + "\r\n";
			toolTipText = toolTipText
					+ this.parameterListToString(parameterList);
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
				Point pos = MappingGraphView.this.graph.getViewport()
						.getViewLocation();
				int x = event.x + pos.x;
				int y = event.y + pos.y;

				if (!MappingGraphView.this.sourceNodeList.isEmpty()) {
					for (GraphNode graphNode : MappingGraphView.this.sourceNodeList) {
						if (graphNode.getNodeFigure().containsPoint(x, y)) {
							PropertiesAction propertiesAction = new PropertiesAction(
									(SchemaItem) graphNode.getData());
							propertiesAction.run();
						}
					}
				}
				if (!MappingGraphView.this.targetNodeList.isEmpty()) {
					for (GraphNode graphNode : MappingGraphView.this.targetNodeList) {
						if (graphNode.getNodeFigure().containsPoint(x, y)) {
							PropertiesAction propertiesAction = new PropertiesAction(
									(SchemaItem) graphNode.getData());
							propertiesAction.run();
						}
					}
				}
				if (!MappingGraphView.this.entityNodeList.isEmpty()) {
					for (GraphNode graphNode : MappingGraphView.this.entityNodeList) {
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
	 * All ArrayLists will be reseted.
	 */
	private void arrayReset() {

		this.targetGraphNodeAlreadyThere = null;
		this.sourceGraphNodeAlreadyThere = null;
		this.entityNode = null;
		this.sourceGraphNode = null;
		this.targetGraphNode = null;

		if (!this.sourceNodeList.isEmpty()) {

			for (GraphNode graphNode : this.sourceNodeList) {
				graphNode.unhighlight();
				graphNode.dispose();
			}
			this.sourceNodeList.clear();
		}

		if (!this.targetNodeList.isEmpty()) {

			for (GraphNode graphNode : this.targetNodeList) {
				graphNode.unhighlight();
				graphNode.dispose();
			}
			this.targetNodeList.clear();
		}

		if (!this.entityNodeList.isEmpty()) {

			for (GraphNode graphNode : this.entityNodeList) {
				graphNode.unhighlight();
				graphNode.dispose();
			}
			this.entityNodeList.clear();
		}
	}

	/**
	 * All connections will be checked for accordances. If there is a accordance
	 * than this existing node will be used.
	 * 
	 * @param graphConnectionNodeName
	 *            is the name to be searched
	 * @param resultCell
	 *            contains content to search
	 */
	@SuppressWarnings("unchecked")
	private void checkForExistingConnections(String graphConnectionNodeName,
			Cell resultCell) {

		List<String> filters = this.getFilters(resultCell);
		String compare1 = this.listToString(filters);
		this.sourceGraphNodeAlreadyThere = null;
		this.targetGraphNodeAlreadyThere = null;
		this.entityNode = null;

		// Check for existing Entity Connection
		if (this.entityNodeList.size() > 0) {
			for (int k = 0; k < this.entityNodeList.size(); k++) {
				if (this.entityNodeList.get(k).getText().equals(
						graphConnectionNodeName)) {
					
					//Target side
					for (Iterator<GraphConnection> iterator = this.entityNodeList
							.get(k).getTargetConnections().iterator();iterator.hasNext();) {
						
						GraphConnection graphConnection = iterator.next();
						String targetName1 = graphConnection.getDestination()
								.getText();
						String targetName2 = graphConnection.getSource()
								.getText();
						List<String> temp = getFilters(((Cell) this.entityNodeList
								.get(k).getData()));
						String compare2 = this.listToString(temp);
						
						if ((targetName1.equals(this.sourceGraphNode.getText()) || targetName2
								.equals(this.sourceGraphNode.getText()))) {

							// If content equals content which is already there
							// than no second entityNode will be drawn
							if (compare1.equals(compare2)) {
								this.entityNode = this.entityNodeList.get(k);
								this.sourceGraphNodeAlreadyThere = this.sourceGraphNode;
							}
						}
						if (targetName1.equals(this.targetGraphNode.getText())
								|| targetName2.equals(this.targetGraphNode
										.getText())) {

							// If content equals content which is already there
							// than no second entityNode will be drawn
							if (compare1.equals(compare2)) {
								this.targetGraphNodeAlreadyThere = this.targetGraphNode;
								this.entityNode = this.entityNodeList.get(k);
							}
						}
					}
					
					//Source side
					for (Iterator<GraphConnection> iterator = this.entityNodeList
							.get(k).getSourceConnections().iterator();iterator.hasNext();) {
						
						GraphConnection graphConnection = iterator.next();
						String targetName1 = graphConnection.getDestination()
								.getText();
						String targetName2 = graphConnection.getSource()
								.getText();
						List<String> temp = getFilters(((Cell) this.entityNodeList
								.get(k).getData()));
						String compare2 = this.listToString(temp);
						
						if ((targetName1.equals(this.sourceGraphNode.getText()) || targetName2
								.equals(this.sourceGraphNode.getText()))) {

							// If content equals content which is already there
							// than no second entityNode will be drawn
							if (compare1.equals(compare2)) {
								this.entityNode = this.entityNodeList.get(k);
								this.sourceGraphNodeAlreadyThere = this.sourceGraphNode;
							}
						}
						if (targetName1.equals(this.targetGraphNode.getText())
								|| targetName2.equals(this.targetGraphNode
										.getText())) {

							// If content equals content which is already there
							// than no second entityNode will be drawn
							if (compare1.equals(compare2)) {
								this.targetGraphNodeAlreadyThere = this.targetGraphNode;
								this.entityNode = this.entityNodeList.get(k);
							}
						}
					}
					
				} 
				else {
					this.sourceGraphNodeAlreadyThere = null;
					this.targetGraphNodeAlreadyThere = null;
					this.entityNode = null;
				}
			}
		}
	}

	/**
	 * Creates the connections between the Nodes
	 */
	private void createGraphConnections() {
		GraphConnection graphConnection;
		if (this.sourceGraphNodeAlreadyThere != null) {
			graphConnection = new GraphConnection(this.graph, SWT.NONE,
					this.sourceGraphNodeAlreadyThere, this.entityNode);
			graphConnection.setLineColor(new Color(null, 255, 0, 0));
			graphConnection = new GraphConnection(this.graph, SWT.NONE,
					this.entityNode, this.targetGraphNode);
			graphConnection.setLineColor(new Color(null, 255, 0, 0));

		} else if (this.targetGraphNodeAlreadyThere != null) {
			graphConnection = new GraphConnection(this.graph, SWT.NONE,
					this.sourceGraphNode, this.entityNode);
			graphConnection.setLineColor(new Color(null, 255, 0, 0));
			graphConnection = new GraphConnection(this.graph, SWT.NONE,
					this.entityNode, this.targetGraphNodeAlreadyThere);
			graphConnection.setLineColor(new Color(null, 255, 0, 0));

		} else {
			graphConnection = new GraphConnection(this.graph, SWT.NONE,
					this.sourceGraphNode, this.entityNode);
			graphConnection.setLineColor(new Color(null, 255, 0, 0));
			graphConnection = new GraphConnection(this.graph, SWT.NONE,
					this.entityNode, this.targetGraphNode);
			graphConnection.setLineColor(new Color(null, 255, 0, 0));
		}
	}

	/**
	 * Source and target Nodes will be drawn and saved in the ArrayLists
	 * 
	 * @param schemaSelection gets drawn
	 */
	private void drawNodes(SchemaSelection schemaSelection) {

		int y = 10;
		if (!schemaSelection.getSourceItems().isEmpty()) {
			for (final SchemaItem sourceSchemaItem : schemaSelection.getSourceItems()) {
				if (sourceSchemaItem.getEntity() == null) {
					continue;
				}
				GraphNode graphNode = new GraphNode(this.graph, SWT.NONE,
						sourceSchemaItem.getEntity().getLocalname());
				graphNode.setLocation(10, y);

				// Set image
				String imageKey = ModelNavigationViewLabelProvider
						.getImageforTreeObjectType(sourceSchemaItem.getType());
				if (imageKey != null) {
					Image image = AbstractUIPlugin
							.imageDescriptorFromPlugin(HALEActivator.PLUGIN_ID,
									"/icons/" + imageKey).createImage(); //$NON-NLS-1$
					graphNode.setImage(image);
				}

				graphNode.setData(sourceSchemaItem);

				// Node Style
				graphNode.setBorderWidth(2);
				graphNode.setBorderColor(this.display
						.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
				graphNode.setBorderHighlightColor(this.display
						.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
				graphNode.setBackgroundColor(new Color(this.display, 250, 150,
						150));
				graphNode
						.setHighlightColor(new Color(this.display, 230, 70, 70));

				this.sourceNodeList.add(graphNode);
				y = y + 30;
			}
		}
		y = 10;
		if (!schemaSelection.getTargetItems().isEmpty()) {
			for (SchemaItem targetSchemaItem : schemaSelection.getTargetItems()) {
				if (targetSchemaItem.getEntity() == null) {
					continue;
				}
				GraphNode graphNode = new GraphNode(this.graph, SWT.NONE,
						targetSchemaItem.getEntity().getLocalname());
				graphNode.setLocation(this.graph.getSize().x * 2 / 3, y);

				// Set image
				String imageKey = ModelNavigationViewLabelProvider
						.getImageforTreeObjectType(targetSchemaItem.getType());
				if (imageKey != null) {
					Image image = AbstractUIPlugin
							.imageDescriptorFromPlugin(HALEActivator.PLUGIN_ID,
									"/icons/" + imageKey).createImage(); //$NON-NLS-1$
					graphNode.setImage(image);
				}

				graphNode.setData(targetSchemaItem);

				// Node Style
				graphNode.setBorderWidth(2);
				graphNode.setBorderColor(this.display
						.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
				graphNode.setBorderHighlightColor(this.display
						.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
				graphNode.setBackgroundColor(new Color(this.display, 250, 150,
						150));
				graphNode
						.setHighlightColor(new Color(this.display, 230, 70, 70));

				this.targetNodeList.add(graphNode);
				y = y + 30;
			}
		}
	}

	/**
	 * Source and target Nodes will be drawn and saved in the ArrayLists if they
	 * have Connections
	 * 
	 * @param schemaSelection
	 *            gets drawn
	 */
	private void drawNodesOnlyWithConnection(SchemaSelection schemaSelection) {

		int y = 10;
		if (!schemaSelection.getSourceItems().isEmpty()
				&& !schemaSelection.getTargetItems().isEmpty()) {
			for (final SchemaItem sourceSchemaItem : schemaSelection
					.getSourceItems()) {
				if (sourceSchemaItem.getEntity() == null) {
					continue;
				}

				boolean existsOneTime = false;
				for (final SchemaItem targetSchemaItem : schemaSelection
						.getTargetItems()) {
					if (existsOneTime) {
						continue;
					}
					Cell resultCell = null;
					resultCell = ((Cell) this.alignmentService.getCell(
							sourceSchemaItem.getEntity(), targetSchemaItem
									.getEntity()));

					// if sourceSchemaItem got a cell then save and draw!
					if (resultCell != null) {
						GraphNode graphNode = new GraphNode(this.graph,
								SWT.NONE, sourceSchemaItem.getEntity()
										.getLocalname());
						graphNode.setLocation(10, y);

						// Set image
						String imageKey = ModelNavigationViewLabelProvider
								.getImageforTreeObjectType(sourceSchemaItem
										.getType());
						if (imageKey != null) {
							Image image = AbstractUIPlugin
									.imageDescriptorFromPlugin(
											HALEActivator.PLUGIN_ID,
											"/icons/" + imageKey).createImage(); //$NON-NLS-1$
							graphNode.setImage(image);
						}

						graphNode.setData(sourceSchemaItem);

						// Node Style
						graphNode.setBorderWidth(2);
						graphNode
								.setBorderColor(this.display
										.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
						graphNode
								.setBorderHighlightColor(this.display
										.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
						graphNode.setBackgroundColor(new Color(this.display,
								250, 150, 150));
						graphNode.setHighlightColor(new Color(this.display,
								230, 70, 70));

						this.sourceNodeList.add(graphNode);
						existsOneTime = true;
						y = y + 30;
					}
				}
			}
			y = 10;
			for (SchemaItem targetSchemaItem : schemaSelection.getTargetItems()) {
				if (targetSchemaItem.getEntity() == null) {
					continue;
				}

				boolean existsOneTime = false;
				for (final SchemaItem sourceSchemaItem : schemaSelection
						.getSourceItems()) {
					if (existsOneTime) {
						continue;
					}
					Cell resultCell = null;
					resultCell = ((Cell) this.alignmentService.getCell(
							sourceSchemaItem.getEntity(), targetSchemaItem
									.getEntity()));

					// if sourceSchemaItem got a cell then save and draw!
					if (resultCell != null) {
						GraphNode graphNode = new GraphNode(this.graph,
								SWT.NONE, targetSchemaItem.getEntity()
										.getLocalname());
						graphNode
								.setLocation(this.graph.getSize().x * 2 / 3, y);

						// Set image
						String imageKey = ModelNavigationViewLabelProvider
								.getImageforTreeObjectType(targetSchemaItem
										.getType());
						if (imageKey != null) {
							Image image = AbstractUIPlugin
									.imageDescriptorFromPlugin(
											HALEActivator.PLUGIN_ID,
											"/icons/" + imageKey).createImage(); //$NON-NLS-1$
							graphNode.setImage(image);
						}

						graphNode.setData(targetSchemaItem);

						// Node Style
						graphNode.setBorderWidth(2);
						graphNode
								.setBorderColor(this.display
										.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
						graphNode
								.setBorderHighlightColor(this.display
										.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
						graphNode.setBackgroundColor(new Color(this.display,
								250, 150, 150));
						graphNode.setHighlightColor(new Color(this.display,
								230, 70, 70));

						this.targetNodeList.add(graphNode);
						existsOneTime = true;
						y = y + 30;
					}
				}
			}
		}
	}

	/**
	 * Creates an entity node if there is no one.
	 * 
	 * @param graphConnectionNodeName
	 *            gets set as name for the new entity node
	 * @param resultCell
	 *            gets set a data
	 */
	private void createNewEntityNode(String graphConnectionNodeName,
			Cell resultCell) {

		if (this.entityNode == null) {
			final GraphNode newEntityNode = new GraphNode(this.graph, SWT.NONE,
					graphConnectionNodeName);
			
			//Set the cell as data into the node
			newEntityNode.setData(resultCell);

			// figure out the y-ordinate for the entity node and set the
			// location
			if (this.entityNodeList.isEmpty()) {
				newEntityNode.setLocation(this.graph.getSize().x * 1 / 3,
						(this.sourceGraphNode.getLocation().y + 
						this.targetGraphNode.getLocation().y) / 2);
				
			} else if (((this.sourceGraphNode.getLocation().y + 
					this.targetGraphNode.getLocation().y) / 2)
					> (this.entityNodeList.get(this.entityNodeList.size() - 1)
							.getLocation().y)+30) {
				newEntityNode.setLocation(this.graph.getSize().x * 1 / 3,
					(this.sourceGraphNode.getLocation().y + 
					this.targetGraphNode.getLocation().y) / 2);
			} else {
				newEntityNode.setLocation(this.graph.getSize().x * 1 / 3,
						(this.entityNodeList.get(this.entityNodeList.size() - 1)
								.getLocation().y) + 30);
			}
			this.entityNode = newEntityNode;
		}
	}

	/**
	 * Makes the stepping for the Nodes
	 */
	private void makeNodeStepping() {

		// Source side
		HashMap<GraphNode, Integer> hm = new HashMap<GraphNode, Integer>();
		if (!this.sourceNodeList.isEmpty()) {
			for (GraphNode graphNode : this.sourceNodeList) {
				int counter = -1;
				SchemaItem parent = (SchemaItem) graphNode.getData();
				SchemaItem oldParent = ((SchemaItem) graphNode.getData())
						.getParent();
				while (!oldParent.equals(parent)) {
					oldParent = parent;
					parent = parent.getParent();
					counter++;
					if (oldParent.getName() == null || parent.getName() == null) {
						break;
					}
				}
				hm.put(graphNode, counter);
			}
			Set<?> set = hm.entrySet();
			Iterator<?> i = set.iterator();
			while (i.hasNext()) {
				Map.Entry me = (Map.Entry) i.next();
				GraphNode temp = ((GraphNode) me.getKey());
				temp.setLocation(temp.getLocation().x
						+ (20 * (Integer) me.getValue()), temp.getLocation().y);
			}
		}

		// Target side
		hm = new HashMap<GraphNode, Integer>();
		if (!this.targetNodeList.isEmpty()) {
			for (GraphNode graphNode : this.targetNodeList) {
				int counter = -1;
				SchemaItem parent = (SchemaItem) graphNode.getData();
				SchemaItem oldParent = ((SchemaItem) graphNode.getData())
						.getParent();
				while (!oldParent.equals(parent)) {
					oldParent = parent;
					parent = parent.getParent();
					counter++;
					if (oldParent.getName() == null || parent.getName() == null) {
						break;
					}
				}
				hm.put(graphNode, counter);
			}
			Set<?> set = hm.entrySet();
			Iterator<?> i = set.iterator();
			while (i.hasNext()) {
				Map.Entry me = (Map.Entry) i.next();
				GraphNode temp = ((GraphNode) me.getKey());
				temp.setLocation(temp.getLocation().x
						+ (20 * (Integer) me.getValue()), temp.getLocation().y);
			}
		}
	}

	/**
	 * @param schemaSelection
	 *            which gets schema items added
	 * @param source
	 *            boolean, if true=source false=target
	 * @param root
	 *            is the root schema item of source or target side
	 */
	private void rekursiveGetChildren(SchemaSelection schemaSelection,
			boolean source, SchemaItem root) {
		if (source) {
			schemaSelection.addSourceItem(root);
		} else {
			schemaSelection.addTargetItem(root);
		}
		for (SchemaItem schemaItem : root.getChildren()) {

			if (schemaItem.hasChildren()) {
				this.rekursiveGetChildren(schemaSelection, source, schemaItem);
			} else {
				if (source) {
					schemaSelection.addSourceItem(schemaItem);
				} else {
					schemaSelection.addTargetItem(schemaItem);
				}
			}

		}
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
				this.rekursiveGetChildren(schemaSelectionAll, true, sourceRoot);
				this.rekursiveGetChildren(schemaSelectionAll, false,targetRoot);
				schemaSelection = schemaSelectionAll;

				// Resets the arrays before the nodes going to be redrawn
				this.arrayReset();

				// Draws the source and target nodes
				this.drawNodesOnlyWithConnection(schemaSelection);

				// Makes the stepping of the Nodes
				this.makeNodeStepping();

				/**
				 * Source and target Nodes will be scanned for accordances at
				 * the Entities. After that these accordances (called cells)
				 * will be drawn and connected by lines in the middle of the
				 * Mapping Graph. In the end the cells and the connections will
				 * be cached in the ArrayLists.
				 */
				if (!this.sourceNodeList.isEmpty()
						&& !this.targetNodeList.isEmpty()) {
					for (GraphNode sourceGraphNode : this.sourceNodeList) {
						SchemaItem sourceSchemaItem = (SchemaItem) sourceGraphNode
								.getData();
						this.sourceGraphNode = sourceGraphNode;
						for (GraphNode targetGraphNode : this.targetNodeList) {
							String cellName = null;
							SchemaItem targetSchemaItem = (SchemaItem) targetGraphNode
									.getData();
							this.targetGraphNode = targetGraphNode;
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
								this.checkForExistingConnections(
										graphConnectionNodeName, resultCell);
								// Creates a new entity node if there is no old
								// one
								this.createNewEntityNode(
										graphConnectionNodeName, resultCell);
								// Creates the connections between the nodes.
								this.createGraphConnections();
								this.entityNodeList.add(this.entityNode);
							}
						}
					}
				}
			}
			
			// if the schemaSelectionInt is 2, the selection in the
			// SchemaExplorer will be drawn
			if (this.schemaSelectionInt == 2) {
				// Resets the arrays before the nodes going to be redrawn
				this.arrayReset();

				// Draws the source and target nodes
				this.drawNodes(schemaSelection);

				// Makes the stepping of the Nodes
				this.makeNodeStepping();

				/**
				 * Source and target Nodes will be scanned for accordances at
				 * the Entities. After that these accordances (called cells)
				 * will be drawn and connected by lines in the middle of the
				 * Mapping Graph. In the end the cells and the connections will
				 * be cached in the ArrayLists.
				 */
				if (!this.sourceNodeList.isEmpty()
						&& !this.targetNodeList.isEmpty()) {
					for (GraphNode sourceGraphNode : this.sourceNodeList) {
						SchemaItem sourceSchemaItem = (SchemaItem) sourceGraphNode
								.getData();
						this.sourceGraphNode = sourceGraphNode;
						for (GraphNode targetGraphNode : this.targetNodeList) {
							String cellName = null;
							SchemaItem targetSchemaItem = (SchemaItem) targetGraphNode
									.getData();
							this.targetGraphNode = targetGraphNode;
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
								this.checkForExistingConnections(
										graphConnectionNodeName, resultCell);
								// Creates a new entity node if there is no old
								// one
								this.createNewEntityNode(
										graphConnectionNodeName, resultCell);
								// Creates the connections between the nodes.
								this.createGraphConnections();
								this.entityNodeList.add(this.entityNode);
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

			if(cellSelection != null){
				
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
				this.arrayReset();

				// Draws the source and target nodes
				this.drawNodesOnlyWithConnection(schemaSelection);

				// Makes the stepping of the Nodes
				this.makeNodeStepping();

				/**
				 * Source and target Nodes will be scanned for accordances at
				 * the Entities. After that these accordances (called cells)
				 * will be drawn and connected by lines in the middle of the
				 * Mapping Graph. In the end the cells and the connections will
				 * be cached in the ArrayLists.
				 */
				if (!this.sourceNodeList.isEmpty()
						&& !this.targetNodeList.isEmpty()) {
					for (GraphNode sourceGraphNode : this.sourceNodeList) {
						SchemaItem sourceSchemaItem = (SchemaItem) sourceGraphNode
								.getData();
						this.sourceGraphNode = sourceGraphNode;
						for (GraphNode targetGraphNode : this.targetNodeList) {
							String cellName = null;
							SchemaItem targetSchemaItem = (SchemaItem) targetGraphNode
									.getData();
							this.targetGraphNode = targetGraphNode;
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
								this.checkForExistingConnections(
										graphConnectionNodeName, resultCell);
								// Creates a new entity node if there is no old
								// one
								this.createNewEntityNode(
										graphConnectionNodeName, resultCell);
								// Creates the connections between the nodes.
								this.createGraphConnections();
								this.entityNodeList.add(this.entityNode);
							}
						}
					}
				}
			}
		}	
	}
}