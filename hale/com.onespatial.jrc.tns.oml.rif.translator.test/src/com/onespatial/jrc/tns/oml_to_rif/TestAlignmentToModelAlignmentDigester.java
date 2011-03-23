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

import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportReport;

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
