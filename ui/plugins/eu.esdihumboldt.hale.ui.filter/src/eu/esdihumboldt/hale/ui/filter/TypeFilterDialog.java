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
package eu.esdihumboldt.hale.ui.filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.instance.model.Filter;

/**
 * Dialog for configuring a type filter.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class TypeFilterDialog extends TitleAreaDialog {

	private TypeFilterField filterField;

	private Filter filter;

	private final String title;

	private final String message;

	/**
	 * Constructor.
	 * 
	 * @param parentShell the parent shell
	 * @param title the dialog title, <code>null</code> for a default title
	 * @param message the dialog message, <code>null</code> for a default
	 *            message
	 */
	public TypeFilterDialog(Shell parentShell, String title, String message) {
		super(parentShell);

		this.title = (title == null) ? ("Type filter") : (title);
		this.message = (message == null) ? ("Define the filter to apply") : (message);

		setHelpAvailable(true);
	}

	/**
	 * @see Dialog#isResizable()
	 */
	@Override
	protected boolean isResizable() {
		// resizable to allow the HelpTray to appear
		return true;
	}

	/**
	 * @see TitleAreaDialog#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		setTitle(title);
		setMessage(message);

		return control;
	}

	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText(title);
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

		Label filterLabel = new Label(page, SWT.NONE);
		filterLabel.setText("Filter");

		filterField = createFilterField(page);
		filterField.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING)
				.grab(true, false).create());

		filterField.addListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(TypeFilterField.PROPERTY_VALID)) {
					getButton(OK).setEnabled((Boolean) evt.getNewValue());
				}
			}
		});

		return page;
	}

	/**
	 * Creates the filter field to use for the filter creation.
	 * 
	 * @param parent the parent composite
	 * @return the filter field to use
	 */
	protected abstract TypeFilterField createFilterField(Composite parent);

	/**
	 * @see Dialog#createButtonsForButtonBar(Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);

		getButton(OK).setEnabled(filterField.isValid());
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
		filter = filterField.getFilter();
		if (filter != null) {
			super.okPressed();
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
