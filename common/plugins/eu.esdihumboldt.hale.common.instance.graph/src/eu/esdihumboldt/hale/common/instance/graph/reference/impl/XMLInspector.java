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

package eu.esdihumboldt.hale.common.instance.graph.reference.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.graph.reference.IdentityReferenceInspector;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraversalCallback;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;

/**
 * Identity and reference inspector for XML IDs and local XLink references.
 * 
 * @author Simon Templer
 */
public class XMLInspector implements IdentityReferenceInspector<String> {

	/**
	 * Collects local XLink references.
	 */
	public class LocalXLinks implements InstanceTraversalCallback {

		private final Set<String> referencedIDs = new HashSet<>();

		@Override
		public boolean visit(Instance instance, QName name, DefinitionGroup parent) {
			return true;
		}

		@Override
		public boolean visit(Group group, QName name, DefinitionGroup parent) {
			return true;
		}

		@Override
		public boolean visit(Object value, QName name, DefinitionGroup parent) {
			if (value != null && parent != null && "href".equals(name.getLocalPart())) {
				ChildDefinition<?> def = parent.getChild(name);
				if (def != null && def.asProperty() != null) {
					Reference ref = def.asProperty().getConstraint(Reference.class);
					if (ref.isReference()) {
						String idRef = ref.extractId(value).toString();
						if (!idRef.contains("#")) {
							// an extracted local reference will not contain #
							referencedIDs.add(idRef);
						}
					}
				}
			}

			return true;
		}

		/**
		 * @return the referenced identifiers
		 */
		public Set<String> getReferencedIDs() {
			return Collections.unmodifiableSet(referencedIDs);
		}

	}

	private static final QName ID_TYPE_NAME = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "ID");

	@Override
	public String getIdentity(Instance instance) {
		// look for XML ID attributes in top level properties
		// return the first ID found

		for (QName property : instance.getPropertyNames()) {
			ChildDefinition<?> def = instance.getDefinition().getChild(property);
			if (def != null && def.asProperty() != null) {
				if (ID_TYPE_NAME.equals(def.asProperty().getPropertyType().getName())) {
					Object[] values = instance.getProperty(property);
					if (values != null && values.length > 0 && values[0] != null) {
						return values[0].toString();
					}
				}
			}
		}

		return null;
	}

	@Override
	public Set<String> getReferencedIdentities(Instance instance) {
		// find all (local) xlink references

		DepthFirstInstanceTraverser traverser = new DepthFirstInstanceTraverser();
		LocalXLinks xlinks = new LocalXLinks();
		traverser.traverse(instance, xlinks);

		return xlinks.getReferencedIDs();
	}
}
