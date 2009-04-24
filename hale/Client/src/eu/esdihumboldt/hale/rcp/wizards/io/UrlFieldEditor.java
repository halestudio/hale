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
package eu.esdihumboldt.hale.rcp.wizards.io;

import java.net.URL;

import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class UrlFieldEditor 
	extends StringButtonFieldEditor {
	
	public UrlFieldEditor(String name, String labelText,
            Composite parent) {
		super(name, labelText, parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.StringButtonFieldEditor#changePressed()
	 */
	@Override
	protected String changePressed() {
		// Open WFSFeatureTypesReaderDialog
		WFSFeatureTypesReaderDialog wfsftrd = new WFSFeatureTypesReaderDialog(
				this.getShell(), "Select a Web Feature Service");
		URL result = wfsftrd.open();
		getTextControl().setData(result);
		return getTextControl().getText();
	}

}
