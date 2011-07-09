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
package eu.esdihumboldt.hale.ui.views.schemas;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

import eu.esdihumboldt.hale.schema.model.Schema;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.views.schemas.explorer.SchemaExplorer;

/**
 * This view component handles the display of source and target schemas.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @version $Id$
 */
public class SchemasView extends ViewPart {

//	/**
	//	 * Function contribution that always uses this view's selection
	//	 */
	//	private class SchemaFunctionContribution extends FunctionWizardContribution {
	//
	//		/**
	//		 * Default constructor
	//		 */
	//		public SchemaFunctionContribution() {
	//			super(true);
	//		}
	//
	//		/**
	//		 * @see FunctionWizardContribution#getSelection()
	//		 */
	//		@Override
	//		protected ISelection getSelection() {
	//			return currentSelection;
	//		}
	//
	//	}

	//private static Logger _log = Logger.getLogger(ModelNavigationView.class);

	/**
	 * Selection provider combining selections from source and target schema explorers
	 */
	private class SchemasSelectionProvider implements ISelectionProvider, ISelectionChangedListener {
		
		/**
		 * The selection listeners
		 */
		private final Set<ISelectionChangedListener> listeners = new HashSet<ISelectionChangedListener>();

		/**
		 * The current selection
		 */
		private ISelection currentSelection;

		/**
		 * Default constructor
		 */
		public SchemasSelectionProvider() {
			super();
			
			sourceExplorer.getTreeViewer().addSelectionChangedListener(this);
			targetExplorer.getTreeViewer().addSelectionChangedListener(this);
		}

		/**
		 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
		 */
		@Override
		public void addSelectionChangedListener(
				ISelectionChangedListener listener) {
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
		 * Update the selection and fire a selection change
		 */
		@SuppressWarnings("unchecked")
		private void updateSelection() {
			// combine the selections of both viewers
			//XXX for now using a StructuredSelection
			
			// source items
			IStructuredSelection sourceSelection = (IStructuredSelection) sourceExplorer.getTreeViewer().getSelection();
			List<Object> elements = new ArrayList<Object>(sourceSelection.toList());
			
			// target items
			IStructuredSelection targetSelection = (IStructuredSelection) targetExplorer.getTreeViewer().getSelection();
			elements.addAll(targetSelection.toList());
	 		
			StructuredSelection selection = new StructuredSelection(elements);
			fireSelectionChange(selection);
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
		 * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
		 */
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			updateSelection();
		}

	}

	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.schemas"; //$NON-NLS-1$

	/**
	 * Viewer for the source schema
	 */
	private SchemaExplorer sourceExplorer;
	
	/**
	 * Viewer for the target schema
	 */
	private SchemaExplorer targetExplorer;

	/**
	 * A reference to the {@link SchemaService} which serves the model for this
	 * view.
	 */
	private SchemaService schemaService;

//	private Image functionImage;
//	
//	private Image augmentImage;

	private SchemaServiceListener schemaListener;

//	private HaleServiceListener alignmentListener;
//
//	private StyleServiceListener styleListener;
	
