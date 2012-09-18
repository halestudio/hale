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

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.omwg.Restriction;
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
