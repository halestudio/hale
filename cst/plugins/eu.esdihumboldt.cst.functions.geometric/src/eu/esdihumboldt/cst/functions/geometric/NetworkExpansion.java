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

package eu.esdihumboldt.cst.functions.geometric;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.script.ScriptException;

import org.springframework.core.convert.ConversionException;

import com.google.common.collect.ListMultimap;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.operation.buffer.BufferBuilder;
import org.locationtech.jts.operation.buffer.BufferParameters;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.convert.ConversionUtil;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.scripting.scripts.mathematical.MathScript;
import eu.esdihumboldt.hale.common.scripting.transformation.AbstractSingleTargetScriptedPropertyTransformation;

/**
 * Network expansion function.
 * 
 * @author Simon Templer
 */
public class NetworkExpansion extends
		AbstractSingleTargetScriptedPropertyTransformation<TransformationEngine> implements
		NetworkExpansionFunction {

	private static int CAP_STYLE = BufferParameters.CAP_ROUND;

	/**
	 * @see AbstractSingleTargetScriptedPropertyTransformation#evaluate(String,
	 *      TransformationEngine, ListMultimap, String,
	 *      PropertyEntityDefinition, Map, TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {
		// get the buffer width parameter
		String bufferWidthString = getTransformedParameterChecked(PARAMETER_BUFFER_WIDTH).as(
				String.class);

		double bufferWidth;
		try {
			bufferWidth = Double.parseDouble(bufferWidthString);
		} catch (NumberFormatException e) {
			// For backwards compatibility try to run the string as script.
			MathScript mathScript = new MathScript();
			try {
				Object result = mathScript.evaluate(bufferWidthString,
						variables.get(ENTITY_VARIABLE), getExecutionContext());
				bufferWidth = ConversionUtil.getAs(result, Double.class);
			} catch (ScriptException e1) {
				throw new TransformationException("Failed to evaluate buffer width expression.", e1);
			} catch (ConversionException e2) {
				throw new TransformationException(
						"Failed to convert buffer width expression result to double.", e2);
			}
		}

		// get input geometry
		PropertyValue input = variables.get(null).get(0);
		Object inputValue = input.getValue();
		if (inputValue instanceof Instance) {
			inputValue = ((Instance) inputValue).getValue();
		}

		GeometryProperty<Geometry> result = calculateBuffer(inputValue, bufferWidth, log);

		// try to yield a result compatible to the target
		if (result != null) {
			TypeDefinition targetType = resultProperty.getDefinition().getPropertyType();
			// TODO check element type?
			Class<?> binding = targetType.getConstraint(Binding.class).getBinding();
			if (Geometry.class.isAssignableFrom(binding)
					&& binding.isAssignableFrom(result.getGeometry().getClass())) {
				return result.getGeometry();
			}
			else {
				return result;
			}
		}

		throw new TransformationException("Geometry for network expansion could not be retrieved.");
	}

	/**
	 * Calculate a buffer geometry.
	 * 
	 * @param geometryHolder the geometry or object holding a geometry
	 * @param bufferWidth the buffer width
	 * @param log the transformation log, may be <code>null</code>
	 * @return the buffer geometry or <code>null</code>
	 */
	@Nullable
	public static GeometryProperty<Geometry> calculateBuffer(Object geometryHolder,
			double bufferWidth, @Nullable TransformationLog log) {
		// find contained geometries
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);
		GeometryFinder geoFind = new GeometryFinder(null);
		traverser.traverse(geometryHolder, geoFind);

		List<GeometryProperty<?>> geometries = geoFind.getGeometries();

		GeometryProperty<?> old_geometry = null;
		if (!geometries.isEmpty()) {
			old_geometry = geometries.get(0);

			if (geometries.size() > 1) {
				if (log != null) {
					log.warn(log
							.createMessage(
									"Multiple geometries found, but network expansion is only done on the first.",
									null));
				}
			}
		}

		GeometryProperty<Geometry> result = null;
		if (old_geometry != null) {
			Geometry new_geometry = null;
			BufferParameters bufferParameters = new BufferParameters();
			bufferParameters.setEndCapStyle(CAP_STYLE);
			BufferBuilder bb = new BufferBuilder(new BufferParameters());
			new_geometry = bb.buffer(old_geometry.getGeometry(), bufferWidth);

			result = new DefaultGeometryProperty<Geometry>(old_geometry.getCRSDefinition(),
					new_geometry);
		}

		return result;
	}

}
