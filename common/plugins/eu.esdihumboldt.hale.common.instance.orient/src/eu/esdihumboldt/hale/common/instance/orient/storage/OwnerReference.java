/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.common.instance.orient.storage;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Owner reference.
 * 
 * @author Simon Templer
 */
public class OwnerReference extends WeakReference<Object> {

	/**
	 * Constructor.
	 * 
	 * @param referent the owner
	 */
	public OwnerReference(Object referent) {
		super(referent);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(get());
	}

	@Override
	public boolean equals(Object obj) {
		if (get() == null) {
			return false;
		}

		if (obj instanceof OwnerReference) {
			return ((OwnerReference) obj).get() == get();
		}

		return super.equals(obj);
	}

}
