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

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.CodeList.CodeEntry;
import eu.esdihumboldt.hale.ui.util.tree.DefaultTreeNode;

/**
 * Tree node representing a code list
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CodeListNode extends DefaultTreeNode {

	/**
	 * Create a new tree node with a given code list
	 * 
	 * @param codes the code list
	 */
	public CodeListNode(CodeList codes) {
		super(codes.getIdentifier());
		
		for (CodeEntry entry : codes.getEntries()) {
			addChild(new DefaultTreeNode(entry.getName()));
		}
	}

}
