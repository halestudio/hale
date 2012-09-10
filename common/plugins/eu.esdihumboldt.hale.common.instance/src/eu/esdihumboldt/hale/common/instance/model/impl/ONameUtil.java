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

package eu.esdihumboldt.hale.common.instance.model.impl;

import java.util.BitSet;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;

import com.google.common.base.Charsets;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Utilities for name encoding and decoding
 * 
 * @author Simon Templer
 */
public class ONameUtil {

	/**
	 * BitSet of safe characters for names in {@link ODocument}s.
	 */
	protected static final BitSet ONAME = new BitSet(256);

	// Static initializer for ONAME
	static {
		// alpha characters
		for (int i = 'a'; i <= 'z'; i++) {
			ONAME.set(i);
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			ONAME.set(i);
		}
		// numeric characters
		for (int i = '0'; i <= '9'; i++) {
			ONAME.set(i);
		}
		// special chars
		// none, as _ is not added because it is used for the encoding
	}

	/**
	 * Encode text to a string that is a valid name for {@link ODocument}
	 * fields.
	 * 
	 * @param text the text to encode
	 * @return the encoded name
	 */
	public static String encodeName(String text) {
		if (text == null) {
			return null;
		}

		return new String(URLCodec.encodeUrl(ONAME, text.getBytes(Charsets.UTF_8)),
				Charsets.US_ASCII).replace('%', '_');
	}

	/**
	 * Decode a name for {@link ODocument} fields to its original string
	 * representation.
	 * 
	 * @param name the name to decode
	 * @return the decoded text
	 * @throws DecoderException if decoding the string fails
	 */
	public static String decodeName(String name) throws DecoderException {
		if (name == null) {
			return null;
		}
		return new String(URLCodec.decodeUrl(name.replace('_', '%').getBytes(Charsets.US_ASCII)),
				Charsets.UTF_8);
	}

}
