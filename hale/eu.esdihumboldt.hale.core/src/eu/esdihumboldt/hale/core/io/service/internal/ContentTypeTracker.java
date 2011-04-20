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

package eu.esdihumboldt.hale.core.io.service.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.osgi.util.extender.ContextBundleTracker;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.internal.ContentTypeDefinition;
import eu.esdihumboldt.hale.core.io.service.ContentTypeService;

/**
 * Tracks {@link ContentTypeDefinition} files
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ContentTypeTracker extends ContextBundleTracker implements ContentTypeService {
	
	private static final ALogger log = ALoggerFactory.getLogger(ContentTypeTracker.class);
	
	private final Map<ContentType, BundleContentType> contentTypes = new HashMap<ContentType, BundleContentType>();

	/**
	 * Creates the content type tracker
	 */
	public ContentTypeTracker() {
		super(TrackingMode.Resolved);
	}

	/**
	 * @see ContextBundleTracker#registerBundleContextual(Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Object registerBundleContextual(Bundle bundle) {
		// load content type definitions
		Enumeration<URL> files = bundle.findEntries("META-INF/contentType", "*.xml", false);
		List<BundleContentType> types = null;
		if (files != null) {
			while (files.hasMoreElements()) {
				URL file = files.nextElement();
				
				try {
					ContentTypeDefinition def = ContentTypeDefinition.load(file.openStream());
					if (types == null) {
						types = new ArrayList<BundleContentType>();
					}
					types.add(new BundleContentType(bundle, def));
				} catch(Exception e) {
					log.error("Error loading content type definition from " + file.toString(), e);
				}
			}
		}
		
		// register content type definitions
		if (types != null) {
			synchronized (contentTypes) {
				for (BundleContentType type : types) {
					ContentType ct = ContentType.getContentType(type.getContentType().getIdentifier());
					
					BundleContentType current = contentTypes.get(ct);
					if (current == null) {
						contentTypes.put(ct, type);
						log.info("Added definition of content type " + ct + 
								" from bundle " + type.getBundle().getSymbolicName() + ".");
					}
					else {
						//TODO allow multiple content type definitions?
						log.warn("Duplicate definition of content type " + ct + 
								" found. Definition from bundle " + 
								type.getBundle().getSymbolicName() + " is ignored.");
					}
				}
			}
		}
		
		return types;
	}

	/**
	 * @see ContextBundleTracker#unregisterBundleContextual(Bundle, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void unregisterBundleContextual(Bundle bundle, Object context) {
		List<BundleContentType> types = (List<BundleContentType>) context;

		// deregister content type definitions
		if (types != null) {
			synchronized (contentTypes) {
				for (BundleContentType type : types) {
					ContentType ct = ContentType.getContentType(type.getContentType().getIdentifier());
					
					BundleContentType current = contentTypes.get(ct);
					if (current != null) {
						if (current.getBundle().equals(bundle)) {
							contentTypes.remove(ct);
						}
					}
				}
			}
		}
	}

	/**
	 * @see ContentTypeService#getContentTypes()
	 */
	@Override
	public Iterable<ContentType> getContentTypes() {
		return Collections.unmodifiableSet(contentTypes.keySet());
	}

	/**
	 * @see ContentTypeService#getDisplayName(ContentType)
	 */
	@Override
	public String getDisplayName(ContentType contentType) {
		BundleContentType type = getBundleContentType(contentType);
		if (type != null) {
			Map<Locale, String> names = type.getContentType().getNames();
			if (names != null) {
				// try to get a localized name
				
				// default locale
				Locale locale = Locale.getDefault();
				String candidate = names.get(locale);
				if (candidate != null) return candidate;
				
				// default locale with language only
				locale = new Locale(locale.getLanguage());
				candidate = names.get(locale);
				if (candidate != null) return candidate;
			}
			
			// fall-back to default name
			return type.getContentType().getDefaultName();
		}
		else {
			// default display name if no definition is present
			return contentType + " file";
		}
	}

	private BundleContentType getBundleContentType(ContentType contentType) {
		BundleContentType type;
		synchronized (contentTypes) {
			type = contentTypes.get(contentType);
		}
		return type;
	}

	/**
	 * @see ContentTypeService#getFileExtensions(ContentType)
	 */
	@Override
	public String[] getFileExtensions(ContentType contentType) {
		BundleContentType type = getBundleContentType(contentType);
		if (type != null) {
			Set<String> extensions = type.getContentType().getFileExtensions();
			if (extensions != null) {
				String[] result = new String[extensions.size()];
				int index = 0;
				for (String ext : extensions) {
					ext = ext.trim();
					if (ext.startsWith(".")) {
						ext = ext.substring(1);
					}
					result[index++] = ext;  
				}
				
				return result;
			}
		}
		
		return null;
	}

}
