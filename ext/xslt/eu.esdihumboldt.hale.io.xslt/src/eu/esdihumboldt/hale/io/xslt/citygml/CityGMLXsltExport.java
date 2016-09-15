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

package eu.esdihumboldt.hale.io.xslt.citygml;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.io.gml.CityGMLConstants;
import eu.esdihumboldt.hale.io.gml.geometry.GMLConstants;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.io.xslt.GroovyXslHelpers;
import eu.esdihumboldt.hale.io.xslt.SourceContextProvider;
import eu.esdihumboldt.hale.io.xslt.XsltConstants;
import eu.esdihumboldt.hale.io.xslt.XsltExport;
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext;
import eu.esdihumboldt.hale.io.xslt.functions.XslVariable;
import eu.esdihumboldt.hale.io.xslt.functions.impl.XslVariableImpl;

/**
 * Specific XSLT export for mappings with a CityGML based schema as target
 * model.
 * 
 * @author Simon Templer
 */
public class CityGMLXsltExport extends XsltExport
		implements CityGMLConstants, GMLConstants, XsltConstants {

	private SourceContextProvider sourceContext;

	private XmlElement targetCityModel;

	private XmlElement sourceCityModel;

	@Override
	protected void init(XmlIndex sourceIndex, XmlIndex targetIndex)
			throws IOProviderConfigurationException {
		super.init(sourceIndex, targetIndex);

		// scan target schema for CityModel
		targetCityModel = findCityModel(targetIndex);
		if (targetCityModel != null) {
			QName name = targetCityModel.getName();
			setParameter(PARAM_ROOT_ELEMENT_NAMESPACE, new ParameterValue(name.getNamespaceURI()));
			setParameter(PARAM_ROOT_ELEMENT_NAME, new ParameterValue(name.getLocalPart()));
		}
		else {
			throw new IOProviderConfigurationException(MessageFormat
					.format("Element {0} not found in the target schema.", CITY_MODEL_ELEMENT));
		}

		// scan source schema for CityModel
		sourceCityModel = findCityModel(sourceIndex);
		if (sourceCityModel != null) {
			// create a custom source context
			sourceContext = new CityGMLSourceContext(sourceCityModel);

			// TODO copy envelope?
		}
	}

	private XmlElement findCityModel(XmlIndex schema) {
		for (XmlElement element : schema.getElements().values()) {
			QName name = element.getName();

			if (CITY_MODEL_ELEMENT.equals(name.getLocalPart())
					&& name.getNamespaceURI().startsWith(CITYGML_NAMESPACE_CORE)) {
				return element;
			}
		}

		return null;
	}

	@Override
	protected void writeContainerIntro(XMLStreamWriter writer, XsltGenerationContext context)
			throws XMLStreamException, IOException {
		if (targetCityModel != null && sourceCityModel != null) {
			// copy GML boundedBy

			// do it in a special template
			String template = context.reserveTemplateName("copyBoundedBy");
			writer.writeStartElement(NS_URI_XSL, "call-template");
			writer.writeAttribute("name", template);
			writer.writeEndElement();

			// find source property
			PropertyDefinition sourceBB = null;
			for (ChildDefinition<?> child : sourceCityModel.getType().getChildren()) {
				if (child.asProperty() != null && child.getName().getLocalPart().equals("boundedBy")
						&& child.getName().getNamespaceURI().startsWith(GML_NAMESPACE_CORE)) {
					sourceBB = child.asProperty();
					break;
				}
			}

			// find target property
			PropertyDefinition targetBB = null;
			for (ChildDefinition<?> child : targetCityModel.getType().getChildren()) {
				if (child.asProperty() != null && child.getName().getLocalPart().equals("boundedBy")
						&& child.getName().getNamespaceURI().startsWith(GML_NAMESPACE_CORE)) {
					targetBB = child.asProperty();
					break;
				}
			}

			if (sourceBB != null && targetBB != null) {
				// create templated
				OutputStreamWriter out = new OutputStreamWriter(
						context.addInclude().openBufferedStream(), getCharset());
				try {
					out.write("<xsl:template name=\"" + template + "\">");

					StringBuilder selectSource = new StringBuilder();
					selectSource.append('/');
					selectSource.append(
							GroovyXslHelpers.asPrefixedName(sourceCityModel.getName(), context));
					selectSource.append('/');
					selectSource
							.append(GroovyXslHelpers.asPrefixedName(sourceBB.getName(), context));
					selectSource.append("[1]");

					out.write("<xsl:for-each select=\"" + selectSource.toString() + "\">");

					String elementName = GroovyXslHelpers.asPrefixedName(targetBB.getName(),
							context);
					out.write("<" + elementName + ">");

					// create bogus rename cell
					DefaultCell cell = new DefaultCell();

					// source
					ListMultimap<String, Entity> source = ArrayListMultimap.create();
					List<ChildContext> sourcePath = new ArrayList<ChildContext>();
					sourcePath.add(new ChildContext(sourceBB));
					PropertyEntityDefinition sourceDef = new PropertyEntityDefinition(
							sourceCityModel.getType(), sourcePath, SchemaSpaceID.SOURCE, null);
					source.put(null, new DefaultProperty(sourceDef));
					cell.setSource(source);

					// target
					ListMultimap<String, Entity> target = ArrayListMultimap.create();
					List<ChildContext> targetPath = new ArrayList<ChildContext>();
					targetPath.add(new ChildContext(targetBB));
					PropertyEntityDefinition targetDef = new PropertyEntityDefinition(
							targetCityModel.getType(), targetPath, SchemaSpaceID.TARGET, null);
					target.put(null, new DefaultProperty(targetDef));
					cell.setTarget(target);

					// parameters
					ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
					parameters.put(RenameFunction.PARAMETER_STRUCTURAL_RENAME,
							new ParameterValue("true"));
					parameters.put(RenameFunction.PARAMETER_IGNORE_NAMESPACES,
							new ParameterValue("true"));
					cell.setTransformationParameters(parameters);

					// variables
					ListMultimap<String, XslVariable> variables = ArrayListMultimap.create();
					variables.put(null, new XslVariableImpl(sourceDef, "."));

					try {
						out.write(context.getPropertyTransformation(RenameFunction.ID)
								.selectFunction(cell).getSequence(cell, variables, context, null));
					} catch (TransformationException e) {
						throw new IllegalStateException(
								"Failed to create template for boundedBy copy.", e);
					}

					out.write("</" + elementName + ">");

					out.write("</xsl:for-each>");

					out.write("</xsl:template>");
				} finally {
					out.close();
				}
			}
		}
	}

	@Override
	protected SourceContextProvider getSourceContext() {
		return sourceContext;
	}

}
