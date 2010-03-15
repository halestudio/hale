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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.userdefined;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;

/**
 * Wizard for the User Defined Function (UDF). A UDF is more or less a 
 * placeholder function that is not executed in lieu of missing transformation 
 * capability, but documents what should be done.
 * TODO: should be possible to add "templates" for UDFs.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class UserDefinedFunctionWizard 
	extends AbstractSingleComposedCellWizard {
	
	private UserDefinedFunctionWizardPage mainPage = null;

	public UserDefinedFunctionWizard(AlignmentInfo selection) {
		super(selection);
	}


	@Override
	protected void init() {
		// TODO Auto-generated method stub
		ICell cell = super.getResultCell();
		this.mainPage = new UserDefinedFunctionWizardPage(
				"Provide information on your user-defined function");
	}


	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		ICell cell = super.getResultCell();
		return false;
	}
	
	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		addPage(mainPage);
	}

}
