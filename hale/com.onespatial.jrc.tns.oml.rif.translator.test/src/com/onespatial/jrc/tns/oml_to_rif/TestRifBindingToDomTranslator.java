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

import javax.xml.bind.JAXBException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.api.Translator;
import com.onespatial.jrc.tns.oml_to_rif.digest.AlignmentToModelAlignmentDigester;
import com.onespatial.jrc.tns.oml_to_rif.digest.UrlToAlignmentDigester;
import com.onespatial.jrc.tns.oml_to_rif.document.RifBindingToDomTranslator;
import com.onespatial.jrc.tns.oml_to_rif.fixture.DomBasedUnitTest;
import com.onespatial.jrc.tns.oml_to_rif.translate.ModelAlignmentToModelRifTranslator;
import com.onespatial.jrc.tns.oml_to_rif.translate.ModelRifToRifTranslator;

import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportReport;

/**
 * Tests that exercise the {@link RifBindingToDomTranslator} component.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class TestRifBindingToDomTranslator extends DomBasedUnitTest
{

    private Translator<URL, org.w3c.dom.Document> translator;

    /**
     * Test-level initialisation.
     * 
     * @throws FactoryConfigurationError
     *             if any errors of this kind occurred creating a new
     *             {@link RifBindingToDomTranslator} if unable to configure
     * @throws ParserConfigurationException
     *             if any errors of this kind occurred creating a new
     *             {@link RifBindingToDomTranslator}
     * @throws JAXBException
     *             if any errors of this kind occurred creating a new
     *             {@link RifBindingToDomTranslator}
     */
    @Before
    public void setUp() throws JAXBException, ParserConfigurationException,
            FactoryConfigurationError
    {
        translator = new UrlToAlignmentDigester().connect(new AlignmentToModelAlignmentDigester(new MappingExportReport())
                .connect(new ModelAlignmentToModelRifTranslator().connect(
                        new ModelRifToRifTranslator()).connect(new RifBindingToDomTranslator())));
    }

    /**
     * Tests ability to translate the example 3 CP source dataset from RIF-PRD
     * Java bindings into a DOM document.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslate() throws TranslationException
    {
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example3_cp.goml");
        Document domDocument = translator.translate(url);
        assertNotNull(domDocument);
        writeDom(domDocument, System.out);
    }

}
