/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.groovy.internal;

import java.util.ArrayList;

import eu.esdihumboldt.hale.common.instance.groovy.InstanceAccessor;

/**
 * {@link ArrayList} with the addition of methods {@link #getP()} and
 * {@link #getProperties()} to facilitate homogeneous usage in Groovy scripts.
 * 
 * @author Kai Schwierczek
 * @param <E> the type of elements in this list
 */
public class InstanceAccessorArrayList<E> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;

	/**
	 * Returns an {@link InstanceAccessor} for all objects in this list.
	 * 
	 * @return an {@link InstanceAccessor} for all objects in this list
	 */
	public InstanceAccessor getP() {
		return getProperties();
	}

	/**
	 * Returns an {@link InstanceAccessor} for all objects in this list.
	 * 
	 * @return an {@link InstanceAccessor} for all objects in this list
	 */
	public InstanceAccessor getProperties() {
		return new InstanceAccessor(this);
	}
}
