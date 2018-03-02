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

/**
 * Interface for mappings between a document and the indexed value
 * 
 * @author Florian Esser
 * @param <D> Document type
 * @param <V> Index value type
 */
public interface IndexMapping<D, V> {

	/**
	 * Map a document to an index value
	 * 
	 * @param document Document to map
	 * @return Index value of the document
	 */
	V map(D document);
}
