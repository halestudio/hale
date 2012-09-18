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

/**
 * The different kinds of operators that can be applied to CQL strings that are
 * mapped to RIF filters.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public enum ComparisonType
{
    /**
     * Comparing two strings for equality.
     */
    STRING_EQUALS,

    /**
     * Testing to see if one string contains another string.
     */
    STRING_CONTAINS,

    /**
     * Evaluating numeric equality.
     */
    NUMBER_EQUALS,

    /**
     * Evaluating whether one number is less than another number.
     */
    NUMBER_LESS_THAN,

    /**
     * Evaluating whether one number is greater than another number.
     */
    NUMBER_GREATER_THAN;

    /**
     * RIF IRI for the numeric 'greater-than' predicate.
     */
    public static final String NUMBER_GREATER_THAN_RIF_PREDICATE_IRI = "http://" //$NON-NLS-1$
            + "www.w3.org/2007/rif-builtin-predicate#numeric-greater-than"; //$NON-NLS-1$

    /**
     * RIF IRI for the numeric 'less-than' predicate.
     */
    public static final String NUMBER_LESS_THAN_RIF_PREDICATE_IRI = "http://" //$NON-NLS-1$
            + "www.w3.org/2007/rif-builtin-predicate#numeric-less-than"; //$NON-NLS-1$

    /**
     * RIF IRI for the string 'contains' predicate.
     */
    public static final String STRING_CONTAINS_RIF_PREDICATE_IRI = "http://" //$NON-NLS-1$
            + "www.w3.org/2007/rif-builtin-predicate#contains"; //$NON-NLS-1$

    /**
     * @return String
     */
    public String getRifPredicate()
    {
        if (this.equals(NUMBER_EQUALS))
        {
            return null;
        }
        else if (this.equals(NUMBER_GREATER_THAN))
        {
            return NUMBER_GREATER_THAN_RIF_PREDICATE_IRI;
        }
        else if (this.equals(NUMBER_LESS_THAN))
        {
            return NUMBER_LESS_THAN_RIF_PREDICATE_IRI;
        }
        else if (this.equals(STRING_CONTAINS))
        {
            return STRING_CONTAINS_RIF_PREDICATE_IRI;
        }
        else if (this.equals(STRING_EQUALS))
        {
            return null;
        }
        return null;
    }

}
