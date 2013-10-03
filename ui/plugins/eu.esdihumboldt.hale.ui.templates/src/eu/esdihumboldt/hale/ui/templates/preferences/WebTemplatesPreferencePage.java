/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.templates.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import eu.esdihumboldt.hale.ui.templates.internal.TemplatesUIPlugin;

/**
 * Preference page for web templates related preferences.
 * 
 * @author Simon Templer
 */
public class WebTemplatesPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage, WebTemplatesPreferences {

	/**
	 * Default constructor.
	 */
	public WebTemplatesPreferencePage() {
		super("Web Templates", FieldEditorPreferencePage.FLAT);
		setPreferenceStore(TemplatesUIPlugin.getDefault().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {
		// ignore
	}

	@Override
	protected void createFieldEditors() {
		// add fields
		addField(new StringFieldEditor(PREF_WEB_TEMPLATES_URL, "Web Templates URL",
				getFieldEditorParent()));
	}

}
