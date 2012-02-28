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

package eu.esdihumboldt.hale.ui.functions.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypePropertyContentProvider;

/**
 * Parameter page for merge function. Provides an UI for selecting the matching
 * properties.
 * 
 * @author Kai Schwierczek
 */
public class MergeParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements ParameterPage {
	private static final String PARAMETER_PROPERTY = "property";
	private static final String PARAMETER_ADDITIONAL_PROPERTY = "additional_property";	

	private FunctionParameter parameter;
	private List<String> initialSelection;
	private CheckboxTreeViewer viewer;
	private TypeDefinition sourceType;
	private Set<EntityDefinition> selection = new HashSet<EntityDefinition>();
	private Set<EntityDefinition> filtered = new HashSet<EntityDefinition>();
	private DefinitionLabelProvider labelProvider = new DefinitionLabelProvider();

	/**
	 * Constructor.
	 */
	public MergeParameterPage() {
		super("propertypage");
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		setPageComplete(true);

		Cell unfinishedCell = getWizard().getUnfinishedCell();
		// selected target could've changed!
		TypeDefinition newSourceType = (TypeDefinition) unfinishedCell.getSource().values().iterator().next()
				.getDefinition().getDefinition();
		if (!newSourceType.equals(sourceType)) {
			selection = new HashSet<EntityDefinition>();
			sourceType = newSourceType;
			viewer.setInput(sourceType);
		}
		// for additional_property: selected properties can change!
		if (parameter.getName().equals(PARAMETER_ADDITIONAL_PROPERTY)) {
			filtered = new HashSet<EntityDefinition>();
			List<String> properties = unfinishedCell.getTransformationParameters().get(PARAMETER_PROPERTY);
			boolean oldSelectionChanged = false;
			for (String propertyPath : properties) {
				EntityDefinition def = getEntityDefinition(propertyPath, sourceType);
				filtered.add(def);
				if (selection.remove(def))
					oldSelectionChanged = true;
			}
			if (oldSelectionChanged)
				viewer.setCheckedElements(selection.toArray());
			viewer.refresh();
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#setParameter(java.util.Set,
	 *      com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameter(Set<FunctionParameter> params, ListMultimap<String, String> initialValues) {
		// this page is only for parameter property, ignore params
		if (initialValues == null)
			return;

		if (params.size() > 1)
			throw new IllegalArgumentException("MergeParameterPage is only for one parameter");
		parameter = params.iterator().next();
		if (parameter.getName().equals(PARAMETER_PROPERTY))
			setTitle("Please select the properties that have to match");
		else if (parameter.getName().equals(PARAMETER_ADDITIONAL_PROPERTY))
			setTitle("Please select other equal properties to merge");
		else
			throw new IllegalArgumentException(
					"MergeParameterPage is only for property or additional_property parameters");

		// cell gets edited
		initialSelection = initialValues.get(parameter.getName());
		setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, String> getConfiguration() {
		ListMultimap<String, String> configuration = ArrayListMultimap.create();
		for (EntityDefinition selected : selection) {
			// build property path (QNames separated by .)
			String propertyPath = Joiner.on('.').join(
					Collections2.transform(selected.getPropertyPath(), new Function<ChildContext, String>() {
						@Override
						public String apply(ChildContext input) {
							return input.getChild().getName().toString();
						}
					}));

			// add it to configuration
			configuration.put(parameter.getName(), propertyPath);
		}

		return configuration;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		// set layout of page
		page.setLayout(new GridLayout());

		Label name = new Label(page, SWT.NONE);
		name.setText(parameter.getDisplayName());

		// create checkbox tree viewer
		viewer = new CheckboxTreeViewer(page, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		// comparator
		viewer.setComparator(new DefinitionComparator());
		// label provider
		viewer.setLabelProvider(labelProvider);
		// content provider
		EntityDefinitionService entityDefinitionService = (EntityDefinitionService) PlatformUI.getWorkbench()
				.getService(EntityDefinitionService.class);
		viewer.setContentProvider(new EntityTypePropertyContentProvider(viewer, entityDefinitionService,
				SchemaSpaceID.SOURCE));
		// check state listener
		viewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				// add/remove it from/to set of selected properties
				EntityDefinition eventSource = (EntityDefinition) event.getElement();
				if (event.getChecked())
					selection.add(eventSource);
				else
					selection.remove(eventSource);
			}
		});

		// for now filter everything after first level
		viewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return parentElement == sourceType;
			}
		});
		if (parameter.getName().equals(PARAMETER_ADDITIONAL_PROPERTY))
			viewer.addFilter(new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					return !filtered.contains(element);
				}
			});

		Cell unfinishedCell = getWizard().getUnfinishedCell();
		if (unfinishedCell.getSource() != null)
			sourceType = (TypeDefinition) unfinishedCell.getSource().values().iterator().next().getDefinition()
					.getDefinition();

		viewer.setInput(sourceType);

		// add initial selection
		if (sourceType != null && initialSelection != null) {
			for (String propertyPath : initialSelection) {
				ArrayList<ChildContext> contextPath = new ArrayList<ChildContext>();
				List<QName> path = PropertyResolver.getQNamesFromPath(propertyPath);
				Iterator<QName> iter = path.iterator();
				ChildDefinition<?> child = sourceType.getChild(iter.next());
				contextPath.add(new ChildContext(child));
				while (iter.hasNext()) {
					child = DefinitionUtil.getChild(child, iter.next());
					contextPath.add(new ChildContext(child));
				}
				selection.add(getEntityDefinition(propertyPath, sourceType));
			}
			viewer.setCheckedElements(selection.toArray());
		}
	}

	private EntityDefinition getEntityDefinition(String propertyPath, TypeDefinition sourceType) {
		ArrayList<ChildContext> contextPath = new ArrayList<ChildContext>();
		List<QName> path = PropertyResolver.getQNamesFromPath(propertyPath);
		Iterator<QName> iter = path.iterator();
		ChildDefinition<?> child = sourceType.getChild(iter.next());
		contextPath.add(new ChildContext(child));
		while (iter.hasNext()) {
			child = DefinitionUtil.getChild(child, iter.next());
			contextPath.add(new ChildContext(child));
		}
		return AlignmentUtil.createEntity(sourceType, contextPath, SchemaSpaceID.SOURCE, null);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#dispose()
	 */
	@Override
	public void dispose() {
		labelProvider.dispose();
		super.dispose();
	}
}
