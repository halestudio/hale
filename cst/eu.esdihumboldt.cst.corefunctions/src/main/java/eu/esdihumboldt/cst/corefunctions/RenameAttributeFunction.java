/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.cst.corefunctions;

import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;
import eu.esdihumboldt.goml.omwg.Property;

/**
 * CstFunction for attribute renaming, i.e. the copying of attributes of the 
 * same type from a source to a target property.
 * 
 * @author Thorsten Reitz, Jan Jezek
 * @version $Id: RenameAttributeFunction.java 2418 2009-12-22 11:35:12Z jjezek $ 
 */
public class RenameAttributeFunction extends AbstractCstFunction {

	private String oldName;
	private String newName;
	
	public static final String OLD_ATTRIBUTE_NAME_PARAMETER = "ENTITY_1_LOCALNAME";
	
	public static final String NEW_ATTRIBUTE_NAME_PARAMETER = "ENTITY_2_LOCALNAME";
	

	public SimpleFeatureType getTargetType(FeatureType sourceType) {
		return null;
	}

	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		throw new UnsupportedOperationException("Currently not supported.");
	}

	/**
	 * This transform implementation can copy any literal attribute value 
	 * (Strings, Numbers).
	 */
	public Feature transform(Feature source, Feature target) {
		Class<?> pdSource = source.getProperty(this.oldName).getDescriptor()
								.getType().getBinding();
		Class<?> pdTarget = target.getProperty(this.newName).getDescriptor()
								.getType().getBinding();
		
		// only do a direct copy if the two Properties have equal bindings.
		if (pdSource.equals(pdTarget)) {
			((SimpleFeature)target).setAttribute(
					this.newName, source.getProperty(this.oldName).getValue());
		}
		else if (pdSource.equals(Integer.class) 
				&& pdTarget.equals(Integer.class)) {
			Integer value = Integer.parseInt(source.getProperty(
					this.oldName).getValue().toString());
			((SimpleFeature)target).setAttribute(
					this.newName, value);
		}
		else if (pdSource.equals(String.class) 
				&& pdTarget.equals(Long.class)) {
			Long value = Long.parseLong(source.getProperty(
					this.oldName).getValue().toString());
			((SimpleFeature)target).setAttribute(
					this.newName, value);
		}
		else if (pdSource.equals(String.class) 
				&& pdTarget.equals(Float.class)) {
			Float value = Float.parseFloat(source.getProperty(
					this.oldName).getValue().toString());
			((SimpleFeature)target).setAttribute(
					this.newName, value);
		}
		else if (pdSource.equals(String.class) 
				&& pdTarget.equals(Double.class)) {
			Double value = Double.parseDouble(source.getProperty(
					this.oldName).getValue().toString());
			((SimpleFeature)target).setAttribute(
					this.newName, value);
		}
		else if (pdTarget.equals(String.class) && 
				(pdSource.equals(Float.class) 
						|| pdSource.equals(Double.class) 
						|| pdSource.equals(Integer.class) 
						|| pdSource.equals(Long.class))) {
			((SimpleFeature)target).setAttribute(
					this.newName, source.getProperty(
							this.oldName).getValue().toString());
		}
		else {
			throw new UnsupportedOperationException("For the given source " +
					"and target attribute bindings, this rename function " +
					"cannot be used.");
		}
		
		return target;
	}

	public boolean configure(Map<String, String> parametersValues) {							
		this.oldName = parametersValues.get(OLD_ATTRIBUTE_NAME_PARAMETER);
		this.newName = parametersValues.get(NEW_ATTRIBUTE_NAME_PARAMETER);		
		return true;
	}

	public boolean configure(ICell cell) {
		this.oldName = ((Property)cell.getEntity1()).getLocalname();
		this.newName = ((Property)cell.getEntity2()).getLocalname();
		return true;
	}

	@Override
	protected void setParametersTypes(Map<String, Class<?>> parametersTypes) {
		parametersTypes.put(OLD_ATTRIBUTE_NAME_PARAMETER, String.class);
		parametersTypes.put(OLD_ATTRIBUTE_NAME_PARAMETER, String.class);		
	}
	
}
