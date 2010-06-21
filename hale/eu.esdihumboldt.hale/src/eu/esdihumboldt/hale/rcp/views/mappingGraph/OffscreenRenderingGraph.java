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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

/**
 * TODO Typedescription
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class OffscreenRenderingGraph extends Graph {

	/**
	 * @param parent
	 * @param style
	 */
	public OffscreenRenderingGraph(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}
	
	public void applyLayout(LayoutAlgorithm layoutAlgorithm) {

		if ((this.getNodes().size() == 0)) {
			return;
		}

		int layoutStyle = 0;

//		if ((nodeStyle & ZestStyles.NODES_NO_LAYOUT_RESIZE) > 0) {
//			layoutStyle = LayoutStyles.NO_LAYOUT_NODE_RESIZING;
//		}

		layoutAlgorithm.setStyle(layoutAlgorithm.getStyle() | layoutStyle);

		// calculate the size for the layout algorithm
		Dimension d = this.getViewport().getSize();
//		d.width = d.width - 10;
//		d.height = d.height - 10;

		if (d.isEmpty()) {
			return;
		}
		LayoutRelationship[] connectionsToLayout = getMyConnectionsToLayout(getNodes());
		LayoutEntity[] nodesToLayout = getMyNodesToLayout(getNodes());

		try {
			Animation.markBegin();
			layoutAlgorithm.applyLayout(nodesToLayout, connectionsToLayout, 0, 0, d.width, d.height, false, false);
			Animation.run(ANIMATION_TIME);
			getLightweightSystem().getUpdateManager().performUpdate();

		} catch (InvalidLayoutConfiguration e) {
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("all")
	private LayoutEntity[] getMyNodesToLayout(List nodes) {
		// @tag zest.bug.156528-Filters.follows : make sure not to layout
		// filtered nodes, if the style says so.
		LayoutEntity[] entities;
		if (ZestStyles.checkStyle(getStyle(), ZestStyles.IGNORE_INVISIBLE_LAYOUT)) {
			LinkedList nodeList = new LinkedList();
			for (Iterator i = nodes.iterator(); i.hasNext();) {
				GraphNode next = (GraphNode) i.next();
				if (next.isVisible()) {
					nodeList.add(next.getLayoutEntity());
				}
			}
			entities = (LayoutEntity[]) nodeList.toArray(new LayoutEntity[] {});
		} else {
			LinkedList nodeList = new LinkedList();
			for (Iterator i = nodes.iterator(); i.hasNext();) {
				GraphNode next = (GraphNode) i.next();
				nodeList.add(next.getLayoutEntity());
			}
			entities = (LayoutEntity[]) nodeList.toArray(new LayoutEntity[] {});
		}
		return entities;
	}
	
	@SuppressWarnings("all")
	private LayoutRelationship[] getMyConnectionsToLayout(List nodesToLayout) {
		// @tag zest.bug.156528-Filters.follows : make sure not to layout
		// filtered connections, if the style says so.
		LayoutRelationship[] entities;
		if (ZestStyles.checkStyle(getStyle(), ZestStyles.IGNORE_INVISIBLE_LAYOUT)) {
			LinkedList connectionList = new LinkedList();
			for (Iterator i = this.getConnections().iterator(); i.hasNext();) {
				GraphConnection next = (GraphConnection) i.next();
				if (next.isVisible() && nodesToLayout.contains(next.getSource()) && nodesToLayout.contains(next.getDestination())) {
					connectionList.add(next.getLayoutRelationship());
				}
			}
			entities = (LayoutRelationship[]) connectionList.toArray(new LayoutRelationship[] {});
		} else {
			LinkedList nodeList = new LinkedList();
			for (Iterator i = this.getConnections().iterator(); i.hasNext();) {
				GraphConnection next = (GraphConnection) i.next();
				if (nodesToLayout.contains(next.getSource()) && nodesToLayout.contains(next.getDestination())) {
					nodeList.add(next.getLayoutRelationship());
				}
			}
			entities = (LayoutRelationship[]) nodeList.toArray(new LayoutRelationship[] {});
		}
		return entities;
	}

//	@Override
//	public boolean isVisible() {
//		return true;
//	}

}
