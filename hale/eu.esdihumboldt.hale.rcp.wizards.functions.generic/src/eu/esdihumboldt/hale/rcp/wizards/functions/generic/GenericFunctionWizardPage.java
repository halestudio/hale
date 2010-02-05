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
 * Classname    : eu.esdihumboldt.hale.rcp.wizards.functions.generic.GenericFunctionWizardPage.java 
 * 
 * Author       : Josef Bezdek
 * 
 * Created on   : Jan, 2010
 *
 */

package eu.esdihumboldt.hale.rcp.wizards.functions.generic;

import java.util.Iterator;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.transformer.CstService;
import eu.esdihumboldt.cst.transformer.capabilities.CstServiceCapabilities;
import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;
import eu.esdihumboldt.hale.rcp.wizards.functions.generic.model.AlgorithmCST;
import eu.esdihumboldt.hale.rcp.wizards.functions.generic.model.FunctionType;
import eu.esdihumboldt.hale.rcp.wizards.functions.generic.model.Model;

public class GenericFunctionWizardPage extends AbstractSingleCellWizardPage {

	FunctionTypeLabelProvider labelProvider;
	TreeViewer tree;
	
	/**
	 * constructor
	 * @param pageName
	 */
	protected GenericFunctionWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
		this.setPageComplete(this.isPageComplete());
        
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());
        composite.setLayout(new FillLayout());
        labelProvider = new FunctionTypeLabelProvider();
        tree = new TreeViewer(composite, SWT.SINGLE);
        tree.setContentProvider(new FunctionTypeContentProvider());
        tree.setLabelProvider(labelProvider);
       	tree.setInput(this.getInitalInput());        
          	
    	tree.addSelectionChangedListener(new ISelectionChangedListener() {
    			public void selectionChanged(SelectionChangedEvent event) {
     				if(event.getSelection().isEmpty()) {
    					return;
    				}
    				if(event.getSelection() instanceof IStructuredSelection) {
    					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
    					for (Iterator <IStructuredSelection> iterator =  selection.iterator(); iterator.hasNext();) {
    						Model domain = (Model) iterator.next();
    						if (domain.getClass() == AlgorithmCST.class){
    							((GenericFunctionWizard)getWizard()).algorithmModel = (AlgorithmCST) domain;
    							((GenericFunctionWizard)getWizard()).algorithmPage.algorithmIsSelected = true;
    							((GenericFunctionWizard)getWizard()).algorithmPage.setPageParameters();
    							((GenericFunctionWizard)getWizard()).algorithmPage.setTitle("Algorithm: "+((GenericFunctionWizard)getWizard()).algorithmModel.getName().toUpperCase());
        						
    						}
    						else{
    							((GenericFunctionWizard)getWizard()).algorithmModel = null;
    							
        							
    						}
    						getWizard().getContainer().updateButtons();
    						
    					}
    					
    				}
    			}
    		});
        setErrorMessage(null);	// should not initially have error message
		super.setControl(composite);

	}
	
	/**
	 * @see IWizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage()	{
		if 	(((GenericFunctionWizard)getWizard()).algorithmModel != null){
			return true;
		}
		return false;
	}
	
	/**
	 * Method that fill treeViewer
	 * @return function type root
	 */
	public FunctionType getInitalInput() {
		int countAlgorithm = 0;   
		FunctionType root = new FunctionType();
		
		FunctionType core = new FunctionType("Core functions");
		FunctionType inspire = new FunctionType("Inspire functions");
		FunctionType others = new FunctionType("Other functions"); 
		root.addBox(core);
		root.addBox(inspire);
		root.addBox(others);
	
/*		
		CstService ts = (CstService) 
		PlatformUI.getWorkbench().getService(
				CstService.class);
	   CstServiceCapabilities tCapabilities = ts.getCapabilities();
	   
	    for (Iterator <FunctionDescription> iter = tCapabilities.getFunctionDescriptions().iterator(); iter.hasNext();){
			FunctionDescription funcDescr = (FunctionDescription) iter.next();
			System.out.println("-----");
			System.out.println(funcDescr.toString());
			System.out.println(funcDescr.getFunctionId());
			System.out.println(funcDescr.getParameterConfiguration());
	    }	
		*/
	
		CstFunctionFactory.getInstance().registerCstPackage(
		"eu.esdihumboldt.cst.corefunctions");
		CstFunction f = null;

		for (Iterator<String> i = CstFunctionFactory.getInstance()
				.getRegisteredFunctions().keySet().iterator(); i.hasNext();) {

			try {
				f = CstFunctionFactory.getInstance().getRegisteredFunctions()
					.get(i.next()).newInstance();
				//System.out.println("********"+f.getClass().toString());
			} catch (Exception e) {
				f = null;
			}
			
			if (f != null){
				AlgorithmCST alg = null;
				try{
					alg = new AlgorithmCST(getAlgorithmName(f.getClass().getSimpleName()), f.getClass().getCanonicalName(), f.getParameters());
				
					
				}
				catch (NullPointerException e){
					alg = new AlgorithmCST(getAlgorithmName(f.getClass().getSimpleName()), f.getClass().getCanonicalName(), null);
				}

				String functionGroup = f.getClass().getName().toString().substring(0, f.getClass().getName().toString().lastIndexOf('.'));
				functionGroup = functionGroup.substring(functionGroup.lastIndexOf('.')+1);
				//System.out.println(functionGroup);
		
				if (functionGroup.equals("corefunctions")){
					core.addCoreFunction(alg);
				}
				else{
					if (functionGroup.equals("inspire"))
						inspire.addInspireFunction(alg);
					else
						others.addOthersFunction(alg);
				}
			
				countAlgorithm++;
				setMaximumParameters(alg);
			}	
		}
		setTitle("Toolbox contains "+countAlgorithm+" algorithms");
		return root;
	}
	

	
	/**
	 * Method that sets maximum of algorithm parameters
	 * @param alg selected algorithm in treeViewer
	 */
	private void setMaximumParameters(AlgorithmCST alg){
		if (((GenericFunctionWizard)getWizard()).maximumParameters < alg.numberOfParameters)
			((GenericFunctionWizard)getWizard()).maximumParameters = alg.numberOfParameters;
	}
	
	/**
	 * Method creates algorithm name from class url
	 * @param url url of selected algorithm
	 * @return name of algorithm
	 */
	private String getAlgorithmName(String className){
		String s = className.toString();
		s = s.substring(s.lastIndexOf('.')+1);
		String ss = s.toLowerCase();
		String name = "";
		int index = 0;
		for (int i=1; i<ss.length(); i++){
			if (s.charAt(i)!=ss.charAt(i)){
				name += s.substring(index, i) + ' ';
				index = i;
			}
		}
		return name;
	}
	

}
