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
