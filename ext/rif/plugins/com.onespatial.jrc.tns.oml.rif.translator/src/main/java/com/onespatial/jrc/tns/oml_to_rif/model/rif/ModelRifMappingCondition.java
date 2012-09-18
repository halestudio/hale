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

import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable;

/**
 * Models the rule condition filter that determines which instances of a given
 * source feature class will result in instances of the target feature class.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class ModelRifMappingCondition
{
    // string equality -> Equal: 'compare' function (test result of op = 0)
    // numeric equality-> Atom: 'numeric-equal' predicate
    // string contains -> Atom: 'contains' predicate

    private RifVariable left;
    private RifVariable right;
    private LogicalType logicalType;
    private List<ModelRifMappingCondition> children;
    private ComparisonType comparisonType;
    private Class<?> literalClass;
    private boolean isNegated = false;
    private String literalValue;

    /**
     * 
     */
    public ModelRifMappingCondition()
    {
        children = new ArrayList<ModelRifMappingCondition>();
    }

    /**
     * @return List &lt;{@link ModelRifMappingCondition}&gt;
     */
    public List<ModelRifMappingCondition> getChildren()
    {
        return children;
    }

    /**
     * @param child
     *            {@link ModelRifMappingCondition}
     */
    public void addChild(ModelRifMappingCondition child)
    {
        children.add(child);
    }

    /**
     * @return {@link RifVariable}
     */
    public RifVariable getLeft()
    {
        return left;
    }

    /**
     * @param left
     *            {@link RifVariable}
     */
    public void setLeft(RifVariable left)
    {
        this.left = left;
    }

    /**
     * @return {@link RifVariable}
     */
    public RifVariable getRight()
    {
        return right;
    }

    /**
     * @param right
     *            {@link RifVariable}
     */
    public void setRight(RifVariable right)
    {
        this.right = right;
    }

    /**
     * @return {@link ComparisonType}
     */
    public ComparisonType getOperator()
    {
        return comparisonType;
    }

    /**
     * @param comparison
     *            {@link ComparisonType}
     */
    public void setOperator(ComparisonType comparison)
    {
        comparisonType = comparison;
    }

    /**
     * @param logicalType
     *            {@link LogicalType}
     */
    public void setLogicalType(LogicalType logicalType)
    {
        this.logicalType = logicalType;
    }

    /**
     * @return {@link LogicalType}
     */
    public LogicalType getLogicalType()
    {
        return logicalType;
    }

    /**
     * @return boolean
     */
    public boolean isNegation()
    {
        return isNegated;
    }

    /**
     * @param value
     *            boolean
     */
    public void setNegated(boolean value)
    {
        isNegated = value;
    }

    /**
     * @param literalClass
     *            Class<?>
     */
    public void setLiteralClass(Class<?> literalClass)
    {
        this.literalClass = literalClass;
    }

    /**
     * @return Class<?>
     */
    public Class<?> getLiteralClass()
    {
        return literalClass;
    }

    /**
     * @return boolean
     */
    public boolean isLogical()
    {
        return logicalType != null;
    }

    /**
     * @return boolean
     */
    public boolean isComparative()
    {
        return comparisonType != null;
    }

    /**
     * @return boolean
     */
    public boolean isGeometric()
    {
        // not implemented yet
        return false;
    }

    /**
     * @return String
     */
    public String getLiteralValue()
    {
        return literalValue;
    }

    /**
     * @param value
     *            String
     */
    public void setLiteralValue(String value)
    {
        literalValue = value;
    }

}
