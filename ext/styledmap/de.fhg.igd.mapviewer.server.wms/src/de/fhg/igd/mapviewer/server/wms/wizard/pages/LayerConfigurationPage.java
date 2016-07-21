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
 * Layer configuration page.
 * 
 * @author Simon Templer
 */
public class LayerConfigurationPage extends WMSWizardPage<WMSConfiguration>
		implements IPropertyChangeListener {

	private LayersFieldEditor layers;

	private final BasicConfigurationPage conf;

	/**
	 * Constructor
	 * 
	 * @param conf the basic WMS configuration page
	 * @param configuration the WMS configuration
	 */
	public LayerConfigurationPage(final BasicConfigurationPage conf,
			WMSConfiguration configuration) {
		super(configuration, Messages.LayerConfigurationPage_0);

		setTitle(Messages.LayerConfigurationPage_1);
		setMessage(Messages.LayerConfigurationPage_2);

		this.conf = conf;
	}

	/**
	 * @see WMSWizardPage#updateConfiguration(WMSConfiguration)
	 */
	@Override
	public boolean updateConfiguration(WMSConfiguration configuration) {
		if (layers.isValid()) {
			configuration.setLayers(layers.getStringValue());
			return true;
		}

		return false;
	}

	/**
	 * @see WMSWizardPage#createContent(Composite)
	 */
	@Override
	public void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);

		page.setLayout(new GridLayout(3, false));

		// layers
		layers = new LayersFieldEditor(conf);
		layers.fillIntoGrid(page, 3);
		layers.setLabelText(Messages.LayerConfigurationPage_3);
		layers.setEmptyStringAllowed(true);
		layers.setStringValue(getConfiguration().getLayers());
		layers.setPage(this);
		layers.setPropertyChangeListener(this);

		setControl(page);

		update();
	}

	private void update() {
		setPageComplete(layers.isValid());
	}

	/**
	 * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(FieldEditor.IS_VALID)) {
			update();
		}
	}

}
