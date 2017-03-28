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

package eu.esdihumboldt.hale.common.filter;

import static org.junit.Assert.*
import eu.esdihumboldt.hale.common.instance.model.Filter

/**
 * Tests for ECQL filter.
 * 
 * @author Simon Templer
 */
class ECQLFilterTest extends CQLFilterTest {

	@Override
	Filter filter(String expr) {
		new FilterGeoECqlImpl(expr)
	}
}
