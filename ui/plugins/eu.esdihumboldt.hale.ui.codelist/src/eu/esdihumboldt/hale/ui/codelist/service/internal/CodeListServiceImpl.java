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

package eu.esdihumboldt.hale.ui.codelist.service.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import de.fhg.igd.osgi.util.configuration.NamespaceConfigurationServiceDecorator;
import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.ui.codelist.service.CodeListService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;

/**
 * Code list service.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CodeListServiceImpl implements CodeListService {

	/**
	 * The associated project service.
	 */
	protected final ProjectService projectService;
	/**
	 * The configuration service to use for storing/loading assignments.
	 */
	protected final IConfigurationService configurationService;

	/**
	 * Constructs this code list service with the given project service. It will
	 * listen to cleans on the project service to clear all code lists. Also it
	 * will get/set code list assignments of the current project.
	 * 
	 * @param projectService the project service
	 */
	public CodeListServiceImpl(ProjectService projectService) {
		this.projectService = projectService;
		configurationService = new NamespaceConfigurationServiceDecorator(
				projectService.getConfigurationService(), "codelist", ":");
		projectService.addListener(new ProjectServiceAdapter() {

			@Override
			public void onClean() {
				codelists.clear();
			}
		});
	}

	/**
	 * Maps code list identifiers to code lists.
	 */
	private final Map<String, CodeList> codelists = new HashMap<String, CodeList>();

	/**
	 * @see CodeListService#findCodeListByIdentifier(String, String)
	 */
	@Override
	public CodeList findCodeListByIdentifier(String namespace, String identifier) {
		String key = namespace + "/" + identifier; //$NON-NLS-1$
		return codelists.get(key);
	}

	/**
	 * @see CodeListService#getCodeLists()
	 */
	@Override
	public List<CodeList> getCodeLists() {
		return new ArrayList<CodeList>(codelists.values());
	}

	/**
	 * @see CodeListService#assignAttributeCodeList(String, CodeList)
	 */
	@Override
	public void assignAttributeCodeList(String attributeIdentifier, CodeList code) {
		if (code == null)
			configurationService.set(attributeIdentifier, null);
		else
			configurationService.set(attributeIdentifier,
					code.getNamespace() + "/" + code.getIdentifier());
	}

	/**
	 * @see CodeListService#findCodeListByAttribute(String)
	 */
	@Override
	public CodeList findCodeListByAttribute(String attributeIdentifier) {
		String key = configurationService.get(attributeIdentifier);
		if (key == null)
			return null;
		else
			return codelists.get(key);
	}

	/**
	 * @see CodeListService#addCodeList(CodeList)
	 */
	@Override
	public void addCodeList(CodeList code) {
		String key = code.getNamespace() + "/" + code.getIdentifier(); //$NON-NLS-1$
		codelists.put(key, code);
	}

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
}
