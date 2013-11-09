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

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.util.groovy.view.ASTViewer;

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
			final ITextViewer viewer) {
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

	private final ITextViewer textViewer;

	/**
	 * Create a Groovy AST tray.
	 * 
	 * @param viewer the associated viewer with the Groovy source or
	 *            <code>null</code>
	 */
	public GroovyASTTray(ITextViewer viewer) {
		super();

		this.textViewer = viewer;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);

		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(page);

		ASTViewer viewer = new ASTViewer(page, textViewer);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getControl());

		// XXX
		if (textViewer != null) {
			String content = textViewer.getDocument().get();
			AstBuilder builder = new AstBuilder();
			List<ASTNode> ast = builder.buildFromString(content);

			viewer.setInput(ast);
		}

		return page;
	}
}
