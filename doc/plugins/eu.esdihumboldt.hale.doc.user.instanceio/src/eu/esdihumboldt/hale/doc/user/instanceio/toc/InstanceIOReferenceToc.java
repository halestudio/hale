/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.doc.user.instanceio.toc;

import org.eclipse.help.AbstractTocProvider;
import org.eclipse.help.IToc;
import org.eclipse.help.ITocContribution;
import org.eclipse.help.internal.toc.HrefUtil;

import eu.esdihumboldt.hale.doc.user.instanceio.InstanceIOReferenceConstants;
import eu.esdihumboldt.hale.doc.util.toc.OneTopicToc;

/**
 * The Topic of instance reader and writer.
 * 
 * @author Yasmina Kammeyer
 */
@SuppressWarnings("restriction")
public class InstanceIOReferenceToc extends AbstractTocProvider implements
		InstanceIOReferenceConstants {

	/**
	 * TOC contribution for the function reference.
	 */
	public static class InstanceIOTocContribution implements ITocContribution {

		private static final String[] NO_DOCS = new String[] {};

		private final String locale;

		/**
		 * @param locale the locale
		 */
		public InstanceIOTocContribution(String locale) {
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
			return HrefUtil.normalizeHref(PLUGIN_ID, "instanceIO.xml");
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
			return new OneTopicToc(new InstanceIOReferenceTopic());
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
	public InstanceIOReferenceToc() {
		super();
	}

	/**
	 * @see AbstractTocProvider#getTocContributions(java.lang.String)
	 */
	@Override
	public ITocContribution[] getTocContributions(String locale) {
		return new ITocContribution[] { new InstanceIOTocContribution(locale) };
	}

}
