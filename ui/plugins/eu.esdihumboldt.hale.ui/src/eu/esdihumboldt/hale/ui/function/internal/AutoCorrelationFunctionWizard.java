/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.function.internal;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Wizard for creating an automatic mapping.
 * 
 * @author Yasmina Kammeyer
 */
public class AutoCorrelationFunctionWizard extends Wizard {

	/**
	 * Page to set the source types
	 */
	protected AutoCorrelationTypesPage typePage;

	/**
	 * Page to set and select the needed Parameter
	 */
	protected AutoCorrelationParameterPage parameterPage;

	/**
	 * Default Constructor
	 */
	public AutoCorrelationFunctionWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public String getWindowTitle() {
		return "Auto Correlation";
	}

	@Override
	public void addPages() {
		typePage = new AutoCorrelationTypesPage("Types");
		parameterPage = new AutoCorrelationParameterPage("Parameter");

		addPage(typePage);
		addPage(parameterPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// Add all cells
		MutableCell cell = null;//
		if (cell != null) {
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
			as.addCell(cell);
		}

		// save page configuration ???

		return true;
	}

	/**
	 * @return the cell created through the wizard, or <code>null</code>
	 */
	public Cell getCreatedCell() {
		return null;// createdCell;
	}

}
