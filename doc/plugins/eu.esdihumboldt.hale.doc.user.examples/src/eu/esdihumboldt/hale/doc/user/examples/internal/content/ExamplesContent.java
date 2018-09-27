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

package eu.esdihumboldt.hale.doc.user.examples.internal.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.apache.velocity.VelocityContext;
import org.eclipse.help.IHelpContentProducer;
import org.osgi.framework.Version;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.fhg.igd.slf4jplus.ATransaction;
import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.align.io.AlignmentWriter;
import eu.esdihumboldt.hale.common.align.io.impl.LoadAlignmentAdvisor;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaIO;
import eu.esdihumboldt.hale.common.schema.io.impl.LoadSchemaAdvisor;
import eu.esdihumboldt.hale.doc.user.examples.internal.ExamplesConstants;
import eu.esdihumboldt.hale.doc.user.examples.internal.extension.ExampleProject;
import eu.esdihumboldt.hale.doc.user.examples.internal.extension.ExampleProjectExtension;
import eu.esdihumboldt.hale.doc.util.content.AbstractVelocityContent;

/**
 * Examples content producer.
 * 
 * @author Simon Templer
 */
public class ExamplesContent extends AbstractVelocityContent implements ExamplesConstants {

	private static final ALogger log = ALoggerFactory.getLogger(ExamplesContent.class);

	private static final String TEMPLATE_OVERVIEW = "overview";

	private static final String TEMPLATE_PROJECT = "project";

	/**
	 * Provider ID of the mapping exporter XXX instead store the descriptor?
	 */
	private static final String ID_MAPPING_EXPORT = "eu.esdihumboldt.hale.io.html.svg.mapping";

	private File tempMappingDir;

	private AlignmentWriter mappingDocExport;

	/**
	 * States if {@link #mappingDocExport} was already initialized (or tried to)
	 */
	private boolean mappingDocExportInitialized = false;

	/**
	 * @see IHelpContentProducer#getInputStream(String, String, Locale)
	 */
	@Override
	public InputStream getInputStream(String pluginID, String href, Locale locale) {
		ATransaction trans = log.begin("Generating examples help content");
		try {
			if (href.startsWith(PATH_PREFIX_PROJECT)) {
				// references a project

				// determine the project id
				String projectId = href.substring(PATH_PREFIX_PROJECT.length());
				// strip everything after a ?
				int ind = projectId.indexOf('?');
				if (ind >= 0) {
					projectId = projectId.substring(0, ind);
				}

				// strip the .*htm? ending
				if (projectId.endsWith("html") || projectId.endsWith("htm")) {
					projectId = projectId.substring(0, projectId.lastIndexOf('.'));
				}

				if (projectId.endsWith(PATH_SUFFIX_MAPPINGDOC)) {
					projectId = projectId.substring(0,
							projectId.length() - PATH_SUFFIX_MAPPINGDOC.length());
					return getMappingContent(projectId);
				}

				ExampleProject project = ExampleProjectExtension.getInstance().get(projectId);
				if (project != null) {
					return getProjectContent(project);
				}

				// Auxiliary mapping documentation files
				ind = projectId.indexOf('/'); // XXX no / may be contained in
												// project id
				String path = projectId.substring(ind + 1);
				projectId = projectId.substring(0, ind);
				return getMappingFileContent(projectId, path);
			}
			else if (href.startsWith(PATH_OVERVIEW)) {
				return getOverviewContent();
			}
		} finally {
			trans.end();
		}

		return null;
	}

	/**
	 * Create the overview content.
	 * 
	 * @return the overview page content
	 */
	private InputStream getOverviewContent() {
		try {
			return getContentFromTemplate("overview", TEMPLATE_OVERVIEW,
					new Callable<VelocityContext>() {

						@Override
						public VelocityContext call() throws Exception {
							VelocityContext context = new VelocityContext();

							context.put("projects",
									ExampleProjectExtension.getInstance().getElements());

							return context;
						}
					});
		} catch (Exception e) {
			log.error("Error creating example project overview", e);
			return null;
		}
	}

	/**
	 * Get the project page content.
	 * 
	 * @param project the project
	 * @return the project page content
	 */
	private InputStream getProjectContent(final ExampleProject project) {
		try {
			return getContentFromTemplate(project.getId(), TEMPLATE_PROJECT,
					new Callable<VelocityContext>() {

						@Override
						public VelocityContext call() throws Exception {
							VelocityContext context = new VelocityContext();

							context.put("project", project);

							return context;
						}
					});
		} catch (Exception e) {
			log.error("Error creating project page", e);
			return null;
		}
	}

	/**
	 * @see AbstractVelocityContent#getTemplate(String)
	 */
	@Override
	protected InputStream getTemplate(String templateId) throws Exception {
		if (TEMPLATE_PROJECT.equals(templateId)) {
			return ExamplesContent.class.getResourceAsStream("project.html");
		}
		return ExamplesContent.class.getResourceAsStream(PATH_OVERVIEW);
	}

