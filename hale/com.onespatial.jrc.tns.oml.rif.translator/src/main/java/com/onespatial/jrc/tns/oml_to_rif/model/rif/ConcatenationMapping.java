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

import java.util.List;
import java.util.ArrayList;

import org.w3._2007.rif.Sentence;

import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable;

/**
 * Holds the data required to construct a concatenation-of-attributes-mapping
 * within a RIF {@link Sentence}.
 * 
 * @author Susanne Reinwarth / TU Dresden
 */
public class ConcatenationMapping
{
    private List<RifVariable> sources = new ArrayList<RifVariable>();
    private RifVariable target;
    private String separator;
    private String concatString;
    
    /**
     * constructor
     * @param sources {@link List}&lt{@link RifVariable}&gt
     * @param target {@link RifVariable}
     * @param separator {@link String}
     * @param concatString {@link String} 
     */
    public ConcatenationMapping(List<RifVariable> sources, RifVariable target,
    		String separator, String concatString)
    {
        super();
        this.sources = sources;
        this.target = target;
        this.separator = separator;
        this.concatString = concatString;
    }

    /**
     * @return {@link List}&lt{@link RifVariable}&gt
     */
    public List<RifVariable> getSources()
    {
        return sources;
    }

    /**
     * @return {@link RifVariable}
     */
    public RifVariable getTarget()
    {
        return target;
    }
    
    /**
     * @return {@link String}
     */
    public String getSeparator()
    {
    	return separator;
    }
    
    /**
     * @return {@link String}
     */
    public String getConcatString()
    {
    	return concatString;
    }

    /**
     * @param att {@link RifVariable}
     */
    public void setTarget(RifVariable att)
    {
        target = att;
    }

}
