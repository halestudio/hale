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

package eu.esdihumboldt.gmlhandler.deegree;

import java.util.List;

import javax.xml.namespace.QName;

import org.deegree.commons.tom.TypedObjectNode;
import org.deegree.feature.Feature;
import org.deegree.feature.property.Property;
import org.deegree.feature.types.FeatureType;
import org.deegree.geometry.Envelope;
import org.deegree.gml.GMLVersion;
import org.deegree.gml.feature.StandardGMLFeatureProps;

/**
 * Wrapper that marks a {@link Feature} as internal
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class InternalFeature implements Feature {

	private Feature feature;

	/**
	 * Constructor
	 * 
	 * @param feature the feature to wrap
	 */
	public InternalFeature(Feature feature) {
		super();
		this.feature = feature;
	}

	/**
	 * @see org.deegree.feature.Feature#getEnvelope()
	 */
	public Envelope getEnvelope() {
		return feature.getEnvelope();
	}

	/**
	 * @see org.deegree.feature.Feature#getGeometryProperties()
	 */
	public Property[] getGeometryProperties() {
		return feature.getGeometryProperties();
	}

	/**
	 * @see org.deegree.feature.Feature#getGMLProperties()
	 */
	public StandardGMLFeatureProps getGMLProperties() {
		return feature.getGMLProperties();
	}

	/**
	 * @see org.deegree.feature.Feature#getId()
	 */
	public String getId() {
		return feature.getId();
	}

	/**
	 * @see org.deegree.feature.Feature#getName()
	 */
	public QName getName() {
		return feature.getName();
	}

	/**
	 * @see org.deegree.feature.Feature#getProperties()
	 */
	public Property[] getProperties() {
		return feature.getProperties();
	}

	/**
	 * @see org.deegree.feature.Feature#getProperties(org.deegree.gml.GMLVersion)
	 */
	public Property[] getProperties(GMLVersion version) {
		return feature.getProperties(version);
	}

	/**
	 * @see org.deegree.feature.Feature#getProperties(javax.xml.namespace.QName, org.deegree.gml.GMLVersion)
	 */
	public Property[] getProperties(QName propName, GMLVersion version) {
		return feature.getProperties(propName, version);
	}

	/**
	 * @see org.deegree.feature.Feature#getProperties(javax.xml.namespace.QName)
	 */
	public Property[] getProperties(QName propName) {
		return feature.getProperties(propName);
	}

	/**
	 * @see org.deegree.feature.Feature#getProperty(javax.xml.namespace.QName, org.deegree.gml.GMLVersion)
	 */
	public Property getProperty(QName propName, GMLVersion version) {
		return feature.getProperty(propName, version);
	}

	/**
	 * @see org.deegree.feature.Feature#getProperty(javax.xml.namespace.QName)
	 */
	public Property getProperty(QName propName) {
		return feature.getProperty(propName);
	}

	/**
	 * @see org.deegree.feature.Feature#getType()
	 */
	public FeatureType getType() {
		return feature.getType();
	}

	/**
	 * @see org.deegree.feature.Feature#setId(java.lang.String)
	 */
	public void setId(String id) {
		feature.setId(id);
	}

	/**
	 * @see org.deegree.feature.Feature#setProperties(java.util.List, org.deegree.gml.GMLVersion)
	 */
	public void setProperties(List<Property> props, GMLVersion version)
			throws IllegalArgumentException {
		feature.setProperties(props, version);
	}

	/**
	 * @see org.deegree.feature.Feature#setProperties(java.util.List)
	 */
	public void setProperties(List<Property> props)
			throws IllegalArgumentException {
		feature.setProperties(props);
	}

	/**
	 * @see org.deegree.feature.Feature#setPropertyValue(javax.xml.namespace.QName, int, org.deegree.commons.tom.TypedObjectNode, org.deegree.gml.GMLVersion)
	 */
	public void setPropertyValue(QName propName, int occurence,
			TypedObjectNode value, GMLVersion version) {
		feature.setPropertyValue(propName, occurence, value, version);
	}

	/**
	 * @see org.deegree.feature.Feature#setPropertyValue(javax.xml.namespace.QName, int, org.deegree.commons.tom.TypedObjectNode)
	 */
	public void setPropertyValue(QName propName, int occurence,
			TypedObjectNode value) {
		feature.setPropertyValue(propName, occurence, value);
	} 

}
