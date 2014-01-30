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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.util.PlatformUtil;

/**
 * Database helpers for working with an OrientDB based server database.
 * 
 * @author Simon Templer
 */
public class DatabaseHelper {

	private static final ALogger log = ALoggerFactory.getLogger(DatabaseHelper.class);

	/**
	 * Name of the system property pointing to an existing folder where the
	 * server database should be created or loaded from.
	 */
	public static final String SYSTEM_PROPERTY_DB_FOLDER = "hale.server.db";

	private static final String DB_NAME = "server.db";

	/**
	 * Get a graph instance for use in the current thread.
	 * 
	 * @return an orient graph instance
	 */
	public static OrientGraph getGraph() {
		String path = System.getProperty(SYSTEM_PROPERTY_DB_FOLDER);
		if (path != null && !path.isEmpty()) {
			Path dbLoc = Paths.get(path);
			if (Files.exists(dbLoc) && Files.isDirectory(dbLoc)) {
				return new OrientGraph("local:" + dbLoc.toAbsolutePath().toString());
			}
			else {
				log.warn("Ignoring server database path specified through system property as it does not exists or is not a directory");
			}
		}

		Path instancePath = PlatformUtil.getInstanceLocation().toPath();

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
//		Path instancePath = PlatformUtil.getInstanceLocation().toPath();
//
//		Path dbLoc = instancePath.resolve(DB_NAME);
//
//		return new OrientGraphNoTx("local:" + dbLoc.toAbsolutePath().toString());
//	}

}
