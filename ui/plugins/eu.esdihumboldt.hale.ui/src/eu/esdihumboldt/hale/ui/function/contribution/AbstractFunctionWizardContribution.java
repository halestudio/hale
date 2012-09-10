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
package eu.esdihumboldt.hale.ui.function.contribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionExtension;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionExtension;
import eu.esdihumboldt.hale.ui.function.contribution.internal.AbstractWizardAction;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardExtension;
import eu.esdihumboldt.hale.ui.internal.Messages;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Contribution that provides access to function wizards
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractFunctionWizardContribution extends ContributionItem {

	private final boolean showAugmentations;

	/**
	 * Constructor
	 * 
	 * @param showAugmentations if augmentations shall be shown
	 */
	public AbstractFunctionWizardContribution(boolean showAugmentations) {
		this.showAugmentations = showAugmentations;
	}

	/**
	 * Create a function wizard contribution that doesn't hide augmentations.
	 */
	public AbstractFunctionWizardContribution() {
		this(true);
	}

	/**
	 * @see ContributionItem#fill(ToolBar, int)
	 */
	@Override
	public void fill(ToolBar parent, int index) {
		AlignmentService alignmentService = (AlignmentService) PlatformUI.getWorkbench()
				.getService(AlignmentService.class);

		for (FunctionWizardDescriptor<?> descriptor : getFunctionWizardDescriptors()) {
			IAction action = createWizardAction(descriptor, alignmentService);
			IContributionItem item = new ActionContributionItem(action);
			item.fill(parent, index++);
		}
	}

	/**
	 * Get the currently applicable function wizard descriptors
	 * 
	 * @return the function wizard descriptors
	 */
	protected Collection<FunctionWizardDescriptor<?>> getFunctionWizardDescriptors() {
		FunctionWizardExtension fwe = FunctionWizardExtension.getInstance();
		Collection<FunctionWizardDescriptor<?>> result = new ArrayList<FunctionWizardDescriptor<?>>();

		// add wizards for type functions
		for (TypeFunction function : TypeFunctionExtension.getInstance().getElements()) {
			result.add(fwe.getWizardDescriptor(function.getId()));
		}

		// add wizards for property functions
		for (PropertyFunction function : PropertyFunctionExtension.getInstance().getElements()) {
			result.add(fwe.getWizardDescriptor(function.getId()));
		}

		return result;

//		return FunctionWizardExtension.getInstance().getFactories();
	}

	/**
	 * Create a wizard action for the given function wizard descriptor
	 * 
	 * @param descriptor the function wizard descriptor
	 * @param alignmentService the alignment service
	 * @return the action that launches the wizard
	 */
	protected abstract AbstractWizardAction<?> createWizardAction(
			FunctionWizardDescriptor<?> descriptor, AlignmentService alignmentService);

	/**
	 * @see ContributionItem#fill(Menu, int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		boolean added = false;

		AlignmentService alignmentService = (AlignmentService) PlatformUI.getWorkbench()
				.getService(AlignmentService.class);

		List<AbstractWizardAction<?>> augmentationActions = new ArrayList<AbstractWizardAction<?>>();

		for (FunctionWizardDescriptor<?> descriptor : getFunctionWizardDescriptors()) {
			if (!descriptor.getFunction().isAugmentation() || showAugmentations) {
				AbstractWizardAction<?> action = createWizardAction(descriptor, alignmentService);
				if (action.isActive()) {
					if (descriptor.getFunction().isAugmentation()) {
						augmentationActions.add(action);
					}
					else {
						IContributionItem item = new ActionContributionItem(action);
						item.fill(menu, index++);
						added = true;
					}
				}
			}
		}

		if (!augmentationActions.isEmpty()) {
			if (added) {
				new Separator().fill(menu, index++);
			}

			// get augmentation target name
//			ISelection selection = selectionService.getSelection();
//			AlignmentInfo info = null;
//			
//			if (selection instanceof DefaultSchemaSelection) {
//				SchemaSelection schemaSelection = (SchemaSelection) selection;
//				info = new SchemaSelectionInfo(schemaSelection, alignmentService);
//			}
//			else if (selection instanceof CellSelection) {
//				CellSelection cellSelection = (CellSelection) selection;
//				info = new CellSelectionInfo(cellSelection);
//			}
//			
			String augmentations;
//			if (info != null && info.getTargetItemCount() == 1) {
//				augmentations = MessageFormat.format(Messages.FunctionWizardContribution_0, info.getFirstTargetItem().getName().getLocalPart()); 
//			}
//			else {
			augmentations = Messages.FunctionWizardContribution_1;
//			}
//			
			MenuItem augItem = new MenuItem(menu, SWT.PUSH, index++);
			augItem.setText(augmentations);
			augItem.setEnabled(false);

			new Separator().fill(menu, index++);

			for (AbstractWizardAction<?> action : augmentationActions) {
				IContributionItem item = new ActionContributionItem(action);
				item.fill(menu, index++);
				added = true;
			}
		}

		if (!added) {
			MenuItem item = new MenuItem(menu, SWT.PUSH, index++);
			item.setText(Messages.FunctionWizardContribution_2); //$NON-NLS-1$
			item.setEnabled(false);
		}
	}

	/**
	 * @see ContributionItem#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}

	/**
	 * Determine if a function wizard is active for the current selection
	 * 
	 * @param descriptor the function wizard descriptor
	 * @return if the function wizard is active for the current selection
	 */
	public abstract boolean isActive(FunctionWizardDescriptor<?> descriptor);

	/**
	 * Determines if there are any active function wizards for the current
	 * selection
	 * 
	 * @return if there are any active function wizards for the current
	 *         selection
	 */
	public boolean hasActiveFunctions() {
		for (FunctionWizardDescriptor<?> descriptor : getFunctionWizardDescriptors()) {
			if (isActive(descriptor)) {
				return true;
			}
		}

		return false;
	}

}
