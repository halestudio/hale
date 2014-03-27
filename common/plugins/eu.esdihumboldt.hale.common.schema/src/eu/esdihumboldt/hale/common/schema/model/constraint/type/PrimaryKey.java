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

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;

/**
 * Specifies a type's primary key, which may be the target of {@link Reference}
 * s.
 * 
 * @author Kai Schwierczek
 */
@Immutable
@Constraint(mutable = false)
public class PrimaryKey implements TypeConstraint {

	private final List<QName> primaryKeyPath;

	/**
	 * Creates a constraint saying that a type does not have a primary key.
	 */
	public PrimaryKey() {
		primaryKeyPath = null;
	}

	/**
	 * Creates a constraint specifying the path to the primary key.
	 * 
	 * @param primaryKeyPath the path to the primary key
	 */
	public PrimaryKey(List<QName> primaryKeyPath) {
		if (primaryKeyPath == null)
			this.primaryKeyPath = null;
		else
			this.primaryKeyPath = Collections.unmodifiableList(primaryKeyPath);
	}

	/**
	 * Returns true, if and only if a primary key is set.
	 * 
	 * @return true, if and only if a primary key is set
	 */
	public boolean hasPrimaryKey() {
		return primaryKeyPath != null;
	}

	/**
	 * Returns the path to the primary key. This is <code>null</code> if and
	 * only if {@link #hasPrimaryKey()} returns false.
	 * 
	 * @return the path to the primary key, may be <code>null</code>
	 */
	public List<QName> getPrimaryKeyPath() {
		return primaryKeyPath;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.model.TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		return true;
	}
}
