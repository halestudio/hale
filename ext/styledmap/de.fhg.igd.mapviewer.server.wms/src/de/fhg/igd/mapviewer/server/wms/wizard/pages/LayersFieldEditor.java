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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;

import de.fhg.igd.mapviewer.server.wms.capabilities.Layer;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSCapabilities;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSUtil;

/**
 * Layers field editor.
 * 
 * @author Simon Templer
 */
public class LayersFieldEditor extends StringButtonFieldEditor {

	private static final Log log = LogFactory.getLog(LayersFieldEditor.class);

	private final BasicConfigurationPage conf;

	/**
	 * Constructor
	 * 
	 * @param conf the WMS configuration
	 */
	public LayersFieldEditor(BasicConfigurationPage conf) {
		this.conf = conf;
	}

	/**
	 * @see StringButtonFieldEditor#changePressed()
	 */
	@Override
	protected String changePressed() {
		String layerString = getStringValue();
		try {
			WMSCapabilities capabilities = WMSUtil.getCapabilities(conf.getServiceURL());
			List<Layer> layers = WMSUtil.getLayers(layerString, capabilities);

			LayersDialog dialog = new LayersDialog(getShell(), layers);
			if (dialog.open() == LayersDialog.OK) {
				return WMSUtil.getLayerString(layers, false);
			}
			else {
				return null;
			}
		} catch (Exception e) {
			log.error("Error getting WMS capabilities", e); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * @see StringFieldEditor#setStringValue(java.lang.String)
	 */
	@Override
	public void setStringValue(String value) {
		super.setStringValue(value);

		refreshValidState();
	}

}
