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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
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
import json.tools.Compress;
import json.tools.Toolbox;
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
	private static final ALogger log = ALoggerFactory.getLogger(TopoJsonInstanceWriter.class);

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
			int quantizeDigit, boolean compressOutput) throws Exception {

		// DBFExtractor is using DBFReader, which converts all the dates from
		// hh:mm:ss into 00:00:00
		String aJson = TopojsonApi.shpToTopojson(shapefilePath, sourceCrs, topologyName, kink,
				quantizeDigit, compressOutput);
		try {
			// Parse JSON string
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(aJson);

			// Replace "\u0000" values with null recursively
			replaceUnwantedCharacters(jsonNode);

			// Convert modified JSON node back to string
			String modifiedJsonString = mapper.writeValueAsString(jsonNode);

			if (compressOutput) {
				Toolbox.writeFile(targetPath, Compress.compressB64(modifiedJsonString));
			}
			else {
				Toolbox.writeFile(targetPath, modifiedJsonString);
			}

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * @param node to replace the unwanted characters
	 * @return the clean node
	 */
	public static JsonNode replaceUnwantedCharacters(JsonNode node) {
		if (node.isObject()) {
			ObjectNode objectNode = (ObjectNode) node;
			objectNode.fields().forEachRemaining(entry -> {
				JsonNode value = entry.getValue();
				if (value.isTextual()) {

					String textValue = value.textValue();
					if (textValue.contains("\u0000")) {
						if (textValue.replaceAll("\u0000", "").isEmpty()) {
							objectNode.put(entry.getKey(), (String) null);
						}
						else {
							objectNode.put(entry.getKey(), textValue.replaceAll("\u0000", ""));
						}
					}
					else if (textValue.equals("null")) {
						objectNode.put(entry.getKey(), (String) null);
					}
					else {
						// Step 1: Parse the string into java.util.Date
						try {
							Date wrongDateFormat = parseStringToDate(textValue,
									"EEE MMM dd HH:mm:ss z yyyy", true);

							if (wrongDateFormat == null) {
								wrongDateFormat = parseStringToDate(textValue, "yyyy-mm-dd", true);
							}
							if (wrongDateFormat != null) {
								// Step 2: Convert java.util.Date to
								// java.time.Instant
								Instant instant = wrongDateFormat.toInstant();

								// Step 3: Convert java.time.Instant to
								// java.time.LocalDateTime
								LocalDateTime localDateTime;
								if (wrongDateFormat.toString().contains("CET")) {
									// Convert the Date object to LocalDateTime
									localDateTime = LocalDateTime
											.ofInstant(wrongDateFormat.toInstant(), ZoneOffset.UTC);

								}
								else {
									localDateTime = instant.atZone(ZoneId.systemDefault())
											.toLocalDateTime();
								}

								// Get the year from LocalDateTime
								int year = localDateTime.getYear();
								if (year == 2) {
									objectNode.put(entry.getKey(), (String) null);
								}
								else {
									// Create a DateTimeFormatter object with
//									// the desired format
//									DateTimeFormatter formatter = DateTimeFormatter
//											.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

									objectNode.put(entry.getKey(), textValue);
								}

							}
							else {
								objectNode.put(entry.getKey(), textValue);
							}

						} catch (Exception e) {
							log.error("Failed to parse the string " + textValue, e);
							objectNode.put(entry.getKey(), (String) null);
						}

					}
				}
				else {
					replaceUnwantedCharacters(value);
				}
			});
		}
		else if (node.isArray()) {
			for (JsonNode arrayElement : node) {
				replaceUnwantedCharacters(arrayElement);
			}
		}

		return node;
	}

	private static Date parseStringToDate(String dateString, String pattern, boolean setGMT) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		if (setGMT) {
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
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
