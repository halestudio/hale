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

package eu.esdihumboldt.cst.functions.groovy.helpers.util;

import eu.esdihumboldt.util.groovy.collector.PropertyCollector;

/**
 * Thread-safe helper for collecting values.
 * 
 * @author Simon Templer
 */
public class Collector extends PropertyCollector<Object, Collector> {

	@Override
	protected Object getPropertyKey(String property) {
		return property;
	}

	@Override
	protected Collector createCollector() {
		return new Collector();
	}

}
