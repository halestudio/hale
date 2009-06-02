package eu.esdihumboldt.hale.rcp.wizards.functions.geometric;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.rcp.views.model.AttributeView;

public class NetworkExpansionFunctionWizard extends Wizard 
implements INewWizard {

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		/*IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		System.out.println(activeWindow.getClass().getName());
		StructuredSelection structuredSelection = (StructuredSelection) activeWindow.getSelectionService().getSelection();
		//selection = (IStructuredSelection) workbench.getWorkbenchWindows()[0].getSelectionService().getSelection();
		System.out.println(structuredSelection.size());
*/
		AttributeView attributeView = null;
		// get All Views
		IViewReference[] views = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		// get AttributeView
		for (int count = 0; count < views.length; count++) {
			if (views[count].getId().equals(
					"eu.esdihumboldt.hale.rcp.views.model.AttributeView")) {
				attributeView = (AttributeView) views[count].getView(false);
					}
		}
		TableViewer targetAttributeViewer = attributeView.getTargetAttributeViewer();
		TableViewer sourceAtttibuteViewer = attributeView.getSourceAttributeViewer();
		Table sourceAttributeList = sourceAtttibuteViewer.getTable();
		System.out.println("Source attribute list");
		for (TableItem targetItem : sourceAttributeList.getSelection()){
			
			System.out.println(targetItem.getText());
		}
		Table targetAttributeList = targetAttributeViewer.getTable();
		System.out.println("Target attribute list");
		for (TableItem targetItem : targetAttributeList.getSelection()){
			System.out.println(targetItem.getText());
			
		}
	}

}
