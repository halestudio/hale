/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.model;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Text;

/**
 * This {@link ViewerFilter} allows the user to type in any search string to 
 * filter the items shown in the Schema Explorer.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class PatternViewFilter 
	extends ViewerFilter {
	
	private Text text = null;
	private String namespace = "http://www.openplans.org/topp";
	
	public PatternViewFilter(Text text) {
		this.text = text;
	}
	
	/**
	 * updates the namespace root node to be protected by this filter.
	 */
	public void setNameSpace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (text == null || text.getText() == null || text.getText().equals("")) {
			return true;
		}
		
		if (element instanceof TreeObject || element instanceof TreeParent) {
			TreeObject to = (TreeObject) element;
			// the namespace node always remains visible.
			if (to.getName().matches(namespace)) {
				return true;
			}
			// other nodes are filtered.
			// TODO find a way of retaining nodes even when they are under a filtered node.
			if (to.getName().matches("^.*?" + text.getText() + ".*?")) {
				return true;
			}
		}
		return false;
	}

}
