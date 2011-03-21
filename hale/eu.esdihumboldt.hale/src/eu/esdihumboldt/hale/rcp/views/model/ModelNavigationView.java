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
package eu.esdihumboldt.hale.rcp.views.model;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.models.schema.SchemaServiceListener;
import eu.esdihumboldt.hale.models.utils.SchemaItemService;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;
import eu.esdihumboldt.hale.rcp.views.model.filtering.AbstractContentProviderAction;
import eu.esdihumboldt.hale.rcp.views.model.filtering.PatternViewFilter;
import eu.esdihumboldt.hale.rcp.views.model.filtering.SimpleToggleAction;
import eu.esdihumboldt.hale.rcp.views.model.filtering.UseAggregationHierarchyAction;
import eu.esdihumboldt.hale.rcp.views.model.filtering.UseFlatHierarchyAction;
import eu.esdihumboldt.hale.rcp.views.model.filtering.UseInheritanceHierarchyAction;
import eu.esdihumboldt.hale.rcp.views.table.tree.ColumnBrowserTip;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardContribution;

/**
 * This view component handles the display of source and target schemas.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @version $Id$
 */
public class ModelNavigationView extends ViewPart implements
		ISelectionProvider{

	/**
	 * Function contribution that always uses this view's selection
	 */
	private class SchemaFunctionContribution extends FunctionWizardContribution {

		/**
		 * Default constructor
		 */
		public SchemaFunctionContribution() {
			super(true);
		}

		/**
		 * @see FunctionWizardContribution#getSelection()
		 */
		@Override
		protected ISelection getSelection() {
			return currentSelection;
		}

	}

	//private static Logger _log = Logger.getLogger(ModelNavigationView.class);
	
	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView"; //$NON-NLS-1$

	/**
	 * Viewer for the source schema
	 */
	private TreeViewer sourceSchemaViewer;
	
	/**
	 * Viewer for the target schema
	 */
	private TreeViewer targetSchemaViewer;

	/**
	 * A reference to the {@link SchemaService} which serves as model for this
	 * {@link ViewPart}.
	 */
	private SchemaItemService schemaItemService;

	/**
	 * The selection listeners
	 */
	private final Set<ISelectionChangedListener> listeners = new HashSet<ISelectionChangedListener>();

	private ISelection currentSelection;
	
	private Image functionImage;
	
	private Image augmentImage;

	private SchemaServiceListener schemaListener;

	private HaleServiceListener alignmentListener;
	
	/**
	 * @see WorkbenchPart#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite _parent) {
		// get schema service
		schemaItemService = (SchemaItemService) PlatformUI.getWorkbench().getService(SchemaItemService.class);
		schemaItemService.addListener(schemaListener = new SchemaServiceListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void update(UpdateMessage message) {
				// ignore
			}
			
			@Override
			public void schemaChanged(final SchemaType schema) {
				if (Display.getCurrent() != null) {
					ModelNavigationView.this.update(schema);
				}
				else {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {
						
						@Override
						public void run() {
							ModelNavigationView.this.update(schema);
						}
					});
				}
			}
		});
		
		// register as selection provider
		getSite().setSelectionProvider(this);
		
		final PatternViewFilter sourceSchemaFilter = new PatternViewFilter();
		final PatternViewFilter targetSchemaFilter = new PatternViewFilter();

		Composite modelComposite = new Composite(_parent, SWT.BEGINNING);
		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 3;
		layout.horizontalSpacing = 0;
		modelComposite.setLayout(layout);
		
		List<SimpleToggleAction> sourceToggleActions = this.getToggleActions(
				sourceSchemaFilter);
		List<SimpleToggleAction> targetToggleActions = this.getToggleActions(
				targetSchemaFilter);

		// source schema toolbar, filter and explorer
		Composite sourceComposite = new Composite(modelComposite, SWT.BEGINNING);
		sourceComposite.setLayout(new GridLayout(1, false));
		sourceComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		List<AbstractContentProviderAction> sourceContentActions = 
			initSchemaExplorerToolBar(sourceComposite, sourceSchemaFilter, 
				sourceToggleActions, "Source"); //$NON-NLS-1$

		this.sourceSchemaViewer = this.schemaExplorerSetup(sourceComposite, SchemaType.SOURCE);
		this.sourceSchemaViewer.addFilter(sourceSchemaFilter);
		
		for (AbstractContentProviderAction cpa : sourceContentActions) {
			cpa.setViewer(sourceSchemaViewer);
		}

		for (SimpleToggleAction sta : sourceToggleActions) {
			sta.setActionTarget(this.sourceSchemaViewer);
		}
		
		// function button
		final Button functionButton = new Button(modelComposite, SWT.PUSH | SWT.FLAT);
		functionImage = HALEActivator.getImageDescriptor("icons/mapping.gif").createImage(); //$NON-NLS-1$
		augmentImage = HALEActivator.getImageDescriptor("icons/augment.gif").createImage(); //$NON-NLS-1$
		functionButton.setImage(functionImage);
		functionButton.setToolTipText(Messages.ModelNavigationView_FunctionButtonToolTipText);
		functionButton.setEnabled(false);
		functionButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		final FunctionWizardContribution functionContribution = new SchemaFunctionContribution();
		manager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				// populate context menu
				manager.add(functionContribution);
			}
			
		});
		final Menu functionMenu = manager.createContextMenu(functionButton);
		functionButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see SelectionAdapter#widgetSelected(SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				// show menu on button press
				functionMenu.setLocation(Display.getCurrent().getCursorLocation());
				functionMenu.setVisible(true);
			}
			
		});
		addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				functionButton.setEnabled(functionContribution.hasActiveFunctions());
				if (event.getSelection() instanceof SchemaSelection) {
					SchemaSelection selection = (SchemaSelection) event.getSelection();
					if (selection.getSourceItems().size() == 0 && selection.getTargetItems().size() > 0) {
						// augmentation
						functionButton.setImage(augmentImage);
					}
					else {
						// function
						functionButton.setImage(functionImage);
					}
				}
			}
			
		});

		// target schema toolbar, filter and explorer
		Composite targetComposite = new Composite(modelComposite, SWT.BEGINNING);
		targetComposite.setLayout(new GridLayout(1, false));
		targetComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		List<AbstractContentProviderAction> targetContentActions = 
			initSchemaExplorerToolBar(targetComposite, targetSchemaFilter, 
				targetToggleActions, "Target"); //$NON-NLS-1$

		this.targetSchemaViewer = this.schemaExplorerSetup(targetComposite,
				SchemaType.TARGET);
		
		this.targetSchemaViewer.addFilter(targetSchemaFilter);
		
		for (AbstractContentProviderAction cpa : targetContentActions) {
			cpa.setViewer(targetSchemaViewer);
		}

		for (SimpleToggleAction sta : targetToggleActions) {
			sta.setActionTarget(this.targetSchemaViewer);
		}
		
		// redraw on alignment change
		AlignmentService as = (AlignmentService) getSite().getService(AlignmentService.class);
		as.addListener(alignmentListener = new HaleServiceListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(UpdateMessage message) {
				if (Display.getCurrent() != null) {
					refresh();
				}
				else {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {
						
						@Override
						public void run() {
							refresh();
						}
					});
				}
			}
			
		});
		
		// also add the alignment listener to the style service (for refreshing icons when style changes)
		StyleService styleService = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		styleService.addListener(alignmentListener);
		
		MenuManager sourceMenuManager = new MenuManager();
		sourceMenuManager.setRemoveAllWhenShown(true);
		final IContributionItem sourceContextFunctions = new SchemaItemContribution(sourceSchemaViewer, false);
		sourceMenuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(sourceContextFunctions);
			}
			
		});
		
		Menu sourceMenu = sourceMenuManager.createContextMenu(sourceSchemaViewer.getControl());
		sourceSchemaViewer.getControl().setMenu(sourceMenu);
		
		MenuManager targetMenuManager = new MenuManager();
		targetMenuManager.setRemoveAllWhenShown(true);
		final IContributionItem targetContextFunctions = new SchemaItemContribution(targetSchemaViewer, true);
		targetMenuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(targetContextFunctions);
			}
			
		});
		
		Menu targetMenu = targetMenuManager.createContextMenu(targetSchemaViewer.getControl());
		targetSchemaViewer.getControl().setMenu(targetMenu);
		
		getSite().registerContextMenu(sourceMenuManager, sourceSchemaViewer);
		getSite().registerContextMenu(targetMenuManager, targetSchemaViewer);
	}

	private List<SimpleToggleAction> getToggleActions(PatternViewFilter pvf) {
		List<SimpleToggleAction> result = new ArrayList<SimpleToggleAction>();
		result.add(new SimpleToggleAction(TreeObjectType.ABSTRACT_FT, 
				Messages.ModelNavigationView_2, Messages.ModelNavigationView_3,  //$NON-NLS-1$ //$NON-NLS-2$
				"/icons/see_abstract_ft.png", pvf)); //$NON-NLS-1$
		result.add(new SimpleToggleAction(TreeObjectType.PROPERTY_TYPE, 
				Messages.ModelNavigationView_PropertyHide, Messages.ModelNavigationView_PropertyShow, 
				"/icons/see_property_type.gif", pvf)); //$NON-NLS-1$
		result.add(new SimpleToggleAction(TreeObjectType.STRING_ATTRIBUTE, 
				Messages.ModelNavigationView_StringHide, Messages.ModelNavigationView_StringShow, 
				"/icons/see_string_attribute.png", pvf)); //$NON-NLS-1$
		result.add(new SimpleToggleAction(TreeObjectType.GEOMETRIC_ATTRIBUTE, 
				Messages.ModelNavigationView_GeometryHide, Messages.ModelNavigationView_GeometryShow, 
				"/icons/see_geometry_attribute.png", pvf)); //$NON-NLS-1$
		result.add(new SimpleToggleAction(TreeObjectType.NUMERIC_ATTRIBUTE, 
				Messages.ModelNavigationView_NumericHide, Messages.ModelNavigationView_NumericShow, 
				"/icons/see_number_attribute.png", pvf)); //$NON-NLS-1$
		return result;
	}

	private List<AbstractContentProviderAction> initSchemaExplorerToolBar(Composite modelComposite, 
			PatternViewFilter pvf, List<SimpleToggleAction> toggleActions, String caption) {

		Composite bar = new Composite(modelComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 10;
		bar.setLayout(gridLayout);
		
		Label captionLabel = new Label(bar, SWT.NONE);
		captionLabel.setText(caption);

		// create toolbar
		ToolBar schemaFilterBar = new ToolBar(bar, SWT.FLAT
				| SWT.WRAP);
		schemaFilterBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		List<AbstractContentProviderAction> actions = new ArrayList<AbstractContentProviderAction>();
		actions.add(new UseFlatHierarchyAction());
		actions.add(new UseInheritanceHierarchyAction());
		actions.add(new UseAggregationHierarchyAction());
		
		// default setting for actions 
		actions.get(2).setChecked(true);

		ToolBarManager manager = new ToolBarManager(schemaFilterBar);
		for (AbstractContentProviderAction action : actions) {
			manager.add(action);
			action.setCaption(caption);
		}
		manager.add(new Separator());
		for (SimpleToggleAction sta : toggleActions) {
			manager.add(sta);
			sta.setCaption(caption);
		}
		manager.update(false);
		
		return actions;
	}

	/**
	 * A helper method for setting up the two SchemaExplorers.
	 * 
	 * @param modelComposite
	 *            the parent {@link Composite} to use.
	 * @param schemaType the viewer type
	 * @return a {@link TreeViewer} with the currently loaded schema.
	 */
	private TreeViewer schemaExplorerSetup(Composite modelComposite, final SchemaType schemaType) {
		PatternFilter patternFilter = new PatternFilter();
	    final FilteredTree filteredTree = new FilteredTree(modelComposite, SWT.MULTI
	            | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter, true);
	    TreeViewer schemaViewer = filteredTree.getViewer();
	    // set the default content provider, settings must match initial action state (be careful: [asIs, invert, invert])
		schemaViewer.setContentProvider(new ConfigurableModelContentProvider(false, false, true));
		ModelNavigationViewLabelProvider labelProvider = new ModelNavigationViewLabelProvider();
		schemaViewer.setLabelProvider(labelProvider);
		schemaViewer.setInput(schemaItemService.getRoot(schemaType));
        schemaViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						updateSelection();
					}
				});
        
        // add tool tip
		new ColumnBrowserTip(schemaViewer, 400, 300, true, 0, labelProvider);
        
		return schemaViewer;
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// ignore
	}

	/**
	 * Update the viewers on changes to a schema
	 * 
	 * @param schema the schema that changed
	 */
	protected void update(SchemaType schema) {
		switch (schema) {
		case SOURCE:
			sourceSchemaViewer.setInput(schemaItemService.getRoot(schema));
			sourceSchemaViewer.refresh();
			break;
		case TARGET:
			targetSchemaViewer.setInput(schemaItemService.getRoot(schema));
			targetSchemaViewer.refresh();
			break;
		}
	}

	/**
	 * Update the selection and fire a selection change
	 */
	@SuppressWarnings("unchecked")
	private void updateSelection() {
		SchemaSelection selection = new SchemaSelection();
		
		// source items
		IStructuredSelection sourceSelection = (IStructuredSelection) sourceSchemaViewer.getSelection();
		if (sourceSelection != null) {
			Iterator<Object> it = sourceSelection.iterator();
			while (it.hasNext()) {
				Object item = it.next();
				if (item != null && item instanceof SchemaItem) {
					selection.addSourceItem((SchemaItem) item);
				}
			}
		}
		
		// target items
		IStructuredSelection targetSelection = (IStructuredSelection) targetSchemaViewer.getSelection();
		if (targetSelection != null) {
			Iterator<Object> it = targetSelection.iterator();
			while (it.hasNext()) {
				Object item = it.next();
				if (item != null && item instanceof SchemaItem) {
					selection.addTargetItem((SchemaItem) item);
				}
			}
		}
 		
		fireSelectionChange(selection);
	}
	
	/**
	 * Select the item with representing the given identifier
	 * 
	 * @param identifier the identifier
	 */
	public void selectItem(String identifier) {
		TreeViewer tree;
		SchemaItem item = schemaItemService.getSchemaItem(identifier, SchemaType.SOURCE);
		if (item == null) {
			item = schemaItemService.getSchemaItem(identifier, SchemaType.TARGET);
			tree = targetSchemaViewer;
		}
		else {
			tree = sourceSchemaViewer;
		}
		
		if (item != null) {
			tree.setSelection(new StructuredSelection(item), true);
		}
	}
	
	/**
	 * Sets the selection to the given selection and fires a selection change
	 * 
	 * @param selection the selection to set 
	 */
	protected void fireSelectionChange(ISelection selection) {
		this.currentSelection = selection;
		
		SelectionChangedEvent event = 
			new SelectionChangedEvent(this, currentSelection);
		
		for (ISelectionChangedListener listener : listeners) {
			listener.selectionChanged(event);
		}
	}

	/**
	 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		return currentSelection;
	}

	/**
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		this.currentSelection = selection;
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (schemaListener != null) {
			schemaItemService.removeListener(schemaListener);
		}
		
		if (alignmentListener != null) {
			AlignmentService as = (AlignmentService) getSite().getService(AlignmentService.class);
			as.removeListener(alignmentListener);
			StyleService styleService = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
			styleService.removeListener(alignmentListener);
		}
		
		if (functionImage != null) {
			functionImage.dispose();
		}
		if (augmentImage != null) {
			augmentImage.dispose();
		}
		
		super.dispose();
	}

	/**
	 * Refresh both tree viewers
	 */
	public void refresh() {
		sourceSchemaViewer.refresh();
		targetSchemaViewer.refresh();
	}

}