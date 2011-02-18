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
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
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
	
	public static class FunctionDescriptionFunctions extends ColumnLabelProvider {

		/**
		 * @see LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			FunctionDescription function = (FunctionDescription) element;
			
			return function.getFunctionId().toString();
		}

	}
	
	public static class FunctionDescriptionDescriptions extends ColumnLabelProvider {

		/**
		 * @see LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			FunctionDescription function = (FunctionDescription) element;
			String descr = "";
			if (function.getFunctionDescription()!=null)
				descr = function.getFunctionDescription().toString().trim();
			return descr;
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
		
		page.setLayout(new GridLayout(2, false));
		
		CstService cst = (CstService) PlatformUI.getWorkbench().getService(CstService.class);
		List<FunctionDescription> functions = cst.getCapabilities().getFunctionDescriptions();
	
		final TableViewer tablev = new TableViewer(page,SWT.FULL_SELECTION);
		tablev.getTable().setCapture(false);		
		tablev.setContentProvider(new ArrayContentProvider());
		
		// 1st Column
		
		TableViewerColumn vColumn1 = new TableViewerColumn(tablev,SWT.WRAP);
		TableColumn column = vColumn1.getColumn();
		column.setText("Function");
		column.setResizable(false);
		vColumn1.setLabelProvider(new FunctionDescriptionFunctions());		
		// 2nd Column
		TableViewerColumn vColumn2 = new TableViewerColumn(tablev,SWT.WRAP);
		TableColumn column2 = vColumn2.getColumn();
		column2.setText("Description");
		column2.setResizable(false);
		vColumn2.setLabelProvider(new FunctionDescriptionDescriptions());		
		tablev.setInput(functions);
		tablev.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tablev.getTable().setHeaderVisible(true);
		
		column.pack();
		column2.pack();
		
		for (int i=0;i<tablev.getTable().getItemCount();i++){
			TableItem titem = tablev.getTable().getItem(i);
			String titemdescription = titem.getText(1).trim();
			if(titemdescription.startsWith("link:"))
			{
				Color col = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
				titem.setForeground(1,col);
				titem.setText(1,titemdescription.substring(5).trim());
			}
		}
		column2.pack();
		
		tablev.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent arg0) {
				TableItem selecteditem = tablev.getTable().getItem(tablev.getTable().getSelectionIndex());
				System.out.println(selecteditem.getForeground(1).toString());
				System.out.println(selecteditem.getDisplay().getSystemColor(SWT.COLOR_BLUE));
				if(selecteditem.getForeground(1).equals(selecteditem.getDisplay().getSystemColor(SWT.COLOR_BLUE)))
				{
					if( !java.awt.Desktop.isDesktopSupported() ) {

			            System.err.println( "Desktop is not supported (fatal)" );
			            System.exit( 1 );
			        }

			       java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

			        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {

			            System.err.println( "Desktop doesn't support the browse action (fatal)" );
			            System.exit( 1 );
			        }

			       
			            try {

			                java.net.URI uri = new java.net.URI(selecteditem.getText(1));
			                desktop.browse( uri );
			            }
			            catch ( Exception e ) {

			                System.err.println( e.getMessage() );
			            }
				}
			}
		});
		
		return page;
	}


}
