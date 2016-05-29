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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.function.internal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.contribution.SchemaSelectionFunctionMatcher;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;
import eu.esdihumboldt.hale.ui.util.wizard.MultiWizard;

/**
 * Wizard for creating a new relation.
 * 
 * @author Simon Templer
 */
public class NewRelationWizard extends MultiWizard<NewRelationPage> {

	private final SchemaSelection initialSelection;
	private final SchemaSelectionFunctionMatcher selectionMatcher;

	private Cell createdCell;

	/**
	 * Default constructor
	 */
	public NewRelationWizard() {
		this(null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param initialSelection the initial selection to initialize the wizard
	 *            with, may be <code>null</code> to start with an empty
	 *            configuration
	 * @param selectionMatcher the matcher that determines if a function is
	 *            applicable for the initial selection, may be <code>null</code>
	 *            to allow all functions
	 */
	public NewRelationWizard(SchemaSelection initialSelection,
			SchemaSelectionFunctionMatcher selectionMatcher) {
		super();

		this.initialSelection = initialSelection;
		this.selectionMatcher = selectionMatcher;

		setHelpAvailable(true);
		setWindowTitle("New relation");
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);

		// disable help button, let function wizards do their work afterwards
		// At all other points the buttons aren't created yet.
		if (getContainer() instanceof HaleWizardDialog)
			((HaleWizardDialog) getContainer()).setHelpButtonEnabled(false);
	}

	/**
	 * @see MultiWizard#createPage()
	 */
	@Override
	protected NewRelationPage createPage() {
		return new NewRelationPage("Select the type of the new relation", initialSelection,
				selectionMatcher);
	}

	/**
	 * @see MultiWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// performFinish of the function wizard was called first

		FunctionWizard functionWizard = getSelectionPage().getFunctionWizard();

		if (functionWizard == null) {
			return false;
		}

		MutableCell cell = functionWizard.getResult();
		if (cell != null) {
			AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
			as.addCell(cell);
		}
		createdCell = cell;

		// save page configuration
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		getSelectionPage().store(ps.getConfigurationService());

		return true;
	}

	/**
	 * @return the cell created through the wizard, or <code>null</code>
	 */
	public Cell getCreatedCell() {
		return createdCell;
	}

}
