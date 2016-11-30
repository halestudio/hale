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

package eu.esdihumboldt.hale.io.jdbc.mssql.util;

/**
 * A class to hold values related SRS.
 * 
 * @author Arun
 */
public class SRS {

	private String authorizedSrId;

	private String authorityName;

	private String srsText;

	/**
	 * A default constructor
	 * 
	 */
	protected SRS() {

		this.authorizedSrId = null;
		this.authorityName = null;
		this.srsText = null;
	}

	/**
	 * A constructor
	 * 
	 * @param authorizedSrId An authorized spatial reference id
	 * @param authorityName An authority name
	 * @param srsText well known text of SRS
	 */
	protected SRS(String authorizedSrId, String authorityName, String srsText) {

		this.authorizedSrId = authorizedSrId;
		this.authorityName = authorityName;
		this.srsText = srsText;
	}

	/**
	 * @return the authorizedSrId
	 */
	protected String getAuthorizedSrId() {
		return authorizedSrId;
	}

	/**
	 * @return the authorityName
	 */
	protected String getAuthorityName() {
		return authorityName;
	}

	/**
	 * @return the srsText
	 */
	protected String getSrsText() {
		return srsText;
	}

	/**
	 * @param authorizedSrId the authorizedSrId to set
	 */
	protected void setAuthorizedSrId(String authorizedSrId) {
		this.authorizedSrId = authorizedSrId;
	}

	/**
	 * @param authorityName the authorityName to set
	 */
	protected void setAuthorityName(String authorityName) {
		this.authorityName = authorityName;
	}

	/**
	 * @param srsText the srsText to set
	 */
	protected void setSrsText(String srsText) {
		this.srsText = srsText;
	}

}
