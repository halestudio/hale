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

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

import eu.esdihumboldt.hale.server.webapp.components.bootstrap.BootstrapFeedbackPanel;
import eu.esdihumboldt.hale.server.webapp.components.openidselector.OpenIdSelectorJsReference;
import eu.esdihumboldt.hale.server.webapp.pages.LoginPage.Failure;

/**
 * Page for logging in with OpenID.
 * 
 * @author Simon Templer
 */
public class OpenIdLoginPage extends BasePage {

	private static final long serialVersionUID = 8764228585413860290L;

	/**
	 * Default constructor.
	 */
	public OpenIdLoginPage() {
		super();
	}

	@Override
	protected void addControls(boolean loggedIn) {
		super.addControls(loggedIn);

		String failureName = getRequest().getRequestParameters()
				.getParameterValue(LoginPage.PARAM_FAILURE).toOptionalString();

		if (failureName != null) {
			Failure failure;
			try {
				failure = Failure.valueOf(failureName);
			} catch (IllegalArgumentException e) {
				failure = Failure.unknown;
			}

			switch (failure) {
			case credentials:
				error("User name or password wrong.");
				break;
			case principal:
				error("User name invalid.");
				break;
			case disabled:
				error("The account is disabled.");
				break;
			case unknown:
			default:
				error("Authentication error. Please try again later.");
			}
		}

		add(new BootstrapFeedbackPanel("feedback"));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		// set focus to username field
//		response.render(OnLoadHeaderItem.forScript("document.f.openid_identifier.focus();"));

		// add boxes css to page
		response.render(PagesCSS.boxesPage());

		// add Open ID selector js/css
		response.render(JavaScriptHeaderItem.forReference(OpenIdSelectorJsReference.INSTANCE));

		// init Open ID selector
		response.render(OnDomReadyHeaderItem.forScript("openid.init('openid_identifier');"));
	}

}
