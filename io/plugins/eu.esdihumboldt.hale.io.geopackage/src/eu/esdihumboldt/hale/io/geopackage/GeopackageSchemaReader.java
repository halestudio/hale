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

package eu.esdihumboldt.hale.io.geopackage;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.persist.AbstractCachedSchemaReader;
import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.manager.GeoPackageManager;

/**
 * Reader for geopackage schema.
 * 
 * @author Simon Templer
 */
public class GeopackageSchemaReader extends AbstractCachedSchemaReader {

	@Override
	protected Schema loadFromSource(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Read Geopackage schema", ProgressIndicator.UNKNOWN);
		try {
			URI loc = getSource().getLocation();
			File file;
			try {
				file = new File(loc);
			} catch (Exception e) {
				throw new IllegalArgumentException("Only files are supported as data source", e);
			}

			GeoPackage gpkg = GeoPackageManager.open(file, false);

			Schema schema = new GeopackageSchemaBuilder().buildSchema(gpkg, loc);

			reporter.setSuccess(true);
			return schema;
		} catch (Exception e) {
			reporter.setSuccess(false);
			reporter.error("Error loading schema", e);
			return null;
		} finally {
			progress.end();
		}
	}

	@Override
	protected String getDefaultTypeName() {
		return "Geopackage schema";
	}

}
