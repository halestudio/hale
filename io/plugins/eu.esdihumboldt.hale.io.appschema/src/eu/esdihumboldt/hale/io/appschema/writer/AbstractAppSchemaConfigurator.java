package eu.esdihumboldt.hale.io.appschema.writer;

import static eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils.resolvePropertyTypes;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.eclipse.core.runtime.content.IContentType;

import com.google.common.io.Files;

import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.ResourceAdvisor;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.ResourceAdvisorExtension;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaIO;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore;
import eu.esdihumboldt.hale.io.appschema.model.FeatureChaining;

/**
 * Base class for HALE alignment to app-schema mapping translators.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public abstract class AbstractAppSchemaConfigurator extends AbstractAlignmentWriter {

	/**
	 * The app-schema mapping generator.
	 */
	protected AppSchemaMappingGenerator generator;

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Translate HALE alignment to App-Schema Configuration",
				ProgressIndicator.UNKNOWN);
		if (getAlignment() == null) {
			throw new IOProviderConfigurationException("No alignment was provided.");
		}
		if (getTargetSchema() == null) {
			throw new IOProviderConfigurationException("No target schema was provided.");
		}
		if (getTarget() == null) {
			throw new IOProviderConfigurationException("No target was provided.");
		}

		DataStore dataStoreParam = getDataStoreParameter();

		FeatureChaining featureChainingParam = getFeatureChainingParameter();
		// resolve property entity definitions here, could't do it on project
		// loading
		resolvePropertyTypes(featureChainingParam, getTargetSchema(), SchemaSpaceID.TARGET);

		generator = new AppSchemaMappingGenerator(getAlignment(), getTargetSchema(),
				dataStoreParam, featureChainingParam);
		try {
			generator.generateMapping(reporter);

			handleMapping(progress, reporter);
		} catch (IOProviderConfigurationException pce) {
			throw pce;
		} catch (Exception e) {
			reporter.error(new IOMessageImpl(e.getMessage(), e));
			reporter.setSuccess(false);
			return reporter;
		}

		progress.end();
		reporter.setSuccess(true);
		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "GeoTools App-Schema Configurator";
	}

	/**
	 * Retrieves the DataStore configuration.
	 * 
	 * @return the configured DataStore
	 */
	protected DataStore getDataStoreParameter() {
		return getParameter(AppSchemaIO.PARAM_DATASTORE).as(DataStore.class);
	}

	/**
	 * Retrieves the FeatureChaining configuration.
	 * 
	 * @return the feature chaining configuration
	 */
	protected FeatureChaining getFeatureChainingParameter() {
		return getParameter(AppSchemaIO.PARAM_CHAINING).as(FeatureChaining.class);
	}

	/**
	 * @return the include schema parameter value
	 */
	protected boolean getIncludeSchemaParameter() {
		Value parameterValue = getParameter(AppSchemaIO.PARAM_INCLUDE_SCHEMA);
		if (Value.NULL.equals(parameterValue)) {
			return false;
		}
		else {
			return parameterValue.as(Boolean.class);
		}
	}

	/**
	 * Template method to be implemented by subclasses. Implementations should
	 * write the generated app-schema mapping to the specified target in the
	 * format specified by the content type parameter.
	 * 
	 * @param progress progress indicator
	 * @param reporter status reporter
	 * @throws IOProviderConfigurationException if an unsupported content type
	 *             has been specified
	 * @throws IOException if an error occurs writing to target
	 */
	protected abstract void handleMapping(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException;

	/**
	 * Copies the target schema to the provided {@link ZipOutputStream}.
	 * 
	 * <p>
	 * Included / imported schemas are also copied and schema locations are
	 * properly updated.
	 * </p>
	 * 
	 * @param zip the zip archive to copy to
	 * @param parentEntry the parent zip entry (may be null)
	 * @param progress the progress indicator
	 * @param reporter the reporter
	 * @throws IOException if an error occurs
	 */
	protected void addTargetSchemaToZip(ZipOutputStream zip, ZipEntry parentEntry,
			ProgressIndicator progress, IOReporter reporter) throws IOException {
		File tmpDir = Files.createTempDir();
		try {
			Map<URI, String> resources = updateTargetSchemaResources(tmpDir, progress, reporter);
			File[] dirs = tmpDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
			for (File dir : dirs) {
				addDirToZip(dir, parentEntry, zip);
			}
			for (Entry<URI, String> resource : resources.entrySet()) {
				String oldSchemaURI = resource.getKey().toString();
				String newSchemaURI = resource.getValue();
				generator.updateSchemaURI(oldSchemaURI, newSchemaURI);
			}
		} finally {
			FileUtils.deleteQuietly(tmpDir);
		}
	}

	// TODO: code adapted from ArchiveProjectWriter: how to avoid duplication?
	private Map<URI, String> updateTargetSchemaResources(File targetDirectory,
			ProgressIndicator progress, IOReporter reporter) throws IOException {
		progress.begin("Copy resources", ProgressIndicator.UNKNOWN);

		Project project = (Project) getProjectInfo();
		// resource locations mapped to new resource path
		Map<URI, String> handledResources = new HashMap<>();
		try {
			List<IOConfiguration> resources = project.getResources();
			// every resource needs his own directory
			int count = 0;

			Iterator<IOConfiguration> iter = resources.iterator();
			while (iter.hasNext()) {
				IOConfiguration resource = iter.next();

				if (resource.getActionId().equals(SchemaIO.ACTION_LOAD_TARGET_SCHEMA)) {
					// get resource path
					Map<String, Value> providerConfig = resource.getProviderConfiguration();
					String path = providerConfig.get(ImportProvider.PARAM_SOURCE).toString();

					URI pathUri;
					try {
						pathUri = new URI(path);
					} catch (URISyntaxException e1) {
						reporter.error(new IOMessageImpl(
								"Skipped resource because of invalid URI: " + path, e1));
						continue;
					}

					// check if path was already handled
					if (handledResources.containsKey(pathUri)) {
						// skip copying the resource
						continue;
					}

					String scheme = pathUri.getScheme();
					LocatableInputSupplier<? extends InputStream> input = null;
					if (scheme != null) {
						if (scheme.equals("http") || scheme.equals("https")
								|| scheme.equals("file") || scheme.equals("platform")
								|| scheme.equals("bundle") || scheme.equals("jar")) {
							input = new DefaultInputSupplier(pathUri);
						}
						else {
							continue;
						}
					}
					else {
						// now can't open that, can we?
						reporter.error(new IOMessageImpl(
								"Skipped resource because it cannot be loaded from "
										+ pathUri.toString(), null));
						continue;
					}

					progress.setCurrentTask("Copying resource at " + path);

					// every resource file is copied into an own resource
					// directory in the target directory
					String resourceFolder = "_schemas";
					if (count > 0) {
						resourceFolder += count;
					}
					File newDirectory = new File(targetDirectory, resourceFolder);
					try {
						newDirectory.mkdir();
					} catch (SecurityException e) {
						throw new IOException(
								"Can not create directory " + newDirectory.toString(), e);
					}

					// the filename
					String name = path.toString().substring(path.lastIndexOf("/") + 1,
							path.length());

					// remove any query string from the filename
					int queryIndex = name.indexOf('?');
					if (queryIndex >= 0) {
						name = name.substring(0, queryIndex);
					}

					if (name.isEmpty()) {
						name = "file";
					}

					File newFile = new File(newDirectory, name);
					Path target = newFile.toPath();

					// retrieve the resource advisor
					Value ct = providerConfig.get(ImportProvider.PARAM_CONTENT_TYPE);
					IContentType contentType = null;
					if (ct != null) {
						contentType = HalePlatform.getContentTypeManager().getContentType(
								ct.as(String.class));
					}
					ResourceAdvisor ra = ResourceAdvisorExtension.getInstance().getAdvisor(
							contentType);

					// copy the resource
					progress.setCurrentTask("Copying resource at " + path);
					ra.copyResource(input, target, contentType, true, reporter);

					// store new path for resource
					String newPath = resourceFolder + "/" + name;
					handledResources.put(pathUri, newPath);
					count++;
				}
			}
		} finally {
			progress.end();
		}

		return handledResources;
	}

	private void addDirToZip(File inputDir, ZipEntry parentEntry, ZipOutputStream zos)
			throws IOException {
		String parentEntryName = (parentEntry != null) ? parentEntry.getName() : null;
		String currentDirEntryName = (parentEntryName != null) ? parentEntryName : "";
		currentDirEntryName += inputDir.getName() + "/";
		ZipEntry currentDirEntry = new ZipEntry(currentDirEntryName);
		zos.putNextEntry(currentDirEntry);

		// add files
		File[] files = inputDir.listFiles((FileFilter) FileFileFilter.FILE);
		for (File f : files) {
			zos.putNextEntry(new ZipEntry(currentDirEntry.getName() + f.getName()));
			Files.copy(f, zos);
			zos.closeEntry();
		}

		// add sub-directories
		File[] dirs = inputDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
		for (File d : dirs) {
			addDirToZip(d, currentDirEntry, zos);
		}
	}

}
