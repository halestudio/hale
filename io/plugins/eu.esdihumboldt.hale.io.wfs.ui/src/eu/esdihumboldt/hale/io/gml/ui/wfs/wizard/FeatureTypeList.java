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
package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.io.wfs.ui.internal.Messages;

/**
 * Component for selecting {@link FeatureType}s with a common namespace
 * 
 * @author Jan Kolar, Simon Templer
 * @partner ?? / Intergraph CS, 01 / Fraunhofer Institute for Computer Graphics
 *          Research
 */
public class FeatureTypeList extends Composite {

	/**
	 * Selection listener interface
	 */
	public static interface TypeSelectionListener {

		/**
		 * Called when the selection was changed
		 */
		public void selectionChanged();
	}

	private final org.eclipse.swt.widgets.List _featuresList;

	private final Map<String, List<FeatureType>> _types = new HashMap<String, List<FeatureType>>();

	private final Combo _namespaces;

	private final Set<TypeSelectionListener> listeners = new HashSet<TypeSelectionListener>();

	private final String fixedNamespace;

	/**
	 * Constructor
	 * 
	 * @param parent the parent composite
	 * @param fixedNamespace the namespace
	 */
	public FeatureTypeList(Composite parent, String fixedNamespace) {
		super(parent, SWT.NONE);

		this.fixedNamespace = fixedNamespace;

		GridLayout layout = new GridLayout(1, false);
		this.setLayout(layout);

		Label label = new Label(this, SWT.NONE);
		label.setText(Messages.FeatureTypeList_LabelFilter);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		this._namespaces = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		this._namespaces.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		this._namespaces.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFeatures();
			}

		});

		label = new Label(this, SWT.NONE);
		label.setText(Messages.FeatureTypeList_LabelFeature);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		this._featuresList = new org.eclipse.swt.widgets.List(this, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		this._featuresList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		_featuresList.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				notifySelectionChanged();
			}

		});

		this.updateNamespaces();
	}

	/**
	 * Set the available feature types
	 * 
	 * @param types the feature types to set
	 */
	public void setFeatureTypes(List<FeatureType> types) {
		this._types.clear();

		if (types != null) {
			for (FeatureType type : types) {
				String namespace = type.getName().getNamespaceURI();
				List<FeatureType> list = this._types.get(namespace);
				if (list == null) {
					list = new ArrayList<FeatureType>();
					this._types.put(namespace, list);
				}
				list.add(type);
			}
		}

		updateNamespaces();
	}

	/**
	 * Get the selected feature types
	 * 
	 * @return the selected {@link FeatureType}s
	 */
	public List<FeatureType> getSelection() {
		List<FeatureType> selectedFeatureTypes = new ArrayList<FeatureType>();

		List<FeatureType> types = null;

		String namespace = this._namespaces.getText();
		if (namespace.equals("") && (_featuresList != null)) { //$NON-NLS-1$
			types = new ArrayList<FeatureType>();
			Set<String> keys = _types.keySet();
			for (String key : keys) {
				types.addAll(_types.get(key));
			}
		}
		else {
			types = _types.get(namespace);
		}

		String[] selection = _featuresList.getSelection();
		if (selection == null || selection.length == 0) {
			// default to all
			selectedFeatureTypes.addAll(types);
		}
		else {
			for (int i = 0; i < selection.length; i++) {
				for (FeatureType type : types) {
					if (type.getName().getLocalPart().equals(selection[i])) {
						selectedFeatureTypes.add(type);
						break;
					}
				}
			}
		}

		return selectedFeatureTypes;
	}

	/**
	 * Update the namespace combo
	 */
	private void updateNamespaces() {
		this._namespaces.removeAll();
		// don't use an empty namespace because mixing features from different namespaces is not allowed - this._namespaces.add(""); //$NON-NLS-1$

		String selectNamespace = null;
		int selectIndex = 0;
		int index = 0;
		for (String namespace : this._types.keySet()) {
			if (selectNamespace == null
					|| (fixedNamespace != null && fixedNamespace.equals(namespace))) {
				selectNamespace = namespace;
				selectIndex = index;
			}

			this._namespaces.add(namespace);
			index++;
		}

		if (selectNamespace != null) {
			this._namespaces.setText(selectNamespace);
		}
		this._namespaces.select(selectIndex);

		/*
		 * if (fixedNamespace != null && fixedNamespace.equals(selectNamespace))
		 * { _namespaces.setEnabled(false); } else {
		 * _namespaces.setEnabled(true); }
		 */

		this.updateFeatures();
	}

	/**
	 * Update the feature type list
	 */
	private void updateFeatures() {
		String namespace = this._namespaces.getText();
		this._featuresList.removeAll();
		this._featuresList.setEnabled(false);

		List<FeatureType> types = this._types.get(namespace);

		if (types != null) {
			for (FeatureType type : types) {
				this._featuresList.add(type.getName().getLocalPart());
			}
			this._featuresList.setEnabled(true);
		}
		else if (namespace.equals("")) { //$NON-NLS-1$
			Set<String> keys = this._types.keySet();
			for (String key : keys) {
				types = this._types.get(key);
				for (FeatureType type : types) {
					this._featuresList.add(type.getName().getLocalPart());
				}
			}
			this._featuresList.setEnabled(true);
		}

		notifySelectionChanged();
	}

	/**
	 * Add a type selection listener
	 * 
	 * @param listener the listener to add
	 */
	public void addTypeSelectionListener(TypeSelectionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a type selection listener
	 * 
	 * @param listener the listener to add
	 */
	public void removeTypeSelectionListener(TypeSelectionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify listeners that the selection has changed
	 */
	protected void notifySelectionChanged() {
		for (TypeSelectionListener listener : listeners) {
			listener.selectionChanged();
		}
	}

	/**
	 * Get the selected namespace
	 * 
	 * @return the namespace
	 */
	public String getNamespace() {
		return _namespaces.getText();
	}

}
