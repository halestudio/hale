/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.concurrency;

/**
 * Default processing strategy.
 * 
 * @author Simon Templer
 */
public class DefaultProcessingStrategy implements ProcessingStrategy {

	private final int count;

	private final int interval;

	private long lastProcessing = 0;

	/**
	 * Constructor. Only one condition (count or interval) has to be true to
	 * allow processing
	 * 
	 * @param count the minimum number of partial results that allows processing
	 *            (values less than one will be ignored)
	 * @param interval the minimum time interval in milliseconds between
	 *            processing (values less than zero will be ignored)
	 */
	public DefaultProcessingStrategy(int count, int interval) {
		super();
		this.count = count;
		this.interval = interval;
	}

	/**
	 * @see ProcessingStrategy#allowProcess(int)
	 */
	@Override
	public boolean allowProcess(int size) {
		boolean allow = (count > 0 && size >= count)
				|| (interval >= 0 && System.currentTimeMillis() >= lastProcessing + interval);

		if (allow) {
			lastProcessing = System.currentTimeMillis();
		}

		return allow;
	}

}
