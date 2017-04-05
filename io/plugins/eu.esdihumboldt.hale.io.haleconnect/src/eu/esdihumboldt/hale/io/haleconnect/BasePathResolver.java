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
 * Interface for resolving the base path of a hale connect service
 * 
 * @author Florian Esser
 */
public interface BasePathResolver {

	/**
	 * Resolve the base path of a service
	 * 
	 * @param service The service to resolve the base path of, usually one of
	 *            the constants in {@link HaleConnectServices}
	 * @return the base path if it could be resolved, null otherwise
	 */
	String getBasePath(String service);
}
