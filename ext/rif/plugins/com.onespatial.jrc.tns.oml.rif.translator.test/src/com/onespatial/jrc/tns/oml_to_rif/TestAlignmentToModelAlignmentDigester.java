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
package com.onespatial.jrc.tns.oml_to_rif;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.api.Translator;
import com.onespatial.jrc.tns.oml_to_rif.digest.AlignmentToModelAlignmentDigester;
import com.onespatial.jrc.tns.oml_to_rif.digest.UrlToAlignmentDigester;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAlignment;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAttributeMappingCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelClassMappingCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelStaticAssignmentCell;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.FilterNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.LessThanNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.logical.AndNode;

import eu.esdihumboldt.hale.ui.io.legacy.mappingexport.MappingExportReport;

/**
 * Unit tests for the {@link AlignmentToModelAlignmentDigester} component.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Simon Templer / Fraunhofer IGD
 */
public class TestAlignmentToModelAlignmentDigester
{
    private Translator<URL, ModelAlignment> translator;

    /**
     * Test-level initialisation.
     */
    @Before
    public void setUp()
    {
        translator = new UrlToAlignmentDigester().connect(new AlignmentToModelAlignmentDigester(new MappingExportReport()));
    }

    /**
     * Tests translation based on the example 3 CP source dataset.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testExample3CP() throws TranslationException
    {
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example3_cp.goml"); //$NON-NLS-1$
        ModelAlignment result = translator.translate(url);
        assertNotNull(result);
        assertNotNull(result.getClassMappings());
        assertNotNull(result.getAttributeMappings());
        assertNotNull(result.getStaticAssignments());

        assertThat(result.getClassMappings().size(), is(1));

        ModelClassMappingCell modelClassMappingCell = result.getClassMappings().get(0);
        assertNotNull(modelClassMappingCell);
        assertNotNull(modelClassMappingCell.getSourceClass());
        assertNotNull(modelClassMappingCell.getTargetClass());
        assertThat(modelClassMappingCell.getSourceClass().getElementName().getLocalPart(), is("ParcelArea")); //$NON-NLS-1$
        assertThat(modelClassMappingCell.getTargetClass().getElementName().getLocalPart(), is("CadastralParcel")); //$NON-NLS-1$
        // CHECKSTYLE:OFF
        assertThat(result.getAttributeMappings().size(), is(7));
        // CHECKSTYLE:ON

        ModelAttributeMappingCell attributeMapping0 = result.getAttributeMappings().get(0);
        assertThat(attributeMapping0.getSourceAttribute().get(0).getDefinition().getName(),
                is("PCVL_PRCL_")); //$NON-NLS-1$
        assertThat(attributeMapping0.getTargetAttribute().get(0).getDefinition().getName(),
                is("inspireId")); //$NON-NLS-1$

        assertThat(result.getStaticAssignments().size(), is(1));
        ModelStaticAssignmentCell assignment0 = result.getStaticAssignments().get(0);
        assertThat(assignment0.getTarget().get(0).getDefinition().getName(), is("inspireId")); //$NON-NLS-1$
        assertThat(assignment0.getContent(), is("DP.CAD.CP")); //$NON-NLS-1$

    }

    /**
     * Tests translation based on the example 3 CP data set including a simple
     * predicate filter on the mapping of source classes to the target logical
     * schema.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testExample3CPWithFilter() throws TranslationException
    {
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example3_cp_filter.goml"); //$NON-NLS-1$
        ModelAlignment result = translator.translate(url);
        assertNotNull(result);
        assertNotNull(result.getClassMappings());
        assertNotNull(result.getAttributeMappings());
        assertNotNull(result.getStaticAssignments());

        assertThat(result.getClassMappings().size(), is(1));

        ModelClassMappingCell modelClassMappingCell = result.getClassMappings().get(0);
        assertNotNull(modelClassMappingCell);
        assertNotNull(modelClassMappingCell.getSourceClass());
        assertNotNull(modelClassMappingCell.getTargetClass());

        assertNotNull(modelClassMappingCell.getMappingConditions());
        assertThat(modelClassMappingCell.getMappingConditions().size(), is(1));
        assertNotNull(modelClassMappingCell.getMappingConditions().get(0).getRoot());
        FilterNode root = modelClassMappingCell.getMappingConditions().get(0).getRoot();
        assertThat(root, is(instanceOf(LessThanNode.class)));
        LessThanNode lessNode = (LessThanNode) root;
        assertNotNull(lessNode.getLeft());

        assertThat(lessNode.getLeft().getPropertyName(), is(equalTo("MI_PRINX"))); //$NON-NLS-1$
        assertThat(lessNode.getRight().getLiteralValue().toString(), is(equalTo("3.5"))); //$NON-NLS-1$

        assertNotNull(lessNode.getRight());

        assertThat(modelClassMappingCell.getSourceClass().getElementName().getLocalPart(), is("ParcelArea")); //$NON-NLS-1$
        assertThat(modelClassMappingCell.getTargetClass().getElementName().getLocalPart(), is("CadastralParcel")); //$NON-NLS-1$
        // CHECKSTYLE:OFF
        assertThat(result.getAttributeMappings().size(), is(7));
        // CHECKSTYLE:ON

        ModelAttributeMappingCell attributeMapping0 = result.getAttributeMappings().get(0);
        assertThat(attributeMapping0.getSourceAttribute().get(0).getDefinition().getName(),
                is("PCVL_PRCL_")); //$NON-NLS-1$
        assertThat(attributeMapping0.getTargetAttribute().get(0).getDefinition().getName(),
                is("inspireId")); //$NON-NLS-1$

        assertThat(result.getStaticAssignments().size(), is(1));
        ModelStaticAssignmentCell assignment0 = result.getStaticAssignments().get(0);
        assertThat(assignment0.getTarget().get(0).getDefinition().getName(), is("inspireId")); //$NON-NLS-1$
        assertThat(assignment0.getContent(), is("DP.CAD.CP")); //$NON-NLS-1$

    }

    /**
     * Tests translation based on the example 3 CP data set including a slightly
     * more complex predicate filter on the mapping of source classes to the
     * target logical schema. CQL Filter on which this test particularly focuses
     * is as follows.
     * <p>
     * <code>
     * PCVL_PRCL = 'a specific string' and (MI_PRINX &lt; 3.5 or ABSTRACT like
     * '%a certain keyword%').
     * </code>
     * <p>
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testExample3CPWithComplexLogicalFilter() throws TranslationException
    {
        URL url = getClass()
                .getClassLoader()
                .getResource(
                        "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example3_cp_complex_logical_filter.goml"); //$NON-NLS-1$
        ModelAlignment result = translator.translate(url);
        assertNotNull(result);
        assertNotNull(result.getClassMappings());
        assertNotNull(result.getAttributeMappings());
        assertNotNull(result.getStaticAssignments());

        assertThat(result.getClassMappings().size(), is(1));

        ModelClassMappingCell modelClassMappingCell = result.getClassMappings().get(0);
        assertNotNull(modelClassMappingCell);
        assertNotNull(modelClassMappingCell.getSourceClass());
        assertNotNull(modelClassMappingCell.getTargetClass());

        assertNotNull(modelClassMappingCell.getMappingConditions());
        assertThat(modelClassMappingCell.getMappingConditions().size(), is(1));
        assertNotNull(modelClassMappingCell.getMappingConditions().get(0).getRoot());
        FilterNode root = modelClassMappingCell.getMappingConditions().get(0).getRoot();
        assertThat(root, is(instanceOf(AndNode.class)));
        assertThat(modelClassMappingCell.getSourceClass().getElementName().getLocalPart(), is("ParcelArea")); //$NON-NLS-1$
        assertThat(modelClassMappingCell.getTargetClass().getElementName().getLocalPart(), is("CadastralParcel")); //$NON-NLS-1$
        // CHECKSTYLE:OFF
        assertThat(result.getAttributeMappings().size(), is(7));
        // CHECKSTYLE:ON

        ModelAttributeMappingCell attributeMapping0 = result.getAttributeMappings().get(0);
        assertThat(attributeMapping0.getSourceAttribute().get(0).getDefinition().getName(),
                is("PCVL_PRCL_")); //$NON-NLS-1$
        assertThat(attributeMapping0.getTargetAttribute().get(0).getDefinition().getName(),
                is("inspireId")); //$NON-NLS-1$

        assertThat(result.getStaticAssignments().size(), is(1));
        ModelStaticAssignmentCell assignment0 = result.getStaticAssignments().get(0);
        assertThat(assignment0.getTarget().get(0).getDefinition().getName(), is("inspireId")); //$NON-NLS-1$
        assertThat(assignment0.getContent(), is("DP.CAD.CP")); //$NON-NLS-1$

    }

}
