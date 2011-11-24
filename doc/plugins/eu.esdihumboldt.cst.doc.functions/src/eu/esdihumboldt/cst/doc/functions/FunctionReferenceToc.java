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

package eu.esdihumboldt.cst.doc.functions;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.AbstractTocProvider;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.IToc;
import org.eclipse.help.ITocContribution;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;
import org.eclipse.help.internal.toc.HrefUtil;

import com.google.common.base.Objects;

import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionExtension;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionExtension;

/**
 * Function reference table of contents provider.
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class FunctionReferenceToc extends AbstractTocProvider {
	
	/**
	 * Table of contents for the function reference
	 */
	public static class ReferenceToc implements IToc {
		
		private final ITopic typeFunctions;
		private final ITopic propertyFunctions;
		
		/**
		 * Default constructor 
		 */
		public ReferenceToc() {
			super();
			// create topics
			typeFunctions = new FunctionsTopic(
					TypeFunctionExtension.getInstance(),
					"Type relations");
			propertyFunctions = new FunctionsTopic(
					PropertyFunctionExtension.getInstance(),
					"Property relations");
		}

		/**
		 * @see IUAElement#isEnabled(IEvaluationContext)
		 */
		@Override
		public boolean isEnabled(IEvaluationContext context) {
			return true;
		}

		/**
		 * @see IUAElement#getChildren()
		 */
		@Override
		public IUAElement[] getChildren() {
			return getTopics();
		}

		/**
		 * @see IHelpResource#getHref()
		 */
		@Override
		public String getHref() {
			//TODO return HREF to main function reference description?
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * @see IHelpResource#getLabel()
		 */
		@Override
		public String getLabel() {
			return "Functions";
		}

		/**
		 * @see IToc#getTopics()
		 */
		@Override
		public ITopic[] getTopics() {
			return new ITopic[]{typeFunctions, propertyFunctions};
		}

		/**
		 * @see IToc#getTopic(String)
		 */
		@Override
		public ITopic getTopic(String href) {
			if (Objects.equal(typeFunctions.getHref(), href)) {
				return typeFunctions;
			}
			if (Objects.equal(propertyFunctions.getHref(), href)) {
				return propertyFunctions;
			}
			return null;
		}

	}

	/**
	 * TOC contribution for the function reference.
	 */
	public static class FunctionTocContribution implements ITocContribution {

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
			// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			return new String[]{};
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
			return "PLUGINS_ROOT/eu.esdihumboldt.hale.doc.user/toc.xml#reference";
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
			return new ReferenceToc();
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
	 * The identifier of this plug-in
	 */
	public static final String PLUGIN_ID = "eu.esdihumboldt.cst.doc.functions";

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
