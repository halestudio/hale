/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.function.generic.GenericTypeFunctionWizard;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;

/**
 * Content provider for a groovy join type.
 * 
 * @author Sameer Sheikh
 */
public class JoinTypeStructureTray extends TypeStructureTray {

	private static boolean isMerge = false;
	private ParameterValue param = null;

	/**
	 * Create a tray for groovy join type structure
	 * 
	 * @param param parameter value
	 * 
	 * @param types the type provider
	 * @param schemaSpace the schema space
	 */
	public JoinTypeStructureTray(ParameterValue param, TypeProvider types,
			SchemaSpaceID schemaSpace) {
		super(types, schemaSpace, isMerge);
		this.param = param;

	}

	/**
	 * 
	 * Creates a tool item
	 * 
	 * @param bar a toolbar
	 * @param page hale wizard page
	 * @param schemaSpace schema space
	 * @param types type provider
	 */
	public static void createToolItem(ToolBar bar, final HaleWizardPage<?> page,
			final SchemaSpaceID schemaSpace, final TypeProvider types) {

		ToolItem item = new ToolItem(bar, SWT.PUSH);

		item.setImage(
				CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_SOURCE_SCHEMA));
		item.setToolTipText("Show source structure");

		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (page.getContainer() instanceof TrayDialog) {
					TrayDialog dialog = (TrayDialog) page.getContainer();

					// close existing tray
					if (dialog.getTray() != null) {
						dialog.closeTray();
					}
					ParameterValue param = CellUtil.getFirstParameter(
							((GenericTypeFunctionWizard) page.getWizard()).getUnfinishedCell(),
							JoinFunction.PARAMETER_JOIN);

					dialog.openTray(new JoinTypeStructureTray(param, types, schemaSpace));
				}
				else {
					// TODO show dialog instead?
				}
			}
		});

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.groovy.internal.TypeStructureTray#createContentProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	protected IContentProvider createContentProvider(TreeViewer tree) {

		return new TreePathProviderAdapter(new GroovyJoinContentProvider(tree, param));

	}
}
