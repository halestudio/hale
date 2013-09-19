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

import org.apache.wicket.markup.html.panel.FeedbackPanel;

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

		add(new FeedbackPanel("feedback"));
	}

}
