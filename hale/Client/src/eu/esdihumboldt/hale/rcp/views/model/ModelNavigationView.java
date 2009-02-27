package eu.esdihumboldt.hale.rcp.views.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.impl.SchemaServiceImpl;
import eu.esdihumboldt.hale.models.impl.SchemaServiceMock;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;

/**
 * This view component handles the display of source and target schemas.
 * 
 * @author Thorsten Reitz
 * @version {$Id}
 */
public class ModelNavigationView extends ViewPart {

	public static final String ID = 
		"eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView";
	private TreeViewer sourceSchemaViewer;
	private TreeViewer targetSchemaViewer;

	@Override
	public void createPartControl(Composite _parent) {
		
		SchemaService schemaService = new SchemaServiceMock();

		Composite modelComposite = new Composite(_parent, SWT.BEGINNING);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 5;
		modelComposite.setLayout(layout);

		Combo sourceCombo = new Combo(modelComposite, SWT.NONE);
		GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gData.grabExcessVerticalSpace = true;
		sourceCombo.setLayoutData(gData);
		sourceCombo.setText("Choose model organization");
		sourceCombo.add("Inheritance hierarchy");
		sourceCombo.add("Aggregation hierarchy");

		Combo targetCombo = new Combo(modelComposite, SWT.NONE);
		gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gData.grabExcessVerticalSpace = true;
		targetCombo.setLayoutData(gData);
		targetCombo.setText("Choose model organization");
		targetCombo.add("Inheritance hierarchy");
		targetCombo.add("Aggregation hierarchy");

		// source schema explorer setup
		this.sourceSchemaViewer = this.schemaExplorerSetup(
				modelComposite, schemaService.getSourceSchema());
		this.targetSchemaViewer = this.schemaExplorerSetup(
				modelComposite, schemaService.getTargetSchema());
	}
	
