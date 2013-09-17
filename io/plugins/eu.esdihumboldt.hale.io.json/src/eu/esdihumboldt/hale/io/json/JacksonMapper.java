/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.json;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.geotools.geojson.geom.GeometryJSON;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Transform Instances into JSON/GeoJSON using the Jackson-API and the Geotools
 * GeoJSON-API
 * 
 * @author Sebastian Reinhardt
 */
public class JacksonMapper {

	private JsonGenerator jsonGen;
	private GeometryJSON geometryJson;

	/**
	 * Writes a collection of instances into JSON
	 * 
	 * @param out the output supplier
	 * @param instances the collection of instances
	 * @param reporter the reporter
	 * @throws IOException if writing
	 */
	public void streamWriteCollection(LocatableOutputSupplier<? extends OutputStream> out,
			InstanceCollection instances, IOReporter reporter) throws IOException {

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out.getOutput(),
				Charset.forName("UTF-8")))) {

			// initialize Jackson Json Streaming Api
			JsonFactory jsonFactory = new JsonFactory();
			// initialize GeoJSON Api
			geometryJson = new GeometryJSON();

			jsonGen = jsonFactory.createJsonGenerator(writer);
			jsonGen.useDefaultPrettyPrinter();
			jsonGen.writeStartObject();

			// iterate through Instances
			try (ResourceIterator<Instance> itInstance = instances.iterator()) {
				while (itInstance.hasNext()) {
					Instance instance = itInstance.next();
					streamWriteInstance(instance, null, writer, reporter);
				}
			}
			jsonGen.writeEndObject();
			jsonGen.flush();
		} finally {
			if (jsonGen != null) {
				jsonGen.close();
			}
		}

		// FIXME - rather move to a validator?!
		// XXX cannot cope with GZiped file
