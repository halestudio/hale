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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.webapp.pages;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;

import eu.esdihumboldt.hale.server.webapp.components.bootstrap.BootstrapFeedbackPanel;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * Default login page.
 * 
 * @author Simon Templer
 */
@PageDescription(title = "Login")
public class LoginPage extends BasePage {

	private static final long serialVersionUID = -4327575549717532905L;

	/**
	 * Name of the failure parameter
	 */
	public static final String PARAM_FAILURE = "failure";

	/**
	 * Failure types
	 */
	public enum Failure {
		/** username not found */
		principal,
		/** bad credentials */
		credentials,
		/** the account is disabled */
		disabled,
		/** unknown */
		unknown
	}

	/**
	 * Default constructor
	 */
	public LoginPage() {
		super();
	}

	@Override
	protected void addControls(boolean loggedIn) {
		super.addControls(loggedIn);

		String failureName = getRequest().getRequestParameters().getParameterValue(PARAM_FAILURE)
				.toOptionalString();

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
		response.render(OnLoadHeaderItem.forScript("document.f.j_username.focus();"));

		// add boxes css to page
		response.render(PagesCSS.boxesPage());
	}

}
