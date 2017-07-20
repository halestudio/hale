/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.geoserver.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.esdihumboldt.hale.common.test.docker.AbstractDockerTest;
import eu.esdihumboldt.hale.common.test.docker.config.DockerConfigInstance;
import eu.esdihumboldt.hale.common.test.docker.config.HaleDockerClient;
import eu.esdihumboldt.hale.io.geoserver.AppSchemaDataStore;
import eu.esdihumboldt.hale.io.geoserver.DataStore;
import eu.esdihumboldt.hale.io.geoserver.DataStoreFile;
import eu.esdihumboldt.hale.io.geoserver.FeatureType;
import eu.esdihumboldt.hale.io.geoserver.Namespace;
import eu.esdihumboldt.hale.io.geoserver.ResourceBuilder;

@Ignore
@SuppressWarnings("javadoc")
public class ResourceManagerIT extends AbstractDockerTest {

	private static final String GEOSERVER_USER = "admin";
	private static final String GEOSERVER_PASSWORD = "geoserver";

	private static final String NAMESPACE_PREFIX = "hale";
	private static final String NAMESPACE_URI = "http://inspire.ec.europa.eu/schemas/lcv/3.0";
	private static final String NAMESPACE_URI_ALT = "http://www.esdi-community.eu/projects/hale";

	private static final String APP_SCHEMA_CONFIG_ARCHIVE = "/data/test_landcover.zip";
	private static final String APP_SCHEMA_DATASTORE = "LandCoverVector";

	private static String geoserverURL;
	private static HaleDockerClient client;

	private final Namespace ns;
	private final DataStore ds;

	public ResourceManagerIT() {
		ns = ResourceBuilder.namespace(NAMESPACE_PREFIX).setAttribute(Namespace.URI, NAMESPACE_URI)
				.build();

		Map<String, String> connectionsParams = new HashMap<String, String>();
		connectionsParams.put("uri", NAMESPACE_URI);
		connectionsParams.put("workspaceName", ns.name());
		connectionsParams.put("mappingFileName", APP_SCHEMA_DATASTORE + ".xml");
		ds = ResourceBuilder.dataStore(APP_SCHEMA_DATASTORE, AppSchemaDataStore.class)
				.setAttribute(DataStore.ID, APP_SCHEMA_DATASTORE + "_datastore")
				.setAttribute(DataStore.WORKSPACE_ID, ns.name())
				.setAttribute(DataStore.CONNECTION_PARAMS, connectionsParams).build();
	}

	@BeforeClass
	public static void startGeoServer() throws Exception {
		DockerConfigInstance conf = new DockerConfigInstance("appschema",
				ResourceManagerIT.class.getClassLoader());
		client = new HaleDockerClient(conf);
		client.createContainer();
		client.startContainer();

		String host = client.getHostName();
		if (host == null) {
			// using docker container directly (probably unix socket connection)
			geoserverURL = "http://" + client.getContainerIp() + ":" + 8080 + "/geoserver";
		}
		else {
			geoserverURL = "http://" + host + ":" + client.getHostPort(8080) + "/geoserver";
		}

		waitForGeoServer();
	}

