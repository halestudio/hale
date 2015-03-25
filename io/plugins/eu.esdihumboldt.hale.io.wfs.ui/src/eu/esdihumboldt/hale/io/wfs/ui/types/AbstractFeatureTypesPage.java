/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.wfs.ui.types;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.io.wfs.ui.capabilities.AbstractWFSCapabilitiesPage;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage;

/**
 * Page for specifying the capabilities URL (and loading the capabilities).
 * 
 * @author Simon Templer
 * @param <T> the configuration object type
 */
public abstract class AbstractFeatureTypesPage<T> extends ConfigurationWizardPage<T> {

	private CheckboxTreeViewer viewer;
	private ICheckStateProvider checkStateProvider;
	private final Set<QName> selected = new HashSet<>();
	private FeatureTypeTreeContentProvider contentProvider;
	private final AbstractWFSCapabilitiesPage<T> capabilitiesPage;
	private String lastCapUrl;

	/**
	 * Constructor
	 * 
	 * @param wizard the parent wizard
	 * @param capabilitiesPage the page querying the WFS capabilities
	 * @param message the page message
	 */
	public AbstractFeatureTypesPage(ConfigurationWizard<? extends T> wizard,
			AbstractWFSCapabilitiesPage<T> capabilitiesPage, String message) {
		super(wizard, "wfsFeatureTypes");
		setTitle("WFS Feature types");
		setMessage(message);
		this.capabilitiesPage = capabilitiesPage;
	}

	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(1, false));

		// create filtered tree
		PatternFilter patternFilter = new PatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new FilteredTree(page, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL
				| SWT.V_SCROLL, patternFilter, true) {

			@Override
			protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
				return new CheckboxTreeViewer(parent, style);
			}
		};

		// configure viewer
		viewer = (CheckboxTreeViewer) tree.getViewer();
		contentProvider = new FeatureTypeTreeContentProvider();
		viewer.setContentProvider(contentProvider);
//		viewer.setComparator(new DefinitionComparator());
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
					checkStateOfTypeChanged((QName) doubleClicked, newState);
				}
			}
		});
		viewer.setLabelProvider(new FeatureTypeTreeLabelProvider());
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
				return selected.contains(element);
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
						if (checkStateProvider.isChecked(child) != event.getChecked()) {
							viewer.setChecked(child, event.getChecked());
							checkStateOfTypeChanged((QName) child, event.getChecked());
						}
					}
					viewer.setGrayed(event.getElement(),
							checkStateProvider.isGrayed(event.getElement()));
					// only two levels, no need to update any parents or
					// children's children
				}
				else {
					checkStateOfTypeChanged((QName) event.getElement(), event.getChecked());
				}
			}
		});

		setControl(page);

		// initial update
		updateState(selected);
	}

	@Override
	protected void onShowPage() {
		// set input to types from capabilities
		Set<QName> types = capabilitiesPage.getCapabilities().getFeatureTypes();
		types = filterTypes(types);
		viewer.setInput(types);

		String capUrl = capabilitiesPage.getCapabilitiesURL();
		if (!Objects.equals(capUrl, lastCapUrl)) {
			// capabilities changed

			selected.clear();
			selected.addAll(initialSelection(types));

			for (QName sel : selected) {
				viewer.expandToLevel(sel, 0);
				updateParent(sel);
			}

			updateState(selected);
		}
		else {
			// capabilities stayed the same
			if (selected.retainAll(types)) {
				updateState(selected);
			}
		}

		lastCapUrl = capUrl;
	}

	/**
	 * Filter the types provided via the capabilities.
	 * 
	 * @param types the feature type names
	 * @return the feature type names to display
	 */
	protected Set<QName> filterTypes(Set<QName> types) {
		return types;
	}

	/**
	 * Determine the initial selection given the list of types.
	 * 
	 * @param types the feature types names as stated in the capabilities
	 * @return the collection of names to be initially selected
	 */
	protected Collection<? extends QName> initialSelection(Set<QName> types) {
		return Collections.emptySet();
	}

	private void checkStateOfTypeChanged(QName type, boolean checked) {
		if (checked)
			selected.add(type);
		else
			selected.remove(type);
		updateParent(type);
		updateState(selected);
	}

	private void updateParent(QName type) {
		if (contentProvider != null && checkStateProvider != null) {
			Object parent = contentProvider.getParent(type);
			viewer.setGrayed(parent, checkStateProvider.isGrayed(parent));
			viewer.setChecked(parent, checkStateProvider.isChecked(parent));
		}
	}

	/**
	 * Update page state based on the selection.
	 * 
	 * @param selected the selected types
	 */
	protected abstract void updateState(Set<QName> selected);

	@Override
	public boolean updateConfiguration(T configuration) {
		return updateConfiguration(configuration, selected);
	}

	/**
	 * Update the configuration object based on the type selection.
	 * 
	 * @param configuration the configuration object to update
	 * @param selected the selected types
	 * @return if the configuration could be successfully updated
	 */
	protected abstract boolean updateConfiguration(T configuration, Set<QName> selected);

}
