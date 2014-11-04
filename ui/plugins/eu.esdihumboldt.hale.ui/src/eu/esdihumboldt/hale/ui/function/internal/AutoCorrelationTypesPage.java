/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.function.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypesContentProvider;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.selection.SchemaSelectionHelper;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Page to select/set the source of the auto correlation function
 * 
 * @author Yasmina Kammeyer
 */
public class AutoCorrelationTypesPage extends HaleWizardPage<AutoCorrelationFunctionWizard> {

	private Composite pageComposite;
	private Button processEntireSchema;
	// private ListMultimap<String, Type> source;
	private Set<EntityDefinition> source;
	// private ListMultimap<String, Type> target;
	private Set<EntityDefinition> target;
	private TreeViewer listOfSourceTypes;
	private TreeViewer listOfTargetTypes;

	/**
	 * @param pageName The name of the page
	 */
	protected AutoCorrelationTypesPage(String pageName) {
		super(pageName);

		setTitle(pageName);
		setDescription("Please choose/confirm your desired source types.");
	}

	/**
	 * Check if the page is valid and set the
	 * 
	 * @return true, if the page's state is valid
	 */
	private boolean isValid() {
		if (processEntireSchema.getSelection()) {
			setPageComplete(true);
			return true;
		}

		else if ((source != null && !source.isEmpty()) || (target != null && !target.isEmpty())) {
			setPageComplete(true);
			return true;
		}

		setPageComplete(false);
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		// set the source and target types to the selection if firstShow
		if (firstShow) {
			SchemaSelection selection = SchemaSelectionHelper.getSchemaSelection();
			source = selection.getSourceItems();
			target = selection.getTargetItems();

			Collection<TypeDefinition> types = new ArrayList<TypeDefinition>();
			if (isValid()) {
				for (EntityDefinition entity : source) {
					// listOfSourceTypes.add(entity.getType().getDisplayName());
					if (entity.getDefinition() instanceof TypeDefinition) {
						types.add((TypeDefinition) entity.getDefinition());
					}
				}
				listOfSourceTypes.setInput(types);
				types = new ArrayList<TypeDefinition>();

				for (EntityDefinition entity : target) {
					// listOfTargetTypes.add(entity.getType().getDisplayName());
					if (entity.getDefinition() instanceof TypeDefinition) {
						types.add((TypeDefinition) entity.getDefinition());
					}
				}
				listOfTargetTypes.setInput(types);
			}

		}

		listOfSourceTypes.refresh();
		listOfTargetTypes.refresh();
		pageComposite.layout();
		pageComposite.pack();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		pageComposite = page;

		GridLayout layout = new GridLayout(1, false);
		page.setLayout(layout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(page);

		Group typeSelectorSpace = new Group(page, SWT.NONE);
		typeSelectorSpace.setText("Types");
		typeSelectorSpace.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(typeSelectorSpace);

		// Types
		Label sourceLabel = new Label(typeSelectorSpace, SWT.NONE);
		sourceLabel.setText("Source Type: ");
		Label targetLabel = new Label(typeSelectorSpace, SWT.NONE);
		targetLabel.setText("Target Type: ");

		listOfSourceTypes = createTypeViewer(typeSelectorSpace, new ArrayList<TypeDefinition>());
		listOfTargetTypes = createTypeViewer(typeSelectorSpace, new ArrayList<TypeDefinition>());

		// Checkbox entire Schema
		processEntireSchema = new Button(page, SWT.CHECK);
		processEntireSchema.setText("Process Entire Schema");
		processEntireSchema.setSelection(false);
		processEntireSchema.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (processEntireSchema.getSelection()) {
					// listOfSourceTypes.setEnabled(false);
					// listOfTargetTypes.setEnabled(false);
				}
				else {
					// listOfSourceTypes.setEnabled(true);
					// listOfTargetTypes.setEnabled(true);
				}
				isValid();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (processEntireSchema.getSelection()) {
					// listOfSourceTypes.setEnabled(false);
					// listOfTargetTypes.setEnabled(false);
				}
				else {
					// listOfSourceTypes.setEnabled(true);
					// listOfTargetTypes.setEnabled(true);
				}
				isValid();
			}
		});
		GridDataFactory.swtDefaults().grab(true, false).applyTo(processEntireSchema);

		setPageComplete(false);
		// page.layout();
		// page.pack();
	}

	/**
	 * 
	 */
	private void createField(Composite parent) {
		// , String text) {

		// Add the items, one by one

//		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL);
//
//		// Create a child composite to hold the controls
//		Composite control = new Composite(sc, SWT.NONE);
//		control.setLayout(new GridLayout(1, false));
//		GridDataFactory.fillDefaults().grab(true, false).applyTo(control);
//		control.setSize(300, 100);
//
//		Label name = new Label(control, SWT.NONE);
//		name.setText(text);
//		name.setLayoutData(GridDataFactory.swtDefaults().create());
//		/*
//		 * // Set the absolute size of the child child.setSize(400, 400);
//		 */
//		// Set the child as the scrolled content of the ScrolledComposite
//		sc.setContent(control);
//
//		// Set the minimum size
//		// sc.setMinSize(400, 400);
//
//		// Expand both horizontally and vertically
//		sc.setExpandHorizontal(false);
//		sc.setExpandVertical(true);
	}

	private TreeViewer createTypeViewer(Composite parent, Collection<TypeDefinition> initialInput) {
		PatternFilter patternFilter = new PatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new FilteredTree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER, patternFilter, true);
		tree.getViewer().setComparator(new DefinitionComparator());

		TreeViewer viewer = tree.getViewer();

		viewer.setComparator(new DefinitionComparator());

		viewer.setLabelProvider(new DefinitionLabelProvider());
		viewer.setContentProvider(new TypesContentProvider(viewer));

		viewer.setInput(initialInput);

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// updateButtonStatus();
			}
		});

		return viewer;
	}

	/**
	 * 
	 * @return The page's result - source types to be processed
	 */
	public Collection<TypeDefinition> getSourceTypes() {
		if (processEntireSchema.getSelection()) {
			// process the whole schema
			return getCompleteTypesFromSchemaSpace(SchemaSpaceID.SOURCE);
		}
		Collection<TypeDefinition> sourceTypes = new ArrayList<TypeDefinition>();
		for (EntityDefinition type : source) {
			if (type instanceof TypeEntityDefinition) {
				sourceTypes.add(type.getType());
			}
		}
		// return all source types if target type(s) selected
		if (sourceTypes.isEmpty() && (target != null && !target.isEmpty())) {
			return getCompleteTypesFromSchemaSpace(SchemaSpaceID.SOURCE);
		}
		return sourceTypes;
	}

	/**
	 * 
	 * @return The page's result - target types to be processed
	 */
	public Collection<TypeDefinition> getTargetTypes() {
		if (processEntireSchema.getSelection()) {
			// process the whole schema
			return getCompleteTypesFromSchemaSpace(SchemaSpaceID.TARGET);
		}
		Collection<TypeDefinition> targetTypes = new ArrayList<TypeDefinition>();
		for (EntityDefinition type : target) {
			if (type instanceof TypeEntityDefinition) {
				targetTypes.add(type.getType());
			}
		}
		// return all source types if target type(s) selected
		if (targetTypes.isEmpty() && (source != null && !source.isEmpty())) {
			return getCompleteTypesFromSchemaSpace(SchemaSpaceID.TARGET);
		}
		return targetTypes;
	}

	/**
	 * @param spaceID the SchemaSpace which should be used
	 * @return the top most type of the schema ID
	 */
	private Collection<TypeDefinition> getCompleteTypesFromSchemaSpace(SchemaSpaceID spaceID) {
		SchemaService sches = (SchemaService) PlatformUI.getWorkbench().getService(
				SchemaService.class);
		SchemaSpace schema = sches.getSchemas(spaceID);

		Collection<TypeDefinition> types = new ArrayList<TypeDefinition>(
				schema.getMappingRelevantTypes());
		return types;
	}

//	private ListMultimap<String, Type> convertEntityToType(Set<EntityDefinition> list) {
//		ListMultimap<String, Type> result = ArrayListMultimap.create();
//
//		for (EntityDefinition entity : list) {
//			if (entity instanceof TypeEntityDefinition) {
//				Type type = new DefaultType((TypeEntityDefinition) entity);
//
//				result.put(null, type);
//			}
//		}
//
//		return result;
//	}
}
