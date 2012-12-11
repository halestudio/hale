/*
 * Copyright (c) 2012 Fraunhofer IGD
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

package eu.esdihumboldt.hale.io.xslt.transformations;

import java.io.ByteArrayOutputStream;
import java.util.Collection;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.velocity.VelocityContext;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TransformationTreeImpl;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.io.gml.writer.internal.GmlWriterUtil;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.io.xslt.XslTransformationUtil;
import eu.esdihumboldt.hale.io.xslt.XslTypeTransformation;
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractTransformationTraverser;
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractVelocityXslTypeTransformation;

/**
 * XSLT representation of the Retype function.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class XslRetype extends AbstractVelocityXslTypeTransformation implements
		XslTypeTransformation {

	private static final ALogger log = ALoggerFactory.getLogger(XslRetype.class);

	private static final String CONTEXT_PARAM_SELECT_INSTANCES = "select_instances";

	private static final String CONTEXT_PARAM_PROPERTIES = "properties";

	@Override
	protected void configureTemplate(final VelocityContext context, final Cell typeCell)
			throws TransformationException {
		Type source = (Type) CellUtil.getFirstEntity(typeCell.getSource());

		TypeEntityDefinition ted = source.getDefinition();
		context.put(CONTEXT_PARAM_SELECT_INSTANCES,
				XslTransformationUtil.selectInstances(ted, "/", context().getNamespaceContext()));

		String properties = createPropertiesFragment(typeCell);
		context.put(CONTEXT_PARAM_PROPERTIES, properties);
	}

	/**
	 * 
	 * @param typeCell
	 * @return
	 * @throws TransformationException
	 */
	protected String createPropertiesFragment(final Cell typeCell) throws TransformationException {
		/*
		 * Create the transformation tree with only those property cells related
		 * to the type cell.
		 */
		Type target = (Type) CellUtil.getFirstEntity(typeCell.getTarget());
		final TransformationTree tree = new TransformationTreeImpl(target.getDefinition()
				.getDefinition(), context().getAlignment()) {

			@Override
			protected Collection<? extends Cell> getRelevantPropertyCells(Alignment alignment,
					TypeDefinition targetType) {
				return AlignmentUtil.getPropertyCellsFromTypeCell(alignment, typeCell);
			}

		};

		// TODO tree as GraphML as informative annotation into XSLT?
		try {
			ByteArrayOutputStream propsOut = new ByteArrayOutputStream();
			try {
				final XMLStreamWriter writer = XslTransformationUtil.setupXMLWriter(propsOut,
						context().getNamespaceContext());

				/*
				 * TODO Do an additional traversal of the transformation tree
				 * first, where for each target node the condition that it is
				 * actually needed is determined.
				 * 
				 * This would have to be from source to target, as parent target
				 * nodes must take into account the conditions present on their
				 * children.
				 */

				AbstractTransformationTraverser trav = new AbstractTransformationTraverser() {

					@Override
					protected void visitProperty(TargetNode node) {
						/*
						 * XXX For now, we just assume that for every target
						 * node, the corresponding element or attribute must be
						 * there.
						 */

						if (node.getDefinition().asProperty() != null
								&& node.getDefinition().asProperty()
										.getConstraint(XmlAttributeFlag.class).isEnabled()) {
							// attribute
							// TODO
						}
						else {
							// group or element
							// XXX but how often?!
						}
					}

					@Override
					protected void leaveProperty(TargetNode node) {
						if (node.getDefinition().asProperty() == null
								|| !node.getDefinition().asProperty()
										.getConstraint(XmlAttributeFlag.class).isEnabled()) {
							// group or element
							// XXX what to close?!
						}
					}

					@Override
					protected void handleUnmappedProperty(ChildDefinition<?> definition) {
						if (DefinitionUtil.getCardinality(definition).getMinOccurs() > 0) {
							if (definition.asProperty() != null) {
								if (definition.asProperty().getConstraint(XmlAttributeFlag.class)
										.isEnabled()) {
									// a mandatory attribute
									log.warn("No mapping for a mandatory attribute");
								}
								else {
									// a mandatory element
									if (definition.asProperty().getConstraint(NillableFlag.class)
											.isEnabled()) {
										// a nillable mandatory element
										try {
											GmlWriterUtil.writeEmptyElement(writer,
													definition.getName());
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
				};

				trav.traverse(tree);
			} finally {
				propsOut.close();
			}

			return propsOut.toString("UTF-8");
		} catch (Exception e) {
			throw new TransformationException("Failed to create property transformations", e);
		}
	}
}
