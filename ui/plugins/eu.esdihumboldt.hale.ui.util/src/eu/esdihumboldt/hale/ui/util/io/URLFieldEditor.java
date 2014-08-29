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

package eu.esdihumboldt.hale.ui.util.io;

import java.net.URL;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * A field editor for URLs. Does validation based on the URL syntax and
 * available protocols.
 * 
 * @author Simon Templer
 */
public class URLFieldEditor extends StringFieldEditor {

	/**
	 * Default constructor
	 */
	public URLFieldEditor() {
		super();

		setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		setEmptyStringAllowed(false);
	}

	/**
	 * @see StringFieldEditor#StringFieldEditor(String, String, Composite)
	 */
	public URLFieldEditor(String name, String labelText, Composite parent) {
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
		setErrorMessage("Please specify a valid URL");

		return super.checkState();
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
