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

package eu.esdihumboldt.hale.common.instance.model.impl.internal;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.codec.DecoderException;

import eu.esdihumboldt.hale.common.instance.model.impl.OGroup;
import eu.esdihumboldt.hale.common.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.ONameUtil;
import eu.esdihumboldt.util.Identifiers;

/**
 * Temporary static namespace map for storing {@link OInstance}s/{@link OGroup}s
 * in a temporary database or using them inside this JVM.
 * 
 * @author Simon Templer
 */
public abstract class ONamespaceMap {

	private static final Identifiers<String> IDS = new Identifiers<String>("n", true);

	/**
	 * Map the namespace of the given qualified name to a short identifier and
	 * return the adapted name.
	 * 
	 * @param org the original qualified name
	 * @return the adapted qualified name
	 */
	public static QName map(QName org) {
		if (XMLConstants.NULL_NS_URI.equals(org.getNamespaceURI())) {
			return org;
		}

		return new QName(IDS.getId(org.getNamespaceURI()), org.getLocalPart());
	}

	/**
	 * Encode a {@link QName} for runtime use with OrientDB.
	 * 
	 * @param org the qualified name
	 * @return the encoded name
	 */
	public static String encode(QName org) {
		String ns = org.getNamespaceURI();
		if (!XMLConstants.NULL_NS_URI.equals(ns)) {
			ns = IDS.getId(org.getNamespaceURI());
		}

		return ns + "_" + ONameUtil.encodeName(org.getLocalPart());
	}

	/**
	 * Determine the original namespace of the given qualified name with a
	 * namespace previously mapped with {@link #map(QName)} and return the
	 * original name.
	 * 
	 * @param mapped the adapted qualified name
	 * @return the original qualified name
	 */
	public static QName unmap(QName mapped) {
		if (XMLConstants.NULL_NS_URI.equals(mapped.getNamespaceURI())) {
			return mapped;
		}

		return new QName(IDS.getObject(mapped.getNamespaceURI()), mapped.getLocalPart());
	}

	/**
	 * Decode a name based on the runtime namespace map.
	 * 
	 * @param name the encoded name
	 * @return the decoded qualified name
	 * @throws DecoderException of decoding the local part of the name fails
	 */
	public static QName decode(String name) throws DecoderException {
		int pos = name.indexOf('_'); // find first underscore
		String local;
		String ns = XMLConstants.NULL_NS_URI;
		if (pos < 0) {
			local = ONameUtil.decodeName(name);
		}
		else {
			ns = IDS.getObject(name.substring(0, pos));
			local = ONameUtil.decodeName(name.substring(pos + 1));
		}
		return new QName(ns, local);
	}

}
