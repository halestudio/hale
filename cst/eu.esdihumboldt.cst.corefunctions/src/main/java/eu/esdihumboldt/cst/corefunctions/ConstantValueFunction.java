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

package eu.esdihumboldt.cst.corefunctions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.PropertyImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.corefunctions.inspire.IdentifierFunction;
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;

/**
 * CST Function to set default
 * attribute target values.
 *
 * @author Ulrich Schaeffler, Anna Pitaev
 * @partner Technische Universitaet Muenchen, 04 / Logica
 * @version $Id$ 
 */
public class ConstantValueFunction extends AbstractCstFunction {
	
	public static final String DEFAULT_VALUE_PARAMETER_NAME = "defaultValue";
	private Object defaultValue = null;
	private Property targetProperty = null;

	/**
	 * @see eu.esdihumboldt.cst.transformer.AbstractCstFunction#setParametersTypes(java.util.Map)
	 */
	@Override
	protected void setParametersTypes(Map<String, Class<?>> parametersTypes) {
		parameterTypes.put(ConstantValueFunction.DEFAULT_VALUE_PARAMETER_NAME, Object.class);

	}

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#configure(eu.esdihumboldt.cst.align.ICell)
	 */
	@Override
	public boolean configure(ICell cell) {
		for (IParameter ip : cell.getEntity2().getTransformation().getParameters()) {
			if (ip.getName().equals(ConstantValueFunction.DEFAULT_VALUE_PARAMETER_NAME)) {
				this.defaultValue = ip.getValue();
			}
		
		}
		
//		this.sourceProperty = ((ComposedProperty)cell.getEntity1()).getCollection().get(0);
		this.targetProperty = ((ComposedProperty)cell.getEntity2()).getCollection().get(0);
		return true;
	}

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.geotools.feature.FeatureCollection)
	 */
	@Override
	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.opengis.feature.Feature, org.opengis.feature.Feature)
	 */
	@Override
	public Feature transform(Feature source, Feature target) {
		PropertyDescriptor pd = target.getProperty(
				this.targetProperty.getLocalname()).getDescriptor();
		
		
		PropertyImpl p = null;
		if (pd.getType().getBinding().isPrimitive()) {
			
			if (pd.getType().getBinding().equals(Integer.class)){
				p = new AttributeImpl((Integer)this.defaultValue, (AttributeDescriptor) pd, null);
			}
			else if (pd.getType().getBinding().equals(Short.class)){
				p = new AttributeImpl((Short)this.defaultValue, (AttributeDescriptor) pd, null);
			}
			else if (pd.getType().getBinding().equals(Double.class)){
				p = new AttributeImpl((Double)this.defaultValue, (AttributeDescriptor) pd, null);
			}
			else if (pd.getType().getBinding().equals(Long.class)){
				p = new AttributeImpl((Long)this.defaultValue, (AttributeDescriptor) pd, null);
			}
			else if (pd.getType().getBinding().equals(Float.class)){
				p = new AttributeImpl((Float)this.defaultValue, (AttributeDescriptor) pd, null);
			}
			else if (pd.getType().getBinding().equals(Boolean.class)){
				p = new AttributeImpl((Boolean)this.defaultValue, (AttributeDescriptor) pd, null);
			}
			else if (pd.getType().getBinding().equals(Byte.class)){
				p = new AttributeImpl((Byte)this.defaultValue, (AttributeDescriptor) pd, null);
			}
			else {
				p = new AttributeImpl((Character)this.defaultValue, (AttributeDescriptor) pd, null);
			}

		}
		else if (pd.getType().getBinding().equals(String.class)){
			p = new AttributeImpl(this.defaultValue.toString(), (AttributeDescriptor) pd, null);
		}
		Collection<org.opengis.feature.Property> c = new HashSet<org.opengis.feature.Property>();
		c.add(p);
		target.setValue(c);
		return target;
	}

}
