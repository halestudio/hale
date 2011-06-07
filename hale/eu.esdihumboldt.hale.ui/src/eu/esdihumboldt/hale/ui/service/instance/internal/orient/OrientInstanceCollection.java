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

package eu.esdihumboldt.hale.ui.service.instance.internal.orient;

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.instance.model.ResourceIterator;

/**
 * Instance collection based on a {@link LocalOrientDB}
 * @author Simon Templer
 */
public class OrientInstanceCollection implements InstanceCollection {

	private final LocalOrientDB database;
	
	/**
	 * Create an instance collection based on the given database
	 * @param database the database
	 */
	public OrientInstanceCollection(LocalOrientDB database) {
		super();
		this.database = database;
	}

	/**
	 * @see InstanceCollection#iterator()
	 */
	@Override
	public ResourceIterator<Instance> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see InstanceCollection#hasSize()
	 */
	@Override
	public boolean hasSize() {
		//XXX not for now
		return false;
	}

	/**
	 * @see InstanceCollection#size()
	 */
	@Override
	public int size() {
		return UNKNOWN_SIZE;
	}

	/**
	 * @see InstanceCollection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		//TODO
//		DatabaseReference<ODatabaseDocumentTx> ref = database.openRead();
//		ODatabaseDocumentTx db = ref.getDatabase();
//		try {
//			//db.countClass(iClassName);
//		} finally {
//			ref.dispose();
//		}
		return false;
	}

}
