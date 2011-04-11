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
package com.onespatial.jrc.tns.oml_to_rif.digest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.onespatial.jrc.tns.oml_to_rif.HaleAlignment;
import com.onespatial.jrc.tns.oml_to_rif.api.AbstractFollowableTranslator;
import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAlignment;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAttributeMappingCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelCentroidCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelConcatenationOfAttributesCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelClassMappingCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelIdentifierCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelStaticAssignmentCell;
import com.onespatial.jrc.tns.oml_to_rif.schema.GmlAttribute;
import com.onespatial.jrc.tns.oml_to_rif.schema.GmlAttributePath;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.corefunctions.CentroidFunction;
import eu.esdihumboldt.cst.corefunctions.ConcatenationOfAttributesFunction;
import eu.esdihumboldt.cst.corefunctions.ConstantValueFunction;
import eu.esdihumboldt.cst.corefunctions.NilReasonFunction;
import eu.esdihumboldt.cst.corefunctions.RenameAttributeFunction;
import eu.esdihumboldt.cst.corefunctions.inspire.IdentifierFunction;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.goml.rdf.DetailedAbout;
import eu.esdihumboldt.goml.rdf.IDetailedAbout;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportReport;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Alignment to model alignment digester.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Simon Templer / Fraunhofer IGD
 * @author Susanne Reinwarth / TU Dresden
 */
