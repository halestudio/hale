/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.io.gml.ui.wfs;

import java.net.URL;

import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.io.gml.ui.wfs.wizard.WfsDescribeFeatureConfiguration;
import eu.esdihumboldt.hale.io.gml.ui.wfs.wizard.WfsDescribeFeatureWizard;
import eu.esdihumboldt.hale.io.gml.ui.wfs.wizard.WfsGetFeatureConfiguration;
import eu.esdihumboldt.hale.io.gml.ui.wfs.wizard.WfsGetFeatureWizard;

/**
 * This editor can be used to select a valid {@link URL} for a WFS to retrieve
 * schema information from. It delegates all details to the
 * {@link WfsDescribeFeatureWizard} and {@link WfsGetFeatureWizard}.
 * 
 * @author Thorsten Reitz
 * @author Jan Kolar
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 02 / Intergraph CS
 */
public class WfsUrlFieldEditor extends StringButtonFieldEditor {

	private final static ALogger _log = ALoggerFactory.getLogger(WfsUrlFieldEditor.class);

	private boolean _getFeatures = false;

	private final String schemaNamespace;

	/**
	 * Constructor
	 * 
	 * @param name the preference name
	 * @param labelText the label text
	 * @param parent the parent composite
	 */
	public WfsUrlFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);

		setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		setEmptyStringAllowed(false);

		this.schemaNamespace = null;
	}

	/**
	 * Constructor
	 * 
	 * @param name the preference name
	 * @param labelText the label text
	 * @param parent the parent composite
	 * @param schemaNamespace the schema namespace, may be <code>null</code>
	 * @param getFeatures if the editor is for a GetFeature request instead of a
	 *            DescribeFeature request
	 */
	public WfsUrlFieldEditor(String name, String labelText, Composite parent,
			String schemaNamespace, boolean getFeatures) {
		super(name, labelText, parent);
		this._getFeatures = getFeatures;

		setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		setEmptyStringAllowed(false);

		this.schemaNamespace = schemaNamespace;
	}

	/**
	 * @see StringFieldEditor#checkState()
	 */
	@Override
	protected boolean checkState() {
		// reset error message in case of an empty string
		setErrorMessage("Please specify a valid URL");

		return super.checkState();
	}

	/**
	 * @see StringButtonFieldEditor#changePressed()
	 */
	@Override
	protected String changePressed() {
		URL result = null;

		if (!this._getFeatures) {
			WfsDescribeFeatureConfiguration conf = new WfsDescribeFeatureConfiguration();
			WfsDescribeFeatureWizard describeFeatureWizard = new WfsDescribeFeatureWizard(conf);
			WizardDialog dialog = new WizardDialog(this.getShell(), describeFeatureWizard);
			if (dialog.open() == WizardDialog.OK) {
				try {
					result = conf.getRequestURL();
				} catch (Throwable e) {
					_log.userError("Error getting the request URL", e); //$NON-NLS-1$
				}
			}
		}
		else {
			WfsGetFeatureConfiguration conf = new WfsGetFeatureConfiguration(schemaNamespace);
			WfsGetFeatureWizard getFeatureWizard = new WfsGetFeatureWizard(conf);
			WizardDialog dialog = new WizardDialog(this.getShell(), getFeatureWizard);
			if (dialog.open() == WizardDialog.OK) {
				try {
					result = conf.getRequestURL();
				} catch (Throwable e) {
					_log.userError("Error getting the request URL", e); //$NON-NLS-1$
				}
			}
		}

		if (result != null) {
			return result.toString();
		}
		else { // applicable if cancel is pressed.
			return null; //$NON-NLS-1$
		}
	}

	/**
	 * @see StringFieldEditor#doCheckState()
	 */
	@Override
	protected boolean doCheckState() {
		final String value = getStringValue();

		try {
			new URL(value);
		} catch (Throwable e) {
			setErrorMessage(e.getLocalizedMessage());
			return false;
		}

		return true;
	}

	/**
	 * Get the URL value.
	 * 
	 * @return the URL or <code>null</code> if the content is no valid URL.
	 */
	public URL getURL() {
		try {
			return new URL(getStringValue());
		} catch (Throwable e) {
			setErrorMessage(e.getLocalizedMessage());
			return null;
		}
	}

}
