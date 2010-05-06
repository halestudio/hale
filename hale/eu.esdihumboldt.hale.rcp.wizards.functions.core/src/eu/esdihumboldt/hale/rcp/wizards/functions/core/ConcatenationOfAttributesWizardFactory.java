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
package eu.esdihumboldt.hale.rcp.wizards.functions.core;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;

/**
 * TODO Typedescription
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class ConcatenationOfAttributesWizardFactory implements
		FunctionWizardFactory {

	/**
	 * 
	 */
	public ConcatenationOfAttributesWizardFactory() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory#createWizard(eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		// TODO Auto-generated method stub
		return new ConcatenationOfAttributesWizard(selection);
	}

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory#supports(eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {
		
		// must be one or more source items and exactly one target item
		if (selection.getSourceItemCount() < 1 || selection.getTargetItemCount() != 1) {
			return false;
		}
		
		// target item must be a property
		SchemaItem target = selection.getFirstTargetItem();
		if (!target.isAttribute() && !String.class.isAssignableFrom(
				target.getPropertyType().getBinding())) {
			return false;
		}
		
		// source items must be properties
		for (SchemaItem source : selection.getSourceItems()) {
			if (!source.isAttribute()) {
				return false;
			}
			if (!Number.class.isAssignableFrom(source.getPropertyType().getBinding())
					&& !String.class.isAssignableFrom(source.getPropertyType().getBinding())){
				return false;
			}
		}
		return true;
	}
	
	

}
