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
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example1_tn.goml"); //$NON-NLS-1$
        HaleAlignment result = digester.translate(url);
        assertNotNull(result);
    }

}
