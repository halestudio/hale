/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.functions.geometric;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.cst.transformer.impl.NetworkExpansionTransformer;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.rcp.utils.SchemaSelectionHelper;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;

/**
 * A simplified Wizard for the configuration of the Network Expansion function,
 * which takes any MultiLineString and buffers it to a MultiPolygon.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NetworkExpansionFunctionWizard 
		extends Wizard 
		implements INewWizard, ISelectionListener {
	
	private static Logger _log = Logger.getLogger(NetworkExpansionFunctionWizard.class);
	
	NetworkExpansionFunctionWizardPage mainPage;
	
	/**
	 * Constructor
	 */
	public NetworkExpansionFunctionWizard() {
		super();
		this.mainPage = new NetworkExpansionFunctionWizardPage(
				"Configure Network Expansion"); 
		super.setWindowTitle("Configure Function"); 
		super.setNeedsProgressMonitor(true);
	}
	
	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		_log.debug("Wizard.canFinish: " + this.mainPage.isPageComplete());

		// Get InstanceService
		InstanceService is = 
			(InstanceService) PlatformUI.getWorkbench().getService(
					InstanceService.class);
		
		// get alignment service
		AlignmentService alignmentService = 
			(AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
		
		SchemaSelection selection = SchemaSelectionHelper.getSchemaSelection();
		
		// Create the cell
		Cell cell = new Cell();
		Entity entity1 = selection.getFirstSourceItem().getEntity();
		Entity entity2 = selection.getFirstTargetItem().getEntity();
		
		System.err.println("debug1");
		Transformation transformation = new Transformation();
		transformation.setLabel(NetworkExpansionTransformer.class.getName()); //FIXME
		transformation.getParameters().add(new Parameter("Expansion", "50"));
		System.err.println("debug1");
		entity1.setTransformation(transformation);
		System.err.println("debug1");

		cell.setEntity1(entity1);
		cell.setEntity2(entity2);
		alignmentService.addOrUpdateCell(cell);

		return this.mainPage.isPageComplete();
	}
	
	/**
	 * @see IWizard#addPages()
     */
    public void addPages() {
        super.addPages(); 
        addPage(this.mainPage);
    }

    /**
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	/**
	 * @see ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final Object selectionObject = ((IStructuredSelection) selection)
					.getFirstElement();
			if (selectionObject != null) {

				TreeItem treeItem = (TreeItem) selectionObject;
				String selectedFeatureType = treeItem.getText();
				System.out.println(selectedFeatureType);
			}
		}

	}

}
