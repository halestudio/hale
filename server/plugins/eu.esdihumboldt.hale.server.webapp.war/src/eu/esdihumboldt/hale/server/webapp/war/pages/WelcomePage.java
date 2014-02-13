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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.webapp.war.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.osgi.framework.Bundle;
import org.springframework.osgi.web.deployer.support.DefaultContextPathStrategy;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.server.webapp.pages.BasePage;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;
import eu.esdihumboldt.hale.server.webapp.war.internal.Activator;

/**
 * The main page for the administration interface. War bundles will be listed
 * (except the this bundle and other bundles where it is not appropriate)
 * according to their name specified by the <code>Webapp-Name</code> MANIFEST.MF
 * header. Bundles having the <code>Webapp-Hide</code> header set to
 * <code>true</code> will not be listed on the welcome page.
 * 
 * @author Michel Kraemer
 */
@PageDescription(title = "Home", root = true)
public class WelcomePage extends BasePage {

	private static final long serialVersionUID = 4112352006256827014L;

	/**
	 * Header keys
	 */
	private static final String WEBAPP_NAME = "Webapp-Name";
	private static final String WEBAPP_HIDE = "Webapp-Hide";
	private static final String BUNDLE_NAME = "Bundle-Name";

	/**
	 * log4j Logger
	 */
	private static final ALogger _log = ALoggerFactory.getLogger(WelcomePage.class);

	/**
	 * Information about a war bundle
	 */
	private static class BundleInfo {

		String path;
		String name;
	}

	/**
	 * Default constructor
	 */
	public WelcomePage() {
		// nothing to do here
	}

	@Override
	protected void addControls(boolean loggedIn) {
		super.addControls(loggedIn);

		// create a model which loads the list of war bundles dynamically
		IModel<List<BundleInfo>> listViewModel = new LoadableDetachableModel<List<BundleInfo>>() {

			private static final long serialVersionUID = 8919477639656535497L;

			@Override
			protected List<BundleInfo> load() {
				// get context paths of other war bundles
				List<BundleInfo> wars = new ArrayList<BundleInfo>();
				Activator aa = Activator.getInstance();
				DefaultContextPathStrategy s = new DefaultContextPathStrategy();
				for (Bundle b : aa.getWarBundles()) {
					if (isHidden(b)) {
						continue;
					}

					BundleInfo bi = new BundleInfo();
					bi.name = getHumanReadableName(b);
					bi.path = s.getContextPath(b);
					wars.add(bi);
				}

				// sort list
				Collections.sort(wars, new Comparator<BundleInfo>() {

					@Override
					public int compare(BundleInfo o1, BundleInfo o2) {
						return o1.name.compareTo(o2.name);
					}
				});

				return wars;
			}
		};

		// fill list view
		ListView<BundleInfo> lv = new ListView<BundleInfo>("applications", listViewModel) {

			private static final long serialVersionUID = -3861139762631118268L;

			@Override
			protected void populateItem(ListItem<BundleInfo> item) {
				BundleInfo bi = item.getModelObject();
				item.add(new ExternalLink("path", bi.path, bi.name));
			}
		};
		add(lv);
	}

	/**
	 * Returns a bundle name that can be viewed on the welcome page
	 * 
	 * @param b the bundle
	 * @return the bundle's name
	 */
	private static String getHumanReadableName(Bundle b) {
		String name = b.getHeaders().get(WEBAPP_NAME);
		if (name == null) {
			_log.warn("Bundle " + b.getSymbolicName() + " has no " + "\"" + WEBAPP_NAME
					+ "\" header defined. Using " + "\"" + BUNDLE_NAME + "\"");
			name = b.getHeaders().get(BUNDLE_NAME);
		}
		return name;
	}

	/**
	 * Checks if a bundle should be listed on the welcome page
	 * 
	 * @param b the bundle
	 * @return true if the bundle is hidden, false if it should be listed
	 */
	private static boolean isHidden(Bundle b) {
		String hide = b.getHeaders().get(WEBAPP_HIDE);
		return Boolean.parseBoolean(hide);
	}
}
