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

package eu.esdihumboldt.cst.functions.inspire.data;

/**
 * This is the INPSIRE Identifier object implementation
 * 
 * @author Ulrich Schaeffler
 * @partner 14 / TUM
 */
@SuppressWarnings("javadoc")
public class InspireIdentifier {

	private String localID = null;
	private String nameSpace = null;
	private String versionID = null;

	public InspireIdentifier() {
	}

	public String getLocalID() {
		return localID;
	}

	public void setLocalID(String localID) {
		this.localID = localID;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public String getVersionID() {
		return versionID;
	}

	public void setVersionID(String versionID) {
		this.versionID = versionID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((localID == null) ? 0 : localID.hashCode());
		result = prime * result + ((nameSpace == null) ? 0 : nameSpace.hashCode());
		result = prime * result + ((versionID == null) ? 0 : versionID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InspireIdentifier other = (InspireIdentifier) obj;
		if (localID == null) {
			if (other.localID != null)
				return false;
		}
		else if (!localID.equals(other.localID))
			return false;
		if (nameSpace == null) {
			if (other.nameSpace != null)
				return false;
		}
		else if (!nameSpace.equals(other.nameSpace))
			return false;
		if (versionID == null) {
			if (other.versionID != null)
				return false;
		}
		else if (!versionID.equals(other.versionID))
			return false;
		return true;
	}

}
