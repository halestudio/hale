/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.commons.inspire.data;

/**
 * This is the INPSIRE Identifier object implementation
 * 
 * @author Ulrich Schaeffler
 * @partner 14 / TUM
 * @version $Id$
 */
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

		if (this.localID != null
				&& this.localID.equals(target.getLocalID()) == false)
			return false;
		else if (this.localID == null && target.getLocalID() != null)
			return false;

		if (this.nameSpace != null
				&& this.nameSpace.equals(target.getNameSpace()) == false)
			return false;
		else if (this.nameSpace == null && target.getNameSpace() != null)
			return false;

		if (this.versionID != null
				&& this.versionID.equals(target.getVersionID()) == false)
			return false;
		else if (this.versionID == null && target.getVersionID() != null)
			return false;

		return true;
	}

}
