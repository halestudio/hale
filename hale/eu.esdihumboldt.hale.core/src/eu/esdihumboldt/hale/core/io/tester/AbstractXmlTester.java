/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.core.io.tester;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import eu.esdihumboldt.hale.core.io.ContentTypeTester;

/**
 * Abstract testing class for XML-files by implementing ContentTypeTester
 * 
 * @author Patrick Lieb
 */
public abstract class AbstractXmlTester implements ContentTypeTester {

	/**
	 * @see eu.esdihumboldt.hale.core.io.ContentTypeTester#matchesContentType(java.io.InputStream)
	 */
	@Override
	public boolean matchesContentType(InputStream in) {

		if (in == null)
			throw new IllegalStateException("Couldn't load instance input");

		BufferedInputStream bin = new BufferedInputStream(in);

		XMLStreamReader reader;

		try {
			reader = XMLInputFactory.newInstance().createXMLStreamReader(bin);
		} catch (Throwable e1) {
			try {
				bin.close();
			} catch (IOException e) {
				// ignore
			}
			return false;

		}

		try {
			return testReader(reader);
		} catch (XMLStreamException e) {
			return false;

		} catch (Throwable e) {
			throw new IllegalStateException("Couldn't load instance input", e);

		} finally {
			try {
				reader.close();
				bin.close();
			} catch (Throwable e) {
				// ignore
			}
		}
	}

	/**
	 * @param reader
	 *            the XMLStreamReader given by matchesContentType
	 * @return true if the reader is based on a XML-file otherwise false
	 * @throws XMLStreamException
	 *             if the XML-StreamReader can't parse the input
	 */
	protected abstract boolean testReader(XMLStreamReader reader)
			throws XMLStreamException;

}