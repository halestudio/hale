/*
 * Copyright (c) 2022 Data Harmonisation Panel
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;
import eu.esdihumboldt.hale.common.core.io.Value;
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
 * Parameter page for skipping properties in structural retype. Provides an UI
 * for selecting the matching properties. Based on {@link MergeParameterPage}
 * 
 * @author Simon Templer
 */
public class RetypeSkipPropertiesParameterPage
		extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements ParameterPage {

	private static final ALogger log = ALoggerFactory
			.getLogger(RetypeSkipPropertiesParameterPage.class);

	private Set<QName> initialSelection;
	private CheckboxTreeViewer viewer;
	private TypeDefinition sourceType;
	private Set<EntityDefinition> selection = new HashSet<EntityDefinition>();
	private final DefinitionLabelProvider labelProvider = new DefinitionLabelProvider(null);

	private FunctionParameterDefinition parameter;

	/**
	 * Constructor.
	 */
	public RetypeSkipPropertiesParameterPage() {
		super("skipPropertiesPage");
		setPageComplete(false);
	}

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
	}

	@Override
	public void setParameter(Set<FunctionParameterDefinition> params,
			ListMultimap<String, ParameterValue> initialValues) {
		if (params.size() > 1) {
			throw new IllegalArgumentException(
					"RetypeSkipPropertiesParameterPage is only for one parameter");
		}

		parameter = params.iterator().next();

		if (!RetypeFunction.PARAMETER_SKIP_ROOT_PROPERTIES.equals(parameter.getName())) {
			throw new IllegalArgumentException("Parameter not supported: " + parameter.getName());
		}

		setTitle("Please select the properties to exclude");
		setDescription("Only applies to structural rename");

		if (initialValues != null) {
			// cell gets edited
			List<ParameterValue> tmp = initialValues.get(parameter.getName());
			if (tmp != null) {
				initialSelection = new HashSet<>();
				for (ParameterValue value : tmp) {
					initialSelection.add(value.as(QName.class));
				}
			}
			else {
				initialSelection = Collections.emptySet();
			}
			setPageComplete(true);
		}
	}

	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> configuration = ArrayListMultimap.create();
		for (EntityDefinition selected : selection) {
			QName name = selected.getLastPathElement().getChild().getName();

			// add it to configuration
			configuration.put(parameter.getName(), new ParameterValue(Value.of(name)));
		}

		return configuration;
	}

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
		Cell unfinishedCell = getWizard().getUnfinishedCell();
		if (unfinishedCell.getSource() != null) {
			sourceType = (TypeDefinition) unfinishedCell.getSource().values().iterator().next()
					.getDefinition().getDefinition();
		}

		viewer.setInput(sourceType);

		// add initial selection
		if (sourceType != null && initialSelection != null) {
			for (QName selName : initialSelection) {
				EntityDefinition entity = getEntityDefinition(selName, sourceType);
				if (entity != null) {
					selection.add(entity);
				}
				else {
					log.warn("Could not find child with name " + selName);
				}
			}
			viewer.setCheckedElements(selection.toArray());
		}
	}

	@Nullable
	private EntityDefinition getEntityDefinition(QName name, TypeDefinition sourceType) {
		ArrayList<ChildContext> contextPath = new ArrayList<ChildContext>();

		List<QName> path = Collections.singletonList(name);

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

	@Override
	public void dispose() {
		labelProvider.dispose();
		super.dispose();
	}
}
