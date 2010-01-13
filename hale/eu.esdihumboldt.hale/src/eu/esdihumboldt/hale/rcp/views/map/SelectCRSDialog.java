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
package eu.esdihumboldt.hale.rcp.views.map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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

import eu.esdihumboldt.hale.models.InstanceService;

/**
 * Dialog for selecting the CRS to use
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SelectCRSDialog extends TitleAreaDialog implements IPropertyChangeListener {
	
	private static Logger _log = Logger.getLogger(SelectCRSDialog.class);
	
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
		 * @param parent
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
				errorMessage = "Invalid WKT";
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
		public CoordinateReferenceSystem getCRS() {
			return crs;
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
	private class CRSFieldEditor extends StringFieldEditor {
		
		private CoordinateReferenceSystem crs;

		/**
		 * @see StringFieldEditor#doCheckState()
		 */
		@Override
		protected boolean doCheckState() {
			try {
				crs = CRS.decode(getStringValue());
				boolean valid = crs != null;
				setErrorMessage("Invalid CRS code");
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
		public CoordinateReferenceSystem getCRS() {
			return crs;
		}

	}
	
	/**
	 * true = EPSG code is used, false = WKT is used
	 */
	private static boolean lastWasCode = true;

	private static String lastCRS = "EPSG:4326";
	
	private static String lastWKT = 
		"PROJCS[\"MGI (Ferro)/AustriaGKWestZone\",\n" +
			"\tGEOGCS[\"MGI (Ferro)\",\n" +
				"\t\tDATUM[\"Militar-Geographische Institut (Ferro)\",\n" +
					"\t\t\tSPHEROID[\"Bessel 1841\",6377397.155,299.1528128,\n" +
					"\t\t\tAUTHORITY[\"EPSG\",\"7004\"]],AUTHORITY[\"EPSG\",\"6805\"]],\n" +
				"\t\tPRIMEM[\"Ferro\",-17.666666666666668,AUTHORITY[\"EPSG\",\"8909\"]],\n" +
				"\t\tUNIT[\"degree\",0.017453292519943295],\n" +
				"\t\tAXIS[\"Geodetic latitude\",NORTH],\n" +
				"\t\tAXIS[\"Geodetic longitude\",EAST],\n" +
				"\t\tAUTHORITY[\"EPSG\",\"4805\"]],\n" +
			"\tPROJECTION[\"Transverse Mercator\"],\n" +
		"PARAMETER[\"central_meridian\",28.0],\n" +
		"PARAMETER[\"latitude_of_origin\",0.0],\n" +
		"PARAMETER[\"scale_factor\",1.0],\n" +
		"PARAMETER[\"false_easting\",0.0],\n" +
		"PARAMETER[\"false_northing\",-5000000.0],\n" +
		"UNIT[\"m\",1.0],\n" +
		"AXIS[\"Y\",EAST],\n" +
		"AXIS[\"X\",NORTH],\n" +
		"AUTHORITY[\"EPSG\",\"31251\"]]";
	
	private CRSFieldEditor crsField;
	
	private WKText wktField;
	
	private static CoordinateReferenceSystem value;
	
	//private static boolean initialized = false;
	
	private static final String DEF_MESSAGE = "";
	
	private Button radioCRS;
	
	private Button radioWKT;
	
	private Group group;

	/**
	 * Constructor
	 * 
	 * @param parentShell
	 */
	public SelectCRSDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public static boolean lastWasCode() {
		return lastWasCode;
	}

	/**
	 * @see TitleAreaDialog#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		
		setTitle("Please specify the CRS to use");
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
		
		newShell.setText("Unable to determine CRS");
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
		radioCRS.setSelection(lastWasCode);
		radioCRS.setText("CRS code");
		radioCRS.addSelectionListener(radioListener);
		
		crsField = new CRSFieldEditor();
		crsField.fillIntoGrid(group, 2);
		crsField.setEmptyStringAllowed(false);
		crsField.setStringValue(lastCRS);
		crsField.setPropertyChangeListener(this);
		crsField.setEnabled(lastWasCode, group);
		
		// WKT string
		radioWKT = new Button(group, SWT.RADIO);
		radioWKT.setSelection(!lastWasCode);
		radioWKT.setText("Well Known Text");
		radioWKT.addSelectionListener(radioListener);
		
		wktField = new WKText(group);
		wktField.getTextField().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		wktField.setText(lastWKT);
		wktField.getTextField().setEnabled(!lastWasCode);
		
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
		if (radioCRS.getSelection()) {
			lastCRS = crsField.getStringValue();
			lastWasCode = true;
			value = crsField.getCRS();
		}
		else {
			lastWKT = wktField.getText();
			lastWasCode = false;
			value = wktField.getCRS();
		}
		
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
	 * @return the value
	 */
	public static CoordinateReferenceSystem getValue() {
		return value;
	}
	
	public static void setWkt(String wkt) {
		SelectCRSDialog.lastWKT = wkt;
		SelectCRSDialog.lastWasCode = false;
		try {
			value = CRS.parseWKT(SelectCRSDialog.lastWKT);
		} catch (Exception e) {
			_log.error("WKT could not be parsed: ", e);
		}
	}
	
	public static void setEpsgcode(String epsgcode) {
		SelectCRSDialog.lastCRS = epsgcode;
		SelectCRSDialog.lastWasCode = true;
		try {
			value = CRS.decode(SelectCRSDialog.lastCRS);
		} catch (Exception e) {
			_log.error("EPSG code could not be decoded: ", e);
		}
	}
	
	public static String getValueWKT(){
		return SelectCRSDialog.lastWKT;
	}

	/**
	 * Determine the CRS to use, the CRS can be retrieved using {@link #getValue()}
	 * 
	 * @return if a new value was set or an existing valid value exists
	 */
	public boolean determineCRS() {
		init();
		
		if (value != null) {
			// valid crs, not reseted
			return true;
		}
		else {
			return open() == SelectCRSDialog.OK;
		}
	}

	/**
	 * Register listener with {@link InstanceService}
	 */
	private static void init() {
		/*XXX reseting on service update is to late - if (!initialized) {
			InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
			is.addListener(new HaleServiceListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void update(UpdateMessage message) {
					// invalidate calue
					value = null;
				}
				
			});
			
			initialized = true;
		}*/
	}
	
	/**
	 * Reset the custom CRS
	 */
	public static void resetCustomCRS() {
		value = null;
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
				setMessage(wktField.getCRS().getName().toString(), IMessageProvider.INFORMATION);
			}
			else {
				setErrorMessage(wktField.getErrorMessage());
				setMessage(DEF_MESSAGE);
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
			setMessage(editor.getCRS().getName().toString(), IMessageProvider.INFORMATION);
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
		getButton(OK).setEnabled(
				(radioCRS.getSelection() && crsField.isValid()) ||
				(radioWKT.getSelection() && wktField.isValid()));
	}

}
