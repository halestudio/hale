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

package eu.esdihumboldt.hale.core.io.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.xml.sax.InputSource;

import eu.esdihumboldt.hale.core.io.ContentType;

/**
 * {@link ContentType} definition for serialization
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ContentTypeDefinition {
	
	/**
	 * Load a content type definition from an input stream
	 * 
	 * @param in the input stream
	 * @return the content type definition
	 * 
	 * @throws MappingException if the mapping could not be loaded
	 * @throws MarshalException if the content type definition could not be read
	 * @throws ValidationException if the input stream did not provide valid XML
	 */
	public static ContentTypeDefinition load(InputStream in) throws MappingException, MarshalException, ValidationException {
		Mapping mapping = new Mapping(ContentTypeDefinition.class.getClassLoader());
		mapping.loadMapping(new InputSource(
				ContentTypeDefinition.class.getResourceAsStream("ContentType.xml")));
		        
		XMLContext context = new XMLContext();
		context.addMapping(mapping);

		Unmarshaller unmarshaller = context.createUnmarshaller();
		try {
			return (ContentTypeDefinition) unmarshaller.unmarshal(new InputSource(in));
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
	
	private String identifier;
	
	private String parent;
	
	private Set<String> fileExtensions = new HashSet<String>();
	
	private String defaultName;
	
	private Map<Locale, String> names = new HashMap<Locale, String>();
	
	private String testerClassName;

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * @return the fileExtensions
	 */
	public Set<String> getFileExtensions() {
		return fileExtensions;
	}

	/**
	 * @param fileExtensions the fileExtensions to set
	 */
	public void setFileExtensions(Set<String> fileExtensions) {
		this.fileExtensions = fileExtensions;
	}

	/**
	 * @return the defaultName
	 */
	public String getDefaultName() {
		return defaultName;
	}

	/**
	 * @param defaultName the defaultName to set
	 */
	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	/**
	 * @return the names
	 */
	public Map<Locale, String> getNames() {
		return names;
	}

	/**
	 * @param names the names to set
	 */
	public void setNames(Map<Locale, String> names) {
		this.names = names;
	}

	/**
	 * @return the testerClassName
	 */
	public String getTesterClassName() {
		return testerClassName;
	}

	/**
	 * @param testerClassName the testerClassName to set
	 */
	public void setTesterClassName(String testerClassName) {
		this.testerClassName = testerClassName;
	}

}
