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
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelRifDocument;
import com.onespatial.jrc.tns.oml_to_rif.translate.ModelAlignmentToModelRifTranslator;

import eu.esdihumboldt.hale.ui.io.legacy.mappingexport.MappingExportReport;

/**
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class TestModelAlignmentToModelRif
{
    private Translator<URL, ModelRifDocument> translator;

    /**
     * Test-level initialisation.
     */
    @Before
    public void setUp()
    {
        translator = new UrlToAlignmentDigester().connect(new AlignmentToModelAlignmentDigester(new MappingExportReport()))
                .connect(new ModelAlignmentToModelRifTranslator());
    }

    /**
     * Tests that it is possible to run a translation using the example 3 CP
     * source dataset.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void runIt() throws TranslationException
    {
        translator.translate(getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example3_cp.goml")); //$NON-NLS-1$
    }

    /**
     * Tests that it is possible to run a translation using the example 3 CP
     * source dataset and a predicate filter on the mapping of source classes to
     * the target logical schema.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslateFilter() throws TranslationException
    {
        ModelRifDocument result = translator.translate(getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example3_cp_filter.goml")); //$NON-NLS-1$
        assertNotNull(result);
        assertNotNull(result.getSentences());
        assertThat(result.getSentences().size(), is(1));
    }

    /**
     * Tests that it is possible to run a translation using the example 3 CP
     * source dataset and a logical negation predicate filter on the mapping of
     * source classes to the target logical schema.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslateNegationFilter() throws TranslationException
    {
        ModelRifDocument result = translator.translate(getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example1_tn_road.goml")); //$NON-NLS-1$
        assertNotNull(result);
        assertNotNull(result.getSentences());
        // CHECKSTYLE:OFF
        assertThat(result.getSentences().size(), is(3));
        // CHECKSTYLE:ON
    }

}
