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
import org.eclipse.swt.widgets.Dialog;

/**
 * This editor can be used to select a valid {@link URL} for a WFS to retrieve
 * schema information from. It delegates all details to the 
 * {@link WFSFeatureTypesReaderDialog}.
 * 
 * @author Thorsten Reitz 
 * @author Jan Kolar
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 02 / Intergraph CS
 * @version $Id$ 
 */
public class UrlFieldEditor 
	extends StringButtonFieldEditor {
	
	private final static Logger _log = Logger.getLogger(UrlFieldEditor.class);
	private boolean _getFeatures = false;
	
	public UrlFieldEditor(String name, String labelText,
            Composite parent) {
		super(name, labelText, parent);
	}
	
	public UrlFieldEditor(String name, String labelText,Composite parent,boolean getFeatures) {
		super(name, labelText, parent);
		this._getFeatures = getFeatures;
	}

	/**
	 * @see org.eclipse.jface.preference.StringButtonFieldEditor#changePressed()
	 */
	@Override
	protected String changePressed() {
		URL result = null;
		
		if (!this._getFeatures) {
			WFSFeatureTypesReaderDialog wfsDialog = new WFSFeatureTypesReaderDialog(this.getShell(), Messages.UrlFieldEditor_FeatureServiceTitle);
			result = wfsDialog.open();
		}
		else {
			WFSDataReaderDialog wfsDialog = new WFSDataReaderDialog(this.getShell(), Messages.UrlFieldEditor_DataReaderDialogTitle);
			result = wfsDialog.open();
		}

		if (result != null) {
			_log.debug("received result: " + result.toString()); //$NON-NLS-1$
			getTextControl().setText(result.toString());
			return getTextControl().getText();
		}
		else { // applicable if cancel is pressed.
			return ""; //$NON-NLS-1$
		}
	}

}