public class AlignmentToModelAlignmentDigester extends
        AbstractFollowableTranslator<HaleAlignment, ModelAlignment>
{

	private final MappingExportReport report;
	
    /**
     * Constructor
     * 
	 * @param report the export report
	 */
	public AlignmentToModelAlignmentDigester(MappingExportReport report) {
		super();
		
		this.report = report;
	}

	/**
     * Translates a {@link HaleAlignment} into an intermediate
     * {@link ModelAlignment} format.
     * 
     * @param hal the HALE alignment
     * @return {@link ModelAlignment}
     * @throws TranslationException
     *             if any problems occurred during translation
     */
    @Override
    public ModelAlignment translate(HaleAlignment hal) throws TranslationException
    {
        List<ModelClassMappingCell> classMappings = new ArrayList<ModelClassMappingCell>();
        List<ModelAttributeMappingCell> attributeMappings = new ArrayList<ModelAttributeMappingCell>();
        List<ModelConcatenationOfAttributesCell> concatenationMappings = new ArrayList<ModelConcatenationOfAttributesCell>();
        List<ModelCentroidCell> centroidMappings = new ArrayList<ModelCentroidCell>();
        List<ModelIdentifierCell> identifierMappings = new ArrayList<ModelIdentifierCell>();
        ArrayList<ModelStaticAssignmentCell> staticAssigments = new ArrayList<ModelStaticAssignmentCell>();
        
        Alignment source = hal.getAlignment();
        
        Map<String, SchemaElement> sourceFeatures = hal.getSourceElements();
        Map<String, SchemaElement> targetFeatures = hal.getTargetElements();

        for (ICell cell : source.getMap())
        {
        	IEntity sourceEntity = cell.getEntity1();
            IEntity targetEntity = cell.getEntity2();

            boolean sourceIsFeatureClass = sourceEntity instanceof FeatureClass;
            boolean targetIsFeatureClass = targetEntity instanceof FeatureClass;
        	
            if (sourceIsFeatureClass && targetIsFeatureClass)
            {
            	// type mapping
                classMappings.add(createCell((FeatureClass) sourceEntity,
                        (FeatureClass) targetEntity, sourceFeatures, targetFeatures));
            }
            else if (!sourceIsFeatureClass && !targetIsFeatureClass)
            {
            	String function = sourceEntity.getTransformation().getService().getLocation();
            	boolean functionIsConcatenationOfAttributes = ConcatenationOfAttributesFunction.class.getName().equals(function);
            	boolean functionIsCentroidFunction = CentroidFunction.class.getName().equals(function);
            	boolean functionIsRenameAttributeFunction = RenameAttributeFunction.class.getName().equals(function);
            	boolean functionIsIdentifierFunction = IdentifierFunction.class.getName().equals(function);
            	
            	// concatenation of attributes
            	if (sourceEntity instanceof ComposedProperty && functionIsConcatenationOfAttributes) {
            		ModelConcatenationOfAttributesCell modelCell = createConcatenation(cell,
            			(ComposedProperty) sourceEntity, (Property) targetEntity,
            			sourceFeatures, targetFeatures);
            		
            		if (modelCell != null)
            			concatenationMappings.add(modelCell);
            	}
            	// centroid function
            	else if (functionIsCentroidFunction)
            	{
            		ModelCentroidCell modelCell = createCentroid(cell,
            				(Property) sourceEntity, (Property) targetEntity,
            				sourceFeatures, targetFeatures);
            		
            		if(modelCell != null) {
            			centroidMappings.add(modelCell);
            		}
            	}
            	// property mapping
            	else if (functionIsRenameAttributeFunction)
            	{
            		ModelAttributeMappingCell modelCell = createAttribute(cell,
            			(Property) sourceEntity, (Property) targetEntity, 
            			sourceFeatures, targetFeatures);
            		            		
            		if (modelCell != null)
            			attributeMappings.add(modelCell);
            	}
            	// INSPIRE identifier
            	else if (functionIsIdentifierFunction)
            	{
            		ModelIdentifierCell modelCell = createIdentifier(cell,
            				(Property) sourceEntity, (Property) targetEntity, sourceFeatures, targetFeatures);
            		
            		if (modelCell != null)
            		{
            			identifierMappings.add(modelCell);
            		}
            	}
            	else
            	{
            		report.setWarning(cell, "Function " + function + " not recognized");
                }
            }
            // augmentations
            else if (sourceIsFeatureClass && !targetIsFeatureClass)
            {
            	ModelStaticAssignmentCell modelCell = createStaticAssignment(cell,
            			(Property) targetEntity, targetFeatures);            	
            	
            	if (modelCell != null) {
            		staticAssigments.add(modelCell);
            	}
            }
            else
            {
                throw new TranslationException("Unhandled combination");
            }
        }

        return new ModelAlignment(classMappings, attributeMappings, staticAssigments,
        		concatenationMappings, centroidMappings, identifierMappings);
    }

    private ModelIdentifierCell createIdentifier(ICell original,
			Property sourceEntity, Property targetEntity,
			Map<String, SchemaElement> sourceFeatures, Map<String, SchemaElement> targetFeatures)
    {		
    	String namespaceCountry = "";
    	String namespaceDataProvider  = "";
    	String namespaceProduct = "";
    	String versionId = "";
    	String versionNilReason = "";
    	
    	for (IParameter param : sourceEntity.getTransformation().getParameters())
        {
            if (param.getName().equals(IdentifierFunction.COUNTRY_PARAMETER_NAME))
            {
            	namespaceCountry = param.getValue();
            }
            else if (param.getName().equals(IdentifierFunction.DATA_PROVIDER_PARAMETER_NAME))
            {
            	namespaceDataProvider = param.getValue();
            }
            else if (param.getName().equals(IdentifierFunction.PRODUCT_PARAMETER_NAME))
            {
            	namespaceProduct = param.getValue();
            }
            else if (param.getName().equals(IdentifierFunction.VERSION))
            {
            	versionId = param.getValue();
            }
            else if (param.getName().equals(IdentifierFunction.VERSION_NIL_REASON))
            {
            	versionNilReason = param.getValue();
            }
        }
    	
    	IDetailedAbout sourceAbout = DetailedAbout.getDetailedAbout(sourceEntity.getAbout(), true);
    	IDetailedAbout targetAbout = DetailedAbout.getDetailedAbout(targetEntity.getAbout(), true);
    	
    	List<FeatureClass> filter = sourceEntity.getDomainRestriction();
    	List<Restriction> restrictions = new ArrayList<Restriction>();
    	
    	if (filter != null)
    	{
    		for (FeatureClass currentFilter : filter)
    		{
    			restrictions.addAll(currentFilter.getAttributeValueCondition());
    		}
    	}
    	
		try {
			return new ModelIdentifierCell(
				createAttributePath(sourceAbout, sourceFeatures),
				createAttributePath(targetAbout, targetFeatures),
				namespaceCountry + namespaceDataProvider + namespaceProduct,
				versionId, versionNilReason, restrictions);
		} catch (TranslationException e) {
			report.setFailed(original, e.getMessage());
			return null;
		}
	}

	private ModelStaticAssignmentCell createStaticAssignment(ICell original, 
    		Property targetEntity, Map<String, SchemaElement> targetFeatures)
    {
    	String function = targetEntity.getTransformation().getService().getLocation();
    	
    	if (ConstantValueFunction.class.getName().equals(function)) {
    		// constant value
    		String content = null;
            for (IParameter param : targetEntity.getTransformation().getParameters())
            {
                if (param.getName().equals(ConstantValueFunction.DEFAULT_VALUE_PARAMETER_NAME))
                {
                    content = param.getValue();
                    break;
                }
            }
            
            IDetailedAbout targetAbout = DetailedAbout.getDetailedAbout(targetEntity.getAbout(), true);
            
            try {
				return new ModelStaticAssignmentCell(
						createAttributePath(targetAbout, targetFeatures), content, false);
			} catch (TranslationException e) {
				report.setFailed(original, e.getMessage());
				return null;
			}
    	}
    	
    	else if (NilReasonFunction.class.getName().equals(function)) {
    		// nil reason
    		String reason = null;
            for (IParameter param : targetEntity.getTransformation().getParameters())
            {
                if (param.getName().equals(NilReasonFunction.PARAMETER_NIL_REASON_TYPE))
                {
                    reason = param.getValue();
                    break;
                }
            }
            
            report.setWarning(original, "The nil reason will be set regardless of whether a value for its parent is set or not");
            
            IDetailedAbout targetAbout = DetailedAbout.getDetailedAbout(targetEntity.getAbout(), true);
            List<String> properties = new ArrayList<String>(targetAbout.getProperties());
            properties.add("nilReason"); //XXX this is an attribute does it make any difference?
			targetAbout = new DetailedAbout(targetAbout.getNamespace(), targetAbout.getFeatureClass(), properties);
	        try {
				return new ModelStaticAssignmentCell(
						createAttributePath(targetAbout, targetFeatures), reason, true);
			} catch (TranslationException e) {
				report.setFailed(original, e.getMessage());
				return null;
			}
    	}
    	else {
    		// not supported
    		report.setFailed(original, "Only default value augmentations supported");
        	return null;
    	}
    }

    private ModelAttributeMappingCell createAttribute(ICell original, Property sourceEntity, Property targetEntity,
    		Map<String, SchemaElement> sourceFeatures, Map<String, SchemaElement> targetFeatures) {
    	
    	//FIXME what about composed properties?
    	if (sourceEntity instanceof ComposedProperty || targetEntity instanceof ComposedProperty) {
    		report.setFailed(original, "Composed properties not supported");
    		return null;
    	}
    	
    	IDetailedAbout sourceAbout = DetailedAbout.getDetailedAbout(sourceEntity.getAbout(), true);
    	IDetailedAbout targetAbout = DetailedAbout.getDetailedAbout(targetEntity.getAbout(), true);
    	
    	List<FeatureClass> filter = sourceEntity.getDomainRestriction();
    	List<Restriction> restrictions = new ArrayList<Restriction>();
    	if (filter != null) {
    		for (FeatureClass currentFilter : filter) {
    			restrictions.addAll(currentFilter.getAttributeValueCondition());
    		}
    	}
    	
    	try {
    		return new ModelAttributeMappingCell(
    				createAttributePath(sourceAbout, sourceFeatures), 
    				createAttributePath(targetAbout, targetFeatures),
    				restrictions);    		
    	} catch (TranslationException e) {
    		report.setFailed(original, e.getMessage());
			return null;
    	}
    }
    
    private ModelCentroidCell createCentroid(ICell original, Property sourceEntity, Property targetEntity,
    		Map<String, SchemaElement> sourceFeatures, Map<String, SchemaElement> targetFeatures) {
    	
    	//FIXME what about composed properties?
    	if (sourceEntity instanceof ComposedProperty) {
    		report.setFailed(original, "Composed source properties not supported in centroid mapping.");
    		return null;
    	}
    	
    	IDetailedAbout sourceAbout = DetailedAbout.getDetailedAbout(sourceEntity.getAbout(), true);
    	IDetailedAbout targetAbout = DetailedAbout.getDetailedAbout(targetEntity.getAbout(), true);
    	
    	List<FeatureClass> filter = sourceEntity.getDomainRestriction();
    	List<Restriction> restrictions = new ArrayList<Restriction>();
    	if (filter != null) {
    		for (FeatureClass currentFilter : filter) {
    			restrictions.addAll(currentFilter.getAttributeValueCondition());
    		}
    	}
    	
    	try {
    		return new ModelCentroidCell(
    				createAttributePath(sourceAbout, sourceFeatures), 
    				createAttributePath(targetAbout, targetFeatures),
    				restrictions);    		
    	} catch (TranslationException e) {
    		report.setFailed(original, e.getMessage());
			return null;
    	}
    }
    
    private ModelConcatenationOfAttributesCell createConcatenation(ICell original, ComposedProperty sourceEntity, Property targetEntity,
    		Map<String, SchemaElement> sourceFeatures, Map<String, SchemaElement> targetFeatures) {
    	
    	
    	if (targetEntity instanceof ComposedProperty) {
    		report.setFailed(original, "Composed properties in target entity not supported");
    		return null;
    	}
    		
    	try {
    		IDetailedAbout targetAbout = DetailedAbout.getDetailedAbout(targetEntity.getAbout(), true);
    		GmlAttributePath targetGmlAttributePath = createAttributePath(targetAbout, targetFeatures);
    		
    		List<Property> collection = sourceEntity.getCollection();    		
    		List<GmlAttributePath> sourceGmlAttributePath = new ArrayList<GmlAttributePath>();
    		for(Property property : collection) {
    			IDetailedAbout sourceAbout = DetailedAbout.getDetailedAbout(property.getAbout(), true);
    			sourceGmlAttributePath.add(createAttributePath(sourceAbout, sourceFeatures));
    		}
    		
    		String separator = original.getEntity1().getTransformation().getParameters().get(0).getValue();
    		String concatString = original.getEntity1().getTransformation().getParameters().get(1).getValue();
    		
    		List<FeatureClass> filter = sourceEntity.getDomainRestriction();
        	List<Restriction> restrictions = new ArrayList<Restriction>();
        	if (filter != null) {
        		for (FeatureClass currentFilter : filter) {
        			restrictions.addAll(currentFilter.getAttributeValueCondition());
        		}
        	}
    		
    		return new ModelConcatenationOfAttributesCell(
    			sourceGmlAttributePath, targetGmlAttributePath, separator, concatString, restrictions);
    	} catch (TranslationException e) {
    		report.setFailed(original, e.getMessage());
			return null;
    	}
    }

    /**
     * Create an attribute path from an {@link IDetailedAbout}
     * 
	 * @param about the about
	 * @param elements the available elements
	 * @return the attribute path
     * @throws TranslationException if the attribute path cannot be resolved
	 */
	private static GmlAttributePath createAttributePath(
			IDetailedAbout about,
			Map<String, SchemaElement> elements) throws TranslationException {
        GmlAttributePath binding = new GmlAttributePath();
        
        // get the parent class for the entity
        SchemaElement entityParent = elements.get(about.getNamespace() + "/" + about.getFeatureClass());
        if (entityParent == null) {
        	throw new TranslationException("Element " + about.getFeatureClass() + " not found");
        }
        TypeDefinition type = entityParent.getType();
        
        List<String> nestedParts = about.getProperties();

        for (String attributeName : nestedParts) {
        	AttributeDefinition attDef = type.getAttribute(attributeName);
        	
        	if (attDef == null) {
        		throw new TranslationException("Attribute " + attributeName + " not found");
        	}
        	
        	GmlAttribute attribute = new GmlAttribute(attDef);
			binding.add(attribute);
			
			type = attDef.getAttributeType();
        }
        
        return binding;
	}

    private ModelClassMappingCell createCell(FeatureClass sourceEntity, FeatureClass targetEntity,
            Map<String, SchemaElement> sourceFeatures, Map<String, SchemaElement> targetFeatures)
            throws TranslationException
    {
        return new ModelClassMappingCell(findFeatureElementDecl(sourceEntity, sourceFeatures),
                findFeatureElementDecl(targetEntity, targetFeatures), findFilter(sourceEntity));
    }

    private List<Restriction> findFilter(FeatureClass sourceEntity)
    {
    	List<Restriction> result = sourceEntity.getAttributeValueCondition(); 
        return (result == null)?(new ArrayList<Restriction>()):(result);
    }

    private SchemaElement findFeatureElementDecl(FeatureClass entity,
            Map<String, SchemaElement> nameToFeatureMap)
    {
        String featureName = entity.getAbout().getAbout();
        SchemaElement result = nameToFeatureMap.get(featureName);
        return result;
    }

}
