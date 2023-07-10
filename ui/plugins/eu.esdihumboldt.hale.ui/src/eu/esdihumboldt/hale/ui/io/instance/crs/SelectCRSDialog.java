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
package eu.esdihumboldt.hale.ui.io.instance.crs;

import java.text.MessageFormat;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.geotools.referencing.CRS;
import org.geotools.referencing.datum.DefaultGeodeticDatum;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;

import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.ui.internal.Messages;

/**
 * Dialog for selecting the CRS to use
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SelectCRSDialog extends TitleAreaDialog implements IPropertyChangeListener {

//	private static ALogger _log = ALoggerFactory.getLogger(SelectCRSDialog.class);

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

	private static final String DEFAULT_CODE = "EPSG:4258"; //$NON-NLS-1$

	private static String lastCode = DEFAULT_CODE;
	private static String lastWKT;
	static {
		try {
			lastWKT = CRS.decode(DEFAULT_CODE).toWKT();
		} catch (Exception e) {
			lastWKT = "GEOGCS[\"ETRS89\",\n" + //$NON-NLS-1$
					"\tDATUM[\"European_Terrestrial_Reference_System_1989\",\n" + //$NON-NLS-1$
					"\t\tSPHEROID[\"GRS 1980\",6378137,298.257222101,\n" + //$NON-NLS-1$
					"\t\t\tAUTHORITY[\"EPSG\",\"7019\"]],\n" + //$NON-NLS-1$
					"\t\tTOWGS84[0,0,0,0,0,0,0],\n" + //$NON-NLS-1$
					"\t\tAUTHORITY[\"EPSG\",\"6258\"]],\n" + //$NON-NLS-1$
					"\tPRIMEM[\"Greenwich\",0,\n" + //$NON-NLS-1$
					"\t\tAUTHORITY[\"EPSG\",\"8901\"]],\n" + //$NON-NLS-1$
					"\tUNIT[\"degree\",0.0174532925199433,\n" + //$NON-NLS-1$
					"\t\tAUTHORITY[\"EPSG\",\"9122\"]],\n" + //$NON-NLS-1$
					"\tAUTHORITY[\"EPSG\",\"4258\"]]"; //$NON-NLS-1$
		}
	}

	private CRSFieldEditor crsField;

	private WKText wktField;

	private Label wktWarning;

	private static final String DEF_MESSAGE = ""; //$NON-NLS-1$

	private Button radioCRS;

	private Button radioWKT;

	private Group group;

	private CRSDefinition value;

	/**
	 * Constructor
	 * 
	 * @param parentShell the parent shell
	 * @param initalValue the initial value
	 */
	public SelectCRSDialog(Shell parentShell, CRSDefinition initalValue) {
		super(parentShell);

		value = initalValue;
	}

	/**
	 * @see TitleAreaDialog#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		setTitle(Messages.SelectCRSDialog_ContentTitle);
		setMessage(DEF_MESSAGE);

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

		newShell.setText(Messages.SelectCRSDialog_ShellTitle);
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
		group.setText(Messages.SelectCRSDialog_GroupText);

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

		boolean lastWasCode = value == null || value instanceof CodeDefinition;

		// CRS string
		radioCRS = new Button(group, SWT.RADIO);
		radioCRS.setSelection(lastWasCode);
		radioCRS.setText(Messages.SelectCRSDialog_RadioCRSText);
		radioCRS.addSelectionListener(radioListener);

		crsField = new CRSFieldEditor();
		crsField.fillIntoGrid(group, 2);
		crsField.setEmptyStringAllowed(false);
		String code;
		if (value instanceof CodeDefinition) {
			code = ((CodeDefinition) value).getCode();
		}
		else if (value instanceof WKTDefinition) {
			// Don't show possibly contradicting code
			code = "";
		}
		else {
			code = lastCode;
		}
		crsField.setStringValue(code);
		crsField.setPropertyChangeListener(this);
		crsField.setEnabled(lastWasCode, group);

		// WKT string
		radioWKT = new Button(group, SWT.RADIO);
		radioWKT.setSelection(!lastWasCode);
		radioWKT.setText(Messages.SelectCRSDialog_RadioWKTText);
		radioWKT.addSelectionListener(radioListener);

		wktField = new WKText(group);
		wktField.getTextField().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		String wkt;
		if (value instanceof WKTDefinition) {
			wkt = ((WKTDefinition) value).getWkt();
		}
		else if (value instanceof CodeDefinition) {
			// Display WKT corresponding to the given code, if possible
			try {
				wkt = ((CodeDefinition) value).getCRS().toWKT();
			} catch (Exception e) {
				// Avoid displaying possibly contradicting WKT
				wkt = "";
			}
		}
		else {
			wkt = lastWKT;
		}
		wktField.setText(wkt);
		wktField.getTextField().setEnabled(!lastWasCode);

		new Label(group, SWT.NONE);

		wktWarning = new Label(group, SWT.NONE);
		wktWarning.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		wktWarning.setText("Please be aware that this WKT definition does not contain Bursa-Wolf\n"
				+ "parameters in the DATUM definition. Therefore, hale»studio may not be able\n"
				+ "to perform accurate coordinate transformations when a datum shift is involved.\n"
				+ "If you intend to perform coordinate transformations with a datum shift,\n"
				+ "please provide the Bursa-Wolf parameters (TOWGS84) or provide an appropriate\n"
				+ "CRS code instead. If no Bursa-Wolf parameters are required for the datum of\n"
				+ "this CRS (e.g. in case of WGS 84 itself), you can ignore this warning.");
		wktWarning.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));

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
			lastCode = crsField.getCRSDefinition().getCode();
		}
		else {
			crs = wktField.getCRSDefinition();
			lastWKT = wktField.getCRSDefinition().getWkt();
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
			if (wktWarning != null) {
				wktWarning.setVisible(false);
			}
			updateMessage(crsField);
		}
		else {
			if (wktField.isValid()) {
				setErrorMessage(null);

				CoordinateReferenceSystem wktCrs = wktField.getCRSDefinition().getCRS();
				boolean bursaWolfApplicable = false;
				boolean bursaWolfAvailable = false;
				if (wktCrs instanceof SingleCRS
						&& ((SingleCRS) wktCrs).getDatum() instanceof DefaultGeodeticDatum) {
					// Check for Bursa-Wolf parameters
					bursaWolfApplicable = true;

					DefaultGeodeticDatum datum = (DefaultGeodeticDatum) ((SingleCRS) wktCrs)
							.getDatum();
					bursaWolfAvailable = datum.getBursaWolfParameters().length > 0;
				}

				boolean showWktWarning = bursaWolfApplicable && !bursaWolfAvailable;

				setMessage(wktField.getCRSDefinition().getCRS().getName().toString(),
						showWktWarning ? IMessageProvider.WARNING : IMessageProvider.INFORMATION);

				if (wktWarning != null) {
					wktWarning.setVisible(showWktWarning);
				}
			}
			else {
				setErrorMessage(wktField.getErrorMessage());
				setMessage(DEF_MESSAGE, IMessageProvider.INFORMATION);
				wktWarning.setVisible(false);
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
			CoordinateReferenceSystem crs = editor.getCRSDefinition().getCRS();
			if (crs.getIdentifiers().isEmpty()) {
				setMessage(crs.getName().toString(), IMessageProvider.INFORMATION);
			}
			else {
				ReferenceIdentifier firstId = crs.getIdentifiers().iterator().next();
				setMessage(
						MessageFormat.format("[{0}:{1}] {2}", firstId.getCodeSpace(),
								firstId.getCode(), crs.getName().getCode()),
						IMessageProvider.INFORMATION);
			}
		}
		else {
			setErrorMessage(editor.getErrorMessage());
			setMessage(DEF_MESSAGE);
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
