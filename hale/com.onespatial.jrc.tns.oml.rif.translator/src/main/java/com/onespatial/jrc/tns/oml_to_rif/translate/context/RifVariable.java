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
package com.onespatial.jrc.tns.oml_to_rif.translate.context;

/**
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class RifVariable
{
    /**
     * 
     */
	public enum Type
    {
        /**
         * Variable type is unknown.
         */
        UNKNOWN,
        /**
         * Instance variable.
         */
        INSTANCE,
        /**
         * Attribute variable.
         */
        ATTRIBUTE
    }

    private String className;
    private String name;
    private boolean isNew;
    private Type type;
    private String propertyName;
    private RifVariable contextVariable;
    private boolean isActionVar;

    /**
     * Default constructor initialises to type UNKNOWN.
     */
    public RifVariable()
    {
        type = Type.UNKNOWN;
    }

    /**
     * @param name
     *            String
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @param isNew
     *            boolean
     */
    public void setIsNew(boolean isNew)
    {
        this.isNew = isNew;
    }

    /**
     * @param className
     *            String
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    /**
     * @return String
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return boolean
     */
    public boolean isNew()
    {
        return isNew;
    }

    /**
     * @return {@link Type}
     */
    public Type getType()
    {
        return type;
    }

    /**
     * @param type
     *            {@link Type}
     */
    public void setType(Type type)
    {
        this.type = type;
    }

    /**
     * @return String
     */
    public String getAttributeName()
    {
        return propertyName;
    }

    /**
     * @param propertyName
     *            String
     */
    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }

    /**
     * @see Object#toString() which this overrides.
     * @return String
     */
    @Override
    public String toString()
    {
        return "?" + name; //$NON-NLS-1$
    }

    /**
     * A convenience method suitable for debugging use, that shows a clear
     * representation of the state of this instance.
     * 
     * @return String
     */
    public String summary()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("?").append(name).append(" ("); //$NON-NLS-1$ //$NON-NLS-2$

        if (contextVariable != null)
        {
            builder.append("Context=").append(contextVariable.getName()).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (type == Type.INSTANCE)
        {
            builder.append(" Class=").append(className).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
            if (isNew)
            {
                builder.append(" New "); //$NON-NLS-1$
            }
        }
        else if (type == Type.ATTRIBUTE)
        {
            builder.append(" Property=").append(propertyName).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else
        {
            builder.append(" Unknown Type"); //$NON-NLS-1$
        }

        builder.append(")"); //$NON-NLS-1$

        return builder.toString();
    }

    /**
     * @return String
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /**
     * Will recursively traverse context variable until a context variable of
     * type INSTANCE is found.
     * 
     * @return instance of which this is a variable.
     */
    public RifVariable getInstanceVariable()
    {
        if (type == Type.INSTANCE)
        {
            return this;
        }
        else if (type == Type.ATTRIBUTE)
        {
            if (contextVariable == null)
            {
                throw new IllegalArgumentException(
                        "Attribute with no context found while trype to find instance variable."); //$NON-NLS-1$
            }
            else
            {
                return contextVariable.getInstanceVariable();
            }
        }
        else
        {
            throw new IllegalArgumentException(
                    "Variable of unknown type encounter while trying to find instance variable."); //$NON-NLS-1$
        }
    }

    /**
     * @return {@link RifVariable}
     */
    public RifVariable getContextVariable()
    {
        return contextVariable;
    }

    /**
     * @param instanceVariable
     *            {@link RifVariable}
     */
    public void setContextVariable(RifVariable instanceVariable)
    {
        this.contextVariable = instanceVariable;
    }

    /**
     * @return boolean
     */
    public boolean isActionVar()
    {
        return isActionVar;
    }

    /**
     * @param isAction
     *            boolean
     */
    public void setIsActionVar(boolean isAction)
    {
        isActionVar = isAction;
    }

}
