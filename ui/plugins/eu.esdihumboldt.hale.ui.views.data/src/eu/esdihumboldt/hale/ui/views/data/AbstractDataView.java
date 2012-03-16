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
package eu.esdihumboldt.hale.ui.views.data;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.WorkbenchPart;

import de.cs3d.ui.util.eclipse.extension.exclusive.ExclusiveExtensionContribution;
import de.cs3d.util.eclipse.extension.exclusive.ExclusiveExtension;
import de.cs3d.util.eclipse.extension.exclusive.ExclusiveExtension.ExclusiveExtensionListener;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.util.selection.SelectionProviderFacade;
import eu.esdihumboldt.hale.ui.views.data.internal.DataViewPlugin;
import eu.esdihumboldt.hale.ui.views.data.internal.extension.InstanceViewController;
import eu.esdihumboldt.hale.ui.views.data.internal.extension.InstanceViewFactory;
import eu.esdihumboldt.hale.ui.views.data.internal.filter.InstanceSelectionListener;
import eu.esdihumboldt.hale.ui.views.data.internal.filter.InstanceSelector;
import eu.esdihumboldt.hale.ui.views.data.internal.filter.WindowSelectionSelector;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;

/**
 * Table view that shows information about certain features
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractDataView extends PropertiesViewPart {

	/**
	 * Action for toggling if an instance selection is provided by the view.
	 */
	public class ToggleProvideSelectionAction extends Action {

		/**
		 * Default constructor
		 */
		public ToggleProvideSelectionAction() {
			super("", AS_CHECK_BOX);
			
			updateText();
			setImageDescriptor(DataViewPlugin.getImageDescriptor("icons/map.gif"));
			setChecked(isProvideInstanceSelection());
			
			addPropertyChangeListener(new IPropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getProperty().equals(CHECKED)) {
						setProvideInstanceSelection(
								(Boolean) event.getNewValue());
						updateText();
					}
				}
			});
		}
		
		/**
		 * Update the action text
		 */
		private void updateText() {
			setToolTipText((isProvideInstanceSelection())
					?("Disable providing an application instance selection (e.g. for the map)")
					:("Enable providing an instance selection for the application, e.g. for display in the map"));
		}

	}

	/**
	 * The instance viewer
	 */
	private InstanceViewer viewer;
	
	private Composite selectorComposite;
	
	private InstanceSelector instanceSelector;
	
	private Control selectorControl;

	private Composite viewerComposite;
	
	private final InstanceViewController controller;

	private TypeDefinition lastType;

	private Iterable<Instance> lastSelection;
	
	private final SelectionProviderFacade selectionFacade = new SelectionProviderFacade();
	
	private boolean provideInstanceSelection = false;

	private final InstanceSelector defaultInstanceSelector;
	
	/**
	 * Creates a table view
	 * 
	 * @param instanceSelector the feature selector
	 * @param controllerPreferenceKey the preference key for storing the
	 *   instance view controller configuration
	 */
	public AbstractDataView(InstanceSelector instanceSelector, 
			String controllerPreferenceKey) {
		super();
		
		this.defaultInstanceSelector = instanceSelector;
		this.controller = new InstanceViewController(
				DataViewPlugin.getDefault().getPreferenceStore(), 
				controllerPreferenceKey);
	}
	
	/**
	 * Get the default instance selector.
	 * @return the default instance selector
	 */
	public InstanceSelector getDefaultInstanceSelector() {
		return defaultInstanceSelector;
	}

	/**
	 * @see WorkbenchPart#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 300;
		page.setLayoutData(data);
		page.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());
		
		// bar composite
		Composite bar = new Composite(page, SWT.NONE);
		bar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 3;
		gridLayout.marginHeight = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		bar.setLayout(gridLayout);
		
		// custom control composite
		Composite custom = new Composite(bar, SWT.NONE);
		custom.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		provideCustomControls(custom);
		
		// selector composite
		selectorComposite = new Composite(bar, SWT.NONE);
		selectorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		selectorComposite.setLayout(gridLayout);
		
		// tree composite
		viewerComposite = new Composite(page, SWT.NONE);
		viewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// tree column layout
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginHeight = 0;
		fillLayout.marginWidth = 0;
		viewerComposite.setLayout(fillLayout);
		
		fillActionBars();
		
		// selector
		setInstanceSelector(defaultInstanceSelector);
		
		// tree viewer
		updateViewer(controller.getCurrent());
		
		controller.addListener(new ExclusiveExtensionListener<InstanceViewer, InstanceViewFactory>() {
			
			@Override
			public void currentObjectChanged(InstanceViewer current,
					InstanceViewFactory definition) {
				updateViewer(current);
			}
		});
		
		getSite().setSelectionProvider(selectionFacade);
	}

	/**
	 * Fill the action bars
	 */
	private void fillActionBars() {
		IContributionItem viewSelector = new ExclusiveExtensionContribution<InstanceViewer, InstanceViewFactory>() {
			@Override
			protected ExclusiveExtension<InstanceViewer, InstanceViewFactory> initExtension() {
				return controller;
			}
		};
		
		IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		toolbar.add(viewSelector);
		toolbar.add(new Separator());
		toolbar.add(new ToggleProvideSelectionAction());
		
		IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		menu.add(viewSelector);
	}

	/**
	 * Update on viewer change.
	 * @param viewer the new viewer
	 */
	private void updateViewer(InstanceViewer viewer) {
		if (this.viewer != null) {
			// dispose old viewer
			this.viewer.getControl().dispose();
		}

		// create new viewer controls
		viewer.createControls(viewerComposite);
		
		viewer.setInput(lastType, lastSelection);
		
		viewerComposite.layout(true, true);
		
		this.viewer = viewer;
		
		// setup selection provider
		updateSelectionProvider();
//		new ViewerMenu(getSite(), viewer.getViewer());
	}

	/**
	 * Update the selection provider when either the instance viewer or
	 * instance selector have changed.
	 */
	protected void updateSelectionProvider() {
		if (viewer == null 
				|| !provideInstanceSelection
				|| getInstanceSelector() instanceof WindowSelectionSelector) {
			selectionFacade.setSelectionProvider(null);
		}
		else {
			selectionFacade.setSelectionProvider(
					viewer.getInstanceSelectionProvider());//.getViewer());
		}
	}

	/**
	 * Add custom controls. Override this method to add custom controls
	 * 
	 * @param parent the parent composite
	 */
	protected void provideCustomControls(Composite parent) {
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		// empty composite trick: exclude
		gd.exclude = true;
		parent.setLayoutData(gd);
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getViewer().getControl().setFocus();
	}

	/**
	 * @return the instance selector
	 */
	public InstanceSelector getInstanceSelector() {
		return instanceSelector;
	}

	/**
	 * @param instanceSelector the instance selector to set
	 */
	public void setInstanceSelector(InstanceSelector instanceSelector) {
		if (this.instanceSelector == instanceSelector) return;
		
		selectionFacade.setSelectionProvider(null); // disable selection provider
		
		this.instanceSelector = instanceSelector;
		
		// remove old control
		if (selectorControl != null) {
			selectorControl.dispose();
		}
		
		// create new control
		selectorControl = instanceSelector.createControl(selectorComposite);
		selectorControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		updateSelectionProvider();
		
		// re-layout
		selectorComposite.getParent().getParent().layout(true, true);
		
		// add listener
		instanceSelector.addSelectionListener(new InstanceSelectionListener() {
			
			@Override
			public void selectionChanged(TypeDefinition type, Iterable<Instance> selection) {
				if (viewer != null) {
					viewer.setInput(type, selection);
				}
				lastType = type;
				lastSelection = selection;
				onSelectionChange(selection);
			}
		});
	}
	
	/**
	 * Called when the selection has changed
	 * 
	 * @param selection the current selection 
	 */
	protected void onSelectionChange(Iterable<Instance> selection) {
		// do nothing
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (selectorControl != null) {
			selectorControl.dispose();
		}
	}

	/**
	 * @return the provideInstanceSelection
	 */
	private boolean isProvideInstanceSelection() {
		return provideInstanceSelection;
	}

	/**
	 * @param provideInstanceSelection the provideInstanceSelection to set
	 */
	private void setProvideInstanceSelection(boolean provideInstanceSelection) {
		this.provideInstanceSelection = provideInstanceSelection;
		
		updateSelectionProvider();
	}

}
