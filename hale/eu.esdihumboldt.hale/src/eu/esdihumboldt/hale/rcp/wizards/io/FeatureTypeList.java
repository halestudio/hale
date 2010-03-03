package eu.esdihumboldt.hale.rcp.wizards.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.opengis.feature.type.FeatureType;

/**
 * Component for selecting {@link FeatureType}s with a common namespace 
 * @author Jan Kolar
 * @partner 01 / Intergraph CS
 * @version $Id: FeatureTypeList.java 1862 2009-09-10 10:28:10Z jkolar $ 
 */
public class FeatureTypeList extends Composite {
	private org.eclipse.swt.widgets.List _featuresList;
	private final Map<String, List<FeatureType>> _types = new HashMap<String, List<FeatureType>>();
	private Combo _namespaces;

	public FeatureTypeList(Composite parent) {
		super(parent, SWT.NONE);
		
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
		
		this._featuresList = new org.eclipse.swt.widgets.List(this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		this._featuresList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		this.updateNamespaces();
	}
	
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
	 * @return the selected {@link FeatureType}s
	 */
	public Collection<FeatureType> getSelection() {
		Collection<FeatureType> selectedFeatureTypes = new ArrayList<FeatureType>();
		
		List<FeatureType> types = null;
		
		String namespace = this._namespaces.getText();
		if (namespace.equals("") && (_featuresList != null)) {
			types = new ArrayList<FeatureType>();
			Set<String> keys = _types.keySet();
			for (String key: keys) {
				types.addAll(_types.get(key));
			}
		}
		else {
			types = _types.get(namespace);
		}
		
		String[] selection = _featuresList.getSelection();
		for (int i=0; i<selection.length; i++) {
			for (FeatureType type: types) {
				if (type.getName().getLocalPart().equals(selection[i])) {
					selectedFeatureTypes.add(type);
					break;
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
		this._namespaces.add(""); //$NON-NLS-1$
		
		String first = null;
		for (String namespace : this._types.keySet()) {
			if (first == null) {
				first = namespace;
			}
			
			this._namespaces.add(namespace);
		}
		
		if (first != null) {
			this._namespaces.setText(first);
		}
		this._namespaces.select(0);
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
		else if (namespace.equals("") && (this._featuresList != null)) { //$NON-NLS-1$
			Set<String> keys = this._types.keySet();
			for (String key: keys) {
				types = this._types.get(key);
				for (FeatureType type : types){
					this._featuresList.add(type.getName().getLocalPart());
				}
			}
			this._featuresList.setEnabled(true);
		}
	}

}
