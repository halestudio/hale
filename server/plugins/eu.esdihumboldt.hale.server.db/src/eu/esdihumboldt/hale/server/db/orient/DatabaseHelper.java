/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.server.db.orient;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * Database helpers for working with an OrientDB based server database.
 * 
 * @author Simon Templer
 */
public class DatabaseHelper {

	private static final String DB_NAME = "server.db";

	/**
	 * Get a graph instance for use in the current thread.
	 * 
	 * @return an orient graph instance
	 */
	public static OrientGraph getGraph() {
		Location loc = Platform.getInstanceLocation();
		Path instancePath = Paths.get(URI.create(loc.getURL().toString().replaceAll(" ", "%20")));

		Path dbLoc = instancePath.resolve(DB_NAME);

		return new OrientGraph("local:" + dbLoc.toAbsolutePath().toString());
	}

	// using the non-transactional graph does not yield the same results!!!
//	/**
//	 * Get a graph instance for use in the current thread.
//	 * 
//	 * @return an orient graph instance
//	 */
//	public static OrientGraphNoTx getNonTransactionalGraph() {
//		Location loc = Platform.getInstanceLocation();
//		Path instancePath = Paths.get(URI.create(loc.getURL().toString().replaceAll(" ", "%20")));
//
//		Path dbLoc = instancePath.resolve(DB_NAME);
//
//		return new OrientGraphNoTx("local:" + dbLoc.toAbsolutePath().toString());
//	}

}
