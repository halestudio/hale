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

package eu.esdihumboldt.hale.ui.codelist.service;

import java.util.List;

import eu.esdihumboldt.hale.common.codelist.CodeList;

/**
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface CodeListService {

	/**
	 * Tries to find the code list with the given namespace and identifier.
	 * 
	 * @param namespace the namespace
	 * @param identifier the identifier
	 * @return the code list or <code>null</code>
	 */
	public CodeList findCodeListByIdentifier(String namespace, String identifier);

	/**
	 * Tries to find a code list associated to the attribute referenced by the
	 * given identifier.
	 * 
	 * @param attributeIdentifier the attribute identifier
	 * @return the code list or <code>null</code>
	 */
	public CodeList findCodeListByAttribute(String attributeIdentifier);

	/**
	 * Assign a code list for an attribute.
	 * 
	 * @param attributeIdentifier the attribute identifier
	 * @param code the code list to assign or <code>null</code> if the
	 *            assignment shall be deleted
	 */
	public void assignAttributeCodeList(String attributeIdentifier, CodeList code);

//	
//	/**
//	 * Inform the service that the search path has changed
//	 */
//	public void searchPathChanged();

	/**
	 * Get the code lists.
	 * 
	 * @return the code lists
	 */
	public List<CodeList> getCodeLists();

	/**
	 * Add a code list to the service.
	 * 
	 * @param code the code list to add
	 */
	public void addCodeList(CodeList code);

}
