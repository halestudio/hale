/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.util.groovy.view;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.google.common.collect.ListMultimap;

/**
 * A viewer for displaying Groovy AST nodes.
 * 
 * @author Simon Templer
 */
public class ASTViewer {

	private class ViewContentProvider extends ArrayContentProvider implements ITreeContentProvider {

		private ListMultimap<Object, ?> children;

		@Override
		public void dispose() {
			children = null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			ASTToTreeVisitor v = new ASTToTreeVisitor();

			if (newInput instanceof Iterable<?>) {
				for (Object node : ((Iterable<?>) newInput)) {
					if (node instanceof ASTNode) {
						((ASTNode) node).visit(v);
					}
				}
			}

			children = v.getChildren();
		}

		@Override
		public Object getParent(Object child) {
			return null;
		}

		@Override
		public Object[] getChildren(Object parent) {
			if (children != null && parent instanceof ASTNode) {
				return children.get(parent).toArray();
			}
			return new Object[0];
		}

		@Override
		public boolean hasChildren(Object parent) {
			if (children != null && parent instanceof ASTNode) {
				return children.containsKey(parent);
			}
			return false;
		}
	}

	private class ViewLabelProvider extends LabelProvider {

		@Override
		public String getText(Object obj) {
			if (obj instanceof ASTNode) {
				ASTNode node = (ASTNode) obj;
				return node.getText();
			}

			return super.getText(obj);
		}

	}

	private final TreeViewer viewer;

	private Action doubleClickAction;

	private final ITextViewer textViewer;

	/**
	 * Constructor.
	 * 
	 * @param parent the parent composite
	 * @param textViewer a text viewer where the code for an AST node should be
	 *            selected on double click, or <code>null</code>
	 */
	public ASTViewer(Composite parent, ITextViewer textViewer) {
		this.textViewer = textViewer;

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
//		new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(null);
		viewer.setInput(null);
		makeActions();
		if (textViewer != null) {
			addDoubleClickAction();
		}
	}

	/**
	 * @return the control
	 */
	public Control getControl() {
		return viewer.getControl();
	}

	/**
	 * Set the input of the viewer to a set of AST Nodes
	 * 
	 * @param ast the AST nodes
	 */
	public void setInput(List<ASTNode> ast) {
		viewer.setInput(ast);
	}

	private void makeActions() {
		doubleClickAction = new Action() {

			@Override
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj == null) {
					return;
				}
				if (obj instanceof ASTNode) {
					ASTNode node = (ASTNode) obj;
					if (node.getLineNumber() != -1) {
						try {
							int offset0 = textViewer.getDocument().getLineOffset(
									node.getLineNumber() - 1)
									+ node.getColumnNumber() - 1;
							int offset1 = textViewer.getDocument().getLineOffset(
									node.getLastLineNumber() - 1)
									+ node.getLastColumnNumber() - 1;
							textViewer.setSelectedRange(offset0, offset1 - offset0);
						} catch (BadLocationException e) {
							// XXX
							e.printStackTrace();
						}
					}
				}
			}
		};
	}

	private void addDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

}