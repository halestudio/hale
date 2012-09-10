package eu.esdihumboldt.commons.mediator.util;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
public class NamespacePrefixMapperImpl extends NamespacePrefixMapper {

	/**
	 * Returns a preferred prefix for the given namespace URI.
	 * 
	 * This method is intended to be overrided by a derived class.
	 * 
	 * @param namespaceUri
	 *            The namespace URI for which the prefix needs to be found.
	 *            Never be null. "" is used to denote the default namespace.
	 * @param suggestion
	 *            When the content tree has a suggestion for the prefix to the
	 *            given namespaceUri, that suggestion is passed as a parameter.
	 *            Typicall this value comes from the QName.getPrefix to show the
	 *            preference of the content tree. This parameter may be null,
	 *            and this parameter may represent an already occupied prefix.
	 * @param requirePrefix
	 *            If this method is expected to return non-empty prefix. When
	 *            this flag is true, it means that the given namespace URI
	 *            cannot be set as the default namespace.
	 * 
	 * @return null if there's no prefered prefix for the namespace URI. In this
	 *         case, the system will generate a prefix for you.
	 * 
	 *         Otherwise the system will try to use the returned prefix, but
	 *         generally there's no guarantee if the prefix will be actually
	 *         used or not.
	 * 
	 *         return "" to map this namespace URI to the default namespace.
	 *         Again, there's no guarantee that this preference will be honored.
	 * 
	 *         If this method returns "" when requirePrefix=true, the return
	 *         value will be ignored and the system will generate one.
	 */
	public String getPreferredPrefix(String namespaceUri, String suggestion,
			boolean requirePrefix) {
		// I want this namespace to be mapped to "wps"
		if ("http://www.opengis.net/wps/1.0.0".equals(namespaceUri))
			return "wps";

		// I want the namespace to be mapped to "ows".
		if ("http://www.opengis.net/ows/1.1".equals(namespaceUri))
			return "ows";
		// I want the namespace to be mapped to "xlink".
		if ("http://www.w3.org/1999/xlink".equals(namespaceUri))
			return "xlink";
		// I want the namespace to be mapped to "xsi".
		if ("http://www.w3.org/2001/XMLSchema-instance".equals(namespaceUri))
			return "xsi";
		// I want the namespace to be mapped to "gml".
		if ("http://www.opengis.net/gml/".equals(namespaceUri))
			return "gml";

		// map omwg
		if ("http://www.omwg.org/TR/d7/ontology/alignment".equals(namespaceUri))
			return "omwg";
		// map align
		if ("http://knowledgeweb.semanticweb.org/heterogeneity/alignment"
				.equals(namespaceUri))
			return "align";
		// map goml
		if ("http://www.esdi-humboldt.eu/goml".equals(namespaceUri))
			return "goml";
		// map rdf
		if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespaceUri))
			return "rdf";
		// map xsi
		if ("http://www.w3.org/2001/XMLSchema-instance".equals(namespaceUri))
			return "xsi";

		// otherwise I don't care. Just use the default suggestion, whatever it
		// may be.
		return suggestion;
	}

	/**
	 * Returns a list of namespace URIs that should be declared at the root
	 * element.
	 * <p>
	 * By default, the JAXB RI produces namespace declarations only when they
	 * are necessary, only at where they are used. Because of this lack of
	 * look-ahead, sometimes the marshaller produces a lot of namespace
	 * declarations that look redundant to human eyes. For example,
	 * 
	 * <pre>
	 * &lt;xmp&gt;
	 * &lt;?xml version=&quot;1.0&quot;?&gt;
	 * &lt;root&gt;
	 *   &lt;ns1:child xmlns:ns1=&quot;urn:foo&quot;&gt; ... &lt;/ns1:child&gt;
	 *   &lt;ns2:child xmlns:ns2=&quot;urn:foo&quot;&gt; ... &lt;/ns2:child&gt;
	 *   &lt;ns3:child xmlns:ns3=&quot;urn:foo&quot;&gt; ... &lt;/ns3:child&gt;
	 *   ...
	 * &lt;/root&gt;
	 * &lt;xmp&gt;
	 * </pre>
	 * <p>
	 * If you know in advance that you are going to use a certain set of
	 * namespace URIs, you can override this method and have the marshaller
	 * declare those namespace URIs at the root element.
	 * <p>
	 * For example, by returning <code>new String[]{"urn:foo"}</code>, the
	 * marshaller will produce:
	 * 
	 * <pre>
	 * &lt;xmp&gt;
	 * &lt;?xml version=&quot;1.0&quot;?&gt;
	 * &lt;root xmlns:ns1=&quot;urn:foo&quot;&gt;
	 *   &lt;ns1:child&gt; ... &lt;/ns1:child&gt;
	 *   &lt;ns1:child&gt; ... &lt;/ns1:child&gt;
	 *   &lt;ns1:child&gt; ... &lt;/ns1:child&gt;
	 *   ...
	 * &lt;/root&gt;
	 * &lt;xmp&gt;
	 * </pre>
	 * <p>
	 * To control prefixes assigned to those namespace URIs, use the
	 * {@link #getPreferredPrefix} method.
	 * 
	 * @return A list of namespace URIs as an array of {@link String}s. This
	 *         method can return a length-zero array but not null. None of the
	 *         array component can be null. To represent the empty namespace,
	 *         use the empty string <code>""</code>.
	 * 
	 * @since JAXB RI 1.0.2
	 */
	public String[] getPreDeclaredNamespaceUris() {
		return new String[] { "http://www.w3.org/2001/XMLSchema-instance",
				"http://www.opengis.net/gml/",
				"http://www.omwg.org/TR/d7/ontology/alignment",
				"http://www.esdi-humboldt.eu/goml",
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
				"http://www.w3.org/2001/XMLSchema-instance" };
	}
}
