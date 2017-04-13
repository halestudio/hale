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

package eu.esdihumboldt.hale.io.haleconnect;

/**
 * Interface for hale connect base path managers.
 * 
 * @author Florian Esser
 */
public interface BasePathManager extends BasePathResolver {

	/**
	 * Set the base path of a hale connect microservice (e.g.
	 * "https://users.haleconnect.com/v1" for the user service)
	 *
	 * @param service service to set the base path for, usually one of the
	 *            constants defined in {@link HaleConnectServices}
	 * @param basePath Base path to set
	 */
	void setBasePath(String service, String basePath);
}
