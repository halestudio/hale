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

package eu.esdihumboldt.hale.ui.views.styledmap.painter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.geotools.api.feature.GeometryAttribute;
import org.geotools.api.feature.IllegalAttributeException;
import org.geotools.api.feature.Property;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.feature.type.Name;
import org.geotools.api.filter.identity.FeatureId;
import org.geotools.api.geometry.BoundingBox;

/**
 * An empty Dummy class used to trick geotools Stylefactory wich only works with
 * SimpleFeatures
 * 
 * @author Sebastian Reinhardt
 */
public class DummyFeature implements SimpleFeature {

	/**
	 * @see org.geotools.api.feature.Feature#getBounds()
	 */
	@Override
	public BoundingBox getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.Feature#getDefaultGeometryProperty()
	 */
	@Override
	public GeometryAttribute getDefaultGeometryProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.Feature#getIdentifier()
	 */
	@Override
	public FeatureId getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.Feature#setDefaultGeometryProperty(org.geotools.api.feature.GeometryAttribute)
	 */
	@Override
	public void setDefaultGeometryProperty(GeometryAttribute arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.geotools.api.feature.ComplexAttribute#getProperties()
	 */
	@Override
	public Collection<Property> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.ComplexAttribute#getProperties(org.geotools.api.feature.type.Name)
	 */
	@Override
	public Collection<Property> getProperties(Name arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.ComplexAttribute#getProperties(java.lang.String)
	 */
	@Override
	public Collection<Property> getProperties(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.ComplexAttribute#getProperty(org.geotools.api.feature.type.Name)
	 */
	@Override
	public Property getProperty(Name arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.ComplexAttribute#getProperty(java.lang.String)
	 */
	@Override
	public Property getProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.ComplexAttribute#getValue()
	 */
	@Override
	public Collection<? extends Property> getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.ComplexAttribute#setValue(java.util.Collection)
	 */
	@Override
	public void setValue(Collection<Property> arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.geotools.api.feature.ComplexAttribute#validate()
	 */
	@Override
	public void validate() throws IllegalAttributeException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.geotools.api.feature.Attribute#getDescriptor()
	 */
	@Override
	public AttributeDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.Property#getName()
	 */
	@Override
	public Name getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.Property#getUserData()
	 */
	@Override
	public Map<Object, Object> getUserData() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.Property#isNillable()
	 */
	@Override
	public boolean isNillable() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see org.geotools.api.feature.Property#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#getAttribute(org.geotools.api.feature.type.Name)
	 */
	@Override
	public Object getAttribute(Name arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#getAttribute(int)
	 */
	@Override
	public Object getAttribute(int arg0) throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#getAttributeCount()
	 */
	@Override
	public int getAttributeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#getAttributes()
	 */
	@Override
	public List<Object> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#getDefaultGeometry()
	 */
	@Override
	public Object getDefaultGeometry() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#getFeatureType()
	 */
	@Override
	public SimpleFeatureType getFeatureType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#getID()
	 */
	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#getType()
	 */
	@Override
	public SimpleFeatureType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#setAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#setAttribute(org.geotools.api.feature.type.Name,
	 *      java.lang.Object)
	 */
	@Override
	public void setAttribute(Name arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#setAttribute(int,
	 *      java.lang.Object)
	 */
	@Override
	public void setAttribute(int arg0, Object arg1) throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#setAttributes(java.util.List)
	 */
	@Override
	public void setAttributes(List<Object> arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#setAttributes(java.lang.Object[])
	 */
	@Override
	public void setAttributes(Object[] arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.geotools.api.feature.simple.SimpleFeature#setDefaultGeometry(java.lang.Object)
	 */
	@Override
	public void setDefaultGeometry(Object arg0) {
		// TODO Auto-generated method stub

	}

}
