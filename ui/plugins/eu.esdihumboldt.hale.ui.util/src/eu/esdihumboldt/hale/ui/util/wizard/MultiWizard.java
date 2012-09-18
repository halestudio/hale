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

package eu.esdihumboldt.hale.ui.util.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardSelectionPage;

/**
 * Wizard that includes a {@link WizardSelectionPage} and allows the execution
 * of a selected wizard.
 * 
 * @param <T> the wizard selection page type
 * @author Simon Templer
 */
public abstract class MultiWizard<T extends WizardSelectionPage> extends Wizard {

	private T selectionPage;

	/**
	 * Default constructor
	 */
	public MultiWizard() {
		super();

		setForcePreviousAndNextButtons(true);
	}

	/**
	 * Create the wrapper page
	 * 
	 * @return the wrapper page
	 */
	protected abstract T createPage();

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(selectionPage = createPage());
	}

	/**
	 * @return the selectionPage
	 */
	public T getSelectionPage() {
		return selectionPage;
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// nothing to do
		return true;
	}

}
