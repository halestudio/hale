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
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.config.CodeListAssociations;
import eu.esdihumboldt.hale.common.codelist.config.CodeListReference;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.ComplexConfigurationService;
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
	 * Name of the property key in the project configuration holding the code
	 * list association configuration.
	 */
	private static final String KEY_ASSOCIATIONS = "codelists";

	/**
	 * The associated project service.
	 */
	protected final ProjectService projectService;

	/**
	 * The configuration service to use for storing/loading assignments.
	 */
	protected final IConfigurationService configurationService;

	/**
	 * The configuration service to use for storing/loading assignments.
	 */
	protected final ComplexConfigurationService complexConfigService;

	/**
	 * The code list association configuration.
	 */
	protected volatile CodeListAssociations associations = new CodeListAssociations();

	/**
	 * Constructs this code list service with the given project service. It will
	 * listen to cleans on the project service to clear all code lists. Also it
	 * will get/set code list assignments of the current project.
	 * 
	 * @param projectService the project service
	 */
	public CodeListServiceImpl(ProjectService projectService) {
		this.projectService = projectService;
		complexConfigService = projectService.getConfigurationService();
		configurationService = new NamespaceConfigurationServiceDecorator(
				projectService.getConfigurationService(), "codelist", ":");
		projectService.addListener(new ProjectServiceAdapter() {

			@Override
			public void onClean() {
				codelists.clear();
			}

			@Override
			public void afterLoad(ProjectService projectService) {
				// update associations from configuration
				CodeListAssociations projectAssociations = complexConfigService
						.getProperty(KEY_ASSOCIATIONS).as(CodeListAssociations.class);
				if (projectAssociations != null) {
					associations = projectAssociations;
				}
				else {
					associations = new CodeListAssociations();
				}
			}
		});
	}

	/**
	 * Maps code list identifiers to code lists.
	 */
	private final Map<CodeListReference, CodeList> codelists = new HashMap<>();

	/**
	 * Maps resource identifiers to code list identifiers.
	 */
	private final Map<String, CodeListReference> resourceAssociations = new HashMap<>();

	/**
	 * @see CodeListService#findCodeListByIdentifier(String, String)
	 */
	@Override
	public CodeList findCodeListByIdentifier(String namespace, String identifier) {
		CodeListReference key = new CodeListReference(namespace, identifier);
		return codelists.get(key);
	}

	@Override
	public CodeList findCodeList(CodeListReference clRef) {
		return codelists.get(clRef);
	}

	/**
	 * @see CodeListService#getCodeLists()
	 */
	@Override
	public List<CodeList> getCodeLists() {
		return new ArrayList<CodeList>(codelists.values());
	}

	@Override
	public CodeList findCodeListByEntity(EntityDefinition entity) {
		CodeList result = null;
		CodeListReference ref = associations.getCodeList(entity);
		if (ref != null) {
			result = findCodeListByIdentifier(ref.getNamespace(), ref.getIdentifier());
		}

		if (result == null) {
			// fall-back to legacy mechanism
			String ident = entity.getDefinition().getIdentifier();
			result = findCodeListByAttribute(ident);
		}

		return result;
	}

	@Override
	public void assignEntityCodeList(EntityDefinition entity, CodeList code) {
		associations.assignCodeList(entity, code);

		// update the project configuration
		complexConfigService.setProperty(KEY_ASSOCIATIONS, Value.complex(associations));
	}

//	public void assignAttributeCodeList(String attributeIdentifier, CodeList code) {
//		if (code == null)
//			configurationService.set(attributeIdentifier, null);
//		else
//			configurationService.set(attributeIdentifier,
//					code.getNamespace() + "/" + code.getIdentifier());
//	}

	/**
	 * Find a code list by attribute identifier.
	 * 
	 * @param attributeIdentifier the attribute identifier
	 * @return the code list or <code>null</code>
	 */
	public CodeList findCodeListByAttribute(String attributeIdentifier) {
		String key = configurationService.get(attributeIdentifier);
		if (key != null) {
			int index = key.lastIndexOf('/');
			if (index >= 0) {
				String namespace = key.substring(0, index);
				String identifier = key.substring(index + 1);
				CodeListReference ref = new CodeListReference(namespace, identifier);
				return codelists.get(ref);
			}
		}
		return null;
	}

	@Override
	public void addCodeList(String resourceId, CodeList code) {
		CodeListReference key = new CodeListReference(code.getNamespace(), code.getIdentifier());
		resourceAssociations.put(resourceId, key);
		// TODO deal with possible replacements?!
		codelists.put(key, code);
	}

	@Override
	public CodeList getCodeList(String resourceId) {
		CodeListReference ref = resourceAssociations.get(resourceId);
		if (ref != null) {
			return codelists.get(ref);
		}
		return null;
	}

	@Override
	public boolean removeCodeList(String resourceId) {
		CodeListReference ref = resourceAssociations.get(resourceId);
		if (ref != null) {
			if (codelists.remove(ref) != null) {
				resourceAssociations.remove(resourceId);
				return true;
			}
		}
		return false;
	}

}
