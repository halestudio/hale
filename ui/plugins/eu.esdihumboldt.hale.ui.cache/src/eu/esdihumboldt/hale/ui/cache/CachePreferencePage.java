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

package eu.esdihumboldt.hale.ui.cache;

import java.io.IOException;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import de.fhg.igd.osgi.util.configuration.JavaPreferencesConfigurationService;
import de.fhg.igd.osgi.util.configuration.NamespaceConfigurationServiceDecorator;
import eu.esdihumboldt.hale.common.cache.Request;
import eu.esdihumboldt.hale.common.core.HalePlatform;

/**
 * The preference page for cache settings.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CachePreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private final IConfigurationService configService;
	private static final String DELIMITER = "/"; //$NON-NLS-1$

	private final PreferenceStore prefs;

	/**
	 * Constructor.
	 */
	public CachePreferencePage() {
		super(GRID);

		IConfigurationService org = HalePlatform.getService(IConfigurationService.class);
		if (org == null) {
			// if no configuration service is present, fall back to new instance

			// 1. use user prefs, may not have rights to access system prefs
			// 2. no default properties
			// 3. default to system properties
			org = new JavaPreferencesConfigurationService(false, null, true);
		}

		configService = new NamespaceConfigurationServiceDecorator(org, Request.class.getPackage()
				.getName().replace(".", DELIMITER), //$NON-NLS-1$
				DELIMITER);

		// TODO implement preference store based on configuration service
		prefs = new PreferenceStore() {

			@Override
			public void load() throws IOException {
				setValue("cache.enabled", configService.getBoolean("hale.cache.enabled", true));
			}

			@Override
			public void save() throws IOException {
				configService.setBoolean("hale.cache.enabled", getBoolean("cache.enabled"));
			}

		};
		try {
			prefs.load();
		} catch (IOException e) {
			// TODO log
		}
		setPreferenceStore(prefs);
	}

	/**
	 * @see IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// nothing to do here
	}

	@Override
	protected void createFieldEditors() {
		// add fields
		addField(new BooleanFieldEditor(
				"cache.enabled", Messages.CachePreferencePage_10, getFieldEditorParent())); //$NON-NLS-1$

		Button clearCache = new Button(getFieldEditorParent(), GRID);
		clearCache.setText(Messages.CachePreferencePage_15);
		clearCache.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Request.getInstance().clear();
			}

		});
		clearCache.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 1, 1));
	}

	/**
	 * @see FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		Request.getInstance().setCacheEnabled(prefs.getBoolean("cache.enabled")); //$NON-NLS-1$

		return super.performOk();
	}
}
