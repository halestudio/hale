/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.appschema.model;

import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.APP_SCHEMA_NAMESPACE;
import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.APP_SCHEMA_PREFIX;
import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.getFirstElementByTagName;
import static eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils.propertyTypeFromDOM;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.esdihumboldt.hale.common.align.io.impl.DOMEntityDefinitionHelper;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.ComplexValueType;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Complex value for {@link FeatureChaining} configuration.
 * 
 * <p>
 * Since feature chaining configuration is supposed to be serialized to XML and
 * stored in the project file, and since {@link PropertyEntityDefinition}
 * objects can't be deserialized at project loading (entity definitions can't be
 * resolved before a {@link TypeIndex} is created), the
 * {@link #fromDOM(Element, Void)} method deserializes them to their JAXB
 * counterpart, {@link PropertyType}, leaving the entity resolution task to
 * downstream code.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class FeatureChainingComplexType implements ComplexValueType<FeatureChaining, Void> {

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ComplexValueType#fromDOM(org.w3c.dom.Element,
	 *      java.lang.Object)
	 */
	@Override
	public FeatureChaining fromDOM(Element fragment, Void context) {
		FeatureChaining value = new FeatureChaining();

		if (fragment != null) {
			NodeList joinElList = fragment.getElementsByTagNameNS(APP_SCHEMA_NAMESPACE, "join");
			for (int i = 0; i < joinElList.getLength(); i++) {
				Element joinEl = (Element) joinElList.item(i);

				JoinConfiguration joinConf = new JoinConfiguration();
				joinConf.setJoinCellId(joinEl.getAttribute("id"));
				value.joins.put(joinConf.joinCellId, joinConf);

				NodeList chainElList = joinEl.getElementsByTagName("chain");
				for (int j = 0; j < chainElList.getLength(); j++) {
					Element chainEl = (Element) chainElList.item(j);

					ChainConfiguration chainConf = new ChainConfiguration();
					chainConf.setChainIndex(Integer.valueOf(chainEl.getAttribute("index")));
					chainConf.setPrevChainIndex(Integer.valueOf(chainEl
							.getAttribute("prevChainIndex")));

					Element nestedTypeTargetEl = getFirstElementByTagName(chainEl, "property",
							"http://www.esdi-humboldt.eu/hale/alignment");
					// I can't resolve the property entity definition here,
					// 'cause I've no target schema index available to perform
					// the resolution: I'll do it later, during the export
					PropertyType propertyType = propertyTypeFromDOM(nestedTypeTargetEl);
					chainConf.setJaxbNestedTypeTarget(propertyType);

					Element mappingNameEl = getFirstElementByTagName(chainEl, "mapping");
					if (mappingNameEl != null) {
						String mappingName = mappingNameEl.getAttribute("name");
						chainConf.setMappingName(mappingName);
					}

					joinConf.chains.put(chainConf.chainIndex, chainConf);
				}
			}
		}

		return value;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ComplexValueType#toDOM(java.lang.Object)
	 */
	@Override
	public Element toDOM(FeatureChaining value) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			Element featureChainingEl = doc
					.createElementNS(APP_SCHEMA_NAMESPACE, "FeatureChaining");
			featureChainingEl.setPrefix(APP_SCHEMA_PREFIX);

			if (value != null && value.joins != null) {
				for (JoinConfiguration joinConf : value.joins.values()) {
					Element joinEl = doc.createElementNS(APP_SCHEMA_NAMESPACE, "join");
					joinEl.setAttribute("id", joinConf.getJoinCellId());
					featureChainingEl.appendChild(joinEl);

					if (joinConf.chains != null) {
						for (ChainConfiguration chainConf : joinConf.chains.values()) {
							Element chainEl = doc.createElementNS(APP_SCHEMA_NAMESPACE, "chain");
							chainEl.setAttribute("index",
									Integer.toString(chainConf.getChainIndex()));
							chainEl.setAttribute("prevChainIndex",
									Integer.toString(chainConf.getPrevChainIndex()));
							joinEl.appendChild(chainEl);

							PropertyEntityDefinition nestedTypeTarget = chainConf
									.getNestedTypeTarget();
							Element nestedTargetEl = DOMEntityDefinitionHelper
									.propertyToDOM(nestedTypeTarget);
							if (nestedTargetEl != null) {
								Node nestedTargetAdopted = doc.adoptNode(nestedTargetEl);
								if (nestedTargetAdopted == null) {
									nestedTargetAdopted = doc.importNode(nestedTargetEl, true);
								}
								chainEl.appendChild(nestedTargetAdopted);
							}
							if (chainConf.mappingName != null
									&& !chainConf.mappingName.trim().isEmpty()) {
								Element mappingEl = doc.createElementNS(APP_SCHEMA_NAMESPACE,
										"mapping");
								mappingEl.setAttribute("name", chainConf.mappingName);
								chainEl.appendChild(mappingEl);
							}
						}
					}
				}
			}

			return featureChainingEl;
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ComplexValueType#getContextType()
	 */
	@Override
	public Class<? extends Void> getContextType() {
		return Void.class;
	}

}
