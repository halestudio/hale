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

package eu.esdihumboldt.hale.common.instance.model;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Filter extension interface.
 * 
 * @author Simon Templer
 */
public interface ContextAwareFilter extends Filter {

	/**
	 * Determines if an instance matches the filter given an iteration context.
	 * 
	 * This method should be called when an iteration context is present,
	 * instead of the original {@link #match(Instance)} method.
	 * 
	 * @param instance the instance to check the filter against
	 * @param context the iteration context
	 * @return <code>true</code> if the given instance matches the filter,
	 *         <code>false</code> otherwise
	 */
	public boolean match(Instance instance, @Nullable Map<Object, Object> context);

}
