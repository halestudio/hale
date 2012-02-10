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

package eu.esdihumboldt.hale.ui.service.schema.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.service.schema.util.NSTypeTreeContentProvider;

/**
 * Wizard page to edit which types are mappable.
 * 
 * @author Kai Schwierczek
 */
public class EditMappableTypesPage extends WizardPage {
	private final TypeIndex typeIndex;
	private final Set<TypeDefinition> changedTypes = new HashSet<TypeDefinition>();

	/**
	 * Creates a new wizard page to edit which types in the given index are
	 * mappable.
	 * 
	 * @param typeIndex the type index to edit
	 */
	public EditMappableTypesPage(TypeIndex typeIndex) {
		super("editMappableTypes", "Edit mappable types", null);
		setDescription("Check which types should be mappable");
		this.typeIndex = typeIndex;
		setPageComplete(true);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		parent.setLayout(new GridLayout());

		// create filtered tree
		PatternFilter patternFilter = new PatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new FilteredTree(parent, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL,
				patternFilter, true) {
			@Override
			protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
				return new CheckboxTreeViewer(parent, style);
			}
		};

		// configure viewer
		final CheckboxTreeViewer viewer = (CheckboxTreeViewer) tree.getViewer();
		viewer.setContentProvider(new NSTypeTreeContentProvider());
		viewer.setLabelProvider(new DefinitionLabelProvider());
		// because elements filtered by FilteredTree lose their checked state:
		viewer.setCheckStateProvider(new ICheckStateProvider() {
			@Override
			public boolean isGrayed(Object element) {
				return element instanceof String;
			}

			@Override
			public boolean isChecked(Object element) {
				return element instanceof String || ((TypeDefinition) element).getConstraint(MappableFlag.class).isEnabled() != changedTypes
								.contains(element);
			}
		});
		viewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getElement() instanceof String) {
					viewer.setChecked(event.getElement(), true);
					viewer.setGrayed(event.getElement(), true);
				} else {
					TypeDefinition type = (TypeDefinition) event.getElement();
					if (event.getChecked() == type.getConstraint(MappableFlag.class).isEnabled())
						changedTypes.remove(type);
					else
						changedTypes.add(type);
				}
			}
		});
		viewer.setInput(typeIndex.getTypes());

		// filter to exclude simple types
		viewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof String
						|| !((TypeDefinition) element).getConstraint(HasValueFlag.class).isEnabled();
			}
		});

		// expand all except XMLSchema
		viewer.expandAll();
		viewer.setExpandedState("http://www.w3.org/2001/XMLSchema", false);

		// set control
		setControl(viewer.getControl());
	}

	/**
	 * Returns the set of types the user selected to change the mappable flag.
	 * 
	 * @return the selected types
	 */
	public Set<TypeDefinition> getSelectedTypes() {
		return changedTypes;
	}
}
