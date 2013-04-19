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

	@Override
	public InputStream getByteStream() {
		return byteStream;
	}

	@Override
	public void setByteStream(InputStream byteStream) {
		this.byteStream = byteStream;
	}

	@Override
	public Reader getCharacterStream() {
		return charStream;
	}

	@Override
	public void setCharacterStream(Reader characterStream) {
		this.charStream = characterStream;
	}

	@Override
	public String getStringData() {
		return data;
	}

	@Override
	public void setStringData(String stringData) {
		this.data = stringData;
	}

	@Override
	public String getEncoding() {
		return encoding;
	}

	@Override
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public String getPublicId() {
		return publicId;
	}

	@Override
	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	@Override
	public String getSystemId() {
		return systemId;
	}

	@Override
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	@Override
	public String getBaseURI() {
		return baseSystemId;
	}

	@Override
	public void setBaseURI(String baseURI) {
		this.baseSystemId = baseURI;
	}

	@Override
	public boolean getCertifiedText() {
		return certifiedText;
	}

	@Override
	public void setCertifiedText(boolean certifiedText) {
		this.certifiedText = certifiedText;
	}

}
