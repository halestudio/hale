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
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
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

	private CheckboxTreeViewer viewer;
	private NSTypeTreeContentProvider contentProvider;
	private ICheckStateProvider checkStateProvider;
	
	private final DefinitionLabelProvider definitionLabels = new DefinitionLabelProvider() {

//		@Override
//		public String getText(Object element) {
//			if (element instanceof Definition<?>) {
//				// force displaying the local part as types are shown according to namespace
//				return ((Definition<?>) element).getName().getLocalPart();
//			}
//			return super.getText(element);
//		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof String)
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			return super.getImage(element);
		}
		
	};

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
		viewer = (CheckboxTreeViewer) tree.getViewer();
		contentProvider = new NSTypeTreeContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setComparator(new DefinitionComparator());
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object doubleClicked = selection.getFirstElement();
				if (doubleClicked instanceof String)
					viewer.setExpandedState(doubleClicked, !viewer.getExpandedState(doubleClicked));
				else {
					boolean newState = !checkStateProvider.isChecked(doubleClicked);
					viewer.setChecked(doubleClicked, newState);
					checkStateOfTypeChanged((TypeDefinition) doubleClicked, newState);
				}
			}
		});
		viewer.setLabelProvider(definitionLabels);
		// because elements filtered by FilteredTree lose their checked state:
		checkStateProvider = new ICheckStateProvider() {
			@Override
			public boolean isGrayed(Object element) {
				if (element instanceof String) {
					Object[] children = contentProvider.getChildren(element);
					boolean containsChecked = false;
					boolean containsUnchecked = false;
					for (Object child : children) {
						if (isChecked(child))
							containsChecked = true;
						else
							containsUnchecked = true;
						if (containsChecked && containsUnchecked)
							return true;
					}
				}
				return false;
			}

			@Override
			public boolean isChecked(Object element) {
				if (element instanceof String) {
					for (Object child : contentProvider.getChildren(element))
						if (isChecked(child))
							return true;
					return false;
				}				
				return ((TypeDefinition) element).getConstraint(MappableFlag.class).isEnabled() != changedTypes
								.contains(element);
			}
		};
		viewer.setCheckStateProvider(checkStateProvider);
		viewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getElement() instanceof String) {
					// update children
					viewer.setGrayed(event.getElement(), false);
					for (Object child : contentProvider.getChildren(event.getElement()))
						if (checkStateProvider.isChecked(child) != event.getChecked()) {
							viewer.setChecked(child, event.getChecked());
							checkStateOfTypeChanged((TypeDefinition) child, event.getChecked());
						}
					// only two levels, no need to update any parents or children's children
				} else
					checkStateOfTypeChanged((TypeDefinition) event.getElement(), event.getChecked());
			}
		});

		// set input to all types in the given index
		viewer.setInput(typeIndex.getTypes());

		// expand all except XMLSchema
		viewer.expandAll();
		viewer.setExpandedState("http://www.w3.org/2001/XMLSchema", false);

		// set control
		setControl(viewer.getControl());
	}

	private void checkStateOfTypeChanged(TypeDefinition type, boolean checked) {
		if (checked == type.getConstraint(MappableFlag.class).isEnabled())
			changedTypes.remove(type);
		else
			changedTypes.add(type);
		Object parent = contentProvider.getParent(type);
		viewer.setGrayed(parent, checkStateProvider.isGrayed(parent));
		viewer.setChecked(parent, checkStateProvider.isChecked(parent));
	}

	/**
	 * Returns the set of types the user selected to change the mappable flag.
	 * 
	 * @return the selected types
	 */
	public Set<TypeDefinition> getSelectedTypes() {
		return changedTypes;
	}

	@Override
	public void dispose() {
		definitionLabels.dispose();
		
		super.dispose();
	}
	
}
