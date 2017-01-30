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

package eu.esdihumboldt.hale.io.gml.reader.internal.instance;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Instance read from a XML or GML stream. Stores its index in the stream.
 * 
 * @author Simon Templer
 */
public class StreamGmlInstance extends DefaultInstance {

	private final int indexInStream;

	/**
	 * Copy constructor. Creates an instance based on the properties and values
	 * of the given instance.
	 * 
	 * @param instance the instance to copy
	 * @param indexInStream the index of the instance in the stream
	 */
	public StreamGmlInstance(Instance instance, int indexInStream) {
		super(instance);
		this.indexInStream = indexInStream;
	}

	/**
	 * Create an instance with an associated stream index
	 * 
	 * @param typeDef the type definition
	 * @param indexInStream the index of the instance in the stream
	 */
	public StreamGmlInstance(TypeDefinition typeDef, int indexInStream) {
		super(typeDef, null); // not necessary to specify a data set
		this.indexInStream = indexInStream;
	}

	/**
	 * @return the index in the stream
	 */
	public int getIndexInStream() {
		return indexInStream;
	}

}
