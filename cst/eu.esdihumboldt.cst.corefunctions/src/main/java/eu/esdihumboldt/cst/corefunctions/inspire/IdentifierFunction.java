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

package eu.esdihumboldt.cst.corefunctions.inspire;

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
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;

import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;

/**
 * This function creates INSPIRE-compliant identifiers like this one
 * <code>urn:de:fraunhofer:exampleDataset:exampleFeatureTypeName:localID</code> 
 * based on the localId of the given source attribute.
 * 
 * @author Ulrich Schaeffler, Thorsten Reitz
 * @partner 01 / Technische Universitaet Muenchen
 * @partner 02 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class IdentifierFunction 
	extends AbstractCstFunction {
	
	public static final String COUNTRY_PARAMETER_NAME = "countryName";
	public static final String DATA_PROVIDER_PARAMETER_NAME = "providerName";
	public static final String PRODUCT_PARAMETER_NAME = "productName";
	
	private String countryName = null;
	private String dataProviderName= null;
	private String productName = null;
	private Property sourceProperty = null;
	private Property targetProperty = null;

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#configure(eu.esdihumboldt.cst.align.ICell)
	 */
	public boolean configure(ICell cell) {
		for (IParameter ip : cell.getEntity1().getTransformation().getParameters()) {
			if (ip.getName().equals(IdentifierFunction.COUNTRY_PARAMETER_NAME)) {
				this.countryName = ip.getValue();
			}
			else{
				if (ip.getName().equals(IdentifierFunction.DATA_PROVIDER_PARAMETER_NAME)) {
					this.dataProviderName = ip.getValue();
				}	
				else{
					if (ip.getName().equals(IdentifierFunction.PRODUCT_PARAMETER_NAME)) {
						this.productName = ip.getValue();
					}
				}
			}
		}
		
		this.sourceProperty = (Property) cell.getEntity1();
		this.targetProperty = (Property) cell.getEntity2();
		return true;
	}


	/* (non-Javadoc)
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.geotools.feature.FeatureCollection)
	 */
	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.opengis.feature.Feature, org.opengis.feature.Feature)
	 */
	public Feature transform(Feature source, Feature target) {
		// define String to use
		String localID = source.getIdentifier().getID();
		String featureTypeName = source.getType().getName().getLocalPart();
		String inspireIDString = "urn:" + this.countryName + ":"
				+ this.dataProviderName + ":" + this.productName + ":"
				+ featureTypeName + ":" + localID;
		
		// set to target feature
		PropertyDescriptor pd = target.getProperty(
				this.targetProperty.getLocalname()).getDescriptor();
		PropertyImpl p = null;
		if (pd.getType().getBinding().equals(String.class)) {
			p = new AttributeImpl(inspireIDString, (AttributeDescriptor) pd,
					null);
		}
		
		//((FeatureType)pd.getType()).getDescriptors()
		
		Collection<org.opengis.feature.Property> c = new HashSet<org.opengis.feature.Property>();
		c.add(p);
		target.setValue(c);
		return target;
	}


	@Override
	protected void setParametersTypes(Map<String, Class<?>> parametersTypes) {
		// TODO Auto-generated method stub
		
	}

}
