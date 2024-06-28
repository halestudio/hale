/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.graph.reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

import javax.annotation.Nullable;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.graph.reference.internal.ReferencesInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * Graph with associations between instances.
 * 
 * @author Simon Templer
 * @param <T> the identifier type, must have a sensible equals implementation
 */
public class ReferenceGraph<T> {

	/**
	 * Custom tinker graph that allows fast access to a random contained vertex
	 * and to the information if the graph has any vertices.
	 * 
	 * This class was created because the call to
	 * {@link TinkerGraph#getVertices()} is very expensive for large graphs.
	 */
	public class CustomTinkerGraph extends TinkerGraph {

		private static final long serialVersionUID = -6470218605426839887L;

		/**
		 * @return a vertex in the graph
		 */
		@Nullable
		public Vertex someVertex() {
			if (isEmpty())
				return null;
			return vertices.values().iterator().next();
		}

		/**
		 * @return if the graph is empty
		 */
		public boolean isEmpty() {
			return vertices.isEmpty();
		}

	}

	private static final ALogger logger = ALoggerFactory.getLogger(ReferenceGraph.class);

	/**
	 * Iterator for instance partitions.
	 */
	public class PartitionIterator implements Iterator<InstanceCollection> {

		private final Queue<List<InstanceReference>> candidates = new LinkedList<>();
		private final int maxObjects;
		private int partCount = 0;
		private int partSum = 0;
		private int biggestAtom = 0;
		private final SimpleLog log;

		/**
		 * @param maxObjects the guiding value for the maximum number of objects
		 *            in a part
		 * @param log the operation log
		 */
		public PartitionIterator(int maxObjects, SimpleLog log) {
			this.maxObjects = maxObjects;
			this.log = log;
		}

		@Override
		public boolean hasNext() {
			/*
			 * There are additional parts if there are candidates in the queue
			 * and/or vertices left in the graph.
			 */
			return !candidates.isEmpty() || verticesLeft();
		}

		private boolean verticesLeft() {
			return !graph.isEmpty();
		}

		@Override
		public InstanceCollection next() {
			List<InstanceReference> part = candidates.poll();
			if (part == null) {
				part = new ArrayList<>(maxObjects);
			}

			Queue<List<InstanceReference>> nextCandidates = new LinkedList<>();

			while (verticesLeft() && part.size() < maxObjects) {
				// add to part
				List<InstanceReference> instances = getNextAtomicPart();
				biggestAtom = Math.max(biggestAtom, instances.size());

				if (part.size() > 0 && part.size() + instances.size() > maxObjects) {
					// add to part candidates for later use
					nextCandidates.add(instances);
					if (!verticesLeft()) {
						// we added everything to candidates and need to
						// terminate the loop
						break;
					}
				}
				else {
					// add to current part
					part.addAll(instances);
				}
			}

			// try to add parts from previous candidates
			while (!candidates.isEmpty() && part.size() < maxObjects) {
				List<InstanceReference> instances = candidates.poll();

				if (part.size() + instances.size() > maxObjects) {
					// add to part candidates for later use
					nextCandidates.add(instances);
				}
				else {
					// add to current part
					part.addAll(instances);
				}
			}

			// collected candidates for next attempt
			candidates.addAll(nextCandidates);

			if (part.isEmpty()) {
				// no vertices left
				if (!candidates.isEmpty()) {
					// yield a previously stored candidate that was to big
					// to fit into a request
					part = candidates.poll();
				}
				else {
					throw new NoSuchElementException("All parts were retrieved");
				}
			}

			partCount++;
			partSum += part.size();
			logger.debug("Reference based partitioning - Part {} - {} instances", partCount,
					part.size());

			if (!hasNext()) {
				log.info(
						"Completed partitioning of {1} instances in {0} parts, biggest inseparable set of instances was of size {2}.",
						partCount, partSum, biggestAtom);
			}

			return new ReferencesInstanceCollection(part, originalCollection);
		}

