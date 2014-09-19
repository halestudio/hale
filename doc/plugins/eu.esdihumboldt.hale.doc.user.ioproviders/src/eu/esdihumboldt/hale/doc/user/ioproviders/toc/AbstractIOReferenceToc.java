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

package eu.esdihumboldt.hale.doc.user.ioproviders.toc;

import org.eclipse.help.AbstractTocProvider;
import org.eclipse.help.IToc;
import org.eclipse.help.ITocContribution;
import org.eclipse.help.internal.toc.HrefUtil;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.doc.user.ioproviders.IOReferenceConstants;
import eu.esdihumboldt.hale.doc.util.toc.OneTopicToc;

/**
 * The Topic of instance reader and writer.
 * 
 * @author Yasmina Kammeyer
 */
@SuppressWarnings("restriction")
public abstract class AbstractIOReferenceToc extends AbstractTocProvider implements
		IOReferenceConstants {

	/**
	 * Constant for no extra documents.
	 */
	protected static final String[] NO_DOCS = new String[] {};

	/**
	 * TOC contribution for the function reference.
	 */
	public class IOTocContribution implements ITocContribution {

		private final String locale;

		/**
		 * @param locale the locale
		 */
		public IOTocContribution(String locale) {
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
			return HrefUtil.normalizeHref(PLUGIN_ID, topic.getProviderClass().getSimpleName()
					+ ".xml");
		}

		/**
		 * @see ITocContribution#getLinkTo()
		 */
		@Override
		public String getLinkTo() {
			return PLUGINS_ROOT + "/eu.esdihumboldt.hale.doc.user/toc.xml#" + anchor;
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
			return new OneTopicToc(topic);
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
	 * The topic.
	 */
	protected final IOReferenceTopic topic;
	/**
	 * The help TOC anchor.
	 */
	protected final String anchor;

	/**
	 * Default constructor
	 * 
	 * @param topic the I/O reference topic
	 */
	public AbstractIOReferenceToc(IOReferenceTopic topic) {
		super();
		this.topic = topic;

		// determine anchor
		Class<? extends IOProvider> providerclass = topic.getProviderClass();
		if (ImportProvider.class.isAssignableFrom(providerclass)) {
			anchor = "import-start";
		}
		else {
			anchor = "export-start";
		}
	}

	@Override
	public ITocContribution[] getTocContributions(String locale) {
		return new ITocContribution[] { new IOTocContribution(locale) };
	}

}
