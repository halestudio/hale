/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.instance.index;

import java.util.Collection;

/**
 * Base interface for index definitions
 * 
 * @author Florian Esser
 * @param <K> Key type
 * @param <D> Document type
 * @param <Q> Query type
 */
public interface Index<K, D, Q> {

	/**
	 * Add a document to the index with the given key
	 * 
	 * @param key Key
	 * @param document Document to index
	 */
	void add(K key, D document);

	/**
	 * Retrieve a document from the index
	 * 
	 * @param key Document key
	 * @return The document
	 */
	D get(K key);

	/**
	 * Find indexed documents that match the given query
	 * 
	 * @param query Search query
	 * @return Collection of keys of matching documents
	 */
	Collection<K> search(Q query);
}
