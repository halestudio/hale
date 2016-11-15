/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.jdbc.mssql.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;

/**
 * Dialog for selecting crs to use
 * 
 * @author Simon Templer, Arun
 */
public class SelectCRSDialog extends TitleAreaDialog implements IPropertyChangeListener {

	/**
	 * Text field for editing WKT
	 */
	private class WKText {

		private CoordinateReferenceSystem crs;

		private String errorMessage;

		private final Text text;

		/**
		 * Constructor
		 * 
		 * @param parent the parent composite
		 */
		public WKText(Composite parent) {
			text = new Text(parent, SWT.MULTI | SWT.BORDER);

			text.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {
					valueChanged();
				}

			});
		}

		/**
		 * Set the text
		 * 
		 * @param text the text
		 */
		public void setText(String text) {
			this.text.setText(text);

			valueChanged();
		}

		/**
		 * Get the text
		 * 
		 * @return the text
		 */
		public String getText() {
			return text.getText();
		}

		private void valueChanged() {
			try {
				crs = CRS.parseWKT(getText());
				errorMessage = "Invalid WKT"; //$NON-NLS-1$
			} catch (Exception e) {
				errorMessage = e.getLocalizedMessage();
				crs = null;
			}

			updateMessage();
		}

		/**
		 * Get the coordinate reference system
		 * 
		 * @return the coordinate reference system
		 */
		public WKTDefinition getCRSDefinition() {
			return new WKTDefinition(getText(), crs);
		}

		/**
		 * Returns if the field's value is valid
		 * 
		 * @return if the value is valid
		 */
		public boolean isValid() {
			return crs != null;
		}

		/**
		 * @return the errorMessage
		 */
		public String getErrorMessage() {
			return errorMessage;
		}

		/**
		 * @return the text field
		 */
		public Text getTextField() {
			return text;
		}

	}

	/**
	 * Field editor for a CRS code
	 */
	private static class CRSFieldEditor extends StringFieldEditor {

		private CoordinateReferenceSystem crs;

		/**
		 * @see StringFieldEditor#doCheckState()
		 */
		@Override
		protected boolean doCheckState() {
			try {
				crs = CRS.decode(getStringValue());
				boolean valid = crs != null;
				setErrorMessage("Invalid CRS code"); //$NON-NLS-1$
				return valid;
			} catch (Exception e) {
				setErrorMessage(e.getMessage());
				return false;
			}
		}

		/**
		 * Get the coordinate reference system
		 * 
		 * @return the coordinate reference system
		 */
		public CodeDefinition getCRSDefinition() {
			return new CodeDefinition(getStringValue(), crs);
		}

	}

	private CRSFieldEditor crsField;

	private WKText wktField;

	private Button radioCRS;

	private Button radioWKT;

	private Group group;

	private CRSDefinition value;

	private final String instanceCode;

	private static final String DEFAULT_AUTHORITY_NAME = "EPSG";

	private static final String DEFAULT_WKT = "\tPROJCS[\"MGI (Ferro)/AustriaGKWestZone\",\n" + //$NON-NLS-1$
			"\tGEOGCS[\"MGI (Ferro)\",\n" + //$NON-NLS-1$
			"\t\tDATUM[\"Militar-Geographische Institut (Ferro)\",\n" + //$NON-NLS-1$
			"\t\t\tSPHEROID[\"Bessel 1841\",6377397.155,299.1528128,\n" + //$NON-NLS-1$
			"\t\t\tAUTHORITY[\"EPSG\",\"7004\"]],AUTHORITY[\"EPSG\",\"6805\"]],\n" + //$NON-NLS-1$
			"\t\tPRIMEM[\"Ferro\",-17.666666666666668,AUTHORITY[\"EPSG\",\"8909\"]],\n" + //$NON-NLS-1$
			"\t\tUNIT[\"degree\",0.017453292519943295],\n" + //$NON-NLS-1$
			"\t\tAXIS[\"Geodetic latitude\",NORTH],\n" + //$NON-NLS-1$
			"\t\tAXIS[\"Geodetic longitude\",EAST],\n" + //$NON-NLS-1$
			"\t\tAUTHORITY[\"EPSG\",\"4805\"]],\n" + //$NON-NLS-1$
			"\tPROJECTION[\"Transverse Mercator\"],\n" + //$NON-NLS-1$
			"PARAMETER[\"central_meridian\",28.0],\n" + //$NON-NLS-1$
			"PARAMETER[\"latitude_of_origin\",0.0],\n" + //$NON-NLS-1$
			"PARAMETER[\"scale_factor\",1.0],\n" + //$NON-NLS-1$
			"PARAMETER[\"false_easting\",0.0],\n" + //$NON-NLS-1$
			"PARAMETER[\"false_northing\",-5000000.0],\n" + //$NON-NLS-1$
			"UNIT[\"m\",1.0],\n" + //$NON-NLS-1$
			"AXIS[\"Y\",EAST],\n" + //$NON-NLS-1$
			"AXIS[\"X\",NORTH],\n" + //$NON-NLS-1$
			"AUTHORITY[\"EPSG\",\"31251\"]]"; //$NON-NLS-1$

	/**
	 * Constructor
	 * 
	 * @param parentShell the parent shell
	 * @param instanceCode the code which attached to MSSQL instance and not
	 *            available in database
	 */
	public SelectCRSDialog(Shell parentShell, String instanceCode) {
		super(parentShell);
		this.instanceCode = instanceCode;
	}

	/**
	 * @see TitleAreaDialog#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		setTitle("Couldn't find spatial reference id (" + instanceCode + ") from MSSQL database");
		setMessage("");

		updateState();
		updateMessage();

		return control;
	}

	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText("Configure CRS");
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

		group = new Group(page, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setLayout(new GridLayout(3, false));
		group.setText("Coordinate Reference System");

		SelectionListener radioListener = new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSelection();
			}

		};

		// CRS string
		radioCRS = new Button(group, SWT.RADIO);
		radioCRS.setSelection(true);
		radioCRS.setText("CRS code");
		radioCRS.addSelectionListener(radioListener);

		crsField = new CRSFieldEditor();
		crsField.fillIntoGrid(group, 2);
		crsField.setEmptyStringAllowed(false);

		String code = DEFAULT_AUTHORITY_NAME + ":" + instanceCode;

		crsField.setStringValue(code);
		crsField.setPropertyChangeListener(this);
		crsField.setEnabled(true, group);

		// WKT string
		radioWKT = new Button(group, SWT.RADIO);
		radioWKT.setSelection(false);
		radioWKT.setText("Well Known Text");
		radioWKT.addSelectionListener(radioListener);

		wktField = new WKText(group);
		wktField.getTextField().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		wktField.setText(DEFAULT_WKT);
		// wktField.getTextField().setMessage(DEFAULT_WKT);
		wktField.getTextField().setEnabled(false);

		return page;
	}

	/**
	 * Update the widgets to the radio selection
	 */
	protected void updateSelection() {
		boolean enableCode = radioCRS.getSelection();

		crsField.setEnabled(enableCode, group);
		wktField.getTextField().setEnabled(!enableCode);

		updateMessage();
		updateState();
	}

	/**
	 * @see Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		CRSDefinition crs;
		if (radioCRS.getSelection()) {
			crs = crsField.getCRSDefinition();
		}
		else {
			crs = wktField.getCRSDefinition();
		}

		value = crs;

		super.okPressed();
	}

	/**
	 * @see Dialog#cancelPressed()
	 */
	@Override
	protected void cancelPressed() {
		value = null;

		super.cancelPressed();
	}

	/**
	 * Get the selected value
	 * 
	 * @return the selected CRS definition
	 */
	public CRSDefinition getValue() {
		return value;
	}

	/**
	 * @see IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(FieldEditor.VALUE)) {
			CRSFieldEditor editor = (CRSFieldEditor) event.getSource();
			updateMessage(editor);
		}
		else if (event.getProperty().equals(FieldEditor.IS_VALID)) {
			updateState();
		}
	}

	/**
	 * Update the dialog message
	 */
	private void updateMessage() {
		if (radioCRS.getSelection()) {
			updateMessage(crsField);
		}
		else {
			if (wktField.isValid()) {
				setErrorMessage(null);
				setMessage(wktField.getCRSDefinition().getCRS().getName().toString(),
						IMessageProvider.INFORMATION);
			}
			else {
				setErrorMessage(wktField.getErrorMessage());
				setMessage("");
			}
		}
	}

	/**
	 * Update the dialog message
	 * 
	 * @param editor the active editor
	 */
	private void updateMessage(CRSFieldEditor editor) {
		if (editor.isValid()) {
			setErrorMessage(null);
			setMessage(editor.getCRSDefinition().getCRS().getName().toString(),
					IMessageProvider.INFORMATION);
		}
		else {
			setErrorMessage(editor.getErrorMessage());
			setMessage("");
		}
	}

	/**
	 * Update the button state
	 */
	private void updateState() {
		getButton(OK).setEnabled((radioCRS.getSelection() && crsField.isValid())
				|| (radioWKT.getSelection() && wktField.isValid()));
	}

}
