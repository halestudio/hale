/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.commons.goml.oml.ext;

import java.util.List;

/**
 * Use a {@link TransformationPipe} if you want to apply a sequence of more than
 * one transformation to a defined input set.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
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
	 * @param pipe
	 *            the pipe to set
	 */
	public void setPipe(List<Transformation> pipe) {
		this.pipe = pipe;
	}

}
