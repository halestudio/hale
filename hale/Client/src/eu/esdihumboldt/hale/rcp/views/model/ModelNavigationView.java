package eu.esdihumboldt.hale.rcp.views.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
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
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;
import org.geotools.feature.NameImpl;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.PropertyDescriptorImpl;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.FeatureTypeHelper;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;
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
	 * Function contribution that always uses this view's selection
	 */
	private class SchemaFunctionContribution extends FunctionWizardContribution {

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
		this.initSchemaExplorerToolBar(sourceComposite, sourceSchemaFilter, 
				sourceToggleActions);

		this.sourceSchemaViewer = this.schemaExplorerSetup(sourceComposite,
				schemaService.getSourceSchema(), schemaService.getSourceNameSpace(),
				SchemaType.SOURCE);
		this.sourceSchemaViewer.addFilter(sourceSchemaFilter);

		for (SimpleToggleAction sta : sourceToggleActions) {
			sta.setActionTarget(this.sourceSchemaViewer);
		}
		
		// function button
		final Button functionButton = new Button(modelComposite, SWT.PUSH | SWT.FLAT);
		functionImage = HALEActivator.getImageDescriptor("icons/mapping.gif").createImage();
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
			}
			
		});

		// target schema toolbar, filter and explorer
		Composite targetComposite = new Composite(modelComposite, SWT.BEGINNING);
		targetComposite.setLayout(new GridLayout(1, false));
		targetComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		this.initSchemaExplorerToolBar(targetComposite, targetSchemaFilter, 
				targetToggleActions);

		this.targetSchemaViewer = this.schemaExplorerSetup(targetComposite,
				schemaService.getTargetSchema(), schemaService.getTargetNameSpace(), 
				SchemaType.TARGET);
		
		this.targetSchemaViewer.addFilter(targetSchemaFilter);

		for (SimpleToggleAction sta : targetToggleActions) {
			sta.setActionTarget(this.targetSchemaViewer);
		}
		
		// redraw on alignment change
		AlignmentService as = (AlignmentService) getSite().getService(AlignmentService.class);
		as.addListener(new HaleServiceListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(UpdateMessage message) {
				sourceSchemaViewer.refresh();
				targetSchemaViewer.refresh();
				
				/*XXX seems to be not enough to update the label colors - sourceSchemaViewer.getControl().redraw();
				targetSchemaViewer.getControl().redraw();*/
			}
			
		});
		
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		final IContributionItem contextFunctions = new FunctionWizardContribution();
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(contextFunctions);
			}
			
		});
		
		Menu sourceMenu = menuManager.createContextMenu(sourceSchemaViewer.getControl());
		sourceSchemaViewer.getControl().setMenu(sourceMenu);
		
		Menu targetMenu = menuManager.createContextMenu(targetSchemaViewer.getControl());
		targetSchemaViewer.getControl().setMenu(targetMenu);
		
		getSite().registerContextMenu(menuManager, this);
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

	private void initSchemaExplorerToolBar(Composite modelComposite, 
			PatternViewFilter pvf, List<SimpleToggleAction> toggleActions) {

		// create view forms
		ViewForm schemaViewForm = new ViewForm(modelComposite, SWT.NONE);

		// create toolbar
		ToolBar schemaFilterBar = new ToolBar(schemaViewForm, SWT.FLAT
				| SWT.WRAP);
		schemaViewForm.setTopRight(schemaFilterBar);

		ToolBarManager manager = new ToolBarManager(schemaFilterBar);
		manager.add(new UseInheritanceHierarchyAction());
		manager.add(new UseAggregationHierarchyAction());
		manager.add(new UseFlatHierarchyAction());
		manager.add(new Separator());
		for (SimpleToggleAction sta : toggleActions) {
			manager.add(sta);
		}
		manager.update(false);
	}

	/**
	 * A helper method for setting up the two SchemaExplorers.
	 * 
	 * @param modelComposite
	 *            the parent {@link Composite} to use.
	 * @param schema
	 *            the Schema to display.
	 * @return a {@link TreeViewer} with the currently loaded schema.
	 */
	private TreeViewer schemaExplorerSetup(Composite modelComposite,
			Collection<FeatureType> schema, String namespace, final SchemaType viewer) {
		PatternFilter patternFilter = new PatternFilter();
	    final FilteredTree filteredTree = new FilteredTree(modelComposite, SWT.MULTI
	            | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter);
	    TreeViewer schemaViewer = filteredTree.getViewer();
		schemaViewer.setContentProvider(new ModelContentProvider());
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
	 * @return
	 */
	private SchemaItem translateSchema(Collection<FeatureType> schema, String namespace) {
		if (schema == null || schema.size() == 0) {
			return new TreeParent("", null, TreeObjectType.ROOT);
		}

		// first, find out a few things about the schema to define the root
		// type.
		// TODO add metadata on schema here.
		// TODO is should be possible to attach attributive data for a flyout.
		TreeParent hidden_root = new TreeParent("ROOT", null, TreeObjectType.ROOT);
		TreeParent root = new TreeParent(namespace, null, TreeObjectType.ROOT);
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
	
	@SuppressWarnings("unchecked")
	private boolean checkInterface(Class<?>[] classes, Class classToFind) {
		for (Class clazz : classes) {
			if (clazz.equals(classToFind)) return true;
			for (Class c : clazz.getInterfaces()) {
				if (c.equals(classToFind)) return true;
			}
		}
		return false;
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
		TreeObjectType tot = TreeObjectType.CONCRETE_FT;
		if (FeatureTypeHelper.isPropertyType(ftk.getFeatureType())) {
			tot = TreeObjectType.PROPERTY_TYPE;
		}
		else if (FeatureTypeHelper.isAbstract(ftk.getFeatureType())) {
			tot = TreeObjectType.ABSTRACT_FT;
		}
		TreeParent result = new TreeParent(ftk.getFeatureType().getName()
				.getLocalPart(), ftk.getFeatureType().getName(), tot);
		
		String ftName = ftk.getFeatureType().getName().getNamespaceURI();
		
		// add properties
		for (PropertyDescriptor pd : ftk.getFeatureType().getDescriptors()) {
			tot = getAttributeType(pd);
			result.addChild(new TreeObject(pd.getName().getLocalPart() + ":<"
					+ pd.getType().getName().getLocalPart() + ">", 
					pd.getName(), tot));
		}
		// add children recursively
		for (FeatureType ft : typeHierarchy.get(ftk)) {
			result.addChild(this.buildSchemaTree(new RobustFTKey(ft),
					typeHierarchy));
		}
		return result;
	}

	/**
	 * Get the tree object type for an attribute
	 * 
	 * @param pd the attribute's property descriptor
	 * @return the tree object type of the attribute
	 */
	protected TreeObjectType getAttributeType(PropertyDescriptor pd) {
		PropertyType type = pd.getType();
		Class<?> binding = type.getBinding();
		
		if (type.toString().matches("^.*?GMLComplexTypes.*")) {
//		if (pd.getType().getName().getNamespaceURI().equals("http://www.opengis.net/gml")) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (Arrays.asList(binding.getClass().getInterfaces())
				.contains(org.opengis.feature.type.GeometryType.class)) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (checkInterface(binding.getInterfaces(),
				com.vividsolutions.jts.geom.Puntal.class)) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (checkInterface(binding.getInterfaces(),
				com.vividsolutions.jts.geom.Polygonal.class)) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (checkInterface(binding.getInterfaces(),
				com.vividsolutions.jts.geom.Lineal.class)) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		}
		// numeric
		else if (Number.class.isAssignableFrom(binding) || Date.class.isAssignableFrom(binding)) {
			return TreeObjectType.NUMERIC_ATTRIBUTE;
		}
		// string
		else if (String.class.isAssignableFrom(binding)) {
			return TreeObjectType.STRING_ATTRIBUTE;
		}
		// boolean
		else if (Boolean.class.isAssignableFrom(binding)) {
			return TreeObjectType.STRING_ATTRIBUTE; //TODO new attribute type?
		}
		// default geometry attribute
		else if (pd.getName().getLocalPart().equalsIgnoreCase("geometry") ||
				pd.getName().getLocalPart().equalsIgnoreCase("the_geom")) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		}
		else if (Arrays.asList(type.getClass().getInterfaces())
				.contains(org.opengis.feature.type.ComplexType.class)) {
			return TreeObjectType.COMPLEX_ATTRIBUTE;
		}
		// collection
		else if (Collection.class.isAssignableFrom(binding)) {
			return TreeObjectType.COMPLEX_ATTRIBUTE;
		}
		
		// default to complex attribute
		return TreeObjectType.COMPLEX_ATTRIBUTE;
	}

	@Override
	public void setFocus() {

	}

	/**
	 * @see HaleServiceListener#update(UpdateMessage)
	 */
	@SuppressWarnings("unchecked")
	public void update(UpdateMessage message) {
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
		
		super.dispose();
	}


}