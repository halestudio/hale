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

package eu.esdihumboldt.hale.common.core.io.project.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.osgi.framework.Version;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueExtension;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.JaxbProjectIO;

/**
 * Represents a project.
 * 
 * @author Simon Templer
 */
public class Project implements ProjectInfo, Cloneable {

	/**
	 * Load a project from an input stream.
	 * 
	 * @param in the input stream
	 * @return the project
	 * 
	 * @throws Exception if the project could not be loaded
	 */
	public static Project load(InputStream in) throws Exception {
		return JaxbProjectIO.load(in);
	}

	/**
	 * Save a project to an output stream.
	 * 
	 * @param project the project to save
	 * @param out the output stream
	 * @throws Exception if saving the project fails
	 */
	public static void save(Project project, OutputStream out) throws Exception {
		JaxbProjectIO.save(project, out);
	}

	/**
	 * The project name
	 */
	private String name;

	/**
	 * The project author
	 */
	private String author;

	/**
	 * The HALE version
	 */
	private Version haleVersion;

	/**
	 * The date the project was created
	 */
	private Date created;

	/**
	 * The date the project was modified
	 */
	private Date modified;

	/**
	 * A project description
	 */
	private String description;

	/**
	 * The configuration the project was saved with
	 */
	private IOConfiguration saveConfiguration;

	/**
	 * I/O configurations
	 */
	private final List<IOConfiguration> resources = new ArrayList<IOConfiguration>();

	/**
	 * Project properties
	 */
	private final Map<String, Value> properties = new TreeMap<String, Value>();

	/**
	 * Project file locations
	 */
	private final List<ProjectFileInfo> projectFiles = new ArrayList<ProjectFileInfo>();

	/**
	 * The saved export configurations. Names (case insensitive) mapped to I/O
	 * configurations.
	 */
	private final Map<String, IOConfiguration> exportConfigurations = new ExportConfigurationMap();

	/**
	 * Default constructor.
	 */
	public Project() {
	}

	/**
	 * Copy constructor.
	 * 
	 * @param project the project to copy
	 */
	public Project(Project project) {
		this.name = project.getName();
		this.author = project.getAuthor();
		this.haleVersion = project.getHaleVersion();
		this.created = project.getCreated();
		this.modified = project.getModified();
		this.description = project.getDescription();
		if (project.getSaveConfiguration() != null) {
			this.saveConfiguration = project.getSaveConfiguration().clone();
		}
		for (IOConfiguration resource : project.getResources()) {
			this.resources.add(resource.clone());
		}
		this.properties.putAll(project.getProperties());
		for (ProjectFileInfo fileInfo : project.getProjectFiles()) {
			this.projectFiles.add(fileInfo.clone());
		}
		for (Entry<String, IOConfiguration> exportPreset : project.getExportConfigurations()
				.entrySet()) {
			this.exportConfigurations.put(exportPreset.getKey(), exportPreset.getValue().clone());
		}
	}

	@Override
	public Project clone() {
		return new Project(this);
	}

	/**
	 * @return the configurations
	 */
	public List<IOConfiguration> getResources() {
		return resources;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectInfo#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectInfo#getAuthor()
	 */
	@Override
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectInfo#getHaleVersion()
	 */
	@Override
	public Version getHaleVersion() {
		return haleVersion;
	}

	/**
	 * @param haleVersion the haleVersion to set
	 */
	public void setHaleVersion(Version haleVersion) {
		this.haleVersion = haleVersion;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectInfo#getCreated()
	 */
	@Override
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectInfo#getModified()
	 */
	@Override
	public Date getModified() {
		return modified;
	}

	/**
	 * @param modified the modified to set
	 */
	public void setModified(Date modified) {
		this.modified = modified;
	}

	/**
	 * @return the project properties, values are either strings, DOM elements
	 *         or complex value types defined in the
	 *         {@link ComplexValueExtension}
	 */
	public Map<String, Value> getProperties() {
		return properties;
	}

	/**
	 * @return the saveConfiguration
	 */
	public IOConfiguration getSaveConfiguration() {
		return saveConfiguration;
	}

	/**
	 * @param saveConfiguration the saveConfiguration to set
	 */
	public void setSaveConfiguration(IOConfiguration saveConfiguration) {
		this.saveConfiguration = saveConfiguration;
	}

	/**
	 * @return names (case insensitive) mapped to export configurations
	 */
	public Map<String, IOConfiguration> getExportConfigurations() {
		return exportConfigurations;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectInfo#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the list of external project file locations
	 */
	public List<ProjectFileInfo> getProjectFiles() {
		return projectFiles;
	}

	@Override
	public Value getSetting(String name) {
		Value value = getProperties().get(name);
		return (value != null) ? (value) : (Value.NULL);
	}

}
