/*
 * Copyright (c) 2022 wetransform GmbH
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

package eu.esdihumboldt.hale.io.json;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.io.shp.writer.ShapefileInstanceWriter;
import json.topojson.api.TopojsonApi;

/**
 * Writes instances as TopoJSON.
 * 
 * @author Flaminia Catalli
 */
public class TopoJSONInstanceWriter extends JsonInstanceWriter {

	private final CRSDefinition targetCrs = new CodeDefinition("EPSG:4326", true); // TODO:
																					// check
																					// if
																					// CRS
																					// is
																					// correct

//	/**
//	 * Parameter name for the default geometry association.
//	 * 
//	 * @deprecated as of release 4.2.0 because we don't use geometry
//	 *             configuration for geoJson and export the data in WG84 format.
//	 *             As geoJson expects WGS 84 with lon/lat (see
//	 *             https://tools.ietf.org/html/rfc7946)
//	 */
//	@Deprecated
//	public static final String PARAM_GEOMETRY_CONFIG = "geojson.geometry.config";

	/**
	 * By default do not use geoJson or topoJson features for the output.
	 */
	public TopoJSONInstanceWriter() {
		super(false, true);
	}

	@Override
	protected String getDefaultTypeName() {
		return "TopoJSON";

	}

	/**
	 * @see eu.esdihumboldt.hale.io.json.JsonInstanceWriter#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		progress.begin("Generating " + getDefaultTypeName(), ProgressIndicator.UNKNOWN);

		URI location = getTarget().getLocation();
		File shpFile = File.createTempFile("intermediate_shape_file", ".shp");
		InstanceCollection instances = getInstances();

		try {
			// 1. write instances into a shape file
			// 2. convert the shape file into topoJson
			// URI location = getTarget().getLocation();
			// InstanceCollection instances = getInstances();
			writeInstanceCollectionToShp(instances, progress, reporter, shpFile.toURI());
			writeShpfileToTopojson(shpFile.getAbsolutePath(), targetCrs.toString(),
					new File(location).getAbsolutePath(), "test", 1, 4, false);
			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl(
					String.format("Error generating %s file", getDefaultTypeName()), e));
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}
		return reporter;

	}

	private void writeInstanceCollectionToShp(InstanceCollection instances,
			ProgressIndicator progress, IOReporter reporter, URI location) {

		ShapefileInstanceWriter writer = new ShapefileInstanceWriter();
		try {
			System.out.println("entered");
			writer.writeInstances(instances, progress, reporter, location);
		} catch (IOException e) {
			reporter.error(new IOMessageImpl(String.format("Error generating Shapefile"), e));
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}

	}

	private void writeShpfileToTopojson(String iFileNameInput, String iCoordinateSystem,
			String iFileOuput, String iTopoName, int iKink, int iQuantizeDigit, boolean iCompress) {

		try {
			TopojsonApi.shpToTopojsonFile(iFileNameInput, iCoordinateSystem, iFileOuput, iTopoName,
					iKink, iQuantizeDigit, iCompress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
