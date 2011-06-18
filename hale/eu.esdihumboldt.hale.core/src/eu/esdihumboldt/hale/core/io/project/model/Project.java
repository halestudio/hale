/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.core.io.project.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.osgi.framework.Version;
import org.xml.sax.InputSource;


/**
 * Represents a project.
 * @author Simon Templer
 */
public class Project {
	
	/**
	 * Load a project from an input stream.
	 * @param in the input stream
	 * @return the project
	 * 
	 * @throws MappingException if the mapping could not be loaded
	 * @throws MarshalException if the project could not be read
	 * @throws ValidationException if the input stream did not provide valid XML
	 */
	public static Project load(InputStream in) throws MappingException, MarshalException, ValidationException {
		Mapping mapping = new Mapping(Project.class.getClassLoader());
		mapping.loadMapping(new InputSource(
				Project.class.getResourceAsStream("Project.xml")));
		        
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
	 * @param project the project to save
	 * @param out the output stream
	 * @throws MappingException if the mapping could not be loaded
	 * @throws ValidationException if the mapping is no valid XML 
	 * @throws MarshalException if the project could not be marshaled 
	 * @throws IOException if the output could not be written 
	 */
	public static void save(Project project, OutputStream out) throws MappingException, MarshalException, ValidationException, IOException {
		Mapping mapping = new Mapping(Project.class.getClassLoader());
		mapping.loadMapping(new InputSource(
				Project.class.getResourceAsStream("Project.xml")));
		        
		XMLContext context = new XMLContext();
		context.setProperty("org.exolab.castor.indent", true); // enable indentation for marshaling as project files should be very small
		context.addMapping(mapping);
		Marshaller marshaller = context.createMarshaller();
		Writer writer = new BufferedWriter(new OutputStreamWriter(out));
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
	private final List<IOConfiguration> configurations = new ArrayList<IOConfiguration>();

	/**
	 * Project properties
	 */
	private final Map<String, String> properties = new HashMap<String, String>();
	
	/**
	 * File names and classes of additional project files
	 */
	private final Map<String, Class<? extends ProjectFile>> files = new HashMap<String, Class<? extends ProjectFile>>();
	
	/**
	 * @return the configurations
	 */
	public List<IOConfiguration> getConfigurations() {
		return configurations;
	}

	/**
	 * @return the name
	 */
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
	 * @return the author
	 */
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
	 * @return the haleVersion
	 */
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
	 * @return the created
	 */
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
	 * @return the modified
	 */
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
	 * @return the files
	 */
	public Map<String, Class<? extends ProjectFile>> getFiles() {
		return files;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
