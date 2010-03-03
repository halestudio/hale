/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : hale
 * 	 
 * Classname    : eu.esdihumboldt.hale.rcp.wizards.functions.generic.GenericFunctionWizardFactory.java 
 * 
 * Author       : Josef Bezdek
 * 
 * Created on   : Jan, 2010
 *
 */

package eu.esdihumboldt.hale.rcp.wizards.functions.generic;

import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;

public class GenericFunctionWizardFactory implements FunctionWizardFactory {

	public GenericFunctionWizardFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		// TODO Auto-generated method stub
		return new GenericFunctionWizard(selection);
	}

	@Override
	public boolean supports(AlignmentInfo selection) {
		if (selection.getFirstSourceItem().isFeatureType() || selection.getFirstTargetItem().isFeatureType()) {
			return false;
		}
		return true;
	}

}
