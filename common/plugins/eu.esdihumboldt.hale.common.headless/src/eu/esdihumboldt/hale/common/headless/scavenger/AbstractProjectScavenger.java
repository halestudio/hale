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

package eu.esdihumboldt.hale.common.headless.scavenger;

import java.io.File;

import eu.esdihumboldt.util.scavenger.AbstractResourceScavenger;

/**
 * Base class for project scavengers.
 * 
 * @param <C> the update context type
 * @param <T> the concrete project reference type
 * @author Simon Templer
 */
public abstract class AbstractProjectScavenger<C, T extends ProjectReference<C>> extends
		AbstractResourceScavenger<T> {

	private final C context;

	/**
	 * Create a new project scavenger.
	 * 
	 * @param scavengeLocation the location to scan, if the location does not
	 *            exist or is not accessible, a default location inside the
	 *            platform instance location is used
	 * @param instanceLocPath the instance location sub-path to use if the
	 *            scavengeLocation is invalid or <code>null</code>, may be
	 *            <code>null</code> if the platform instance location should not
	 *            be used as fall-back
	 * @param context the update context
	 */
	public AbstractProjectScavenger(File scavengeLocation, String instanceLocPath, C context) {
		super(scavengeLocation, instanceLocPath);
		this.context = context;

		triggerScan();
	}

	/**
	 * @return the update context
	 */
	public C getContext() {
		return context;
	}

	@Override
	protected void updateResource(T reference, String resourceId) {
		reference.update(getContext());
	}

}
