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

package eu.esdihumboldt.gmlhandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Map;
import java.util.Set;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.deegree.commons.xml.XMLParsingException;
import org.deegree.commons.xml.stax.FormattingXMLStreamWriter;
import org.deegree.commons.xml.stax.XMLStreamWriterWrapper;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.feature.FeatureCollection;
import org.deegree.feature.types.ApplicationSchema;
import org.deegree.gml.GMLInputFactory;
import org.deegree.gml.GMLOutputFactory;
import org.deegree.gml.GMLStreamReader;
import org.deegree.gml.GMLStreamWriter;
import org.deegree.gml.GMLVersion;
import org.deegree.gml.feature.schema.ApplicationSchemaXSDDecoder;


/**
 * <p>
 * This class implements methods for handling of the gml data.
 * </p>
 * 
 * <p>
 * Supported GML versions:
 * <ul>
 * <li>gml_2</li>
 * <li>gml_3_0</li>
 * <li>gml_3_1</li>
 * <li>gml_3_2</li> </li>
 * </p>
 * 
 * 
 * 
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$
 */
public class GmlHandler {
	
	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(GmlHandler.class);
	
	/**
	 * gml version
	 */
	private GMLVersion gmlVersion;
	
	/**
	 *   schema location
     */
	private String schemaUrl;
	
	
	/**
	 * map storing user defined namespaces
	 */
    private Map<String,String> namespaces; 

    /**
     * data location
     */
    private String gmlUrl;
    
    /**
     * target gml location
     */
    private String targetGmlUrl; 
    /**
     * Constructor
     * 
     * @param gmlVersion gml format to be processed, 
     * @param schemaUrl  schema location,
     * @param namespaces map storing predefined namespaces.
     */
    public GmlHandler (GMLVersions gmlVersion, String schemaUrl, Map<String,String>namespaces){
    	this.gmlVersion = getGMLVersion(gmlVersion);
    	this.schemaUrl = schemaUrl;
    	this.namespaces = namespaces;
    	
    }
    
   /**
    * Loads the underlying schema.
    * 
    * @return ApplicationSchema object
    * @throws MalformedURLException 
    * @throws IllegalAccessException 
    * @throws InstantiationException 
    * @throws ClassNotFoundException 
    * @throws ClassCastException 
    */
    public ApplicationSchema readSchema() throws MalformedURLException, ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException  {
		LOG.info("Parses the underlying schema at " + this.schemaUrl);
		//converts schema location to the url format
		String schemaURL = new URL(this.schemaUrl).toString();
		//creates schema-decoder
		ApplicationSchemaXSDDecoder decoder = new ApplicationSchemaXSDDecoder(
				this.gmlVersion, this.namespaces, schemaURL);
        //reads the schema
		ApplicationSchema gmlSchema = decoder.extractFeatureTypeSchema();
        if (gmlSchema != null){
        	LOG.info("Schema loaded successfully");
        }else {
            LOG.warn("Schema is NULL");
        }
		return gmlSchema;
		
	}
     
    /**
     * Reads deegree3 based FeatureCollection from the gml-instance.
     
     * @return Feature Collection.
     * @throws IOException 
     * @throws FactoryConfigurationError 
     * @throws XMLStreamException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws ClassNotFoundException 
     * @throws ClassCastException 
     * @throws UnknownCRSException 
     * @throws XMLParsingException 
     * @throws Exception
     */
    public  FeatureCollection readFC() throws XMLStreamException, FactoryConfigurationError, IOException, ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException, XMLParsingException, UnknownCRSException  {
		 LOG.info("Reading the gml instance at " + this.gmlUrl);
    	// converts gml location to the URL format
		URL url = new URL(this.gmlUrl);
		//creates reader to parse gml
		GMLStreamReader gmlStreamReader = GMLInputFactory
				.createGMLStreamReader(this.gmlVersion, url);
		//sets application schema
		gmlStreamReader.setApplicationSchema(readSchema());
		
		FeatureCollection fc = (FeatureCollection)gmlStreamReader.readFeature();
		//resolves local references
		gmlStreamReader.getIdContext().resolveLocalRefs();
		return fc;
	}
    /**
     * Encodes a deegree3-based Feature/FeatureCollection instance.
     * @param fc - FeatureCollection to be encoded.
     * @throws XMLStreamException 
     * @throws FileNotFoundException 
     * @throws TransformationException 
     * @throws UnknownCRSException 
     * 
     */
  
