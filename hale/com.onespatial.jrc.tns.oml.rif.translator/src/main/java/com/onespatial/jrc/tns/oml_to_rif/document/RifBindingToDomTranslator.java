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
package com.onespatial.jrc.tns.oml_to_rif.document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import com.onespatial.jrc.tns.oml_to_rif.api.AbstractFollowableTranslator;
import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;

/**
 * RIF-PRD binding to W3C DOM translator.
 * 
 * @author richards
 */
public class RifBindingToDomTranslator extends
        AbstractFollowableTranslator<org.w3._2007.rif.Document, org.w3c.dom.Document>
{
    /**
     * JAXB Marshaller for Radius Studio webservice bindings.
     */
    private Marshaller marshaller;

    /**
     * Document Builder used to create empty DOM documents.
     */
    private DocumentBuilder documentBuilder;

    /**
     * @throws JAXBException
     *             if unable to create a new context instance
     * @throws ParserConfigurationException
     *             if unable to configure DOM parser
     * @throws FactoryConfigurationError
     *             if unable to configure translation factory
     */
    public RifBindingToDomTranslator() throws JAXBException, ParserConfigurationException,
            FactoryConfigurationError
    {
        this(JAXBContext.newInstance("org.w3._2007.rif"));
    }

    /**
     * @param context
     *            {@link JAXBContext}
     * @throws JAXBException
     *             if unable to marshal document to XML
     * @throws ParserConfigurationException
     *             if unable to configure DOM parser
     * @throws FactoryConfigurationError
     *             if unable to configure translation factory
     */
    public RifBindingToDomTranslator(JAXBContext context) throws JAXBException,
            ParserConfigurationException, FactoryConfigurationError
    {
        marshaller = context.createMarshaller();
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    /**
     * Translates a RIF {@link org.w3._2007.rif.Document} to a DOM
     * {@link Document}.
     * 
     * @param rifDoc
     *            {@link org.w3._2007.rif.Document}
     * @return {@link Document}
     * @throws TranslationException
     *             if anything goes wrong during the translation
     */
    @Override
    public Document translate(org.w3._2007.rif.Document rifDoc) throws TranslationException
    {
        try
        {
            Document domDoc = documentBuilder.newDocument();
            marshaller.marshal(rifDoc, domDoc);
            return domDoc;
        }
        catch (JAXBException e)
        {
            throw new TranslationException(e);
        }
    }

}
