/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : hale
 * 	 
 * Classname    : eu.esdihumboldt.hale.rcp.wizards.functions.generic.GenericFunctionWizard.java 
 * 
 * Author       : Josef Bezdek
 * 
 * Created on   : Jan, 2010
 *
 */

package eu.esdihumboldt.hale.rcp.wizards.functions.generic;

import java.util.Iterator;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.generic.model.AlgorithmCST;

public class GenericFunctionWizard extends AbstractSingleCellWizard {
	
	protected GenericFunctionWizardPage mainPage;
	protected AlgorithmWizardPage algorithmPage;
	protected AlgorithmCST algorithmModel = null;
	protected int maximumParameters = 0;	//maximum input algorithm parameters in one algorithm
	protected boolean parametersCompleted = false;

	
	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(AlignmentInfo)
	 */
	public GenericFunctionWizard(AlignmentInfo selection) {
		super(selection);
	}

	/**
	 * @see AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {
		this.mainPage = new GenericFunctionWizardPage(""); 
		super.setWindowTitle("Generic Wizard"); 
		super.setNeedsProgressMonitor(true);
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ICell cell = getResultCell();
		Entity entity1 = (Entity) cell.getEntity1();
		Transformation transformation = new Transformation();
		Iterator <String> iter = algorithmModel.parameters.keySet().iterator();
		transformation.setService(new Resource(algorithmModel.getFunctionID().getAuthority()));
		// add all active parameters
		for (int i=0; i<algorithmModel.numberOfParameters; i++){
			if (iter.hasNext()){
				transformation.getParameters().add(
					new Parameter(iter.next().toString(), algorithmPage.params[i].getText()));
			}	
		}
		entity1.setTransformation(transformation);
		return true;
	}
	

	/**
	 * @see Wizard#canFinish()
	 */
	@Override
	public boolean canFinish(){
		// cannot complete the wizard from the first page
		if ((this.getContainer().getCurrentPage() == algorithmPage) && (parametersCompleted)) 
			return true;
		return false;
	}
	
	/**
	 * @see IWizard#addPages()
     */
    public void addPages() {
       // super.addPages(); 
        addPage(this.mainPage);
		algorithmPage = new AlgorithmWizardPage("Algorithm");
		addPage(this.algorithmPage);
		
    }


}
