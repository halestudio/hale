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

package eu.esdihumboldt.hale.server.webapp.pages;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;

/**
 * Common page CSS resources.
 * 
 * @author Simon Templer
 */
public class PagesCSS {

	/**
	 * @return the CSS for a page that displays its content as bordered boxes
	 */
	public static CssReferenceHeaderItem boxesPage() {
		return CssReferenceHeaderItem.forReference(new CssResourceReference(PagesCSS.class,
				"css/boxes.css"));
	}

}
