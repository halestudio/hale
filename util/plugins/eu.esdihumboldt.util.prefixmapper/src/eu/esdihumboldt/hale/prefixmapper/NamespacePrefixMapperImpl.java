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

package eu.esdihumboldt.hale.prefixmapper;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;

/**
 * Custom namespace prefix mapper.
 * 
 * @author Thorsten Reitz
 */
public class NamespacePrefixMapperImpl extends NamespacePrefixMapper {

	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
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
		// I want the namespace to be mapped to "gml".
		if ("http://www.opengis.net/sld/".equals(namespaceUri))
			return "sld";

		// map omwg
		if ("http://www.omwg.org/TR/d7/ontology/alignment".equals(namespaceUri))
			return "omwg";
		// map align
		if ("http://knowledgeweb.semanticweb.org/heterogeneity/alignment".equals(namespaceUri))
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

	@Override
	public String[] getPreDeclaredNamespaceUris() {
		return new String[] { "http://www.w3.org/2001/XMLSchema-instance",
				"http://www.opengis.net/gml/", "http://www.omwg.org/TR/d7/ontology/alignment",
				"http://www.esdi-humboldt.eu/goml", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
				"http://www.w3.org/2001/XMLSchema-instance" };
	}
}
