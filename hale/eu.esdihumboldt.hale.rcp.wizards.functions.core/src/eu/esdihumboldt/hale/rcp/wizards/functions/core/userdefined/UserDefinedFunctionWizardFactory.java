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

import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;

/**
 * WizardFactory for the User defined Function. A UDF is more or less a 
 * placeholder function that is not executed, but documents what should be 
 * done.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class UserDefinedFunctionWizardFactory 
	implements FunctionWizardFactory {

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory#createWizard(eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		return new UserDefinedFunctionWizard(selection);
	}

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory#supports(eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {
		// Can be used on any selection.
		return true;
	}

}
