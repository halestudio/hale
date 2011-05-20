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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.esdihumboldt.commons.inspire.data.InspireIdentifier;
import eu.esdihumboldt.commons.tools.FeatureInspector;
import eu.esdihumboldt.cst.corefunctions.Messages;
import eu.esdihumboldt.specification.cst.AbstractCstFunction;
import eu.esdihumboldt.specification.cst.CstFunction;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

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
	
	public static final String COUNTRY_PARAMETER_NAME = "countryName"; //$NON-NLS-1$
	public static final String DATA_PROVIDER_PARAMETER_NAME = "providerName"; //$NON-NLS-1$
	public static final String PRODUCT_PARAMETER_NAME = "productName"; //$NON-NLS-1$
	public static final String VERSION = "version"; //$NON-NLS-1$
	public static final String VERSION_NIL_REASON = "versionNilReason"; //$NON-NLS-1$
	
	public static final String INSPIRE_IDENTIFIER_PREFIX = "urn:x-inspire:object:id"; //$NON-NLS-1$
	
	private String countryName = null;
	private String dataProviderName= null;
	private String productName = null;
	private String version = null;
	private String versionNilReason = "unknown"; //$NON-NLS-1$
	
	private Property sourceProperty = null;
	private Property targetProperty = null;

	/**
	 * @see CstFunction#configure(ICell)
	 */
	public boolean configure(ICell cell) {
		for (IParameter ip : cell.getEntity1().getTransformation().getParameters()) {
			if (ip.getName().equals(IdentifierFunction.COUNTRY_PARAMETER_NAME)) {
				this.countryName = ip.getValue();
			}
			else if (ip.getName().equals(IdentifierFunction.DATA_PROVIDER_PARAMETER_NAME)) {
				this.dataProviderName = ip.getValue();
			}	
			else if (ip.getName().equals(IdentifierFunction.PRODUCT_PARAMETER_NAME)) {
				this.productName = ip.getValue();
			}
			else if (ip.getName().equals(IdentifierFunction.VERSION)){
				this.version = ip.getValue();
			}
			else if (ip.getName().equals(VERSION_NIL_REASON)) {
				versionNilReason = ip.getValue();
			}
		}
		
		this.sourceProperty = (Property) cell.getEntity1();
		this.targetProperty = (Property) cell.getEntity2();
		return true;
	}

	/**
	 * This implementation is not null-safe.
	 * 
	 * @see CstFunction#transform(Feature, Feature)
	 */
	public Feature transform(Feature source, Feature target) {
		
		//check if the input features have the expected property name
		if (target.getProperties(targetProperty.getLocalname()).size() == 0)
			return null;

		// inject result into target object
		PropertyDescriptor pd = target.getProperty(
				this.targetProperty.getLocalname()).getDescriptor();

		if (pd.getType().getName().getNamespaceURI() != null 
				&& pd.getType().getName().getNamespaceURI().equals(
				"urn:x-inspire:specification:gmlas:BaseTypes:3.2")  //$NON-NLS-1$
				&& pd.getType().getName().getLocalPart().equals("IdentifierPropertyType")) { //$NON-NLS-1$
			
			// get the source attribute value
			Object value = FeatureInspector.getPropertyValue(source, sourceProperty.getAbout(), null);
			
			// set attributes
			FeatureInspector.setPropertyValue(target, 
					Arrays.asList(targetProperty.getLocalname(), "Identifier", "localId"),  //$NON-NLS-1$ //$NON-NLS-2$
					value.toString()); // source.getIdentifier().toString()
			FeatureInspector.setPropertyValue(target, 
					Arrays.asList(targetProperty.getLocalname(), "Identifier", "namespace"),  //$NON-NLS-1$ //$NON-NLS-2$
					getNamespace(target.getType().getName().getLocalPart()));
			
			if (version != null && !version.isEmpty()) {
				FeatureInspector.setPropertyValue(target, 
						Arrays.asList(targetProperty.getLocalname(), "Identifier", "versionId"),  //$NON-NLS-1$ //$NON-NLS-2$
						this.version);
			}
			else {
				FeatureInspector.setPropertyValue(target, 
						Arrays.asList(targetProperty.getLocalname(), "Identifier", "versionId", "nilReason"),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						this.versionNilReason);
			}
		}
		else if (pd.getType().getBinding().equals(InspireIdentifier.class)) {
			InspireIdentifier ii=new InspireIdentifier();
			String localID = null;
			if (source.getIdentifier().getID()==null || source.getIdentifier().getID().equalsIgnoreCase("")){ //$NON-NLS-1$
				localID = UUID.randomUUID().toString();
			}
			else {
				localID = source.getIdentifier().getID();
			}
			
			ii.setLocalID(localID);
			
			String featureTypeName = source.getType().getName().getLocalPart();
			ii.setNameSpace(this.countryName + ":" //$NON-NLS-1$
					+ this.dataProviderName + ":" + this.productName + ":" //$NON-NLS-1$ //$NON-NLS-2$
					+ featureTypeName);
			
		
			if (this.version != null && !this.version.equals("")){ //$NON-NLS-1$
				ii.setVersionID(this.version);
			}
			else{
				ii.setVersionID(""); //$NON-NLS-1$
			}

			((SimpleFeature)target).setAttribute(this.targetProperty.getLocalname(),ii);
		
		}
		//TODO: Write in gml:id
		else if (pd.getType().getBinding().equals(String.class)){
			// define String to use
			String localID = null;
			if (source.getIdentifier().getID()==null || source.getIdentifier().getID().equalsIgnoreCase("")){ //$NON-NLS-1$
				localID = UUID.randomUUID().toString();
			}
			else {
				localID = source.getIdentifier().getID();
			}

			if (this.version != null && !this.version.equals("")){ //$NON-NLS-1$

					String featureTypeName = source.getType().getName().getLocalPart();
					String inspireIDString = INSPIRE_IDENTIFIER_PREFIX + ":" + this.countryName + ":" //$NON-NLS-1$ //$NON-NLS-2$
							+ this.dataProviderName + ":" + this.productName + ":" //$NON-NLS-1$ //$NON-NLS-2$
							+ featureTypeName + ":" + localID + ":"+this.version;	 //$NON-NLS-1$ //$NON-NLS-2$
					// set to target feature
					((SimpleFeature)target).setAttribute(this.targetProperty.getLocalname(),inspireIDString);
			}
				
				else{
					String featureTypeName = source.getType().getName().getLocalPart();
					String inspireIDString = INSPIRE_IDENTIFIER_PREFIX + ":" + this.countryName + ":" //$NON-NLS-1$ //$NON-NLS-2$
							+ this.dataProviderName + ":" + this.productName + ":" //$NON-NLS-1$ //$NON-NLS-2$
							+ featureTypeName + ":" + localID; //$NON-NLS-1$
					// set to target feature
					((SimpleFeature)target).setAttribute(this.targetProperty.getLocalname(),inspireIDString);
				}
			}

		return target;
	}
	
	/**
	 * @see CstFunction#getParameters()
	 */
	public Cell getParameters() {
		Cell parameterCell = new Cell();
		Property entity1 = new Property(new About("")); //$NON-NLS-1$
		
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add(InspireIdentifier.class.getName());
		entityTypes.add(String.class.getName());
		entity1.setTypeCondition(entityTypes);
		
		Transformation transf = new Transformation();
		entity1.setTransformation(transf);
		
		Property entity2 = new Property(new About("")); //$NON-NLS-1$
		 
		// Setting of type condition for entity2
			// 	entity2 has same type conditions as entity1
		entity2.setTypeCondition(entityTypes);
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}
	
	private String getNamespace(String featureTypeName) {
		return this.countryName + ":" //$NON-NLS-1$
				+ this.dataProviderName + ":" + this.productName + ":" //$NON-NLS-1$ //$NON-NLS-2$
				+ featureTypeName;
	}

	@Override
	public String getDescription() {
		return Messages.getString("IdentifierFunction.42"); //$NON-NLS-1$
	}

}
