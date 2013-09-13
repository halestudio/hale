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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.geotools.geojson.geom.GeometryJSON;

import com.vividsolutions.jts.geom.Geometry;

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

	File file;
	JsonGenerator g;
	GeometryJSON gj;

	/**
	 * @param target the target file to write to
	 */
	public JacksonMapper(URI target) {
		file = new File(target);

	}

	/**
	 * writes a collection of instances into JSON
	 * 
	 * @param instances the collection of instances
	 * @throws IOException
	 */
	public void streamWriteCollection(InstanceCollection instances) throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(),
				Charset.forName("UTF-8"))) {

			// initialize Jackson Json Streaming Api
			JsonFactory f = new JsonFactory();
			// initialize GeoJSON Api
			gj = new GeometryJSON();

			g = f.createJsonGenerator(writer);
			g.useDefaultPrettyPrinter();
			g.writeStartObject();

			// iterate through Instances
			ResourceIterator<Instance> itInstance = instances.iterator();
			while (itInstance.hasNext()) {
				Instance instance = itInstance.next();
				streamWriteInstance(instance, null, writer);
			}
			g.writeEndObject();
			g.flush();
			g.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		isValidJSON(readFile(file));
	}

	/**
	 * writes a single instance into JSON
	 * 
	 * @param instance the instance to write
	 * @param instanceName the QName of the instance
	 * @param writer the writer used
	 * @throws JsonGenerationException
	 * @throws IOException
	 */
	private void streamWriteInstance(Instance instance, QName instanceName, BufferedWriter writer)
			throws JsonGenerationException, IOException {
		// write the Instance and name
		if (instanceName == null) {
			g.writeObjectFieldStart(instance.getDefinition().getName().getLocalPart());
		}
		else
			g.writeObjectFieldStart(instanceName.getLocalPart());

		// check if the instance contains a value and write it down
		if (instance.getValue() != null) {
			if (instance.getValue() instanceof Geometry
					|| instance.getValue() instanceof GeometryProperty<?>) {
				streamWriteGeometry("_value", instance.getValue());
				g.writeEndObject();
				return;
			}
			if (instance.getValue() instanceof Number) {
				streamWriteNumeric("_value", instance.getValue());
			}
			else
				g.writeStringField("_value", instance.getValue().toString());
		}

		Iterator<QName> nameIt = instance.getPropertyNames().iterator();
		while (nameIt.hasNext()) {

			QName name = nameIt.next();
			Object[] values = instance.getProperty(name);
			// iterate through the children of the definition

			if (values != null) {
				for (Object obj : values) {
					if (obj instanceof Instance) {
						streamWriteInstance((Instance) obj, name, writer);
					}
					else if (obj instanceof Group) {
						streamWriteGroup((Group) obj, writer);
					}
					else if (obj instanceof Geometry || obj instanceof GeometryProperty<?>) {
						streamWriteGeometry(name.getLocalPart(), obj);
					}
					else if (obj instanceof Number) {
						streamWriteNumeric(name.getLocalPart(), obj);
					}
					else
						g.writeStringField(name.getLocalPart(), obj.toString());
				}
			}
		}
		g.writeEndObject();
	}

	/**
	 * writes a group into JSON
	 * 
	 * @param group the group to write
	 * @param writer the writer used
	 * @throws JsonGenerationException
	 * @throws IOException
	 */
	private void streamWriteGroup(Group group, BufferedWriter writer)
			throws JsonGenerationException, IOException {
		// write the Instance and name
		g.writeObjectFieldStart("Group");

		Iterator<QName> nameIt = group.getPropertyNames().iterator();
		while (nameIt.hasNext()) {

			QName name = nameIt.next();
			Object[] values = group.getProperty(name);
			// iterate through the children of the definition

			if (values != null) {
				for (Object obj : values) {
					if (obj instanceof Instance) {
						streamWriteInstance((Instance) obj, name, writer);
					}
					else if (obj instanceof Group) {
						streamWriteGroup((Group) obj, writer);
					}
					else if (obj instanceof Geometry || obj instanceof GeometryProperty<?>) {
						streamWriteGeometry(name.getLocalPart(), obj);
					}

					else if (obj instanceof Number) {
						streamWriteNumeric(name.getLocalPart(), obj);
					}
					else {
						g.writeStringField(name.getLocalPart(), obj.toString());
					}

				}
			}
		}
		g.writeEndObject();
	}

	/**
	 * writes a property numeric into json
	 * 
	 * @param name the property name
	 * @param obj the numeric
	 * @throws IOException
	 * @throws JsonGenerationException
	 */
	private void streamWriteNumeric(String name, Object obj) throws JsonGenerationException,
			IOException {
		if (obj instanceof Integer) {
			g.writeNumberField(name, (Integer) obj);
		}
		else if (obj instanceof Float) {
			g.writeNumberField(name, (Float) obj);
		}
		else if (obj instanceof Double) {
			g.writeNumberField(name, (Double) obj);
		}
		else if (obj instanceof Long) {
			g.writeNumberField(name, (Long) obj);
		}
		else if (obj instanceof BigDecimal) {
			g.writeNumberField(name, (BigDecimal) obj);
		}
		else
			g.writeNumberField(name, (Double) obj);
	}

	private void streamWriteGeometry(String name, Object obj) throws IOException {

		g.writeFieldName(name);

		if (obj instanceof Geometry) {
			g.writeRawValue(gj.toString((Geometry) obj));
		}
		else if (obj instanceof GeometryProperty<?>) {
			g.writeRawValue(gj.toString(((GeometryProperty<?>) obj).getGeometry()));
		}
	}

	/**
	 * validates a json string
	 * 
	 * @param json the json string
	 * @return true if valid, else false
	 */
	public boolean isValidJSON(final String json) {
		boolean valid = false;
		try {
			final JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json);
			while (parser.nextToken() != null) {
				//
			}
			valid = true;
		} catch (JsonParseException jpe) {
			jpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return valid;
	}

	/**
	 * file reader method used for json validation
	 * 
	 * @param file the file to read
	 * @return string representation of the file
	 * @throws IOException
	 */
	private String readFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		reader.close();
		return stringBuilder.toString();
	}

}
