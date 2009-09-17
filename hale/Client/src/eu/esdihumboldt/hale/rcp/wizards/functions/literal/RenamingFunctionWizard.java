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

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.cst.transformer.impl.RenameAttributeTransformer;
import eu.esdihumboldt.cst.transformer.impl.RenameFeatureTransformer;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.rcp.utils.SchemaSelectionHelper;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;
import eu.esdihumboldt.hale.rcp.views.model.attribute.AttributeView;

/**
 * This {@link Wizard} is used to invoke a Renaming Transformer for the Source
 * Feature Type
 * 
 * @author Anna Pitaev, Logica; Simon Templer, Fraunhofer IGD
 * @version $Id$
 */
public class RenamingFunctionWizard extends Wizard implements INewWizard {

	private static Logger _log = Logger.getLogger(RenamingFunctionWizard.class);

	RenamingFunctionWizardMainPage mainPage;

	/**
	 * constructor
	 */
	public RenamingFunctionWizard() {
		super();
		this.mainPage = new RenamingFunctionWizardMainPage(
				"Configure Feature Type Renaming Function",
				"Configure Feature Type Renaming Function");
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
		SchemaSelection selection = SchemaSelectionHelper.getSchemaSelection();
		
		SchemaItem source = selection.getFirstSourceItem();
		SchemaItem target = selection.getFirstTargetItem();
		
		if (source == null || target == null) {
			return false;
		}
		
		Cell c = new Cell();
		Transformation t = new Transformation();
		
		if (source.isFeatureType() && target.isFeatureType()) {
			// Type renaming
			t.setLabel(RenameFeatureTransformer.class.getName());
			
			//TODO any parameters needed?
		}
		else if (source.isAttribute() && target.isAttribute()) {
			// Attribute renaming
			t.setLabel(RenameAttributeTransformer.class.getName());
			
			//Add old attribute name
			t.getParameters().add(new Parameter(
					RenameAttributeTransformer.OLD_ATTRIBUTE_NAME_PARAMETER, 
					source.getName().getLocalPart()));
			t.getParameters().add(new Parameter(
					RenameAttributeTransformer.NEW_ATTRIBUTE_NAME_PARAMETER, 
					target.getName().getLocalPart()));
		}
		else {
			//TODO error message?
			return false;
		}

		Entity entity1 = source.getEntity();
		Entity entity2 = target.getEntity();
		
		entity1.setTransformation(t);
		c.setEntity1(entity1);
		c.setEntity2(entity2);
		
		AlignmentService alservice = (AlignmentService) PlatformUI
				.getWorkbench().getService(AlignmentService.class);
		// store transformation in AS
		alservice.addOrUpdateCell(c);

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		_log.debug("in init..");
		/*
		 * IWorkbenchWindow activeWindow =
		 * PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		 * System.out.println(activeWindow.getClass().getName());
		 * StructuredSelection structuredSelection = (StructuredSelection)
		 * activeWindow.getSelectionService().getSelection(); //selection =
		 * (IStructuredSelection)
		 * workbench.getWorkbenchWindows()[0].getSelectionService
		 * ().getSelection(); System.out.println(structuredSelection.size());
		 * 
		 * AttributeView attributeView = null; // get All Views IViewReference[]
		 * views =
		 * PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage
		 * ().getViewReferences(); // get AttributeView for (int count = 0;
		 * count < views.length; count++) { if (views[count].getId().equals(
		 * "eu.esdihumboldt.hale.rcp.views.model.AttributeView")) {
		 * attributeView = (AttributeView) views[count].getView(false); } }
		 * TableViewer targetAttributeViewer =
		 * attributeView.getTargetAttributeViewer(); TableViewer
		 * sourceAtttibuteViewer = attributeView.getSourceAttributeViewer();
		 * Table sourceAttributeList = sourceAtttibuteViewer.getTable();
		 * _log.debug("Source attribute list:"); for (TableItem targetItem :
		 * sourceAttributeList.getSelection()){
		 * _log.debug(targetItem.getText()); } Table targetAttributeList =
		 * targetAttributeViewer.getTable();
		 * _log.debug("Target attribute list"); for (TableItem targetItem :
		 * targetAttributeList.getSelection()){
		 * _log.debug(targetItem.getText());
		 */
	}

	/**
	 * @see IWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		addPage(mainPage);
	}

	protected AttributeView getAttributeView() {
		AttributeView attributeView = null;
		// get All Views
		IViewReference[] views = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViewReferences();
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
