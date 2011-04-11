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
package com.onespatial.jrc.tns.oml_to_rif.model.rif;

import java.util.ArrayList;
import java.util.List;

import org.w3._2007.rif.Sentence;

import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifContext;
import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable;
import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable.Type;

/**
 * A class that contains the raw materials for constructing a RIF
 * {@link Sentence}.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Susanne Reinwarth / TU Dresden
 */
public class ModelSentence extends RifContext
{
    private RifVariable sourceClass;
    private RifVariable targetClass;
    private List<ModelRifMappingCondition> mappingConditions;
    private List<PropertyMapping> propertyMappings;
    private List<StaticAssignment> staticAssignments;
    private List<ConcatenationMapping> concatenationMappings;
    private List<CentroidMapping> centroidMappings;
    private List<IdentifierMapping> identifierMappings;
    private boolean attributeFilterSentence;

    private int targetIdx = 1;
    private int sourceIdx = 1;

    /**
     * Default constructor.
     */
    public ModelSentence()
    {
        super(null);
        propertyMappings = new ArrayList<PropertyMapping>();
        staticAssignments = new ArrayList<StaticAssignment>();
        mappingConditions = new ArrayList<ModelRifMappingCondition>();
        concatenationMappings = new ArrayList<ConcatenationMapping>();
        centroidMappings = new ArrayList<CentroidMapping>();
        identifierMappings = new ArrayList<IdentifierMapping>();
        attributeFilterSentence = false;
    }

    /**
     * @return {@link ModelRifMappingCondition}
     */
    public List<ModelRifMappingCondition> getMappingConditions()
    {
        return mappingConditions;
    }

    /**
     * @param condition
     *            {@link ModelRifMappingCondition}
     */
    public void addMappingCondition(ModelRifMappingCondition condition)
    {
        mappingConditions.add(condition);

    }

    /**
     * @param variableName
     *            {@link String}
     * @param className
     *            {@link String}
     */
    public void setTargetClass(String variableName, String className)
    {
        targetClass = super.createActionVariable(variableName, true);
        targetClass.setClassName(className);
        targetClass.setType(Type.INSTANCE);
    }
    
    /**
     * @param variableName
     *            {@link String}
     * @param className
     *            {@link String}
     */
    public void setSourceClass(String variableName, String className)
    {
        sourceClass = super.createVariable(variableName);
        sourceClass.setClassName(className);
        sourceClass.setType(Type.INSTANCE);

    }

    /**
     * @return {@link RifVariable}
     */
    public RifVariable getSourceClass()
    {
        return sourceClass;
    }

    /**
     * @return {@link RifVariable}
     */
    public RifVariable getTargetClass()
    {
        return targetClass;
    }

    /**
     * @return List &lt;{@link PropertyMapping}
     */
    public List<PropertyMapping> getPropertyMappings()
    {
        return propertyMappings;
    }

    /**
     * @param mapping
     *            {@link PropertyMapping}
     */
    public void addPropertyMapping(PropertyMapping mapping)
    {
        propertyMappings.add(mapping);
    }
    
    /**
     * @param mapping {@link ConcatenationMapping}
     */
    public void addConcatenationMapping(ConcatenationMapping mapping)
    {
    	concatenationMappings.add(mapping);
    }
    
    /**
     * @return {@link List}&lt{@link ConcatenationMapping}&gt
     */
    public List<ConcatenationMapping> getConcatenationMappings()
    {
    	return concatenationMappings;
    }

    /**
     * @param assignment
     *            {@link StaticAssignment}
     */
    public void addStaticAssigment(StaticAssignment assignment)
    {
        staticAssignments.add(assignment);
    }

    /**
     * @return List&lt;{@link StaticAssignment}&gt;
     */
    public List<StaticAssignment> getStaticAssignments()
    {
        return staticAssignments;
    }

    /**
     * @param staticAssignments
     *            List&lt;{@link StaticAssignment}&gt;
     */
    public void setStaticAssignments(List<StaticAssignment> staticAssignments)
    {
        this.staticAssignments = staticAssignments;
    }
    
    /**
     * @param mapping {@link CentroidMapping}
     */
    public void addCentroidMapping(CentroidMapping mapping)
    {
    	centroidMappings.add(mapping);
    }
    
    /**
     * @return {@link List}&lt{@link CentroidMapping}&gt
     */
    public List<CentroidMapping> getCentroidMappings()
    {
    	return centroidMappings;
    }
    
    /**
     * @param mapping {@link IdentifierMapping}
     */
    public void addIdentifierMapping(IdentifierMapping mapping)
    {
    	identifierMappings.add(mapping);
    }
    
    /**
     * @return {@link List}&lt{@link IdentifierMapping}&gt
     */
    public List<IdentifierMapping> getIdentifierMappings()
    {
    	return identifierMappings;
    }

    /**
     * @return int the next target index number
     */
    public int getNextTargetIdx()
    {
        return targetIdx++;
    }

    /**
     * @return the next source index number
     */
    public int getNextSourceIdx()
    {
        return sourceIdx++;
    }

    /**
     * @param requiredContext
     *            {@link RifVariable}
     * @param propertyName
     *            {@link String}
     * @return {@link RifVariable}
     */
    public RifVariable findChildAttribute(RifVariable requiredContext, String propertyName)
    {
        RifVariable result = null;
        for (RifVariable candidate : getVariables())
        {
            if (candidate.getType() == Type.ATTRIBUTE
                    && candidate.getPropertyName().equals(propertyName)
                    && candidate.getContextVariable().equals(requiredContext))
            {
                result = candidate;
                break;
            }
        }

        return result;
    }

    /**
     * @param instanceVariable
     *            {@link RifVariable} the parent {@link RifVariable} for which
     *            to search for children
     * @return List&lt;{@link RifVariable}&gt; the list of children
     */
    public List<RifVariable> findChildren(RifVariable instanceVariable)
    {
        List<RifVariable> result = new ArrayList<RifVariable>();
        for (RifVariable var : this.getVariables())
        {
            RifVariable context = var.getContextVariable();
            if (context != null && context.equals(instanceVariable))
            {
                result.add(var);
            }
        }
        return result;
    }
    
    /**
     * @param is true if sentence is an attribute filter sentence
     */
    public void setAttributeFilterSentence (boolean is)
    {
    	attributeFilterSentence = is;
    }
    
    /**
     * @return true if sentence is an attribute filter sentence
     */
    public boolean isAttributeFilterSentence() {
    	return attributeFilterSentence;
    }
}
