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

package eu.esdihumboldt.tools;

import java.io.File;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import eu.esdihumboldt.generated.configuration.Configuration;

import eu.esdihumboldt.generated.configuration.PropertyType;

import eu.esdihumboldt.mediator.util.XMLHandler;

/**
 * Helper class to load a component configuration.
 *
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$ 
 */
public class ConfigurationManager {
	
	private static final String CONFIGURATION_CONTEXT = "eu.esdihumboldt.generated.configuration";

	private static final String COMPONENT_CONFIGURATION_FILE = "configuration.xml";

	/** XML Handler to unmarshal configuration */
	private static  XMLHandler xmlHandler;
	
	/** container object for the component configuration */
	private static Configuration configuration;
	
   /**
    * returns a value of component specific property
    * @param propertyName
    * @return String propertyValue
    */
	
	public static String getComponentProperty(String propertyName) {
		
		boolean isFound = false;
		String propertyValue = "";
		//load configuration if needed
		if (configuration == null) configuration = loadConfiguration();
		List<PropertyType>  componentProperties = configuration.getComponentproperties().getProperty();
		
		Iterator<PropertyType> iterator = componentProperties.iterator();
		PropertyType pType; 
		while(iterator.hasNext()&&!isFound){
			pType = iterator.next();
			if (pType.getKey().equals(propertyName)) {
				propertyValue = pType.getValue();
				isFound = true;
			}
			
		}
		if (propertyValue == null){
			throw new RuntimeException("Component Configuration Property  " + propertyName +" is not defined for this component");
		}
		
		
		return propertyValue;
	}
	
	

	/**
	 * returns a value of the system property.
	 * @param systemPropertyName
	 * @return String systemPropertyValue
	 */
	public static String getSystemProperty(String systemPropertyName){
		boolean isFound = false;
		String propertyValue = "";
		//load configuration if needed
		if (configuration == null) configuration = loadConfiguration();
		List<PropertyType>  systemProperties = configuration.getSystemproperties().getProperty();
		
		Iterator<PropertyType> iterator = systemProperties.iterator();
		PropertyType pType; 
		while(iterator.hasNext()&&!isFound){
			pType = iterator.next();
			if (pType.getKey().equals(systemPropertyName)) {
				propertyValue = pType.getValue();
				isFound = true;
			}
			
		}
		if (propertyValue == null){
			throw new RuntimeException("System Configuration Property  " + systemPropertyName +" is not defined for this component");
		}
		
		
		return propertyValue;
		
	}

	/**
	 * loads configuration properties from the file to the configuration object
	 * additionally stores the system property to the SYSTEM
	 * @return Configuration 
	 */
	private static Configuration loadConfiguration() {
		//1. unmarshall configuration
		JAXBContext jc;
		JAXBElement<Configuration> root = null;
		try {
			jc = JAXBContext.newInstance(CONFIGURATION_CONTEXT);
			Unmarshaller u = jc.createUnmarshaller();

			// it will debug problems while unmarshalling
			u.setEventHandler(
					new javax.xml.bind.helpers.DefaultValidationEventHandler());
			
			root = u.unmarshal(new StreamSource(ClassLoader.getSystemResourceAsStream(COMPONENT_CONFIGURATION_FILE)),
					Configuration.class);
			configuration = root.getValue();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		//2. store system property to the system	
		List<PropertyType> systemProperties = configuration.getSystemproperties().getProperty();
			Iterator<PropertyType> iterator = systemProperties.iterator();
			PropertyType property; 
			while (iterator.hasNext()){
			     property = iterator.next();
			     System.setProperty(property.getKey(), property.getValue());
			}
		
		return configuration;
	}
}
