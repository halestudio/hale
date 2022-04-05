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

package eu.esdihumboldt.hale.io.gml.writer.internal;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotools.geometry.jts.JTS;
import org.geotools.gml3.GML;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.SubtaskProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.MultiLocationOutputSupplier;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.graph.reference.ReferenceGraphPartitioner;
import eu.esdihumboldt.hale.common.instance.graph.reference.impl.XMLInspector;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractGeoInstanceWriter;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;
import eu.esdihumboldt.hale.common.instance.io.util.EnumWindingOrderTypes;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Identifiable;
import eu.esdihumboldt.hale.common.instance.model.IdentifiableInstanceReference;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.impl.PerTypeInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.instance.tools.InstanceCollectionPartitioner;
import eu.esdihumboldt.hale.common.instance.tools.impl.NoPartitioner;
import eu.esdihumboldt.hale.common.instance.tools.impl.SimplePartitioner;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.io.gml.geometry.GMLConstants;
import eu.esdihumboldt.hale.io.gml.internal.simpletype.SimpleTypeUtil;
import eu.esdihumboldt.hale.io.gml.writer.XmlWrapper;
import eu.esdihumboldt.hale.io.gml.writer.XmlWriterBase;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.AbstractTypeMatcher;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.Descent;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.StreamGeometryWriter;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.format.DecimalFormatUtil;
import eu.esdihumboldt.util.geometry.quadtree.FixedBoundaryQuadtree;
import eu.esdihumboldt.util.geometry.quadtree.QuadtreeBuilder;
import eu.esdihumboldt.util.geometry.quadtree.QuadtreeNodeVisitor;

/**
 * Writes GML/XML using a {@link XMLStreamWriter}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class StreamGmlWriter extends AbstractGeoInstanceWriter
		implements XmlWriterBase, GMLConstants {

	/**
	 * Schema instance namespace (for specifying schema locations)
	 */
	public static final String SCHEMA_INSTANCE_NS = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI; // $NON-NLS-1$

	private static final ALogger log = ALoggerFactory.getLogger(StreamGmlWriter.class);

	/**
	 * The parameter name for the flag specifying if a geometry should be
	 * simplified before writing it, if possible. Defaults to true.
	 */
	public static final String PARAM_SIMPLIFY_GEOMETRY = "gml.geometry.simplify";

	/**
	 * The parameter name for the flag specifying if the output should be
	 * indented. Defaults to <code>false</code>.
	 */
	public static final String PARAM_PRETTY_PRINT = "xml.pretty";

	/**
	 * The parameter name for the flag specifying an identifier (XML ID) for the
	 * container.
	 */
	public static final String PARAM_CONTAINER_ID = "xml.containerId";

	/**
	 * The parameter name of the flag specifying if nilReason attributes should
	 * be omitted if an element is not nil.
	 */
	public static final String PARAM_OMIT_NIL_REASON = "xml.notNil.omitNilReason";

	/**
	 * The parameter name of the flag specifying if codespace should be
	 * automatically added to the gml:identifier during GML export.
	 */
	public static final String PARAM_ADD_CODESPACE = "xml.add.codespace";

	/**
	 * The name of the parameter specifying how the output of geometry
	 * coordinates should be formatted.
	 */
	public static final String PARAM_GEOMETRY_FORMAT = "geometry.write.decimalFormat";

	/**
	 * The name of the parameter specifying how the output of Double values
	 * should be formatted.
	 */
	public static final String PARAM_DECIMAL_FORMAT = "xml.decimalFormat";

	/**
	 * Name of the parameter defining the instance threshold.
	 */
	public static final String PARAM_INSTANCES_THRESHOLD = "instancesPerFile";

	/**
	 * Name of the parameter to create separate files for each feature type
	 */
	public static final String PARAM_PARTITION_BY_FEATURE_TYPE = "partition.byFeatureType";

	/**
	 * Name of the parameter to separate instances by extent
	 */
	public static final String PARAM_PARTITION_BY_EXTENT = "partition.byExtent";

	/**
	 * Name of the parameter to specify how much instances a tile can hold at
	 * most before it is split up
	 */
	public static final String PARAM_PARTITION_BY_EXTENT_MAX_NODES = "partition.byExtent.maxInstancesPerTile";

	/**
	 * Name of the parameter stating the mode to use for extent partitioning
	 */
	public static final String PARAM_PARTITION_BY_EXTENT_MODE = "partition.byExtent.mode";

	/**
	 * Value for extent partitioning mode to use the bounding box of the dataset
	 * as the quadtree boundary
	 */
	public static final String PARTITION_BY_EXTENT_MODE_DATASET = "dataset";

	/**
	 * Value for extent partitioning mode to use the world (WGS 84) as the
	 * quadtree boundary
	 */
	public static final String PARTITION_BY_EXTENT_MODE_WORLD = "world";

	/**
	 * Name of the parameter stating the mode to use for instance partitioning.
	 */
	public static final String PARAM_PARTITION_MODE = "partition.mode";

	/**
	 * Value for partitioning mode parameter that just creates a single part.
	 */
	public static final String PARTITION_MODE_NONE = "none";

	/**
	 * Value for partitioning mode parameter that always cuts at the threshold.
	 */
	public static final String PARTITION_MODE_CUT = "cut";

	/**
	 * Value for partitioning mode parameter that keeps related instances
	 * together.
	 */
	public static final String PARTITION_MODE_RELATED = "related";

	/**
	 * Value for threshold parameter to deactivate partitioning.
	 */
	public static final int NO_PARTITIONING = 0;

	/**
	 * The XML stream writer
	 */
	private PrefixAwareStreamWriter writer;

	/**
	 * The GML namespace
	 */
	private String gmlNs;

