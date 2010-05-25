/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.goml.rdf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import eu.esdihumboldt.cst.rdf.IAbout;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class DetailedAbout implements IDetailedAbout {
	
	private UUID uuid;
	
	private final String namespace;
	
	private final String featureClass;
	
	private final List<String> properties;

	/**
	 * Create a detailed about for a feature class
	 * 
	 * @param namespace the namespace
	 * @param featureClass the feature class
	 */
	public DetailedAbout(String namespace, String featureClass) {
		this(namespace, featureClass, (List<String>) null);
	}
	
	/**
	 * Create a detailed about for a property
	 * 
	 * @param namespace the namespace
	 * @param featureClass the feature class that holds the first of the
	 *   (nested) properties
	 * @param properties the names of the (nested) properties
	 */
	public DetailedAbout(String namespace, String featureClass,
			String... properties) {
		this(namespace, featureClass, Arrays.asList(properties));
	}
	
	/**
	 * Create a detailed about for a property
	 * 
	 * @param namespace the namespace
	 * @param featureClass the feature class that holds the first of the
	 *   (nested) properties
	 * @param properties the names of the (nested) properties
	 */
	public DetailedAbout(String namespace, String featureClass,
			List<String> properties) {
		super();
		this.namespace = namespace;
		this.featureClass = featureClass;
		this.properties = properties;
	}

	/**
	 * Create a detailed about from an about string
	 * 
	 * @param about the about string
	 */
	public DetailedAbout(String about) {
		super();
		
		// separate in namespace + feature class & properties
		String main;
		String propertiesString = null;
		int propertiesIndex = about.indexOf(PROPERTY_DELIMITER);
		if (propertiesIndex >= 0) {
			main = about.substring(0, propertiesIndex);
			if (propertiesIndex < about.length() + 1) {
				propertiesString = about.substring(propertiesIndex + 1);
			}
		}
		else {
			main = about;
		}
		
		// separate in namspace & feature class
		int typeIndex = main.lastIndexOf('/');
		if (typeIndex >= 0 && typeIndex < main.length() + 1) {
			namespace = main.substring(0, typeIndex);
			featureClass = main.substring(typeIndex + 1);
		}
		else {
			throw new IllegalArgumentException("No feature class specified in about");
		}
		
		// get properties
		if (propertiesString != null) {
			String[] propertiesArray = propertiesString.split(String.valueOf(PROPERTY_DELIMITER));
			if (propertiesArray != null) {
				properties = Arrays.asList(propertiesArray);
			}
			else {
				properties = null;
			}
		}
		else {
			properties = null;
		}
	}

	/**
	 * @see IDetailedAbout#getFeatureClass()
	 */
	@Override
	public String getFeatureClass() {
		return featureClass;
	}

	/**
	 * @see IDetailedAbout#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @see IDetailedAbout#getProperties()
	 */
	@Override
	public List<String> getProperties() {
		return new ArrayList<String>(properties);
	}

	/**
	 * @see IAbout#getAbout()
	 */
	@Override
	public String getAbout() {
		StringBuffer result = new StringBuffer();
		result.append(namespace);
		result.append(MAIN_DELIMITER);
		result.append(featureClass);
		
		// properties
		if (properties != null && !properties.isEmpty()) {
			result.append(PROPERTY_DELIMITER);
			
			boolean first = true;
			for (String property : properties) {
				if (first) {
					first = false;
				}
				else {
					result.append(PROPERTY_DELIMITER);
				}
				
				result.append(property);
			}
		}
		
		return result.toString();
	}

	/**
	 * @see IAbout#getUid()
	 */
	@Override
	public UUID getUid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUid(UUID uuid) {
		this.uuid = uuid;
	}
	
	// static methods
	
	/**
	 * Get a detailed about from an {@link IAbout}
	 * 
	 * @param about the about
	 * 
	 * @return the detailed about
	 */
	public static IDetailedAbout getDetailedAbout(IAbout about) {
		if (about instanceof IDetailedAbout) {
			return (IDetailedAbout) about;
		}
		else {
			DetailedAbout result = new DetailedAbout(about.getAbout());
			result.setUid(about.getUid());
			return result;
		}
	}

}
