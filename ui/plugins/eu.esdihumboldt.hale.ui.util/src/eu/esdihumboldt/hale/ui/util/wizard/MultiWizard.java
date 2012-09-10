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
