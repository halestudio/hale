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

package com.onespatial.jrc.tns.oml_to_rif.model.alignment;

import java.util.ArrayList;
import java.util.List;

import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.api.Translator;
import com.onespatial.jrc.tns.oml_to_rif.digest.CqlToMappingConditionTranslator;

import eu.esdihumboldt.goml.omwg.Restriction;

/**
 * Holds filters for class mappings and attribute mappings.
 * 
 * @author Susanne Reinwarth / TU Dresden
 */
public abstract class AbstractModelFilter {
	
	private List<ModelMappingCondition> mappingConditions;
    private static final Translator<Filter, ModelMappingCondition> DIGESTER;

    static
    {
        DIGESTER = new CqlToMappingConditionTranslator();
    }
	
	/**
	 * @param filterRestrictions
     *            List&lt;{@link Restriction}&gt; 
	 * @throws TranslationException
     *             if unable to build the filters
	 */
	public AbstractModelFilter(List<Restriction> filterRestrictions)
			throws TranslationException
	{
		this.mappingConditions = buildMappingConditions(filterRestrictions);
	}
	
	/**
     * @return List&lt;{@link ModelMappingCondition}&gt;
     */
    public List<ModelMappingCondition> getMappingConditions()
    {
        return mappingConditions;
    }

    private List<ModelMappingCondition> buildMappingConditions(List<Restriction> mappingRestrictions)
            throws TranslationException
    {
        List<ModelMappingCondition> result;
        try
        {
            result = new ArrayList<ModelMappingCondition>();

            for (Restriction restriction : mappingRestrictions)
            {
                ModelMappingCondition condition = DIGESTER.translate(CQL.toFilter(restriction
                        .getCqlStr()));
                result.add(condition);
            }
        }
        catch (CQLException e)
        {
            throw new TranslationException(e);
        }

        return result;
    }
}
