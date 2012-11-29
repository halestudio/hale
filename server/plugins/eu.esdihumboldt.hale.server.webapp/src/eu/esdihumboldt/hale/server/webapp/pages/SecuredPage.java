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

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * A web page that only may be shown to a logged in user.
 * 
 * @author Simon Templer
 */
public abstract class SecuredPage extends BasePage {

	private static final long serialVersionUID = -1274734579343448637L;

	/**
	 * @see BasePage#BasePage()
	 */
	public SecuredPage() {
		super();
	}

	/**
	 * @see BasePage#BasePage(PageParameters)
	 */
	public SecuredPage(PageParameters parameters) {
		super(parameters);
	}

	/**
	 * @see BasePage#addControls(boolean)
	 */
	@Override
	protected final void addControls(boolean loggedIn) {
		if (!loggedIn) {
			throw new RuntimeException("Secured page cannot be created when no user is logged in");
		}
		else {
			super.addControls(loggedIn);
			addControls();
		}
	}

	/**
	 * Add the page controls
	 */
	protected void addControls() {
		// do nothing
	}

}
