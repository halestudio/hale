/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.fixture;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Test fixture class for facilitating tests requiring to manipulate DOM
 * documents.
 * 
 * @author simonp
 */
public abstract class DomBasedUnitTest
{
    /**
     * For logging of test output by child classes.
     */
    protected Log log = LogFactory.getLog(DomBasedUnitTest.class);

    /**
     * Document builder to parse test artifacts into DOM.
     */
    private static DocumentBuilder builder;

    /**
     * Transform used to dump DOMs.
     */
    private static Transformer transformer;

    /**
     * Run once, before any tests are run.
     * 
     * @throws FactoryConfigurationError
     *             if any errors occurred configuring the
     *             {@link DocumentBuilderFactory}
     * @throws ParserConfigurationException
     *             if any errors occurred creating a {@link DocumentBuilder}
     * @throws TransformerConfigurationException
     *             if any errors occurred configuring the
     *             {@link TransformerFactory}
     */
    @BeforeClass
    public static void beforeAnyTest() throws ParserConfigurationException,
            FactoryConfigurationError, TransformerConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        builder = factory.newDocumentBuilder();

        TransformerFactory tFactory = TransformerFactory.newInstance();
        transformer = tFactory.newTransformer();
    }

    /**
     * Load a DOM from the class path.
     * 
     * @param path
     *            the name of the XML file.
     * @return the DOM of the XML file.
     * @throws IOException
     *             if any errors occurred acquiring an {@link InputStream} to
     *             read the given XML file
     * @throws SAXException
     *             if any errors occurred parsing the XML file
     */
    protected Document loadDom(String path) throws SAXException, IOException
    {
        InputStream rifStream = getClass().getResourceAsStream(path);
        if (rifStream == null)
        {
            throw new IllegalArgumentException("Could not find " + path + " on class path.");
        }

        return builder.parse(rifStream);
    }

    /**
     * @param actualDom
     *            {@link Document}
     * @param expectedContent
     *            String
     * @throws SAXException
     *             if any errors occurred parsing the content
     * @throws IOException
     *             if any errors occurred acquiring an {@link InputStream} to
     *             read the content
     */
    protected void testDom(Document actualDom, String expectedContent) throws SAXException,
            IOException
    {
        Document expectedDom = loadDom(expectedContent);

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        Diff diff = new Diff(expectedDom, actualDom);

        if (!diff.similar())
        {
            log.debug("Expected:");
            writeDom(expectedDom, System.out);
            log.debug("\n\nActual:");
            writeDom(actualDom, System.out);
            log.debug(diff);
        }

        assertThat(diff.similar(), is(true));
        // assertThat(diff.identical(), is(true));
    }

    /**
     * Pretty Print a DOM to a stream.
     * 
     * @param document
     *            the DOM to print.
     * @param dest
     *            the stream to write to.
     */
    protected void writeDom(Document document, OutputStream dest)
    {
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(dest);
        try
        {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
        }
        catch (TransformerException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * @param rifDocument
     *            {@link org.w3._2007.rif.Document}
     * @return {@link Document}
     * @throws JAXBException
     *             if any errors occurred trying to marshall the RIF content to
     *             an DOM document
     * @throws ParserConfigurationException
     */
    protected org.w3c.dom.Document getDomFromRif(org.w3._2007.rif.Document rifDocument)
            throws JAXBException
    {
        org.w3c.dom.Document domDocument = builder.newDocument();
        JAXBContext jc = JAXBContext.newInstance("org.w3._2007.rif", getClass().getClassLoader());
        jc.createMarshaller().marshal(
                new JAXBElement<org.w3._2007.rif.Document>(new QName("http://www.w3.org/2007/rif#",
                        "Document", "rif"), org.w3._2007.rif.Document.class, rifDocument),
                domDocument);
        return domDocument;
    }
}
