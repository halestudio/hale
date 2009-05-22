package eu.esdihumboldt.hale.rcp.views.model;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;

/**
 * This view component handles the display of source and target schemas.
 * 
 * @author Thorsten Reitz
 * @version {$Id}
 */
public class ModelNavigationView extends ViewPart implements
		HaleServiceListener {

	private static Logger _log = Logger.getLogger(ModelNavigationView.class);

	private static final String SOURCE_MODEL_ID = "source";
	private static final String TARGET_MODEL_ID = "target";

	/**
	 * Used to access the SchemaService.
	 */
	public static IWorkbenchPartSite site;

	public static final String ID = "eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView";

	private TreeViewer sourceSchemaViewer;
	private TreeViewer targetSchemaViewer;

	private Text sourceFilterText;
	private Text targetFilterText;

	/**
	 * A reference to the {@link SchemaService} which serves as model for this
	 * {@link ViewPart}.
	 */
	private SchemaService schemaService;

	@Override
	public void createPartControl(Composite _parent) {

		ModelNavigationView.site = this.getSite();

		schemaService = (SchemaService) this.getSite().getService(
				SchemaService.class);
		schemaService.addListener(this);

		Composite modelComposite = new Composite(_parent, SWT.BEGINNING);
		GridLayout layout = new GridLayout(2, true);
		layout.verticalSpacing = 6;
		layout.horizontalSpacing = 3;
		modelComposite.setLayout(layout);

		// source schema toolbar, filter and explorer
		Composite sourceComposite = new Composite(modelComposite, SWT.BEGINNING);
		sourceComposite.setLayout(new GridLayout(1, false));
		sourceComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		this.initSchemaExplorerToolBar(sourceComposite);
		this.sourceFilterText = new Text(sourceComposite, SWT.NONE | SWT.BORDER);
		this.sourceFilterText.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false));
		this.sourceFilterText.setText("");
		this.sourceSchemaViewer = this.schemaExplorerSetup(sourceComposite,
				schemaService.getSourceSchema(), SOURCE_MODEL_ID);
		this.sourceSchemaViewer.addFilter(new PatternViewFilter(
				this.sourceFilterText));
		this.sourceFilterText.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event e) {
				sourceSchemaViewer.refresh();
			}
		});

		// target schema toolbar, filter and explorer
		Composite targetComposite = new Composite(modelComposite, SWT.BEGINNING);
		targetComposite.setLayout(new GridLayout(1, false));
		targetComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		this.initSchemaExplorerToolBar(targetComposite);
		this.targetFilterText = new Text(targetComposite, SWT.NONE | SWT.BORDER);
		this.targetFilterText.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false));
		this.targetFilterText.setText("");
		this.targetSchemaViewer = this.schemaExplorerSetup(targetComposite,
				schemaService.getTargetSchema(), TARGET_MODEL_ID);
		this.targetSchemaViewer.addFilter(new PatternViewFilter(
				this.targetFilterText));
		this.targetFilterText.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event e) {
				targetSchemaViewer.refresh();
			}
		});
	}

	private void initSchemaExplorerToolBar(Composite modelComposite) {

		// create view forms
		ViewForm schemaViewForm = new ViewForm(modelComposite, SWT.NONE);

		// create toolbar
		ToolBar schemaFilterBar = new ToolBar(schemaViewForm, SWT.FLAT
				| SWT.WRAP);
		schemaViewForm.setTopRight(schemaFilterBar);

		ToolBarManager manager = new ToolBarManager(schemaFilterBar);
		manager.add(new UseInheritanceHierarchyAction());
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
			Collection<FeatureType> schema, final String targetViewName) {
		Composite viewerBComposite = new Composite(modelComposite, SWT.NONE);
		FillLayout fLayout = new FillLayout();
		viewerBComposite.setLayout(fLayout);
		GridData gData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL);
		gData.verticalSpan = 32;
		gData.grabExcessHorizontalSpace = true;
		gData.grabExcessVerticalSpace = true;
		gData.verticalIndent = 12;
		viewerBComposite.setLayoutData(gData);
		TreeViewer schemaViewer = new TreeViewer(viewerBComposite, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		schemaViewer.setContentProvider(new ModelContentProvider());
		schemaViewer.setLabelProvider(new ModelNavigationViewLabelProvider());
		schemaViewer.setInput(translateSchema(schema));

		schemaViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						updateAttributeView(targetViewName);
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
	private TreeObject translateSchema(Collection<FeatureType> schema) {
		if (schema == null || schema.size() == 0) {
			return new TreeParent("", TreeObjectType.ROOT);
		}

		// first, find out a few things about the schema to define the root
		// type.
		// TODO add metadata on schema here.
		// TODO is should be possible to attach attributive data for a flyout.
		TreeParent hidden_root = new TreeParent("ROOT", TreeObjectType.ROOT);
		TreeParent root = new TreeParent(schema.iterator().next().getName()
				.getNamespaceURI(), TreeObjectType.ROOT);
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
	 * @param type
	 *            the type to start the hierarchy from.
	 * @param typeHierarchy
	 *            the Map containing all subtypes for all FTs.
	 * @return a {@link TreeObject} that contains all Properties and all
	 *         subtypes and their property, starting with the given FT.
	 */
	private TreeObject buildSchemaTree(RobustFTKey ftk,
			Map<RobustFTKey, Set<FeatureType>> typeHierarchy) {
		TreeObjectType tot = TreeObjectType.CONCRETE_FT;
		if (ftk.getFeatureType().isAbstract()
				|| ftk.getFeatureType().getSuper() == null) {
			tot = TreeObjectType.ABSTRACT_FT;
		}
		TreeParent result = new TreeParent(ftk.getFeatureType().getName()
				.getLocalPart(), tot);
		// add properties
		for (PropertyDescriptor pd : ftk.getFeatureType().getDescriptors()) {
			tot = TreeObjectType.SIMPLE_ATTRIBUTE;
			if (pd.getType().toString().matches("^.*?GMLComplexTypes.*")) {
				tot = TreeObjectType.GEOMETRIC_ATTRIBUTE;
			} else if (Arrays.asList(pd.getType().getClass().getInterfaces())
					.contains(org.opengis.feature.type.GeometryType.class)) {
				tot = TreeObjectType.GEOMETRIC_ATTRIBUTE;
			} else if (Arrays.asList(pd.getType().getClass().getInterfaces())
					.contains(org.opengis.feature.type.ComplexType.class)) {
				tot = TreeObjectType.COMPLEX_ATTRIBUTE;
			}
			result.addChild(new TreeObject(pd.getName().getLocalPart() + ":"
					+ pd.getType().toString().replaceFirst("^.*?<", "<"), tot));
		}
		// add children recursively
		for (FeatureType ft : typeHierarchy.get(ftk)) {
			result.addChild(this.buildSchemaTree(new RobustFTKey(ft),
					typeHierarchy));
		}
		return result;
	}

	@Override
	public void setFocus() {

	}

	/**
	 * Update of AttributeList contents.
	 * 
	 * @param _viewer
	 *            =true selection changed in sourceSchemaViewer, else
	 *            targetSchemaViewer
	 */
	private void updateAttributeView(String targetViewName) {
		Tree tree;
		TreeItem selectedItem;
		AttributeView attributeView = null;
		// get All Views
		IViewReference[] views = this.getViewSite().getWorkbenchWindow()
				.getActivePage().getViewReferences();
		// get AttributeView
		for (int count = 0; count < views.length; count++) {
			if (views[count].getId().equals(
					"eu.esdihumboldt.hale.rcp.views.model.AttributeView")) {
				attributeView = (AttributeView) views[count].getView(false);
			}
		}

		if (targetViewName.equals(SOURCE_MODEL_ID)) {
			tree = sourceSchemaViewer.getTree();
			attributeView.clear(true);
		} else {
			tree = targetSchemaViewer.getTree();
			attributeView.clear(false);
		}

		if (tree.getSelection() != null && tree.getSelection().length > 0) {

			// set counter for the FeatureType to use for the attribure
			// declaration in the AttributeView
			int itemNumber = 0;
            boolean wasExpanded = true;
			// updates attribute view for each selected item in case multiple
			// selection
			for (TreeItem treeItem : tree.getSelection()) {
				itemNumber++;
				selectedItem = treeItem;

				

				// if selected Item is no attribute
				/*
				 * ap: if block returns always true if
				 * (!selectedItem.getImage().equals(
				 * PlatformUI.getWorkbench().getSharedImages().getImage(
				 * ISharedImages.IMG_OBJ_ELEMENT))) {
				 */
				// if selection changed in sourceSchemaViewer
				if (targetViewName.equals(SOURCE_MODEL_ID)) {
					// select all attributes of the feature type even if it is not
					// expand
					if (!selectedItem.getExpanded()) {
						selectedItem.setExpanded(true);
						sourceSchemaViewer.refresh();  
						wasExpanded = false;
					}
					
					// if not tree root
					if (!(selectedItem.getParentItem() == null)) {
						attributeView.updateView(true, selectedItem.getText(),
								selectedItem.getItems(), itemNumber);
					}
					if (!wasExpanded){
						selectedItem.setExpanded(false);
					    sourceSchemaViewer.refresh();  
					}    
				}
				// if selection changed in targetSchemaViewer
				else {
					// select all attributes of the feature type even if it is not
					// expand
					if (!selectedItem.getExpanded()) {
						selectedItem.setExpanded(true);
						targetSchemaViewer.refresh();  
						wasExpanded = false;
					}
					// if not tree root
					if (!(selectedItem.getParentItem() == null)) {
						attributeView.updateView(false, selectedItem.getText(),
								selectedItem.getItems(), itemNumber);
					}	
					if (!wasExpanded){
						selectedItem.setExpanded(false);
					    targetSchemaViewer.refresh();  
					}    
				}
               //collapse selected item  if was not expanded before selection
				
			}
		}
	}

	public void update() {
		this.sourceSchemaViewer.setInput(this.translateSchema(schemaService
				.getSourceSchema()));
		this.sourceSchemaViewer.refresh();
		this.targetSchemaViewer.setInput(this.translateSchema(schemaService
				.getTargetSchema()));
		this.targetSchemaViewer.refresh();
	}

}