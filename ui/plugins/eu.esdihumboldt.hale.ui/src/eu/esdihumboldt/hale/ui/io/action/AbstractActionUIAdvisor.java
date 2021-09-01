/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.io.action;

/**
 * Base implementation of an {@link ActionUIAdvisor}.
 * 
 * @param <T> the resource representation type
 * @author Simon Templer
 */
public abstract class AbstractActionUIAdvisor<T> implements ActionUIAdvisor<T> {

	@Override
	public boolean supportsRemoval(String resourceId) {
		return false;
	}

	@Override
	public boolean removeResource(String resourceId) {
		return false;
	}

	@Override
	public boolean supportsClear() {
		return false;
	}

	@Override
	public boolean clear() {
		return false;
	}

	@Override
	public boolean supportsRetrieval() {
		return false;
	}

	@Override
	public T retrieveResource(String resourceId) {
		return null;
	}

}
