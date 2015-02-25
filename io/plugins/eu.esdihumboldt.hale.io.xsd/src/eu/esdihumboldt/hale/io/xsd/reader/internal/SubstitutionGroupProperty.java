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

package eu.esdihumboldt.hale.io.xsd.reader.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.base.Preconditions;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.DisplayName;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultGroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Group property that resolves all possible substitutions for a property and
 * offers them as a choice. The property must be set using
 * {@link #setProperty(DefaultPropertyDefinition)}-
 * 
 * @author Simon Templer
 */
public class SubstitutionGroupProperty extends LazyGroupPropertyDefinition {

	private DefaultPropertyDefinition property;

	/**
	 * The
	 * 
	 * @param name the property name
	 * @param parentGroup the parent group
	 */
	public SubstitutionGroupProperty(QName name, DefinitionGroup parentGroup) {
		super(name, parentGroup, null, false);

		setConstraint(ChoiceFlag.ENABLED);
	}

	/**
	 * Set the property represented by the group. The property must have been
	 * created with this group as parent and the {@link Cardinality} constraint
	 * must have been already set.
	 * 
	 * @param property the property to set
	 */
	public void setProperty(DefaultPropertyDefinition property) {
		Preconditions.checkArgument(property.getDeclaringGroup() == this);

		this.property = property;

		// apply cardinality to group
		setConstraint(property.getConstraint(Cardinality.class));
		// set cardinality to exactly one for the property
		property.setConstraint(Cardinality.CC_EXACTLY_ONCE);
		// set display name to property name
		setConstraint(new DisplayName(property.getDisplayName()));
	}

	/**
	 * @see DefaultGroupPropertyDefinition#addChild(ChildDefinition)
	 */
	@Override
	public void addChild(ChildDefinition<?> child) {
		// do nothing
		// prevents a property being added manually
	}

	/**
	 * @see LazyGroupPropertyDefinition#initChildren()
	 */
	@Override
	protected void initChildren() {
		if (property != null) {
			TypeDefinition propertyType = property.getPropertyType();

			// add property and substitutions

			// collect substitution types and elements
			List<XmlElement> substitutions = collectSubstitutions(property.getName(), propertyType);

			if (substitutions == null || substitutions.isEmpty()) {
				// add property (XXX even if the property type is abstract)
				super.addChild(property); // no redeclaration necessary as this
											// is already the declaring group
			}
			else {
				// add property if the type is not abstract
				if (!propertyType.getConstraint(AbstractFlag.class).isEnabled()) {
					super.addChild(property); // no redeclaration necessary as
												// this is already the declaring
												// group
				}

				// add substitutions
				for (XmlElement substitution : substitutions) {
					PropertyDefinition p = new SubstitutionProperty(substitution, property, this);
					super.addChild(p); // must call super add
				}
			}
		}
		// else empty group
	}

	/**
	 * Collect all sub-types from the given type that may substitute it on
	 * condition of the given element name.
	 * 
	 * @param elementName the element name
	 * @param type the type to be substituted
	 * @return the substitution types
	 */
	public static List<XmlElement> collectSubstitutions(QName elementName, TypeDefinition type) {
		Set<QName> substitute = new HashSet<QName>();
		substitute.add(elementName);
		Queue<TypeDefinition> subTypes = new LinkedList<TypeDefinition>();

		/*
		 * Add type itself also to list of types to be checked for
		 * substitutions. (this is needed e.g. in CityGML 0.4.0 schema
		 * cityObjectMember substituting featureMember) This essentially then is
		 * only a substitution in name and not in type. XXX if other elements,
		 * that are in no relation to the type, should also be possible for
		 * substitution, we would need some kond of substitution index in
		 * XmlIndex
		 */
		subTypes.add(type);

		// add all sub-types to the queue
		subTypes.addAll(type.getSubTypes());

		List<XmlElement> result = new ArrayList<XmlElement>();

		while (!subTypes.isEmpty()) {
			TypeDefinition subType = subTypes.poll();

			// check the declared elements for the substitution group
			Collection<? extends XmlElement> elements = subType.getConstraint(XmlElements.class)
					.getElements();
			Iterator<? extends XmlElement> it = elements.iterator();
			while (it.hasNext()) {
				XmlElement element = it.next();
				QName subGroup = element.getSubstitutionGroup();
				if (subGroup != null && substitute.contains(subGroup)) {
					// only if substitution group match

					// add element name also to the name that may be substituted
					substitute.add(element.getName());
					if (!element.getType().getConstraint(AbstractFlag.class).isEnabled()) {
						// only add if type is not abstract
						result.add(element);
					}
				}
			}

			// XXX what about using xsi:type?
			// XXX we could also add elements for other sub-types then, e.g.
			// while also adding a specific constraint

			// add the sub-type's sub-types
			subTypes.addAll(subType.getSubTypes());
		}

		return result;
	}

}
