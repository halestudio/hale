/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.functions.core.reference;

import org.eclipse.jface.wizard.IWizard;

import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;

/**
 * @author Thorsten Reitz
 * @version $Id$
 */
public class CreateReferenceWizard 
	extends AbstractSingleCellWizard 
	implements FunctionWizard {
	
	private CreateReferenceWizardPage mainPage;

	/**
	 * @param selection
	 */
	public CreateReferenceWizard(AlignmentInfo selection) {
		super(selection);
	}

	@Override
	protected void init() {
		this.mainPage = new CreateReferenceWizardPage(
				Messages.CreateReferenceWizard_0);

	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * @see IWizard#addPages()
     */
    public void addPages() {
        super.addPages(); 
        addPage(this.mainPage);
    }

}
