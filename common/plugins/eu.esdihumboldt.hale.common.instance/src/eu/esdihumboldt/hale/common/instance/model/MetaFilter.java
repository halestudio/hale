/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.instance.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Filter that accepts instances based on their instance metadata entries.
 * 
 * @author Simon Templer
 */
public class MetaFilter implements Filter {

	/*
	 * FIXME the type should not be part of this filter - this is done for now
	 * to be able to easily add support to the OrientDB instance collection and
	 * to the data view instance selector.
	 */
	private final TypeDefinition type;

	private final String metadataKey;

	private final Set<? extends Object> values;

	/**
	 * Create a new metadata filter.
	 * 
	 * @param type the type of instances to accept, may be <code>null</code>
	 * @param metadataKey the name of the metadata key to check
	 * @param values the values that should be accepted for the given metadata
	 *            key
	 */
	public MetaFilter(@Nullable TypeDefinition type, @Nonnull String metadataKey,
			@Nonnull Collection<? extends Object> values) {
		super();
		this.type = type;
		this.metadataKey = metadataKey;
		this.values = new HashSet<>(values);
	}

	@Override
	public boolean match(Instance instance) {
		if (type != null) {
			if (!type.equals(instance.getDefinition())) {
				return false;
			}
		}

		if (values.isEmpty()) {
			return true;
		}

		List<Object> meta = instance.getMetaData(metadataKey);
		for (Object obj : meta) {
			if (values.contains(obj)) {
				// accept if there is at least one match
				return true;
			}
		}

		return false;
	}

	/**
	 * @return the type
	 */
	public TypeDefinition getType() {
		return type;
	}

	/**
	 * @return the metadataKey
	 */
	public String getMetadataKey() {
		return metadataKey;
	}

	/**
	 * @return the values
	 */
	public Set<? extends Object> getValues() {
		return Collections.unmodifiableSet(values);
	}
}
