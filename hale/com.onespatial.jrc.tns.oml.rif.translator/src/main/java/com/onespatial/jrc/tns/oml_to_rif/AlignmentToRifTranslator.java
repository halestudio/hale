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

import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportReport;

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
