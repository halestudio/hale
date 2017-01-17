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

package eu.esdihumboldt.util.geometry.interpolation.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcString;

/**
 * Represents a string of arcs.
 *
 * @author Simon Templer
 */
public class ArcStringImpl implements ArcString {

	private final List<Arc> arcs;

	/**
	 * Create a new arc string from the given arcs.
	 * 
	 * @param arcs the arcs forming the arc string
	 */
	public ArcStringImpl(Collection<Arc> arcs) {
		super();
		this.arcs = new ArrayList<>(arcs);
	}

	@Override
	public List<Arc> getArcs() {
		return Collections.unmodifiableList(arcs);
	}

}
