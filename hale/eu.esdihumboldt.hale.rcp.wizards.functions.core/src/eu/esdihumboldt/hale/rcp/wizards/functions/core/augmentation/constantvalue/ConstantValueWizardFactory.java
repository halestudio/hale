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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.augmentation.constantvalue;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.corefunctions.ConstantValueFunction;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;
import eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizard;
import eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizardFactory;

/**
 * Factory for the ConstantValueWizard
 *
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$ 
 */
public class ConstantValueWizardFactory extends AugmentationWizardFactory {

	

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizardFactory#createWizard(eu.esdihumboldt.hale.rcp.views.model.SchemaItem, eu.esdihumboldt.cst.align.ICell)
	 */
	@Override
	protected AugmentationWizard createWizard(SchemaItem item,
			ICell augmentation) {
		
		return new ConstantValueWizard(item, augmentation);
	}

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizardFactory#supports(eu.esdihumboldt.hale.rcp.views.model.SchemaItem, eu.esdihumboldt.cst.align.ICell)
	 */
	@Override
	protected boolean supports(SchemaItem item, ICell augmentation) {
		boolean supports = false;
		//defined on the attributes of the simple type only
		if (item.isAttribute()&&(item.getType().equals(TreeObjectType.NUMERIC_ATTRIBUTE)||item.getType().equals(TreeObjectType.STRING_ATTRIBUTE))){
			supports =(augmentation == null || augmentation.getEntity2().getTransformation().getService().getLocation().equals(ConstantValueFunction.class.getName()));
		}
		return supports;
	}

}
