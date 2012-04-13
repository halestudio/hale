/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst.doc.functions.internal.toc;

import org.eclipse.help.AbstractTocProvider;
import org.eclipse.help.IToc;
import org.eclipse.help.ITocContribution;
import org.eclipse.help.internal.toc.HrefUtil;

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;



/**
 * Function reference table of contents provider.
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class FunctionReferenceToc extends AbstractTocProvider implements FunctionReferenceConstants {
	
	/**
	 * TOC contribution for the function reference.
	 */
	public static class FunctionTocContribution implements ITocContribution {

		private static final String[] NO_DOCS = new String[]{};

		private final String locale;
		
		/**
		 * @param locale the locale
		 */
		public FunctionTocContribution(String locale) {
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
			return HrefUtil.normalizeHref(PLUGIN_ID, "functions.xml");
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
			return new OneTopicToc(new FunctionReferenceTopic());
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
	public FunctionReferenceToc() {
		super();
	}

	/**
	 * @see AbstractTocProvider#getTocContributions(java.lang.String)
	 */
	@Override
	public ITocContribution[] getTocContributions(String locale) {
		return new ITocContribution[]{new FunctionTocContribution(locale)};
	}

}