	/**
	 * A helper method for setting up the two SchemaExplorers.
	 * @param modelComposite the parent {@link Composite} to use.
	 * @param schema the Schema to display.
	 * @return a {@link TreeViewer} with the currently loaded schema.
	 */
	private TreeViewer schemaExplorerSetup(Composite modelComposite, Collection<FeatureType> schema) {
		Composite viewerBComposite = new Composite(modelComposite, SWT.NONE);
		FillLayout fLayout = new FillLayout();
		viewerBComposite.setLayout(fLayout);
		GridData gData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL);
		gData.verticalSpan = 34;
		gData.grabExcessHorizontalSpace = true;
		gData.grabExcessVerticalSpace = true;
		viewerBComposite.setLayoutData(gData);
		TreeViewer schemaViewer = new TreeViewer(viewerBComposite, SWT.SINGLE
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		schemaViewer.setContentProvider(new ModelContentProvider());
		schemaViewer
				.setLabelProvider(new ModelNavigationViewLabelProvider());
		
		schemaViewer.setInput(translateSchema(schema));
		
		// must be expanded, because the whole tree must be instantiated
		schemaViewer.expandAll();
		schemaViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						updateAttributeView(false);
					}
				});
		return schemaViewer;
	}

	/**
	 * @param schema the {@link Collection} of {@link FeatureType}s that 
	 * represent the schema to display.
	 * @return
	 */
	private TreeObject translateSchema(Collection<FeatureType> schema) {
		
		if (schema == null || schema.size() == 0) {
			return new TreeParent("", TreeObjectType.ROOT);
		}
		// first, find out a few things about the schema to define the root type.
		// TODO add metadata on schema here.
		// TODO is should be possible to attach attributive data for a flyout.
		TreeParent hidden_root = new TreeParent("ROOT", TreeObjectType.ROOT);
		TreeParent root = new TreeParent(
				schema.iterator().next().getName().getNamespaceURI(), 
				TreeObjectType.ROOT);
		hidden_root.addChild(root);
		
		// build the tree of FeatureTypes, starting from those types which 
		// don't have a supertype. 
		Map<FeatureType, Set<FeatureType>> typeHierarchy = 
			new HashMap<FeatureType, Set<FeatureType>>();
		// first, put all FTs in the Map, with an empty Set of subtypes.
		for (FeatureType ft : schema) {
			typeHierarchy.put(ft, new HashSet<FeatureType>());
		}
		// second, walk all FTs and register them as subtypes to their supertypes.
		for (FeatureType ft : schema) {
			if (ft.getSuper() != null) {
				Set<FeatureType> subtypes = typeHierarchy.get(ft.getSuper());
				System.out.println(ft.getSuper().getName().getNamespaceURI());
				if (subtypes != null) {
					subtypes.add(ft);
				}
			}
		}
		// finally, build the tree, starting with those types that don't have supertypes.
		for (FeatureType ft : schema) {
			if (ft.getSuper() == null) {
				root.addChild(this.buildSchemaTree(ft, typeHierarchy));
			}
		}
		
		// TODO show references to Properties which are FTs already added as links.
		return hidden_root;
	}
	
	/**
	 * Recursive method for setting up the inheritance tree.
	 * @param type the type to start the hierarchy from.
	 * @param typeHierarchy the Map containing all subtypes for all FTs.
	 * @return a {@link TreeObject} that contains all Properties and all 
	 * subtypes and their property, starting with the given FT.
	 */
	private TreeObject buildSchemaTree(
			FeatureType type, Map<FeatureType, Set<FeatureType>> typeHierarchy) {
		TreeObjectType tot = TreeObjectType.CONCRETE_FT;
		if (type.isAbstract()) {
			tot = TreeObjectType.ABSTRACT_FT;
		}
		TreeParent result = new TreeParent(type.getName().getLocalPart(), tot);
		// add properties
		for (PropertyDescriptor pd : type.getDescriptors()) {
			System.out.println(pd.getType().getClass());
			tot = TreeObjectType.SIMPLE_ATTRIBUTE;
			if (Arrays.asList(pd.getType().getClass().getInterfaces()).contains(
					org.opengis.feature.type.GeometryType.class)) {
				tot = TreeObjectType.GEOMETRIC_ATTRIBUTE;
			}
			else if (Arrays.asList(pd.getType().getClass().getInterfaces()).contains(
					org.opengis.feature.type.ComplexType.class)) {
				tot = TreeObjectType.COMPLEX_ATTRIBUTE;
			}
			result.addChild(
					new TreeObject(pd.getName().getLocalPart() + ":" 
							+ pd.getType().toString().replaceFirst(
									"^.*?<", "<"), tot));
		}
		// add children recursively
		for (FeatureType ft : typeHierarchy.get(type)) {
			result.addChild(this.buildSchemaTree(ft, typeHierarchy));
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
	private void updateAttributeView(boolean _viewer) {
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

		if (_viewer) {
			tree = sourceSchemaViewer.getTree();
			attributeView.clear(true);
		} else {
			tree = targetSchemaViewer.getTree();
			attributeView.clear(false);
		}

		if (tree.getSelection() != null && tree.getSelection().length > 0) {
			selectedItem = tree.getSelection()[0];

			// if selected Item is no attribute
			if (!selectedItem.getImage().equals(
					PlatformUI.getWorkbench().getSharedImages().getImage(
							ISharedImages.IMG_OBJ_ELEMENT))) {
				// if selection changed in sourceSchemaViewer
				if (_viewer) {
					// if not tree root
					if (!selectedItem.getText().equals("ROOT")) { // FIXME
						attributeView.updateView(true, selectedItem.getText(),
								selectedItem.getItems());
					}
				}
				// if selection changed in targetSchemaViewer
				else {
					// if not tree root
					if (!selectedItem.getText().equals("ROOT")) { // FIXME
						attributeView.updateView(false, selectedItem.getText(),
								selectedItem.getItems());
					}
				}
			}
		}
	}

}