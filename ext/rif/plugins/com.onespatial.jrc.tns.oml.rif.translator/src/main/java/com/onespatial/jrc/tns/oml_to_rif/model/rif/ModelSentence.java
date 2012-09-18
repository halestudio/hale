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
 *     1Spatial PLC <http://www.1spatial.com>
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
 */
public class ModelSentence extends RifContext
{
    private RifVariable sourceClass;
    private RifVariable targetClass;
    private List<ModelRifMappingCondition> mappingConditions;
    private List<PropertyMapping> propertyMappings;
    private List<StaticAssignment> staticAssignments;

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

}
