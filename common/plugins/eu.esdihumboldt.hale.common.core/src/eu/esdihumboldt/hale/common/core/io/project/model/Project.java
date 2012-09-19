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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.osgi.framework.Version;
import org.xml.sax.InputSource;

import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;

/**
 * Represents a project.
 * 
 * @author Simon Templer
 */
public class Project implements ProjectInfo {

	/**
	 * Load a project from an input stream.
	 * 
	 * @param in the input stream
	 * @return the project
	 * 
	 * @throws MappingException if the mapping could not be loaded
	 * @throws MarshalException if the project could not be read
	 * @throws ValidationException if the input stream did not provide valid XML
	 */
	public static Project load(InputStream in) throws MappingException, MarshalException,
			ValidationException {
		Mapping mapping = new Mapping(Project.class.getClassLoader());
		mapping.loadMapping(new InputSource(Project.class.getResourceAsStream("Project.xml")));

		XMLContext context = new XMLContext();
		context.addMapping(mapping);

		Unmarshaller unmarshaller = context.createUnmarshaller();
		try {
			return (Project) unmarshaller.unmarshal(new InputSource(in));
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Save a project to an output stream.
	 * 
	 * @param project the project to save
	 * @param out the output stream
	 * @throws MappingException if the mapping could not be loaded
	 * @throws ValidationException if the mapping is no valid XML
	 * @throws MarshalException if the project could not be marshaled
	 * @throws IOException if the output could not be written
	 */
	public static void save(ProjectInfo project, OutputStream out) throws MappingException,
			MarshalException, ValidationException, IOException {
		Mapping mapping = new Mapping(Project.class.getClassLoader());
		mapping.loadMapping(new InputSource(Project.class.getResourceAsStream("Project.xml")));

		XMLContext context = new XMLContext();
		context.setProperty("org.exolab.castor.indent", true); // enable
																// indentation
																// for
																// marshaling as
																// project files
																// should be
																// very small
		context.addMapping(mapping);
		Marshaller marshaller = context.createMarshaller();
//		marshaller.setEncoding("UTF-8"); XXX not possible using the XMLContext but UTF-8 seems to be default, see http://jira.codehaus.org/browse/CASTOR-2846
		Writer writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
		try {
			marshaller.setWriter(writer);
			marshaller.marshal(project);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// ignore
			}
		}
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
	private final Map<String, String> properties = new TreeMap<String, String>();

	/**
	 * Project file locations
	 */
	private final List<ProjectFileInfo> projectFiles = new ArrayList<ProjectFileInfo>();

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
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
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

}