	/**
	 * Get the content of a auxiliary mapping file.
	 * 
	 * @param projectId the project id
	 * @param path the file path
	 * @return the file content or <code>null</code>
	 */
	private InputStream getMappingFileContent(String projectId, String path) {
		if (tempMappingDir == null) {
			return null;
		}

		File candidate = new File(tempMappingDir, path);
		if (candidate.exists()) {
			try {
				return new FileInputStream(candidate);
			} catch (FileNotFoundException e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * Get the mapping documentation content for an example project.
	 * 
	 * @param projectId the project ID
	 * @return the mapping documentation content stream or <code>null</code>
	 */
	private InputStream getMappingContent(String projectId) {
		if (!mappingDocExportInitialized) {
			mappingDocExport = HaleIO.createIOProvider(AlignmentWriter.class, null,
					ID_MAPPING_EXPORT);
			if (mappingDocExport == null) {
				log.error("Could not create mapping documentation exporter.");
			}

			mappingDocExportInitialized = true;
		}

		if (mappingDocExport == null) {
			// no mapping documentation export possible
			return null;
		}

		if (tempMappingDir == null) {
			tempMappingDir = Files.createTempDir();
			tempMappingDir.deleteOnExit();
		}

		// the file of the mapping documentation
		File mappingDoc = new File(tempMappingDir, projectId + ".html");

		if (!mappingDoc.exists()) {
			ATransaction trans = log.begin("Generate example mapping documentation");
			try {
				// create the mapping documentation

				ExampleProject exampleProject = ExampleProjectExtension.getInstance()
						.get(projectId);
				final Project project = (Project) exampleProject.getInfo();

				// determine alignment location - contained in project file, not
				// a resource
				URI alignmentLoc = exampleProject.getAlignmentLocation();
				if (alignmentLoc == null) {
					// no alignment present
					return null;
				}

				// store configurations per action ID
				Multimap<String, IOConfiguration> confs = HashMultimap.create();
				for (IOConfiguration conf : project.getResources()) {
					confs.put(conf.getActionId(), conf);
				}

				// load schemas
				// source schemas
				LoadSchemaAdvisor source = new LoadSchemaAdvisor(SchemaSpaceID.SOURCE);
				for (IOConfiguration conf : confs.get(SchemaIO.ACTION_LOAD_SOURCE_SCHEMA)) {
					source.setConfiguration(conf);
					executeProvider(source, conf.getProviderId(), null);
				}
				// target schemas
				LoadSchemaAdvisor target = new LoadSchemaAdvisor(SchemaSpaceID.TARGET);
				for (IOConfiguration conf : confs.get(SchemaIO.ACTION_LOAD_TARGET_SCHEMA)) {
					target.setConfiguration(conf);
					executeProvider(target, conf.getProviderId(), null);
				}

				// load alignment
				// manual loading needed, as we can't rely on the environment
				// alignment advisor
				DefaultInputSupplier alignmentIn = new DefaultInputSupplier(alignmentLoc);
				AlignmentReader reader = HaleIO.findIOProvider(AlignmentReader.class, alignmentIn,
						alignmentLoc.getPath());
				LoadAlignmentAdvisor alignmentAdvisor = new LoadAlignmentAdvisor(null,
						source.getSchemaSpace(), target.getSchemaSpace(),
						exampleProject.getUpdater());
				reader.setSource(alignmentIn);
				executeProvider(alignmentAdvisor, null, reader);
				Alignment alignment = alignmentAdvisor.getAlignment();

				if (alignment != null) {
					// save alignment docu
					synchronized (mappingDocExport) { // only a single instance
						mappingDocExport.setAlignment(alignment);
						mappingDocExport.setTarget(new FileIOSupplier(mappingDoc));
						if (mappingDocExport instanceof ProjectInfoAware) {
							ProjectInfo smallInfo = new ProjectInfo() {

								@Override
								public String getName() {
									return project.getName();
								}

								@Override
								public Date getModified() {
									return null;
								}

								@Override
								public Version getHaleVersion() {
									return null;
								}

								@Override
								public String getDescription() {
									return project.getDescription();
								}

								@Override
								public Date getCreated() {
									return null;
								}

								@Override
								public String getAuthor() {
									return project.getAuthor();
								}

								@Override
								public Value getProperty(String name) {
									return Value.NULL;
								}
							};
							((ProjectInfoAware) mappingDocExport).setProjectInfo(smallInfo); // project);
						}
						mappingDocExport.execute(null);
					}

					mappingDoc.deleteOnExit();
				}
			} catch (Throwable e) {
				log.error("Error generating mapping documentation for example project", e);
				return null;
			} finally {
				trans.end();
			}
		}

		if (mappingDoc.exists()) {
			try {
				return new FileInputStream(mappingDoc);
			} catch (FileNotFoundException e) {
				return null;
			}
		}
		else
			return null;
	}

	/**
	 * Execute the I/O provider given or specified by the given provider ID.
	 * 
	 * @param advisor the advisor to use for configuration of the provider and
	 *            handling the results
	 * @param providerId the ID of the provider to execute, may be
	 *            <code>null</code> if provider is set
	 * @param provider the provider to execute
	 * @throws Exception if executing the provider fails or if a provider with
	 *             the given ID is not found
	 */
	@SuppressWarnings("unchecked")
	private void executeProvider(@SuppressWarnings("rawtypes") IOAdvisor advisor, String providerId,
			IOProvider provider) throws Exception {
		if (provider == null) {
			// find and create the provider
			IOProviderDescriptor descriptor = IOProviderExtension.getInstance()
					.getFactory(providerId);
			if (descriptor != null) {
				provider = descriptor.createExtensionObject();
			}
			else {
				throw new IllegalStateException(
						"I/O provider with ID " + providerId + " not found");
			}
		}

		// use advisor to configure provider
		advisor.prepareProvider(provider);
		advisor.updateConfiguration(provider);

		// execute
		IOReport report = provider.execute(null);

		// handle results
		if (report.isSuccess()) {
			advisor.handleResults(provider);
		}
	}

}
