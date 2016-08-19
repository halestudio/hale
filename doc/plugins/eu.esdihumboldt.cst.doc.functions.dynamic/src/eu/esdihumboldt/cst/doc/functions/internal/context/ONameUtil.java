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

package eu.esdihumboldt.cst.doc.functions.internal.context;

import java.util.BitSet;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;

import com.google.common.base.Charsets;

/**
 * Utilities for name encoding and decoding
 * 
 * @author Simon Templer
 */
public class ONameUtil {

	/**
	 * BitSet of safe characters for names.
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
	 * Encode text to a string that is a valid name.
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
	 * Decode a name to its original string representation.
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
