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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.filter;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;

/**
 * Factory for {@link FilterWizard}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @since 1.0.0-M4
 */
public class SimpleFilterWizardFactory implements FunctionWizardFactory {

	/**
	 * @see FunctionWizardFactory#createWizard(AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		return new SimpleFilterWizard(selection);
	}

	/**
	 * @see FunctionWizardFactory#supports(AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {
		if (selection.getSourceItemCount() >= 1 && // at least one source item (no augmentations supported)
				selection.getTargetItemCount() >= 1) {
			// ensure that for composed properties the parent feature is the same
			if (selection.getSourceItemCount() > 1) {
				SchemaItem type = null;
				for (SchemaItem item : selection.getSourceItems()) {
					if (type == null) {
						type = FilterUtils2.getParentTypeItem(item);
					}
					else {
						SchemaItem type2 = FilterUtils2.getParentTypeItem(item);
						if (!type.getDefinition().equals(type2.getDefinition())) { // TypeDefinition.equals
							return false;
						}
					}
				}
			}
			
			ICell cell = selection.getAlignment(selection.getSourceItems(), selection.getTargetItems());
			
			return cell != null;
		}
		
		return false;
	}

}
