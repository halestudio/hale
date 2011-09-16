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
package eu.esdihumboldt.cst.iobridge.impl;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.namespace.QName;

import org.eclipse.xsd.XSDSchema;
import org.geotools.feature.FeatureCollection;
import org.geotools.gml3.ApplicationSchemaXSD;

import org.geotools.xml.Configuration;
import org.geotools.xml.Encoder;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Attention: This is at best a temporary solution until we implement a full 
 * GML Generator.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class GmlGenerator {
	/**
	 * configuration used by gml Parser/Encoder
	 */
	private Configuration configuration;

	/**
	 * Encoder to marshall the jts objects to gml.
	 */
	private Encoder encoder;

	/**
	 * Schema to generate gml for application specific data.
	 */
	private ApplicationSchemaXSD appSchema;

	/**
	 * Qname for the FeatureCollection that to be encoded
	 */
	private QName qName;

	/**
	 * @return the qName
	 */
	public QName getqName() {
		return qName;
	}

	/**
	 * @param qName
	 *            the qName to set
	 */
	public void setqName(QName qName) {
		this.qName = qName;
	}

	/**
	 * @return the appSchema
	 */
	public ApplicationSchemaXSD getAppSchema() {
		return appSchema;
	}

	/**
	 * @param appSchema
	 *            the appSchema to set
	 */
	public void setAppSchema(ApplicationSchemaXSD appSchema) {
		this.appSchema = appSchema;
	}

	/**
	 * @return the encoder
	 */
	public Encoder getEncoder() {
		return encoder;
	}

	/**
	 * @param encoder
	 *            the encoder to set
	 */
	public void setEncoder(Encoder encoder) {
		this.encoder = encoder;
	}

	/**
	 * @return the configuration
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration
	 *            the configuration to set
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Constructor
	 * 
	 * @param gmlVersion
	 *            supported GMLVersion
	 * @throws IOException
	 */
	public GmlGenerator(String gmlVersion, String namespace,
			String schemaLocation) throws IOException {
		// 1. create GML2/3 Configuration
		if (gmlVersion.equals(GmlVersion.gml2.name()))
			this.configuration = new org.geotools.gml2.GMLConfiguration();
		else {
			this.configuration = new org.geotools.gml3.GMLConfiguration();
		}

		// 3. create ApplicationSchema XSD
		this.appSchema = new ApplicationSchemaXSD(namespace, schemaLocation);

		// 4. create Qname used by encoder
		this.qName = new QName("http://www.opengis.net/gml", //$NON-NLS-1$
				gmlElement.FeatureCollection.name());

		// 5. create encoder
		XSDSchema schema = this.appSchema.getSchema();

		this.encoder = new Encoder(this.configuration, schema);
		this.encoder.setSchemaLocation(namespace, schemaLocation);
	}

	/**
	 * Marshalls Feature Collection to the GML Format.
	 * 
	 * @param transformedCollection
	 * @param out
	 * @throws IOException
	 */
	public void encode(
			FeatureCollection<? extends FeatureType, ? extends Feature> transformedCollection,
			OutputStream out) throws IOException {
		this.encoder.encode(transformedCollection, this.qName, out);
	}

	/**
	 * enum for gml version3
	 * 
	 * @author anna
	 * 
	 */
	public enum GmlVersion {
		gml2, gml3
	}

	/**
	 * 
	 * enum for gml element names
	 * 
	 */
	// TODO add more elements
	public enum gmlElement {
		FeatureCollection
	}
}
