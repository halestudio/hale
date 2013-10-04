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

package eu.esdihumboldt.hale.server.webapp.components.openidselector;

import org.apache.wicket.request.resource.CssResourceReference;

/**
 * A CSS reference that loads the CSS resources needed for the OpenID selector.
 */
public class OpenIdSelectorCssReference extends CssResourceReference {

	private static final long serialVersionUID = -1426189285818634246L;

	/**
	 * The singleton instance.
	 */
	public static final OpenIdSelectorCssReference INSTANCE = new OpenIdSelectorCssReference();

	private OpenIdSelectorCssReference() {
		super(OpenIdSelectorCssReference.class, "css/openid.css");
	}
}
