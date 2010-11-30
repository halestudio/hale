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

/**
 * The different kinds of operators that can be applied to CQL strings that are
 * mapped to RIF filters.
 * 
 * @author simonp
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
    public static final String NUMBER_GREATER_THAN_RIF_PREDICATE_IRI = "http://"
            + "www.w3.org/2007/rif-builtin-predicate#numeric-greater-than";

    /**
     * RIF IRI for the numeric 'less-than' predicate.
     */
    public static final String NUMBER_LESS_THAN_RIF_PREDICATE_IRI = "http://"
            + "www.w3.org/2007/rif-builtin-predicate#numeric-less-than";

    /**
     * RIF IRI for the string 'contains' predicate.
     */
    public static final String STRING_CONTAINS_RIF_PREDICATE_IRI = "http://"
            + "www.w3.org/2007/rif-builtin-predicate#contains";

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
