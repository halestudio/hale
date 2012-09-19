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
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
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
        this(JAXBContext.newInstance("org.w3._2007.rif")); //$NON-NLS-1$
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
