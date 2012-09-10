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

package eu.esdihumboldt.hale.ui.function.internal;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.util.wizard.MultiWizard;

/**
 * Wizard for creating a new relation.
 * 
 * @author Simon Templer
 */
public class NewRelationWizard extends MultiWizard<NewRelationPage> {

	/**
	 * Default constructor
	 */
	public NewRelationWizard() {
		super();

		setWindowTitle("New relation");
	}

	/**
	 * @see MultiWizard#createPage()
	 */
	@Override
	protected NewRelationPage createPage() {
		return new NewRelationPage("Select the type of the new relation");
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
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
			as.addCell(cell);
		}

		// save page configuration
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
		getSelectionPage().store(ps.getConfigurationService());

		return true;
	}

}
