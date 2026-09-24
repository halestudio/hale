/*
 * Copyright (c) 2020 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.gml.writer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.operation.MathTransform;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.graph.reference.ReferenceGraph;
import eu.esdihumboldt.hale.common.instance.graph.reference.impl.XMLInspector;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.MultiInstanceCollection;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.io.gml.writer.internal.DefaultMultipartHandler;
import eu.esdihumboldt.hale.io.gml.writer.internal.MultipartHandler;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.format.DecimalFormatUtil;

/**
 * Writes instances to a XPlanGML XPlanAuszug
 * 
 * @author Florian Esser
 */
public class XPlanGmlInstanceWriter extends StreamGmlWriter {

	/**
	 * The identifier of the writer as registered to the I/O provider extension.
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.gml.xplan.writer";

	/**
	 * The base part of all XPlanGML namespace URIs
	 */
	public static final String XPLAN_NS_BASE = "http://www.xplanung.de/xplangml/";

	/**
	 * Name of the parameter to create separate files for each feature type
	 */
	public static final String PARAM_PARTITION_BY_PLAN = "partition.byPlan";

	/**
	 * Default constructor
	 */
	public XPlanGmlInstanceWriter() {
		super(true);
	}

	/**
	 * @see StreamGmlWriter#requiresDefaultContainer()
	 */
	@Override
	protected boolean requiresDefaultContainer() {
		return true; // requires an XPlanAuszug element being present
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter#isFeatureCollection(eu.esdihumboldt.hale.io.xsd.model.XmlElement)
	 */
	@Override
	protected boolean isFeatureCollection(XmlElement el) {
		return el.getName().getLocalPart().contains("XPlanAuszug")
				&& !el.getType().getConstraint(AbstractFlag.class).isEnabled()
				&& hasChild(el.getType(), "featureMember"); //$NON-NLS-1$
	}

	private boolean isPartitionByPlanConfigured() {
		return getParameter(PARAM_PARTITION_BY_PLAN).as(Boolean.class, false);
	}

	/**
	 * Writes a {@code gml:boundedBy} with a {@code gml:Envelope} for the
	 * XPlanAuszug container. This defines the mandatory default CRS of the XPlanung
	 * model through the envelope's {@code srsName} (XPlanung conformance rule
	 * 2.1.3.1).
	 *
	 * @see StreamGmlWriter#writeAdditionalElements(XMLStreamWriter,
	 *      InstanceCollection, TypeDefinition, IOReporter)
	 */
	@Override
	protected void writeAdditionalElements(XMLStreamWriter writer, InstanceCollection instances,
			TypeDefinition containerDefinition, IOReporter reporter) throws XMLStreamException {
		// The gml:boundedBy must precede the feature members in the container, so
		// the extent is computed in a separate pass over the instances here. This
		// intentionally reads (and reprojects) the geometries a second time rather
		// than buffering all of them in memory, which keeps the streaming writer's
		// memory profile intact for large plans.
		boolean written = supportsBoundedByEnvelope(containerDefinition)
				&& writeBoundedBy(writer, instances, reporter);

		if (!written) {
			// Fall back to the default handling (e.g. GML 2 boundedBy) if the
			// container's schema doesn't declare gml:boundedBy, the target GML
			// namespace doesn't support the Envelope content model written by
			// writeBoundedBy, or no extent could be determined from the instances.
			super.writeAdditionalElements(writer, instances, containerDefinition, reporter);
		}
	}

	/**
	 * @param containerDefinition the container type definition
	 * @return <code>true</code> if the container's schema declares a
	 *         {@code gml:boundedBy} child and the target GML namespace is GML 3.2
	 *         ({@link #NS_GML_32}). GML 2 and GML 3.0/3.1 all share the
	 *         {@link #NS_GML} namespace returned by {@link #getGmlNs()} for those
	 *         versions, but unlike GML 3.2 their {@code gml:Envelope} does not
	 *         support {@code lowerCorner}/{@code upperCorner}, so
	 *         {@link #writeBoundedBy(XMLStreamWriter, InstanceCollection, IOReporter)}
	 *         must not be used for them
	 */
	private boolean supportsBoundedByEnvelope(TypeDefinition containerDefinition) {
		if (!NS_GML_32.equals(getGmlNs())) {
			return false;
		}

		// containerDefinition.getChild(QName) requires an exact (namespace
		// qualified) match, but boundedBy may be declared/inherited in either the
		// GML 3.2 or GML 2/3.0/3.1 namespace depending on which schema the
		// container type derives from, so match on the local name instead; this
		// includes children inherited from a super type (e.g. gml:AbstractFeatureType),
		// see TypeDefinition#getChildren()
		for (ChildDefinition<?> child : containerDefinition.getChildren()) {
			if ("boundedBy".equals(child.getName().getLocalPart()) //$NON-NLS-1$
					&& child.asProperty() != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine the overall extent of the given instances and write it as a
	 * {@code gml:boundedBy} containing a {@code gml:Envelope}. The envelope's
	 * {@code srsName} provides the default CRS required for a valid XPlanung model.
	 *
	 * @param writer the XML stream writer
	 * @param instances the instances written to the container
	 * @param reporter the reporter
	 * @return <code>true</code> if a bounding envelope was written,
	 *         <code>false</code> if no extent could be determined (e.g. because no
	 *         geometries were present)
	 * @throws XMLStreamException if writing the elements fails
	 */
	private boolean writeBoundedBy(XMLStreamWriter writer, InstanceCollection instances,
			IOReporter reporter) throws XMLStreamException {
		final Envelope envelope = new Envelope();
		CRSDefinition crsDef = null;
		String crsCode = null;
		boolean mixedCrs = false;
		boolean mixedCrsGeometrySkipped = false;
		boolean conversionFailed = false;

		// If a target CRS is configured, every geometry below is expected to end
		// up in that CRS; used to detect a silently failed conversion (see below).
		final CRSDefinition targetCrs = getTargetCRS();
		final boolean hasUsableTargetCrs = targetCrs != null && targetCrs.getCRS() != null;
		final String targetCrsCode = extractCode(targetCrs);

		final InstanceTraverser traverser = new DepthFirstInstanceTraverser();
		try (ResourceIterator<Instance> it = instances.iterator()) {
			while (it.hasNext()) {
				Instance inst = it.next();

				GeometryFinder finder = new GeometryFinder(getTargetCRS());
				traverser.traverse(inst, finder);

				for (GeometryProperty<?> geomProperty : finder.getGeometries()) {
					// Extract the geometry using the same conversion (e.g. to the
					// target CRS) that is applied when the geometry is written, so
					// that the envelope and its srsName are consistent with the
					// feature geometries. The reporter is intentionally not passed
					// on here to avoid duplicating the per-feature error that gets
					// reported below when the feature geometry itself is written;
					// instead, a failed conversion is detected explicitly below,
					// since AbstractGeoInstanceWriter#convertGeometry otherwise
					// falls back to returning the untransformed geometry silently.
					Pair<Geometry, CRSDefinition> pair = extractGeometry(geomProperty, true, null);
					if (pair == null) {
						continue;
					}

					Geometry geom = pair.getFirst();
					if (geom == null || geom.isEmpty()) {
						continue;
					}

					CRSDefinition geomCrs = pair.getSecond();

					if (hasUsableTargetCrs) {
						String returnedCode = extractCode(geomCrs);
						boolean convertedToTarget = targetCrsCode == null ? returnedCode == null
								: targetCrsCode.equals(returnedCode);
						if (!convertedToTarget) {
							// The conversion to the target CRS silently failed for
							// this geometry; its coordinates are not comparable to
							// the other, correctly reprojected geometries, so it
							// cannot be used to compute the envelope.
							conversionFailed = true;
							continue;
						}
					}

					if (crsDef == null) {
						// first geometry encountered defines the envelope's CRS
						crsDef = geomCrs;
						crsCode = extractCode(geomCrs);
						envelope.expandToInclude(geom.getEnvelopeInternal());
						continue;
					}

					String otherCode = extractCode(geomCrs);
					boolean sameCrs = crsCode == null ? otherCode == null
							: crsCode.equals(otherCode);
					if (sameCrs) {
						envelope.expandToInclude(geom.getEnvelopeInternal());
						continue;
					}

					// Geometry in a different CRS than the envelope's reference
					// CRS. This only happens when no target CRS is configured to
					// unify them; with a target CRS configured, geometries either
					// end up in that CRS above or are excluded as a conversion
					// failure. Reproject it to the envelope's reference CRS so its
					// extent stays numerically correct; if that is not possible,
					// exclude the geometry from the envelope rather than mixing
					// incompatible coordinates.
					mixedCrs = true;
					Geometry reprojected = reprojectGeometry(geom, geomCrs, crsDef, reporter);
					if (reprojected != null) {
						envelope.expandToInclude(reprojected.getEnvelopeInternal());
					}
					else {
						mixedCrsGeometrySkipped = true;
					}
				}
			}
		}

		if (conversionFailed) {
			// A geometry could not be reprojected to the configured target CRS,
			// so the envelope cannot be reliably computed: an incomplete or
			// wrong extent would be worse than none. The per-feature error for
			// the affected geometry is reported separately when it is written.
			reporter.error(
					"Could not determine the XPlanAuszug bounding envelope: at least one geometry could not be reprojected to the configured target CRS, so its extent cannot be reliably included. The mandatory default CRS (srsName) is omitted rather than risk an incorrect extent."); //$NON-NLS-1$
			return false;
		}

		if (envelope.isNull()) {
			return false;
		}

		final String gmlNs = getGmlNs();
		final String srsName = extractCode(crsDef);
		final DecimalFormat formatter = getCoordinateFormatter();

		if (srsName == null) {
			reporter.warn(
					"No CRS could be determined for the XPlanAuszug bounding envelope; the mandatory default CRS (srsName) is missing and the output will not satisfy XPlanung conformance rule 2.1.3.1. Configure a target CRS or ensure the source geometries carry a CRS."); //$NON-NLS-1$
		}
		else if (mixedCrs) {
			reporter.warn(String.format(
					"The exported geometries use more than one CRS but no target CRS is configured to unify them. Geometries in a CRS other than the envelope's reference CRS (%s) were reprojected for the XPlanAuszug bounding envelope%s. Configure a target CRS to obtain consistent output.", //$NON-NLS-1$
					srsName,
					mixedCrsGeometrySkipped
							? "; some of them could not be reprojected and were excluded from the extent" //$NON-NLS-1$
							: ""));
		}

		writer.writeStartElement(gmlNs, "boundedBy"); //$NON-NLS-1$
		writer.writeStartElement(gmlNs, "Envelope"); //$NON-NLS-1$
		if (srsName != null) {
			writer.writeAttribute("srsName", srsName); //$NON-NLS-1$
		}
		// The envelope is computed from a 2D JTS Envelope, so it always has two
		// coordinate values per corner, regardless of whether the CRS itself is
		// 3D; without srsDimension a 3D srsName would imply a dimension mismatch
		// with the two-value lowerCorner/upperCorner written below.
		writer.writeAttribute("srsDimension", "2"); //$NON-NLS-1$ //$NON-NLS-2$

		writer.writeStartElement(gmlNs, "lowerCorner"); //$NON-NLS-1$
		writer.writeCharacters(DecimalFormatUtil.applyFormatter(envelope.getMinX(), formatter) + " " //$NON-NLS-1$
				+ DecimalFormatUtil.applyFormatter(envelope.getMinY(), formatter));
		writer.writeEndElement();

		writer.writeStartElement(gmlNs, "upperCorner"); //$NON-NLS-1$
		writer.writeCharacters(DecimalFormatUtil.applyFormatter(envelope.getMaxX(), formatter) + " " //$NON-NLS-1$
				+ DecimalFormatUtil.applyFormatter(envelope.getMaxY(), formatter));
		writer.writeEndElement();

		writer.writeEndElement(); // Envelope
		writer.writeEndElement(); // boundedBy

		return true;
	}

	/**
	 * Reproject a geometry from its source CRS to the given target CRS. Used to
	 * unify geometries in different CRS for the bounding envelope when no target
	 * CRS is configured for the writer (in which case
	 * {@link #extractGeometry(Object, boolean, IOReporter)} does not already
	 * reproject them).
	 *
	 * @param geom the geometry to reproject
	 * @param sourceCrs the CRS of the geometry
	 * @param targetCrs the CRS to reproject to
	 * @param reporter the reporter
	 * @return the reprojected geometry, or <code>null</code> if it could not be
	 *         reprojected (e.g. because a CRS is not resolvable or no transform
	 *         could be found)
	 */
	private Geometry reprojectGeometry(Geometry geom, CRSDefinition sourceCrs,
			CRSDefinition targetCrs, IOReporter reporter) {
		if (sourceCrs == null || sourceCrs.getCRS() == null || targetCrs == null
				|| targetCrs.getCRS() == null) {
			return null;
		}
		try {
			MathTransform transform = CRS.findMathTransform(sourceCrs.getCRS(), targetCrs.getCRS());
			return JTS.transform(geom, transform);
		} catch (Exception e) {
			reporter.warn(
					"Could not reproject a geometry to the XPlanAuszug bounding envelope's CRS; it was excluded from the extent computation", //$NON-NLS-1$
					e);
			return null;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		init();

		if (isPartitionByPlanConfigured()) {
			partitionByPlan(progress, reporter);
		}
		else {
			return super.execute(progress, reporter);
		}

		return reporter;
	}

	private void partitionByPlan(ProgressIndicator progress, IOReporter reporter)
			throws IOException {

		final Set<TypeDefinition> planTypes = collectPlanTypes(getTargetSchema().getTypes());

		/*
		 * Split instances into plan and non-plan instances. Associate the ID of
		 * a plan with its plan type and the plan instance.
		 */
		final XMLInspector gadget = new XMLInspector();
		final DefaultInstanceCollection nonPlanInstances = new DefaultInstanceCollection();
		final Map<String, TypeDefinition> planIdToPlanTypeMapping = new HashMap<>();
		final Map<String, InstanceCollection> planIdToInstancesMapping = new HashMap<>();
		try (ResourceIterator<Instance> it = getInstances().iterator()) {
			while (it.hasNext()) {
				Instance inst = it.next();
				if (!planTypes.contains(inst.getDefinition())) {
					nonPlanInstances.add(inst);
					continue;
				}

				String planId = gadget.getIdentity(inst);
				planIdToInstancesMapping.put(planId,
						new DefaultInstanceCollection(Arrays.asList(inst)));
				planIdToPlanTypeMapping.put(planId, inst.getDefinition());
			}
		}

		/*
		 * Collect referenced instances for every plan instance
		 */
		for (String planId : planIdToInstancesMapping.keySet()) {
			MultiInstanceCollection mic = new MultiInstanceCollection(
					Arrays.asList(planIdToInstancesMapping.get(planId), nonPlanInstances));
			ReferenceGraph<String> rg = new ReferenceGraph<String>(new XMLInspector(), mic, planId);

			Iterator<InstanceCollection> p = rg.partition(1, reporter);
			while (p.hasNext()) {
				boolean found = false;
				InstanceCollection c = p.next();
				Iterator<Instance> it = c.iterator();
				while (it.hasNext()) {
					Instance i = it.next();
					if (planId.equals(gadget.getIdentity(i))) {
						planIdToInstancesMapping.put(planId, c);
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
		}

		final MultipartHandler handler = new MultipartHandler() {

			@Override
			public String getTargetFilename(InstanceCollection part, URI originalTarget) {
				Path origPath = Paths.get(originalTarget).normalize();
				Pair<String, String> nameAndExt = DefaultMultipartHandler
						.getFileNameAndExtension(origPath.toString());

				String planId = null;
				for (Entry<String, InstanceCollection> mapping : planIdToInstancesMapping
						.entrySet()) {
					if (part == mapping.getValue()) {
						planId = mapping.getKey();
						break;
					}
				}

				if (planId == null) {
					throw new RuntimeException("Plan was not seen before");
				}

				// Replace all characters that are not allowed in XML IDs with
				// an underscore. In addition, the colon (:) is also replaced
				// to make sure that the resulting String can be used safely in
				// a file name.
				String sanitizedPlanId = planId.replaceAll("[^A-Za-z0-9-_.]", "_");
				return String.format("%s%s%s.%s.%s.%s", origPath.getParent().toString(),
						File.separator, nameAndExt.getFirst(),
						planIdToPlanTypeMapping.get(planId).getDisplayName(), sanitizedPlanId,
						nameAndExt.getSecond());
			}
		};

		try {
			writeParts(planIdToInstancesMapping.values().iterator(), handler, progress, reporter);
		} catch (XMLStreamException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	private Set<TypeDefinition> collectPlanTypes(Collection<? extends TypeDefinition> types) {
		final Set<TypeDefinition> planTypes = new HashSet<>();

		for (TypeDefinition type : types) {
			QName typeName = type.getName();
			if (typeName.getNamespaceURI().toString().startsWith(XPLAN_NS_BASE)
					&& typeName.getLocalPart().endsWith("_PlanType")) {
				planTypes.add(type);
			}
		}
		return planTypes;
	}
}