    public  void writeFC(FeatureCollection fc) throws FileNotFoundException, XMLStreamException, UnknownCRSException, TransformationException {
    	
		LOG.info("Exporting the gml-instance to the location " + this.targetGmlUrl);
    	XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
       //will set namespaces if these not set explicitly
		outputFactory.setProperty( "javax.xml.stream.isRepairingNamespaces", new Boolean( true ) );
        //create XML File Stream Writer
		XMLStreamWriter xmlStreamWriter = outputFactory.createXMLStreamWriter(new FileOutputStream(new File(this.targetGmlUrl)),"UTF-8");
         XMLStreamWriterWrapper writer = new XMLStreamWriterWrapper( xmlStreamWriter, this.schemaUrl);
       
        
        //set namespaces, this should be done explicitly
        //TODO define a nicer way to set the default namespace 
        writer.setDefaultNamespace("http://www.opengis.net/gml/3.2");
        //read the namespaces from the map containing namespaces
        Set<String> nsPrefixes = this.namespaces.keySet();
        String nsValue = "";
        for (String nsPrefix :  nsPrefixes){
        	nsValue = (String)this.namespaces.get(nsPrefix);
        	writer.setPrefix(nsPrefix, nsValue);
        	
        	
        }
      
      //create exporter to export files
      GMLStreamWriter exporter = GMLOutputFactory.createGMLStreamWriter(GMLVersion.GML_32, new FormattingXMLStreamWriter( writer ));
      exporter.write(fc);
      writer.flush();
      writer.close();
      LOG.debug("Gml instance has been exported successfully ");
		
	        
		
		
	}
    /**
     * Converts from the Humboldt version to the deegree3 version
     * @param gmlVersion
     * @return deegree3-based version
     */
	private GMLVersion getGMLVersion(GMLVersions gmlVersion) {
		GMLVersion version = null;
		if (gmlVersion.equals(GMLVersions.gml2)){
			version = GMLVersion.GML_2;
		}else if (gmlVersion.equals(GMLVersions.gml3_0)){
			version = GMLVersion.GML_30;
		}else if(gmlVersion.equals(GMLVersions.gml3_1)){
			 version = GMLVersion.GML_31;
		}else if (gmlVersion.equals(GMLVersions.gml3_2_1)){
			version = GMLVersion.GML_32;
		}
		return  version;
	}

	/**
	 * @return the schemaUrl
	 */
	public String getSchemaUrl() {
		return schemaUrl;
	}

	/**
	 * @param schemaUrl the schemaUrl to set
	 */
	public void setSchemaUrl(String schemaUrl) {
		this.schemaUrl = schemaUrl;
	}

	/**
	 * @return the namespaces
	 */
	public Map<String, String> getNamespaces() {
		return namespaces;
	}

	/**
	 * @param namespaces the namespaces to set
	 */
	public void setNamespaces(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}

	/**
	 * @return the gmlUrl
	 */
	public String getGmlUrl() {
		return gmlUrl;
	}

	/**
	 * @param gmlUrl the gmlUrl to set
	 */
	public void setGmlUrl(String gmlUrl) {
		this.gmlUrl = gmlUrl;
	}

	/**
	 * @return the targetGmlUrl
	 */
	public String getTargetGmlUrl() {
		return targetGmlUrl;
	}

	/**
	 * @param targetGmlUrl the targetGmlUrl to set
	 */
	public void setTargetGmlUrl(String targetGmlUrl) {
		this.targetGmlUrl = targetGmlUrl;
	}

}

