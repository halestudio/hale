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

import groovy.transform.CompileStatic

/**
 * Information on a hale connect project
 * 
 * @author Florian Esser
 */
@CompileStatic
public class HaleConnectProjectInfo {

	private final String id;
	private final HaleConnectUserInfo user;
	private final HaleConnectOrganisationInfo organisation;
	private final String name;
	private final String author;
	private final Long lastModified;

	/**
	 * Create the project info
	 * 
	 * @param id Project ID
	 * @param user information about the owner (if owned by a user, otherwise
	 *            <code>null</code>)
	 * @param organisation information about the owner (if owned by an
	 *            organisation, otherwise <code>null</code>)
	 * @param name Project name
	 * @param author Project author
	 * @param lastModified The timestamp when the project was last modified on
	 *            hale connect
	 */
	public HaleConnectProjectInfo(String id, HaleConnectUserInfo user,
	HaleConnectOrganisationInfo organisation, String name, String author,
	Long lastModified) {
		this.id = id;
		this.user = user;
		this.organisation = organisation;
		this.name = name;
		this.author = author;
		this.lastModified = lastModified;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the userId
	 */
	public HaleConnectUserInfo getUser() {
		return user;
	}

	/**
	 * @return the orgId
	 */
	public HaleConnectOrganisationInfo getOrganisation() {
		return organisation;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the project's author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return the {@link Owner} of the project
	 */
	public Owner getOwner() {
		if (user != null && !user.getUserId().isEmpty()) {
			return new Owner(type: OwnerType.USER, id: user.getUserId());
		}
		else if (organisation != null && !organisation.getId().isEmpty()) {
			return new Owner(type: OwnerType.ORGANISATION, id: organisation.getId());
		}
		else {
			throw new IllegalStateException("Unknown owner type");
		}
	}

	/**
	 * @return The last modified timestamp of this hale connect project
	 */
	public Long getLastModified() {
		return lastModified;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((organisation == null) ? 0 : organisation.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this.is(obj))
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HaleConnectProjectInfo other = (HaleConnectProjectInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (organisation == null) {
			if (other.organisation != null)
				return false;
		}
		else if (!organisation.equals(other.organisation))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		}
		else if (!user.equals(other.user))
			return false;
		return true;
	}
}
