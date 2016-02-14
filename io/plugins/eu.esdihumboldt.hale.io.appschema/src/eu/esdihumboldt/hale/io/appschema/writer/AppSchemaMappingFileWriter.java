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

import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.DATASTORE_FILE;
import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.FEATURETYPE_FILE;
import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.LAYER_FILE;
import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.NAMESPACE_FILE;
import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.WORKSPACE_FILE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.io.ByteStreams;

import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;
import eu.esdihumboldt.hale.io.geoserver.DataStore;
import eu.esdihumboldt.hale.io.geoserver.FeatureType;
import eu.esdihumboldt.hale.io.geoserver.Layer;
import eu.esdihumboldt.hale.io.geoserver.Namespace;
import eu.esdihumboldt.hale.io.geoserver.Workspace;

/**
 * Writes the generated app-schema configuration to file.
 * 
 * <p>
 * If the provider's content type is set to
 * <code>eu.esdihumboldt.hale.io.appschema.mapping</code> , only the mapping
 * file is written.
 * <p>
 * If content type is set to
 * <code>eu.esdihumboldt.hale.io.appschema.archive</code>, a ZIP archive
 * containing both the mapping file and all other necessary configuration files
 * is written (secondary namespaces, datastore configuration file, etc.). The
 * archive must be uncompressed in GeoServer's data directory to publish the
 * app-schema datastore.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class AppSchemaMappingFileWriter extends AbstractAppSchemaConfigurator {

	private static final String DEFAULT_CONTENT_TYPE_ID = AppSchemaIO.CONTENT_TYPE_MAPPING;

	/**
	 * @see eu.esdihumboldt.hale.io.appschema.writer.AbstractAppSchemaConfigurator#handleMapping(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected void handleMapping(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		if (getContentType() == null) {
			// contentType was not specified, use default (mapping file)
			setContentType(HalePlatform.getContentTypeManager().getContentType(
					DEFAULT_CONTENT_TYPE_ID));
		}

		if (getContentType().getId().equals(AppSchemaIO.CONTENT_TYPE_MAPPING)) {
			writeMappingFile();
		}
		else if (getContentType().getId().equals(AppSchemaIO.CONTENT_TYPE_ARCHIVE)) {
			writeArchive(progress, reporter);
		}
		else {
			throw new IOProviderConfigurationException("Unsupported content type: "
					+ getContentType().getName());
		}
	}

	private void writeMappingFile() throws IOException {
		OutputStream out = getTarget().getOutput();
		generator.writeMappingConf(out);
		out.flush();

		if (generator.getGeneratedMapping().requiresMultipleFiles()) {
			try (OutputStream includedTypesOut = new FileOutputStream(getIncludedTypesFile())) {
				generator.writeIncludedTypesMappingConf(includedTypesOut);
			}
		}
	}

	private File getIncludedTypesFile() {
		String parentDir = new File(getTarget().getLocation()).getParent();
		File inclTypesMappingFile = new File(parentDir, AppSchemaIO.INCLUDED_TYPES_MAPPING_FILE);

		return inclTypesMappingFile;
	}

	private void writeArchive(ProgressIndicator progress, IOReporter reporter) throws IOException {
		Workspace ws = generator.getMainWorkspace();
		Namespace mainNs = generator.getMainNamespace();
		DataStore ds = generator.getAppSchemaDataStore();

		// save to archive
		final ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(getTarget()
				.getOutput()));
		// add workspace folder
		ZipEntry workspaceFolder = new ZipEntry(ws.getAttribute(Workspace.NAME) + "/");
		zip.putNextEntry(workspaceFolder);
		// add workspace file
		zip.putNextEntry(new ZipEntry(workspaceFolder.getName() + WORKSPACE_FILE));
		copyAndCloseInputStream(ws.asStream(), zip);
		zip.closeEntry();
		// add namespace file
		zip.putNextEntry(new ZipEntry(workspaceFolder.getName() + NAMESPACE_FILE));
		copyAndCloseInputStream(mainNs.asStream(), zip);
		zip.closeEntry();
		// add datastore folder
		ZipEntry dataStoreFolder = new ZipEntry(workspaceFolder.getName()
				+ ds.getAttribute(DataStore.NAME) + "/");
		zip.putNextEntry(dataStoreFolder);
		// add datastore file
		zip.putNextEntry(new ZipEntry(dataStoreFolder.getName() + DATASTORE_FILE));
		copyAndCloseInputStream(ds.asStream(), zip);
		zip.closeEntry();
		// add target schema to zip
		if (getIncludeSchemaParameter()) {
			addTargetSchemaToZip(zip, dataStoreFolder, progress, reporter);
		}
		// add main mapping file
		Map<String, String> connectionParams = ds.getConnectionParameters();
		zip.putNextEntry(new ZipEntry(dataStoreFolder.getName()
				+ connectionParams.get("mappingFileName")));
		generator.writeMappingConf(zip);
		zip.closeEntry();
		// add included types mapping file, if necessary
		if (generator.getGeneratedMapping().requiresMultipleFiles()) {
			zip.putNextEntry(new ZipEntry(dataStoreFolder.getName()
					+ AppSchemaIO.INCLUDED_TYPES_MAPPING_FILE));
			generator.writeIncludedTypesMappingConf(zip);
			zip.closeEntry();
		}

		// add feature type entries
		List<FeatureType> featureTypes = generator.getFeatureTypes();
		for (FeatureType ft : featureTypes) {
			Layer layer = generator.getLayer(ft);

			// add feature type folder
			ZipEntry featureTypeFolder = new ZipEntry(dataStoreFolder.getName()
					+ ft.getAttribute(FeatureType.NAME) + "/");
			zip.putNextEntry(featureTypeFolder);
			// add feature type file
			zip.putNextEntry(new ZipEntry(featureTypeFolder.getName() + FEATURETYPE_FILE));
			copyAndCloseInputStream(ft.asStream(), zip);
			zip.closeEntry();
			// add layer file
			zip.putNextEntry(new ZipEntry(featureTypeFolder.getName() + LAYER_FILE));
			copyAndCloseInputStream(layer.asStream(), zip);
			zip.closeEntry();
		}

		// add secondary namespaces
		List<Namespace> secondaryNamespaces = generator.getSecondaryNamespaces();
		for (Namespace secNs : secondaryNamespaces) {
			Workspace secWs = generator.getWorkspace(secNs);

			// add workspace folder
			ZipEntry secondaryWorkspaceFolder = new ZipEntry(secWs.name() + "/");
			zip.putNextEntry(secondaryWorkspaceFolder);
			// add workspace file
			zip.putNextEntry(new ZipEntry(secondaryWorkspaceFolder.getName() + WORKSPACE_FILE));
			copyAndCloseInputStream(secWs.asStream(), zip);
			zip.closeEntry();
			// add namespace file
			zip.putNextEntry(new ZipEntry(secondaryWorkspaceFolder.getName() + NAMESPACE_FILE));
			copyAndCloseInputStream(secNs.asStream(), zip);
			zip.closeEntry();
		}

		zip.close();
	}

	private void copyAndCloseInputStream(InputStream from, OutputStream to) throws IOException {
		try {
			ByteStreams.copy(from, to);
		} finally {
			try {
				from.close();
			} catch (IOException e) {
				// ignore
			}

		}
	}
}
