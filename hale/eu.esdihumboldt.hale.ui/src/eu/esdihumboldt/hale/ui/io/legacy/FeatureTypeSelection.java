/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.ui.io.legacy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.ui.internal.Messages;

/**
 * Component for selecting {@link FeatureType}s of a common namespace 
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FeatureTypeSelection extends Composite {
	
	private Combo namespaces;
	
	private Text features; //TODO replace with a table?
	
	private final Map<String, List<FeatureType>> types = new HashMap<String, List<FeatureType>>();

	/**
	 * Constructor
	 * 
	 * @param parent the parent composite
	 */
	public FeatureTypeSelection(Composite parent) {
		super(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout(1, false);
		setLayout(layout);
		
		Label label = new Label(this, SWT.NONE);
		label.setText(Messages.FeatureTypeSelection_LabelNamespace);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		
		namespaces = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		namespaces.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		namespaces.addSelectionListener(new SelectionListener() {

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
		label.setText(Messages.FeatureTypeSelection_LabelFeature);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		
		features = new Text(this, 
				SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL |
				SWT.READ_ONLY);
		features.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		updateNamespaces();
	}
	
	/**
	 * Update the feature list
	 */
	private void updateFeatures() {
		String namespace = namespaces.getText();
		
		List<FeatureType> types = this.types.get(namespace);
		if (types != null) {
			StringBuffer text = new StringBuffer();
			
			for (FeatureType type : types) {
				text.append(type.getName().getLocalPart());
				text.append('\n');
			}
			
			features.setText(text.toString());
			features.setEnabled(true);
		}
		else {
			features.setText(""); //$NON-NLS-1$
			features.setEnabled(false);
		}
	}

	/**
	 * Update the namespace combo
	 */
	private void updateNamespaces() {
		namespaces.removeAll();
		
		String first = null;
		for (String namespace : types.keySet()) {
			if (first == null) {
				first = namespace;
			}
			
			namespaces.add(namespace);
		}
		
		if (first != null) {
			namespaces.setText(first);
		}
		
		updateFeatures();
	}

	/**
	 * Set the available {@link FeatureType}s
	 * 
	 * @param types the types
	 */
	public void setFeatureTypes(List<FeatureType> types) {
		this.types.clear();
		
		if (types != null) {
			for (FeatureType type : types) {
				String namespace = type.getName().getNamespaceURI();
				List<FeatureType> list = this.types.get(namespace);
				if (list == null) {
					list = new ArrayList<FeatureType>();
					this.types.put(namespace, list);
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
		Collection<FeatureType> result = new ArrayList<FeatureType>();
		
		if (!types.isEmpty()) {
			String namespace = namespaces.getText();
			List<FeatureType> types = this.types.get(namespace);
			if (types != null) {
				result.addAll(types);
			}
		}
		
		return result;
	}

}
