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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.schema.util.NSTypeTreeContentProvider;

/**
 * Wizard page to edit which types are mappable.
 * 
 * @author Kai Schwierczek
 */
public class EditMappableTypesPage extends WizardPage {

	private final TypeIndex typeIndex;
	private final SchemaSpaceID spaceID;
	private final Set<TypeDefinition> changedTypes = new HashSet<TypeDefinition>();

	private CheckboxTreeViewer viewer;
	private NSTypeTreeContentProvider contentProvider;
	private ICheckStateProvider checkStateProvider;

	private final ViewerFilter mappedTypeFilter = new ViewerFilter() {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof String)
				return true;
			TypeDefinition type = (TypeDefinition) element;
			AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
			if (as.getAlignment().getCells(type, spaceID).size() > 0)
				return false;
			return true;
		}
	};

	private final DefinitionLabelProvider definitionLabels = new DefinitionLabelProvider(null) {

		private final Image lockImg = HALEUIPlugin.getImageDescriptor("icons/lock.gif")
				.createImage();

		// @Override
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
				return PlatformUI.getWorkbench().getSharedImages()
						.getImage(ISharedImages.IMG_OBJ_FOLDER);
			else if (!mappedTypeFilter.select(viewer, null, element))
				return lockImg;
			else
				return super.getImage(element);
		}

		@Override
		public void dispose() {
			super.dispose();
			lockImg.dispose();
		}
	};

	/**
	 * Creates a new wizard page to edit which types in the given index are
	 * mappable.
	 * 
	 * @param spaceID the schema space of which the types are
	 * @param typeIndex the type index to edit
	 */
	public EditMappableTypesPage(SchemaSpaceID spaceID, TypeIndex typeIndex) {
		super("editMappableTypes", "Edit mappable types", null);
		setDescription("Check which types should be mappable");
		this.typeIndex = typeIndex;
		this.spaceID = spaceID;
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
		FilteredTree tree = new FilteredTree(parent,
				SWT.BORDER | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL, patternFilter, true) {

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
				return ((TypeDefinition) element).getConstraint(MappingRelevantFlag.class)
						.isEnabled() != changedTypes.contains(element);
			}
		};
		viewer.setCheckStateProvider(checkStateProvider);

		viewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getElement() instanceof String) {
					// update children
					viewer.setGrayed(event.getElement(), false);
					for (Object child : contentProvider.getChildren(event.getElement())) {
//						if (mappedTypeFilter.select(viewer, event.getElement(), child)) {
						if (checkStateProvider.isChecked(child) != event.getChecked()) {
							viewer.setChecked(child, event.getChecked());
							checkStateOfTypeChanged((TypeDefinition) child, event.getChecked());
						}
//						}
					}
					viewer.setGrayed(event.getElement(),
							checkStateProvider.isGrayed(event.getElement()));
					// only two levels, no need to update any parents or
					// children's children
				}
				else {
//					if (mappedTypeFilter.select(viewer, null, event.getElement()))
					checkStateOfTypeChanged((TypeDefinition) event.getElement(),
							event.getChecked());
//					else if (!event.getChecked())
//						viewer.setChecked(event.getElement(), true);
				}
			}
		});

//		// filter types which are used in the current alignment
//		viewer.addFilter(mappedTypeFilter);

		// set input to all types in the given index
		viewer.setInput(typeIndex.getTypes());

		// expand all except XMLSchema
		viewer.expandAll();
		viewer.setExpandedState("http://www.w3.org/2001/XMLSchema", false);

		// set control
		setControl(viewer.getControl());
	}

	private void checkStateOfTypeChanged(TypeDefinition type, boolean checked) {
//		if (mappedTypeFilter.select(viewer, null, type)) {
		if (checked == type.getConstraint(MappingRelevantFlag.class).isEnabled())
			changedTypes.remove(type);
		else
			changedTypes.add(type);
		if (contentProvider != null && checkStateProvider != null) {
			Object parent = contentProvider.getParent(type);
			viewer.setGrayed(parent, checkStateProvider.isGrayed(parent));
			viewer.setChecked(parent, checkStateProvider.isChecked(parent));
		}
//		}
//		else if (!checked)
//			viewer.setChecked(type, true);
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