	/**
	 * @see WorkbenchPart#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite _parent) {
		// get schema service
		schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		schemaService.addSchemaServiceListener(schemaListener = new SchemaServiceListener() {
			
			@Override
			public void schemasCleared(final SchemaSpaceID spaceID) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						switch (spaceID) {
						case SOURCE:
							sourceExplorer.setSchema(null);
							break;
						case TARGET:
							targetExplorer.setSchema(null);
						}
					}
				});
			}
			
			@Override
			public void schemaAdded(final SchemaSpaceID spaceID, Schema schema) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						switch (spaceID) {
						case SOURCE:
							sourceExplorer.setSchema(schemaService.getSchemas(spaceID));
							break;
						case TARGET:
							targetExplorer.setSchema(schemaService.getSchemas(spaceID));
						}
					}
				});
			}
		});
		
//		final PatternViewFilter sourceSchemaFilter = new PatternViewFilter();
//		final PatternViewFilter targetSchemaFilter = new PatternViewFilter();

		Composite modelComposite = new Composite(_parent, SWT.BEGINNING);
		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 3;
		layout.horizontalSpacing = 0;
		modelComposite.setLayout(layout);
		
//		List<SimpleToggleAction> sourceToggleActions = this.getToggleActions(
//				sourceSchemaFilter);
//		List<SimpleToggleAction> targetToggleActions = this.getToggleActions(
//				targetSchemaFilter);

		// source schema toolbar, filter and explorer
		sourceExplorer = new SchemaExplorer(modelComposite);
		sourceExplorer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
//		List<AbstractContentProviderAction> sourceContentActions = 
//			initSchemaExplorerToolBar(sourceComposite, sourceSchemaFilter, 
//				sourceToggleActions, Messages.ModelNavigationView_Source, "source"); //$NON-NLS-1$ //$NON-NLS-2$
//
//		this.sourceSchemaViewer = this.schemaExplorerSetup(sourceComposite, SchemaSpaceID.SOURCE);
//		this.sourceSchemaViewer.addFilter(sourceSchemaFilter);
//		
//		for (AbstractContentProviderAction cpa : sourceContentActions) {
//			cpa.setViewer(sourceSchemaViewer);
//		}
//
//		for (SimpleToggleAction sta : sourceToggleActions) {
//			sta.setActionTarget(this.sourceSchemaViewer);
//		}
		
		// function button
		final Button functionButton = new Button(modelComposite, SWT.PUSH | SWT.FLAT);
//		functionImage = SchemasViewPlugin.getImageDescriptor("icons/mapping.gif").createImage(); //$NON-NLS-1$
//		augmentImage = SchemasViewPlugin.getImageDescriptor("icons/augment.gif").createImage(); //$NON-NLS-1$
//		functionButton.setImage(functionImage);
//		functionButton.setToolTipText(Messages.ModelNavigationView_FunctionButtonToolTipText);
//		functionButton.setEnabled(false);
//		functionButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
//		MenuManager manager = new MenuManager();
//		manager.setRemoveAllWhenShown(true);
//		final FunctionWizardContribution functionContribution = new SchemaFunctionContribution();
//		manager.addMenuListener(new IMenuListener() {
//
//			@Override
//			public void menuAboutToShow(IMenuManager manager) {
//				// populate context menu
//				manager.add(functionContribution);
//			}
//			
//		});
//		final Menu functionMenu = manager.createContextMenu(functionButton);
//		functionButton.addSelectionListener(new SelectionAdapter() {
//
//			/**
//			 * @see SelectionAdapter#widgetSelected(SelectionEvent)
//			 */
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				// show menu on button press
//				functionMenu.setLocation(Display.getCurrent().getCursorLocation());
//				functionMenu.setVisible(true);
//			}
//			
//		});
//		addSelectionChangedListener(new ISelectionChangedListener() {
//
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				functionButton.setEnabled(functionContribution.hasActiveFunctions());
//				if (event.getSelection() instanceof SchemaSelection) {
//					SchemaSelection selection = (SchemaSelection) event.getSelection();
//					if (selection.getSourceItems().size() == 0 && selection.getTargetItems().size() > 0) {
//						// augmentation
//						functionButton.setImage(augmentImage);
//					}
//					else {
//						// function
//						functionButton.setImage(functionImage);
//					}
//				}
//			}
//			
//		});

		// target schema toolbar, filter and explorer
		targetExplorer = new SchemaExplorer(modelComposite);
		targetExplorer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
//		List<AbstractContentProviderAction> targetContentActions = 
//			initSchemaExplorerToolBar(targetComposite, targetSchemaFilter, 
//				targetToggleActions, Messages.ModelNavigationView_Target, "target"); //$NON-NLS-1$ //$NON-NLS-2$
//
//		this.targetSchemaViewer = this.schemaExplorerSetup(targetComposite,
//				SchemaSpaceID.TARGET);
//		
//		this.targetSchemaViewer.addFilter(targetSchemaFilter);
//		
//		for (AbstractContentProviderAction cpa : targetContentActions) {
//			cpa.setViewer(targetSchemaViewer);
//		}
//
//		for (SimpleToggleAction sta : targetToggleActions) {
//			sta.setActionTarget(this.targetSchemaViewer);
//		}
		
		// redraw on alignment change
//		AlignmentService as = (AlignmentService) getSite().getService(AlignmentService.class);
//		as.addListener(alignmentListener = new HaleServiceListener() {
//
//			@Override
//			public void update(@SuppressWarnings("rawtypes") UpdateMessage message) {
//				refreshInDisplayThread();
//			}
//			
//		});
		
