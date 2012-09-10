/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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
