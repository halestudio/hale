/*
 * Copyright (c) 2012 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.internal;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.PathElement;

/**
 * {@link PathElement} that represents a <code>xsl:for-each</code> tag.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class XslForEach implements PathElement {

	private static final QName NAME = new QName(XsltGenerator.NS_URI_XSL, "for-each");

	private final String select;

	/**
	 * Create a <code>xsl:for-each</code> tag path element.
	 * 
	 * @param select the select attribute
	 */
	public XslForEach(String select) {
		this.select = select;
	}

	@Override
	public void prepareWrite(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeAttribute("select", select);
	}

	@Override
	public QName getName() {
		return NAME;
	}

	@Override
	public TypeDefinition getType() {
		// not transient, but not type definition available either
		return null;
	}

	@Override
	public boolean isProperty() {
		return false;
	}

	@Override
	public boolean isTransient() {
		// must be written
		return false;
	}

	@Override
	public boolean isDowncast() {
		return false;
	}

	@Override
	public boolean isUnique() {
		// may be repeated as it is independent of the target structure
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		/*
		 * This is kind of dirty but needed for correct handling in Descent.
		 */
		return obj instanceof XslForEach;
	}

}
