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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.SkipValidation;

/**
 * Controls validation being skipped for geometry properties with an augmented
 * value set.
 * 
 * @author Simon Templer
 */
@Immutable
public class SkipGeometryValidation extends SkipValidation {

	private static SkipGeometryValidation instance;

	/**
	 * Get the shared constraint instance.
	 * 
	 * @return the constraint instance
	 */
	public static SkipGeometryValidation getInstance() {
		synchronized (SkipGeometryValidation.class) {
			if (instance == null) {
				instance = new SkipGeometryValidation();
			}
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	protected SkipGeometryValidation() {
		super();
	}

	/**
	 * @see SkipValidation#skipValidation(Object)
	 */
	@Override
	public boolean skipValidation(Object propertyValue) {
		if (propertyValue instanceof Instance) {
			// return true if an augmented value is set
			return ((Instance) propertyValue).getValue() != null;
		}

		return false;
	}

	/**
	 * Constraint is inherited.
	 * 
	 * @return <code>true</code>
	 */
	@Override
	public boolean isInheritable() {
		return true;
	}

}
