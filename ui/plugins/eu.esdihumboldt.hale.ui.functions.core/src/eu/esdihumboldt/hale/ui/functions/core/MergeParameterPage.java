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
 * Parameter page for merge function. Provides an UI for selecting the matching properties.
 * 
 * @author Kai Schwierczek
 */
public class MergeParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements ParameterPage {
	private List<String> initialSelection;
	private CheckboxTreeViewer viewer;
	private TypeDefinition sourceType;
	private Set<EntityDefinition> selection = new HashSet<EntityDefinition>();

	/**
	 * Constructor.
	 */
	public MergeParameterPage() {
		super("property", "Please select the properties that have to match", null);
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		setPageComplete(true);
		// selected target could've changed!
		TypeDefinition newSourceType = (TypeDefinition) getWizard().getUnfinishedCell().getSource().values().iterator().next().getDefinition().getDefinition();
		if (!newSourceType.equals(sourceType)) {
			selection = new HashSet<EntityDefinition>();
			sourceType = newSourceType;
			viewer.setInput(sourceType);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#setParameter(java.util.Set, com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameter(Set<FunctionParameter> params, ListMultimap<String, String> initialValues) {
		// this page is only for parameter property, ignore params
		if (initialValues == null)
			return;

		// cell gets edited
		initialSelection = initialValues.get("property");
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
			configuration.put("property", propertyPath);
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

		// create checkbox tree viewer
		viewer = new CheckboxTreeViewer(page, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().
				grab(true, true).create());
		// comparator
		viewer.setComparator(new DefinitionComparator());
		// label provider
		viewer.setLabelProvider(new DefinitionLabelProvider());
		// content provider
		EntityDefinitionService entityDefinitionService = (EntityDefinitionService) PlatformUI.getWorkbench().getService(EntityDefinitionService.class);
		viewer.setContentProvider(new EntityTypePropertyContentProvider(viewer, entityDefinitionService, SchemaSpaceID.SOURCE));
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
		if (unfinishedCell.getSource() != null)
			sourceType = (TypeDefinition) unfinishedCell.getSource().values().iterator().next().getDefinition().getDefinition();

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
				selection.add(AlignmentUtil.createEntity(sourceType, contextPath, SchemaSpaceID.SOURCE));
			}
			viewer.setCheckedElements(selection.toArray());
		}
	}
}
