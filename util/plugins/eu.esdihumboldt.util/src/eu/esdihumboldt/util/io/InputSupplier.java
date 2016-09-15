/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.util.io;

import java.io.IOException;

/**
 * A factory for readable sources, usually a stream.
 * 
 * @author Simon Templer
 * @param <T> the input source type
 */
public interface InputSupplier<T> {

	/**
	 * Returns an object that encapsulates a readable resource.
	 * 
	 * This method may be called repeatedly to get independent channels to the
	 * same underlying resource.
	 * 
	 * @return a new input source, the caller is responsible for cleanup
	 * @throws IOException if a new input source cannot be opened
	 */
	T getInput() throws IOException;

}
