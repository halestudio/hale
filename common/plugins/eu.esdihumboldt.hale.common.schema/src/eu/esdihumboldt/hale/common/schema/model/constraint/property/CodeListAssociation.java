/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.schema.model.constraint.property;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;

/**
 * Specifies code list associated to a property.
 * 
 * XXX the information in this constraint is not used anywhere right now,
 * currently it is only use to transport the information
 * 
 * @author Simon Templer
 */
@Constraint(mutable = false)
public class CodeListAssociation implements PropertyConstraint {

	/**
	 * References to code lists, usually URLs.
	 * 
	 * TODO expand with more information? (e.g. which format or how to load?)
	 */
	private final Set<String> codeLists;

	/**
	 * Creates a default code list association constraint w/o any associations.
	 */
	public CodeListAssociation() {
		this(Collections.emptyList());
	}

	/**
	 * Create a code list constraint w/ the given code list references.
	 * 
	 * @param codeLists the collection of code list references
	 */
	public CodeListAssociation(Collection<? extends String> codeLists) {
		this.codeLists = new HashSet<>(codeLists);
	}

	/**
	 * Returns whether there are any associated code lists.
	 * 
	 * @return true, if there are associated code lists, false otherwise
	 */
	public boolean hasAssociatedCodeLists() {
		return !codeLists.isEmpty();
	}

	/**
	 * Get the references to the associated code lists.
	 * 
	 * @return the code list references
	 */
	public Iterable<String> getCodeLists() {
		return codeLists;
	}

}
