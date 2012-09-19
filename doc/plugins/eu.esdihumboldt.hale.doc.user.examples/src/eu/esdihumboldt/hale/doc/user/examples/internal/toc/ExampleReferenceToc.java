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

package eu.esdihumboldt.hale.doc.user.examples.internal.toc;

import org.eclipse.help.AbstractTocProvider;
import org.eclipse.help.IToc;
import org.eclipse.help.ITocContribution;
import org.eclipse.help.internal.toc.HrefUtil;

import eu.esdihumboldt.hale.doc.user.examples.internal.ExamplesConstants;
import eu.esdihumboldt.hale.doc.util.toc.OneTopicToc;

/**
 * Example projects reference table of contents provider.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class ExampleReferenceToc extends AbstractTocProvider implements ExamplesConstants {

	/**
	 * TOC contribution for the examples reference.
	 */
	public static class ExampleTocContribution implements ITocContribution {

		private static final String[] NO_DOCS = new String[] {};

		private final String locale;

		/**
		 * @param locale the locale
		 */
		public ExampleTocContribution(String locale) {
			super();
			this.locale = locale;
		}

		/**
		 * @see ITocContribution#getCategoryId()
		 */
		@Override
		public String getCategoryId() {
			// no category
			return null;
		}

		/**
		 * @see ITocContribution#getContributorId()
		 */
		@Override
		public String getContributorId() {
			return PLUGIN_ID;
		}

		/**
		 * @see ITocContribution#getExtraDocuments()
		 */
		@Override
		public String[] getExtraDocuments() {
			// none
			return NO_DOCS;
		}

		/**
		 * @see ITocContribution#getId()
		 */
		@Override
		public String getId() {
			return HrefUtil.normalizeHref(PLUGIN_ID, "examples.xml");
		}

		/**
		 * @see ITocContribution#getLinkTo()
		 */
		@Override
		public String getLinkTo() {
			return PLUGINS_ROOT + "/eu.esdihumboldt.hale.doc.user/toc.xml#reference";
		}

		/**
		 * @see ITocContribution#getLocale()
		 */
		@Override
		public String getLocale() {
			return locale;
		}

		/**
		 * @see ITocContribution#getToc()
		 */
		@Override
		public IToc getToc() {
			return new OneTopicToc(new ExampleReferenceTopic());
		}

		/**
		 * @see ITocContribution#isPrimary()
		 */
		@Override
		public boolean isPrimary() {
			return false;
		}

	}

	/**
	 * Default constructor
	 */
	public ExampleReferenceToc() {
		super();
	}

	/**
	 * @see AbstractTocProvider#getTocContributions(java.lang.String)
	 */
	@Override
	public ITocContribution[] getTocContributions(String locale) {
		return new ITocContribution[] { new ExampleTocContribution(locale) };
	}

}
