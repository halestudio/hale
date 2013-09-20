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
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;

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
					streamWriteInstance(instance, null, reporter);
				}
			}
			jsonGen.writeEndObject();
			jsonGen.flush();
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
	 * Writes a collection of instances as GeoJSON
	 * 
	 * @param out the output supplier
	 * @param instances the collection of instances
	 * @param reporter the reporter
	 * @throws IOException if writing the instances fails
	 */
	public void streamWriteGeoJSONCollection(LocatableOutputSupplier<? extends OutputStream> out,
			InstanceCollection instances, IOReporter reporter) throws IOException {

		// TODO What about bbox & crs?

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out.getOutput(),
				Charset.forName("UTF-8")))) {

			JsonFactory jsonFactory = new JsonFactory();
			geometryJson = new GeometryJSON();

			jsonGen = jsonFactory.createJsonGenerator(writer);
			jsonGen.useDefaultPrettyPrinter();
			jsonGen.writeStartObject();

			jsonGen.writeStringField("type", "FeatureCollection");
			jsonGen.writeArrayFieldStart("features");
			// iterate through Instances
			try (ResourceIterator<Instance> itInstance = instances.iterator()) {
				while (itInstance.hasNext()) {
					Instance instance = itInstance.next();
					streamWriteGeoJSONInstance(instance, reporter);
				}
			}
			jsonGen.writeEndArray();
			jsonGen.writeEndObject();
			jsonGen.flush();
		}
	}

	/**
	 * Writes a single instance as GeoJSON.
	 * 
	 * @param instance the instance to write
	 * @param reporter the reporter
	 * @throws IOException if writing the instance fails
	 */
	private void streamWriteGeoJSONInstance(Instance instance, IOReporter reporter)
			throws IOException {
		jsonGen.writeStartObject();
		jsonGen.writeStringField("type", "Feature");

		// TODO let user configure default geometry
		GeometryFinder geomFinder = new GeometryFinder(null);
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);
		traverser.traverse(instance, geomFinder);
		Collection<GeometryProperty<?>> geometries = geomFinder.getGeometries();
		if (!geometries.isEmpty()) {
			// XXX It would propably be better to put CRS to each geometry.
			// This is currently not possible because geotools doesn't support
			// this.
			GeometryProperty<?> geomProp = geometries.iterator().next();
			if (geomProp.getCRSDefinition() != null) {
				jsonGen.writeFieldName("crs");
				jsonGen.writeRawValue(new FeatureJSON().toString(geomProp.getCRSDefinition()
						.getCRS()));
			}
		}

		jsonGen.writeFieldName("geometry");
		if (geometries.isEmpty())
			jsonGen.writeNull();
		else if (geometries.size() == 1)
			streamWriteGeometryValue(geometries.iterator().next().getGeometry());
		else {
			jsonGen.writeStartObject();
			jsonGen.writeStringField("type", "GeometryCollection");
			jsonGen.writeArrayFieldStart("geometries");
			for (GeometryProperty<?> geom : geometries)
				streamWriteGeometryValue(geom.getGeometry());
			jsonGen.writeEndArray();
			jsonGen.writeEndObject();
		}

		jsonGen.writeFieldName("properties");
		jsonGen.writeStartObject();
		jsonGen.writeStringField("_type", instance.getDefinition().getName().getLocalPart());
		streamWriteProperties(instance, reporter);
		jsonGen.writeEndObject();
		jsonGen.writeEndObject();
	}

	/**
	 * Writes a single instance into JSON
	 * 
	 * @param instance the instance to write
	 * @param propertyName the property name this instance is in (may be null
	 *            for top level instance)
	 * @param reporter the reporter
	 * @throws JsonGenerationException if there was a problem generating the
	 *             JSON
	 * @throws IOException if writing the file failed
	 */
	private void streamWriteInstance(Instance instance, String propertyName, IOReporter reporter)
			throws JsonGenerationException, IOException {
		// write the Instance and name
		if (propertyName == null)
			jsonGen.writeObjectFieldStart(instance.getDefinition().getName().getLocalPart());
		else
			jsonGen.writeObjectFieldStart(propertyName);

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

			streamWriteValue("_value", value);
		}

		streamWriteProperties(instance, reporter);

		jsonGen.writeEndObject();
	}

	/**
	 * Writes a group into JSON
	 * 
	 * @param group the group to write
	 * @param propertyName the property name this group is in
	 * @param reporter the reporter
	 * @throws JsonGenerationException if there was a problem generating the
	 *             JSON
	 * @throws IOException if writing the file failed
	 */
	private void streamWriteGroup(Group group, String propertyName, IOReporter reporter)
			throws JsonGenerationException, IOException {
		// For choices search for the (only!) child and skip the choice.
		if (group.getDefinition() instanceof GroupPropertyDefinition) {
			if (((GroupPropertyDefinition) group.getDefinition()).getConstraint(ChoiceFlag.class)
					.isEnabled()) {
				Iterator<QName> childPropertyNames = group.getPropertyNames().iterator();

				if (!childPropertyNames.hasNext()) {
					reporter.warn(new IOMessageImpl("Found an empty choice.", null));
					jsonGen.writeNull();
					return;
				}

				QName childPropertyName = childPropertyNames.next();
				Object[] values = group.getProperty(childPropertyName);
				Object value = values[0];
				if (childPropertyNames.hasNext() || values.length > 1)
					reporter.warn(new IOMessageImpl(
							"Found a choice with multiple children. Using first.", null));

				// delegate to only value
				streamWriteProperty(childPropertyName.getLocalPart(), value, reporter);
				return;
			}
		}
		// write the Instance and name
		jsonGen.writeObjectFieldStart(propertyName);
		streamWriteProperties(group, reporter);
		jsonGen.writeEndObject();
	}

	/**
	 * Writes the properties of a group into JSON
	 * 
	 * @param group the group to write
	 * @param reporter the reporter
	 * @throws IOException if writing the file failed
	 */
	private void streamWriteProperties(Group group, IOReporter reporter) throws IOException {
		// iterate over all properties
		Iterator<QName> nameIt = group.getPropertyNames().iterator();
		while (nameIt.hasNext()) {
			QName name = nameIt.next();
			Object[] values = group.getProperty(name);

			// ... and over all values of each property
			if (values != null)
				for (Object obj : values)
					streamWriteProperty(name.getLocalPart(), obj, reporter);
		}
	}

	/**
	 * Writes a single property of a group into JSON
	 * 
	 * @param name the property name
	 * @param value the value
	 * @param reporter the reporter
	 * @throws IOException if writing the file failed
	 */
	private void streamWriteProperty(String name, Object value, IOReporter reporter)
			throws IOException {
		if (value instanceof Instance)
			streamWriteInstance((Instance) value, name, reporter);
		else if (value instanceof Group)
			streamWriteGroup((Group) value, name, reporter);
		else
			streamWriteValue(name, value);
	}

	/**
	 * Writes a flat value (no instance or group).
	 * 
	 * @param name the property name
	 * @param value the value
	 * @throws IOException if writing the field fails
	 */
	private void streamWriteValue(String name, Object value) throws IOException {
		if (value instanceof Geometry)
			streamWriteGeometry(name, (Geometry) value);
		else if (value instanceof GeometryProperty<?>)
			streamWriteGeometry(name, ((GeometryProperty<?>) value).getGeometry());
		else if (value instanceof Number)
			streamWriteNumeric(name, (Number) value);
		else {
			// XXX use conversion service or something?
			jsonGen.writeStringField(name, value.toString());
		}
	}

	/**
	 * Writes a property numeric into json
	 * 
	 * @param name the property name
	 * @param num the numeric
	 * @throws JsonGenerationException if there was a problem generating the
	 *             JSON
	 * @throws IOException if writing the file failed
	 */
	private void streamWriteNumeric(String name, Number num) throws JsonGenerationException,
			IOException {
		if (num instanceof Integer)
			jsonGen.writeNumberField(name, (Integer) num);
		else if (num instanceof Float)
			jsonGen.writeNumberField(name, (Float) num);
		else if (num instanceof Double)
			jsonGen.writeNumberField(name, (Double) num);
		else if (num instanceof Long)
			jsonGen.writeNumberField(name, (Long) num);
		else if (num instanceof BigDecimal)
			jsonGen.writeNumberField(name, (BigDecimal) num);
		else if (num instanceof BigInteger)
			jsonGen.writeNumberField(name, new BigDecimal((BigInteger) num));
		else {
			// XXX this case is not particularly good ...
			jsonGen.writeFieldName(name);
			jsonGen.writeNumber(String.valueOf(num));
		}
	}

	private void streamWriteGeometry(String name, Geometry geom) throws IOException {
		jsonGen.writeFieldName(name);
		streamWriteGeometryValue(geom);
	}

	private void streamWriteGeometryValue(Geometry geom) throws IOException {
		jsonGen.writeRawValue(geometryJson.toString(geom));
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
