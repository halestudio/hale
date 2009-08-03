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
package eu.esdihumboldt.hale.rcp.wizards.functions.literal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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

import sun.util.logging.resources.logging;


import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.rcp.views.model.AttributeView;
//import eu.esdihumboldt.transformers.cst.RenameTransformer;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;

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
		String typeNameSource = mainPage.getSourceFeatureTypeName().getText();
		String typeNameTarget =  mainPage.getTargetFeatureTypeName().getText();
		_log.debug("Source Feature Type: " + typeNameSource );
		_log.debug("Target Feature Type: " +typeNameTarget);
		
		
		 
		//get service
		SchemaService service = (SchemaService)ModelNavigationView.site.getService(SchemaService.class);
		FeatureType ft_source = service.getFeatureTypeByName(typeNameSource);
		FeatureType ft_target = service.getFeatureTypeByName(typeNameTarget);
		
		
		//get URI and local name
		List<String> nameparts = new ArrayList<String>(); 
		nameparts.add(ft_source.getName().getNamespaceURI());
		nameparts.add(ft_source.getName().getLocalPart());


		
		//evtl. move to performFinish
		Cell c = new Cell();
		FeatureClass entity1 = new FeatureClass(nameparts);
		Transformation t = new Transformation();
		t.setLabel("Rename");
		List parameters = new ArrayList<IParameter>();
//		parameters.add(new Param("SourceFeatureType", ft_source.getName().toString()));
//		parameters.add(new Param("TargetFeatureType", ft_target.getName().toString()));
		entity1.setTransformation(t); 
		c.setEntity1(entity1);
		
		List<String> nameparts_2 = new ArrayList<String>(); 
		nameparts_2.add(ft_target.getName().getNamespaceURI());
		nameparts_2.add(ft_target.getName().getLocalPart());
		FeatureClass entity2 = new FeatureClass(nameparts_2); 
		c.setEntity2(entity2);
		AlignmentService alservice = (AlignmentService)ModelNavigationView.site.getService(AlignmentService.class);
		//store transformation in AS
		alservice.addOrUpdateCell(c);

		
		
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
		//check if entity1 filtered before renaming
		String alignment = "";
		ICell filterCell = alservice.getCell(entity1, entity1);
		//TODO add enumeration for the alignment types.
		if (filterCell!=null) alignment = "Filter, Rename";
		else alignment = "Rename";
		AttributeView attributeView = getAttributeView();
		Label alignmentLabel = attributeView.getAlLabel();
		alignmentLabel.setImage(attributeView.drawAlignmentImage(alignment));
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
