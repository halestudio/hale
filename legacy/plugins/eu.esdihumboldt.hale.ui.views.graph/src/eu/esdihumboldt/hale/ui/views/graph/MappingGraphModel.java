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
package eu.esdihumboldt.hale.ui.views.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.omwg.Restriction;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

/**
 * MappingGraphModel contains the graphNodes, entity's and connections
 * 
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class MappingGraphModel {

	/**
	 * The specific ID of this class
	 */
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.mappingGraph.MappingGraphModel"; //$NON-NLS-1$

	/**
	 * The list which contains the source nodes
	 */
	private ArrayList<GraphNode> sourceNodeList = new ArrayList<GraphNode>();

	/**
	 * The list which contains the target nodes
	 */
	private ArrayList<GraphNode> targetNodeList = new ArrayList<GraphNode>();

	/**
	 * The list which contains the entity nodes
	 */
	private ArrayList<GraphNode> entityNodeList = new ArrayList<GraphNode>();
	
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
	 * All ArrayLists will be reseted.
	 */
	void arrayReset() {

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
	 * @param graphConnectionNodeName is the name to be searched
	 * @param resultCell contains content to search
	 */
	@SuppressWarnings("unchecked") 
	void checkForExistingConnections(String graphConnectionNodeName,
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
	 * @param schemaSelection which gets schema items added
	 * @param source boolean, if true=source false=target
	 * @param root is the root schema item of source or target side
	 */
	void rekursiveGetChildren(SchemaSelection schemaSelection,
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
	 * Gets the filter attributes and writes them into a list
	 * 
	 * @param cell
	 *            is the cell which contains filters
	 * @return a List of Strings with Filters
	 */
	List<String> getFilters(Cell cell) {
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
						filters.add(restriction.getCqlStr() + "\r\n"); //$NON-NLS-1$
					}
				}
			} else if (cell.getEntity2() instanceof Property) {
				if (((Property) cell.getEntity2())
						.getValueCondition() != null) {
					for (Restriction restriction : ((Property) cell
							.getEntity2()).getValueCondition()) {
						filters.add(restriction.getCqlStr() + "\r\n"); //$NON-NLS-1$
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
						filters.add(restriction.getCqlStr() + "\r\n"); //$NON-NLS-1$
					}
				}
			} else if (cell.getEntity1() instanceof Property) {
				if (((Property) cell.getEntity1()).getValueCondition() != null) {
					for (Restriction restriction : ((Property) cell
							.getEntity1()).getValueCondition()) {
						filters.add(restriction.getCqlStr() + "\r\n"); //$NON-NLS-1$
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
	String listToString(List<String> list) {
		String toolTipText = ""; //$NON-NLS-1$

		if (!list.isEmpty()) {
			for (String stringtext : list) {
				toolTipText = toolTipText + stringtext + "\r\n"; //$NON-NLS-1$
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
	String parameterListToString(List<IParameter> parameterList) {
		String toolTipText = ""; //$NON-NLS-1$

		if (!parameterList.isEmpty()) {
			for (IParameter parameter : parameterList) {
				toolTipText = toolTipText + parameter.getName() + " : "; //$NON-NLS-1$
				toolTipText = toolTipText + parameter.getValue() + "\r\n"; //$NON-NLS-1$
			}
		}
		return toolTipText;
	}

	/**
	 * @return The list of source nodes
	 */
	public ArrayList<GraphNode> getSourceNodeList() {
		return this.sourceNodeList;
	}

	/**
	 * @param sourceNodeList
	 */
	public void setSourceNodeList(ArrayList<GraphNode> sourceNodeList) {
		this.sourceNodeList = sourceNodeList;
	}

	/**
	 * @return The list of target nodes
	 */
	public ArrayList<GraphNode> getTargetNodeList() {
		return this.targetNodeList;
	}

	/**
	 * @param targetNodeList
	 */
	public void setTargetNodeList(ArrayList<GraphNode> targetNodeList) {
		this.targetNodeList = targetNodeList;
	}

	/**
	 * @return The list of entity nodes
	 */
	public ArrayList<GraphNode> getEntityNodeList() {
		return this.entityNodeList;
	}

	/**
	 * @param entityNodeList
	 */
	public void setEntityNodeList(ArrayList<GraphNode> entityNodeList) {
		this.entityNodeList = entityNodeList;
	}

	/**
	 * @return The target node which is already there
	 */
	public GraphNode getTargetGraphNodeAlreadyThere() {
		return this.targetGraphNodeAlreadyThere;
	}

	/**
	 * @param targetGraphNodeAlreadyThere
	 */
	public void setTargetGraphNodeAlreadyThere(GraphNode targetGraphNodeAlreadyThere) {
		this.targetGraphNodeAlreadyThere = targetGraphNodeAlreadyThere;
	}

	/**
	 * @return The source node which is already there
	 */
	public GraphNode getSourceGraphNodeAlreadyThere() {
		return this.sourceGraphNodeAlreadyThere;
	}

	/**
	 * @param sourceGraphNodeAlreadyThere
	 */
	public void setSourceGraphNodeAlreadyThere(GraphNode sourceGraphNodeAlreadyThere) {
		this.sourceGraphNodeAlreadyThere = sourceGraphNodeAlreadyThere;
	}

	/**
	 * @return The entity node
	 */
	public GraphNode getEntityNode() {
		return this.entityNode;
	}

	/**
	 * @param entityNode
	 */
	public void setEntityNode(GraphNode entityNode) {
		this.entityNode = entityNode;
	}

	/**
	 * @return The source node
	 */
	public GraphNode getSourceGraphNode() {
		return this.sourceGraphNode;
	}

	/**
	 * @param sourceGraphNode
	 */
	public void setSourceGraphNode(GraphNode sourceGraphNode) {
		this.sourceGraphNode = sourceGraphNode;
	}

	/**
	 * @return targetGraphNode
	 */
	public GraphNode getTargetGraphNode() {
		return this.targetGraphNode;
	}

	/**
	 * @param targetGraphNode
	 */
	public void setTargetGraphNode(GraphNode targetGraphNode) {
		this.targetGraphNode = targetGraphNode;
	}
	
	
	
}
