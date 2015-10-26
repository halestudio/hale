/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.functions.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.PropertyPathContentProvider;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * Parameter page for merge function. Provides an UI for selecting the matching
 * properties.
 * 
 * @author Kai Schwierczek
 */
public class MergeParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>>
		implements ParameterPage {

	private static final ALogger log = ALoggerFactory.getLogger(MergeParameterPage.class);

	private static final String PARAMETER_PROPERTY = "property";
	private static final String PARAMETER_ADDITIONAL_PROPERTY = "additional_property";

	private FunctionParameterDefinition parameter;
	private List<String> initialSelection;
	private CheckboxTreeViewer viewer;
	private TypeDefinition sourceType;
	private Set<EntityDefinition> selection = new HashSet<EntityDefinition>();
	private Set<EntityDefinition> filtered = new HashSet<EntityDefinition>();
	private final DefinitionLabelProvider labelProvider = new DefinitionLabelProvider(null);

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
		TypeDefinition newSourceType = (TypeDefinition) unfinishedCell.getSource().values()
				.iterator().next().getDefinition().getDefinition();
		if (!newSourceType.equals(sourceType)) {
			selection = new HashSet<EntityDefinition>();
			sourceType = newSourceType;
			viewer.setInput(sourceType);
		}
		// for additional_property: selected properties can change!
		if (parameter.getName().equals(PARAMETER_ADDITIONAL_PROPERTY)) {
			filtered = new HashSet<EntityDefinition>();
			List<ParameterValue> properties = unfinishedCell.getTransformationParameters().get(
					PARAMETER_PROPERTY);
			boolean oldSelectionChanged = false;
			for (ParameterValue propertyPath : properties) {
				EntityDefinition def = getEntityDefinition(propertyPath.as(String.class),
						sourceType);
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
	public void setParameter(Set<FunctionParameterDefinition> params,
			ListMultimap<String, ParameterValue> initialValues) {
		if (params.size() > 1)
			throw new IllegalArgumentException("MergeParameterPage is only for one parameter");
		parameter = params.iterator().next();
		if (parameter.getName().equals(PARAMETER_PROPERTY)) {
			setTitle("Please select the properties that have to match");
			setDescription("Instances that have equal values for these properties will be merged into one");
		}
		else if (parameter.getName().equals(PARAMETER_ADDITIONAL_PROPERTY)) {
			setTitle("Please select other equal properties to merge");
			setDescription("For these properties only the unique values will be retained in the merged instance");
		}
		else
			throw new IllegalArgumentException(
					"MergeParameterPage is only for property or additional_property parameters");

		if (initialValues != null) {
			// cell gets edited
			List<ParameterValue> tmp = initialValues.get(parameter.getName());
			if (tmp != null) {
				initialSelection = new ArrayList<String>(tmp.size());
				for (ParameterValue value : tmp)
					initialSelection.add(value.as(String.class));
			}
			else
				initialSelection = Collections.emptyList();
			setPageComplete(true);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> configuration = ArrayListMultimap.create();
		for (EntityDefinition selected : selection) {
			// build property path (QNames separated by .)
			/*
			 * FIXME this is problematic with property names that contain dots
			 * and only works out because only top level properties are allowed.
			 * If multiple levels are needed, properties should be stored as
			 * Lists of QNames (Complex values) or EntityDefinitions.
			 */
			String propertyPath = Joiner.on('.').join(
					Collections2.transform(selected.getPropertyPath(),
							new Function<ChildContext, String>() {

								@Override
								public String apply(ChildContext input) {
									return input.getChild().getName().toString();
								}
							}));

			// add it to configuration
			configuration.put(parameter.getName(), new ParameterValue(propertyPath));
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
		viewer.setContentProvider(new PropertyPathContentProvider(SchemaSpaceID.SOURCE));
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
			sourceType = (TypeDefinition) unfinishedCell.getSource().values().iterator().next()
					.getDefinition().getDefinition();

		viewer.setInput(sourceType);

		// add initial selection
		if (sourceType != null && initialSelection != null) {
			for (String propertyPath : initialSelection) {
				EntityDefinition entity = getEntityDefinition(propertyPath, sourceType);
				if (entity != null) {
					selection.add(entity);
				}
				else {
					log.warn("Could not find child for property path " + propertyPath);
				}
			}
			viewer.setCheckedElements(selection.toArray());
		}
	}

	@Nullable
	private EntityDefinition getEntityDefinition(String propertyPath, TypeDefinition sourceType) {
		ArrayList<ChildContext> contextPath = new ArrayList<ChildContext>();

		// XXX removed because it causes problems with dots in property names
//		List<QName> path = PropertyResolver.getQNamesFromPath(propertyPath);
		// FIXME quick fix that only works because only first level properties
		// are supported
		List<QName> path = Collections.singletonList(QName.valueOf(propertyPath));

		Iterator<QName> iter = path.iterator();
		ChildDefinition<?> child = sourceType.getChild(iter.next());
		if (child != null) {
			contextPath.add(new ChildContext(child));
			while (iter.hasNext()) {
				child = DefinitionUtil.getChild(child, iter.next());
				contextPath.add(new ChildContext(child));
			}
			return AlignmentUtil.createEntity(sourceType, contextPath, SchemaSpaceID.SOURCE, null);
		}

		return null;
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