//	/**
//	 * The type index
//	 */
//	private TypeIndex types;

	/**
	 * The geometry writer
	 */
	private StreamGeometryWriter geometryWriter;

	/**
	 * Additional schemas included in the document
	 */
	private final Map<String, Locatable> additionalSchemas = new HashMap<>();
	private final Map<String, String> additionalSchemaPrefixes = new HashMap<>();

	/**
	 * States if a feature collection shall be used
	 */
	private final boolean useFeatureCollection;

	private XmlIndex targetIndex;

	private XmlWrapper documentWrapper;

	/**
	 * Create a GML writer
	 * 
	 * @param useFeatureCollection if a GML feature collection shall be used to
	 *            store the instances (if possible)
	 */
	public StreamGmlWriter(boolean useFeatureCollection) {
		super();
		this.useFeatureCollection = useFeatureCollection;

		addSupportedParameter(PARAM_ROOT_ELEMENT_NAMESPACE);
		addSupportedParameter(PARAM_ROOT_ELEMENT_NAME);
		addSupportedParameter(PARAM_SIMPLIFY_GEOMETRY);
	}

	/**
	 * @return the document wrapper
	 */
	@Nullable
	public XmlWrapper getDocumentWrapper() {
		return documentWrapper;
	}

	/**
	 * @param documentWrapper the document wrapper to set
	 */
	public void setDocumentWrapper(@Nullable XmlWrapper documentWrapper) {
		this.documentWrapper = documentWrapper;
	}

	@Override
	public List<? extends Locatable> getValidationSchemas() {
		List<Locatable> result = new ArrayList<Locatable>();
		result.addAll(super.getValidationSchemas());

		for (Locatable schema : additionalSchemas.values()) {
			result.add(schema);
		}

		return result;
	}

	/**
	 * Add a schema that should be included for validation. Should be called
	 * before or in
	 * {@link #write(InstanceCollection, OutputStream, ProgressIndicator, IOReporter)}
	 * prior to writing the schema locations, but after {@link #init()}
	 * 
	 * @param namespace the schema namespace
	 * @param schema the schema location
	 * @param prefix the desired namespace prefix, may be <code>null</code>
	 */
	protected void addValidationSchema(String namespace, Locatable schema,
			@Nullable String prefix) {
		additionalSchemas.put(namespace, schema);
		if (prefix != null) {
			additionalSchemaPrefixes.put(namespace, prefix);
		}
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		init();

		if (isThresholdConfigured()) {
			partitionByThreshold(progress, reporter);
		}
		else if (isPartitionByFeatureTypeConfigured()) {
			partitionByFeatureType(progress, reporter);
		}
		else if (isPartitionByExtentConfigured()) {
			partitionByExtent(progress, reporter);
		}
		else {
			write(getInstances(), getTarget().getOutput(), progress, reporter);
		}

		return reporter;
	}

	private void partitionByExtent(ProgressIndicator progress, IOReporter reporter)
			throws IOException {
		int maxNodes = getParameter(PARAM_PARTITION_BY_EXTENT_MAX_NODES).as(Integer.class, 1000);
		String mode = getParameter(PARAM_PARTITION_BY_EXTENT_MODE).as(String.class,
				PARTITION_BY_EXTENT_MODE_DATASET);

		final SubtaskProgressIndicator qtProgress = new SubtaskProgressIndicator(progress) {

			@Override
			protected String getCombinedTaskName(String taskName, String subtaskName) {
				return taskName + " (" + subtaskName + ")";
			}

		};

		// Map for instances that either contain no or multiple geometries
		Map<String, InstanceReference> unhandledInstances = new HashMap<>();

		QuadtreeBuilder<Point, InstanceReference> builder = new QuadtreeBuilder<>();
		try (ResourceIterator<Instance> it = getInstances().iterator()) {
			qtProgress.begin("Collecting geometries", getInstances().size());

			final XMLInspector gadget = new XMLInspector();
			int i = 0;
			while (it.hasNext()) {
				Instance inst = it.next();
				InstanceReference instRef = getInstances().getReference(inst);

				InstanceTraverser traverser = new DepthFirstInstanceTraverser();
				GeometryFinder finder = new GeometryFinder(getTargetCRS());
				traverser.traverse(inst, finder);
				List<GeometryProperty<?>> geoms = finder.getGeometries();
				if (geoms.isEmpty() || geoms.size() > 1) {
					unhandledInstances.put(gadget.getIdentity(inst), instRef);
				}
				else {
					GeometryProperty<?> geomProperty = geoms.get(0);

					Geometry geom = geomProperty.getGeometry();

					Point centroid;
					switch (mode) {
					case PARTITION_BY_EXTENT_MODE_WORLD:
						CoordinateReferenceSystem sourceCrs = geomProperty.getCRSDefinition()
								.getCRS();
						CodeDefinition wgs84 = new CodeDefinition("EPSG:4326");
						try {
							MathTransform toWgs84 = CRS.findMathTransform(sourceCrs,
									wgs84.getCRS());
							Geometry geomWgs84 = JTS.transform(geom, toWgs84);
							centroid = geomWgs84.getCentroid();
						} catch (FactoryException | MismatchedDimensionException
								| TransformException e) {
							log.error("Unable to transform geometry to WGS 84", e);
							throw new IllegalStateException(e.getMessage(), e);
						}
						break;
					case PARTITION_BY_EXTENT_MODE_DATASET:
						// fall through to default
					default:
						centroid = geom.getCentroid();
					}

					builder.add(centroid,
							new IdentifiableInstanceReference(instRef, gadget.getIdentity(inst)));
				}

				qtProgress.advance(1);
				if (++i % 100 == 0) {
					qtProgress.setCurrentTask(MessageFormat.format("{0} instances processed", i));
				}
			}

			qtProgress.setCurrentTask("Building quadtree");

			FixedBoundaryQuadtree<InstanceReference> qt;
			switch (mode) {
			case PARTITION_BY_EXTENT_MODE_DATASET:
				qt = builder.build(maxNodes);
				break;
			case PARTITION_BY_EXTENT_MODE_WORLD:
				Envelope world = new Envelope(-180, 180, -90, 90);
				qt = builder.build(maxNodes, world);
				break;
			default:
				log.error(MessageFormat.format(
						"Unrecognized extent partitioning mode \"{0}\", using dataset boundaries",
						mode));
				qt = builder.build(maxNodes);
			}

			qtProgress.setCurrentTask("Performing spatial partitioning");

			final Map<String, String> idToKeyMapping = new HashMap<>();
			final Map<String, Collection<InstanceReference>> keyToRefsMapping = new HashMap<>();

			// Instances without geometry or with multiple geometries
			keyToRefsMapping.put(ExtentPartsHandler.KEY_NO_GEOMETRY, unhandledInstances.values());
			unhandledInstances.keySet().stream()
					.forEach(id -> idToKeyMapping.put(id, ExtentPartsHandler.KEY_NO_GEOMETRY));

			buildMappings(qt, idToKeyMapping, keyToRefsMapping);

			// Partition source instances based on quadtree tiles
			Iterator<InstanceCollection> collIt = new Iterator<InstanceCollection>() {

				private final Queue<String> keySet = new LinkedList<>(keyToRefsMapping.keySet());

				@Override
				public boolean hasNext() {
					return !keySet.isEmpty();
				}

				@Override
				public InstanceCollection next() {
					String key = keySet.poll();
					Collection<InstanceReference> refs = keyToRefsMapping.get(key);

					InstanceCollection instColl = new DefaultInstanceCollection(refs.stream()
							.map(ref -> getInstances().getInstance(
									IdentifiableInstanceReference.getRootReference(ref)))
							.collect(Collectors.toList()));
					return new ExtentPartsHandler.TreeKeyDecorator(instColl, key);
				}
			};

			final Map<String, URI> keyToTargetMapping = new HashMap<>();
			keyToRefsMapping.keySet().stream().forEach(k -> keyToTargetMapping.put(k,
					new File(ExtentPartsHandler.getTargetFilename(k, getTarget().getLocation()))
							.toURI()));

			final ExtentPartsHandler handler = new ExtentPartsHandler(keyToTargetMapping,
					idToKeyMapping);

			qtProgress.end();

			try {
				writeParts(collIt, handler, progress, reporter);
			} catch (XMLStreamException e) {
				throw new IOException(e.getMessage(), e);
			}

		}
	}

	private void buildMappings(FixedBoundaryQuadtree<InstanceReference> qt,
			final Map<String, String> idToKeyMapping,
			final Map<String, Collection<InstanceReference>> keyToRefsMapping) {
		QuadtreeNodeVisitor<InstanceReference> visitor = new QuadtreeNodeVisitor<InstanceReference>() {

			@Override
			public void visit(Geometry geometry, InstanceReference data, String treeKey) {
				if (!keyToRefsMapping.containsKey(treeKey)) {
					keyToRefsMapping.put(treeKey, new HashSet<InstanceReference>());
				}
				keyToRefsMapping.get(treeKey).add(data);
				if (data instanceof Identifiable) {
					Identifiable id = (Identifiable) data;
					idToKeyMapping.put(id.getId().toString(), treeKey);
				}
				else {
					throw new IllegalStateException("Instance reference has no ID");
				}
			}
		};

		qt.traverse(visitor);
	}

	private void partitionByFeatureType(ProgressIndicator progress, IOReporter reporter)
			throws IOException {
		// Threshold currently not supported if partitioning by feature type

		final Set<TypeDefinition> types = new HashSet<>();

		// Map GML IDs to features types and collect types
		final XMLInspector gadget = new XMLInspector();
		final Map<String, TypeDefinition> idToTypeMapping = new HashMap<>();
		try (ResourceIterator<Instance> it = getInstances().iterator()) {
			while (it.hasNext()) {
				Instance inst = it.next();
				types.add(inst.getDefinition());
				idToTypeMapping.put(gadget.getIdentity(inst), inst.getDefinition());
			}
		}

		final Map<TypeDefinition, URI> typeToTargetMapping = new HashMap<>();
		types.stream().forEach(t -> typeToTargetMapping.put(t, new File(
				PerTypePartsHandler.getTargetFilename(t.getName(), getTarget().getLocation()))
						.toURI()));
		final PerTypePartsHandler handler = new PerTypePartsHandler(typeToTargetMapping,
				idToTypeMapping);
		final PerTypeInstanceCollection instancesPerType = PerTypeInstanceCollection
				.fromInstances(getInstances(), types);

		try {
			writeParts(instancesPerType.collectionsIterator(), handler, progress, reporter);
		} catch (XMLStreamException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	private void partitionByThreshold(ProgressIndicator progress, IOReporter reporter)
			throws IOException {
		InstanceCollectionPartitioner partitioner = getPartitioner(this, reporter);
		int threshold = getParameter(PARAM_INSTANCES_THRESHOLD).as(Integer.class, NO_PARTITIONING);

		try (ResourceIterator<InstanceCollection> parts = partition(partitioner, getInstances(),
				threshold, progress, reporter)) {
			writeParts(parts, new DefaultMultipartHandler(), progress, reporter);
		} catch (XMLStreamException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * Get the instance collection partitioner based on the provider
	 * configuration of the partitioning mode.
	 * 
	 * @param provider the I/O provider
	 * @param log the log
	 * @return the partitioner to use
	 */
	public static InstanceCollectionPartitioner getPartitioner(IOProvider provider, SimpleLog log) {
		String mode = provider.getParameter(PARAM_PARTITION_MODE).as(String.class,
				PARTITION_MODE_RELATED);

		switch (mode) {
		case PARTITION_MODE_NONE:
			return new NoPartitioner();
		case PARTITION_MODE_CUT:
			return new SimplePartitioner();
		case PARTITION_MODE_RELATED:
			return new ReferenceGraphPartitioner(new XMLInspector());
		default:
			log.error("Unrecognized partition mode {0}, will create only one part", mode);
			return new NoPartitioner();
		}
	}

	/**
	 * Write the given {@link InstanceCollection}s to multiple files using the
	 * configured target as a base file name.<br>
	 * <br>
	 * Parts can only be written if the configured target is a URI to a local
	 * file.
	 * 
	 * @param instanceCollections the parts to write
	 * @param handler Handler that provides the parts' file names and an XML
	 *            writer
	 * @param progress Progress indicator
	 * @param reporter the reporter to use for the execution report
	 * @throws IOException if an I/O operation fails
	 * @throws XMLStreamException if an XML processing error occurs
	 * @see #setTarget(LocatableOutputSupplier)
	 */
	protected void writeParts(Iterator<InstanceCollection> instanceCollections,
			MultipartHandler handler, ProgressIndicator progress, IOReporter reporter)
			throws IOException, XMLStreamException {
		final URI location = getTarget().getLocation();

		if (location == null) {
			reporter.error("Cannot write multiple GML files: Output location unknown");
			return;
		}

		// Can only write multiple instance collection if target is a local file
		if (!"file".equals(location.getScheme())) {
			reporter.error("Cannot write multiple GML files: Target must be a local file");
			return;
		}

		Path origPath = Paths.get(location).normalize();
		if (origPath.toFile().isDirectory()) {
			reporter.error("Cannot write to a directory: Target must a file");
			return;
			// TODO Support writing to a directory; use parameter to specify
			// file name prefix.
		}

		List<URI> filesWritten = new ArrayList<>();
		while (instanceCollections.hasNext()) {
			InstanceCollection instances = instanceCollections.next();
			String targetFilename = handler.getTargetFilename(instances, location);

			File targetFile = new File(targetFilename);
			LocatableOutputSupplier<? extends OutputStream> out = new FileIOSupplier(targetFile);
			if (getTarget() instanceof GZipOutputSupplier) {
				out = new GZipOutputSupplier(out);
			}

			PrefixAwareStreamWriter writer = null;
			OutputStream os = out.getOutput();
			try {
				// The MultipartHandler can provide a specially decorated
				// writer, e.g. for reference rewriting
				writer = handler.getDecoratedWriter(createWriter(os, reporter), targetFile.toURI());
				write(instances, writer, progress, reporter);
			} finally {
				os.close();
				if (writer != null) {
					writer.close();
				}
			}

			filesWritten.add(targetFile.toURI());
		}

		if (filesWritten.size() > 1) {
			setTarget(new MultiLocationOutputSupplier(filesWritten));
		}
		else if (!filesWritten.isEmpty()) {
			setTarget(new LocatableOutputSupplier<OutputStream>() {

				@Override
				public OutputStream getOutput() throws IOException {
					throw new UnsupportedOperationException();
				}

				@Override
				public URI getLocation() {
					return filesWritten.get(0);
				}
			});
		}
	}

	/**
	 * Partition instances in parts that respectively contain all referenced
	 * instances.
	 * 
	 * @param partitioner the partitioner
	 * @param instances instances to partition
	 * @param threshold the guiding value for the maximum number of objects in a
	 *            part
	 * @param progress Progress indicator
	 * @param log the operation log
	 * @return an iterator of instance collections, each instance collection
	 *         represents a part
	 */
	protected ResourceIterator<InstanceCollection> partition(
			InstanceCollectionPartitioner partitioner, InstanceCollection instances, int threshold,
			ProgressIndicator progress, SimpleLog log) {
		progress.setCurrentTask("Partitioning data");

		// partition the graph
		return partitioner.partition(instances, threshold, log);
	}

	// FIXME
//	/**
//	 * @see AbstractInstanceWriter#getValidationSchemas()
//	 */
//	@Override
//	public List<Schema> getValidationSchemas() {
//		List<Schema> result = new ArrayList<Schema>(super.getValidationSchemas());
//		result.addAll(additionalSchemas);
//		return result;
//	}

	private boolean isThresholdConfigured() {
		int threshold = getParameter(PARAM_INSTANCES_THRESHOLD).as(Integer.class, NO_PARTITIONING);
		return threshold != NO_PARTITIONING && threshold > 0;
	}

	private boolean isPartitionByFeatureTypeConfigured() {
		return getParameter(PARAM_PARTITION_BY_FEATURE_TYPE).as(Boolean.class, false);
	}

	private boolean isPartitionByExtentConfigured() {
		return getParameter(PARAM_PARTITION_BY_EXTENT).as(Boolean.class, false);
	}

	@Override
	public boolean isPassthrough() {
		if (isPartitionByFeatureTypeConfigured()) {
			return false;
		}
		else if (isThresholdConfigured()) {
			InstanceCollectionPartitioner partitioner = getPartitioner(this, SimpleLog.NO_LOG);
			return !partitioner.usesReferences();
		}
		else {
			return true;
		}
	}

	/**
	 * @see AbstractInstanceWriter#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		if (getXMLIndex() == null) {
			fail("No XML target schema");
		}
	}

	/**
	 * @see AbstractInstanceWriter#checkCompatibility()
	 */
	@Override
	public void checkCompatibility() throws IOProviderConfigurationException {
		super.checkCompatibility();

		XmlIndex xmlIndex = getXMLIndex();
		if (xmlIndex == null) {
			fail("No XML target schema");
		}

		if (requiresDefaultContainer()) {
			XmlElement element;
			try {
				element = findDefaultContainter(xmlIndex, null);
			} catch (Exception e) {
				// ignore
				element = null;
			}
			if (element == null) {
				fail("Cannot find container element in schema.");
			}
		}
	}

	/**
	 * States if the instance writer in all cases requires that the default
	 * container is being found.
	 * 
	 * @return if the default container must be present in the target schema
	 */
	protected boolean requiresDefaultContainer() {
		return false; // not needed, we allow specifying it through a parameter
	}

	/**
	 * Get the XML type index.
	 * 
	 * @return the target type index
	 */
	protected XmlIndex getXMLIndex() {
		if (targetIndex == null) {
			targetIndex = getXMLIndex(getTargetSchema());
		}
		return targetIndex;
	}

	/**
	 * Get the XML index from the given schema space
	 * 
	 * @param schemas the schema space
	 * @return the XML index or <code>null</code>
	 */
	public static XmlIndex getXMLIndex(SchemaSpace schemas) {
		// XXX respect a container, types?
		for (Schema schema : schemas.getSchemas()) {
			if (schema instanceof XmlIndex) {
				// TODO respect root element for schema selection?
				return (XmlIndex) schema;
			}
		}

		return null;
	}

	/**
	 * Create and configure an <code>XMLStreamWriter</code> that writes to the
	 * given <code>OutputStream</code>
	 * 
	 * @param outStream <code>OutputStream</code> to write to
	 * @param reporter the reporter
	 * @return the configured <code>XMLStreamWriter</code>
	 * @throws XMLStreamException if creating or configuring the
	 *             <code>XMLStreamWriter</code> fails
	 */
	protected PrefixAwareStreamWriter createWriter(OutputStream outStream, IOReporter reporter)
			throws XMLStreamException {
		// create and set-up a writer
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		// will set namespaces if these not set explicitly
		outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", //$NON-NLS-1$
				Boolean.valueOf(true));

		// create XML stream writer with specified encoding
		PrefixAwareStreamWriter tmpWriter = new PrefixAwareStreamWriterDecorator(
				outputFactory.createXMLStreamWriter(outStream, getCharset().name())); // $NON-NLS-1$

		XmlIndex index = getXMLIndex();
		// read the namespaces from the map containing namespaces
		if (index.getPrefixes() != null) {
			for (Entry<String, String> entry : index.getPrefixes().entrySet()) {
				if (entry.getValue().isEmpty()) {
					// XXX don't use a default namespace, as this results in
					// problems with schemas w/o elementFormQualified=true
					// defNamespace = entry.getKey();
				}
				else {
					tmpWriter.setPrefix(entry.getValue(), entry.getKey());
				}
			}
		}

		GmlWriterUtil.addNamespace(tmpWriter, SCHEMA_INSTANCE_NS, "xsi"); //$NON-NLS-1$

		String defNamespace = null;

		// determine default namespace
//		if (defNamespace == null) {
		// XXX don't use a default namespace, as this results in problems
		// with schemas w/o elementFormQualified=true
		// defNamespace = index.getNamespace();

		// TODO remove prefix for target schema namespace?
//		}

		tmpWriter.setDefaultNamespace(defNamespace);

		if (documentWrapper != null) {
			documentWrapper.configure(tmpWriter, reporter);
		}

		// prettyPrint if enabled
		if (isPrettyPrint()) {
			return new IndentingXMLStreamWriter(tmpWriter);
		}
		else {
			return tmpWriter;
		}
	}

	/**
	 * Create and setup the type index and the GML namespace (Initializes
	 * {@link #gmlNs} and {@link #targetIndex}, resets {@link #geometryWriter}
	 * and {@link #additionalSchemas}).
	 */
	protected void init() {
		// reset target index
		targetIndex = null;
		// reset geometry writer
		geometryWriter = null;
		// reset additional schemas
		additionalSchemas.clear();
		additionalSchemaPrefixes.clear();

		// determine GML namespace from target schema
		String gml = null;
		XmlIndex index = getXMLIndex();
		if (index.getPrefixes() != null) {
			Set<String> candidates = new TreeSet<>();
			for (String ns : index.getPrefixes().keySet()) {
				if (ns.startsWith(GML_NAMESPACE_CORE)) { // $NON-NLS-1$
					candidates.add(ns);
				}
			}
			if (!candidates.isEmpty()) {
				if (candidates.size() == 1) {
					gml = candidates.iterator().next();
				}
				else {
					log.warn("Multiple candidates for GML namespace found");
					// TODO how to choose the right one?

					// prefer known GML namespaces
					if (candidates.contains(NS_GML_32)) {
						gml = NS_GML_32;
					}
					else if (candidates.contains(NS_GML)) {
						gml = NS_GML;
					}
					else {
						// fall back to first namespace
						gml = candidates.iterator().next();
					}
				}
			}
		}

		if (gml == null) {
			// default to GML 2/3 namespace
			gml = GML.NAMESPACE;
		}

		gmlNs = gml;
		if (log.isDebugEnabled()) {
			log.debug("GML namespace is " + gmlNs); //$NON-NLS-1$
		}
	}

	/**
	 * @return if the output should be pretty printed
	 */
	public boolean isPrettyPrint() {
		return getParameter(PARAM_PRETTY_PRINT).as(Boolean.class, false);
	}

	/**
	 * Set if the output should be pretty printed.
	 * 
	 * @param prettyPrint <code>true</code> if the output should be indented,
	 *            <code>false</code> otherwise
	 */
	public void setPrettyPrint(boolean prettyPrint) {
		setParameter(PARAM_PRETTY_PRINT, Value.of(prettyPrint));
	}

	/**
	 * @return {@link DecimalFormat} to apply to geometry coordinates
	 */
	public DecimalFormat getCoordinateFormatter() {
		String pattern = getParameter(PARAM_GEOMETRY_FORMAT).as(String.class);
		if (pattern != null && pattern.trim().length() > 0) {
			return DecimalFormatUtil.getFormatter(pattern);
		}

		return null;
	}

	/**
	 * @return {@link DecimalFormat} to apply to {@link Double} values
	 */
	public DecimalFormat getDecimalFormatter() {
		String pattern = getParameter(PARAM_DECIMAL_FORMAT).as(String.class);
		if (pattern != null && pattern.trim().length() > 0) {
			return DecimalFormatUtil.getFormatter(pattern);
		}

		return null;
	}

	/**
	 * Set the output format of geometry coordinates
	 * 
	 * @param format pattern in which geometry coordinates would be formatted
	 */
	public void setGeometryWriteFormat(String format) {
		setParameter(PARAM_GEOMETRY_FORMAT, Value.of(format));
	}

	/**
	 * Set the output format of {@link Double} values
	 * 
	 * @param format pattern in which geometry coordinates would be formatted
	 */
	public void setDecimalWriteFormat(String format) {
		setParameter(PARAM_DECIMAL_FORMAT, Value.of(format));
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return true;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "GML/XML";
	}

	/**
	 * Write the given instances to an {@link OutputStream}.
	 * 
	 * @param instances the instance collection
	 * @param out The <code>OutputStream</code> to write to
	 * @param reporter the reporter
	 * @param progress the progress
	 * @throws IOException if creating an XML stream writer fails
	 */
	protected void write(InstanceCollection instances, OutputStream out, ProgressIndicator progress,
			IOReporter reporter) throws IOException {
		PrefixAwareStreamWriter writer;
		try {
			writer = createWriter(out, reporter);
		} catch (XMLStreamException e) {
			throw new IOException("Creating the XML stream writer failed", e);
		}

		try {
			write(instances, writer, progress, reporter);
		} finally {
			out.close();
		}
	}

	/**
	 * Write the given instances to an {@link XMLStreamWriter}.<br>
	 * <br>
	 * Use {@link #createWriter(OutputStream, IOReporter)} to create a properly
	 * configured writer for this method.
	 * 
	 * @param instances the instance collection
	 * @param writer the writer to write the instances to
	 * @param reporter the reporter
	 * @param progress the progress
	 * @see #createWriter(OutputStream, IOReporter)
	 */
	protected void write(InstanceCollection instances, PrefixAwareStreamWriter writer,
			ProgressIndicator progress, IOReporter reporter) {

		this.writer = writer;

		try {
			final SubtaskProgressIndicator sub = new SubtaskProgressIndicator(progress) {

				@Override
				protected String getCombinedTaskName(String taskName, String subtaskName) {
					return taskName + " (" + subtaskName + ")";
				}

			};
			progress = sub;

			progress.begin(getTaskName(), instances.size());

			XmlElement container = findDefaultContainter(targetIndex, reporter);

			TypeDefinition containerDefinition = (container == null) ? (null)
					: (container.getType());
			QName containerName = (container == null) ? (null) : (container.getName());

			if (containerDefinition == null) {
				XmlElement containerElement = getConfiguredContainerElement(this, getXMLIndex());
				if (containerElement != null) {
					containerDefinition = containerElement.getType();
					containerName = containerElement.getName();
					container = containerElement;
				}
				else {
					// this is the last option, so we can throw a specific error
					throw new IllegalStateException("Configured container element not found");
				}
			}

			if (containerDefinition == null || containerName == null) {
				throw new IllegalStateException("No root element/container found");
			}

			/*
			 * Add schema for container to validation schemas, if the namespace
			 * differs from the main namespace or additional schemas.
			 * 
			 * Needed for validation based on schemaLocation attribute.
			 */
			if (container != null
					&& !containerName.getNamespaceURI().equals(targetIndex.getNamespace())
					&& !additionalSchemas.containsKey(containerName.getNamespaceURI())) {
				try {
					final URI containerSchemaLoc = stripFragment(container.getLocation());
					if (containerSchemaLoc != null) {
						addValidationSchema(containerName.getNamespaceURI(), new Locatable() {

							@Override
							public URI getLocation() {
								return containerSchemaLoc;
							}
						}, null);
					}
				} catch (Exception e) {
					reporter.error("Could not determine location of container definition", e);
				}

			}

			// additional schema namespace prefixes
			for (Entry<String, String> schemaNs : additionalSchemaPrefixes.entrySet()) {
				GmlWriterUtil.addNamespace(writer, schemaNs.getKey(), schemaNs.getValue());
			}

			writer.writeStartDocument();
			if (documentWrapper != null) {
				documentWrapper.startWrap(writer, reporter);
			}

			GmlWriterUtil.writeStartElement(writer, containerName);

			// generate mandatory id attribute (for feature collection)
			String containerId = getParameter(PARAM_CONTAINER_ID).as(String.class);
			GmlWriterUtil.writeID(writer, containerDefinition, null, false, containerId);

			// write schema locations
			StringBuffer locations = new StringBuffer();
			String noNamespaceLocation = null;
			if (targetIndex.getNamespace() != null && !targetIndex.getNamespace().isEmpty()) {
				locations.append(targetIndex.getNamespace());
				locations.append(" "); //$NON-NLS-1$
				locations.append(targetIndex.getLocation().toString());
			}
			else {
				noNamespaceLocation = targetIndex.getLocation().toString();
			}
			for (Entry<String, Locatable> schema : additionalSchemas.entrySet()) {
				if (schema.getKey() != null && !schema.getKey().isEmpty()) {
					if (locations.length() > 0) {
						locations.append(" "); //$NON-NLS-1$
					}
					locations.append(schema.getKey());
					locations.append(" "); //$NON-NLS-1$
					locations.append(schema.getValue().getLocation().toString());
				}
				else {
					noNamespaceLocation = schema.getValue().getLocation().toString();
				}
			}
			if (locations.length() > 0) {
				writer.writeAttribute(SCHEMA_INSTANCE_NS, "schemaLocation", locations.toString()); //$NON-NLS-1$
			}
			if (noNamespaceLocation != null) {
				writer.writeAttribute(SCHEMA_INSTANCE_NS, "noNamespaceSchemaLocation", //$NON-NLS-1$
						noNamespaceLocation);
			}

			writeAdditionalElements(writer, containerDefinition, reporter);

			// write the instances
			ResourceIterator<Instance> itInstance = instances.iterator();
			try {
				Map<TypeDefinition, DefinitionPath> paths = new HashMap<TypeDefinition, DefinitionPath>();

				long lastUpdate = 0;
				int count = 0;
				Descent lastDescent = null;
				while (itInstance.hasNext() && !progress.isCanceled()) {
					Instance instance = itInstance.next();

					TypeDefinition type = instance.getDefinition();

					/*
					 * Skip all objects that are no features when writing to a
					 * GML feature collection.
					 */
					boolean skip = useFeatureCollection && !GmlWriterUtil.isFeatureType(type);
					if (skip) {
						progress.advance(1);
						continue;
					}

					// get stored definition path for the type
					DefinitionPath defPath;
					if (paths.containsKey(type)) {
						// get the stored path, may be null
						defPath = paths.get(type);
					}
					else {
						// determine a valid definition path in the container
						defPath = findMemberAttribute(containerDefinition, containerName, type);
						// store path (may be null)
						paths.put(type, defPath);
					}
					if (defPath != null) {
						// write the feature
						lastDescent = Descent.descend(writer, defPath, lastDescent, false);
						writeMember(instance, type, reporter);
					}
					else {
						reporter.warn(new IOMessageImpl(MessageFormat.format(
								"No compatible member attribute for type {0} found in root element {1}, one instance was skipped",
								type.getDisplayName(), containerName.getLocalPart()), null));
					}

					progress.advance(1);
					count++;

					long now = System.currentTimeMillis();
					// only update every 100 milliseconds
					if (now - lastUpdate > 100 || !itInstance.hasNext()) {
						lastUpdate = now;
						sub.subTask(String.valueOf(count) + " instances");
					}
				}
				if (lastDescent != null) {
					lastDescent.close();
				}
			} finally {
				itInstance.close();
			}

			writer.writeEndElement(); // FeatureCollection

			if (documentWrapper != null) {
				documentWrapper.endWrap(writer, reporter);
			}
			writer.writeEndDocument();

			writer.close();

			reporter.setSuccess(reporter.getErrors().isEmpty());
		} catch (Exception e) {
			reporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}
	}

	/**
	 * Strip the fragment from a location (as it usually represents line and
	 * column numbers)
	 * 
	 * @param location the location
	 * @return the location w/o fragment
	 * @throws URISyntaxException if the URI cannot be recreated properly
	 */
	private URI stripFragment(URI location) throws URISyntaxException {
		return new URI(location.getScheme(), location.getUserInfo(), location.getHost(),
				location.getPort(), location.getPath(), location.getQuery(), null);
	}

	/**
	 * @return the execution task name
	 */
	protected String getTaskName() {
		return "Generating " + getTypeName();
	}

	/**
	 * This method is called after the container element is started and filled
	 * with needed attributes. The default implementation ensures that a
	 * mandatory boundedBy of GML 2 FeatureCollection is written.
	 * 
	 * @param writer the XML stream writer
	 * @param containerDefinition the container type definition
	 * @param reporter the reporter
	 * @throws XMLStreamException if writing anything fails
	 */
	protected void writeAdditionalElements(XMLStreamWriter writer,
			TypeDefinition containerDefinition, IOReporter reporter) throws XMLStreamException {
		// boundedBy is needed for GML 2 FeatureCollections
		// XXX working like this - getting the child with only a local name?
		ChildDefinition<?> boundedBy = containerDefinition.getChild(new QName("boundedBy")); //$NON-NLS-1$
		if (boundedBy != null && boundedBy.asProperty() != null
				&& boundedBy.asProperty().getConstraint(Cardinality.class).getMinOccurs() > 0) {
			writer.writeStartElement(boundedBy.getName().getNamespaceURI(),
					boundedBy.getName().getLocalPart());
			writer.writeStartElement(gmlNs, "null"); //$NON-NLS-1$
			writer.writeCharacters("missing"); //$NON-NLS-1$
			writer.writeEndElement();
			writer.writeEndElement();
		}
	}

	/**
	 * Get the for an I/O provider configured target container element, assuming
	 * the I/O provider uses the {@link #PARAM_ROOT_ELEMENT_NAMESPACE} and
	 * {@value #PARAM_ROOT_ELEMENT_NAME} parameters for this.
	 * 
	 * @param provider the I/O provider
	 * @param targetIndex the target XML index
	 * @return the container element or <code>null</code> if it was not found
	 */
	public static XmlElement getConfiguredContainerElement(IOProvider provider,
			XmlIndex targetIndex) {
		// no container defined, try to use a custom root element
		String namespace = provider.getParameter(PARAM_ROOT_ELEMENT_NAMESPACE).as(String.class);
		// determine target namespace
		if (namespace == null) {
			// default to target namespace
			namespace = targetIndex.getNamespace();
		}
		String elementName = provider.getParameter(PARAM_ROOT_ELEMENT_NAME).as(String.class);

		// find root element
		XmlElement containerElement = null;
		if (elementName != null) {
			QName name = new QName(namespace, elementName);
			containerElement = targetIndex.getElements().get(name);
		}

		return containerElement;
	}

	/**
	 * Find the default container element.
	 * 
	 * @param targetIndex the target type index
	 * @param reporter the reporter, may be <code>null</code>
	 * @return the container XML element or <code>null</code>
	 */
	protected XmlElement findDefaultContainter(XmlIndex targetIndex, IOReporter reporter) {
		if (useFeatureCollection) {
			// try to find FeatureCollection element
			Iterator<XmlElement> it = targetIndex.getElements().values().iterator();
			Collection<XmlElement> fcElements = new HashSet<XmlElement>();
			while (it.hasNext()) {
				XmlElement el = it.next();
				if (isFeatureCollection(el)) {
					fcElements.add(el);
				}
			}

			if (fcElements.isEmpty() && gmlNs != null && gmlNs.equals(NS_GML)) { // $NON-NLS-1$
				// no FeatureCollection defined and "old" namespace -> GML 2
				// include WFS 1.0.0 for the FeatureCollection element
				try {
					URI location = StreamGmlWriter.class
							.getResource("/schemas/wfs/1.0.0/WFS-basic.xsd").toURI(); //$NON-NLS-1$
					XmlSchemaReader schemaReader = new XmlSchemaReader();
					schemaReader.setSource(new DefaultInputSupplier(location));
					// FIXME to work with the extra schema it must be integrated
					// with the main schema
//					schemaReader.setSharedTypes(sharedTypes);

					IOReport report = schemaReader.execute(null);

					if (report.isSuccess()) {
						XmlIndex wfsSchema = schemaReader.getSchema();

						// look for FeatureCollection element
						for (XmlElement el : wfsSchema.getElements().values()) {
							if (isFeatureCollection(el)) {
								fcElements.add(el);
							}
						}

						// add as additional schema, replace location for
						// verification
						additionalSchemas.put(wfsSchema.getNamespace(),
								new SchemaDecorator(wfsSchema) {

									@Override
									public URI getLocation() {
										return URI.create(
												"http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd");
									}
								});

						// add namespace
						GmlWriterUtil.addNamespace(writer, wfsSchema.getNamespace(), "wfs"); //$NON-NLS-1$
					}
				} catch (Exception e) {
					log.warn("Using WFS schema for the FeatureCollection definition failed", e); //$NON-NLS-1$
				}
			}

			if (fcElements.isEmpty() && reporter != null) {
				reporter.warn(
						new IOMessageImpl("No element describing a FeatureCollection found", null)); //$NON-NLS-1$
			}
			else {
				// select fc element TODO priorized selection (root element
				// parameters)
				XmlElement fcElement = fcElements.iterator().next();

				log.info("Found " + fcElements.size() + " possible FeatureCollection elements" + //$NON-NLS-1$ //$NON-NLS-2$
						", using element " + fcElement.getName()); //$NON-NLS-1$

				return fcElement;
			}
		}

		return null;
	}

	/**
	 * Find a matching attribute for the given member type in the given
	 * container type
	 * 
	 * @param container the container type
	 * @param containerName the container element name
	 * @param memberType the member type
	 * 
	 * @return the attribute definition or <code>null</code>
	 */
	protected DefinitionPath findMemberAttribute(TypeDefinition container, QName containerName,
			final TypeDefinition memberType) {
		// XXX not working if property is no substitution of the property type -
		// use matching instead
//		for (PropertyDefinition property : GmlWriterUtil.collectProperties(container.getChildren())) {
//			// direct match - 
//			if (property.getPropertyType().equals(memberType)) {
//				long max = property.getConstraint(Cardinality.class).getMaxOccurs();
//				return new DefinitionPath(
//						property.getPropertyType(), 
//						property.getName(),
//						max != Cardinality.UNBOUNDED && max <= 1);
//			}
//		}

		AbstractTypeMatcher<TypeDefinition> matcher = new AbstractTypeMatcher<TypeDefinition>() {

			@Override
			protected DefinitionPath matchPath(TypeDefinition type, TypeDefinition matchParam,
					DefinitionPath path) {
				if (type.equals(memberType)) {
					return path;
				}

				// XXX special case: FeatureCollection from foreign schema
				Collection<? extends XmlElement> elements = matchParam
						.getConstraint(XmlElements.class).getElements();
				Collection<? extends XmlElement> containerElements = type
						.getConstraint(XmlElements.class).getElements();
				if (!elements.isEmpty() && !containerElements.isEmpty()) {
					TypeDefinition parent = matchParam.getSuperType();
					while (parent != null) {
						if (parent.equals(type)) {
							// FIXME will not work with separately loaded
							// schemas because e.g. the choice allowing the
							// specific type is missing
							// FIXME add to path
//							return new DefinitionPath(path).addSubstitution(elements.iterator().next());
						}

						parent = parent.getSuperType();
					}
				}

				return null;
			}
		};

		// candidate match (go down at maximum ten levels)
		List<DefinitionPath> candidates = matcher.findCandidates(container, containerName, true,
				memberType, 10);
		if (candidates != null && !candidates.isEmpty()) {
			return candidates.get(0); // TODO notification? FIXME will this
										// work? possible problem: attribute is
										// selected even though better candidate
										// is in other attribute
		}

		return null;
	}

	/**
	 * Method to determine if an element represents a feature collection in the
	 * context of the current writer.
	 * 
	 * @param el Element to check
	 * @return true if the element is a feature collection
	 */
	protected boolean isFeatureCollection(XmlElement el) {
		// TODO improve condition?
		// FIXME working like this?!
		return (el.getName().getLocalPart().contains("FeatureCollection"))
				&& !el.getType().getConstraint(AbstractFlag.class).isEnabled()
				&& hasChild(el.getType(), "featureMember"); //$NON-NLS-1$
	}

	/**
	 * Check if a type has a child with the given local name.
	 * 
	 * @param type Type to search
	 * @param localName Local name to search for
	 * @return true if such a child exists
	 */
	protected boolean hasChild(TypeDefinition type, String localName) {
		for (ChildDefinition<?> child : DefinitionUtil.getAllProperties(type)) {
			if (localName.equals(child.getName().getLocalPart())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Write a given instance
	 * 
	 * @param instance the instance to writer
	 * @param type the feature type definition
	 * @param report the reporter
	 * @throws XMLStreamException if writing the feature fails
	 */
	protected void writeMember(Instance instance, TypeDefinition type, IOReporter report)
			throws XMLStreamException {
//		Name elementName = GmlWriterUtil.getElementName(type);
//		writer.writeStartElement(elementName.getNamespaceURI(), elementName.getLocalPart());

		writeProperties(instance, type, true, false, true, report);

//		writer.writeEndElement(); // type element name
	}

	/**
	 * Write the given feature's properties
	 * 
	 * @param group the feature
	 * @param definition the feature type
	 * @param allowElements if element properties may be written
	 * @param parentIsNil if the parent property is nil
	 * @param addCodespace if codespace is automatically added
	 * @param report the reporter
	 * @throws XMLStreamException if writing the properties fails
	 */
	private void writeProperties(Group group, DefinitionGroup definition, boolean allowElements,
			boolean parentIsNil, boolean addCodespace, IOReporter report)
			throws XMLStreamException {
		// eventually generate mandatory ID that is not set
		GmlWriterUtil.writeRequiredID(writer, definition, group, true);

		// writing the feature is controlled by the type definition
		// so retrieving values from instance must happen based on actual
		// structure! (e.g. including groups)

		// write the attributes, as they must be handled first
		writeProperties(group, DefinitionUtil.getAllChildren(definition), true, parentIsNil,
				addCodespace, report);

		if (allowElements) {
			// write the elements
			writeProperties(group, DefinitionUtil.getAllChildren(definition), false, parentIsNil,
					addCodespace, report);
		}
	}

	/**
	 * Write attribute or element properties.
	 * 
	 * @param parent the parent group
	 * @param children the child definitions
	 * @param attributes <code>true</code> if attribute properties shall be
	 *            written, <code>false</code> if element properties shall be
	 *            written
	 * @param parentIsNil if the parent property is nil
	 * @param addCodespace if codespace should be automatically added
	 * @param report the reporter
	 * @throws XMLStreamException if writing the attributes/elements fails
	 */
	private void writeProperties(Group parent, Collection<? extends ChildDefinition<?>> children,
			boolean attributes, boolean parentIsNil, boolean addCodespace, IOReporter report)
			throws XMLStreamException {
		if (parent == null) {
			return;
		}

		boolean parentIsChoice = parent.getDefinition() instanceof GroupPropertyDefinition
				&& ((GroupPropertyDefinition) parent.getDefinition())
						.getConstraint(ChoiceFlag.class).isEnabled();

		for (ChildDefinition<?> child : children) {
			Object[] values = parent.getProperty(child.getName());

			if (child.asProperty() != null) {
				PropertyDefinition propDef = child.asProperty();
				boolean isAttribute = propDef.getConstraint(XmlAttributeFlag.class).isEnabled();

				if (attributes && isAttribute) {
					if (values != null && values.length > 0) {
						boolean allowWrite = true;

						// special case handling: omit nilReason
						if (getParameter(PARAM_OMIT_NIL_REASON).as(Boolean.class, true)) {
							Cardinality propCard = propDef.getConstraint(Cardinality.class);
							if ("nilReason".equals(propDef.getName().getLocalPart())
									&& propCard.getMinOccurs() < 1) {
								allowWrite = parentIsNil;
							}
						}

						// write attribute
						if (allowWrite) {
							// special case handling: replace incorrect
							// nilReason "unpopulated"
							if ("nilReason".equals(propDef.getName().getLocalPart())
									&& "unpopulated".equals(values[0])) {
								// TODO more strict check to ensure that this is
								// a GML nilReason? (check property type and
								// parent types)
								writeAttribute("other:unpopulated", propDef);
							}
							else {
								// default
								writeAttribute(values[0], propDef);
							}
						}

						if (values.length > 1) {
							// TODO warning?!
						}
					} // end if for nilReason

					if (child.getName().getLocalPart().toString().equals("codeSpace")
							&& values == null) {
						boolean allowWrite = true;

						// special case handling: auto add codespace
						if (getParameter(PARAM_ADD_CODESPACE).as(Boolean.class, true)) {
							if (child.getName().getLocalPart().toString().equals("codeSpace")) {
								allowWrite = addCodespace;
								// allowWrite = true;
							}
						}

						// write attribute
						if (allowWrite) {
							// special case handling: automatically add
							// codespace to gml:identifier
							if (child.getName().getLocalPart().toString().equals("codeSpace")) {
								writeAttribute("http://inspire.ec.europa.eu/ids", propDef);
							}
						}
					} // end if codespace

				}
				else if (!attributes && !isAttribute) {
					int numValues = 0;
					if (values != null) {
						// write element
						for (Object value : values) {
							writeElement(value, propDef, report);
						}
						numValues = values.length;
					}

					// write additional elements to satisfy minOccurrs
					// only if parent is not a choice
					if (!parentIsChoice) {
						Cardinality cardinality = propDef.getConstraint(Cardinality.class);
						if (cardinality.getMinOccurs() > numValues) {
							if (propDef.getConstraint(NillableFlag.class).isEnabled()) {
								// nillable element
								for (int i = numValues; i < cardinality.getMinOccurs(); i++) {
									// write nil element
									writeElement(null, propDef, report);
								}
							}
							else {
								// no value for non-nillable element

								for (int i = numValues; i < cardinality.getMinOccurs(); i++) {
									// write empty element
									GmlWriterUtil.writeEmptyElement(writer, propDef.getName());
								}

								// TODO add warning to report
							}
						}
					}
				}
			}
			else if (child.asGroup() != null) {
				// handle to child groups
				if (values != null) {
					for (Object value : values) {
						if (value instanceof Group) {
							writeProperties((Group) value,
									DefinitionUtil.getAllChildren(child.asGroup()), attributes,
									parentIsNil, addCodespace, report);
						}
						else {
							// TODO warning/error?
						}
					}
				} // if(values!=null)
			} // if (child.asProperty!=0)
		} // end for loop children

	} // end method

	/**
	 * Write a property element.
	 * 
	 * @param value the element value
	 * @param propDef the property definition
	 * @param report the reporter
	 * @throws XMLStreamException if writing the element fails
	 */
	private void writeElement(Object value, PropertyDefinition propDef, IOReporter report)
			throws XMLStreamException {
		Group group = null;
		if (value instanceof Group) {
			group = (Group) value;
			if (value instanceof Instance) {
				// extract value from instance
				value = ((Instance) value).getValue();
			}
		}

		if (group == null) {
			// just a value

			if (value == null) {
				// null value
				if (propDef.getConstraint(Cardinality.class).getMinOccurs() > 0) {
					// write empty element
					GmlWriterUtil.writeEmptyElement(writer, propDef.getName());

					// mark as nil
					writeElementValue(null, propDef);
				}
				// otherwise just skip it
			}
			else {
				GmlWriterUtil.writeStartElement(writer, propDef.getName());

				Pair<Geometry, CRSDefinition> pair = extractGeometry(value, true, report);
				if (pair != null) {
					String srsName = extractCode(pair.getSecond());
					// write geometry
					writeGeometry(pair.getFirst(), propDef, srsName, report);
				}
				else {
					// simple element with value
					// write value as content
					writeElementValue(value, propDef);
				}

				writer.writeEndElement();
			}
		}
		else {
			// children and maybe a value

			GmlWriterUtil.writeStartElement(writer, propDef.getName());

			boolean hasValue = propDef.getPropertyType().getConstraint(HasValueFlag.class)
					.isEnabled();

			boolean isIdentifier = propDef.getDisplayName().equals("identifier");

			Pair<Geometry, CRSDefinition> pair = extractGeometry(value, true, report);
			// handle about annotated geometries
			if (!hasValue && pair != null) {
				String srsName = extractCode(pair.getSecond());
				// write geometry
				writeGeometry(pair.getFirst(), propDef, srsName, report);
			}
			else {
				boolean hasOnlyNilReason = hasOnlyNilReason(group);

				// write no elements if there is a value or only a nil reason
				boolean writeElements = !hasValue && !hasOnlyNilReason;
				boolean isNil = !writeElements && (!hasValue || value == null);
				boolean isCodespace = isIdentifier || value == null; // Not
																		// fully
																		// sure
																		// about
																		// that
																		// one

				// write all children
				writeProperties(group, group.getDefinition(), writeElements, isNil, isCodespace,
						report);

				// write value
				if (hasValue) {
					writeElementValue(value, propDef);
				}
				else if (hasOnlyNilReason) {
					// complex element with a nil value -> write xsi:nil if
					// possible

					/*
					 * XXX open question: should xsi:nil be there also if there
					 * are other attributes than nilReason?
					 */

					writeElementValue(null, propDef);
				}
			}

			writer.writeEndElement();
		}
	}

	/**
	 * Determines if a group has as its only property the nilReason attribute.
	 * 
	 * @param group the group to test
	 * @return <code>true</code> if the group has the nilReason attribute and no
	 *         other children, or no children at all, <code>false</code>
	 *         otherwise
	 */
	private boolean hasOnlyNilReason(Group group) {
		int count = 0;
		QName nilReasonName = null;
		for (QName name : group.getPropertyNames()) {
			if (count > 0)
				// more than one property
				return false;
			if (!name.getLocalPart().equals("nilReason"))
				// a property different from nilReason
				return false;
			nilReasonName = name;
			count++;
		}

		if (nilReasonName != null) {
			// make sure it is an attribute
			DefinitionGroup parent = group.getDefinition();
			ChildDefinition<?> child = parent.getChild(nilReasonName);
			if (child.asProperty() == null) {
				// is a group
				return false;
			}
			if (!child.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
				// not an attribute
				return false;
			}
		}

		return true;
	}

	/**
	 * Write an element value, either as element content or as <code>nil</code>.
	 * 
	 * @param value the element value
	 * @param propDef the property definition the value is associated to
	 * @throws XMLStreamException if an error occurs writing the value
	 */
	private void writeElementValue(Object value, PropertyDefinition propDef)
			throws XMLStreamException {
		if (value == null) {
			// null value
			if (!propDef.getConstraint(NillableFlag.class).isEnabled()) {
				log.warn("Non-nillable element " + propDef.getName() + " is null"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				// nillable -> we may mark it as nil
				writer.writeAttribute(SCHEMA_INSTANCE_NS, "nil", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		else {
			TypeDefinition propType = propDef.getPropertyType();

			if (value instanceof Iterable
					&& List.class
							.isAssignableFrom(propType.getConstraint(Binding.class).getBinding())
					&& propType.getConstraint(ElementType.class).getBinding() != null) {
				// element is a list
				// TODO more robust detection?

				boolean first = true;
				for (Object element : ((Iterable<?>) value)) {
					if (first) {
						first = false;
					}
					else {
						// space delimits list elements
						writer.writeCharacters(" ");
					}

					// write the element
					writer.writeCharacters(SimpleTypeUtil.convertToXml(element,
							propType.getConstraint(ElementType.class).getDefinition()));
				}
			}
			else if (getDecimalFormatter() != null && (value instanceof Double
					|| value instanceof Float || value instanceof BigDecimal)) {
				// Apply formatting only to decimal values, not integers
				String representation = DecimalFormatUtil.applyFormatter((Number) value,
						getDecimalFormatter());
				writer.writeCharacters(
						SimpleTypeUtil.convertToXml(representation, propDef.getPropertyType()));
			}
			else {
				// write value as content
				writer.writeCharacters(
						SimpleTypeUtil.convertToXml(value, propDef.getPropertyType()));
			}
		}
	}

	/**
	 * Write a geometry
	 * 
	 * @param geometry the geometry
	 * @param property the geometry property
	 * @param srsName the common SRS name, may be <code>null</code>
	 * @param report the reporter
	 * @throws XMLStreamException if an error occurs writing the geometry
	 */
	private void writeGeometry(Geometry geometry, PropertyDefinition property, String srsName,
			IOReporter report) throws XMLStreamException {

		// write geometries
		getGeometryWriter().write(writer, geometry, property, srsName, report,
				getCoordinateFormatter());
	}

	/**
	 * Get the geometry writer
	 * 
	 * @return the geometry writer instance to use
	 */
	protected StreamGeometryWriter getGeometryWriter() {
		if (geometryWriter == null) {
			// default to true
			boolean simplifyGeometry = getParameter(PARAM_SIMPLIFY_GEOMETRY).as(Boolean.class,
					true);

			geometryWriter = StreamGeometryWriter.getDefaultInstance(gmlNs, simplifyGeometry);
		}

		return geometryWriter;
	}

	/**
	 * Write a property attribute
	 * 
	 * @param value the attribute value, may be <code>null</code>
	 * @param propDef the associated property definition
	 * @throws XMLStreamException if writing the attribute fails
	 */
	private void writeAttribute(Object value, PropertyDefinition propDef)
			throws XMLStreamException {
		GmlWriterUtil.writeAttribute(writer, value, propDef);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.io.impl.AbstractGeoInstanceWriter#getDefaultWindingOrder()
	 */
	@Override
	protected EnumWindingOrderTypes getDefaultWindingOrder() {
		return EnumWindingOrderTypes.counterClockwise;
	}
}
