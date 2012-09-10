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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.ui.filter.internal.Messages;

/**
 * Dialog showing the filter form
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@Deprecated
public class FeatureFilterFormDialog extends TitleAreaDialog {

	private final FeatureType featureType;

	private FeatureFilterForm form;

	private String filterExpression;

	/**
	 * Constructor
	 * 
	 * @param parentShell the parent shell
	 * @param featureType the feature type
	 */
	public FeatureFilterFormDialog(Shell parentShell, FeatureType featureType) {
		super(parentShell);

		this.featureType = featureType;
	}

	/**
	 * @see TitleAreaDialog#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		setTitle(Messages.FeatureFilterFormDialog_0); //$NON-NLS-1$
		// setMessage("");

		return control;
	}

	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText(Messages.FeatureFilterFormDialog_1); //$NON-NLS-1$
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

		form = new FeatureFilterForm(featureType, page, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		form.setLayoutData(gd);

		return page;
	}

	/**
	 * Get the filter expression
	 * 
	 * @return the filter expression
	 */
	public String getFilterExpression() {
		return filterExpression;
	}

	/**
	 * @see Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		filterExpression = form.buildCQL();

		super.okPressed();
	}

	/**
	 * @see Dialog#cancelPressed()
	 */
	@Override
	protected void cancelPressed() {
		filterExpression = null;

		super.cancelPressed();
	}

}
