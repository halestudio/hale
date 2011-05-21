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

package eu.esdihumboldt.hale.ui.common.filter;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.ListDialog;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;

import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.internal.CommonUIPlugin;
import eu.esdihumboldt.hale.ui.common.internal.Messages;

/**
 * Field for editing a filter
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FeatureFilterField extends Composite {
	
	/**
	 * Filter listener interface
	 */
	public interface FilterListener {
		
		/**
		 * Notifies that the filter has changed
		 */
		public void filterChanged();
		
	}
	
	private final Text filterText;
	private final Button openForm;
	private final Button insertVar;
	private final Button clearFilter;
	
	private TypeDefinition type;
	
	private final Image insertVarImage;
	private final Image openFormImage;
	private final Image clearFilterImage;
	
	private final Set<FilterListener> listeners = new HashSet<FilterListener>();

	/**
	 * Create a new filter field
	 * 
	 * @param type the feature type
	 * @param parent the parent composite
	 * @param style the composite style
	 */
	public FeatureFilterField(TypeDefinition type, Composite parent, int style) {
		super(parent, style);
		
		this.type = type;
		
		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		
		// images
		insertVarImage = CommonUIPlugin.getImageDescriptor("icons/insert.gif").createImage(); //$NON-NLS-1$
		openFormImage = CommonUIPlugin.getImageDescriptor("icons/form.gif").createImage(); //$NON-NLS-1$
		clearFilterImage = CommonUIPlugin.getImageDescriptor("icons/remove.gif").createImage(); //$NON-NLS-1$
		
		// create components
		
		// text field
		filterText = new Text(this, SWT.SINGLE | SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterText.setToolTipText(Messages.FeatureFilterField_3); //$NON-NLS-1$
		filterText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				clearFilter.setEnabled(filterText.getText() != null && !filterText.getText().isEmpty());
				notifyListeners();
			}
			
		});
		
		// clear filter
		clearFilter = new Button(this, SWT.PUSH);
		clearFilter.setEnabled(false);
		clearFilter.setImage(clearFilterImage);
		clearFilter.setToolTipText(Messages.FeatureFilterField_0); //$NON-NLS-1$
		clearFilter.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		clearFilter.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				filterText.setText(""); //$NON-NLS-1$
				clearFilter.setEnabled(false);
				notifyListeners();
			}
			
		});
		
		// insert variable
		insertVar = new Button(this, SWT.PUSH);
		insertVar.setImage(insertVarImage);
		insertVar.setToolTipText(Messages.FeatureFilterField_6); //$NON-NLS-1$
		insertVar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		insertVar.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				SortedSet<String> attributeNames = new TreeSet<String>();
				//TODO adapt to TypeDefinition?
				for (PropertyDescriptor property : FeatureFilterField.this.type.getFeatureType().getDescriptors()) {
					attributeNames.add(property.getName().getLocalPart());
				}
				
				ListDialog dialog = new ListDialog(Display.getCurrent().getActiveShell());
				dialog.setTitle(Messages.FeatureFilterField_7); //$NON-NLS-1$
				dialog.setMessage(Messages.FeatureFilterField_8); //$NON-NLS-1$
				dialog.setContentProvider(ArrayContentProvider.getInstance());
				dialog.setLabelProvider(new LabelProvider());
				dialog.setInput(attributeNames);
				
				if (dialog.open() == ListDialog.OK && dialog.getResult() != null
						&& dialog.getResult().length >= 1) {
					String var = (String) dialog.getResult()[0];
					filterText.insert(var);
					filterText.setFocus();
				}
			}
			
		});
		
		// open form
		openForm = new Button(this, SWT.PUSH);
		openForm.setImage(openFormImage);
		openForm.setToolTipText(Messages.FeatureFilterField_9); //$NON-NLS-1$
		openForm.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		openForm.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FeatureFilterFormDialog dialog = new FeatureFilterFormDialog(
						Display.getCurrent().getActiveShell(), FeatureFilterField.this.type.getFeatureType());
				if (dialog.open() == FeatureFilterFormDialog.OK) {
					String filter = dialog.getFilterExpression();
					setFilterExpression(filter);
				}
			}
			
		});
		
		setType(type);
	}
	
	/**
	 * Set the feature type
	 *  
	 * @param type the feature type
	 */
	public void setType(TypeDefinition type) {
		this.type = type;
		
		openForm.setEnabled(type != null);
		insertVar.setEnabled(type != null);
	}
	
	/**
	 * Get the filter expression
	 * 
	 * @return the filter expression
	 */
	public String getFilterExpression() {
		return filterText.getText();
	}
	
	/**
	 * Get the filter
	 * 
	 * @return the filter
	 * @throws CQLException if the filter cannot be created
	 */
	public Filter getFilter() throws CQLException {
		String expr = getFilterExpression();
		if (expr == null || expr.isEmpty()) {
			return null;
		}
		else {
			return CQL.toFilter(expr);
		}
	}
	
	/**
	 * Set the filter expression
	 * 
	 * @param filterExpression the filter expression
	 */
	public void setFilterExpression(String filterExpression) {
		filterText.setText(filterExpression);
		
		notifyListeners();
	}

	/**
	 * @see Widget#dispose()
	 */
	@Override
	public void dispose() {
		openFormImage.dispose();
		insertVarImage.dispose();
		clearFilterImage.dispose();
		
		super.dispose();
	}
	
	/**
	 * Add a filter listener
	 * 
	 * @param listener the filter listener 
	 */
	public void addListener(FilterListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a filter listener
	 * 
	 * @param listener the filter listener 
	 */
	public void removeListener(FilterListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Notify the listeners of a filter change
	 */
	protected void notifyListeners() {
		for (FilterListener listener : listeners) {
			listener.filterChanged();
		}
	}
	
}
