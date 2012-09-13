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

package eu.esdihumboldt.hale.ui.service.population.internal;

import eu.esdihumboldt.hale.ui.common.service.population.Population;

/**
 * Default implementation instance/property {@link Population}
 * 
 * @author Simon Templer
 */
public class PopulationImpl implements Population {

	private int parents;
	private int overall;

	/**
	 * Create a population.
	 * 
	 * @param parents the initial parent count
	 * @param overall the initial overall count
	 */
	public PopulationImpl(int parents, int overall) {
		super();
		this.parents = parents;
		this.overall = overall;
	}

	/**
	 * @see Population#getOverallCount()
	 */
	@Override
	public int getOverallCount() {
		return overall;
	}

	/**
	 * @see Population#getParentsCount()
	 */
	@Override
	public int getParentsCount() {
		return parents;
	}

	/**
	 * Increase the parents count.
	 */
	public void increaseParents() {
		if (parents == UNKNOWN) {
			parents = 1;
		}
		else {
			parents++;
		}
	}

	/**
	 * Increase the overall count.
	 * 
	 * @param count the number by which to increase the overall count
	 */
	public void increaseOverall(int count) {
		if (overall == UNKNOWN) {
			overall = count;
		}
		else {
			overall += count;
		}
	}

}
