/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.translate.context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable.Type;

/**
 * @author simonp
 */
public class RifContext
{
    private RifContext parent;
    private Map<String, RifVariable> variables;

    /**
     * @param parent
     *            {@link RifContext}
     */
    public RifContext(RifContext parent)
    {
        this.parent = parent;
        this.variables = new LinkedHashMap<String, RifVariable>();
    }

    /**
     * @param name
     *            String
     * @return {@link RifVariable}
     */
    public RifVariable createVariable(String name)
    {
        RifVariable v = createActionVariable(name, false);
        v.setIsActionVar(false);
        return v;
    }

    /**
     * @param name
     *            String
     * @param isNew
     *            boolean
     * @return {@link RifVariable}
     */
    public RifVariable createActionVariable(String name, boolean isNew)
    {
        RifVariable variable = new RifVariable();
        variable.setName(name);
        variable.setIsNew(isNew);
        variable.setIsActionVar(true);
        variables.put(name, variable);
        return variable;
    }

    /**
     * Get variables of a particular type.
     * 
     * @param type
     *            the type to get.
     * @return list of all variables in context (or ancestor) of that type.
     */
    public List<RifVariable> getVariables(Type type)
    {
        List<RifVariable> result = new ArrayList<RifVariable>();
        for (RifVariable variable : getVariables())
        {
            if (variable.getType() == type)
            {
                result.add(variable);
            }
        }
        return result;
    }

    /**
     * Get all variables in context (or ancestor).
     * 
     * @return list of all variables in this context or one of its ancestors.
     */
    public List<RifVariable> getVariables()
    {
        List<RifVariable> result = new ArrayList<RifVariable>();
        if (parent != null)
        {
            result.addAll(parent.getVariables());
        }
        result.addAll(variables.values());
        return result;
    }

    /**
     * Get all the variables that are explictly in this context.
     * 
     * @return list of variables in this context.
     */
    public List<RifVariable> getLocalVariables()
    {
        List<RifVariable> result = new ArrayList<RifVariable>();
        result.addAll(variables.values());
        return result;
    }

    /**
     * @param name
     *            String
     * @return {@link RifVariable}
     */
    public RifVariable getVariable(String name)
    {
        RifVariable variable = null;
        if (parent != null)
        {
            variable = parent.getVariable(name);
        }

        if (variable == null)
        {
            variable = variables.get(name);
        }

        if (variable == null)
        {

            throw new IllegalArgumentException("Attempting to access undefined variable " + name
                    + " context " + this);
        }

        return variable;
    }

    /**
     * @see Object#toString() which this overrides.
     * @return String
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for (RifVariable variable : getVariables())
        {
            builder.append("\n\t").append(variable.summary());
        }
        return builder.toString();
    }
}
