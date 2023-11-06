/*
 * Copyright (c) 2022 wetransform GmbH
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

package eu.esdihumboldt.hale.common.schema.persist.hsd;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;

/**
 * Helper class for merging multiple schemas for export.
 * 
 * @author Simon Templer
 */
public class MergeSchemas {

	private static class MergedSchema extends DefaultSchemaSpace implements Schema {

		private final String namespace;

		public MergedSchema(String namespace, Iterable<? extends Schema> schemas) {
			this.namespace = namespace;
			for (Schema schema : schemas) {
				addSchema(schema);
			}
		}

		@Override
		public URI getLocation() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getNamespace() {
			return this.namespace;
		}

	}

	/**
	 * Namespace to use for combined schemas if no common namespace is
	 * available.
	 */
	public static final String DEFAULT_COMBINED_NAMESPACE = HaleSchemaConstants.NS + "/combine";

	/**
	 * Merge the given schemas to a single schema.
	 * 
	 * @param schemas the schemas to merge
	 * @param mergeAll <code>true</code> if all schemas should be merged
	 *            independent of the namespace, <code>false</code> if only those
	 *            with the same namespace should be merged
	 * @return the merged schemas
	 */
	public static Iterable<? extends Schema> merge(Iterable<? extends Schema> schemas,
			boolean mergeAll) {
		// count namespaces
		Map<String, Long> counts = Streams.stream(schemas).map(schema -> {
			String ns = schema.getNamespace();
			if (ns == null) {
				// replace null with empty string -> counting fails for null
				// values
				ns = "";
			}
			return ns;
		}).collect(Collectors.groupingBy(e -> e, Collectors.counting()));

		if (mergeAll) {
			// we merge to one namespace
			String namespace;
			if (counts.size() == 1) {
				// if there is only one namespace, use it
				namespace = counts.keySet().iterator().next();
			}
			else {
				namespace = DEFAULT_COMBINED_NAMESPACE;
			}
			if ("".equals(namespace)) {
				namespace = null;
			}

			Schema schema = new MergedSchema(namespace, schemas);

			return Collections.singletonList(schema);
		}
		else {
			// TODO support merging by namespace
			throw new UnsupportedOperationException();
		}
	}

}
