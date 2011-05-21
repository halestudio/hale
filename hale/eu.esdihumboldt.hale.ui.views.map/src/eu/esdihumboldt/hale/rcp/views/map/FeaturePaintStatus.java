/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.views.map;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeaturePaintStatus {
	
	private volatile int referenceFailed = 0;
	
	private volatile int transformedFailed = 0;

	/**
	 * @return the referenceFailed
	 */
	public int getReferenceFailed() {
		return referenceFailed;
	}

	/**
	 * @param referenceFailed the referenceFailed to set
	 */
	public void setReferenceFailed(int referenceFailed) {
		this.referenceFailed = referenceFailed;
	}

	/**
	 * @return the transformedFailed
	 */
	public int getTransformedFailed() {
		return transformedFailed;
	}

	/**
	 * @param transformedFailed the transformedFailed to set
	 */
	public void setTransformedFailed(int transformedFailed) {
		this.transformedFailed = transformedFailed;
	}

}
