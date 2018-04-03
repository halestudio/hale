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

package eu.esdihumboldt.hale.ui.util.components;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Password field editor that uses {@link ISecurePreferences}
 * 
 * When using it be careful to also include the org.eclipse.equinox.security.ui
 * bundle or add you own secure storage module through the extension point
 * 
 * @author Simon Templer
 */
public class PasswordFieldEditor extends FieldEditor {

	private static ALogger log = ALoggerFactory.getLogger(PasswordFieldEditor.class);

	/**
	 * The label
	 */
	private Label label;

	/**
	 * The text field
	 */
	private Text text;

	private final String secureNodeName;

	private final String keyName;

	private final String name;

	/**
	 * Constructor
	 * 
	 * @param secureNodeName the name of the node in the
	 *            {@link ISecurePreferences}
	 * @param keyName the name of the key representing the password in that node
	 * @param name the field name (displayed in the label)
	 * @param fieldEditorParent the parent composite
	 */
	public PasswordFieldEditor(String secureNodeName, String keyName, String name,
			Composite fieldEditorParent) {
		init(keyName, name);
		this.keyName = keyName;
		this.secureNodeName = secureNodeName;
		this.name = name;

		Layout layout = fieldEditorParent.getLayout();
		doFillIntoGrid(fieldEditorParent,
				(layout instanceof GridLayout) ? (((GridLayout) layout).numColumns) : (2));
	}

	/**
	 * @see FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, numColumns - 1, 1));
	}

	/**
	 * @see FieldEditor#doFillIntoGrid(Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		label = new Label(parent, SWT.NONE);
		label.setText(name);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		text = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);

		adjustForNumColumns(numColumns);
	}

	/**
	 * @see FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {
		try {
			text.setText(
					SecurePreferencesFactory.getDefault().node(secureNodeName).get(keyName, "")); //$NON-NLS-1$
		} catch (StorageException e) {
			text.setText(""); //$NON-NLS-1$
			log.warn("Can't access secure preferences", e); //$NON-NLS-1$
		}
	}

	/**
	 * @see FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		text.setText(""); //$NON-NLS-1$
	}

	/**
	 * @see FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		try {
			String password = text.getText();
			SecurePreferencesFactory.getDefault().node(secureNodeName).put(keyName, password,
					password != null && !password.isEmpty());
		} catch (StorageException e) {
			log.error("Unable to save password to secure preferences", e); //$NON-NLS-1$
		}
	}

	/**
	 * @see FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		return 2;
	}

	/**
	 * Returns this field editor's text control.
	 *
	 * @return the text control
	 */
	public Text getTextControl() {
		return text;
	}

}
