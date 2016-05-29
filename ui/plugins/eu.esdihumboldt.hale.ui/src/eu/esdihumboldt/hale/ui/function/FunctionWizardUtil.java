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

package eu.esdihumboldt.hale.ui.function;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.function.contribution.SchemaSelectionFunctionMatcher;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardExtension;
import eu.esdihumboldt.hale.ui.function.internal.NewRelationWizard;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultSchemaSelection;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Function wizard utilities.
 * 
 * @author Simon Templer
 */
public class FunctionWizardUtil {

	/**
	 * Open a wizard that creates a new relation with the given ID and adds the
	 * result to the alignment.
	 * 
	 * @param functionId the function identifier
	 * @param elements the schema selection defining the initial selection or
	 *            <code>null</code>
	 * @return the created cell or <code>null</code>
	 */
	public static Cell createNewWizard(String functionId, SchemaSelection elements) {
		FunctionWizardDescriptor<?> desc = FunctionWizardExtension.getInstance()
				.getWizardDescriptor(functionId);
		FunctionWizard wizard = desc.createNewWizard(elements);

		if (wizard != null) {
			// initialize the wizard
			wizard.init();

			HaleWizardDialog dialog = new HaleWizardDialog(Display.getCurrent().getActiveShell(),
					wizard);

			if (dialog.open() == WizardDialog.OK) {
				MutableCell cell = wizard.getResult();
				AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
				as.addCell(cell);
				return cell;
			}
		}

		return null;
	}

	/**
	 * Launches a wizard for mapping to a specific target entity.
	 * 
	 * @param target the target entity
	 * @return the created cell or <code>null</code>
	 */
	public static Cell addRelationForTarget(EntityDefinition target) {
		return addRelationForTarget(target, null);
	}

	/**
	 * Launches a wizard for mapping to a specific target entity.
	 * 
	 * @param target the target entity
	 * @param source the source entities the target should be mapped from, or
	 *            <code>null</code>
	 * @return the created cell or <code>null</code>
	 */
	public static Cell addRelationForTarget(EntityDefinition target,
			Iterable<EntityDefinition> source) {
		DefaultSchemaSelection initialSelection = new DefaultSchemaSelection();
		initialSelection.addTargetItem(target);
		if (source != null) {
			for (EntityDefinition sourceEntity : source) {
				initialSelection.addSourceItem(sourceEntity);
			}
		}

		SchemaSelectionFunctionMatcher selectionMatcher;
		if (source == null) {
			// ignore source
			selectionMatcher = new SchemaSelectionFunctionMatcher(true, false);
		}
		else {
			// respect source
			selectionMatcher = new SchemaSelectionFunctionMatcher(false, false);
		}

		NewRelationWizard wizard = new NewRelationWizard(initialSelection, selectionMatcher);
		wizard.setWindowTitle("Map to " + target.getDefinition().getDisplayName());
		Shell shell = Display.getCurrent().getActiveShell();
		HaleWizardDialog dialog = new HaleWizardDialog(shell, wizard);
		if (dialog.open() == Window.OK) {
			return wizard.getCreatedCell();
		}
		else {
			return null;
		}
	}
}
