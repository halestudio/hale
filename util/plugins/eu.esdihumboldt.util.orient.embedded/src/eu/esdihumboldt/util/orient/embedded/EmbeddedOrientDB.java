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

package eu.esdihumboldt.util.orient.embedded;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

/**
 * Controls bundle life cycle
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class EmbeddedOrientDB implements BundleActivator {

	private static OServer server;

	/**
	 * @see BundleActivator#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		server = OServerMain.create();
		// TODO instead configuration through fragment
		server.startup("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<orient-server>"
				+ "<network>"
				+ "<protocols>"
				+ "<protocol name=\"binary\" implementation=\"com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary\"/>"
				+ "<protocol name=\"http\" implementation=\"com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb\"/>"
				+ "</protocols>"
				+ "<listeners>"
//		   + "<listener ip-address=\"0.0.0.0\" port-range=\"2424-2430\" protocol=\"binary\"/>"
//		   + "<listener ip-address=\"0.0.0.0\" port-range=\"2480-2490\" protocol=\"http\"/>"
				+ "</listeners>"
				+ "</network>"
				+ "<users>"
				+ "<user name=\"root\" password=\"root\" resources=\"*\"/>"
				+ "</users>"
				+ "<properties>"
				// +
				// "<entry name=\"orientdb.www.path\" value=\"C:/work/dev/orientechnologies/orientdb/releases/1.0rc1-SNAPSHOT/www/\"/>"
				// +
				// "<entry name=\"orientdb.config.file\" value=\"C:/work/dev/orientechnologies/orientdb/releases/1.0rc1-SNAPSHOT/config/orientdb-server-config.xml\"/>"
				+ "<entry name=\"server.cache.staticResources\" value=\"false\"/>"
				+ "<entry name=\"log.console.level\" value=\"info\"/>"
				+ "<entry name=\"log.file.level\" value=\"info\"/>" + "</properties>"
				+ "</orient-server>");
	}

	/**
	 * @see BundleActivator#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		server.shutdown();
	}

	/**
	 * Get the server instance
	 * 
	 * @return the server instance
	 */
	public static OServer getServer() {
		return server;
	}

}
