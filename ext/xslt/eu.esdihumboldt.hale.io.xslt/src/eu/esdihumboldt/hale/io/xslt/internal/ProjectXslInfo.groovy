/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.internal

import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo


/**
 * Creates information on a project that can be inserted into an XSL document.
 * 
 * @author Simon Templer
 */
public class ProjectXslInfo {

	public static String getInfo(ProjectInfo info) {
		if (!info)
			return '<!-- No project information available -->'

		StringBuilder result = new StringBuilder()

		result << "<!-- Generated on ${new Date()} -->"

		if (info?.name) {
			result << "<!-- Project name: ${cmt(info.name)} -->"
		}
		if (info?.author) {
			result << "<!-- Project author: ${cmt(info.author)} -->"
		}
		if (info?.description) {
			result << "<!-- Project description: ${cmt(info.description)} -->"
		}

		result.toString()
	}

	private static String cmt(String value) {
		// replace all double dashes
		value.replaceAll(/\-\-+/, '-')
	}
}
