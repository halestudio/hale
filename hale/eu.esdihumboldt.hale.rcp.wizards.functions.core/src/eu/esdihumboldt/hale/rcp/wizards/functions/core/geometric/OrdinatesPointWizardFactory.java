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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.geometric;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;

/**
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class OrdinatesPointWizardFactory 
	implements FunctionWizardFactory {

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory#createWizard(eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		return new OrdinatesPointWizard(selection);
	}

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory#supports(eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {
		// must be exactly two source items and exactly one target item
		if (selection.getSourceItemCount() != 2 || selection.getTargetItemCount() != 1) {
			return false;
		}

		// target item must be a property
		SchemaItem target = selection.getFirstTargetItem();
		if (target.getPropertyType() == null ) {
			return false;
		}
		if (!target.isAttribute()
				|| !Geometry.class.isAssignableFrom(
				target.getPropertyType().getBinding())) {
			return false;
		}
		
		// source items must be properties
		for (SchemaItem source : selection.getSourceItems()) {
			if (source.getPropertyType() == null ) {
				return false;
			}
			if (!source.isAttribute() 
					|| !Number.class.isAssignableFrom(
					source.getPropertyType().getBinding())) {
				return false;
			}
		}
		
//		ICell cell = selection.getAlignment(selection.getSourceItems(),
//				selection.getTargetItems());
//		if (cell != null) {
//			// only allow editing matching transformation
//			try {
//				return cell.getEntity1().getTransformation().getService().getLocation().equals(
//						GenericMathFunction.class.getName());
//			} catch (NullPointerException e) {
//				return false;
//			}
//		}
		
		return true;
	}

}
