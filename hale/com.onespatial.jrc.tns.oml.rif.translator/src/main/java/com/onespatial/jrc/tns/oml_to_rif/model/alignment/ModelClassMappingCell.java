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
import com.sun.xml.xsom.XSElementDecl;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * An interim model of a {@link Cell} within an {@link Alignment} that defines a
 * mapping between source and target classes. Used as a stage in translation to
 * RIF-PRD.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class ModelClassMappingCell
{
    private SchemaElement sourceClass;
    private SchemaElement targetClass;
    private List<ModelMappingCondition> mappingConditions;
    private static final Translator<Filter, ModelMappingCondition> DIGESTER;

    static
    {
        DIGESTER = new CqlToMappingConditionTranslator();
    }

    /**
     * @param sourceClass
     *            {@link XSElementDecl}
     * @param targetClass
     *            {@link XSElementDecl}
     * @param filterRestrictions
     *            List&lt;{@link Restriction}&gt;
     * @throws TranslationException
     *             if unable to build the filters
     */
    public ModelClassMappingCell(SchemaElement sourceClass, SchemaElement targetClass,
            List<Restriction> filterRestrictions) throws TranslationException
    {
        super();
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.mappingConditions = buildMappingConditions(filterRestrictions);
    }

    /**
     * @return {@link XSElementDecl}
     */
    public SchemaElement getSourceClass()
    {
        return sourceClass;
    }

    /**
     * @return {@link XSElementDecl}
     */
    public SchemaElement getTargetClass()
    {
        return targetClass;
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
