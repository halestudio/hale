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

package eu.esdihumboldt.hale.io.xml.validator.internal;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

/**
 * Implementation of {@link LSInput}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class LSInputImpl implements LSInput {

	private String publicId;
	private String systemId;
	private String baseSystemId;

	private InputStream byteStream;
	private Reader charStream;
	private String data;

	private String encoding;

	private boolean certifiedText;

	/**
	 * Default constructor
	 */
	public LSInputImpl() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param publicId the public Id
	 * @param systemId the system Id
	 * @param byteStream the byte stream
	 */
	public LSInputImpl(String publicId, String systemId, InputStream byteStream) {
		this.publicId = publicId;
		this.systemId = systemId;
		this.byteStream = byteStream;
	}

	public InputStream getByteStream() {
		return byteStream;
	}

	public void setByteStream(InputStream byteStream) {
		this.byteStream = byteStream;
	}

	public Reader getCharacterStream() {
		return charStream;
	}

	public void setCharacterStream(Reader characterStream) {
		this.charStream = characterStream;
	}

	public String getStringData() {
		return data;
	}

	public void setStringData(String stringData) {
		this.data = stringData;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getBaseURI() {
		return baseSystemId;
	}

	public void setBaseURI(String baseURI) {
		this.baseSystemId = baseURI;
	}

	public boolean getCertifiedText() {
		return certifiedText;
	}

	public void setCertifiedText(boolean certifiedText) {
		this.certifiedText = certifiedText;
	}

}
