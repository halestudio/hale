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
package eu.esdihumboldt.hale.rcp.utils.cst;

import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.cst.transformer.CstService;
import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;

/**
 * Dialog showing the functions registered with the CST
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class ListFunctionsDialog extends TitleAreaDialog {
	
	/**
	 * Label provider for function descriptions
	 */
	public static class FunctionDescriptionLabels extends LabelProvider {

		/**
		 * @see LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			FunctionDescription function = (FunctionDescription) element;
			
			return function.getFunctionId().toString();
		}

	}

	/**
	 * Constructor
	 * 
	 * @param parentShell
	 */
	public ListFunctionsDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * @see TitleAreaDialog#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		
		setTitle("Functions that are currently registered with the CST");
		//setMessage("");
		
		return control;
	}

	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setText("Available functions");
	}

	/**
	 * @see TitleAreaDialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		page.setLayoutData(data);
		
		page.setLayout(new GridLayout(1, false));
		
		CstService cst = (CstService) PlatformUI.getWorkbench().getService(CstService.class);
		List<FunctionDescription> functions = cst.getCapabilities().getFunctionDescriptions();
		
		ListViewer list = new ListViewer(page);
		list.setContentProvider(new ArrayContentProvider());
		list.setLabelProvider(new FunctionDescriptionLabels());
		list.setInput(functions);
		
		list.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		return page;
	}


}