		// also add the alignment listener to the style service (for refreshing icons when style changes)
//		StyleService styleService = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
//		styleService.addListener(styleListener = new StyleServiceListener() {
//			
//			@Override
//			public void stylesRemoved(StyleService styleService) {
//				// refresh to update legend images
//				refreshInDisplayThread();
//			}
//			
//			@Override
//			public void stylesAdded(StyleService styleService) {
//				// refresh to update legend images
//				refreshInDisplayThread();
//			}
//			
//			@Override
//			public void backgroundChanged(StyleService styleService, RGB background) {
//				// refresh to update legend images
//				refreshInDisplayThread();
//			}
//
//			@Override
//			public void styleSettingsChanged(StyleService styleService) {
//				// refresh to update legend images
//				refreshInDisplayThread();
//			}
//		});
		
//		MenuManager sourceMenuManager = new MenuManager();
//		sourceMenuManager.setRemoveAllWhenShown(true);
//		final IContributionItem sourceContextFunctions = new SchemaItemContribution(sourceSchemaViewer, false);
//		sourceMenuManager.addMenuListener(new IMenuListener() {
//
//			@Override
//			public void menuAboutToShow(IMenuManager manager) {
//				manager.add(sourceContextFunctions);
//			}
//			
//		});
//		
//		Menu sourceMenu = sourceMenuManager.createContextMenu(sourceSchemaViewer.getControl());
//		sourceSchemaViewer.getControl().setMenu(sourceMenu);
//		
//		MenuManager targetMenuManager = new MenuManager();
//		targetMenuManager.setRemoveAllWhenShown(true);
//		final IContributionItem targetContextFunctions = new SchemaItemContribution(targetSchemaViewer, true);
//		targetMenuManager.addMenuListener(new IMenuListener() {
//
//			@Override
//			public void menuAboutToShow(IMenuManager manager) {
//				manager.add(targetContextFunctions);
//			}
//			
//		});
//		
//		Menu targetMenu = targetMenuManager.createContextMenu(targetSchemaViewer.getControl());
//		targetSchemaViewer.getControl().setMenu(targetMenu);
//		
//		getSite().registerContextMenu(sourceMenuManager, sourceSchemaViewer);
//		getSite().registerContextMenu(targetMenuManager, targetSchemaViewer);

		// initialization of explorers
		sourceExplorer.setSchema(schemaService.getSchemas(SchemaSpaceID.SOURCE));
		targetExplorer.setSchema(schemaService.getSchemas(SchemaSpaceID.TARGET));
		
