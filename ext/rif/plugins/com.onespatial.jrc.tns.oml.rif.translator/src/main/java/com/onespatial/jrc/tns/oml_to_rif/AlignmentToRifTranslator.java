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

import javax.xml.bind.JAXBException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.api.Translator;
import com.onespatial.jrc.tns.oml_to_rif.digest.AlignmentToModelAlignmentDigester;
import com.onespatial.jrc.tns.oml_to_rif.document.RifBindingToDomTranslator;
import com.onespatial.jrc.tns.oml_to_rif.translate.ModelAlignmentToModelRifTranslator;
import com.onespatial.jrc.tns.oml_to_rif.translate.ModelRifToRifTranslator;

import eu.esdihumboldt.hale.ui.io.legacy.mappingexport.MappingExportReport;

/**
 * Provides the entry-point to the translation functionality contained within
 * this Java library. An example of how it may be deployed within code is as
 * follows. We assume that <code>getHaleAlignment()</code> is defined elsewhere
 * in the code.
 * 
 * <pre>
 * import org.w3c.dom.Document;
 * import eu.esdihumboldt.goml.align.Alignment;
 * 
 * ...
 * 
 * Alignment alignment = getHaleAlignment();
 * try
 * {
 *     Document document = AlignmentToRifTranslator.getInstance().translate(alignment);
 *     return document;
 * }
 * catch (TranslationException e)
 * {
 *     // handle exception
 * }
 * 
 * </pre>
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Simon Templer / Fraunhofer IGD
 */
public class AlignmentToRifTranslator
{

    /**
     * Transform used to dump DOMs.
     */
    private static Transformer transformer;

    /**
     * Default constructor.
     * 
     * @throws TranslationException
     *             if any exceptions are thrown during translation
     */
    public AlignmentToRifTranslator() throws TranslationException
    {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        try
        {
            transformer = tFactory.newTransformer();
        }
        catch (TransformerConfigurationException e)
        {
            throw new TranslationException(e);
        }
    }

    /**
     * Returns the singleton instance of the {@link Translator}.
     * @param report the export report
     * 
     * @return {@link Translator}&lt;{@link HaleAlignment},
     *         {@link org.w3._2007.rif.Document}&gt;
     */
    public static Translator<HaleAlignment, Document> getInstance(MappingExportReport report)
    {
        try
        {
            return new AlignmentToModelAlignmentDigester(report)
                    .connect(new ModelAlignmentToModelRifTranslator().connect(
                            new ModelRifToRifTranslator()).connect(new RifBindingToDomTranslator()));
        }
        catch (JAXBException e)
        {
            throw new IllegalStateException(e);
        }
        catch (ParserConfigurationException e)
        {
            throw new IllegalStateException(e);
        }
        catch (FactoryConfigurationError e)
        {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @return {@link Transformer}
     */
    public static Transformer getTransformer()
    {
        return transformer;
    }

}
