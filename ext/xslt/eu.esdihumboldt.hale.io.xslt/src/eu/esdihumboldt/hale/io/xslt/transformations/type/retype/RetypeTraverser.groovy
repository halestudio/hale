/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.transformations.type.retype

import javax.xml.XMLConstants
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamWriter

import com.tinkerpop.blueprints.Vertex

import de.cs3d.util.logging.ALogger
import de.cs3d.util.logging.ALoggerFactory
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag
import eu.esdihumboldt.hale.io.gml.writer.internal.GmlWriterUtil
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractTransformationTraverser



/**
 * TODO Type description
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
class RetypeTraverser extends AbstractTransformationTraverser {

	private static final ALogger log = ALoggerFactory.getLogger(RetypeTraverser);

	private final XMLStreamWriter writer

	/**
	 * @param writer
	 */
	RetypeTraverser(XMLStreamWriter writer) {
		this.writer = writer
	}

	@Override
	protected void visitProperty(Vertex node) {
		/*
		 * XXX For now, we just assume that for every target
		 * node, the corresponding element or attribute must be
		 * there.
		 */
		if (node.definition().asProperty() != null &&
		node.definition().asProperty().getConstraint(XmlAttributeFlag).enabled) {
			// attribute
			// TODO
		}
		else {
			// group or element
			// XXX but how often?!
		}
	}

	@Override
	protected void leaveProperty(Vertex node) {
		if (node.definition().asProperty() == null ||
		!node.definition().asProperty().getConstraint(XmlAttributeFlag).enabled) {
			// group or element
			// XXX what to close?!
		}
	}

	@Override
	protected void handleUnmappedProperty(Vertex node) {
		if (node.cardinality().minOccurs > 0) {
			if (node.definition().asProperty() != null) {
				if (definition().asProperty().getConstraint(XmlAttributeFlag).enabled) {
					// a mandatory attribute
					log.warn("No mapping for a mandatory attribute");
				}
				else {
					// a mandatory element
					if (node.definition().asProperty().getConstraint(NillableFlag).enabled) {
						// a nillable mandatory element
						try {
							GmlWriterUtil.writeEmptyElement(writer,
									node.definition().name);
							writer.writeAttribute(
									XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
									"nil", "true");
						} catch (XMLStreamException e) {
							throw new IllegalStateException(e);
						}
					}
					else {
						// a mandatory element
						log.warn("No mapping for a mandatory element");
					}
				}
			}
			else {
				// a mandatory group
				log.warn("No mapping for a mandatory group");
			}
		}
	}
}
