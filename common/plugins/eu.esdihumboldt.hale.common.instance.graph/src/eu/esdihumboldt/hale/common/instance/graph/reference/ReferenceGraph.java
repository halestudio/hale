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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
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

	private static final ALogger log = ALoggerFactory.getLogger(ReferenceGraph.class);

	/**
	 * Iterator for instance partitions.
	 */
	public class PartitionIterator implements Iterator<InstanceCollection> {

		private final Queue<List<InstanceReference>> candidates = new LinkedList<>();
		private final int maxObjects;
		private int partCount = 0;

		/**
		 * @param maxObjects the guiding value for the maximum number of objects
		 *            in a part
		 */
		public PartitionIterator(int maxObjects) {
			this.maxObjects = maxObjects;
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
			return graph.getVertices().iterator().hasNext();
		}

		@Override
		public InstanceCollection next() {
			List<InstanceReference> part = candidates.poll();
			if (part == null) {
				part = new LinkedList<>();
			}

			while (verticesLeft() && part.size() < maxObjects) {
				// add to part
				List<InstanceReference> instances = getNextAtomicPart();

				if (part.size() + instances.size() > maxObjects) {
					// add to part candidates for later use
					candidates.add(instances);
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

			if (part.isEmpty()) {
				// no vertices left
				if (!candidates.isEmpty()) {
					// yield a previously stored candidate that was to big
					part = candidates.poll();
				}
				else {
					throw new NoSuchElementException("All parts were retrieved");
				}
			}

			partCount++;
			log.debug("Reference based partitioning - Part {} - {} instances", partCount,
					part.size());

			return new ReferencesInstanceCollection(part, originalCollection);
		}

		/**
		 * @return the next atomic part from the graph as instance references
		 */
		private List<InstanceReference> getNextAtomicPart() {
			Iterator<Vertex> it = graph.getVertices().iterator();
			if (it.hasNext()) {
				// select an arbitrary vertex
				Vertex vtx = it.next();

				// get all vertices associated with that vertex
				GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<>();
				final Set<Vertex> visited = new LinkedHashSet<>();
				/**
				 * Example for the Groovy console - getting all associated
				 * vertices. <code>
				 * g = TinkerGraphFactory.createTinkerGraph()
				 * x = new LinkedHashSet()
				 * g.v(1).as('ref').aggregate(x).both.loop('ref', { !x.contains(it.object) })
				 * x
				 * </code>
				 */
				pipe.start(vtx).as("ref").aggregate(visited).both()
						.loop("ref", new PipeFunction<LoopBundle<Vertex>, Boolean>() {

							@Override
							public Boolean compute(LoopBundle<Vertex> loop) {
								return !visited.contains(loop.getObject());
							}
						}).iterate();

				List<InstanceReference> result = new LinkedList<>();
				for (Vertex associated : visited) {
					InstanceReference ref = associated.getProperty(P_INSTANCE_REFERENCE);
					if (ref != null) {
						result.add(ref);
					}
					else {
						log.warn("Encountered referenced object w/o associated instance: "
								+ associated.getProperty(P_IDENT).toString());
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

	private static final String P_IDENT = "id";

	private final Graph graph;

	private final IdentityReferenceInspector<T> inspector;

	/**
	 * Index for fast retrieval of vertices by identifier.
	 */
	private final Map<T, Vertex> identifiedVertices = new HashMap<>();

	private final InstanceCollection originalCollection;

	private boolean partitioned = false;

	/**
	 * Create a new reference graph from the given instance collection.
	 * 
	 * @param inspector the instance inspector to use
	 * @param instances the
	 */
	public ReferenceGraph(IdentityReferenceInspector<T> inspector, InstanceCollection instances) {
		this.graph = new TinkerGraph();
		this.inspector = inspector;
		this.originalCollection = instances;

		populate(instances);

		// identified vertices no longer needed after populate
		identifiedVertices.clear();
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
	 * @return an iterator of instance collections, each instance collection
	 *         represents a part
	 */
	public Iterator<InstanceCollection> partition(int maxObjects) {
		if (!partitioned) {
			partitioned = true;
			return new PartitionIterator(maxObjects);
		}
		throw new IllegalStateException(
				"Partitioning the instance collection can only be done once");
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
