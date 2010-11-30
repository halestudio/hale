/*
 * Copyright (c) 1Spatial Group Ltd.
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

/**
 * Tests that exercise the {@link RifBindingToDomTranslator} component.
 * 
 * @author simonp
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
        translator = new UrlToAlignmentDigester().connect(new AlignmentToModelAlignmentDigester()
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