	private static void waitForGeoServer() throws Exception {
		NamespaceManager nsMgr = new NamespaceManager(geoserverURL);
		Namespace ns = ResourceBuilder.namespace("it.geosolutions").build();
		nsMgr.setResource(ns);
		nsMgr.setCredentials(GEOSERVER_USER, GEOSERVER_PASSWORD);

		int num = 0, maxAttempts = 10;
		Exception lastException = null;
		while (num < maxAttempts) {
			try {
				if (nsMgr.exists()) {
					return;
				}
			} catch (Exception e) {
				lastException = e;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		if (lastException != null) {
			throw lastException;
		}
	}

	@AfterClass
	public static void tearDownGeoServer() throws Exception {
		client.killAndRemoveContainer();
	}

	@Before
	public void setUp() throws Exception {
		deleteDataStore();
		createNamespace();
	}

	@After
	public void tearDown() throws Exception {
		deleteDataStore();
		deleteNamespace();
	}

	@Test
	public void testNamespaceManager() throws Exception {
		NamespaceManager nsMgr = createNamespaceManager();

		// check namespace was created correctly
		Document nsDoc = nsMgr.read();
		assertEquals("namespace", nsDoc.getDocumentElement().getNodeName());

		NodeList prefixNodes = nsDoc.getElementsByTagName("prefix");
		assertEquals(1, prefixNodes.getLength());
		assertEquals(NAMESPACE_PREFIX, prefixNodes.item(0).getTextContent());

		NodeList uriNodes = nsDoc.getElementsByTagName("uri");
		assertEquals(1, uriNodes.getLength());
		assertEquals(NAMESPACE_URI, uriNodes.item(0).getTextContent());

		// update namespace URI
		try {
			ns.setAttribute(Namespace.URI, NAMESPACE_URI_ALT);
			nsMgr.update();

			nsDoc = nsMgr.read();
			uriNodes = nsDoc.getElementsByTagName("uri");
			assertEquals(1, uriNodes.getLength());
			assertEquals(NAMESPACE_URI_ALT, uriNodes.item(0).getTextContent());
		} finally {
			// reset namespace URI
			ns.setAttribute(Namespace.URI, NAMESPACE_URI);
		}
	}

	@Test
	public void testDataStoreManager() throws Exception {
		DataStoreManager dsMgr = createDataStoreManager();

		// create datastore
		URL url = dsMgr.create();
		assertNotNull(url);
		assertTrue(dsMgr.exists());

		Document listDoc = dsMgr.list();
		assertEquals("dataStores", listDoc.getDocumentElement().getNodeName());

		NodeList dataStoreNodes = listDoc.getElementsByTagName("dataStore");
		assertEquals(1, dataStoreNodes.getLength());

		NodeList dataStoreChildren = dataStoreNodes.item(0).getChildNodes();
		for (int i = 0; i < dataStoreChildren.getLength(); i++) {
			Node child = dataStoreChildren.item(i);
			if ("name".equals(child.getNodeName())) {
				assertEquals(APP_SCHEMA_DATASTORE, child.getTextContent());
				break;
			}
		}
	}

	@Test
	public void testConfigArchiveUpload() throws Exception {
		// build mapping file resource
		ContentType contentType = DataStoreFile.ZIP_CONTENT_TYPE;
		InputStream is = getClass().getResourceAsStream(APP_SCHEMA_CONFIG_ARCHIVE);
		DataStoreFile configArchive = ResourceBuilder.dataStoreFile(is, contentType)
				.setAttribute(DataStoreFile.EXTENSION, "appschema")
				.setAttribute(DataStoreFile.DATASTORE, ds.name())
				.setAttribute(DataStoreFile.WORKSPACE, ns.name()).build();
		DataStoreFileManager dsFileMgr = new DataStoreFileManager(geoserverURL);
		dsFileMgr.setCredentials(GEOSERVER_USER, GEOSERVER_PASSWORD);
		dsFileMgr.setResource(configArchive);

		// upload mapping configuration (datastore is created implicitly)
		Map<String, String> updateParams = new HashMap<String, String>();
		updateParams.put("configure", "all");
		dsFileMgr.update(updateParams);

		// verify datastore was created
		DataStoreManager dsMgr = createDataStoreManager();
		Document listDoc = dsMgr.list();
		assertEquals("dataStores", listDoc.getDocumentElement().getNodeName());

		NodeList dataStoreNodes = listDoc.getElementsByTagName("dataStore");
		assertEquals(1, dataStoreNodes.getLength());

		NodeList dataStoreChildren = dataStoreNodes.item(0).getChildNodes();
		for (int i = 0; i < dataStoreChildren.getLength(); i++) {
			Node child = dataStoreChildren.item(i);
			if ("name".equals(child.getNodeName())) {
				assertEquals(APP_SCHEMA_DATASTORE, child.getTextContent());
				break;
			}
		}

		// check feature types were created
		FeatureType lcd = ResourceBuilder.featureType("LandCoverDataset").build();
		FeatureType lcu = ResourceBuilder.featureType("LandCoverUnit").build();
		FeatureTypeManager ftMgr = new FeatureTypeManager(geoserverURL);
		ftMgr.setCredentials(GEOSERVER_USER, GEOSERVER_PASSWORD);
		ftMgr.setWorkspace(ns.name());
		ftMgr.setDataStore(ds.name());

		ftMgr.setResource(lcd);
		assertTrue(ftMgr.exists());

		ftMgr.setResource(lcu);
		assertTrue(ftMgr.exists());
	}

	private void createNamespace() throws MalformedURLException {
		NamespaceManager nsMgr = createNamespaceManager();

		if (nsMgr.exists()) {
			nsMgr.delete();
		}

		assertFalse(nsMgr.exists());
		// create namespace
		URL nsURL = nsMgr.create();
		assertNotNull(nsURL);
		assertTrue(nsMgr.exists());
	}

	private NamespaceManager createNamespaceManager() throws MalformedURLException {
		NamespaceManager nsMgr = new NamespaceManager(geoserverURL);
		nsMgr.setCredentials(GEOSERVER_USER, GEOSERVER_PASSWORD);
		nsMgr.setResource(ns);

		return nsMgr;
	}

	private void deleteNamespace() throws MalformedURLException {
		NamespaceManager nsMgr = createNamespaceManager();

		if (nsMgr.exists()) {
			nsMgr.delete();
		}

		assertFalse(nsMgr.exists());
	}

	private DataStoreManager createDataStoreManager() throws MalformedURLException {
		DataStoreManager dsMgr = new DataStoreManager(geoserverURL);
		dsMgr.setCredentials(GEOSERVER_USER, GEOSERVER_PASSWORD);
		dsMgr.setWorkspace(ns.name());
		dsMgr.setResource(ds);

		return dsMgr;
	}

	private void deleteDataStore() throws MalformedURLException {
		DataStoreManager dsMgr = createDataStoreManager();

		if (dsMgr.exists()) {
			Map<String, String> deleteParams = new HashMap<String, String>();
			deleteParams.put("recurse", "true");
			dsMgr.delete(deleteParams);
		}

		assertFalse(dsMgr.exists());
	}
}
