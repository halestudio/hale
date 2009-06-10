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
package eu.esdihumboldt.rcp.wizards.functions.literal;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.type.FeatureType;


import eu.esdihumboldt.hale.rcp.views.model.AttributeView;
//import eu.esdihumboldt.transformers.cst.RenameTransformer;

/**
 * This {@link Wizard} is used to invoke a Renaming Transformer for the Source Feature Type
 * 
 * @author Anna Pitaev, Logica
 * @version $Id$
 */
public class RenamingFunctionWizard extends Wizard 
implements INewWizard {
	
private static Logger _log = Logger.getLogger(RenamingFunctionWizard.class);
	
	RenamingFunctionWizardMainPage mainPage;
	
	/**
	 * constructor
	 */
	public RenamingFunctionWizard(){
		super();
		this.mainPage = new RenamingFunctionWizardMainPage(
				"Configure Feature Type Renaming Function", "Configure Feature Type Renaming Function"); 
		super.setWindowTitle("Configure Function"); 
		super.setNeedsProgressMonitor(true);
		
	}

	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		_log.debug("Wizard.canFinish: " + this.mainPage.isPageComplete());
		return this.mainPage.isPageComplete();
	}


	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	
	@Override
	public boolean performFinish() {
		//TODO replace syouts with _log
		System.out.println("Source Feature Type: " + mainPage.getSourceFeatureTypeName().getText());
		System.out.println("Target Feature Type: " + mainPage.getTargetFeatureTypeName().getText());
		
		//System.out.println(mainPage.getTargetFeatureTypeName().getText());
		/*FeatureType ft = mainPage.getSourceFeatureType();
		String newname = mainPage.getTargetFeatureTypeName().getText();
		RenameTransformer rt = new RenameTransformer(newname);
		FeatureType targetft = rt.getTargetType(ft, null);*/
		//TODO update ModelNavigationView by highliting of the featuretypes.
		//System.out.println("Transformed Feature Type: " + targetft.getName());
		System.out.println("Transformation finished");
		
		//highlight a selection
		Color color = new Color(Display.getDefault(), 135, 190, 100);
		mainPage.getSourceViewer().getTree().getSelection()[0].setBackground(0, color);
		//mainPage.getSourceViewer().getTree().deselectAll();
		mainPage.getSourceViewer().getControl().redraw();
		mainPage.getTargetViewer().getTree().getSelection()[0].setBackground(0, color);
		//mainPage.getTargetViewer().getTree().deselectAll();
		mainPage.getTargetViewer().getControl().redraw();
		//update aligment image
		AttributeView attributeView = getAttributeView();
		Label alignmentLabel = attributeView.getAlLabel();
		alignmentLabel.setImage(attributeView.drawAlignmentImage("Renaming"));
		alignmentLabel.redraw();
		
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		_log.debug("in init..");
		/*IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		System.out.println(activeWindow.getClass().getName());
		StructuredSelection structuredSelection = (StructuredSelection) activeWindow.getSelectionService().getSelection();
		//selection = (IStructuredSelection) workbench.getWorkbenchWindows()[0].getSelectionService().getSelection();
		System.out.println(structuredSelection.size());

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
		_log.debug("Source attribute list:");
		for (TableItem targetItem : sourceAttributeList.getSelection()){
			_log.debug(targetItem.getText());
		}
		Table targetAttributeList = targetAttributeViewer.getTable();
		_log.debug("Target attribute list");
		for (TableItem targetItem : targetAttributeList.getSelection()){
			_log.debug(targetItem.getText());
			*/
		}
	
	
	 /*
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        super.addPages(); 
        addPage(mainPage);        
    }
    protected AttributeView getAttributeView() {
		AttributeView attributeView = null;
		// get All Views
		IViewReference[] views = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		// get AttributeView
		// get AttributeView
		for (int count = 0; count < views.length; count++) {
			if (views[count].getId().equals(
					"eu.esdihumboldt.hale.rcp.views.model.AttributeView")) {
				attributeView = (AttributeView) views[count].getView(false);
			}
			
		}
		return attributeView;
	}

}
