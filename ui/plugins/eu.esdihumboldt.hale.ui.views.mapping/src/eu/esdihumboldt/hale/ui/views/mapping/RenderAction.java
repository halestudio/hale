/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.mapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import eu.esdihumboldt.hale.ui.util.graph.OffscreenGraph;


/**
 * TODO Type description
 * @author Simon Templer
 */
public class RenderAction extends Action {

	private GraphViewer viewer;

	/**
	 * @param viewer
	 */
	public RenderAction(GraphViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		OffscreenGraph graph = new OffscreenGraph(1000, 1000) {
			
			@Override
			protected void configureViewer(GraphViewer viewer) {
				viewer.setContentProvider(RenderAction.this.viewer.getContentProvider());
				viewer.setLabelProvider(RenderAction.this.viewer.getLabelProvider());
				viewer.setInput(RenderAction.this.viewer.getInput());
				viewer.setLayoutAlgorithm(new TreeLayoutAlgorithm(TreeLayoutAlgorithm.LEFT_RIGHT), false);
			}
		};
		
		File file = new File("C:\\Temp\\aa_test.png");
		
		try {
			graph.save(new FileOutputStream(file), "png");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.run();
	}

}
