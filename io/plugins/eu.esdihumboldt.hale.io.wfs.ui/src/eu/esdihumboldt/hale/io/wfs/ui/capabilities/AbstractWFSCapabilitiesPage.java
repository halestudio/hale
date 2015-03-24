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

package eu.esdihumboldt.hale.io.wfs.ui.capabilities;

import java.net.URL;

import javax.annotation.Nullable;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.io.wfs.capabilities.WFSCapabilities;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage;

/**
 * Page for specifying the capabilities URL (and loading the capabilities).
 * 
 * @author Simon Templer
 * @param <T> the configuration object type
 */
public abstract class AbstractWFSCapabilitiesPage<T> extends ConfigurationWizardPage<T> {

	/**
	 * The capabilities URL editor
	 */
	private WFSCapabilitiesFieldEditor location;

	/**
	 * Constructor
	 * 
	 * @param wizard the parent wizard
	 */
	public AbstractWFSCapabilitiesPage(ConfigurationWizard<? extends T> wizard) {
		super(wizard, "wfsCapabilities");
		setTitle("WFS Capabilities");
		setMessage("Please specify the GetCapabilities URL of the WFS");
	}

	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(2, false));

		location = new WFSCapabilitiesFieldEditor("location", "GetCapabilities URL", page);
		location.setPage(this);
		location.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					update();
				}
			}
		});

		String currentValue = getCapabilitiesURL(getWizard().getConfiguration());
		if (currentValue != null) {
			location.setValue(currentValue);
		}

		setControl(page);

		update();
	}

	/**
	 * Determine an initial capabilities URL from the current configuration.
	 * 
	 * @param configuration the configuration object
	 * @return the capabilities URL or <code>null</code>
	 */
	@Nullable
	protected String getCapabilitiesURL(T configuration) {
		return null;
	}

	@Override
	public boolean updateConfiguration(T configuration) {
		if (location.isValid()) {
			boolean result = updateConfiguration(configuration, location.getUsedUrl(),
					location.getCapabilities());
			if (result) {
				updateRecent();
			}
			return result;
		}

		return false;
	}

	/**
	 * Update the configuration
	 * 
	 * @param configuration the WMS configuration
	 * @param capabilitiesUrl the capabilities URL
	 * @param capabilities the loaded capabilities or <code>null</code>
	 * @return if the page is valid
	 */
	protected abstract boolean updateConfiguration(T configuration, URL capabilitiesUrl,
			WFSCapabilities capabilities);

	/**
	 * Update the list of recently used WFSes
	 */
	public void updateRecent() {
		if (location != null) {
			location.updateRecent();
		}
	}

	private void update() {
		setPageComplete(location.isValid());
	}

}
