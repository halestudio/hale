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

package eu.esdihumboldt.hale.ui.firststeps;

import java.util.Arrays;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.function.FunctionWizardUtil;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultSchemaSelection;
import eu.esdihumboldt.hale.ui.views.schemas.SchemasView;

/**
 * Cheat sheet action that opens a dialog/wizard for mapping to a specific
 * target type or property.
 * 
 * @author Simon Templer
 */
public class MapTargetAction extends AbstractTargetAction {

	private static final String FUNCTION_PREFIX = "function:";

	private String functionId;

	@Override
	protected Cell run(EntityDefinition target, ICheatSheetManager manager) {
		return createRelation(target, null, manager);
	}

	/**
	 * Create the relation.
	 * 
	 * @param target the target entity
	 * @param source the source entities the target should be mapped from,
	 *            <code>null</code> by default
	 * @param manager the cheat sheet manager
	 * @return the created cell or <code>null</code>
	 */
	protected Cell createRelation(EntityDefinition target, Iterable<EntityDefinition> source,
			ICheatSheetManager manager) {
		// try selecting the entities in the schema explorer
		DefaultSchemaSelection ss = new DefaultSchemaSelection();
		ss.addTargetItem(target);
		if (source != null) {
			for (EntityDefinition item : source) {
				ss.addSourceItem(item);
			}
		}
		try {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView(SchemasView.ID);
			view.getSite().getSelectionProvider().setSelection(ss);
		} catch (Exception e) {
			// ignore
		}

		// launch the wizard
		if (functionId == null) {
			return FunctionWizardUtil.addRelationForTarget(target, source);
		}
		else {
			return FunctionWizardUtil.createNewWizard(functionId, ss);
		}
	}

	@Override
	public void run(String[] params, ICheatSheetManager manager) {
		functionId = null;
		if (params != null && params.length > 1 && params[0].startsWith(FUNCTION_PREFIX)) {
			functionId = params[0].substring(FUNCTION_PREFIX.length());
			super.run(Arrays.copyOfRange(params, 1, params.length), manager);
		}
		else {
			super.run(params, manager);
		}
	}

}
