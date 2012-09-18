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

import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.api.Translator;
import com.onespatial.jrc.tns.oml_to_rif.digest.AlignmentToModelAlignmentDigester;
import com.onespatial.jrc.tns.oml_to_rif.digest.UrlToAlignmentDigester;
import com.onespatial.jrc.tns.oml_to_rif.fixture.DomBasedUnitTest;
import com.onespatial.jrc.tns.oml_to_rif.translate.ModelAlignmentToModelRifTranslator;
import com.onespatial.jrc.tns.oml_to_rif.translate.ModelRifToRifTranslator;

import eu.esdihumboldt.hale.ui.io.legacy.mappingexport.MappingExportReport;

/**
 * Tests that focus on the transportation data theme.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class TestTransportationDataTheme extends DomBasedUnitTest
{
    private Translator<URL, org.w3._2007.rif.Document> translator;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp()
    {
        translator = new UrlToAlignmentDigester().connect(new AlignmentToModelAlignmentDigester(new MappingExportReport())
                .connect(new ModelAlignmentToModelRifTranslator()
                        .connect(new ModelRifToRifTranslator())));
    }

    /**
     * Test of a translation using the Example 2 Transport/Rail source dataset.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     * @throws JAXBException
     *             if unable to convert the RIF output into a DOM document
     * @throws ParserConfigurationException
     *             if unable to configure a parser to handle the conversion from
     *             RIF to DOM
     */
    // @Test
    public void testExample2TransportRail() throws TranslationException, JAXBException,
            ParserConfigurationException
    {
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example2_tn_rail.goml"); //$NON-NLS-1$
        org.w3._2007.rif.Document doc = translator.translate(url);
        writeDom(getDomFromRif(doc), System.out);
    }

    /**
     * Test of a translation using the Example 2 Transport/Road source dataset.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     * @throws JAXBException
     *             if unable to convert the RIF output into a DOM document
     * @throws ParserConfigurationException
     *             if unable to configure a parser to handle the conversion from
     *             RIF to DOM
     */
    @Test
    public void testExample2TransportRoad() throws TranslationException, JAXBException,
            ParserConfigurationException
    {
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example2_tn_road.goml"); //$NON-NLS-1$
        org.w3._2007.rif.Document doc = translator.translate(url);
        writeDom(getDomFromRif(doc), System.out);
    }

    /**
     * Test of a translation using the Example 2 Transport/Water source dataset.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     * @throws JAXBException
     *             if unable to convert the RIF output into a DOM document
     * @throws ParserConfigurationException
     *             if unable to configure a parser to handle the conversion from
     *             RIF to DOM
     */
    @Test
    public void testExample2TransportWater() throws TranslationException, JAXBException,
            ParserConfigurationException
    {
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example2_tn_water.goml"); //$NON-NLS-1$
        org.w3._2007.rif.Document doc = translator.translate(url);
        writeDom(getDomFromRif(doc), System.out);
    }

    /**
     * Test of a translation using the Example 1 Transport/Water source dataset.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     * @throws JAXBException
     *             if unable to convert the RIF output into a DOM document
     * @throws ParserConfigurationException
     *             if unable to configure a parser to handle the conversion from
     *             RIF to DOM
     */
    @Test
    public void testExample1TransportRoad() throws TranslationException, JAXBException,
            ParserConfigurationException
    {
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example1_tn_road.goml"); //$NON-NLS-1$
        org.w3._2007.rif.Document doc = translator.translate(url);
        writeDom(getDomFromRif(doc), System.out);
    }

    /**
     * Test of a translation using the Example 1 Transport/Rail source dataset.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     * @throws JAXBException
     *             if unable to convert the RIF output into a DOM document
     * @throws ParserConfigurationException
     *             if unable to configure a parser to handle the conversion from
     *             RIF to DOM
     */
    @Test
    public void testExample1TransportRail() throws TranslationException, JAXBException,
            ParserConfigurationException
    {
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example1_tn_rail.goml"); //$NON-NLS-1$
        org.w3._2007.rif.Document doc = translator.translate(url);
        writeDom(getDomFromRif(doc), System.out);

    }

}
