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

	@Override
	protected Cell run(EntityDefinition target, ICheatSheetManager manager) {
		// try selecting the target entity in the schema explorer
		try {
			DefaultSchemaSelection ss = new DefaultSchemaSelection();
			ss.addTargetItem(target);

			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView(SchemasView.ID);
			view.getSite().getSelectionProvider().setSelection(ss);
		} catch (Exception e) {
			// ignore
		}

		// launch the wizard
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
		return FunctionWizardUtil.addRelationForTarget(target, source);
	}

}
