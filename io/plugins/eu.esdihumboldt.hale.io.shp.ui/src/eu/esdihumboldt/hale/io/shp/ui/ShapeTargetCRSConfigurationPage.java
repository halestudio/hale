/*
 * Copyright (c) 2020 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.shp.ui;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.io.GeoInstanceWriter;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.SimpleTargetCRSConfigurationPage;

/**
 * Configuration page for the Shape UI.
 * 
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Emanuela Epure
 */
public class ShapeTargetCRSConfigurationPage<P extends GeoInstanceWriter, W extends IOWizard<P>>
		extends SimpleTargetCRSConfigurationPage<P, W> {

	/**
	 * Default constructor.
	 */
	public ShapeTargetCRSConfigurationPage() {
		super(true);
	}

}