//		URI targetLoc = out.getLocation();
//		if (targetLoc != null) {
//			File file = new File(targetLoc);
//			try (InputStream in = Files.newInputStream(file.toPath())) {
//				isValidJSON(in, reporter);
//			}
//		}
	}

	/**
	 * writes a single instance into JSON
	 * 
	 * @param instance the instance to write
	 * @param instanceName the QName of the instance
	 * @param writer the writer used
	 * @param reporter the reporter
	 * @throws JsonGenerationException if there was a problem generating the
	 *             JSON
	 * @throws IOException if writing the file failed
	 */
	private void streamWriteInstance(Instance instance, QName instanceName, BufferedWriter writer,
			IOReporter reporter) throws JsonGenerationException, IOException {
		// write the Instance and name
		if (instanceName == null) {
			jsonGen.writeObjectFieldStart(instance.getDefinition().getName().getLocalPart());
		}
		else
			jsonGen.writeObjectFieldStart(instanceName.getLocalPart());

		// check if the instance contains a value and write it down
		Object value = instance.getValue();
		if (value != null) {
			// detect geometry collections (as occurring in XML schemas)
			if (value instanceof Collection) {
				Collection<?> col = (Collection<?>) value;
				if (!col.isEmpty()) {
					Object element = col.iterator().next();
					if (element instanceof GeometryProperty<?> || element instanceof Geometry) {
						// extract geometry
						value = element;

						if (col.size() > 1) {
							// there are more geometry values
							// XXX can we handle multiple here?
							// XXX for now warn
							reporter.warn(new IOMessageImpl(
									"Ignoring multiple geometries in instance value", null));
						}
					}
				}
			}

			if (value instanceof Geometry || value instanceof GeometryProperty<?>) {
				streamWriteGeometry("_value", value);
				jsonGen.writeEndObject();
				return;
			}
			if (instance.getValue() instanceof Number) {
				streamWriteNumeric("_value", value);
			}
			else
				jsonGen.writeStringField("_value", value.toString());
		}

		Iterator<QName> nameIt = instance.getPropertyNames().iterator();
		while (nameIt.hasNext()) {

			QName name = nameIt.next();
			Object[] values = instance.getProperty(name);
			// iterate through the children of the definition

			if (values != null) {
				for (Object obj : values) {
					if (obj instanceof Instance) {
						streamWriteInstance((Instance) obj, name, writer, reporter);
					}
					else if (obj instanceof Group) {
						streamWriteGroup((Group) obj, writer, reporter);
					}
					else if (obj instanceof Geometry || obj instanceof GeometryProperty<?>) {
						streamWriteGeometry(name.getLocalPart(), obj);
					}
					else if (obj instanceof Number) {
						streamWriteNumeric(name.getLocalPart(), obj);
					}
					else
						jsonGen.writeStringField(name.getLocalPart(), obj.toString());
				}
			}
		}
		jsonGen.writeEndObject();
	}

	/**
	 * Writes a group into JSON
	 * 
	 * @param group the group to write
	 * @param writer the writer used
	 * @param reporter the reporter
	 * @throws JsonGenerationException if there was a problem generating the
	 *             JSON
	 * @throws IOException if writing the file failed
	 */
	private void streamWriteGroup(Group group, BufferedWriter writer, IOReporter reporter)
			throws JsonGenerationException, IOException {
		// write the Instance and name
		jsonGen.writeObjectFieldStart("Group");

		Iterator<QName> nameIt = group.getPropertyNames().iterator();
		while (nameIt.hasNext()) {

			QName name = nameIt.next();
			Object[] values = group.getProperty(name);
			// iterate through the children of the definition

			if (values != null) {
				for (Object obj : values) {
					if (obj instanceof Instance) {
						streamWriteInstance((Instance) obj, name, writer, reporter);
					}
					else if (obj instanceof Group) {
						streamWriteGroup((Group) obj, writer, reporter);
					}
					else if (obj instanceof Geometry || obj instanceof GeometryProperty<?>) {
						streamWriteGeometry(name.getLocalPart(), obj);
					}

					else if (obj instanceof Number) {
						streamWriteNumeric(name.getLocalPart(), obj);
					}
					else {
						jsonGen.writeStringField(name.getLocalPart(), obj.toString());
					}

				}
			}
		}
		jsonGen.writeEndObject();
	}

	/**
	 * writes a property numeric into json
	 * 
	 * @param name the property name
	 * @param obj the numeric
	 * @throws JsonGenerationException if there was a problem generating the
	 *             JSON
	 * @throws IOException if writing the file failed
	 */
	private void streamWriteNumeric(String name, Object obj) throws JsonGenerationException,
			IOException {
		if (obj instanceof Integer) {
			jsonGen.writeNumberField(name, (Integer) obj);
		}
		else if (obj instanceof Float) {
			jsonGen.writeNumberField(name, (Float) obj);
		}
		else if (obj instanceof Double) {
			jsonGen.writeNumberField(name, (Double) obj);
		}
		else if (obj instanceof Long) {
			jsonGen.writeNumberField(name, (Long) obj);
		}
		else if (obj instanceof BigDecimal) {
			jsonGen.writeNumberField(name, (BigDecimal) obj);
		}
		else
			jsonGen.writeNumberField(name, (Double) obj);
	}

	private void streamWriteGeometry(String name, Object obj) throws IOException {

		jsonGen.writeFieldName(name);

		if (obj instanceof Geometry) {
			jsonGen.writeRawValue(geometryJson.toString((Geometry) obj));
		}
		else if (obj instanceof GeometryProperty<?>) {
			jsonGen.writeRawValue(geometryJson.toString(((GeometryProperty<?>) obj).getGeometry()));
		}
	}

	/**
	 * Validates a JSON stream
	 * 
	 * @param in the JSON stream
	 * @param reporter the reporter
	 * @return true if valid, else false
	 */
	public boolean isValidJSON(final InputStream in, IOReporter reporter) {
		boolean valid = false;
		try {
			final JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(in);
			while (parser.nextToken() != null) {
				//
			}
			valid = true;
		} catch (Exception e) {
			reporter.error(new IOMessageImpl("Produced invalid JSON output", e));
		}

		return valid;
	}

}
