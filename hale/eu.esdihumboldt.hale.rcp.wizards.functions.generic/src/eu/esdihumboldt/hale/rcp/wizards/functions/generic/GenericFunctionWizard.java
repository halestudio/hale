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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.generic.model.AlgorithmCST;

public class GenericFunctionWizard extends Wizard implements FunctionWizard {
	
	protected GenericFunctionWizardPage mainPage;
	protected AlgorithmWizardPage algorithmPage;
	protected AlgorithmCST algorithmModel = null;
	protected int maximumParameters = 0;	//maximum input algorithm parameters in one algorithm
	protected boolean parametersCompleted = false;
	private final Cell cell;
	private final SchemaItem sourceItem;
	private final SchemaItem targetItem;
	private boolean existMapping = false;
	
	/**
	 * Constructor solve both variant
	 * 1. sourceItem isn't selected
	 * 2. sourceItem is selected
	 */
	public GenericFunctionWizard(ICell augmentation, SchemaItem sourceItem, SchemaItem targetItem) {
		super();
		this.sourceItem = sourceItem;
		this.targetItem = targetItem;
		if (augmentation == null) {
			cell = new Cell();
			if (sourceItem == null)
				cell.setEntity1(Entity.NULL_ENTITY);
			else 
				cell.setEntity1(sourceItem.getEntity());
			
			cell.setEntity2(targetItem.getEntity());
			
		}
		else {
			existMapping =  true;
			// copy the cell
			cell = new Cell();
			cell.setEntity1(augmentation.getEntity1());
			cell.setEntity2(augmentation.getEntity2());
			cell.setAbout(augmentation.getAbout());
			cell.setLabel(augmentation.getLabel());
			cell.setMeasure(augmentation.getMeasure());
			cell.setRelation(augmentation.getRelation());
		}
		
		init();
	}

	/**
	 * @see Wizard#init()
	 */
	//@Override
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
		Entity entity = null;
		
		if (algorithmModel.transformationOnEntity1)
			// transformation is on entity1
			entity = (Entity) cell.getEntity1(); 
		else
			// transformation is on entity2
			entity = (Entity) cell.getEntity2();
		Transformation transformation = new Transformation();
		transformation.setService(new Resource(algorithmModel.getFunctionID()));
		
		if (algorithmModel.parameters != null){
			Iterator <IParameter> iter = algorithmModel.parameters.iterator();
			// add all active parameters
			for (int i=0; i<algorithmModel.numberOfParameters; i++){
				if (iter.hasNext()){
					transformation.getParameters().add(
							new Parameter(iter.next().getName(), algorithmPage.params[i].getText()));
				}	
			}
		}	
		entity.setTransformation(transformation);
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

	/**
	 * Get the cell to edit
	 * 
	 * @return the cell to edit
	 */
	public final Cell getResultCell() {
		return cell;
	}
	
	/**
	 * @see FunctionWizard#getResult()
	 */
	@Override
	public List<ICell> getResult() {
		List<ICell> result = new ArrayList<ICell>();
		result.add(getResultCell());
		return result;
	}

	/**
	 * The method return current surceItem
	 */
	protected SchemaItem getSourceItem(){
		return sourceItem;
	}

	/**
	 * The method return current targetItem
	 */
	protected SchemaItem getTargetItem(){
		return targetItem;
	}

	/**
	 * The method return if exist mapping for current selection
	 */
	protected boolean existMapping(){
		return existMapping;
	}
}

