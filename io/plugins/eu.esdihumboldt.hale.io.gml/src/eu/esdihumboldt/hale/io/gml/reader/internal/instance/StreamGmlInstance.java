/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.gml.reader.internal.instance;

import eu.esdihumboldt.hale.common.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Instance read from a XML or GML stream. Stores its index in the stream.
 * @author Simon Templer
 */
public class StreamGmlInstance extends OInstance {
	
	private final int indexInStream;

	/**
	 * Create an instance with an associated stream index
	 * @param typeDef the type definition
	 * @param indexInStream the index of the instance in the stream
	 */
	public StreamGmlInstance(TypeDefinition typeDef,
			int indexInStream) {
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
