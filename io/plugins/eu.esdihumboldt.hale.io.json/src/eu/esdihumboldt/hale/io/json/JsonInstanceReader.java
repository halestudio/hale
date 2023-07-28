/*
 * Copyright (c) 2023 wetransform GmbH
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
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ext.impl.PerTypeInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.io.json.internal.JsonInstanceCollection;
import eu.esdihumboldt.hale.io.json.internal.JsonInstanceConstants;
import eu.esdihumboldt.hale.io.json.reader.JsonToInstance;
import eu.esdihumboldt.util.Pair;

/**
 * Reader for Json/GeoJson data.
 * 
 * @author Simon Templer
 */
public class JsonInstanceReader extends AbstractInstanceReader implements JsonInstanceConstants {

	private static final ALogger log = ALoggerFactory.getLogger(JsonInstanceReader.class);

	private InstanceCollection instances;

	/**
	 * Default constructor.
	 */
	public JsonInstanceReader() {
		super();

		addSupportedParameter(PARAM_TYPENAME);
	}

	@Override
	public InstanceCollection getInstances() {
		return instances;
	}

	@Override
	public boolean isCancelable() {
		// actual
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Creating " + getDefaultTypeName() + " parser", ProgressIndicator.UNKNOWN);

		try {
			boolean expectGeoJson = false;
			progress.setCurrentTask("Extracting json instances");

			String featureCollection = "FeatureCollection";
			String typeFeatureCollection = "type";

			String targetField = "@type";
			ObjectMapper objectMapper = new ObjectMapper();

			try {
				JsonNode rootNode = objectMapper.readTree(new File(getSource().getLocation()));

				String name = null;
				if (rootNode.findValue(typeFeatureCollection) != null) {
					name = rootNode.findValue(typeFeatureCollection).asText();
				}
				if (name != null && name.equals(featureCollection)) {
					expectGeoJson = true;

					Map<String, Integer> fieldCountMap = new HashMap<>();
					countFieldOccurrences(rootNode, fieldCountMap, targetField);

					if (!fieldCountMap.isEmpty()) {
						Map<TypeDefinition, InstanceCollection> collections = new HashMap<>();
						for (Map.Entry<String, Integer> entry : fieldCountMap.entrySet()) {

							String typename = entry.getKey();
							Collection<? extends TypeDefinition> mappingTypes = getSourceSchema()
									.getMappingRelevantTypes();

							for (Iterator<?> iterator = mappingTypes.iterator(); iterator
									.hasNext();) {
								TypeDefinition defaultType = (TypeDefinition) iterator.next();
								if (defaultType.getName().getLocalPart().equals(typename)) {
									JsonToInstance translator = new JsonToInstance(expectGeoJson,
											defaultType, SimpleLog.fromLogger(log));
									collections.put(defaultType, new JsonInstanceCollection(
											translator, getSource(), getCharset()));
								}
							}
						}
						instances = new PerTypeInstanceCollection(collections);
					}
				}
				else {
					TypeDefinition type = getSourceSchema().getMappingRelevantTypes().stream()
							.findFirst().orElse(null);

					JsonToInstance translator = new JsonToInstance(expectGeoJson, type,
							SimpleLog.fromLogger(log));
					instances = new JsonInstanceCollection(translator, getSource(), getCharset());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error("Error preparing reading {0}", getDefaultTypeName(), e);
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}
		return reporter;
	}

	/**
	 * count how many times a field filterField can be found in a json
	 * 
	 * @param node to look into
	 * @param fieldCountMap map containing the value of the searched filed and
	 *            the number of occurrences
	 * @param filterField field name
	 */
	private static void countFieldOccurrences(JsonNode node, Map<String, Integer> fieldCountMap,
			String filterField) {
		if (node.isObject()) {
			node.fieldNames().forEachRemaining(fieldName -> {

				if (fieldName.equals(filterField)) {

					fieldCountMap.put(node.get(fieldName).asText(),
							fieldCountMap.getOrDefault(fieldName, 0) + 1);
					countFieldOccurrences(node.get(fieldName), fieldCountMap, filterField);

				}
				else {
					countFieldOccurrences(node.get(fieldName), fieldCountMap, filterField);
				}
			});
		}
		else if (node.isArray()) {
			for (JsonNode arrayElement : node) {
				countFieldOccurrences(arrayElement, fieldCountMap, filterField);
			}
		}
	}

	@Override
	protected String getDefaultTypeName() {
		return "JSON";
	}

	/**
	 * Get the type definition from a JSON.
	 * 
	 * @param source the JSON/GeoJSON source
	 * @return the type definition or <code>null</code> in case reading the type
	 *         was not possible
	 */
	public TypeDefinition readJSONType(LocatableInputSupplier<? extends InputStream> source) {
		setSource(source);
		try {
			super.execute(null);
			Collection<? extends TypeDefinition> types = getSourceSchema()
					.getMappingRelevantTypes();
			if (!types.isEmpty()) {
				return types.iterator().next();
			}
			else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Determine the type out of the the mapping relevant types in the given
	 * type index, that matches the given data type best.
	 * 
	 * @param types the type index
	 * @param dataType the JSON data type
	 * @param preferredName the name of the preferred type
	 * @return the most compatible type found together with is compatibility
	 *         rating or <code>null</code> if there is no type that at least has
	 *         one matching property
	 * 
	 * @see #checkCompatibility(TypeDefinition, TypeDefinition)
	 */
	public static Pair<TypeDefinition, Integer> getMostCompatibleJsonType(TypeIndex types,
			TypeDefinition dataType, String preferredName) {
		int maxCompatibility = -1;
		TypeDefinition maxType = null;

		// check preferred name first
		TypeDefinition preferredType = types
				.getType(new QName(JsonInstanceConstants.NAMESPACE_INSTANCE_JSON, preferredName));
		if (preferredType != null) {
			int comp = checkCompatibility(preferredType, dataType);
			if (comp >= 100) {
				// return an exact match directly
				return new Pair<TypeDefinition, Integer>(preferredType, 100);
			}
			else {
				maxType = preferredType;
				maxCompatibility = comp;
			}
		}

		for (TypeDefinition schemaType : types.getMappingRelevantTypes()) {
			if (JsonInstanceConstants.NAMESPACE_INSTANCE_JSON
					.equals(schemaType.getName().getNamespaceURI())) {
				// is a json type

				int comp = checkCompatibility(schemaType, dataType);
				if (comp >= 100) {
					// return an exact match directly
					return new Pair<TypeDefinition, Integer>(schemaType, 100);
				}
				else if (comp > maxCompatibility) {
					maxType = schemaType;
					maxCompatibility = comp;
				}
				else if (maxCompatibility > 0 && comp == maxCompatibility) {
					// TODO debug message? possible duplicate?
				}
			}
		}

		if (maxType != null && maxCompatibility > 0) {
			// return the type with the maximum compatibility rating
			return new Pair<TypeDefinition, Integer>(maxType, maxCompatibility);
		}

		return null;
	}

	/**
	 * Determines if the compatibility rating between the two JSON type
	 * definitions.
	 * 
	 * @param schemaType the type to test for compatibility
	 * @param dataType the type representing the data to read
	 * @return the percentage of compatibility (value from <code>0</code> to
	 *         <code>100</code>), where <code>100</code> represents an exact
	 *         match and <code>0</code> no compatibility
	 */
	public static int checkCompatibility(TypeDefinition schemaType, TypeDefinition dataType) {
		// JSON types are flat, so only regard properties
		Collection<? extends PropertyDefinition> children = DefinitionUtil
				.getAllProperties(dataType);
		int count = children.size();
		int schemaCount = DefinitionUtil.getAllProperties(schemaType).size();

		// check for every property if it exists with the schema, with the same
		// name
		int num = 0;
		for (PropertyDefinition property : children) {
			ChildDefinition<?> child = schemaType.getChild(property.getName());
			if (child != null && child.asProperty() != null) {
				num++;
			}
		}

		if (num == count && count == schemaCount) {
			// exact match
			return 100;
		}
		else {
			int percentage = (int) Math.round((double) (num * 100) / (double) count);
			if (percentage > 1) {
				// reduce value by one, to ensure 100 is not returned, but only
				// return zero if there actually is no match
				percentage -= 1;
			}
			// compatibility measure with a max of 99
			return percentage;
		}
	}

}
