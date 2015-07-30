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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.esdihumboldt.hale.io.geoserver.AppSchemaDataStore;
import eu.esdihumboldt.hale.io.geoserver.DataStore;
import eu.esdihumboldt.hale.io.geoserver.DataStoreFile;
import eu.esdihumboldt.hale.io.geoserver.DataStoreFile.Extension;
import eu.esdihumboldt.hale.io.geoserver.Namespace;
import eu.esdihumboldt.hale.io.geoserver.ResourceBuilder;

@SuppressWarnings("javadoc")
public class ResourceManagerTest {

	// TODO: make this configurable
	private static final String GEOSERVER_URL = "http://localhost:8080/geoserver";
	private static final String GEOSERVER_USER = "admin";
	private static final String GEOSERVER_PASSWORD = "geoserver";

	private static final String NAMESPACE_PREFIX = "hale";
	private static final String NAMESPACE_URI = "http://www.esdi-community.eu/projects/hale";
	private static final String NAMESPACE_URI_ALT = "http://www.esdi-community.eu/projects/hale_alt";

	private static final String APP_SCHEMA_MAPPING_FILE = "/data/LandCoverVector.xml";
	private static final String APP_SCHEMA_DATASTORE = "LandCoverVector";
	private static final String APP_SCHEMA_WORKSPACE = "hale_lcv";
	private static final String APP_SCHEMA_URI = "http://inspire.ec.europa.eu/schemas/lcv/3.0";

	@Test
	@Ignore("Requires a live GeoServer instance to run")
	public void testNamespaceManager() throws Exception {

		NamespaceManager nsMgr = new NamespaceManager(GEOSERVER_URL);
		Namespace ns = createNamespace(nsMgr, NAMESPACE_PREFIX, NAMESPACE_URI);

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
		ns.setAttribute(Namespace.URI, NAMESPACE_URI_ALT);
		nsMgr.update();

		nsDoc = nsMgr.read();
		uriNodes = nsDoc.getElementsByTagName("uri");
		assertEquals(1, uriNodes.getLength());
		assertEquals(NAMESPACE_URI_ALT, uriNodes.item(0).getTextContent());

		// delete namespace
		nsMgr.delete();
		assertFalse(nsMgr.exists());
	}

	@Test
	@Ignore("Requires a live GeoServer instance to run")
	public void testDataStoreManager() throws Exception {

		NamespaceManager nsMgr = new NamespaceManager(GEOSERVER_URL);
		Namespace ns = createNamespace(nsMgr, APP_SCHEMA_WORKSPACE, APP_SCHEMA_URI);

		Map<String, String> connectionsParams = new HashMap<String, String>();
		connectionsParams.put("uri", APP_SCHEMA_URI);
		connectionsParams.put("workspaceName", ns.name());
		connectionsParams.put("mappingFileName", "mapping.xml");
		DataStore ds = ResourceBuilder.dataStore(APP_SCHEMA_DATASTORE, AppSchemaDataStore.class)
				.setAttribute(DataStore.CONNECTION_PARAMS, connectionsParams).build();

		DataStoreManager dsMgr = new DataStoreManager(GEOSERVER_URL);
		dsMgr.setCredentials(GEOSERVER_USER, GEOSERVER_PASSWORD);
		dsMgr.setWorkspace(APP_SCHEMA_WORKSPACE);
		dsMgr.setResource(ds);

		if (dsMgr.exists()) {
			dsMgr.delete();
		}

		assertFalse(dsMgr.exists());
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

		Map<String, String> deleteParams = new HashMap<String, String>();
		deleteParams.put("recurse", "true");
		dsMgr.delete(deleteParams);
		assertFalse(dsMgr.exists());

		// delete namespace
		nsMgr.delete();
		assertFalse(nsMgr.exists());

	}

	@Test
	@Ignore("Requires a live GeoServer instance to run")
	public void testDataStoreFileManager() throws Exception {

		NamespaceManager nsMgr = new NamespaceManager(GEOSERVER_URL);
		@SuppressWarnings("unused")
		Namespace ns = createNamespace(nsMgr, APP_SCHEMA_WORKSPACE, APP_SCHEMA_URI);

		InputStream resourceStream = getClass().getResourceAsStream(APP_SCHEMA_MAPPING_FILE);
		assertNotNull(resourceStream);
		DataStoreFile mappingFile = ResourceBuilder.dataStoreFile(resourceStream)
				.setAttribute(DataStoreFile.WORKSPACE, APP_SCHEMA_WORKSPACE)
				.setAttribute(DataStoreFile.DATASTORE, APP_SCHEMA_DATASTORE)
				.setAttribute(DataStoreFile.EXTENSION, Extension.appschema.name()).build();
		DataStoreFileManager dsFileMgr = new DataStoreFileManager(GEOSERVER_URL);
		dsFileMgr.setCredentials(GEOSERVER_USER, GEOSERVER_PASSWORD);
		dsFileMgr.setResource(mappingFile);

		// upload mapping file (datastore is created implicitly)
		Map<String, String> updateParameters = new HashMap<String, String>();
		updateParameters.put("configure", "all");
		dsFileMgr.update(updateParameters);

		assertTrue(dsFileMgr.exists());

		// delete datastore
		DataStore ds = ResourceBuilder.dataStore(APP_SCHEMA_DATASTORE, AppSchemaDataStore.class)
				.build();
		DataStoreManager dsMgr = new DataStoreManager(GEOSERVER_URL);
		dsMgr.setCredentials(GEOSERVER_USER, GEOSERVER_PASSWORD);
		dsMgr.setWorkspace(APP_SCHEMA_WORKSPACE);
		dsMgr.setResource(ds);

		assertTrue(dsMgr.exists());
		Map<String, String> deleteParams = new HashMap<String, String>();
		deleteParams.put("recurse", "true");
		dsMgr.delete(deleteParams);
		assertFalse(dsMgr.exists());

		// delete namespace
		nsMgr.delete();
		assertFalse(nsMgr.exists());
	}

	private Namespace createNamespace(NamespaceManager nsMgr, String prefix, String uri) {
		Namespace ns = ResourceBuilder.namespace(prefix).setAttribute(Namespace.URI, uri).build();
		nsMgr.setCredentials(GEOSERVER_USER, GEOSERVER_PASSWORD);
		nsMgr.setResource(ns);

		if (nsMgr.exists()) {
			nsMgr.delete();
		}

		assertFalse(nsMgr.exists());
		// create namespace
		URL nsURL = nsMgr.create();
		assertNotNull(nsURL);
		assertTrue(nsMgr.exists());

		return ns;
	}

}
