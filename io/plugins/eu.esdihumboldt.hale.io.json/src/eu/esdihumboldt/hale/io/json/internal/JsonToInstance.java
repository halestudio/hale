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

package eu.esdihumboldt.hale.io.json.internal;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Class to read instances from JSON.
 * 
 * @author Simon Templer
 */
public class JsonToInstance extends AbstractJsonInstanceProcessor<Instance>
		implements InstanceJsonConstants {

	private final NamespaceManager namespaces;

	private final JsonInstanceBuilder builder;

	// GeoJson expects WGS 84 with lon/lat (see
	// https://tools.ietf.org/html/rfc7946)
	private final CRSDefinition sourceCrs = new CodeDefinition("EPSG:4326", true);

	private final TypeDefinition defaultType;
	private final TypeIndex schema;
	private final SimpleLog log;
	private final boolean forceDefault;

	/**
	 * 
	 * @param expectGeoJson if the input is expected to be GeoJson
	 * @param defaultType the feature type to use for all features or
	 *            <code>null</code>
	 * @param forceDefault if the default type should be always used (disables
	 *            other mechanisms to determine the type)
	 * @param schema if a schema is specified, the feature type may be
	 *            determined based on the schema
	 */
	public JsonToInstance(boolean expectGeoJson, TypeDefinition defaultType, boolean forceDefault,
			TypeIndex schema, SimpleLog log) {
		this(JsonReadMode.auto, expectGeoJson, defaultType, forceDefault, schema, log);
	}

	/**
	 * 
	 * @param mode the mode for reading the Json, supporting different kinds of
	 *            document structures
	 * @param expectGeoJson if the input is expected to be GeoJson
	 * @param defaultType the default type to use for all features or
	 *            <code>null</code>
	 * @param forceDefault if the default type should be always used (disables
	 *            other mechanisms to determine the type)
	 * @param schema if a schema is specified, the feature type may be
	 *            determined based on the schema
	 */
	public JsonToInstance(JsonReadMode mode, boolean expectGeoJson, TypeDefinition defaultType,
			boolean forceDefault, TypeIndex schema, SimpleLog log) {
		this(mode, expectGeoJson, defaultType, forceDefault, schema, log,
				new IgnoreNamespaces() /* new JsonNamespaces() */);
	}

	/**
	 *
	 * @param mode the mode for reading the Json, supporting different kinds of
	 *            document structures
	 * @param expectGeoJson if the input is expected to be GeoJson
	 * @param defaultType the feature type to use for all features or
	 *            <code>null</code>
	 * @param forceDefault if the default type should be always used (disables
	 *            other mechanisms to determine the type)
	 * @param schema if a schema is specified, the feature type may be
	 *            determined based on the schema
	 * @param namespaces the namespace manager
	 */
	public JsonToInstance(JsonReadMode mode, boolean expectGeoJson, TypeDefinition defaultType,
			boolean forceDefault, TypeIndex schema, SimpleLog log, NamespaceManager namespaces) {
		super(mode, expectGeoJson);

		if (defaultType == null && forceDefault) {
			throw new IllegalStateException(
					"Default type needs to be specified when forcing to use default type");
		}

		this.namespaces = namespaces;
		this.defaultType = (defaultType != null) ? defaultType : determineDefaultType(schema, log);
		this.log = log;
		this.schema = schema;
		this.forceDefault = forceDefault;

		this.builder = new JsonInstanceBuilder(log, namespaces, sourceCrs);

		if (expectGeoJson) {
			// XXX should GeoJson namespace be the namespace w/o prefix?
//      namespaces.setPrefix(NAMESPACE_GEOJSON, "");
		}
	}

	/**
	 * Determine default type if none is specified.
	 * 
	 * Note: This functionality should be removed once any kind of
	 * auto-detection based on the actual content is implemented.
	 */
	private static TypeDefinition determineDefaultType(TypeIndex schema, SimpleLog log) {
		if (schema == null) {
			return null;
		}

		Collection<? extends TypeDefinition> candidates = schema.getMappingRelevantTypes();
		if (!candidates.isEmpty()) {
			if (candidates.size() > 1) {
				// sort to have a reproducable behavior what the chosen type is
				return candidates.stream()
						.sorted(Comparator
								.<TypeDefinition, String> comparing(t -> t.getName().toString()))
						.findFirst().get();
			}
			else {
				return candidates.iterator().next();
			}

		}

		return null;
	}

	/**
	 * @return namespace
	 */
	public NamespaceManager getNamespaces() {
		return namespaces;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.json.internal.AbstractJsonInstanceProcessor#processInstance(java.util.Map,
	 *      com.fasterxml.jackson.databind.node.ObjectNode, java.util.Map)
	 */
	@Override
	protected Instance processInstance(Map<String, JsonNode> fields, ObjectNode geom,
			Map<String, JsonNode> properties) {
		// determine schema type

		/*
		 * Currently only configuration of a fixed type that should be assumed
		 * for all features is supported. This could be extended later with some
		 * form of type detection (e.g. using the information in @type in case
		 * the data was written with InstanceToJson)
		 */
		TypeDefinition type = defaultType;

		if (!forceDefault && schema != null) {
			JsonNode typeField = fields.get("@type");
			if (typeField != null && typeField.isTextual()) {
				QName typeName = extractName(typeField.textValue());

				if (typeName != null) {
					// look for exact match
					TypeDefinition candidate = schema.getType(typeName);
					if (candidate == null) {
						// look for local part match
						List<TypeDefinition> candidates = schema.getMappingRelevantTypes().stream()
								.filter(t -> typeName.getLocalPart()
										.equals(t.getName().getLocalPart()))
								.collect(Collectors.toList());
						if (candidates.size() == 1) {
							candidate = candidates.get(0);
						}
						else if (candidates.size() > 1) {
							// sort by namespace URI to consistently use same
							// type
							candidate = candidates.stream()
									.sorted(Comparator.<TypeDefinition, String> comparing(
											t -> t.getName().getNamespaceURI()))
									.findFirst().get();

							log.warn(
									"Multiple candidates matching the local name of type name {0}, choosing {1}",
									typeName, candidate.getName());
						}
					}

					if (candidate != null) {
						// override default type
						type = candidate;
					}
				}
			}
		}

		return builder.buildInstance(type, geom, properties);
	}

	/**
	 * Extract a qualified name from a text representation with an optional
	 * namespace prefix.
	 * 
	 * @param text the text representation of the name
	 * @return the qualified name
	 */
	private QName extractName(String text) {
		return JsonToInstance.extractName(text, namespaces);
	}

	/**
	 * Extract a qualified name from a text representation with an optional
	 * namespace prefix.
	 * 
	 * @param text the text representation of the name
	 * @param namespaces the namespace manager
	 * @return the qualified name
	 */
	public static QName extractName(String text, NamespaceManager namespaces) {
		if (text == null) {
			return null;

		}
		int firstSep = text.indexOf(':');
		if (firstSep >= 0 && firstSep + 1 < text.length()) {
			String prefix = text.substring(0, firstSep);
			String name = text.substring(firstSep + 1);
			Optional<String> namespace = namespaces.getNamespace(prefix);
			if (namespace.isPresent()) {
				return new QName(namespace.get(), name, prefix);
			}
			else {
				return new QName(name);
			}
		}
		else {
			return new QName(text);
		}
	}

}
