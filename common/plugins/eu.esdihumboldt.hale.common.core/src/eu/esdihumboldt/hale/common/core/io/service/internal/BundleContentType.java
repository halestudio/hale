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

package eu.esdihumboldt.hale.common.core.io.service.internal;

import org.osgi.framework.Bundle;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ContentTypeTester;
import eu.esdihumboldt.hale.common.core.io.internal.ContentTypeDefinition;

/**
 * A content type associated with a bundle
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class BundleContentType {
	
	private static final ALogger log = ALoggerFactory.getLogger(BundleContentType.class);
	
	private final Bundle bundle;
	
	private final ContentTypeDefinition contentType;

	/**
	 * Create a bundle content type
	 * 
	 * @param bundle the bundle
	 * @param contentType the content type definition
	 */
	public BundleContentType(Bundle bundle, ContentTypeDefinition contentType) {
		super();
		this.bundle = bundle;
		this.contentType = contentType;
	}
	
	/**
	 * Get the content type tester associated with the content type
	 * 
	 * @return the content type tester or <code>null</code> if none associated
	 */
	public ContentTypeTester getTester() {
		String testerClass = contentType.getTesterClassName();
		if (testerClass != null) {
			try {
				Class<?> clazz = bundle.loadClass(testerClass);
				return (ContentTypeTester) clazz.newInstance();
			} catch (ClassNotFoundException e) {
				log.warn("Could not load content type tester class " + testerClass, e);
			} catch (Exception e) {
				log.warn("Could not instantiate content type tester class " + testerClass, e);
			}
		}
		
		return null;
	}

	/**
	 * @return the bundle
	 */
	public Bundle getBundle() {
		return bundle;
	}

	/**
	 * @return the contentType
	 */
	public ContentTypeDefinition getContentType() {
		return contentType;
	}

}