		// register selection provider
		getSite().setSelectionProvider(new SchemasSelectionProvider());
	}

//	/**
//	 * Refresh map in the display thread
//	 */
//	protected void refreshInDisplayThread() {
//		if (Display.getCurrent() != null) {
//			refresh();
//		}
//		else {
//			final Display display = PlatformUI.getWorkbench().getDisplay();
//			display.syncExec(new Runnable() {
//				
//				@Override
//				public void run() {
//					refresh();
//				}
//			});
//		}
//	}

//	private List<SimpleToggleAction> getToggleActions(PatternViewFilter pvf) {
//		List<SimpleToggleAction> result = new ArrayList<SimpleToggleAction>();
//		result.add(new SimpleToggleAction(TreeObjectType.ABSTRACT_FT, 
//				Messages.ModelNavigationView_2, Messages.ModelNavigationView_3,  //$NON-NLS-1$ //$NON-NLS-2$
//				"/icons/see_abstract_ft.png", pvf)); //$NON-NLS-1$
//		result.add(new SimpleToggleAction(TreeObjectType.PROPERTY_TYPE, 
//				Messages.ModelNavigationView_PropertyHide, Messages.ModelNavigationView_PropertyShow, 
//				"/icons/see_property_type.gif", pvf)); //$NON-NLS-1$
//		result.add(new SimpleToggleAction(TreeObjectType.STRING_ATTRIBUTE, 
//				Messages.ModelNavigationView_StringHide, Messages.ModelNavigationView_StringShow, 
//				"/icons/see_string_attribute.png", pvf)); //$NON-NLS-1$
//		result.add(new SimpleToggleAction(TreeObjectType.GEOMETRIC_ATTRIBUTE, 
//				Messages.ModelNavigationView_GeometryHide, Messages.ModelNavigationView_GeometryShow, 
//				"/icons/see_geometry_attribute.png", pvf)); //$NON-NLS-1$
//		result.add(new SimpleToggleAction(TreeObjectType.NUMERIC_ATTRIBUTE, 
//				Messages.ModelNavigationView_NumericHide, Messages.ModelNavigationView_NumericShow, 
//				"/icons/see_number_attribute.png", pvf)); //$NON-NLS-1$
//		return result;
//	}

//	private List<AbstractContentProviderAction> initSchemaExplorerToolBar(Composite modelComposite, 
//			PatternViewFilter pvf, List<SimpleToggleAction> toggleActions, String caption, String ident) {
//
//		Composite bar = new Composite(modelComposite, SWT.NONE);
//		GridLayout gridLayout = new GridLayout(2, false);
//		gridLayout.marginWidth = 0;
//		gridLayout.marginHeight = 0;
//		gridLayout.verticalSpacing = 0;
//		gridLayout.horizontalSpacing = 10;
//		bar.setLayout(gridLayout);
//		
//		Label captionLabel = new Label(bar, SWT.NONE);
//		captionLabel.setText(caption);
//
//		// create toolbar
//		ToolBar schemaFilterBar = new ToolBar(bar, SWT.FLAT
//				| SWT.WRAP);
//		schemaFilterBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//		
//		List<AbstractContentProviderAction> actions = new ArrayList<AbstractContentProviderAction>();
//		actions.add(new UseFlatHierarchyAction());
//		actions.add(new UseInheritanceHierarchyAction());
//		actions.add(new UseAggregationHierarchyAction());
//		
//		// default setting for actions 
//		actions.get(2).setChecked(true);
//
//		ToolBarManager manager = new ToolBarManager(schemaFilterBar);
//		for (AbstractContentProviderAction action : actions) {
//			manager.add(action);
//			action.setCaption(ident);
//		}
//		manager.add(new Separator());
//		for (SimpleToggleAction sta : toggleActions) {
//			manager.add(sta);
//			sta.setCaption(ident);
//		}
//		manager.update(false);
//		
//		return actions;
//	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// ignore
	}

//	/**
//	 * Update the viewers on changes to a schema
//	 * 
//	 * @param schema the schema that changed
//	 */
//	protected void update(SchemaSpaceID schema) {
//		switch (schema) {
//		case SOURCE:
//			sourceSchemaViewer.setInput(schemaItemService.getRoot(schema));
//			sourceSchemaViewer.refresh();
//			break;
//		case TARGET:
//			targetSchemaViewer.setInput(schemaItemService.getRoot(schema));
//			targetSchemaViewer.refresh();
//			break;
//		}
//	}

//	/**
//	 * Select the item with representing the given identifier
//	 * 
//	 * @param identifier the identifier
//	 */
//	public void selectItem(String identifier) {
//		TreeViewer tree;
//		SchemaItem item = schemaItemService.getSchemaItem(identifier, SchemaSpaceID.SOURCE);
//		if (item == null) {
//			item = schemaItemService.getSchemaItem(identifier, SchemaSpaceID.TARGET);
//			tree = targetSchemaViewer;
//		}
//		else {
//			tree = sourceSchemaViewer;
//		}
//		
//		if (item != null) {
//			tree.setSelection(new StructuredSelection(item), true);
//		}
//	}
	
	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (schemaListener != null) {
			schemaService.removeSchemaServiceListener(schemaListener);
		}
		
//		if (alignmentListener != null) {
//			AlignmentService as = (AlignmentService) getSite().getService(AlignmentService.class);
//			as.removeListener(alignmentListener);
//			StyleService styleService = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
//			styleService.removeListener(styleListener);
//		}
//		
//		if (functionImage != null) {
//			functionImage.dispose();
//		}
//		if (augmentImage != null) {
//			augmentImage.dispose();
//		}
		
		super.dispose();
	}

//	/**
//	 * Refresh both tree viewers
//	 */
//	public void refresh() {
//		sourceSchemaViewer.refresh();
//		targetSchemaViewer.refresh();
//	}

}