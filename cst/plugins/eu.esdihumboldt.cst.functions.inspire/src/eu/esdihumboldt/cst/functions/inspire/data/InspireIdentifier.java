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

	public boolean equals(InspireIdentifier target) {
		if (target == null)
			return false;

		if (this.localID != null && this.localID.equals(target.getLocalID()) == false)
			return false;
		else if (this.localID == null && target.getLocalID() != null)
			return false;

		if (this.nameSpace != null && this.nameSpace.equals(target.getNameSpace()) == false)
			return false;
		else if (this.nameSpace == null && target.getNameSpace() != null)
			return false;

		if (this.versionID != null && this.versionID.equals(target.getVersionID()) == false)
			return false;
		else if (this.versionID == null && target.getVersionID() != null)
			return false;

		return true;
	}

}
