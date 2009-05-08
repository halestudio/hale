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

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * This editor can be used to select a valid {@link URL} for a WFS to retrieve
 * schema information from. It delegates all details to the 
 * {@link WFSFeatureTypesReaderDialog}.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class UrlFieldEditor 
	extends StringButtonFieldEditor {
	
	private final static Logger _log = Logger.getLogger(UrlFieldEditor.class);
	
	public UrlFieldEditor(String name, String labelText,
            Composite parent) {
		super(name, labelText, parent);
	}

	/**
	 * @see org.eclipse.jface.preference.StringButtonFieldEditor#changePressed()
	 */
	@Override
	protected String changePressed() {
		// Open WFSFeatureTypesReaderDialog
		WFSFeatureTypesReaderDialog wfsftrd = new WFSFeatureTypesReaderDialog(
				this.getShell(), "Select a Web Feature Service");
		URL result = wfsftrd.open();
		if (result != null) {
			_log.debug("received result: " + result.toString());
			getTextControl().setText(result.toString());
			return getTextControl().getText();
		}
		else { // applicable if cancel is pressed.
			return "";
		}
	}

}
