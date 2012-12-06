/*
 * Copyright (c) 2012 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.internal;

import java.util.Collections;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;

/**
 * Namespace context backed by a {@link BiMap}. It ensures that a prefix for a
 * namespace that was specified once is not overridden. Prefixes are first come,
 * first serve.
 * 
 * @author Simon Templer
 */
public class NamespaceContextImpl implements NamespaceContext {

	/**
	 * The prefix for generated namespace prefixes.
	 */
	private static final String DEFAULT_NS_PREFIX = "ns";

	/**
	 * Namespace prefixes mapped to namespaces.
	 */
	private final BiMap<String, String> prefixes = HashBiMap.create();

	/**
	 * Add a namespace prefix.
	 * 
	 * @param preferredPrefix the preferred namespace prefix
	 * @param namespace the namespace
	 * @return the used namespace prefix
	 */
	public String add(final String preferredPrefix, final String namespace) {
		if (!prefixes.containsValue(namespace)) {
			// namespace not yet added
			String prefix = preferredPrefix;
			int i = 1;
			while (prefix == null || prefix.isEmpty() || prefixes.containsKey(prefix)) {
				// find an alternate prefix
				prefix = DEFAULT_NS_PREFIX + i++;
			}
			prefixes.put(prefix, namespace);
		}
		return getPrefix(namespace);
	}

	/**
	 * Get the prefix namespace mapping as a map.
	 * 
	 * @return an immutable map where prefixes are mapped to namespaces
	 */
	public BiMap<String, String> asMap() {
		return ImmutableBiMap.copyOf(prefixes);
	}

	@Override
	public String getNamespaceURI(String prefix) {
		return prefixes.get(prefix);
	}

	@Override
	public String getPrefix(String namespaceURI) {
		return prefixes.inverse().get(namespaceURI);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getPrefixes(String namespaceURI) {
		return Collections.singleton(prefixes.inverse().get(namespaceURI)).iterator();
	}

}
