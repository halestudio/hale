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
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.cst.transformer.CstService;
import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.hale.Messages;

/**
 * Dialog showing the functions registered with the CST
 * 
 * @author Simon Templer and Jose Ignacio Gisbert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 02 / Etra I+D
 * @version $Id$ 
 */
public class ListFunctionsDialog extends TitleAreaDialog {
	
	private static final ALogger log = ALoggerFactory.getLogger(ListFunctionsDialog.class);
	
	private static Composite parentcomp;
	private static Browser browser=null;
	private static String[] urls;
	private static String[] titles;
	private static int index;
	
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
			String descr = ""; //$NON-NLS-1$
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
		
		setTitle(Messages.ListFunctionsDialog_1); //$NON-NLS-1$
		//setMessage("");
		
		return control;
	}

	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setText(Messages.ListFunctionsDialog_2); //$NON-NLS-1$
	}

	/**
	 * @see TitleAreaDialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
		
		/*Composite page = new Composite(parent, SWT.NONE);
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
				if(selecteditem.getForeground(1).equals(selecteditem.getDisplay().getSystemColor(SWT.COLOR_BLUE)))
				{
					if( !java.awt.Desktop.isDesktopSupported() ) {
						log.error("Desktop Java API is not supported. Can not open default Web Browser");
						return;
			        }

			       java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

			        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
			        	log.error("Desktop Java API doesn't support the browse action. Can not open default Web Browser");
						return;
			        }

			       
			            try {

			                java.net.URI uri = new java.net.URI(selecteditem.getText(1));
			                desktop.browse( uri );
			            }
			            catch ( Exception e ) {
			            	log.error(e.getMessage(),e);
			            }
				}
			}
		});
		
		return page;*/
		
		
		
		//Composite page = new Composite(parent,SWT.NONE);
		
		
		/*Composite compTools = new Composite(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		compTools.setLayoutData(data);
		compTools.setLayout(new GridLayout(2, false));
		ToolBar tocBar = new ToolBar(compTools, SWT.NONE);
		ToolItem openItem = new ToolItem(tocBar, SWT.PUSH);
		openItem.setText("Browse");
		ToolBar navBar = new ToolBar(compTools, SWT.NONE);
		navBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));
		final ToolItem back = new ToolItem(navBar, SWT.PUSH);
		back.setText("Back");
		back.setEnabled(false);
		final ToolItem forward = new ToolItem(navBar, SWT.PUSH);
		forward.setText("Forward");
		forward.setEnabled(false);*/
		
		
		parentcomp = parent;
		Composite comp = new Composite(parent,SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(data);
		comp.setLayout(new FillLayout());
		final SashForm form = new SashForm(comp, SWT.HORIZONTAL|SWT.BORDER);
		
		form.setLayout(new FillLayout());
		
		CstService cst = (CstService) PlatformUI.getWorkbench().getService(CstService.class);
		List<FunctionDescription> functions = cst.getCapabilities().getFunctionDescriptions();
		
		final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(form, SWT.SINGLE |SWT.BORDER);
				
		titles = new String[functions.size()];
		urls = new String[functions.size()];
		for (int i=0;i<functions.size();i++)
		{
			list.add(functions.get(i).getFunctionId().getFile());
			
			titles[i]=functions.get(i).getFunctionDescription();
			urls[i]=functions.get(i).getFunctionDescription();
			if (urls[i]==null) urls[i]=" "; //$NON-NLS-1$
		}
		try {
			browser = new Browser(form, SWT.NONE|SWT.WRAP);
		} catch (SWTError e) {
			MessageBox messageBox = new MessageBox(parent.getShell(), SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage(Messages.ListFunctionsDialog_4); //$NON-NLS-1$
			messageBox.setText(Messages.ListFunctionsDialog_5); //$NON-NLS-1$
			messageBox.open();
		}
		/*back.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				browser.back();
			}
		});
		forward.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				browser.forward();
			}
		});*/
		list.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				int index = list.getSelectionIndex();
				if(urls[index].startsWith("file:")||urls[index].startsWith("http:")) //$NON-NLS-1$ //$NON-NLS-2$
					browser.setUrl(urls[index]);
				else
					browser.setText(urls[index]);
			}
		});
		/*final LocationListener locationListener = new LocationListener() {
			public void changed(LocationEvent event) {
				Browser browser = (Browser)event.widget;
				back.setEnabled(browser.isBackEnabled());
				forward.setEnabled(browser.isForwardEnabled());
			}
			public void changing(LocationEvent event) {
			}
		};*/
		/* Build a table of contents. Open each HTML file
		 * found in the given folder to retrieve their title.
		 */
		final TitleListener tocTitleListener = new TitleListener() {
			public void changed(TitleEvent event) {
				titles[index] = event.title;
			}
		};
		/*final ProgressListener tocProgressListener = new ProgressListener() {
			public void changed(ProgressEvent event) {
			}
			public void completed(ProgressEvent event) {
				Browser browser = (Browser)event.widget;
				index++;
				boolean tocCompleted = index >= titles.length;
				if (tocCompleted) {
					browser.dispose();
					browser = new Browser(form, SWT.NONE);
					browser = browser;
					form.layout(true);
					browser.addLocationListener(locationListener);
					list.removeAll();
					for (int i = 0; i < titles.length; i++) list.add(titles[i]);
					list.select(0);
					browser.setUrl(urls[0]);
					parentcomp.getShell().setText("SWT Browser - Documentation Viewer");
					return;
				}
				parentcomp.getShell().setText("Building index "+index+"/"+urls.length);
				browser.setUrl(urls[index]);
			}
		};
		openItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				DirectoryDialog dialog = new DirectoryDialog(parentcomp.getShell());
				String folder = dialog.open();
				if (folder == null) return;
				File file = new File(folder);
				File[] files = file.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".html") || name.endsWith(".htm");
					}
				});
				if (files.length == 0) return;
				urls = new String[files.length];
				titles = new String[files.length];
				index = 0;
				for (int i = 0; i < files.length; i++) {
					try {
						String url = files[i].toURL().toString();
						urls[i] = url;
					} catch (MalformedURLException ex) {}
				}
				parentcomp.getShell().setText("Building index");
				browser.addTitleListener(tocTitleListener);
				browser.addProgressListener(tocProgressListener);
				if (urls.length > 0) browser.setUrl(urls[0]);
			}
		});*/
		
		return comp;
	}


}
