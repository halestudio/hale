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

package eu.esdihumboldt.hale.ui.model.augmentations;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.ui.model.functions.AlignmentInfo;
import eu.esdihumboldt.hale.ui.model.functions.CellSelectionInfo;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizard;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.model.schema.NullSchemaItem;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Factory for augmentation function wizards
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AugmentationWizardFactory implements
		FunctionWizardFactory {

	/**
	 * @see FunctionWizardFactory#createWizard(AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		SchemaItem item = selection.getFirstTargetItem();
		
		// get augmentation cell
		ICell augmentation = selection.getAlignment(NullSchemaItem.INSTANCE, item);
		
		return createWizard(item, augmentation);
	}

	/**
	 * Create a wizard to create or edit an augmentation 
	 * 
	 * @param item the item to be augmented
	 * @param augmentation the current augmentation, may be <code>null</code>
	 * 
	 * @return the augmentation wizard
	 */
	protected abstract AugmentationWizard createWizard(SchemaItem item, ICell augmentation);

	/**
	 * @see FunctionWizardFactory#supports(AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {
		// requires exactly one target item and no source item or the NULL_ENTITY as source item
		/*if ((selection.getSourceItemCount() != 0 && !selection.getFirstSourceItem().equals(NullSchemaItem.INSTANCE))
				|| selection.getTargetItemCount() != 1) {
			return false;
		}*/
		
		// only one target item must be selected, the source selection is ignored
		if (selection.getTargetItemCount() != 1) {
			return false;
		}
		
		SchemaItem item = selection.getFirstTargetItem();
		
		AlignmentService alignmentService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		
		//TODO? check if there is any mapping for this item? or rather not?
		
		// get augmentation cell
		ICell augmentation = selection.getAlignment(NullSchemaItem.INSTANCE, item);
		
		// special case: editing a cell that is not the augmentation cell -> use alignment service to get it
		if (augmentation == null && selection instanceof CellSelectionInfo) {
			augmentation = alignmentService.getCell(Entity.NULL_ENTITY, item.getEntity());
		}
		
		return supports(item, augmentation);
	}

	/**
	 * Determine if the given item is supported by the augmentation
	 * 
	 * @param item the item to be augmented
	 * @param augmentation the current augmentation of the item, may
	 *   be <code>null</code>
	 * 
	 * @return if the augmentation of the given item is supported
	 */
	protected abstract boolean supports(SchemaItem item, ICell augmentation);

}
