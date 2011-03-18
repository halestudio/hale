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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.augmentation.nilreason;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.corefunctions.NilReasonFunction;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizard;
import eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizardFactory;

/**
 * Factory for the NilReasonWizard
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NilReasonWizardFactory extends AugmentationWizardFactory {

	/**
	 * @see AugmentationWizardFactory#createWizard(SchemaItem, ICell)
	 */
	@Override
	protected AugmentationWizard createWizard(SchemaItem item,
			ICell augmentation) {
		return new NilReasonWizard(item, augmentation);
	}

	/**
	 * @see AugmentationWizardFactory#supports(SchemaItem, ICell)
	 */
	@Override
	protected boolean supports(SchemaItem item, ICell augmentation) {
		if (!item.isAttribute()) {
			return false;
		}
		
		if (item.hasChildren()) {
			boolean foundNilReason = false;
			for (SchemaItem child : item.getChildren()) {
				if (child.getName().getLocalPart().equalsIgnoreCase("nilreason") //$NON-NLS-1$
						&& child.isAttribute()) {
					foundNilReason = true;
				}
			}
			if (!foundNilReason) {
				return false;
			}
		}
		else {
			return false;
		}
		
		return augmentation == null || augmentation.getEntity2().getTransformation()
			.getService().getLocation().equals(NilReasonFunction.class.getName());
	}

}
