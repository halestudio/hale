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

package eu.esdihumboldt.cst.functions.geometric;

import java.util.Map;

import com.google.common.collect.ListMultimap;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.buffer.BufferBuilder;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;

/**
 * Network expansion function.
 * @author Simon Templer
 */
public class NetworkExpansion extends AbstractSingleTargetPropertyTransformation<TransformationEngine> {
	
	/**
	 * Name of the parameter specifying the buffer width.
	 */
	public static final String PARAMETER_BUFFER_WIDTH = "bufferWidth";
	
	private static int CAP_STYLE = BufferParameters.CAP_ROUND;

	/**
	 * @see AbstractSingleTargetPropertyTransformation#evaluate(String, TransformationEngine, ListMultimap, String, PropertyEntityDefinition, Map, TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier,
			TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException, NoResultException {
		// get the buffer width parameter
		String bufferWidthString = getParameterChecked(PARAMETER_BUFFER_WIDTH);
		double bufferWidth = Double.parseDouble(bufferWidthString);
		
		// get input geometry
		PropertyValue input = variables.get(null).get(0);
		Object inputValue = input.getValue();
		if (inputValue instanceof Instance) {
			inputValue = ((Instance) inputValue).getValue();
		}
		
		CRSDefinition crs = null;
		Geometry old_geometry = null;
		
		if (inputValue instanceof GeometryProperty<?>) {
			GeometryProperty<?> geomProp = (GeometryProperty<?>) inputValue;
			old_geometry = geomProp.getGeometry();
			crs = geomProp.getCRSDefinition();
		}
		else if (inputValue instanceof Geometry) {
			old_geometry = (Geometry) inputValue;
		}
		
		if (old_geometry != null) {
			Geometry new_geometry = null;
			BufferParameters bufferParameters = new BufferParameters();
			bufferParameters.setEndCapStyle(CAP_STYLE);
			BufferBuilder bb = new BufferBuilder(new BufferParameters());
			new_geometry = bb.buffer(old_geometry, bufferWidth);
			
			// try to yield a result compatible to the target
			TypeDefinition targetType = resultProperty.getDefinition().getPropertyType();
			//TODO check element type?
			Class<?> binding = targetType.getConstraint(Binding.class).getBinding();
			if (Geometry.class.isAssignableFrom(binding) 
					&& binding.isAssignableFrom(new_geometry.getClass())) {
				return new_geometry;
			}
			return new DefaultGeometryProperty<Geometry>(crs, new_geometry);
		}
		else {
			throw new TransformationException("Geometry for network expansion could not be retrieved.");
		}
	}

}
