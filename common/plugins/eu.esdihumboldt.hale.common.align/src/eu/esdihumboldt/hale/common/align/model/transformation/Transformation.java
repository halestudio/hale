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

package eu.esdihumboldt.hale.common.align.model.transformation;

import eu.esdihumboldt.hale.common.align.model.Alignment;

/**
 * A transformation is an {@link Alignment} that has been processed to a set of
 * transformation cells that can be converted to instructions to be executed to
 * perform that transformation on instances.
 * 
 * @author Simon Templer
 */
public interface Transformation {

	// TODO add method to generate specific transformation instructions given a
	// source instance
//	public X y(Collection<? extends Type> sourceTypes, Instance source);

//	/**
//	 * 
//	 * @param tree
//	 * @param sourceTypes
//	 * @param source the source instance XXX eventually multiple?
//	 * @return
//	 */
//	public X prepare(TransformationTree tree, 
//			Collection<? extends Type> sourceTypes, Instance source);

}
