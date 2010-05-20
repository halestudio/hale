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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;

import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationViewLabelProvider;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;

/**
 * MappingGraphNodeRenderer handles the nodes
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MappingGraphNodeRenderer {

	/**
	 * The mappingGraphModel 
	 */
	MappingGraphModel mappingGraphModel;
	
	/**
	 * The mappingGraphView
	 */
	MappingGraphView mappingGraphView;
	
	/**
	 * The used display
	 */
	private Display display = Display.getCurrent();

	/**
	 * @param mappingGraphModel
	 * @param mappingGraphView 
	 */
	public MappingGraphNodeRenderer(MappingGraphModel mappingGraphModel, MappingGraphView mappingGraphView){
		this.mappingGraphModel = mappingGraphModel;
		this.mappingGraphView = mappingGraphView;
	}
	
	/**
	 * Creates the connections between the Nodes
	 */
	void createGraphConnections() {
		GraphConnection graphConnection;
		if (this.mappingGraphModel.getSourceGraphNodeAlreadyThere() != null) {
			graphConnection = new GraphConnection(this.mappingGraphView.getGraph(), SWT.NONE,
					this.mappingGraphModel.getSourceGraphNodeAlreadyThere(), this.mappingGraphModel.getEntityNode());
			graphConnection.setLineColor(new Color(null, 255, 0, 0));
			graphConnection = new GraphConnection(this.mappingGraphView.getGraph(), SWT.NONE,
					this.mappingGraphModel.getEntityNode(), this.mappingGraphModel.getTargetGraphNode());
			graphConnection.setLineColor(new Color(null, 255, 0, 0));

		} else if (this.mappingGraphModel.getTargetGraphNodeAlreadyThere() != null) {
			graphConnection = new GraphConnection(this.mappingGraphView.getGraph(), SWT.NONE,
					this.mappingGraphModel.getSourceGraphNode(), this.mappingGraphModel.getEntityNode());
			graphConnection.setLineColor(new Color(null, 255, 0, 0));
			graphConnection = new GraphConnection(this.mappingGraphView.getGraph(), SWT.NONE,
					this.mappingGraphModel.getEntityNode(), this.mappingGraphModel.getTargetGraphNodeAlreadyThere());
			graphConnection.setLineColor(new Color(null, 255, 0, 0));

		} else {
			graphConnection = new GraphConnection(this.mappingGraphView.getGraph(), SWT.NONE,
					this.mappingGraphModel.getSourceGraphNode(), this.mappingGraphModel.getEntityNode());
			graphConnection.setLineColor(new Color(null, 255, 0, 0));
			graphConnection = new GraphConnection(this.mappingGraphView.getGraph(), SWT.NONE,
					this.mappingGraphModel.getEntityNode(), this.mappingGraphModel.getTargetGraphNode());
			graphConnection.setLineColor(new Color(null, 255, 0, 0));
		}
	}
	
	/**
	 * Source and target Nodes will be drawn and saved in the ArrayLists
	 * 
	 * @param schemaSelection gets drawn
	 */
	void drawNodes(SchemaSelection schemaSelection) {

		int y = 10;
		if (!schemaSelection.getSourceItems().isEmpty()) {
			for (final SchemaItem sourceSchemaItem : schemaSelection.getSourceItems()) {
				if (sourceSchemaItem.getEntity() == null) {
					continue;
				}
				GraphNode graphNode = new GraphNode(this.mappingGraphView.getGraph(), SWT.NONE,
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

				this.mappingGraphModel.getSourceNodeList().add(graphNode);
				y = y + 30;
			}
		}
		y = 10;
		if (!schemaSelection.getTargetItems().isEmpty()) {
			for (SchemaItem targetSchemaItem : schemaSelection.getTargetItems()) {
				if (targetSchemaItem.getEntity() == null) {
					continue;
				}
				GraphNode graphNode = new GraphNode(this.mappingGraphView.getGraph(), SWT.NONE,
						targetSchemaItem.getEntity().getLocalname());
				graphNode.setLocation(this.mappingGraphView.getGraph().getSize().x * 2 / 3, y);

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

				this.mappingGraphModel.getTargetNodeList().add(graphNode);
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
	void drawNodesOnlyWithConnection(SchemaSelection schemaSelection) {

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
					resultCell = ((Cell) this.mappingGraphView.getAlignmentService().getCell(
							sourceSchemaItem.getEntity(), targetSchemaItem
									.getEntity()));

					// if sourceSchemaItem got a cell then save and draw!
					if (resultCell != null) {
						GraphNode graphNode = new GraphNode(this.mappingGraphView.getGraph(),
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

						this.mappingGraphModel.getSourceNodeList().add(graphNode);
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
					resultCell = ((Cell) this.mappingGraphView.getAlignmentService().getCell(
							sourceSchemaItem.getEntity(), targetSchemaItem
									.getEntity()));

					// if sourceSchemaItem got a cell then save and draw!
					if (resultCell != null) {
						GraphNode graphNode = new GraphNode(this.mappingGraphView.getGraph(),
								SWT.NONE, targetSchemaItem.getEntity()
										.getLocalname());
						graphNode
								.setLocation(this.mappingGraphView.getGraph().getSize().x * 2 / 3, y);

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

						this.mappingGraphModel.getTargetNodeList().add(graphNode);
						existsOneTime = true;
						y = y + 30;
					}
				}
			}
		}
	}
	
	/**
	 * Makes the stepping for the Nodes
	 */
	void makeNodeStepping() {

		// Source side
		HashMap<GraphNode, Integer> hm = new HashMap<GraphNode, Integer>();
		if (!this.mappingGraphModel.getSourceNodeList().isEmpty()) {
			for (GraphNode graphNode : this.mappingGraphModel.getSourceNodeList()) {
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
		if (!this.mappingGraphModel.getTargetNodeList().isEmpty()) {
			for (GraphNode graphNode : this.mappingGraphModel.getTargetNodeList()) {
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
	 * Creates an entity node if there is no one.
	 * 
	 * @param graphConnectionNodeName
	 *            gets set as name for the new entity node
	 * @param resultCell
	 *            gets set a data
	 */
	void createNewEntityNode(String graphConnectionNodeName,
			Cell resultCell) {

		if (this.mappingGraphModel.getEntityNode() == null) {
			final GraphNode newEntityNode = new GraphNode(this.mappingGraphView.getGraph(), SWT.NONE,
					graphConnectionNodeName);
			
			//Set the cell as data into the node
			newEntityNode.setData(resultCell);

			// figure out the y-ordinate for the entity node and set the
			// location
			if (this.mappingGraphModel.getEntityNodeList().isEmpty()) {
				newEntityNode.setLocation(this.mappingGraphView.getGraph().getSize().x * 1 / 3,
						(this.mappingGraphModel.getSourceGraphNode().getLocation().y + 
								this.mappingGraphModel.getTargetGraphNode().getLocation().y) / 2);
				
			} else if (((this.mappingGraphModel.getSourceGraphNode().getLocation().y + 
					this.mappingGraphModel.getTargetGraphNode().getLocation().y) / 2)
					> (this.mappingGraphModel.getEntityNodeList().get(this.mappingGraphModel.getEntityNodeList().size() - 1)
							.getLocation().y)+30) {
				newEntityNode.setLocation(this.mappingGraphView.getGraph().getSize().x * 1 / 3,
					(this.mappingGraphModel.getSourceGraphNode().getLocation().y + 
							this.mappingGraphModel.getTargetGraphNode().getLocation().y) / 2);
			} else {
				newEntityNode.setLocation(this.mappingGraphView.getGraph().getSize().x * 1 / 3,
						(this.mappingGraphModel.getEntityNodeList().get(this.mappingGraphModel.getEntityNodeList().size() - 1)
								.getLocation().y) + 30);
			}
			this.mappingGraphModel.setEntityNode(newEntityNode);
		}
	}
}
