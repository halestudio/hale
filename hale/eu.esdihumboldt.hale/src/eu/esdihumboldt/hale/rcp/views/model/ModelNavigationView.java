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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.map.style.FeatureTypeStyleAction;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;
import eu.esdihumboldt.hale.rcp.views.model.dialogs.PropertiesAction;
import eu.esdihumboldt.hale.rcp.views.model.filtering.AbstractContentProviderAction;
import eu.esdihumboldt.hale.rcp.views.model.filtering.PatternViewFilter;
import eu.esdihumboldt.hale.rcp.views.model.filtering.SimpleToggleAction;
import eu.esdihumboldt.hale.rcp.views.model.filtering.UseAggregationHierarchyAction;
import eu.esdihumboldt.hale.rcp.views.model.filtering.UseFlatHierarchyAction;
import eu.esdihumboldt.hale.rcp.views.model.filtering.UseInheritanceHierarchyAction;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardContribution;
import eu.esdihumboldt.tools.RobustFTKey;

/**
 * This view component handles the display of source and target schemas.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @version $Id$
 */
public class ModelNavigationView extends ViewPart implements
		HaleServiceListener, ISelectionProvider{

	/**
	 * Context menu contribution
	 */
	private static class SchemaItemContribution extends
			FunctionWizardContribution {
		
		private final TreeViewer tree;

		/**
		 * Create a new contribution
		 * 
		 * @param tree the tree for retrieving the selected item
		 * 
		 * @param showAugmentations if augmentations shall be shown in the menu
		 */
		public SchemaItemContribution(TreeViewer tree, boolean showAugmentations) {
			super(showAugmentations);
			
			this.tree = tree;
		}

		/**
		 * @see FunctionWizardContribution#fill(Menu, int)
		 */
		@Override
		public void fill(Menu menu, int index) {
			if (tree.getSelection() instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection) tree.getSelection();
				Object tmp = selection.getFirstElement();
				if (tmp != null && tmp instanceof SchemaItem) {
					SchemaItem item = (SchemaItem) tmp;
					boolean addSep = false;
					
					// properties
					if (item.isType() || item.isAttribute()) {
						IAction action = new PropertiesAction(item);
						IContributionItem contrib = new ActionContributionItem(action);
						contrib.fill(menu, index++);
						
						addSep = true;
					}
					
					// SLD
					if (item.isFeatureType() && item.getPropertyType() instanceof FeatureType
							&& !((FeatureType) item.getPropertyType()).isAbstract()) {
						IAction action = new FeatureTypeStyleAction((FeatureType) item.getPropertyType());
						action.setText("Edit style...");
						action.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
								HALEActivator.PLUGIN_ID, "/icons/styles.gif"));
						IContributionItem contrib = new ActionContributionItem(action);
						contrib.fill(menu, index++);
						
						addSep = true;
					}
					
					if (addSep) {
						new Separator().fill(menu, index++);
					}
				}
			}
			
			super.fill(menu, index);
		}

	}

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

	private static Logger _log = Logger.getLogger(ModelNavigationView.class);
	
	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView";

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
	private SchemaService schemaService;

	/**
	 * The selection listeners
	 */
	private final Set<ISelectionChangedListener> listeners = new HashSet<ISelectionChangedListener>();

	private ISelection currentSelection;
	
	private Image functionImage;
	
	private Image augmentImage;

	/**
	 * @see WorkbenchPart#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite _parent) {
		// get schema service
		schemaService = (SchemaService) this.getSite().getService(
				SchemaService.class);
		schemaService.addListener(this);
		
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
				sourceToggleActions);

		this.sourceSchemaViewer = this.schemaExplorerSetup(sourceComposite,
				schemaService.getSourceSchema(), schemaService.getSourceNameSpace(),
				SchemaType.SOURCE);
		this.sourceSchemaViewer.addFilter(sourceSchemaFilter);
		
		for (AbstractContentProviderAction cpa : sourceContentActions) {
			cpa.setViewer(sourceSchemaViewer);
		}

		for (SimpleToggleAction sta : sourceToggleActions) {
			sta.setActionTarget(this.sourceSchemaViewer);
		}
		
		// function button
		final Button functionButton = new Button(modelComposite, SWT.PUSH | SWT.FLAT);
		functionImage = HALEActivator.getImageDescriptor("icons/mapping.gif").createImage();
		augmentImage = HALEActivator.getImageDescriptor("icons/augment.gif").createImage();
		functionButton.setImage(functionImage);
		functionButton.setToolTipText("Select a mapping function");
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
				targetToggleActions);

		this.targetSchemaViewer = this.schemaExplorerSetup(targetComposite,
				schemaService.getTargetSchema(), schemaService.getTargetNameSpace(), 
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
		as.addListener(new HaleServiceListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(UpdateMessage message) {
				if (Display.getCurrent() != null) {
					sourceSchemaViewer.refresh();
					targetSchemaViewer.refresh();
				}
				else {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {
						
						@Override
						public void run() {
							sourceSchemaViewer.refresh();
							targetSchemaViewer.refresh();
						}
					});
				}
			}
			
		});
		
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
		result.add(new SimpleToggleAction(TreeObjectType.PROPERTY_TYPE, 
				"Hide Property Types", "Show Property Types", 
				"/icons/placeholder.gif", pvf));
		result.add(new SimpleToggleAction(TreeObjectType.STRING_ATTRIBUTE, 
				"Hide String Attributes", "Show String Attributes", 
				"/icons/see_string_attribute.png", pvf));
		result.add(new SimpleToggleAction(TreeObjectType.GEOMETRIC_ATTRIBUTE, 
				"Hide Geometry Attributes", "Show Geometry Attributes", 
				"/icons/see_geometry_attribute.png", pvf));
		result.add(new SimpleToggleAction(TreeObjectType.NUMERIC_ATTRIBUTE, 
				"Hide Numeric Attributes", "Show Numeric Attributes", 
				"/icons/see_number_attribute.png", pvf));
		return result;
	}

	private List<AbstractContentProviderAction> initSchemaExplorerToolBar(Composite modelComposite, 
			PatternViewFilter pvf, List<SimpleToggleAction> toggleActions) {

		// create view forms
		ViewForm schemaViewForm = new ViewForm(modelComposite, SWT.NONE);

		// create toolbar
		ToolBar schemaFilterBar = new ToolBar(schemaViewForm, SWT.FLAT
				| SWT.WRAP);
		schemaViewForm.setTopRight(schemaFilterBar);
		
		List<AbstractContentProviderAction> actions = new ArrayList<AbstractContentProviderAction>();
		actions.add(new UseInheritanceHierarchyAction());
		actions.add(new UseAggregationHierarchyAction());
		actions.add(new UseFlatHierarchyAction());
		
		// default
		actions.get(0).setChecked(true);

		ToolBarManager manager = new ToolBarManager(schemaFilterBar);
		for (AbstractContentProviderAction action : actions) {
			manager.add(action);
		}
		manager.add(new Separator());
		for (SimpleToggleAction sta : toggleActions) {
			manager.add(sta);
		}
		manager.update(false);
		
		return actions;
	}

	/**
	 * A helper method for setting up the two SchemaExplorers.
	 * 
	 * @param modelComposite
	 *            the parent {@link Composite} to use.
	 * @param schema
	 *            the Schema to display.
	 * @param namespace the namespace
	 * @param viewer the viewer type
	 * @return a {@link TreeViewer} with the currently loaded schema.
	 */
	private TreeViewer schemaExplorerSetup(Composite modelComposite,
			Collection<FeatureType> schema, String namespace, final SchemaType viewer) {
		PatternFilter patternFilter = new PatternFilter();
	    final FilteredTree filteredTree = new FilteredTree(modelComposite, SWT.MULTI
	            | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter, true);
	    TreeViewer schemaViewer = filteredTree.getViewer();
	    // set the default content provider
		schemaViewer.setContentProvider(new InheritanceContentProvider());
		schemaViewer.setLabelProvider(new ModelNavigationViewLabelProvider());
		schemaViewer.setInput(translateSchema(schema, namespace));
        schemaViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						updateSelection();
					}
				});
		return schemaViewer;
	}

	/**
	 * @param schema
	 *            the {@link Collection} of {@link FeatureType}s that represent
	 *            the schema to display.
	 * @param namespace the namespace
	 * 
	 * @return the root item
	 */
	private SchemaItem translateSchema(Collection<FeatureType> schema, String namespace) {
		if (schema == null || schema.size() == 0) {
			return new TreeParent("", null, TreeObjectType.ROOT, null);
		}

		// first, find out a few things about the schema to define the root
		// type.
		// TODO add metadata on schema here.
		// TODO is should be possible to attach attributive data for a flyout.
		TreeParent hidden_root = new TreeParent("ROOT", null, TreeObjectType.ROOT, null);
		TreeParent root = new TreeParent(namespace, null, TreeObjectType.ROOT, null);
		hidden_root.addChild(root);

		// build the tree of FeatureTypes, starting from those types which
		// don't have a supertype.
		Map<RobustFTKey, Set<FeatureType>> typeHierarchy = new HashMap<RobustFTKey, Set<FeatureType>>();

		// first, put all FTs in the Map, with an empty Set of subtypes.
		for (FeatureType ft : schema) {
			typeHierarchy.put(new RobustFTKey(ft), new HashSet<FeatureType>());
		}

		// second, walk all FTs and register them as subtypes to their
		// supertypes.
		for (RobustFTKey ftk : typeHierarchy.keySet()) {
			if (ftk.getFeatureType().getSuper() != null) {
				Set<FeatureType> subtypes = typeHierarchy.get(new RobustFTKey(
						(FeatureType) ftk.getFeatureType().getSuper()));
				if (subtypes != null) {
					subtypes.add(ftk.getFeatureType());
					_log.debug("Supertype was added: "
							+ ftk.getFeatureType().getSuper());
				} else {
					_log.warn("Subtypes-Set was null. Supertype should have "
							+ "been added, but wasn't, probably because of an "
							+ "unstable Feature Name + Namespace.");
				}
			}
		}
		// finally, build the tree, starting with those types that don't have
		// supertypes.
		for (RobustFTKey ftk : typeHierarchy.keySet()) {
			if (ftk.getFeatureType().getSuper() == null) {
				root.addChild(this.buildSchemaTree(ftk, typeHierarchy));
			}
		}

		// TODO show references to Properties which are FTs already added as
		// links.
		return hidden_root;
	}
	
	/**
	 * Recursive method for setting up the inheritance tree.
	 * 
	 * @param ftk
	 *            a {@link RobustFTKey} identifying the type to start the 
	 *            hierarchy from.
	 * @param typeHierarchy
	 *            the Map containing all subtypes for all FTs.
	 * @return a {@link TreeObject} that contains all Properties and all
	 *         subtypes and their property, starting with the given FT.
	 */
	private TreeObject buildSchemaTree(RobustFTKey ftk,
			Map<RobustFTKey, Set<FeatureType>> typeHierarchy) {
		FeatureTypeItem featureItem = new FeatureTypeItem(ftk.getFeatureType());
		
		// add properties
		addProperties(featureItem, ftk.getFeatureType());
		
		// add children recursively
		for (FeatureType ft : typeHierarchy.get(ftk)) {
			featureItem.addChild(this.buildSchemaTree(new RobustFTKey(ft),
					typeHierarchy));
		}
		return featureItem;
	}

	/**
	 * Add properties of the given feature type to the given tree parent
	 * 
	 * @param parent the tree parent
	 * @param featureType the feature type
	 */
	private static void addProperties(TreeParent parent, FeatureType featureType) {
		for (PropertyDescriptor pd : featureType.getDescriptors()) {
			PropertyItem property = new PropertyItem(pd);
			
			if (pd.getType() instanceof FeatureType) {
				addProperties(property, (FeatureType) pd.getType());
			}
			
			parent.addChild(property);
		}
	}

	@Override
	public void setFocus() {

	}

	/**
	 * @see HaleServiceListener#update(UpdateMessage)
	 */
	@SuppressWarnings("unchecked")
	public void update(UpdateMessage message) {
		if (Display.getCurrent() != null) {
			update();
		}
		else {
			final Display display = PlatformUI.getWorkbench().getDisplay();
			display.syncExec(new Runnable() {
				
				@Override
				public void run() {
					update();
				}
			});
		}
	}
	
	private void update() {
		this.sourceSchemaViewer.setInput(this.translateSchema(schemaService
				.getSourceSchema(), schemaService.getSourceNameSpace()));
		this.sourceSchemaViewer.refresh();
		this.targetSchemaViewer.setInput(this.translateSchema(schemaService
				.getTargetSchema(), schemaService.getTargetNameSpace()));
		this.targetSchemaViewer.refresh();
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
		if (functionImage != null) {
			functionImage.dispose();
		}
		if (augmentImage != null) {
			augmentImage.dispose();
		}
		
		super.dispose();
	}


}