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

package eu.esdihumboldt.hale.server.webapp.components.user;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.User;
import eu.esdihumboldt.hale.server.webapp.components.bootstrap.BootstrapFeedbackPanel;
import eu.esdihumboldt.hale.server.webapp.util.UserUtil;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;

/**
 * Form for filling in basic user information.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("serial")
public class UserDetailsForm extends Panel {

	private static final ALogger log = ALoggerFactory.getLogger(UserDetailsForm.class);

	private final boolean newUser;

	private String name;

	private String surname;

	private String email;

	/**
	 * Create a user details form.
	 * 
	 * @param componentId the component ID in the markup
	 * @param newUser if the panel is displayed to a new user
	 */
	public UserDetailsForm(String componentId, boolean newUser) {
		super(componentId);
		this.newUser = newUser;

		OrientGraph graph = DatabaseHelper.getGraph();
		try {
			String login = UserUtil.getLogin();
			User user = User.getByLogin(graph, login);
			setName(user.getName());
			setSurname(user.getSurname());
			setEmail(user.getEmail());
		} catch (NonUniqueResultException e) {
			error("Internal error");
			log.error("Duplicate user");
		} finally {
			graph.shutdown();
		}

		BootstrapForm<UserDetailsForm> form = new BootstrapForm<UserDetailsForm>("details",
				new CompoundPropertyModel<>(this)) {

			@Override
			protected void onSubmit() {
				OrientGraph graph = DatabaseHelper.getGraph();
				try {
					User user = User.getByLogin(graph, UserUtil.getLogin());
					if (user == null) {
						error("User not found");
						return;
					}

					user.setName(getName());
					user.setSurname(getSurname());
					user.setEmail(getEmail());
				} catch (Exception e) {
					error("Internal error");
					log.error("Duplicate user");
				} finally {
					graph.shutdown();
				}

				if (UserDetailsForm.this.newUser) {
					// forward to home page
					setResponsePage(getApplication().getHomePage());
				}
			}

		};
		add(form);

		// fields
		form.add(new TextField<>("name"));
		form.add(new TextField<>("surname"));
		form.add(new TextField<>("email"));

		// cancel link
		BookmarkablePageLink<Void> cancelLink = new BookmarkablePageLink<>("cancel",
				getApplication().getHomePage());
		cancelLink.setVisible(newUser);
		form.add(cancelLink);

		// feedback
		form.add(new BootstrapFeedbackPanel("feedback"));
	}

	@SuppressWarnings("javadoc")
	public String getName() {
		return name;
	}

	@SuppressWarnings("javadoc")
	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("javadoc")
	public String getSurname() {
		return surname;
	}

	@SuppressWarnings("javadoc")
	public void setSurname(String surname) {
		this.surname = surname;
	}

	@SuppressWarnings("javadoc")
	public String getEmail() {
		return email;
	}

	@SuppressWarnings("javadoc")
	public void setEmail(String email) {
		this.email = email;
	}

}
