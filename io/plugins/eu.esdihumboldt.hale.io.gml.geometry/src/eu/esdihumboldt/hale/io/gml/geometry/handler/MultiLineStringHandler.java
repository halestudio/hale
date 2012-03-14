/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.gml.geometry.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GMLGeometryUtil;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;

/**
 * Handler for multi line geometries
 * 
 * @author Patrick Lieb
 */
public class MultiLineStringHandler extends FixedConstraintsGeometryHandler {

	private static final String MULTI_LINE_STRING_TYPE = "MultiLineStringType";

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler#createGeometry(eu.esdihumboldt.hale.common.instance.model.Instance)
	 */
	@Override
	public Object createGeometry(Instance instance)
			throws GeometryNotSupportedException {

		MultiLineString line = null;
		LineStringHandler handler = new LineStringHandler();

		Collection<Object> values = PropertyResolver.getValues(instance,
				"lineStringMember.LineString", false);
		if (values != null && !values.isEmpty()) {
			Iterator<Object> iterator = values.iterator();
			List<LineString> lines = new ArrayList<LineString>();
			while (iterator.hasNext()) {
				Object value = iterator.next();

				if (value instanceof Instance) {
					try {
						@SuppressWarnings("unchecked")
						DefaultGeometryProperty<LineString> lineString = (DefaultGeometryProperty<LineString>) handler
								.createGeometry((Instance) value);
						lines.add(lineString.getGeometry());
					} catch (GeometryNotSupportedException e) {
						throw new GeometryNotSupportedException(
								"Could not parse lineStringMember", e);
					}
				}
			}
			LineString[] lineStrings = lines.toArray(new LineString[lines
					.size()]);
			line = getGeometryFactory().createMultiLineString(lineStrings);
		}

		if (line != null) {
			CRSDefinition crsDef = GMLGeometryUtil.findCRS(instance);
			return new DefaultGeometryProperty<MultiLineString>(crsDef, line);
		}

		throw new GeometryNotSupportedException();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler#initConstraints()
	 */
	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>(
				2);

		constraints.add(Binding.get(GeometryProperty.class));
		constraints.add(GeometryType.get(MultiLineString.class));

		constraints.add(new GeometryFactory(this));

		return constraints;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.AbstractGeometryHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();

		types.add(new QName(NS_GML, MULTI_LINE_STRING_TYPE));
		types.add(new QName(NS_GML_32, MULTI_LINE_STRING_TYPE));

		return types;
	}

}
