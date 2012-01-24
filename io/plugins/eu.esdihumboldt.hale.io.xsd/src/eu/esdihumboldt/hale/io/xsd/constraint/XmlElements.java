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

package eu.esdihumboldt.hale.io.xsd.constraint;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Constraint that states the {@link XmlElement}s associated with a type 
 * definition, by default no elements are contained.
 * @author Simon Templer
 */
@Constraint(mutable = true)
public class XmlElements implements TypeConstraint {
	
	private Set<XmlElement> elements;
	
	/**
	 * Create a default XML element constraint w/o any elements
	 */
	public XmlElements() {
		super();
	}
	
	/**
	 * Get the elements associated with the type
	 * @return the XML elements
	 */
	public Collection<? extends XmlElement> getElements() {
		if (elements == null) {
			return Collections.emptyList();
		}
		else {
			return Collections.unmodifiableCollection(elements);
		}
	}

	/**
	 * Add a XML element to the type
	 * @param element the element to add
	 */
	public void addElement(XmlElement element) {
		if (elements == null) {
			elements = new HashSet<XmlElement>();
		}
		elements.add(element);
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// not inheritable, substitutions instead pose a kind of inheritance
		return false;
	}
	
}
