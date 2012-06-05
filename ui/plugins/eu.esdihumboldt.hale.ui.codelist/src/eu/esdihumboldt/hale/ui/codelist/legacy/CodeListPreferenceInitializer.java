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

package eu.esdihumboldt.hale.ui.codelist.legacy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.ui.codelist.internal.CodeListUIPlugin;

/**
 * Code list preferences
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CodeListPreferenceInitializer extends
		AbstractPreferenceInitializer implements CodeListPreferenceConstants {

	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferences = CodeListUIPlugin.getDefault().getPreferenceStore();
		
		preferences.setDefault(KEY_SEARCH_PATH_COUNT, 0);
	}
	
	/**
	 * Save the search path to the preferences
	 * 
	 * @param searchPath the search path
	 */
	public static void setSearchPath(List<String> searchPath) {
		IPreferenceStore preferences = CodeListUIPlugin.getDefault().getPreferenceStore();
		
		//TODO remove old ones
		/*int oldCount = preferences.getInt(KEY_SEARCH_PATH_COUNT);
		int numRemove = oldCount - searchPath.size();
		
		if (numRemove > 0) {
			for (int i = ...)
		}*/
		
		// set new path
		preferences.setValue(KEY_SEARCH_PATH_COUNT, searchPath.size());
		for (int i = 0; i < searchPath.size(); i++) {
			preferences.setValue(KEY_SEARCH_PATH_PREFIX + i, searchPath.get(i));
		}
	}
	
	/**
	 * Get the search path from the preferences
	 * 
	 * @return the search path
	 */
	public static List<String> getSearchPath() {
		IPreferenceStore preferences = CodeListUIPlugin.getDefault().getPreferenceStore();
		
		List<String> result = new ArrayList<String>();
		int count = preferences.getInt(KEY_SEARCH_PATH_COUNT);
		
		for (int i = 0; i < count; i++) {
			String path = preferences.getString(KEY_SEARCH_PATH_PREFIX + i);
			if (path != null) {
				result.add(path);
			}
		}
		
		return result;
	}
	
	/**
	 * Assign a code list location to an attribute
	 * 
	 * @param attributeIdentifier the attribute identifier
	 * @param codeListLocation the code list location or <code>null</code>
	 */
	public static void assignCodeList(String attributeIdentifier, URI codeListLocation) {
		IPreferenceStore preferences = CodeListUIPlugin.getDefault().getPreferenceStore();
		
		preferences.setValue(KEY_ATTRIBUTE_PREFIX + attributeIdentifier, (codeListLocation == null)?(""):(codeListLocation.toString())); //$NON-NLS-1$
	}
	
	/**
	 * Get the assigned code list location for an attribute
	 * 
	 * @param attributeIdentifier the attribute identifier
	 * 
	 * @return the code list location or <code>null</code> if none is assigned
	 */
	public static URI getAssignedCodeList(String attributeIdentifier) {
		IPreferenceStore preferences = CodeListUIPlugin.getDefault().getPreferenceStore();
		
		String location = preferences.getString(KEY_ATTRIBUTE_PREFIX + attributeIdentifier);
		if (location == null || location.isEmpty()) {
			return null;
		}
		else {
			return URI.create(location);
		}
	}

}
