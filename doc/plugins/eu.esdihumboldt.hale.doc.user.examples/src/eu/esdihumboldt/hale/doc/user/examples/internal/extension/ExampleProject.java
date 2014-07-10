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

package eu.esdihumboldt.hale.doc.user.examples.internal.extension;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.google.common.io.ByteStreams;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.align.io.AlignmentIO;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.util.LocationUpdater;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

/**
 * Represents a declared example project
 * 
 * @author Simon Templer
 */
public class ExampleProject implements Identifiable, Comparable<ExampleProject> {

	private final String id;

	private final ProjectInfo info;

	private final String bundleName;

	private final String location;

	private final String summary;

	private final File alignmentFile = File.createTempFile("example_alignment", ".xml");

	private final LocationUpdater updater;

	/**
	 * Create an example project from a configuration element.
	 * 
	 * @param id the project identifier
	 * @param conf the configuration element
	 * @throws URISyntaxException if the project location can't be resolved to a
	 *             valid URI
	 * @throws IOException if reading the project information fails
	 * @throws IOProviderConfigurationException if the project reader wasn't
	 *             configured correctly
	 */
	public ExampleProject(String id, IConfigurationElement conf) throws URISyntaxException,
			IOProviderConfigurationException, IOException {
		super();

		this.id = id;
		this.summary = conf.getAttribute("summary");

		// determine location
		bundleName = conf.getDeclaringExtension().getContributor().getName();
		Bundle bundle = Platform.getBundle(bundleName);

		this.location = conf.getAttribute("location");
		URL url = bundle.getResource(location);
		LocatableInputSupplier<InputStream> in = new DefaultInputSupplier(url.toURI());

		// load project info
		ProjectReader reader = HaleIO.findIOProvider(ProjectReader.class, in, location);
		Map<String, ProjectFile> projectFiles = new HashMap<String, ProjectFile>();
		projectFiles.put(AlignmentIO.PROJECT_FILE_ALIGNMENT, new ProjectFile() {

			@Override
			public void store(LocatableOutputSupplier<OutputStream> target) throws Exception {
				throw new UnsupportedOperationException();
			}

			@Override
			public void reset() {
				// do nothing
			}

			@Override
			public void load(InputStream in) throws Exception {
				OutputStream out = new BufferedOutputStream(new FileOutputStream(alignmentFile));
				// save to alignment file
				ByteStreams.copy(in, out);
				out.close();
				alignmentFile.deleteOnExit();
			}

			@Override
			public void apply() {
				// do nothing
			}
		});
		reader.setProjectFiles(projectFiles);
		reader.setSource(in);
		reader.execute(null);

		Project project = reader.getProject();

		// update paths in project
		updater = new LocationUpdater(project, url.toURI());
		// example projects cannot be saved where they are, so forget about
		// relative paths
		updater.updateProject(false);

		this.info = project;
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(ExampleProject o) {
		int result;

		if (getInfo() == null || getInfo().getName() == null) {
			result = 1;
		}
		else if (o.getInfo() == null || o.getInfo().getName() == null) {
			result = -1;
		}
		else {
			result = getInfo().getName().compareToIgnoreCase(o.getInfo().getName());
		}

		if (result == 0) {
			result = getId().compareTo(o.getId());
		}

		return result;
	}

	/**
	 * @see Identifiable#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Get the example project info
	 * 
	 * @return the project info
	 */
	public ProjectInfo getInfo() {
		return info;
	}

	/**
	 * Get the name of the bundle the example project is contained in.
	 * 
	 * @return the name of the bundle containing the project
	 */
	public String getBundleName() {
		return bundleName;
	}

	/**
	 * Get the example project summary.
	 * 
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * Get the location of the project in its bundle.
	 * 
	 * @return the bundle location as path inside the bundle that contains it
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Get the location of the alignment file.
	 * 
	 * @return the alignmentFile
	 */
	public URI getAlignmentLocation() {
		if (alignmentFile.exists()) {
			return alignmentFile.toURI();
		}
		return null;
	}

	/**
	 * Get the used location updater.
	 * 
	 * @return the updater
	 */
	public LocationUpdater getUpdater() {
		return updater;
	}

}
