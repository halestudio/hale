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

package eu.esdihumboldt.hale.ui.filter;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
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
import org.eclipse.ui.PlatformUI;
import org.geotools.filter.text.cql2.CQLException;

import eu.esdihumboldt.hale.common.filter.FilterGeoCqlImpl;
import eu.esdihumboldt.hale.common.filter.FilterGeoECqlImpl;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.definition.selector.PropertyDefinitionDialog;
import eu.esdihumboldt.hale.ui.filter.internal.FilterUIPlugin;
import eu.esdihumboldt.hale.ui.filter.internal.Messages;

/**
 * Field for editing a filter.
 * @author Simon Templer
 * @author Sebastian Reinhardt
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
	//private final Button openForm;
	private final Button insertVar;
	private final Button clearFilter;
	private ControlDecoration decoration;
	
	private TypeDefinition type;
	private SchemaSpaceID ssid;
	
	private final Image insertVarImage;
	private final Image openFormImage;
	private final Image clearFilterImage;
	
	private final Set<FilterListener> listeners = new HashSet<FilterListener>();

	/**
	 * Create a new filter field for a given type.
	 * 
	 * @param type the type definition
	 * @param parent the parent composite
	 * @param style the composite style
	 * @param ssid the schema space
	 */
	public FeatureFilterField(TypeDefinition type, Composite parent, int style, 
			SchemaSpaceID ssid) {
		super(parent, style);
		
		this.type = type;
		this.ssid = ssid;
		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 8;
		layout.verticalSpacing = 0;
		
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		
		// images
		insertVarImage = FilterUIPlugin.getImageDescriptor("icons/insert.gif").createImage(); //$NON-NLS-1$
		openFormImage = FilterUIPlugin.getImageDescriptor("icons/form.gif").createImage(); //$NON-NLS-1$
		
		clearFilterImage = CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_REMOVE);
		
		// create components
		
		// text field
		filterText = new Text(this, SWT.SINGLE | SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterText.setToolTipText(Messages.FeatureFilterField_3); //$NON-NLS-1$
		filterText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if(filterText.getText() != null && !filterText.getText().isEmpty()){
					clearFilter.setEnabled(true);
				}
				else{
					setDecorationDefault();
				}
				notifyListeners();
			}
			
		});
		PlatformUI.getWorkbench().getHelpSystem().setHelp(filterText,
				"eu.esdihumboldt.hale.doc.user.filter_field");

		decoration = new ControlDecoration(filterText, SWT.RIGHT | SWT.TOP);
		setDecorationDefault();

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
				PropertyDefinitionDialog dialog = new PropertyDefinitionDialog(
						Display.getCurrent().getActiveShell(),
						FeatureFilterField.this.ssid, 
						FeatureFilterField.this.type,
						Messages.FeatureFilterField_7, null);
				
				if (dialog.open() == PropertyDefinitionDialog.OK && dialog.getObject() != null
						&& dialog.getObject().getType().getName().toString().length() >= 1) {
					String var = "";
					for(int i = 0; i< dialog.getObject().getPropertyPath().size(); i++) {
					if(i == 0) var = var.concat(dialog.getObject().getPropertyPath().get(i).getChild().getName().getLocalPart().toString());
					else var = var.concat("." + dialog.getObject().getPropertyPath().get(i).getChild().getName().getLocalPart().toString());
					}
					filterText.insert(var);
					filterText.setFocus();
				}
			}
			
		});
		
		// open form
//		openForm = new Button(this, SWT.PUSH);
//		
//		openForm.setImage(openFormImage);
//		openForm.setToolTipText(Messages.FeatureFilterField_9); //$NON-NLS-1$
//		openForm.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
//		openForm.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				FeatureFilterFormDialog dialog = new FeatureFilterFormDialog(
//						Display.getCurrent().getActiveShell(), FeatureFilterField.this.type.getFeatureType());
//				if (dialog.open() == FeatureFilterFormDialog.OK) {
//					String filter = dialog.getFilterExpression();
//					setFilterExpression(filter);
//				}
//			}
//			
//		});
//		
//		setType(type);
	}
	
	/**
	 * Set the feature type
	 *  
	 * @param type the feature type
	 */
	public void setType(TypeDefinition type) {
		this.type = type;
		
	//	openForm.setEnabled(type != null);
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
	 * @return the cql filter
	 * @throws CQLException if the filter cannot be created
	 */
	public Filter getCQLFilter() throws CQLException  {
		String expr = getFilterExpression();
		if (expr == null || expr.isEmpty()) {
			return null;
		}
		else {
			
			
			return new FilterGeoCqlImpl(expr);
		}
	}
	
	/**
	 * Get the filter
	 * 
	 * @return the ecql filter
	 * @throws CQLException if the filter cannot be created
	 */
	public Filter getECQLFilter() throws CQLException  {
		String expr = getFilterExpression();
		if (expr == null || expr.isEmpty()) {
			return null;
		}
		else {
			return new FilterGeoECqlImpl(expr);
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

	/**
	 * Set the field decoration.
	 * FIXME not very nice, why exposed at all? 
	 * @param type the message type, either WARNING, ERROR or DEFAULT
	 * @param message the decoration message
	 */
	public void setDecoration(String type, String message) {
		if (type.equals("ERROR")) {
			decoration.setImage(FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
					.getImage());
			decoration.setDescriptionText(message);
			decoration.show();
		}

		if (type.equals("DEFAULT")) {
			setDecorationDefault();
		}
		if (type.equals("WARNING")) {
			decoration.setImage(FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING)
					.getImage());
			decoration.setDescriptionText(message);
			decoration.show();
		}
	}
	
	private void setDecorationDefault(){
		 decoration.setImage(FieldDecorationRegistry.getDefault()
	                .getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
		 decoration.setDescriptionText("for Example: \"HERP.DERP\" = 'DURR'");
		 decoration.show();
	}
	
}
