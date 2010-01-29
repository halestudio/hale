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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.inspire.data.InspireIdentifier;

/**
 * This function creates INSPIRE-compliant identifiers like this one
 * <code>urn:de:fraunhofer:exampleDataset:exampleFeatureTypeName:localID</code> 
 * based on the localId of the given source attribute.
 * 
 * @author Ulrich Schaeffler
 * @partner 14 / TUM
 * @version $Id$ 
 */
public class IdentifierFunction 
	extends AbstractCstFunction {
	
	public static final String COUNTRY_PARAMETER_NAME = "countryName";
	public static final String DATA_PROVIDER_PARAMETER_NAME = "providerName";
	public static final String PRODUCT_PARAMETER_NAME = "productName";
	public static final String VERSION = "version";
	
	public static final String INSPIRE_IDENTIFIER_PREFIX = "urn:x-inspire:object:id";
	
	private String countryName = null;
	private String dataProviderName= null;
	private String productName = null;
	private String version = null;
	
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
					else {
						if (ip.getName().equals(IdentifierFunction.VERSION)){
							this.version = ip.getValue();
						}
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
		
		//check if the input features have the expected property name
		if (target.getProperties(targetProperty.getLocalname()).size()==0) return null;
		if (source.getProperties(sourceProperty.getLocalname()).size()==0) return null;

		// inject result into target object
		PropertyDescriptor pd = target.getProperty(
				this.targetProperty.getLocalname()).getDescriptor();

		if (pd.getType().getBinding().equals(InspireIdentifier.class)) {
			InspireIdentifier ii=new InspireIdentifier();
			String localID = null;
			if (source.getIdentifier().getID()==null || source.getIdentifier().getID().equalsIgnoreCase("")){
				localID = UUID.randomUUID().toString();
			}
			else {
				localID = source.getIdentifier().getID();
			}
			
			ii.setLocalID(localID);
			
			String featureTypeName = source.getType().getName().getLocalPart();
			ii.setNameSpace(this.countryName + ":"
					+ this.dataProviderName + ":" + this.productName + ":"
					+ featureTypeName);
			
		
			if (this.version != null && !this.version.equals("")){
				ii.setVersionID(this.version);
			}
			else{
				ii.setVersionID("");
			}

			((SimpleFeature)target).setAttribute(this.targetProperty.getLocalname(),ii);
		
		}
		//TODO: Write in gml:id
		else if (pd.getType().getBinding().equals(String.class)){
			// define String to use
			String localID = null;
			if (source.getIdentifier().getID()==null || source.getIdentifier().getID().equalsIgnoreCase("")){
				localID = UUID.randomUUID().toString();
			}
			else {
				localID = source.getIdentifier().getID();
			}

			if (this.version != null && !this.version.equals("")){

					String featureTypeName = source.getType().getName().getLocalPart();
					String inspireIDString = this.INSPIRE_IDENTIFIER_PREFIX + ":" + this.countryName + ":"
							+ this.dataProviderName + ":" + this.productName + ":"
							+ featureTypeName + ":" + localID + ":"+this.version;	
					// set to target feature
					((SimpleFeature)target).setAttribute(this.targetProperty.getLocalname(),inspireIDString);
			}
				
				else{
					String featureTypeName = source.getType().getName().getLocalPart();
					String inspireIDString = this.INSPIRE_IDENTIFIER_PREFIX + ":" + this.countryName + ":"
							+ this.dataProviderName + ":" + this.productName + ":"
							+ featureTypeName + ":" + localID;
					// set to target feature
					((SimpleFeature)target).setAttribute(this.targetProperty.getLocalname(),inspireIDString);
				}
			}

		return target;
	}


	@Override
	protected void setParametersTypes(Map<String, Class<?>> parametersTypes) {
		parameterTypes.put(IdentifierFunction.COUNTRY_PARAMETER_NAME, String.class);
		parameterTypes.put(IdentifierFunction.DATA_PROVIDER_PARAMETER_NAME, String.class);
		parameterTypes.put(IdentifierFunction.PRODUCT_PARAMETER_NAME, String.class);
		parameterTypes.put(IdentifierFunction.VERSION, String.class);
		
	}
	
	public Cell getParameters() {
		Cell parameterCell = new Cell();
		Property entity1 = new Property(new About(""));
		
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add("eu.esdihumboldt.inspire.data.InspireIdentifier");
		entityTypes.add("java.lang.String");
		entity1.setTypeCondition(entityTypes);
		
		Property entity2 = new Property(new About(""));
		 
		// Setting of type condition for entity2
			// 	entity2 has same type conditions as entity1
		entity2.setTypeCondition(entityTypes);
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

}
