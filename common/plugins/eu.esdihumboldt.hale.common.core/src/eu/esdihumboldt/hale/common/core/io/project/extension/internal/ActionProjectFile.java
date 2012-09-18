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

package eu.esdihumboldt.hale.common.core.io.project.extension.internal;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOAction;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOActionExtension;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorExtension;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

/**
 * Project file based on an I/O action
 * 
 * @author Simon Templer
 */
public class ActionProjectFile implements ProjectFile {

	private static final ALogger log = ALoggerFactory.getLogger(ActionProjectFile.class);

	private final Map<String, String> saveParameters;
	private final String saveProviderId;
	private final Map<String, String> loadParameters;
	private final String loadProviderId;
	private final String loadActionId;
	private final String saveActionId;

	private File applyFile;

	/**
	 * Create a project file based on an I/O action
	 * 
	 * @param loadActionId the action identifier for loading the file
	 * @param loadProviderId the provider identifier to use for loading the
	 *            file, may be <code>null</code> to use auto-detection
	 * @param loadParameters the parameters for the I/O provider used for
	 *            loading the file
	 * @param saveActionId the action identifier for saving the file
	 * @param saveProviderId the provider identifier to use for saving the file
	 * @param saveParameters the parameters for the I/O provider used for saving
	 *            the file
	 */
	public ActionProjectFile(String loadActionId, String loadProviderId,
			Map<String, String> loadParameters, String saveActionId, String saveProviderId,
			Map<String, String> saveParameters) {
		this.loadActionId = loadActionId;
		this.loadProviderId = loadProviderId;
		this.loadParameters = loadParameters;
		this.saveActionId = saveActionId;
		this.saveProviderId = saveProviderId;
		this.saveParameters = saveParameters;
	}

	/**
	 * @see ProjectFile#load(InputStream)
	 */
	@Override
	public void load(InputStream in) throws Exception {
		if (applyFile != null && !applyFile.delete()) {
			applyFile.deleteOnExit();
		}

		// direct the stream to a temporary file
		File tmpFile = File.createTempFile("project-file", null);
		OutputStream out = new FileOutputStream(tmpFile);
		try {
			IOUtils.copy(in, out);
			out.flush();
		} finally {
			out.close();
			in.close();
		}

		applyFile = tmpFile;
	}

	/**
	 * @see ProjectFile#apply()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void apply() {
		if (applyFile == null) {
			return;
		}

		try {
			// load the temporary file using an ImportProvider
			LocatableInputSupplier<? extends InputStream> tmpIn = new FileIOSupplier(applyFile);

			// get the action
			IOAction action = IOActionExtension.getInstance().get(loadActionId);
			checkState(ImportProvider.class.isAssignableFrom(action.getProviderType()),
					"Load action not compatible to ImportProvider");
			// try specified provider
			ImportProvider provider = null;
			if (loadProviderId != null) {
				try {
					provider = (ImportProvider) HaleIO.createIOProvider(action.getProviderType(),
							null, loadProviderId);
				} catch (Exception e) {
					// ignore
					log.error(
							"Could not get specified import provider, trying auto-detection instead",
							e);
				}
			}
			// find provider if necessary
			if (provider == null) {
				provider = (ImportProvider) HaleIO.findIOProvider(action.getProviderType(), tmpIn,
						null);
			}

			if (provider == null) {
				throw new IllegalStateException("No provider for loading project file found");
			}

			// find advisor
			@SuppressWarnings("rawtypes")
			IOAdvisor advisor = IOAdvisorExtension.getInstance().findAdvisor(loadActionId);
			checkState(advisor != null, "No advisor for loading project file found");

			// configure provider
			// set given parameters
			setParameters(provider, loadParameters);
			// set source
			provider.setSource(tmpIn);

			// execute the provider
			executeProvider(provider, advisor);
		} finally {
			if (!applyFile.delete()) {
				applyFile.deleteOnExit();
				applyFile = null;
			}
		}
	}

	private void setParameters(IOProvider provider, Map<String, String> parameters) {
		for (Entry<String, String> entry : parameters.entrySet()) {
			provider.setParameter(entry.getKey(), entry.getValue());
		}
	}

	private <P extends IOProvider> void executeProvider(P provider, IOAdvisor<P> advisor) {
		IOReporter reporter = provider.createReporter();
		ATransaction trans = log.begin(reporter.getTaskName());
		try {
			// use advisor to configure provider
			advisor.prepareProvider(provider);
			advisor.updateConfiguration(provider);

			// execute
			IOReport report = provider.execute(new LogProgressIndicator());

			// handle results
			if (report.isSuccess()) {
				advisor.handleResults(provider);
			}
		} catch (Exception e) {
			log.error("Error executing an I/O provider.", e);
		} finally {
			trans.end();
		}
	}

	/**
	 * @see ProjectFile#reset()
	 */
	@Override
	public void reset() {
		if (applyFile != null && !applyFile.delete()) {
			applyFile.deleteOnExit();
			applyFile = null;
		}
	}

	/**
	 * @see ProjectFile#store(OutputStream)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void store(final OutputStream out) throws Exception {
		// get the action
		IOAction action = IOActionExtension.getInstance().get(saveActionId);
		checkState(ExportProvider.class.isAssignableFrom(action.getProviderType()),
				"Save action not compatible to ExportProvider");
		// get specified provider
		ExportProvider provider = (ExportProvider) HaleIO.createIOProvider(
				action.getProviderType(), null, saveProviderId);

		if (provider == null) {
			throw new IllegalStateException("No provider for saving project file found");
		}

		// find advisor
		@SuppressWarnings("rawtypes")
		IOAdvisor advisor = IOAdvisorExtension.getInstance().findAdvisor(saveActionId);
		checkState(advisor != null, "No advisor for saving project file found");

		// configure provider
		// set given parameters
		setParameters(provider, saveParameters);
		// set target
		provider.setTarget(new LocatableOutputSupplier<OutputStream>() {

			private boolean first = true;

			@Override
			public OutputStream getOutput() throws IOException {
				if (first) {
					first = false;
					return out;
				}
				throw new IllegalStateException("Output stream only available once");
			}

			@Override
			public URI getLocation() {
				return null;
			}
		});

		// execute the provider
		executeProvider(provider, advisor);
	}

}
