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
import eu.esdihumboldt.hale.core.io.project.ProjectInfo;
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
					ProjectInfo info = proService.getProjectInfo();
					/*
					 * TODO It may be that there's no related project!
					 * e.g.: loading a project produces a Report but there's not project related at
					 * this moment. Introducing an id or something similar may prevent this
					 * and improves the handling of Reports (renaming the project or so).
					 * 
					 */
					if (info != null) {
//						_treeViewer.add(info, report.getTaskName());
						_treeViewer.setInput(new ReportItem(info, report));
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
			else if (element instanceof Integer) {
				ProjectInfo info = ReportListContentProvider.projectData.get(element);
				String ret = info.getName();
				
				if (ret == null) {
					ret = "";
				}
				return info.getName();
			}
			
			
			return "Unhandled type";
		}
	}
	

}
