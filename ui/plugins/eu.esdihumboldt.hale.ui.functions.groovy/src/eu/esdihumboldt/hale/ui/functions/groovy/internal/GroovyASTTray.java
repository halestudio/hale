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

package eu.esdihumboldt.hale.ui.functions.groovy.internal;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.util.groovy.ast.GroovyAST;
import eu.esdihumboldt.hale.ui.util.groovy.ast.viewer.ASTViewer;
import eu.esdihumboldt.hale.ui.util.source.CompilingSourceViewer;

/**
 * Dialog tray displaying the Groovy AST for the source.
 * 
 * @author Simon Templer
 */
public class GroovyASTTray extends DialogTray {

	/**
	 * Create a tool item for displaying the Groovy AST in the dialog tray.
	 * 
	 * @param bar the tool bar to add the item to
	 * @param page the associated wizard page
	 * @param viewer the associated viewer with the Groovy source or
	 *            <code>null</code>
	 */
	public static void createToolItem(ToolBar bar, final HaleWizardPage<?> page,
			final CompilingSourceViewer<GroovyAST> viewer) {
		ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setText("AST");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (page.getContainer() instanceof TrayDialog) {
					TrayDialog dialog = (TrayDialog) page.getContainer();

					// close existing tray
					if (dialog.getTray() != null) {
						dialog.closeTray();
					}

					dialog.openTray(new GroovyASTTray(viewer));
				}
				else {
					// TODO show dialog instead?
				}
			}
		});
	}

	private final CompilingSourceViewer<GroovyAST> groovyViewer;

	/**
	 * Create a Groovy AST tray.
	 * 
	 * @param viewer the associated viewer with the Groovy source or
	 *            <code>null</code>
	 */
	public GroovyASTTray(CompilingSourceViewer<GroovyAST> viewer) {
		super();

		this.groovyViewer = viewer;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);

		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(page);

		final ASTViewer viewer = new ASTViewer(page, groovyViewer);
		GridDataFactory.fillDefaults().grab(true, true).hint(400, SWT.DEFAULT)
				.applyTo(viewer.getControl());

		if (groovyViewer != null) {
			// current AST
			try {
				GroovyAST ast = groovyViewer.getCompiled().get();
				if (ast != null) {
					viewer.setInput(ast.getNodes());
					viewer.getTreeViewer().expandAll();
				}
				else {
					viewer.setInput(null);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// listen to AST changes
			final IPropertyChangeListener listener = new IPropertyChangeListener() {

				@Override
				public void propertyChange(final PropertyChangeEvent event) {
					if (CompilingSourceViewer.PROPERTY_COMPILED.equals(event.getProperty())) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {
								if (event.getNewValue() instanceof GroovyAST) {
									viewer.setInput(((GroovyAST) event.getNewValue()).getNodes());
									viewer.getTreeViewer().expandAll();
								}
								else {
									viewer.setInput(null);
								}
							}
						});
					}
				}
			};
			groovyViewer.addPropertyChangeListener(listener);

			// ensure listener is removed
			page.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent e) {
					groovyViewer.removePropertyChangeListener(listener);
				}
			});
		}

		return page;
	}
}
