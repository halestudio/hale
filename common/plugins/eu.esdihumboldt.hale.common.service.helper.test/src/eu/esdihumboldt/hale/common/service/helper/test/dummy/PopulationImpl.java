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

package eu.esdihumboldt.hale.common.service.helper.test.dummy;


/**
 * Default implementation instance/property {@link Population}
 * 
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
