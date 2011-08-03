/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import swing2swt.layout.BorderLayout;
import eu.esdihumboldt.hale.core.report.Message;
import eu.esdihumboldt.hale.core.report.Report;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.report.ReportListener;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import eu.esdihumboldt.hale.ui.views.report.properties.ReportPropertiesViewPart;

/**
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportList extends ReportPropertiesViewPart implements ReportListener<Report<Message>, Message> {

	public static final String ID = "eu.esdihumboldt.hale.ui.views.report.ReportList"; //$NON-NLS-1$
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private TreeViewer _treeViewer;

	public ReportList() {
		// get ReportService and add listener
		ReportService repService = (ReportService) PlatformUI.getWorkbench().getService(ReportService.class);
		repService.addReportListener(this);
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new BorderLayout(0, 0));
		{
			Composite composite = new Composite(container, SWT.NONE);
			composite.setLayoutData(BorderLayout.CENTER);
			formToolkit.adapt(composite);
			formToolkit.paintBordersFor(composite);
			composite.setLayout(new TreeColumnLayout());
			{
				_treeViewer = new TreeViewer(composite, SWT.BORDER);
				Tree tree = _treeViewer.getTree();
				tree.setHeaderVisible(true);
				tree.setLinesVisible(true);
				formToolkit.paintBordersFor(tree);
			}
		}

		createActions();
		initializeToolBar();
		initializeMenu();
		
		// set label provider
		_treeViewer.setLabelProvider(new ReportListLabelProvider());
		
		// set content provider
		_treeViewer.setContentProvider(new ReportListContentProvider());
		
		getSite().setSelectionProvider(_treeViewer);
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
		_treeViewer.getControl().setFocus();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#getReportType()
	 */
	@Override
	public Class getReportType() {
		return Report.class;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#getMessageType()
	 */
	@Override
	public Class getMessageType() {
		return Message.class;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#reportAdded(eu.esdihumboldt.hale.core.report.Report)
	 */
	@Override
	public void reportAdded(final Report<Message> report) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try{
					
					ProjectService proService = (ProjectService) PlatformUI.getWorkbench().getService(ProjectService.class);
					String projectname = proService.getProjectName();
					/*
					 * TODO It may be that there's no related project!
					 * e.g.: loading a project produces a Report but there's not project related at
					 * this moment. Introducing an id or something similar may prevent this
					 * and improves the handling of Reports (renaming the project or so).
					 * 
					 */
					if (projectname != null) {
						_treeViewer.add(proService.getProjectName(), report.getTaskName());
						_treeViewer.setInput(new ReportItem(projectname, report));
					}
				} catch (NullPointerException e) {
					// TODO remove this or add proper Exception handling
					//System.err.println("NullPointer... "+report.getSummary());
//					e.printStackTrace();
				}
			}
		});
	}
	
	public class ReportListLabelProvider implements ILabelProvider  {

		private Map<ImageDescriptor, Image> imageCache = new HashMap<ImageDescriptor, Image>();
		
		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		@Override
		public boolean isLabelProperty(Object element, String property) {
			//System.err.println("LabelProvider.isLabelProperty(): ");
			// TODO Auto-generated method stub
			return false;
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element) {
			//System.err.println("LabelProvider.getImage(): "+element.getClass());
			
			if (element instanceof String) {
				return null;
			}
			else if (element instanceof Report) {
				// get the right image
				Report report = (Report) element;
				
				String img = "icons/signed_yes.gif";
				if (report.getWarnings().size() > 0 && report.getErrors().size() > 0) {
					img = "icons/errorwarning_tab.gif";
				} else if (report.getErrors().size() > 0) {
					img = "icons/error.gif";
				} else if (report.getWarnings().size() > 0) {
					img = "icons/warning.gif";
				}
				
				ImageDescriptor descriptor = null;
				
//				descriptor = ImageDescriptor.createFromURL(
//						FileLocator.find(Platform.getBundle(ReportList.ID), new Path("icons/ft_stylelist.gif"), null)
//				);
				
				// TODO Platform.getBundle(ReportList.ID) does not work so here is a static plugin path!
				descriptor = AbstractUIPlugin.imageDescriptorFromPlugin("eu.esdihumboldt.hale.ui.views.report", img);
				if (descriptor == null) {
					return null;
				}
				
				Image image = imageCache.get(descriptor);
				if (image == null) {
					image = descriptor.createImage();
					imageCache.put(descriptor, image);
				}
				return image;
			}
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			//System.err.println("LabelProvider.getText(): "+element.getClass());
			if (element instanceof String) {
				return (String)element;
			}
			else if (element instanceof Report) {
				return ((Report) element).getTaskName();
			}
			
			
			return "Unhandle type";
		}
	}
	
	public class ReportListContentProvider implements ITreeContentProvider {

		private Map<String, List<Report>> data = new HashMap<String, List<Report>>();
		
		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof ReportItem) {
				String project = ((ReportItem) newInput).getProject();
				ArrayList<Report> reports;
				
				//System.err.println("ReportListContentProvider.inputChanged() "+project);
				
				// check if there's already a list
				if (this.data.get(project) == null) {
					reports = new ArrayList<Report>();
				} else {
					reports = (ArrayList<Report>) this.data.get(project);
				}
				
				// add the new report
				reports.add(((ReportItem) newInput).getReport());
				this.data.put(project, reports);
			}
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			//System.err.println("getElements: "+inputElement);
			// display the listing of projects
			
			Object[] keys = data.keySet().toArray();
			
			return keys;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object parentElement) {
			List<Report> reports = this.data.get(parentElement);
			
			if (reports.size() == 0) {
				return null;
			}
			
			
			Object[] ret = new Object[reports.size()];
			for(int i = 0; i < reports.size(); i++) {
				ret[i] = reports.get(i);
			}
			
			return ret;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		@Override
		public Object getParent(Object element) {
			//System.err.println("ReportListContentProvider.getParent(): Implement me!");
			// TODO Auto-generated method stub
			return "";
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object element) {
			List<Report> list = this.data.get(element);
			if (list != null && list.size() > 0) {
				return true;
			}

			return false;
		}

	}
}
