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

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import java.util.Collections;
import java.util.List;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;

/**
 * Specifies a type's primary key, which may be the target of {@link Reference}s.
 *
 * @author Kai Schwierczek
 */
@Immutable
@Constraint(mutable = false)
public class PrimaryKey implements TypeConstraint {
	private final List<ChildDefinition<?>> primaryKeyPath;

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
	public PrimaryKey(List<ChildDefinition<?>> primaryKeyPath) {
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
	 * Returns the path to the primary key. This is <code>null</code>
	 * if and only if {@link #hasPrimaryKey()} returns false.
	 *
	 * @return the path to the primary key, may be <code>null</code>
	 */
	public List<ChildDefinition<?>> getPrimaryKeyPath() {
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
