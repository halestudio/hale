/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.cst.debug.metadata.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;

/**
 * Dialog for displaying a GraphML graph via zest
 * 
 * @author Sebastian Reinhardt
 */
public class GraphMLDialog extends Dialog {

	String graphString;

	/**
	 * constructor with int-style parameter
	 * 
	 * @param parent the parent shell
	 * @param style the style
	 */
	public GraphMLDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * constructor with graph as string parameter
	 * 
	 * @param parent the parent shell
	 * @param graphString the string representation of the graph
	 */
	public GraphMLDialog(Shell parent, String graphString) {
		this(parent, SWT.NONE);
		this.graphString = graphString;
	}

	/**
	 * Opens the dialog for displaying the graph
	 * 
	 * @throws IOException may be thrown if the graph string from the database
	 *             can fails to convert
	 */
	public void open() throws IOException {
		Shell parent = super.getParent();
		Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setLayout(GridLayoutFactory.fillDefaults().create());
		shell.setText(getText());

		final Composite viewerContainer = new Composite(shell, SWT.EMBEDDED);
		viewerContainer.setLayout(new FillLayout());
		viewerContainer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		TinkerGraph tgraph = new TinkerGraph();

		GraphMLReader greader = new GraphMLReader(tgraph);
		ByteArrayInputStream in;
		in = new ByteArrayInputStream(graphString.getBytes(("UTF-8")));
		greader.inputGraph(in);

		GraphViewer viewer = new GraphViewer(viewerContainer, SWT.NONE);
		TreeLayoutAlgorithm la = new TreeLayoutAlgorithm(TreeLayoutAlgorithm.RIGHT_LEFT);
		viewer.setLabelProvider(new GraphMLLabelProvider());
		viewer.setContentProvider(new GraphMLContentProvider());
		viewer.setInput(tgraph.getEdges());
		viewer.setLayoutAlgorithm(la, true);
		viewer.applyLayout();

		viewerContainer.pack();
		viewerContainer.setVisible(true);

		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}
