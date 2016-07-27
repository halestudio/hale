/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.server.wms.wizard.pages;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.fhg.igd.mapviewer.server.wms.WMSConfiguration;
import de.fhg.igd.mapviewer.server.wms.wizard.WMSWizardPage;

/**
 * Basic configuration page.
 * 
 * @author Simon Templer
 */
public class BasicConfigurationPage extends WMSWizardPage<WMSConfiguration> {

	/**
	 * Field for WMS location.
	 */
	protected WMSLocationFieldEditor location;

	/**
	 * Field for WMS name.
	 */
	protected ConfigurationNameFieldEditor name = null;

	private Composite page;

	private boolean checkName = true;

	/**
	 * Default constructor
	 * 
	 * @param configuration the WMS configuration
	 */
	public BasicConfigurationPage(WMSConfiguration configuration) {
		super(configuration, Messages.BasicConfigurationPage_0);

		setTitle(Messages.BasicConfigurationPage_1);
		setMessage(Messages.BasicConfigurationPage_2);
	}

	/**
	 * @see WMSWizardPage#updateConfiguration(WMSConfiguration)
	 */
	@Override
	public boolean updateConfiguration(WMSConfiguration configuration) {
		if (location.isValid() && (!checkName || name.isValid())) {
			configuration.setBaseUrl(location.getStringValue());
			configuration.setName(name.getStringValue());
			return true;
		}

		return false;
	}

	/**
	 * @see WMSWizardPage#createContent(Composite)
	 */
	@Override
	public void createContent(Composite parent) {
		page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(3, false));

		createComponent();

		setControl(page);
		update();
	}

	/**
	 * Create components for basic options, the service name and location.
	 */
	protected void createComponent() {
		// name
		String serverName = getConfiguration().getName();
		boolean enterName = serverName == null || serverName.isEmpty();

		name = new ConfigurationNameFieldEditor(getConfiguration());
		name.fillIntoGrid(page, 3);
		name.setStringValue(serverName);
		if (!enterName) {
			checkName = false;
			name.setEnabled(false, page);
		}
		name.setLabelText(Messages.BasicConfigurationPage_3);
		name.setPage(this);
		name.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					update();
				}
			}
		});

		// location
		location = new WMSLocationFieldEditor(page.getDisplay());
		location.fillIntoGrid(page, 3);
		location.setEmptyStringAllowed(false);
		location.setStringValue(getConfiguration().getBaseUrl());
		location.setLabelText(Messages.BasicConfigurationPage_4);
		location.setPage(this);
		location.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					update();
				}
			}
		});
	}

	private void update() {
		setPageComplete(location.isValid() && (!checkName || name.isValid()));
	}

	/**
	 * Get the service URL
	 * 
	 * @return the service URL
	 */
	public String getServiceURL() {
		return location.getStringValue();
	}

	/**
	 * Get the service name
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return name.getStringValue();
	}

	/**
	 * @return basic configuration page
	 */
	public Composite getComposite() {
		return page;
	}
}
