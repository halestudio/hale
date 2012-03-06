/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.function.contribution;

import java.util.Set;

import org.eclipse.ui.PlatformUI;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.ui.function.contribution.internal.SchemaSelectionWizardAction;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.selection.SchemaSelectionHelper;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Function wizard contribution based on {@link SchemaSelection}
 * @author Simon Templer
 */
public class SchemaSelectionFunctionContribution extends
		AbstractFunctionWizardContribution {
	
	private static final ALogger log = ALoggerFactory.getLogger(SchemaSelectionFunctionContribution.class);

	/**
	 * @see AbstractFunctionWizardContribution#createWizardAction(FunctionWizardDescriptor, AlignmentService)
	 */
	@Override
	protected SchemaSelectionWizardAction createWizardAction(
			FunctionWizardDescriptor<?> descriptor,
			AlignmentService alignmentService) {
		return new SchemaSelectionWizardAction(this, descriptor, alignmentService);
	}
	
	/**
	 * @see AbstractFunctionWizardContribution#isActive(FunctionWizardDescriptor)
	 */
	@Override
	public boolean isActive(FunctionWizardDescriptor<?> descriptor) {
		AbstractFunction<?> function = descriptor.getFunction();
		if (function instanceof TypeFunction) {
			TypeFunction tf = (TypeFunction) function;
			// match selection against function definition
			return matchTypeFunction(tf, getSelection());
		}
		else if (function instanceof PropertyFunction) {
			PropertyFunction pf = (PropertyFunction) function;
			// match selection against function definition
			return matchPropertyFunction(pf, getSelection());
		}
		else {
			log.error("Unsupported function deactivated");
			return false;
		}
	}

	/**
	 * Test if the given function definition matches the given selection 
	 * @param function the function definition
	 * @param selection the schema selection
	 * @return <code>true</code> if the definition matches the selection,
	 *   <code>false</code> otherwise 
	 */
	private boolean matchPropertyFunction(PropertyFunction function,
			SchemaSelection selection) {
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		
		if (selection == null || selection.isEmpty()) {
			// for no selection always allow creating a new cell if there are
			// type relations present
			return AlignmentUtil.hasTypeRelation(as.getAlignment());
		}
		
		// check types
		Set<EntityDefinition> sourceItems = selection.getSourceItems();
		if (!checkType(sourceItems, PropertyEntityDefinition.class)) {
			return false;
		}
		Set<EntityDefinition> targetItems = selection.getTargetItems();
		if (!checkType(targetItems, PropertyEntityDefinition.class)) {
			return false;
		}
		
		//TODO check if properties have the same parent type? what about joins?
		
		// check counts
		if (!checkCount(sourceItems.size(), function.getSource(), false)) {
			return false;
		}
		if (!checkCount(targetItems.size(), function.getTarget(), true)) {
			return false;
		}
		
		//TODO other checks?
		
		return true;
	}

	/**
	 * Test if the given function definition matches the given selection 
	 * @param function the function definition
	 * @param selection the schema selection
	 * @return <code>true</code> if the definition matches the selection,
	 *   <code>false</code> otherwise 
	 */
	private boolean matchTypeFunction(TypeFunction function, SchemaSelection selection) {
		// check types
		Set<EntityDefinition> sourceItems = selection.getSourceItems();
		if (!checkType(sourceItems, TypeEntityDefinition.class)) {
			return false;
		}
		Set<EntityDefinition> targetItems = selection.getTargetItems();
		if (!checkType(targetItems, TypeEntityDefinition.class)) {
			return false;
		}
		
		// check counts
		if (!checkCount(sourceItems.size(), function.getSource(), true)) {
			return false;
		}
		if (!checkCount(targetItems.size(), function.getTarget(), false)) {
			return false;
		}
		
		//TODO other checks?
		
		return true;
	}

	/**
	 * Checks if each item is of the given type 
	 * @param items the items
	 * @param type the type
	 * @return <code>true</code> if all items are of the given type
	 */
	private static boolean checkType(Iterable<?> items,
			Class<?> type) {
		for (Object item : items) {
			if (!type.isAssignableFrom(item.getClass())) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the given entity count is compatible with the given set of 
	 * entity definitions
	 * @param count the entity count
	 * @param entities the entity definitions
	 * @param isTarget if the entities are target entities
	 * @return if then entity count is compatible with the definitions
	 */
	private static boolean checkCount(int count,
			Set<? extends AbstractParameter> entities,
			boolean isTarget) {
		int min = 0;
		int max = 0;
		
		for (AbstractParameter param : entities) {
			min += param.getMinOccurrence();
			if (max != AbstractParameter.UNBOUNDED) {
				int pMax = param.getMaxOccurrence();
				if (pMax == AbstractParameter.UNBOUNDED) {
					max = pMax;
				}
				else {
					max += pMax;
				}
			}
		}
		
		// check minimum
		if (count < min) {
			return false;
		}
		
		if (max == 0 && !isTarget) {
			// allow augmentations
			return true;
		}
		
		// check maximum
		if (max != AbstractParameter.UNBOUNDED && count > max) {
			return false;
		}
		
		return true;
	}

	/**
	 * Get the schema selection
	 * @return the schema selection to use
	 */
	public SchemaSelection getSelection() {
//		ISelectionService selectionService = PlatformUI.getWorkbench()
//			.getActiveWorkbenchWindow().getSelectionService();
//		ISelection selection = selectionService.getSelection();
		
//		if (selection instanceof SchemaSelection) {
//			return (SchemaSelection) selection;
//		}
//		else {
			return SchemaSelectionHelper.getSchemaSelection();
//		}
	}

}
