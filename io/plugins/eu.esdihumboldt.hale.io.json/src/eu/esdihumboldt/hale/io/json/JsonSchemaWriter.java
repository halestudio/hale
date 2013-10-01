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

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.io.impl.AbstractSchemaWriter;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;

/**
 * JSON schema writer. Conforms to http://json-schema.org/draft-04/schema#.
 * 
 * @author Thorsten Reitz
 */
public class JsonSchemaWriter extends AbstractSchemaWriter {

	private JsonGenerator jsonGen;

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Generating JSON schema", ProgressIndicator.UNKNOWN);

		Set<String> visitedTypes = new HashSet<String>();

		try (OutputStream out = getTarget().getOutput()) {

			// initialize Jackson Json Streaming Api
			JsonFactory jsonFactory = new JsonFactory();
			jsonGen = jsonFactory.createJsonGenerator(out);
			jsonGen.useDefaultPrettyPrinter();

			// get the name for this schema from the original filename
			String schemaName = getSchemas().getSchemas().iterator().next().getLocation().getPath();
			schemaName = schemaName.substring(schemaName.lastIndexOf('/'),
					schemaName.lastIndexOf('.'));

			jsonGen.writeStartObject(); // 1
			jsonGen.writeStringField("$schema", "http://json-schema.org/draft-04/schema#");
			jsonGen.writeStringField("title", schemaName);
			jsonGen.writeStringField("type", "object");
			jsonGen.writeFieldName("properties");
			jsonGen.writeStartObject(); // 2

			// create general schema type declarations
			for (Schema schema : getSchemas().getSchemas()) {

				// create an ID for the schema
				String id = schema.getNamespace();
				if (id == null || id.equals("")) {
					id = schema.getLocation().toString();
					if (id == null || id.equals("")) {
						id = "//generated-ns/" + UUID.randomUUID().toString();
					}
				}

				// iterate over the schema types classified as mapping relevant
				for (TypeDefinition type : getSchemas().getMappingRelevantTypes()) {
					// Create type declaration
					encodeTypeDefinition(id, type);

				}

			}

			jsonGen.writeEndObject(); // 2
			jsonGen.writeEndObject(); // 1

			jsonGen.flush();

			reporter.setSuccess(true);

		} finally {
//			jsonGen.close();
			progress.end();
		}
		return reporter;
	}

	private void encodeTypeDefinition(String schemaID, TypeDefinition type)
			throws JsonGenerationException, IOException {

		jsonGen.writeFieldName(type.getDisplayName());
		jsonGen.writeStartObject(); // 1

		jsonGen.writeStringField("title", type.getName().toString());
		jsonGen.writeStringField("id", schemaID + "/" + type.getName().toString());
		jsonGen.writeStringField("type", "object");
		jsonGen.writeFieldName("properties");
		jsonGen.writeStartObject(); // 2

		// determine cardinality of the property

		// determine if a property is simple or an object itself

		// add simple properties to type declaration
		Set<String> requiredFields = new HashSet<String>();
		for (ChildDefinition child : type.getChildren()) {

			jsonGen.writeFieldName(child.getDisplayName());
			jsonGen.writeStartObject(); // 3

			if (child.getDescription() != null) {
				jsonGen.writeStringField("description", child.getDescription());
			}

			if (child.asProperty() != null) {
				TypeDefinition td = child.asProperty().getPropertyType();
				Binding binding = td.getConstraint(Binding.class);
				if (binding != null) {
					jsonGen.writeStringField("type", getType(binding));
				}
			}

			// register cardinalities
			Cardinality card = (Cardinality) child.getConstraint(Cardinality.class);
			if (card != null) {
				if (card.getMinOccurs() != Cardinality.UNBOUNDED) {
					jsonGen.writeNumberField("minItems", card.getMinOccurs());
					if (card.getMinOccurs() > 0) {
						requiredFields.add(child.getDisplayName());
					}
				}
				if (card.getMaxOccurs() != Cardinality.UNBOUNDED) {
					jsonGen.writeNumberField("maxItems", card.getMaxOccurs());
				}

			}

			// make field optional if it has minOccurs > 0 but
			// Nillable = true
			NillableFlag nf = (NillableFlag) child.getConstraint(NillableFlag.class);
			if (nf != null) {
				if (nf.isEnabled()) {
					requiredFields.add(child.getDisplayName());
				}
			}

			jsonGen.writeEndObject(); // 3

		}

		// write Array of required fields for this type
		if (requiredFields.size() > 0) {
			jsonGen.writeArrayFieldStart("required"); // A1
			for (String requiredFieldName : requiredFields) {
				jsonGen.writeString(requiredFieldName);
			}
			jsonGen.writeEndArray(); // A1
		}

		jsonGen.writeEndObject(); // 2
		jsonGen.writeEndObject(); // 1

	}

	private void encodePropertyDefinition() {

	}

	@Override
	protected String getDefaultTypeName() {
		return "JSON schema";
	}

	private String getType(Binding binding) {
		switch (binding.getBinding().getName()) {
		case "java.lang.String":
			return "string";
		case "java.lang.Integer":
			return "integer";
		case "java.lang.Long":
			return "integer";
		case "java.lang.Float":
			return "number";
		case "java.lang.Double":
			return "number";
		case "java.lang.Boolean":
			return "boolean";
		case "java.util.Date":
			return "string";
		default:
			return "object";
		}
	}

	private String getFormat(Binding binding) {
		switch (binding.getBinding().getName()) {
		case "java.util.Date":
			return "date-time";
		case "java.util.Calendar":
			return "date-time";
		default:
			return null;
		}
	}

}
