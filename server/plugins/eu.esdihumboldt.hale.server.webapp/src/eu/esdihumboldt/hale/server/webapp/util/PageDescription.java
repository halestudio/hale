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

package eu.esdihumboldt.hale.server.webapp.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.apache.wicket.markup.html.WebPage;

/**
 * An annotation to describe a page
 * 
 * @author Michel Kraemer
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PageDescription {

	/**
	 * @return the page title
	 */
	String title();

	/**
	 * @return the page's parent used for breadcrumbs
	 */
	Class<? extends WebPage> parent() default WebPage.class;

	/**
	 * @return true if this page is the root of all web applications
	 */
	boolean root() default false;
}
