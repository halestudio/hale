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

package eu.esdihumboldt.hale.io.topojson;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.MultiLocationOutputSupplier;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.io.shp.writer.ShapefileInstanceWriter;
import json.topojson.api.TopojsonApi;

/**
 * Writes instances as TopoJSON by first creating an intermediate Shapefile and
 * then converting the Shapefile to TopoJSON. Supports only local file targets.
 * 
 * If the Shapefile writer creates multiple intermediate files (e.g. because the
 * target schema contains multiple types and/or multiple geometry types), this
 * instance writer will also create multiple TopoJSON files with a numeric
 * suffix added to the configured output file name. So if the configured output
 * file name is <code>output.json</code>, the created files will be named
 * <code>output_1.json</code>, <code>output_2.json</code>, etc.
 * 
 * @author Flaminia Catalli
 * @author Florian Esser
 */
public class TopoJsonInstanceWriter extends AbstractInstanceWriter {

	/**
	 * Parameter for TopoJSON generation that controls how the polygons are
	 * simplified. A value of 0 means that no simplification will take place.
	 * 
	 * TODO make configurable
	 */
	private static final int KINK_VALUE = 0;

	/**
	 * Parameter for TopoJSON generation that controls if and how the TopoJSON
	 * output is quantized.
	 * 
	 * TODO make configurable
	 */
	private static final int QUANTIZE_DIGIT_VALUE = 4;

	/**
	 * Name of the generated topology
	 * 
	 * TODO make configurable
	 */
	private static final String DEFAULT_TOPOLOGY_NAME = "Topology";

	/**
	 * TopoJSON, as a descendant of GeoJSON, requires geometries to be
	 * represented in WGS-84 (EPSG:4326)
	 */
	private static final String TARGET_CRS_CODE = "EPSG:4326";

	private final CRSDefinition targetCrs = new CodeDefinition(TARGET_CRS_CODE, true);

	@Override
	protected String getDefaultTypeName() {
		return "TopoJSON";
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		progress.begin("Generating " + getDefaultTypeName(), ProgressIndicator.UNKNOWN);

		try {
			URI location = getTarget().getLocation();
			InstanceCollection instances = getInstances();

			File tempShapefile = File.createTempFile("topojsontmp", ".shp");
			tempShapefile.deleteOnExit();

			List<URI> shapefilesWritten = writeIntermediateShapefiles(instances, progress, reporter,
					tempShapefile);

			progress.begin("Generating " + getDefaultTypeName(), ProgressIndicator.UNKNOWN);

			String targetPath = Paths.get(location).getParent().toString();
			String baseFilename = Paths.get(location).getFileName().toString();
			String extension = baseFilename.substring(baseFilename.lastIndexOf("."));
			baseFilename = baseFilename.substring(0, baseFilename.lastIndexOf("."));

			int i = 1;
			for (URI shapefileUri : shapefilesWritten) {
				File targetFile = new File(targetPath + "/" + baseFilename
						+ ((shapefilesWritten.size() > 1) ? "_" + i++ : "") + extension);
				convertShapefileToTopoJson(new File(shapefileUri).getAbsolutePath(),
						targetCrs.getCRS(), targetFile.getAbsolutePath(), DEFAULT_TOPOLOGY_NAME,
						KINK_VALUE, QUANTIZE_DIGIT_VALUE, false);
			}

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

	private List<URI> writeIntermediateShapefiles(InstanceCollection instances,
			ProgressIndicator progress, IOReporter reporter, File targetFile) {

		List<URI> filesWritten = new ArrayList<URI>();

		ShapefileInstanceWriter writer = new ShapefileInstanceWriter();
		try {
			writer.setTargetCRS(targetCrs);
			writer.setTarget(new FileIOSupplier(targetFile));
			writer.setInstances(instances);
			writer.execute(progress);

			// ShapefileInstanceWriter will update its getTarget() result
			// if there were multiple Shapefiles written. In that case,
			// getTarget() will return a MultiLocationOutputSupplier.
			LocatableOutputSupplier<? extends OutputStream> targets = writer.getTarget();
			if (targets instanceof MultiLocationOutputSupplier) {
				filesWritten.addAll(((MultiLocationOutputSupplier) targets).getLocations());
			}
			else {
				filesWritten.add(targets.getLocation());
			}
		} catch (IOException | IOProviderConfigurationException e) {
			reporter.error(
					new IOMessageImpl(String.format("Error generating intermediate Shapefile"), e));
			reporter.setSuccess(false);
		}

		return filesWritten;
	}

	private void convertShapefileToTopoJson(String shapefilePath,
			CoordinateReferenceSystem sourceCrs, String targetPath, String topologyName, int kink,
			int quantizeDigit, boolean compressOutput) throws IOException {

		TopojsonApi.shpToTopojsonFile(shapefilePath, sourceCrs, targetPath, topologyName, kink,
				quantizeDigit, compressOutput);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.io.InstanceWriter#isPassthrough()
	 */
	@Override
	public boolean isPassthrough() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

}
