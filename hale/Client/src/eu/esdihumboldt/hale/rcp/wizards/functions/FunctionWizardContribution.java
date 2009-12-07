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
package eu.esdihumboldt.hale.rcp.wizards.functions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.rcp.utils.SchemaSelectionHelper;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelection;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;

/**
 * Contributon that provides access to function wizards
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class FunctionWizardContribution extends ContributionItem {

	/**
	 * Action for creating function wizards
	 */
	public class WizardAction extends Action implements ISelectionListener {
		
		private final AlignmentService alignmentService;
		
		private final FunctionWizardDescriptor descriptor;

		/**
		 * Constructor
		 * 
		 * @param descriptor the function wizard descriptor
		 * @param selectionService the selection service
		 * @param alignmentService the alignment service
		 */
		public WizardAction(FunctionWizardDescriptor descriptor,
				ISelectionService selectionService,
				AlignmentService alignmentService) {
			super(descriptor.getName(), IAction.AS_PUSH_BUTTON);
			
			this.descriptor = descriptor;
			this.alignmentService = alignmentService;
			
			setImageDescriptor(descriptor.getIcon());
			
			selectionService.addSelectionListener(this);
			
			updateState();
		}

		/**
		 * @see Action#run()
		 */
		@Override
		public void run() {
			ISelection selection = getSelection();
			
			if (selection.isEmpty()) return;
			
			FunctionWizard wizard = null;
			AlignmentInfo info = null;
			
			if (selection instanceof SchemaSelection) {
				SchemaSelection schemaSelection = (SchemaSelection) selection;
				info = new SchemaSelectionInfo(schemaSelection, alignmentService);
			}
			else if (selection instanceof CellSelection) {
				CellSelection cellSelection = (CellSelection) selection;
				info = new CellSelectionInfo(cellSelection);
			}
			
			if (info != null && descriptor.supports(info)) {
				wizard = descriptor.createWizard(info);
			}
			
			if (wizard != null) {
				WizardDialog dialog = new WizardDialog(
						Display.getCurrent().getActiveShell(),
						wizard);
				
				if (dialog.open() == WizardDialog.OK) {
					for (ICell cell : wizard.getResult()) {
						alignmentService.addOrUpdateCell(cell);
					}
				}
			}
		}

		/**
		 * @see ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
		 */
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			updateState();
		}

		/**
		 * Update the action state
		 */
		private void updateState() {
			setEnabled(isActive());
		}
		
		/**
		 * Get if the wizard action shall be currently active
		 * 
		 * @return if the wizard action shall be currently active
		 */
		public boolean isActive() {
			return FunctionWizardContribution.this.isActive(descriptor);
		}

	}

	private final boolean showAugmentations;
	
	/**
	 * Constructor
	 * 
	 * @param showAugmentations if augmentations shall be shown
	 */
	public FunctionWizardContribution(boolean showAugmentations) {
		this.showAugmentations = showAugmentations;
	}
	
	/**
	 * @see ContributionItem#fill(ToolBar, int)
	 */
	/*@Override
	public void fill(ToolBar parent, int index) {
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		AlignmentService alignmentService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		
		for (FunctionWizardDescriptor descriptor : FunctionWizardExtension.getFunctionWizards()) {
			IAction action = new WizardAction(descriptor, selectionService,
					alignmentService);
			IContributionItem item = new ActionContributionItem(action);
			item.fill(parent, index++);
		}
	}*/

	/**
	 * @see ContributionItem#fill(Menu, int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		boolean added = false;
		
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		AlignmentService alignmentService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		
		List<WizardAction> augmentationActions = new ArrayList<WizardAction>();
		
		for (FunctionWizardDescriptor descriptor : FunctionWizardExtension.getFunctionWizards()) {
			if (!descriptor.isAugmentation() || showAugmentations) {
				WizardAction action = new WizardAction(descriptor, selectionService,
						alignmentService);
				if (action.isActive()) {
					if (descriptor.isAugmentation()) {
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
			ISelection selection = selectionService.getSelection();
			AlignmentInfo info = null;
			
			if (selection instanceof SchemaSelection) {
				SchemaSelection schemaSelection = (SchemaSelection) selection;
				info = new SchemaSelectionInfo(schemaSelection, alignmentService);
			}
			else if (selection instanceof CellSelection) {
				CellSelection cellSelection = (CellSelection) selection;
				info = new CellSelectionInfo(cellSelection);
			}
			
			String augmentations = "Augmentations";
			if (info != null && info.getTargetItemCount() == 1) {
				augmentations += " for " + info.getFirstTargetItem().getName().getLocalPart();
			}
			
			MenuItem augItem = new MenuItem(menu, SWT.PUSH, index++);
			augItem.setText(augmentations);
			augItem.setEnabled(false);
			
			//new Separator().fill(menu, index++);
			
			for (WizardAction action : augmentationActions) {
				IContributionItem item = new ActionContributionItem(action);
				item.fill(menu, index++);
				added = true;
			}
		}
		
		if (!added) {
			MenuItem item = new MenuItem(menu, SWT.PUSH, index++);
			item.setText("No function available");
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
	 * Get the selection to use
	 * 
	 * @return the selection to use
	 */
	protected ISelection getSelection() {
		ISelectionService selectionService = PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		
		if (selection instanceof SchemaSelection ||
				selection instanceof CellSelection) {
			return selection;
		}
		else {
			return SchemaSelectionHelper.getSchemaSelection();
		}
	}
	
	/**
	 * Determine if a function wizard is active for the current
	 *   selection
	 *   
	 * @param descriptor the function wizard descriptor
	 * @return if the function wizard is active for the current
	 *   selection
	 */
	protected boolean isActive(FunctionWizardDescriptor descriptor) {
		AlignmentService alignmentService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		ISelection selection = getSelection();
		
		if (selection == null || selection.isEmpty()) return false;
		
		boolean enabled = false;
		
		if (selection instanceof SchemaSelection) {
			SchemaSelection schemaSelection = (SchemaSelection) selection;
			if (descriptor.supports(new SchemaSelectionInfo(schemaSelection, alignmentService))) {
				enabled = true;
			}
		}
		else if (selection instanceof CellSelection) {
			CellSelection cellSelection = (CellSelection) selection;
			if (descriptor.supports(new CellSelectionInfo(cellSelection))) {
				enabled = true;
			}
		}
		
		return enabled;
	}
	
	/**
	 * Determines if there are any active function wizards for the
	 *   current selection
	 *   
	 * @return if there are any active function wizards for the
	 *   current selection
	 */
	public boolean hasActiveFunctions() {
		for (FunctionWizardDescriptor descriptor : FunctionWizardExtension.getFunctionWizards()) {
			if (isActive(descriptor)) {
				return true;
			}
		}
		
		return false;
	}

}
