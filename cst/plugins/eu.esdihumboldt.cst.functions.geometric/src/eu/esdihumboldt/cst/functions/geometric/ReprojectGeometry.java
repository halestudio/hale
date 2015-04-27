package eu.esdihumboldt.cst.functions.geometric;

import java.util.List;
import java.util.Map;

import net.jcip.annotations.Immutable;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.google.common.collect.ListMultimap;
import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

@Immutable
public class ReprojectGeometry extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine> implements
		ReprojectGeometryFunction {

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {

		// Get input geometry
		PropertyValue input = variables.get("source").get(0);
		Object inputValue = input.getValue();
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);
		GeometryFinder geoFind = new GeometryFinder(null);
		traverser.traverse(inputValue, geoFind);
		List<GeometryProperty<?>> geoms = geoFind.getGeometries();
		CoordinateReferenceSystem sourceCRS = geoms.get(0).getCRSDefinition().getCRS();
		Geometry sourceGeometry = geoms.get(0).getGeometry();

		Geometry resultGeometry = sourceGeometry;
		CoordinateReferenceSystem targetCRS = sourceCRS;

		// Get input parameter
		String srs = getParameterChecked(PARAMETER_REFERENCE_SYSTEM).as(String.class);
		if (srs != null) {
			try {
				targetCRS = CRS.decode(srs);
			} catch (FactoryException e) {
				throw new TransformationException(
						"Error to find destiantion Cordinate reference System.", e);
			}

			// Retrieve transformation from cell context, or create a new
			// instance
			Map<Object, Object> cellContext = getExecutionContext().getCellContext();
			MathTransform transform = getOrCreateMathTransform(sourceCRS, targetCRS, cellContext);

			// Apply transformation
			try {
				resultGeometry = JTS.transform(sourceGeometry, transform);
			} catch (MismatchedDimensionException | TransformException e) {
				throw new TransformationException("Problem on execute transformation from: "
						+ sourceCRS + " to " + targetCRS, e);
			}
		}

		return new DefaultGeometryProperty<Geometry>(new CodeDefinition(CRS.toSRS(targetCRS),
				targetCRS), resultGeometry);

	}

	/**
	 * Attempt to find a math transform between the specified Coordinate
	 * Reference Systems.
	 * <p>
	 * The method first tries to look up a relevant MathTransform instance from
	 * the provided {@code context} object; then, if none was found, it creates
	 * a new one and stores it in the context, to allow its reuse by following
	 * reproject transformations.
	 * </p>
	 * 
	 * @param sourceCRS The source CRS.
	 * @param targetCRS The target CRS.
	 * @param context The context.
	 * @return The math transform from {@code sourceCRS} to {@code targetCRS}.
	 * @throws TransformationException if no math transform could be found
	 */
	private MathTransform getOrCreateMathTransform(CoordinateReferenceSystem sourceCRS,
			CoordinateReferenceSystem targetCRS, Map<Object, Object> context)
			throws TransformationException {
		MathTransform transform = null;
		String key = sourceCRS.getName().hashCode() + " --> " + targetCRS.getName().hashCode();

		synchronized (context) {
			transform = (MathTransform) context.get(key);
			if (transform == null) {
				transform = createMathTransform(sourceCRS, targetCRS);

				context.put(key, transform);
			}
		}

		return transform;
	}

	/**
	 * Attempt to find a math transform between the specified Coordinate
	 * Reference Systems.
	 * 
	 * @param sourceCRS The source CRS.
	 * @param targetCRS The target CRS.
	 * @return The math transform from {@code sourceCRS} to {@code targetCRS}.
	 * @throws TransformationException if no math transform could be found
	 */
	private MathTransform createMathTransform(CoordinateReferenceSystem sourceCRS,
			CoordinateReferenceSystem targetCRS) throws TransformationException {
		MathTransform transform = null;
		try {
			transform = CRS.findMathTransform(sourceCRS, targetCRS, false);
			// Transformation cannot be found because the sourceCRS is
			// missing bursa-wolf parameters
		} catch (FactoryException ex1) {
			try {
				Integer code = CRS.lookupEpsgCode(sourceCRS, true);
				if (code != null) {
					transform = CRS.findMathTransform(CRS.decode("EPSG:" + code, true), targetCRS);
				}
				else {
					throw new TransformationException(
							"Unable to find requested transformation from: " + sourceCRS + " to "
									+ targetCRS);
				}
			} catch (FactoryException ex2) {
				throw new TransformationException("Problem on execute transformation from: "
						+ sourceCRS + " to " + targetCRS, ex2);
			}
		}
		return transform;
	}
}
