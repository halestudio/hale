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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.geotools.filter.text.cql2.CQLException;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Dialog for configuring a CQL type filter. 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TypeFilterDialog extends TitleAreaDialog {
	
	private final TypeDefinition type;
	
	private TypeFilterField filterField;

	private Filter filter;
	
	/**
	 * Constructor.
	 * @param parentShell the parent shell
	 * @param type the type definition
	 */
	public TypeFilterDialog(Shell parentShell, TypeDefinition type) {
		super(parentShell);
		
		this.type = type;
	}
	
	/**
	 * @see TitleAreaDialog#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		
		setTitle("Type filter"); //$NON-NLS-1$
		setMessage("Define the filter to apply");
		
		return control;
	}

	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setText("Type filter"); //$NON-NLS-1$
	}

	/**
	 * @see TitleAreaDialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		page.setLayoutData(data);
		
		page.setLayout(new GridLayout(1, false));
		
		filterField = new TypeFilterField(type, page, SWT.NONE, null);
		filterField.setLayoutData(GridDataFactory.swtDefaults()
				.align(SWT.FILL, SWT.BEGINNING).grab(true, false).create());
		
		return page;
	}
	
	/**
	 * Get the filter expression
	 * 
	 * @return the filter expression
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * @see Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		try {
			filter = filterField.getCQLFilter(); //XXX what is the preferred way?
			super.okPressed();
		} catch (CQLException e) {
			// ignore, don't close dialog
			//TODO the OK button should be disabled!
		} 
	}

	/**
	 * @see Dialog#cancelPressed()
	 */
	@Override
	protected void cancelPressed() {
		filter = null;
		
		super.cancelPressed();
	}

}
