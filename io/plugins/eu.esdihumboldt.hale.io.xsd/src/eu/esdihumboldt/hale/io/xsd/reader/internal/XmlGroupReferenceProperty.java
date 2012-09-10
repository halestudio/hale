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

package eu.esdihumboldt.hale.io.xsd.reader.internal;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.io.xsd.model.XmlGroup;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * Group property referencing a XML group
 * 
 * @author Simon Templer
 */
public class XmlGroupReferenceProperty extends LazyGroupPropertyDefinition {

	private final QName groupName;

	private XmlGroup referencedGroup;

	/**
	 * Create a property that references a XML group
	 * 
	 * @param name the property name
	 * @param declaringType the declaring type
	 * @param index the XML index
	 * @param groupName the group name
	 * @param allowFlatten if the group may be replaced by its children
	 */
	public XmlGroupReferenceProperty(QName name, DefinitionGroup declaringType, XmlIndex index,
			QName groupName, boolean allowFlatten) {
		super(name, declaringType, index, allowFlatten);

		this.groupName = groupName;
	}

	/**
	 * @see LazyGroupPropertyDefinition#initChildren()
	 */
	@Override
	protected void initChildren() {
		XmlGroup group = resolveGroup();

		if (group == null) {
			throw new IllegalStateException("Referenced group could not be found: "
					+ groupName.toString());
		}

		// redeclare children
		for (ChildDefinition<?> child : group.getDeclaredChildren()) {
			ChildDefinition<?> redeclaredChild = DefinitionUtil.redeclareChild(child, this);
			addChild(redeclaredChild);
		}
	}

	private XmlGroup resolveGroup() {
		if (referencedGroup == null) {
			referencedGroup = index.getGroups().get(groupName);
		}

		return referencedGroup;
	}

}
