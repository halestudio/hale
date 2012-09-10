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

package eu.esdihumboldt.hale.ui.views.styledmap.painter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.BoundingBox;

/**
 * An empty Dummy class used to trick geotools Stylefactory wich only works with
 * SimpleFeatures
 * 
 * @author Sebastian Reinhardt
 */
public class DummyFeature implements SimpleFeature {

	/**
	 * @see org.opengis.feature.Feature#getBounds()
	 */
	@Override
	public BoundingBox getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.Feature#getDefaultGeometryProperty()
	 */
	@Override
	public GeometryAttribute getDefaultGeometryProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.Feature#getIdentifier()
	 */
	@Override
	public FeatureId getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.Feature#setDefaultGeometryProperty(org.opengis.feature.GeometryAttribute)
	 */
	@Override
	public void setDefaultGeometryProperty(GeometryAttribute arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.opengis.feature.ComplexAttribute#getProperties()
	 */
	@Override
	public Collection<Property> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.ComplexAttribute#getProperties(org.opengis.feature.type.Name)
	 */
	@Override
	public Collection<Property> getProperties(Name arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.ComplexAttribute#getProperties(java.lang.String)
	 */
	@Override
	public Collection<Property> getProperties(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.ComplexAttribute#getProperty(org.opengis.feature.type.Name)
	 */
	@Override
	public Property getProperty(Name arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.ComplexAttribute#getProperty(java.lang.String)
	 */
	@Override
	public Property getProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.ComplexAttribute#getValue()
	 */
	@Override
	public Collection<? extends Property> getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.ComplexAttribute#setValue(java.util.Collection)
	 */
	@Override
	public void setValue(Collection<Property> arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.opengis.feature.ComplexAttribute#validate()
	 */
	@Override
	public void validate() throws IllegalAttributeException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.opengis.feature.Attribute#getDescriptor()
	 */
	@Override
	public AttributeDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.Property#getName()
	 */
	@Override
	public Name getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.Property#getUserData()
	 */
	@Override
	public Map<Object, Object> getUserData() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.Property#isNillable()
	 */
	@Override
	public boolean isNillable() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see org.opengis.feature.Property#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#getAttribute(org.opengis.feature.type.Name)
	 */
	@Override
	public Object getAttribute(Name arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#getAttribute(int)
	 */
	@Override
	public Object getAttribute(int arg0) throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#getAttributeCount()
	 */
	@Override
	public int getAttributeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#getAttributes()
	 */
	@Override
	public List<Object> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#getDefaultGeometry()
	 */
	@Override
	public Object getDefaultGeometry() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#getFeatureType()
	 */
	@Override
	public SimpleFeatureType getFeatureType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#getID()
	 */
	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#getType()
	 */
	@Override
	public SimpleFeatureType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#setAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#setAttribute(org.opengis.feature.type.Name,
	 *      java.lang.Object)
	 */
	@Override
	public void setAttribute(Name arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#setAttribute(int,
	 *      java.lang.Object)
	 */
	@Override
	public void setAttribute(int arg0, Object arg1) throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#setAttributes(java.util.List)
	 */
	@Override
	public void setAttributes(List<Object> arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#setAttributes(java.lang.Object[])
	 */
	@Override
	public void setAttributes(Object[] arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.opengis.feature.simple.SimpleFeature#setDefaultGeometry(java.lang.Object)
	 */
	@Override
	public void setDefaultGeometry(Object arg0) {
		// TODO Auto-generated method stub

	}

}
