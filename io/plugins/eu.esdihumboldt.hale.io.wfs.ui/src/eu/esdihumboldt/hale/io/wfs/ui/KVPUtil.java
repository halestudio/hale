/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.wfs.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.http.client.utils.URIBuilder;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;

import eu.esdihumboldt.hale.io.wfs.WFSVersion;

/**
 * Utilities related to KVP encoded WFS requests.
 * 
 * @author Simon Templer
 */
public class KVPUtil {

	/**
	 * Add typename and namespace parameters for a WFS request to the given URI
	 * builder.
	 * 
	 * @param builder the builder for the WFS request URI
	 * @param selected the type names to include
	 * @param version the targeted WFS version
	 */
	public static void addTypeNameParameter(URIBuilder builder, Iterable<QName> selected,
			WFSVersion version) {
		// namespaces mapped to prefixes
		Map<String, String> namespaces = new HashMap<>();
		// type names with updated prefix
		Set<QName> typeNames = new HashSet<>();

		for (QName type : selected) {
			String prefix;
			if (type.getNamespaceURI() != null && !type.getNamespaceURI().isEmpty()) {
				prefix = namespaces.get(type.getNamespaceURI());
				if (prefix == null) {
					// no mapping yet for namespace
					String candidate = type.getPrefix();
					prefix = addPrefix(candidate, type.getNamespaceURI(), namespaces);
				}
			}
			else {
				// default namespace
				prefix = XMLConstants.DEFAULT_NS_PREFIX;
			}

			// add updated type
			typeNames.add(new QName(type.getNamespaceURI(), type.getLocalPart(), prefix));
		}

		final String paramNamespaces;
		final String paramTypenames;
		final String prefixNamespaceDelim;
		switch (version) {
		case V1_1_0:
			paramNamespaces = "NAMESPACE";
			paramTypenames = "TYPENAME";
			prefixNamespaceDelim = "=";
			break;
		case V2_0_0:
			/*
			 * XXX below are the values as defined in the WFS 2 specification.
			 * There have been problems with some GeoServer instances if used in
			 * that manner.
			 */
			paramNamespaces = "NAMESPACES";
			paramTypenames = "TYPENAMES";
			prefixNamespaceDelim = ",";
			break;
		default:
			// fall-back to WFS 1.1
			paramNamespaces = "NAMESPACE";
			paramTypenames = "TYPENAME";
			prefixNamespaceDelim = "=";
		}

		// add namespace prefix definitions
		if (!namespaces.isEmpty()) {
			builder.addParameter(
					paramNamespaces,
					Joiner.on(',').join(
							Maps.transformEntries(namespaces,
									new EntryTransformer<String, String, String>() {

										@Override
										public String transformEntry(String namespace, String prefix) {
											StringBuilder sb = new StringBuilder();
											sb.append("xmlns(");
											sb.append(prefix);
											sb.append(prefixNamespaceDelim);
											sb.append(namespace);
											sb.append(")");
											return sb.toString();
										}

									}).values()));
		}
		// add type names
		if (!typeNames.isEmpty()) {
			builder.addParameter(
					paramTypenames,
					Joiner.on(',').join(
							Iterables.transform(typeNames, new Function<QName, String>() {

								@Override
								public String apply(QName typeName) {
									String prefix = typeName.getPrefix();
									if (prefix == null || prefix.isEmpty()) {
										return typeName.getLocalPart();
									}
									return prefix + ":" + typeName.getLocalPart();
								}
							})));
		}
	}

	/**
	 * Add a new prefix.
	 * 
	 * @param candidate the prefix candidate
	 * @param namespace the namespace to be associated to the prefix
	 * @param namespaces the current namespaces mapped to prefixes
	 * @return the prefix to use
	 */
	private static String addPrefix(String candidate, final String namespace,
			final Map<String, String> namespaces) {
		int num = 1;
		while (namespaces.values().contains(candidate)) {
			candidate = "ns" + num;
			num++;
		}
		namespaces.put(namespace, candidate);
		return candidate;
	}

}
