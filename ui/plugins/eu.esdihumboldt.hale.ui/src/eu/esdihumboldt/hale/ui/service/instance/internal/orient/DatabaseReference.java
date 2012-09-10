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

import com.orientechnologies.orient.core.db.ODatabase;

/**
 * Reference to an OrientDB database
 * 
 * @param <T> the concrete type of the reference
 * 
 * @author Simon Templer
 */
public interface DatabaseReference<T extends ODatabase> {

	/**
	 * Get the database reference.
	 * 
	 * @return the database reference
	 */
	public T getDatabase();

	/**
	 * Dispose the reference when the database object isn't used any more. The
	 * underlying connection is closed.<br>
	 * This should be called in a finally block after using the database
	 * reference.
	 */
	public void dispose();

	/**
	 * Dispose the reference when the database object isn't used any more. This
	 * should be called in a finally block after using the database reference.
	 * 
	 * @param closeConnection if the underlying connection shall be closed, only
	 *            use <code>false</code> here if you are providing other means
	 *            of closing the database
	 */
	public void dispose(boolean closeConnection);

}
