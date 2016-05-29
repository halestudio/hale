/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.oml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentReader;
import eu.esdihumboldt.hale.common.align.io.impl.internal.AlignmentBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ChildContextBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.EntityBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.NamedEntityBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValueBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.PropertyBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.TypeBean;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.io.oml.helper.AssignTranslator;
import eu.esdihumboldt.hale.io.oml.helper.CalculateAreaTranslator;
import eu.esdihumboldt.hale.io.oml.helper.CalculateLengthTranslator;
import eu.esdihumboldt.hale.io.oml.helper.CentroidTranslator;
import eu.esdihumboldt.hale.io.oml.helper.ClassificationMappingTranslator;
import eu.esdihumboldt.hale.io.oml.helper.DateExtractionTranslator;
import eu.esdihumboldt.hale.io.oml.helper.FormattedStringTranslator;
import eu.esdihumboldt.hale.io.oml.helper.FunctionTranslator;
import eu.esdihumboldt.hale.io.oml.helper.GeographicalNameTranslator;
import eu.esdihumboldt.hale.io.oml.helper.IdentifierTranslator;
import eu.esdihumboldt.hale.io.oml.helper.MathematicalExpressionTranslator;
import eu.esdihumboldt.hale.io.oml.helper.NetworkExpansionTranslator;
import eu.esdihumboldt.hale.io.oml.helper.NilReasonTranslator;
import eu.esdihumboldt.hale.io.oml.helper.NotSupportedTranslator;
import eu.esdihumboldt.hale.io.oml.helper.OrdinatesToPointTranslator;
import eu.esdihumboldt.hale.io.oml.helper.RenameTranslator;
import eu.esdihumboldt.hale.io.oml.helper.RetypeTranslator;
import eu.esdihumboldt.hale.io.oml.internal.goml.align.Alignment;
import eu.esdihumboldt.hale.io.oml.internal.goml.align.Entity;
import eu.esdihumboldt.hale.io.oml.internal.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.ComposedFeatureClass;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.ComposedProperty;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.FeatureClass;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Property;
import eu.esdihumboldt.hale.io.oml.internal.goml.rdf.DetailedAbout;
import eu.esdihumboldt.hale.io.oml.internal.goml.rdf.IDetailedAbout;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IEntity;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IParameter;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * This class reads the OML Document into Java Object.
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class OmlReader extends AbstractAlignmentReader implements AlignmentReader {

	private final Map<String, FunctionTranslator> map = new HashMap<String, FunctionTranslator>();

	/**
	 * Default Constructor
	 */
	public OmlReader() {
		map.put("eu.esdihumboldt.cst.corefunctions.RenameAttributeFunction", new RenameTranslator());
		map.put("eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction",
				new RetypeTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.DateExtractionFunction",
				new DateExtractionTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.ClassificationMappingFunction",
				new ClassificationMappingTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.ConstantValueFunction", new AssignTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.GenericMathFunction",
				new MathematicalExpressionTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.OrdinatesToPointFunction",
				new OrdinatesToPointTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.NetworkExpansionFunction",
				new NetworkExpansionTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.ConcatenationOfAttributesFunction",
				new FormattedStringTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.BoundingBoxFunction",
				new NotSupportedTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.CalculateArea", new CalculateAreaTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.CalculateLength",
				new CalculateLengthTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.CentroidFunction", new CentroidTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.ClipByRectangleFunction",
				new NotSupportedTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.NilReasonFunction", new NilReasonTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.inspire.GeographicalNameFunction",
				new GeographicalNameTranslator());
		map.put("eu.esdihumboldt.cst.corefunctions.inspire.IdentifierFunction",
				new IdentifierTranslator());
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected MutableAlignment loadAlignment(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		try {
			progress.begin("Load ontology mapping file", ProgressIndicator.UNKNOWN);

			OmlRdfReader reader = new OmlRdfReader();

			Alignment alignment = reader.read(getSource().getLocation().toURL());

			AlignmentBean align = new AlignmentBean();

			List<CellBean> cells = new ArrayList<CellBean>();

			List<ICell> map = alignment.getMap();

			for (ICell cell : map) {

				// create a new CellBean for each ICell
				CellBean cellBean = new CellBean();

				IEntity entity = cell.getEntity1();
				IEntity entity2 = cell.getEntity2();

				// temporary list to be copied into the CellBean
				List<NamedEntityBean> temp_source = new ArrayList<NamedEntityBean>();
				List<NamedEntityBean> temp_target = new ArrayList<NamedEntityBean>();

				setBeanLists(entity, temp_source, getSourceSchema());
				setBeanLists(entity2, temp_target, getTargetSchema());

				// set source/target lists of the CellBean
				cellBean.setSource(temp_source);
				cellBean.setTarget(temp_target);

				// check if one of the entities has a transformation
				if (entity.getTransformation() != null || entity2.getTransformation() != null) {
					// set parameters and transformation id for the cellBean
					// from
					// entity 1 if it has the transformation
					if (entity.getTransformation() != null) {
						setParameters(cellBean, entity, reporter, cell);
						setTransformationId(cellBean, entity);
					}
					else {
						// else set parameters and transformation id from entity
						// 2
						setParameters(cellBean, entity2, reporter, cell);
						setTransformationId(cellBean, entity2);
					}
				}

				// add the CellBean to a list of CellBeans
				cells.add(cellBean);
			}

			// set the cells for the alignment after the all iterations
			align.setCells(cells);

			MutableAlignment mutableAlignment = align.createAlignment(reporter, getSourceSchema(),
					getTargetSchema(), new PathUpdate(null, null));

			reporter.setSuccess(true);

			return mutableAlignment;
		} finally {
			progress.end();
		}
	}

	@Override
	protected String getDefaultTypeName() {
		return "HALE 2.1 Alignment";
	}

	private void setBeanLists(IEntity entity, List<NamedEntityBean> list, TypeIndex schema) {

		if (entity.getAbout().getAbout().equals(Entity.NULL_ENTITY.getAbout().getAbout())) {
			return;
		}

		if (entity instanceof ComposedFeatureClass) {
			ComposedFeatureClass cfc = (ComposedFeatureClass) entity;

			List<FeatureClass> coll = cfc.getCollection();

			for (FeatureClass fc : coll) {
				setBeanLists(fc, list, schema);
			}
		}
		else if (entity instanceof ComposedProperty) {
			ComposedProperty cp = (ComposedProperty) entity;

			List<Property> coll = cp.getCollection();

			for (Property prop : coll) {
				setBeanLists(prop, list, schema);
			}

		}
		else if (entity instanceof FeatureClass) {

			// get the detailed about-information
			IDetailedAbout about = DetailedAbout.getDetailedAbout(entity.getAbout(), false);

			TypeBean typeBean = new TypeBean();

			setTypeNameBean(typeBean, about, schema);

			NamedEntityBean namedEntityBean = new NamedEntityBean();

			namedEntityBean.setEntity(typeBean);
			namedEntityBean.setName(null);

			list.add(namedEntityBean);

		}
		else if (entity instanceof Property) {
			// get the detailed about-information
			IDetailedAbout about = DetailedAbout.getDetailedAbout(entity.getAbout(), true);

			PropertyBean prop = new PropertyBean();
			List<ChildContextBean> childList = new ArrayList<ChildContextBean>();

			List<String> props = about.getProperties();

			for (String property : props) {
				ChildContextBean ccb = new ChildContextBean();
				ccb.setChildName(new QName(property));

				childList.add(ccb);
			}

			setTypeNameBean(prop, about, schema);
			prop.setProperties(childList);

			NamedEntityBean namedEntityBean = new NamedEntityBean();

			namedEntityBean.setEntity(prop);
			namedEntityBean.setName(null);

			list.add(namedEntityBean);

		}

	}

	private void setTypeNameBean(EntityBean<?> entityBean, IDetailedAbout about, TypeIndex schema) {

		QName name = new QName(about.getNamespace(), about.getFeatureClass());

		if (schema.getType(name) == null) {
			name = findElementType(schema, name);
		}

		entityBean.setTypeName(name);

	}

	private QName findElementType(TypeIndex schema, QName elementName) {
		if (schema instanceof SchemaSpace) {
			SchemaSpace ss = (SchemaSpace) schema;
			for (Schema schem : ss.getSchemas()) {
				if (schem instanceof XmlIndex) {
					XmlElement xmlelem = ((XmlIndex) schem).getElements().get(elementName);
					if (xmlelem != null) {
						return xmlelem.getType().getName();
					}
					// if there is no element try to find one with an extra "/"
					// sign in the namespace because in earlier version this
					// case can occur
					xmlelem = ((XmlIndex) schem).getElements().get(
							new QName(elementName.getNamespaceURI() + "/", elementName
									.getLocalPart()));
					if (xmlelem != null) {
						return xmlelem.getType().getName();
					}
				}
			}
		}
		else {
			for (TypeDefinition typedef : schema.getTypes()) {
				XmlElements xmlelem = typedef.getConstraint(XmlElements.class);
				for (XmlElement elem : xmlelem.getElements()) {
					if (elem.getName().equals(elementName)
							|| elem.getName().equals(
									new QName(elementName.getNamespaceURI() + "/", elementName
											.getLocalPart()))) {
						return typedef.getName();
					}
				}
			}
		}
		return elementName;
	}

	private void setParameters(CellBean cellBean, IEntity entity, IOReporter reporter, ICell cell) {

		String transId = entity.getTransformation().getService().getLocation();

		// get the list of parameters
		List<IParameter> list = entity.getTransformation().getParameters();

		// create a list of ParameterValue (because
		// setTransformationParameters needs a list)
		List<ParameterValueBean> params = new ArrayList<ParameterValueBean>();

		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i).getName();
			String value = list.get(i).getValue();

			// create the ParameterValue for the CellBean
			ParameterValueBean paramVal = new ParameterValueBean(name, value);

			// add the ParameterValue
			params.add(paramVal);
		}

		// set the new transformation parameters
		if (map.containsKey(transId)) {
			cellBean.setTransformationParameters(map.get(transId).getNewParameters(params,
					cellBean, reporter, cell));
		}
		else {
			cellBean.setTransformationParameters(params);
		}

	}

	private void setTransformationId(CellBean cellBean, IEntity entity) {

		String transId = entity.getTransformation().getService().getLocation();

		// set the new transformation identifier
		if (map.containsKey(transId) && map.get(transId).getTransformationId() != null) {
			cellBean.setTransformationIdentifier(map.get(transId).getTransformationId());
		}
		else {
			cellBean.setTransformationIdentifier(transId);
		}

	}

}
