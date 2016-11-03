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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.io.gml.writer.internal.GmlWriterUtil;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryConverterRegistry.ConversionLadder;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.CompositeSurfaceWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.CurveSingleSegmentWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.CurveWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.EnvelopeWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.LegacyMultiPolygonWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.LegacyPolygonWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.LineStringWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.MultiCurveWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.MultiLineStringWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.MultiPointWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.MultiPolygonWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.Pattern;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.PointWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers.PolygonWriter;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;

/**
 * Write geometries for a GML document.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class StreamGeometryWriter extends AbstractTypeMatcher<Class<? extends Geometry>> {

	private static final ALogger log = ALoggerFactory.getLogger(StreamGeometryWriter.class);

	/**
	 * Get a geometry writer instance with a default configuration.
	 * 
	 * @param gmlNs the GML namespace
	 * @param simplifyGeometry if geometries should be simplified before writing
	 *            them if possible (e.g. a MultiGeometry with only one geometry
	 *            is reduced to the contained geometry)
	 * @return the geometry writer
	 */
	public static StreamGeometryWriter getDefaultInstance(String gmlNs, boolean simplifyGeometry) {
		StreamGeometryWriter sgm = new StreamGeometryWriter(gmlNs, simplifyGeometry);

		// TODO configure via extension?
		sgm.registerGeometryWriter(new CurveWriter());
		sgm.registerGeometryWriter(new CurveSingleSegmentWriter());
		sgm.registerGeometryWriter(new MultiCurveWriter());
		sgm.registerGeometryWriter(new PointWriter());
		sgm.registerGeometryWriter(new PolygonWriter());
		sgm.registerGeometryWriter(new LineStringWriter());
		sgm.registerGeometryWriter(new MultiPolygonWriter());
		sgm.registerGeometryWriter(new CompositeSurfaceWriter());
		sgm.registerGeometryWriter(new MultiPointWriter());
		sgm.registerGeometryWriter(new MultiLineStringWriter());
		sgm.registerGeometryWriter(new LegacyPolygonWriter());
		sgm.registerGeometryWriter(new LegacyMultiPolygonWriter());
		sgm.registerGeometryWriter(new EnvelopeWriter());

		return sgm;
	}

	/**
	 * The GML namespace
	 */
	private final String gmlNs;

	/**
	 * Geometry types mapped to compatible writers
	 */
	private final Map<Class<? extends Geometry>, Set<GeometryWriter<?>>> geometryWriters = new HashMap<Class<? extends Geometry>, Set<GeometryWriter<?>>>();

	/**
	 * Types mapped to geometry types mapped to matched definition paths
	 */
	// XXX stored paths instead per attribute definition?
	private final Map<TypeDefinition, Map<Class<? extends Geometry>, List<DefinitionPath>>> storedPaths = new HashMap<TypeDefinition, Map<Class<? extends Geometry>, List<DefinitionPath>>>();

	private final boolean simplifyGeometry;

	/**
	 * Constructor
	 * 
	 * @param gmlNs the GML namespace
	 * @param simplifyGeometry if geometries should be simplified before writing
	 *            them if possible (e.g. a MultiGeometry with only one geometry
	 *            is reduced to the contained geometry)
	 */
	public StreamGeometryWriter(String gmlNs, boolean simplifyGeometry) {
		super();

		this.gmlNs = gmlNs;
		this.simplifyGeometry = simplifyGeometry;
	}

	/**
	 * Register a geometry writer
	 * 
	 * @param writer the geometry writer
	 */
	public void registerGeometryWriter(GeometryWriter<?> writer) {
		Class<? extends Geometry> geomType = writer.getGeometryType();
		Set<GeometryWriter<?>> writers = geometryWriters.get(geomType);
		if (writers == null) {
			writers = new HashSet<GeometryWriter<?>>();
			geometryWriters.put(geomType, writers);
		}

		writers.add(writer);
	}

	/**
	 * Write a geometry to a stream for a GML document
	 * 
	 * @param writer the XML stream writer
	 * @param geometry the geometry
	 * @param property the geometry property
	 * @param srsName the SRS name of a common SRS for the whole document, may
	 *            be <code>null</code>
	 * @param report the reporter
	 * @throws XMLStreamException if any error occurs writing the geometry
	 */
	public void write(XMLStreamWriter writer, Geometry geometry, PropertyDefinition property,
			String srsName, IOReporter report) throws XMLStreamException {
		// write eventual required id
		GmlWriterUtil.writeRequiredID(writer, property.getPropertyType(), null, false);

		// write any srsName attribute on the parent element
		writeSrsName(writer, property.getPropertyType(), geometry, srsName);

		// write any srsDimension attribute on the parent element
		writeSrsDimension(writer, property.getPropertyType(), geometry);

		if (simplifyGeometry) {
			// if geometry collection containing only one geometry,
			// reduce to internal geometry
			if (geometry instanceof GeometryCollection
					&& ((GeometryCollection) geometry).getNumGeometries() == 1) {
				geometry = geometry.getGeometryN(0);
			}
		}

		Class<? extends Geometry> geomType = geometry.getClass();

		// remember if we already found a solution to this problem
		List<DefinitionPath> preferredPaths = restoreCandidate(property.getPropertyType(),
				geomType);

		if (preferredPaths == null) {
			// find candidates
			List<DefinitionPath> candidates = findCandidates(property, geomType);

			// if no candidate found, try with compatible geometries
			Class<? extends Geometry> originalType = geomType;
			Geometry originalGeometry = geometry;
			ConversionLadder ladder = GeometryConverterRegistry.getInstance()
					.createLadder(geometry);
			while (candidates.isEmpty() && ladder.hasNext()) {
				geometry = ladder.next();
				geomType = geometry.getClass();

				log.info("Possible structure for writing " + originalType.getSimpleName() + //$NON-NLS-1$
						" not found, trying " + geomType.getSimpleName() + " instead"); //$NON-NLS-1$ //$NON-NLS-2$

				List<DefinitionPath> candPaths = restoreCandidate(property.getPropertyType(),
						geomType);
				if (candPaths != null) {
					// use stored candidate
					candidates = new ArrayList<>(candPaths);
				}
				else {
					candidates = findCandidates(property, geomType);
				}
			}

			if (candidates.isEmpty()) {
				// also try the generic geometry type
				geometry = originalGeometry;
				geomType = Geometry.class;

				log.info("Possible structure for writing " + originalType.getSimpleName() + //$NON-NLS-1$
						" not found, trying the generic geometry type instead"); //$NON-NLS-1$ //$NON-NLS-2$

				List<DefinitionPath> candPaths = restoreCandidate(property.getPropertyType(),
						geomType);
				if (candPaths != null) {
					// use stored candidate
					candidates = new ArrayList<>(candidates);
				}
				else {
					candidates = findCandidates(property, geomType);
				}

				// remember generic match for later
				storeCandidate(property.getPropertyType(), originalType,
						sortPreferredCandidates(candidates, geomType));
			}

			for (DefinitionPath candidate : candidates) {
				log.info("Geometry structure match: " + geomType.getSimpleName() + " - " //$NON-NLS-1$ //$NON-NLS-2$
						+ candidate);
			}

			if (candidates.isEmpty()) {
				log.error("No geometry structure match for " + //$NON-NLS-1$
						originalType.getSimpleName() + " found, writing WKT " + //$NON-NLS-1$
						"representation instead"); //$NON-NLS-1$

				writer.writeCharacters(originalGeometry.toText());
				return;
			}

			// determine preferred candidate
			preferredPaths = sortPreferredCandidates(candidates, geomType);

			// remember for later
			storeCandidate(property.getPropertyType(), geomType, preferredPaths);
		}

		DefinitionPath path = selectValidPath(preferredPaths, geometry);
		if (path != null) {
			// write geometry
			writeGeometry(writer, geometry, path, srsName);
		}
		else {
			report.error(new IOMessageImpl(
					"No valid path found for encoding geometry, geometry is skipped.", null));
		}
	}

	/**
	 * Select the first valid path that is applicable for the given geometry.
	 * 
	 * @param preferredPaths the path candidates in order
	 * @param geometry the geometry to write
	 * @return the selected path
	 */
	@Nullable
	private DefinitionPath selectValidPath(List<DefinitionPath> preferredPaths, Geometry geometry) {
		for (DefinitionPath candidate : preferredPaths) {
			GeometryWriter<?> writer = candidate.getGeometryWriter();
			if (writer.accepts(geometry)) {
				return candidate;
			}
		}

		return null;
	}

	/**
	 * Sort the given candidates by preference.
	 * 
	 * @param candidates the path candidates
	 * @param geomType the geometry type
	 * @return the sorted list of candidates
	 */
	private List<DefinitionPath> sortPreferredCandidates(List<DefinitionPath> candidates,
			Class<? extends Geometry> geomType) {
		List<DefinitionPath> preferred = new ArrayList<>(candidates);

		// sort candidates by preference
		Collections.sort(preferred, new Comparator<DefinitionPath>() {

			@Override
			public int compare(DefinitionPath o1, DefinitionPath o2) {
				int p1 = priority(o1);
				int p2 = priority(o2);
				if (p1 > p2)
					return -1;
				else if (p2 < p1)
					return 1;
				return 0;
			}

			public int priority(DefinitionPath path) {
				if (path != null && path.getGeometryCompatibleType() != null) {
					/*
					 * Policies:
					 * 
					 * General - prefer simple types over composite types
					 * 
					 * MultiPolygons - prefer MultiSurface over
					 * CompositeSurface, because it is more flexible
					 * 
					 * MultiLineString - rely on validity check
					 */
					switch (path.getGeometryCompatibleType().getName().getLocalPart()) {
					case "LineStringType":
						return 50;
					case "CurveType":
						return 40;
					case "CompositeCurveType":
						return 30;
					case "MultiCurveType":
					case "MultiSurfaceType":
						return 10;
					case "MultiPolygonType":
						return 5;
					case "CompositeSurfaceType":
						return -10;
					}
				}

				return 0;
			}
		});

		return preferred;
	}

	/**
	 * Find candidates for a possible path to use for writing the geometry
	 * 
	 * @param property the start property
	 * @param geomType the geometry type
	 * 
	 * @return the path candidates
	 */
	public List<DefinitionPath> findCandidates(PropertyDefinition property,
			Class<? extends Geometry> geomType) {
		Set<GeometryWriter<?>> writers = findWriters(geomType);
		if (writers == null || writers.isEmpty()) {
			// if no writer is present, we can cancel right here
			return new ArrayList<DefinitionPath>();
		}

		long max = property.getConstraint(Cardinality.class).getMaxOccurs();
		return super.findCandidates(property.getPropertyType(), property.getName(),
				max != Cardinality.UNBOUNDED && max <= 1, geomType);
	}

	/**
	 * Write the geometry using the given path
	 * 
	 * @param writer the XML stream writer
	 * @param geometry the geometry
	 * @param path the definition path to use
	 * @param srsName the SRS name of a common SRS for the whole document, may
	 *            be <code>null</code>
	 * @throws XMLStreamException if writing the geometry fails
	 */
	@SuppressWarnings("unchecked")
	private void writeGeometry(XMLStreamWriter writer, Geometry geometry, DefinitionPath path,
			String srsName) throws XMLStreamException {
		@SuppressWarnings("rawtypes")
		GeometryWriter geomWriter = path.getGeometryWriter();

		QName name = path.getLastName();

		if (path.isEmpty()) {
			// directly write geometry
			geomWriter.write(writer, geometry, path.getLastType(), name, gmlNs);
		}
		else {
			for (PathElement step : path.getSteps()) {
				if (!step.isTransient()) {
					// start elements
					name = step.getName();
					GmlWriterUtil.writeStartPathElement(writer, step, false);
					// write eventual required ID
					GmlWriterUtil.writeRequiredID(writer, step.getType(), null, false);
					// write eventual srsName
					writeSrsName(writer, step.getType(), geometry, srsName);
					// write eventual srsDimension
					writeSrsDimension(writer, step.getType(), geometry);
				}
			}

			// write geometry
			geomWriter.write(writer, geometry, path.getLastType(), name, gmlNs);

			for (int i = 0; i < path.getSteps().size(); i++) {
				PathElement step = path.getSteps().get(path.getSteps().size() - 1 - i);

				if (!step.isTransient()) {
					// end elements
					writer.writeEndElement();
				}
			}
		}
	}

	/**
	 * Write the SRS name if a corresponding attribute is present
	 * 
	 * @param writer the XML stream writer
	 * @param type the element type definition
	 * @param geometry the geometry
	 * @param srsName the common SRS name, may be <code>null</code>
	 * @throws XMLStreamException if writing the SRS name fails
	 */
	private void writeSrsName(XMLStreamWriter writer, TypeDefinition type, Geometry geometry,
			String srsName) throws XMLStreamException {
		// TODO can SRS be extracted from geometry?

		if (srsName != null) {
			PropertyDefinition srsAtt = null;
			for (ChildDefinition<?> att : DefinitionUtil.getAllProperties(type)) {
				// XXX is this enough? or should groups be handled explicitly?
				if (att.asProperty() != null
						// if we write an attribute, it must be an attribute ;)
						&& att.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()
						&& att.getName().getLocalPart().equals("srsName") // TODO //$NON-NLS-1$
																			// improve
																			// condition?
						&& (att.getName().getNamespaceURI() == null
								|| att.getName().getNamespaceURI().equals(gmlNs)
								|| att.getName().getNamespaceURI().isEmpty())) {
					srsAtt = att.asProperty();
					break;
				}
			}

			if (srsAtt != null) {
				GmlWriterUtil.writeAttribute(writer, srsName, srsAtt);
			}
		}
	}

	/**
	 * Write the SRS dimension if a corresponding attribute is present
	 * 
	 * @param writer the XML stream writer
	 * @param type the element type definition
	 * @param geometry the geometry
	 * @throws XMLStreamException if writing the SRS dimension fails
	 */
	private void writeSrsDimension(XMLStreamWriter writer, TypeDefinition type, Geometry geometry)
			throws XMLStreamException {
		// detect geometry dimension if possible
		Integer dimension = detectDimension(geometry);

		if (dimension != null) {
			PropertyDefinition dimensionAtt = null;
			for (ChildDefinition<?> att : DefinitionUtil.getAllProperties(type)) {
				// XXX is this enough? or should groups be handled explicitly?
				if (att.asProperty() != null
						// if we write an attribute, it must be an attribute ;)
						&& att.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()
						&& att.getName().getLocalPart().equals("srsDimension") //$NON-NLS-1$
						&& (att.getName().getNamespaceURI() == null
								|| att.getName().getNamespaceURI().equals(gmlNs)
								|| att.getName().getNamespaceURI().isEmpty())) {
					dimensionAtt = att.asProperty();
					break;
				}
			}

			if (dimensionAtt != null) {
				GmlWriterUtil.writeAttribute(writer, dimension, dimensionAtt);
			}
		}
	}

	@Nullable
	private Integer detectDimension(Geometry geometry) {
		// test the coordinate dimension
		Coordinate coord = geometry.getCoordinate();
		if (coord != null) {
			int dimension = 2;
			if (!Double.isNaN(coord.z)) {
				dimension = 3;
			}
			return dimension;
		}

		// not able to detect
		return null;
	}

	/**
	 * Store the candidate for later use
	 * 
	 * @param type the attribute type definition
	 * @param geomType the geometry type
	 * @param path the definition path
	 */
	private void storeCandidate(TypeDefinition type, Class<? extends Geometry> geomType,
			List<DefinitionPath> path) {
		Map<Class<? extends Geometry>, List<DefinitionPath>> paths = storedPaths.get(type);
		if (paths == null) {
			paths = new HashMap<>();
			storedPaths.put(type, paths);
		}
		paths.put(geomType, path);
	}

	/**
	 * Restore the candidate matching the given types
	 * 
	 * @param type the attribute type definition
	 * @param geomType the geometry type
	 * 
	 * @return a previously found path or <code>null</code>
	 */
	private List<DefinitionPath> restoreCandidate(TypeDefinition type,
			Class<? extends Geometry> geomType) {
		Map<Class<? extends Geometry>, List<DefinitionPath>> paths = storedPaths.get(type);
		if (paths != null) {
			return paths.get(geomType);
		}
		return null;
	}

	/**
	 * Determines if a type definition is compatible to a geometry type
	 * 
	 * @param type the type definition
	 * @param geomType the geometry type
	 * @param path the current definition path
	 * 
	 * @return the (eventually updated) definition path if a match is found,
	 *         otherwise <code>null</code>
	 */
	@Override
	protected DefinitionPath matchPath(TypeDefinition type, Class<? extends Geometry> geomType,
			DefinitionPath path) {

		// check compatibility list
		Set<GeometryWriter<?>> writers = findWriters(geomType);

		if (writers != null) {
			for (GeometryWriter<?> writer : writers) {
				boolean compatible = false;
				Set<QName> names = writer.getCompatibleTypes();
				if (names != null) {
					if (names.contains(type.getName())) {
						// check type name
						compatible = true;
					}

					if (!compatible && type.getName().getNamespaceURI().equals(gmlNs)) {
						// check GML type name
						compatible = names.contains(new QName(Pattern.GML_NAMESPACE_PLACEHOLDER,
								type.getName().getLocalPart()));
						// the GML_NAMESPACE_PLACEHOLDER namespace references
						// the GML namespace
					}

					if (compatible) {
						// check structure / match writer
						DefinitionPath candidate = writer.match(type, path, gmlNs);
						if (candidate != null) {
							// set appropriate writer for path and return it
							candidate.setGeometryWriter(writer, type);
							return candidate;
						}
					}
				}
			}
		}

		// fall back to binding test
		// check for equality because we don't want a match for the property
		// types
		Class<? extends Geometry> geomBinding = type.getConstraint(GeometryType.class).getBinding();
		boolean compatible = geomType.equals(geomBinding);

		if (compatible) {
			// check structure / match writers
			if (writers != null) {
				for (GeometryWriter<?> writer : writers) {
					DefinitionPath candidate = writer.match(type, path, gmlNs);
					if (candidate != null) {
						// set appropriate writer for path and return it
						candidate.setGeometryWriter(writer, type);
						return candidate;
					}
				}
			}
		}

		return null;
	}

	private Set<GeometryWriter<?>> findWriters(Class<? extends Geometry> geomType) {
		Set<GeometryWriter<?>> writers = geometryWriters.get(geomType);
		if (writers == null) {
			writers = new HashSet<>();
		}
		else {
			writers = new HashSet<>(writers);
		}

		// also handle super class writers, e.g. LineString for LinearRing
		Class<?> geomAlt = geomType;
		while (geomAlt.getSuperclass() != null && !Geometry.class.equals(geomAlt.getSuperclass())
				&& Geometry.class.isAssignableFrom(geomAlt.getSuperclass())) {
			geomAlt = geomAlt.getSuperclass();
			Set<GeometryWriter<?>> moreWriters = geometryWriters.get(geomAlt);
			if (moreWriters != null) {
				writers.addAll(moreWriters);
			}
		}

		return writers;
	}

}
