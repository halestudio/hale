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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Validate using the XML API.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class Validate {

	private static final String MESSAGE_PATTERN = "{0} at line {1}:{2}";
	private final URI[] schemaLocations;

	/**
	 * Constructor
	 * 
	 * @param schemaLocations the schema locations
	 */
	public Validate(URI[] schemaLocations) {
		super();
		this.schemaLocations = schemaLocations;
	}

	/**
	 * @see Validator#validate(InputStream)
	 */
	public void validate(InputStream xml) {
		javax.xml.validation.Schema validateSchema;
		try {
			URI mainUri = null;
			Source[] sources = new Source[schemaLocations.length];
			for (int i = 0; i < this.schemaLocations.length; i++) {
				URI schemaLocation = this.schemaLocations[i];

				if (mainUri == null) { // use first schema location for main URI
					mainUri = schemaLocation;
				}

				// load a WXS schema, represented by a Schema instance
				sources[i] = new StreamSource(schemaLocation.toURL().openStream());
			}
			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			factory.setResourceResolver(new SchemaResolver(mainUri));
			validateSchema = factory.newSchema(sources);
		} catch (Exception e) {
			throw new IllegalStateException("Error parsing schema for XML validation", e); //$NON-NLS-1$
		}
		
		final AtomicInteger warnings = new AtomicInteger(0);
		final AtomicInteger errors = new AtomicInteger(0);

		// create a Validator instance, which can be used to validate an
		// instance document
		javax.xml.validation.Validator validator = validateSchema.newValidator();
		validator.setErrorHandler(new ErrorHandler() {
			
			@Override
			public void warning(SAXParseException exception) throws SAXException {
				System.out.println(MessageFormat.format(MESSAGE_PATTERN, 
						exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber()));
				warnings.incrementAndGet();
			}
			
			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				System.err.println(MessageFormat.format(MESSAGE_PATTERN, 
						exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber()));
				errors.incrementAndGet();
			}
			
			@Override
			public void error(SAXParseException exception) throws SAXException {
				System.err.println(MessageFormat.format(MESSAGE_PATTERN, 
						exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber()));
				errors.incrementAndGet();
			}
		});

		// validate the XML document
		try {
			validator.validate(new StreamSource(xml));
		} catch (Exception e) {
			throw new IllegalStateException("Error validating XML file", e); //$NON-NLS-1$
		}
		
		System.out.println("Validation completed.");
		System.out.println(warnings.get() + " warnings");
		System.out.println(errors.get() + " errors");
	}
	
	/**
	 * @param args first argument schema file, second argument XML file 
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		File schemaFile = new File(args[0]);
		Validate val = new Validate(new URI[]{schemaFile.getAbsoluteFile().toURI()});
		File xmlFile = new File(args[1]);
		val.validate(new FileInputStream(xmlFile));
	}
}