		/**
		 * @return the next atomic part from the graph as instance references
		 */
		private List<InstanceReference> getNextAtomicPart() {
			// select an arbitrary vertex
			Vertex vtx = null;

			if (handleFirst != null) {
				vtx = graph.getVertex(handleFirst);
			}
			if (vtx == null) {
				vtx = graph.someVertex();
			}
			if (vtx != null) {
				// get all vertices associated with that vertex
				final Set<Vertex> visited = new LinkedHashSet<>();
				if (!vtx.getEdges(Direction.BOTH).iterator().hasNext()) {
					// no edges associated - no need to use gremlin
					// does not speed up the process by much though
					visited.add(vtx);
				}
				else {
					/**
					 * Example for the Groovy console - getting all associated
					 * vertices. <code>
					 * g = TinkerGraphFactory.createTinkerGraph()
					 * x = new LinkedHashSet()
					 * g.v(1).as('ref').aggregate(x).both.loop('ref', { !x.contains(it.object) })
					 * x
					 * </code>
					 */
					GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<>();
					pipe.start(vtx).as("ref").aggregate(visited).both()
							.loop("ref", new PipeFunction<LoopBundle<Vertex>, Boolean>() {

								@Override
								public Boolean compute(LoopBundle<Vertex> loop) {
									return !visited.contains(loop.getObject());
								}
							}).iterate();
				}

				List<InstanceReference> result = new ArrayList<>();
				for (Vertex associated : visited) {
					InstanceReference ref = associated.getProperty(P_INSTANCE_REFERENCE);
					if (ref != null) {
						result.add(ref);
					}
					else {
						Set<String> ids = new HashSet<>();
						for (Vertex referer : associated.getVertices(Direction.IN)) {
							Object ident = referer.getId();
							if (ident != null) {
								ids.add(ident.toString());
							}
						}

						if (ids.isEmpty()) {
							log.warn("Encountered referenced object w/o associated instance: "
									+ associated.getId());
						}
						else {
							String enumIds = String.join(", ", ids);
							log.warn("Encountered referenced object w/o associated instance: "
									+ associated.getId() + " - referenced from " + enumIds);
						}
					}
				}

				// remove vertices from graph
				for (Vertex v : visited) {
					graph.removeVertex(v);
				}

				return result;
			}
			else {
				return Collections.emptyList();
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private static final String P_INSTANCE_REFERENCE = "instanceRef";

	private static final String E_REFERENCE = "refs";

	private final CustomTinkerGraph graph;

	private final IdentityReferenceInspector<T> inspector;

	/**
	 * Index for fast retrieval of vertices by identifier.
	 */
	private final Map<T, Vertex> identifiedVertices = new HashMap<>();

	private final InstanceCollection originalCollection;

	private boolean partitioned = false;

	private final T handleFirst;

	/**
	 * Create a new reference graph from the given instance collection.
	 * 
	 * @param inspector the instance inspector to use
	 * @param instances the
	 * @param handleFirst the ID of the first instance to handle, optional and
	 *            can be null
	 */
	public ReferenceGraph(IdentityReferenceInspector<T> inspector, InstanceCollection instances,
			T handleFirst) {
		this.graph = new CustomTinkerGraph();
		this.inspector = inspector;
		this.originalCollection = instances;
		this.handleFirst = handleFirst;

		populate(instances);

		// identified vertices no longer needed after populate
		identifiedVertices.clear();
	}

	/**
	 * @param inspector the instance inspector to use
	 * @param instances the
	 */
	public ReferenceGraph(IdentityReferenceInspector<T> inspector, InstanceCollection instances) {
		this(inspector, instances, null);
	}

	/**
	 * Populate the graph with the instances from the given collection.
	 * 
	 * @param instances an instance collection
	 */
	protected void populate(InstanceCollection instances) {
		try (ResourceIterator<Instance> it = instances.iterator()) {
			while (it.hasNext()) {
				Instance instance = it.next();
				addInstance(instance, instances.getReference(instance));
			}
		}
	}

	/**
	 * Add an instance to the reference graph.
	 * 
	 * @param instance the instance to add
	 * @param ref the reference that can be used to retrieve the instance
	 */
	protected void addInstance(Instance instance, InstanceReference ref) {
		// retrieve / create vertex
		T id = inspector.getIdentity(instance);
		Vertex vertex = getVertex(id);
		// store instance reference
		vertex.setProperty(P_INSTANCE_REFERENCE, ref);

		// create references
		Set<T> associations = inspector.getReferencedIdentities(instance);
		for (T idRef : associations) {
			Vertex assoc = getVertex(idRef);
			// add edge between vertices
			graph.addEdge(null, vertex, assoc, E_REFERENCE);
		}
	}

	/**
	 * Partition the collected instances in parts that respectively contain all
	 * referenced instances.
	 * 
	 * @param maxObjects the guiding value for the maximum number of objects in
	 *            a part
	 * @param log the operation log
	 * @return an iterator of instance collections, each instance collection
	 *         represents a part
	 */
	public Iterator<InstanceCollection> partition(int maxObjects, SimpleLog log) {
		if (!partitioned) {
			partitioned = true;
			return new PartitionIterator(maxObjects, log);
		}
		throw new IllegalStateException(
				"Partitioning the instance collection can only be done once");
	}

	/**
	 * Partition the collected instances in parts that respectively contain all
	 * referenced instances.
	 * 
	 * @param maxObjects the guiding value for the maximum number of objects in
	 *            a part
	 * @return an iterator of instance collections, each instance collection
	 *         represents a part
	 */
	public Iterator<InstanceCollection> partition(int maxObjects) {
		return partition(maxObjects, SimpleLog.fromLogger(logger));
	}

	private Vertex getVertex(T id) {
		Vertex vertex = null;
		if (id != null) {
			vertex = identifiedVertices.get(id);
		}
		if (vertex == null) {
			vertex = graph.addVertex(id);
			identifiedVertices.put(id, vertex);
		}
		return vertex;
	}

}
