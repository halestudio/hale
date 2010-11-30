/*
 * Copyright (c) 1Spatial Group Ltd.
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
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelClassMappingCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelStaticAssignmentCell;
import com.onespatial.jrc.tns.oml_to_rif.schema.GmlAttribute;
import com.onespatial.jrc.tns.oml_to_rif.schema.GmlAttributePath;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.cst.align.ext.IParameter;
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
 * @author simonp
 * @author Simon Templer
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
            	// property mapping
            	ModelAttributeMappingCell modelCell = createAttribute(cell,
            			(Property) sourceEntity, (Property) targetEntity, 
            			sourceFeatures, targetFeatures);
            	if (modelCell != null) {
            		attributeMappings.add(modelCell);
            	}
            }
            else if (sourceIsFeatureClass && !targetIsFeatureClass)
            {
            	// augmentations
            	ModelStaticAssignmentCell modelCell = createStaticAssignment(cell, (Property) targetEntity, targetFeatures);
            	if (modelCell != null) {
            		staticAssigments.add(modelCell);
            	}
            }
            else
            {
                throw new TranslationException("Unhandled combination");
            }

        }

        return new ModelAlignment(classMappings, attributeMappings,
                staticAssigments);

    }

    private ModelStaticAssignmentCell createStaticAssignment(ICell original, 
    		Property targetEntity, Map<String, SchemaElement> targetFeatures)
    {
        boolean supported = false;
    	String content = null;
        for (IParameter param : targetEntity.getTransformation().getParameters())
        {
            if (param.getName().equals("defaultValue"))
            {
                content = param.getValue();
                supported = true;
                break;
            }
        }
        
        if (!supported) {
        	//XXX what about other augmentation types?
        	report.setFailed(original, "Only default value augmentations supported");
        	return null;
        }
        else {
	        IDetailedAbout targetAbout = DetailedAbout.getDetailedAbout(targetEntity.getAbout(), true);
	        try {
				return new ModelStaticAssignmentCell(
						createAttributePath(targetAbout, targetFeatures), content);
			} catch (TranslationException e) {
				report.setFailed(original, e.getMessage());
				return null;
			}
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
    	
    	try {
			return new ModelAttributeMappingCell(
					createAttributePath(sourceAbout, sourceFeatures), 
					createAttributePath(targetAbout, targetFeatures));
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
        	
        	GmlAttribute attribute = new GmlAttribute(attDef);
			binding.add(attribute);
			
			type = attDef.getAttributeType();
        }
        
        return binding;
	}

//    private Map<String, XSElementDecl> buildFeatureMap(SchemaBrowser sourceBrowser)
//            throws TranslationException
//    {
//        try
//        {
//            List<XSElementDecl> sourceFeatureDecls = sourceBrowser.getFeatureClassNames();
//            Map<String, XSElementDecl> sourceFeatures = new LinkedHashMap<String, XSElementDecl>();
//            for (XSElementDecl featureDecl : sourceFeatureDecls)
//            {
//                sourceFeatures.put(featureDecl.getTargetNamespace() + "/" + featureDecl.getName(),
//                        featureDecl);
//            }
//            return sourceFeatures;
//        }
//        catch (SAXException e)
//        {
//            throw new TranslationException(e);
//        }
//    }

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
