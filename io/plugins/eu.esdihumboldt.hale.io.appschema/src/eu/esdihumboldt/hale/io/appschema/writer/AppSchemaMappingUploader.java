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

package eu.esdihumboldt.hale.io.appschema.writer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.entity.ContentType;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;
import eu.esdihumboldt.hale.io.geoserver.DataStore;
import eu.esdihumboldt.hale.io.geoserver.DataStoreFile;
import eu.esdihumboldt.hale.io.geoserver.Namespace;
import eu.esdihumboldt.hale.io.geoserver.ResourceBuilder;
import eu.esdihumboldt.hale.io.geoserver.Workspace;
import eu.esdihumboldt.hale.io.geoserver.rest.DataStoreFileManager;
import eu.esdihumboldt.hale.io.geoserver.rest.DataStoreManager;
import eu.esdihumboldt.hale.io.geoserver.rest.NamespaceManager;
import eu.esdihumboldt.hale.io.geoserver.rest.ResourceException;

/**
 * Uploads the generated app-schema mapping configuration to a GeoServer
 * instance using its REST API.
 * <p>
 * The current implementation checks for the existence of namespaces/workspaces
 * and does not create nor update them if they already exist. On the contrary,
 * if the target app-schema datastore already exists, it is destroyed and then
 * re-created.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class AppSchemaMappingUploader extends AbstractAppSchemaConfigurator {

	private URL geoserverURL;
	private String username;
	private String password;
	private boolean includeTargetSchema = false;

	/**
	 * @see eu.esdihumboldt.hale.io.appschema.writer.AbstractAppSchemaConfigurator#handleMapping(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected void handleMapping(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		LocatableOutputSupplier<? extends OutputStream> target = getTarget();

		geoserverURL = target.getLocation().toURL();
		username = getParameter(AppSchemaIO.PARAM_USER).as(String.class);
		password = getParameter(AppSchemaIO.PARAM_PASSWORD).as(String.class);
		includeTargetSchema = getIncludeSchemaParameter();

		publishNamespaces();

		publishAppSchemaDataStore(progress, reporter);
	}

	private void publishNamespaces() {
		NamespaceManager nsMgr = new NamespaceManager(geoserverURL);
		nsMgr.setCredentials(username, password);

		// check whether main namespace/workspace exists; if not, create it
		Namespace mainNs = generator.getMainNamespace();
		createNamespaceIfRequired(nsMgr, mainNs);

		// check whether secondary namespaces/workspaces exist; if not, create
		// them
		List<Namespace> secondaryNamespaces = generator.getSecondaryNamespaces();
		for (Namespace ns : secondaryNamespaces) {
			createNamespaceIfRequired(nsMgr, ns);
		}
	}

	private void createNamespaceIfRequired(NamespaceManager nsMgr, Namespace ns) {
		nsMgr.setResource(ns);
		if (!nsMgr.exists()) {
			nsMgr.create();
		}
		else {
			// check whether the attributes of the existent namespace match
			// those of the one being created; throw exeption if they don't
			Namespace existing = Namespace.fromDocument(nsMgr.read());
			throwIfAttributesDontMatch(ns, existing);
		}
	}

	private void throwIfAttributesDontMatch(Namespace nsNew, Namespace nsExisting) {
		Object newUri = nsNew.getAttribute(Namespace.URI);
		Object existingUri = nsExisting.getAttribute(Namespace.URI);
		if (!newUri.equals(existingUri)) {
			throw new ResourceException("Namespace \"" + nsNew.name() + "\""
					+ " exists, but its URI is \"" + existingUri + "\" instead of \"" + newUri
					+ "\"");
		}

		Object newIsolatedAttr = nsNew.getAttribute(Namespace.ISOLATED);
		Object existingIsolatedAttr = nsExisting.getAttribute(Namespace.ISOLATED);
		if (!newIsolatedAttr.equals(existingIsolatedAttr)) {
			// isolated attributes don't match, play it safe and throw exception
			throw new ResourceException("Namespace \"" + nsNew.name()
					+ " exists, but the value of its \"isolated\" attribute is \""
					+ existingIsolatedAttr + "\" instead of \"" + newIsolatedAttr + "\"");
		}
	}

	private void publishAppSchemaDataStore(ProgressIndicator progress, IOReporter reporter)
			throws IOException {
		Workspace ws = generator.getMainWorkspace();

		// build datastore resource
		DataStore dataStore = generator.getAppSchemaDataStore();
		DataStoreManager dsMgr = new DataStoreManager(geoserverURL);
		dsMgr.setCredentials(username, password);
		dsMgr.setResource(dataStore);
		dsMgr.setWorkspace(ws.name());
		// remove datastore, if necessary
		if (dsMgr.exists()) {
			Map<String, String> deleteParams = new HashMap<String, String>();
			deleteParams.put("recurse", "true");

			dsMgr.delete(deleteParams);
		}

		// build mapping file resource
		ContentType contentType = getMimeType();
		byte[] content = writeContent(dataStore.name(), contentType, progress, reporter);
		DataStoreFile mappingFile = ResourceBuilder
				.dataStoreFile(new ByteArrayInputStream(content), contentType)
				.setAttribute(DataStoreFile.EXTENSION, "appschema")
				.setAttribute(DataStoreFile.DATASTORE, dataStore.name())
				.setAttribute(DataStoreFile.WORKSPACE, ws.name()).build();
		DataStoreFileManager dsFileMgr = new DataStoreFileManager(geoserverURL);
		dsFileMgr.setCredentials(username, password);
		dsFileMgr.setResource(mappingFile);

		Map<String, String> updateParams = new HashMap<String, String>();
		updateParams.put("configure", "all");
		dsFileMgr.update(updateParams);
	}

	private ContentType getMimeType() {
		if (generator.getGeneratedMapping().requiresMultipleFiles() || includeTargetSchema) {
			return DataStoreFile.ZIP_CONTENT_TYPE;
		}
		else {
			return DataStoreFile.DEF_CONTENT_TYPE;
		}
	}

	private byte[] writeContent(String mappingFileName, ContentType contentType,
			ProgressIndicator progress, IOReporter reporter) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			if (contentType.equals(DataStoreFile.ZIP_CONTENT_TYPE)) {
				try (ZipOutputStream zos = new ZipOutputStream(bos)) {
					if (includeTargetSchema) {
						// add target schema to zip
						addTargetSchemaToZip(zos, null, progress, reporter);
					}
					// main mapping configuration file
					zos.putNextEntry(new ZipEntry(mappingFileName + ".appschema"));
					generator.writeMappingConf(zos);
					zos.closeEntry();
					if (generator.getGeneratedMapping().requiresMultipleFiles()) {
						zos.putNextEntry(new ZipEntry(AppSchemaIO.INCLUDED_TYPES_MAPPING_FILE));
						generator.writeIncludedTypesMappingConf(zos);
						zos.closeEntry();
					}
				}
			}
			else {
				generator.writeMappingConf(bos);
			}

			return bos.toByteArray();
		}
	}
}
