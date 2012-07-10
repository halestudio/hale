/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.io.util;

import java.net.URI;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * A field editor for URIs. Does validation based on the URI syntax.
 * @author Simon Templer
 */
public class URIFieldEditor extends StringFieldEditor {
	
	/**
	 * Default constructor
	 */
	public URIFieldEditor() {
		super();
		
		setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		setEmptyStringAllowed(false);
	}

	/**
	 * @see StringFieldEditor#StringFieldEditor(String, String, Composite)
	 */
	public URIFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		
		setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		setEmptyStringAllowed(false);
	}

	/**
	 * @see StringFieldEditor#checkState()
	 */
	@Override
	protected boolean checkState() {
		// reset error message in case of an empty string
		setErrorMessage("Please specify a valid URI");
		
		return super.checkState();
	}

	/**
	 * @see StringFieldEditor#doCheckState()
	 */
	@Override
	protected boolean doCheckState() {
		final String value = getStringValue();
		
		try {
			new URI(value);
		} catch (Throwable e) {
			setErrorMessage(e.getLocalizedMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	 * Get the URI value.
	 * @return the URL or <code>null</code> if the content is no valid URI.
	 */
	public URI getURI() {
		try {
			return new URI(getStringValue());
		} catch (Throwable e) {
			setErrorMessage(e.getLocalizedMessage());
			return null;
		}
	}

}
