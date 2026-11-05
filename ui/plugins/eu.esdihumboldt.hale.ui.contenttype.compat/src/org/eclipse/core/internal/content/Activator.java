/*
 * Copyright (c) 2026 wetransform GmbH
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 */

package org.eclipse.core.internal.content;

import javax.xml.parsers.SAXParserFactory;

/**
 * Stand-in for the internal activator of org.eclipse.core.contenttype, which
 * no longer exists since Eclipse 2023-12 (contenttype 3.9).
 *
 * hale-core workaround: the XML content describers of
 * eu.esdihumboldt.hale.util.nonosgi (XMLRootHandler) still call
 * {@code Activator.getDefault().getFactory()} to get a SAX parser factory.
 * Without this class they fail with a NoClassDefFoundError and the content
 * types (e.g. hale projects) are disabled.
 *
 * Like the original, {@link #getFactory()} provides a namespace aware factory:
 * the describers match root elements by namespace and local name, which a
 * factory that is not namespace aware does not report (hale-core's own
 * fallback if there is no activator). Alignments of format 2.5 were then
 * passed to the reader of format 2.6.
 *
 * The class is added to a package of a signed bundle; this only works as long
 * as Equinox does not verify signed content (osgi.signedcontent.support).
 *
 * Remove once hale-core no longer refers to the activator.
 */
public class Activator {

	private static final Activator INSTANCE = new Activator();

	/**
	 * @return the activator
	 */
	public static Activator getDefault() {
		return INSTANCE;
	}

	/**
	 * @return a new namespace aware SAX parser factory
	 */
	public SAXParserFactory getFactory() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory;
	}

}
