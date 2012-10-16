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

package eu.esdihumboldt.hale.io.jdbc.extension;


/**
 * Interface for extensions configuring a certain connection type.
 * @author Simon Templer
 * @param <T> the concrete connection type
 */
public interface ConnectionConfigurer<T> {
	
	/**
	 * Configure a connection after it has been established.
	 * @param connection the JDBC connection
	 */
	public void configureConnection(T connection);

}
