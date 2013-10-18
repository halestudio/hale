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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;

/**
 * A JavaScript reference that loads the JavaScript resources needed for the
 * OpenID selector.
 */
public class OpenIdSelectorJsReference extends JQueryPluginResourceReference {

	private static final long serialVersionUID = 3621586047545561338L;

	/**
	 * The singleton instance.
	 */
	public static final OpenIdSelectorJsReference INSTANCE = new OpenIdSelectorJsReference();

	private OpenIdSelectorJsReference() {
		super(OpenIdSelectorJsReference.class, "js/openid-en.js");
	}

	@Override
	public Iterable<? extends HeaderItem> getDependencies() {
		List<HeaderItem> deps = new ArrayList<HeaderItem>();
		for (HeaderItem dep : super.getDependencies()) {
			deps.add(dep);
		}
		deps.add(CssHeaderItem.forReference(OpenIdSelectorCssReference.INSTANCE));
		deps.add(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(
				OpenIdSelectorJsReference.class, "js/openid-jquery.js")));
		return deps;
	}
}
