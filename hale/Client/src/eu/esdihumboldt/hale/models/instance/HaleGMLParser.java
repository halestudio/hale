/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.models.instance;

import org.apache.xerces.parsers.SAXParser;
import org.eclipse.xsd.XSDSchema;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.NamespaceSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.xml.Configuration;
import org.geotools.xml.impl.ParserHandler;
import org.geotools.xs.XS;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleGMLParser {
	
    /** sax handler which maintains the element stack */
    private ParserHandler handler;

    /** the sax parser driving the handler */
    private SAXParser parser;

    /** the instance document being parsed */
    private InputStream input;

    /**
     * Creats a new instance of the parser.
     *
     * @param configuration The parser configuration, bindings and context,
     *         must never be <code>null</code>.
     *
     */
    public HaleGMLParser(Configuration configuration) {
        if (configuration == null) {
            throw new NullPointerException("configuration");
        }

        handler = new ParserHandler(configuration);
    }

    /**
     * Signals the parser to parse the entire instance document. The object
     * returned from the parse is the object which has been bound to the root
     * element of the document. This method should only be called once for
     * a single instance document.
     *
     * @return The object representation of the root element of the document.
     *
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     *
     * @deprecated use {@link #parse(InputStream)}
     */
    public Object parse() throws IOException, SAXException, ParserConfigurationException {
        return parse(input);
    }

    /**
     * Parses an instance documented defined by an input stream.
     * <p>
     * The object returned from the parse is the object which has been bound to the root
     * element of the document. This method should only be called once for a single instance document.
     * </p>
     *
     * @return The object representation of the root element of the document.
     *
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public Object parse(InputStream input)
        throws IOException, SAXException, ParserConfigurationException {
        return parse(new InputSource(input));
    }

    /**
     * Parses an instance documented defined by a reader.
     * <p>
     * The object returned from the parse is the object which has been bound to the root
     * element of the document. This method should only be called once for a single instance document.
     * </p>
     *
     * @return The object representation of the root element of the document.
     *
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public Object parse(Reader reader)
        throws IOException, SAXException, ParserConfigurationException {
        return parse(new InputSource(reader));
    }

    /**
     * Parses an instance documented defined by a sax input source.
     * <p>
     * The object returned from the parse is the object which has been bound to the root
     * element of the document. This method should only be called once for a single instance document.
     * </p>
     *
     * @return The object representation of the root element of the document.
     *
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public Object parse(InputSource source)
        throws IOException, SAXException, ParserConfigurationException {
        parser = parser();
        parser.setContentHandler(handler);
        parser.setErrorHandler(handler);

        parser.parse(source);

        return handler.getValue();
    }

    /**
     * Sets the strict parsing flag.
     * <p>
     * When set to <code>true</code>, this will cause the parser to operate in
     * a strict mode, which means that xml being parsed must be exactly correct
     * with respect to the schema it references.
     * </p>
     * <p>
     * Some examples of cases in which the parser will throw an exception while
     * operating in strict mode:
     * <ul>
     *  <li>no 'schemaLocation' specified, or specified incorrectly
     *  <li>element found which is not declared in the schema
     * </ul>
     * </p>
     * @param strict The strict flag.
     */
    public void setStrict(boolean strict) {
        handler.setStrict(strict);
    }

    /**
     * Sets the flag controlling wether the parser should validate or not.
     *
     * @param validating Validation flag, <code>true</code> to validate, otherwise <code>false</code>
     */
    public void setValidating(boolean validating) {
        handler.setValidating(validating);
    }

    /**
     * @return Flag determining if the parser is validatin or not.
     */
    public boolean isValidating() {
        return handler.isValidating();
    }
    
    /**
     * Sets the flag which controls how the parser handles validation errors.
     * <p>
     * When this flag is set, the parser will throw an exception when it encounters 
     * a validation error. Otherise the error will be stored, retreivable from 
     * {@link #getValidationErrors()}.
     * </p>
     * <p>
     * The default behavior is to set this flag to <code>false</code>. So client
     * code should explicitly set this flag if it is desired that the exception 
     * be thrown when the validation error occurs.
     * </p>
     * @param fail failure flag, <code>true</code> to fail, otherwise <code>false</code>
     */
    public void setFailOnValidationError( boolean fail ) {
        handler.setFailOnValidationError( fail );
    }
    
    /**
     * @return The flag determining how the parser deals with validation errors.
     */
    public boolean isFailOnValidationError() {
        return handler.isFailOnValidationError();
    }
    
    /**
     * Returns a list of any validation errors that occured while parsing.
     *
     * @return A list of errors, or an empty list if none.
     */
    public List getValidationErrors() {
        return handler.getValidationErrors();
    }

    /**
     * Returns the schema objects referenced by the instance document being
     * parsed. This method can only be called after a successful parse has
     * begun.
     *
     * @return The schema objects used to parse the document, or null if parsing
     * has not commenced.
     */
    public XSDSchema[] getSchemas() {
        if (handler != null) {
            return handler.getSchemas();
        }

        return null;
    }

    /**
     * Returns the namespace mappings maintained by the parser.
     * <p>
     * Clients may register additional namespace mappings. This is useful when
     * an application whishes to provide some "default" namespace mappings.
     * </p>
     * <p>
     * Clients should register namespace mappings in the current "context", ie
     * do not call {@link NamespaceSupport#pushContext()}. Example:
     * <code>
     * Parser parser = new Parser( ... );
     * parser.getNamespaces().declarePrefix( "foo", "http://www.foo.com" );
     * ...
     * </code>
     * </p>
     *
     * @return The namespace support containing prefix to uri mappings.
     * @since 2.4
     */
    public NamespaceSupport getNamespaces() {
        return handler.getNamespaceSupport();
    }

    protected SAXParser parser() throws ParserConfigurationException, SAXException {
        //JD: we use xerces directly here because jaxp does seem to allow use to 
        // override all the namespaces to validate against
        SAXParser parser = new SAXParser();

        //set the appropriate features
        parser.setFeature("http://xml.org/sax/features/namespaces", true);

        if (handler.isValidating()) {
            parser.setFeature("http://xml.org/sax/features/validation", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
        }

        //set the schema sources of this configuration, and all dependent ones
        StringBuffer schemaLocation = new StringBuffer();

        for (Iterator d = handler.getConfiguration().allDependencies().iterator(); d.hasNext();) {
            Configuration dependency = (Configuration) d.next();

            //ignore xs namespace
            if (XS.NAMESPACE.equals(dependency.getNamespaceURI())) {
                continue;
            }

            //seperate entries by space
            if (schemaLocation.length() > 0) {
                schemaLocation.append(" ");
            }

            //add the entry
            schemaLocation.append(dependency.getNamespaceURI());
            schemaLocation.append(" ");
            schemaLocation.append(dependency.getSchemaFileURL());
        }

        //set hte property to map namespaces to schema locations
        parser.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation",
            schemaLocation.toString());

        //set the default location
        parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
            handler.getConfiguration().getSchemaFileURL());

        return parser;
    }

}
