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

package eu.esdihumboldt.hale.io.oml.internal.goml.oml.ext;

import java.util.List;

/**
 * Use a {@link TransformationPipe} if you want to apply a sequence of more than
 * one transformation to a defined input set.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("javadoc")
public class TransformationPipe extends Transformation {

	private List<Transformation> pipe;

	/**
	 * @param pipe
	 */
	public TransformationPipe(List<Transformation> pipe) {
		super();
		this.pipe = pipe;
	}

	/**
	 * @return the pipe
	 */
	public List<Transformation> getPipe() {
		return pipe;
	}

	/**
	 * @param pipe the pipe to set
	 */
	public void setPipe(List<Transformation> pipe) {
		this.pipe = pipe;
	}

}
