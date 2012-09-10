package eu.esdihumboldt.commons.mediator.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

public class XMLHandler {
	/**
	 * JAXBContext Ref the base object from which a XML unmarshaller can be
	 * created
	 */
	private JAXBContext context;

	/**
	 * JAXB XML unmarshller ref
	 */
	private Unmarshaller unmarshaller;

	/**
	 * JAXB XML marshller ref
	 */
	private Marshaller marshaller;

	/**
	 * default constructor TODO add marshshelling exception
	 */
	public XMLHandler(String contextPath) {
		try {
			context = JAXBContext.newInstance(contextPath);
			unmarshaller = context.createUnmarshaller();
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (JAXBException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Unmarshall XML String to a Request object
	 * 
	 * @param xmlRequestString
	 * @return
	 * @throws UnmarshallException
	 */
	public Object unmarshall(String xmlRequestString) {
		ByteArrayInputStream is = new ByteArrayInputStream(
				xmlRequestString.getBytes());

		// unmarshall the object using JAXB this should be a message object
		Object operationRequest = unmarshallRequest(is);

		return operationRequest;
	}

	/**
	 * Marshall the response to and XML string
	 * 
	 * @param response
	 * @return
	 * @throws MarshallingException
	 */
	public String marshall(Object object, String localPart) {
		// create a message as root and add response
		Writer writer = new StringWriter();
		// marshal to string
		try {
			marshaller.marshal(new JAXBElement(new QName("uri", localPart),
					object.getClass(), object), writer);
		} catch (JAXBException exception) {
			exception.printStackTrace();
		}

		return writer.toString();
	}

	/**
	 * Creates a string reader object for the string passed as a parameter
	 * 
	 * @param request
	 * @return StringReader
	 */
	protected StringReader createStringReader(String request) {
		return new StringReader(request);
	}

	/**
	 * @param reader
	 * @return
	 */
	public Object unmarshallRequest(InputStream is) {
		// Message object is the root object always
		Object message = null;
		// use the JAXB unmarshaller to get the message
		try {
			Object object = unmarshaller.unmarshal(is);
			message = (Object) ((JAXBElement) object).getValue();
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
		return message;
	}

}
