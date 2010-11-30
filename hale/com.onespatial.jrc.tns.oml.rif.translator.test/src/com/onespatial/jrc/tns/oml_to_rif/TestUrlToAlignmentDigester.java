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

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.digest.UrlToAlignmentDigester;

/**
 * Tests that exercise the {@link UrlToAlignmentDigester} component.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class TestUrlToAlignmentDigester
{
    private UrlToAlignmentDigester digester;

    /**
     * Test-level initialisation.
     */
    @Before
    public void setUp()
    {
        digester = new UrlToAlignmentDigester();
    }

    /**
     * Tests that a translation is possible using the example 1 transportation
     * source dataset.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslateExample1TNAlignment() throws TranslationException
    {
        // use this to save duplication of mapping files
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example1_tn.goml");
        HaleAlignment result = digester.translate(url);
        assertNotNull(result);
    }

}
