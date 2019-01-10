/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ReferenceProperty;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * Reference property constraints that references XML elements and is only
 * resolved after loading the schema.
 * 
 * @author Simon Templer
 */
public class ElementReferenceProperty extends ReferenceProperty {

	private final Collection<QName> referencedElements;

	private final XmlIndex index;

	private boolean initialized = false;

	/**
	 * Constructor.
	 * 
	 * @param index the XML index
	 * @param referencedElements the referenced XML elements
	 */
	public ElementReferenceProperty(XmlIndex index, Collection<QName> referencedElements) {
		super(true);
		this.referencedElements = referencedElements;
		this.index = index;
	}

	/**
	 * Constructor.
	 * 
	 * @param index the XML index
	 * @param valuePath the path to the reference value property
	 * @param referencedElements the referenced XML elements
	 */
	public ElementReferenceProperty(XmlIndex index, List<QName> valuePath,
			Collection<QName> referencedElements) {
		super(valuePath);
		this.referencedElements = referencedElements;
		this.index = index;
	}

	@Override
	public Collection<? extends TypeDefinition> getReferencedTypes() {
		if (!initialized) {
			// resolve elements
			for (QName elementName : referencedElements) {
				XmlElement element = index.getElements().get(elementName);
				if (element != null) {
					super.addReferencedType(element.getType());
				}
			}

			initialized = true;
		}
		return super.getReferencedTypes();
	}

}
