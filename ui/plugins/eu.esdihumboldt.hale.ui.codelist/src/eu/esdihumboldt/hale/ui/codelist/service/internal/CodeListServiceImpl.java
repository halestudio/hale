/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.codelist.service.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.ui.codelist.service.CodeListService;

/**
 * Code list service
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CodeListServiceImpl implements CodeListService {
	
//	private static final ALogger log = ALoggerFactory.getLogger(CodeListServiceImpl.class);
	
//	private boolean initialized = false;
	
	/**
	 * Maps code list identifiers to code lists
	 */
	private final Map<String, CodeList> codelists = new HashMap<String, CodeList>();
	
//	/**
//	 * Maps attribute identifiers to code lists
//	 */
//	private final Map<String, CodeList> cachedAttributeCodeLists = new HashMap<String, CodeList>();

	/**
	 * @see CodeListService#findCodeListByIdentifier(String, String)
	 */
	@Override
	public CodeList findCodeListByIdentifier(String namespace, String identifier) {
//		if (!initialized) {
//			init();
//			initialized = true;
//		}
		
		String key = namespace + "/" + identifier; //$NON-NLS-1$
		return codelists.get(key);
	}

	/**
	 * @see CodeListService#getCodeLists()
	 */
	@Override
	public List<CodeList> getCodeLists() {
//		if (!initialized) {
//			init();
//			initialized = true;
//		}
		
		return new ArrayList<CodeList>(codelists.values());
	}

//	/**
//	 * @see CodeListService#assignAttributeCodeList(String, CodeList)
//	 */
//	@Override
//	public void assignAttributeCodeList(String attributeIdentifier,
//			CodeList codeList) {
//		if (codeList != null) {
//			// cache code list
//			cachedAttributeCodeLists.put(attributeIdentifier, codeList);
//			
//			// store assignment
//			CodeListPreferenceInitializer.assignCodeList(attributeIdentifier, codeList.getLocation());
//		}
//		else {
//			cachedAttributeCodeLists.remove(attributeIdentifier);
//			CodeListPreferenceInitializer.assignCodeList(attributeIdentifier, null);
//		}
//	}

//	/**
//	 * @see CodeListService#findCodeListByAttribute(String)
//	 */
//	@Override
//	public CodeList findCodeListByAttribute(String attributeIdentifier) {
//		CodeList cached = cachedAttributeCodeLists.get(attributeIdentifier);
//		if (cached != null) {
//			// return cached code list
//			return cached;
//		}
//		else {
//			URI location = CodeListPreferenceInitializer.getAssignedCodeList(attributeIdentifier);
//			if (location != null) {
//				try {
//					// load the code list
//					CodeList codeList = new XmlCodeList(location.toURL().openStream(), location);
//					cachedAttributeCodeLists.put(attributeIdentifier, codeList);
//					return codeList;
//				} catch (Exception e) {
//					log.error("Error loading code list from " + location); //$NON-NLS-1$
//				}
//			}
//			return null;
//		}
//	}

//	/**
//	 * @see CodeListService#searchPathChanged()
//	 */
//	@Override
//	public void searchPathChanged() {
//		initialized = false;
//	}

//	/**
//	 * Initialize the 
//	 */
//	private void init() {
//		codelists.clear();
//		
//		List<String> paths = CodeListPreferenceInitializer.getSearchPath();
//		for (String path : paths) {
//			loadSearchPath(path);
//		}
//	}

//	/**
//	 * Load the code lists found on the given path
//	 * 
//	 * @param path the path
//	 */
//	private void loadSearchPath(String path) {
//		File searchPath = new File(path);
//		
//		File[] candidates = searchPath.listFiles(new FilenameFilter() {
//			
//			@Override
//			public boolean accept(File dir, String name) {
//				return name.toLowerCase().endsWith(".xml"); //$NON-NLS-1$
//			}
//		});
//		
//		if (candidates == null) {
//			log.warn("No potential code list files found in " + path); //$NON-NLS-1$
//		}
//		else {
//			for (File candidate : candidates) {
//				try {
//					addSearchPathCodeList(new FileInputStream(candidate), candidate.toURI());
//				} catch (FileNotFoundException e) {
//					// ignore
//				}
//			}
//		}
//	}

//	/**
//	 * Add a search path code list
//	 * 
//	 * @param in the input stream
//	 * @param location the code list location
//	 */
//	private void addSearchPathCodeList(InputStream in, URI location) {
//		try {
//			CodeList codeList = new XmlCodeList(in, location);
//			String key = codeList.getNamespace() + "/" + codeList.getIdentifier(); //$NON-NLS-1$
//			searchPathCodeLists.put(key, codeList);
//		} catch (Exception e) {
//			// ignore
//			log.debug("Tried to load code list but failed", e); //$NON-NLS-1$
//		}
//	}

	/**
	 * @see CodeListService#addCodeList(CodeList)
	 */
	@Override
	public void addCodeList(CodeList code) {

		String ident = code.getNamespace() + "/" + code.getIdentifier();
		codelists.put(ident, code);
	}

}
