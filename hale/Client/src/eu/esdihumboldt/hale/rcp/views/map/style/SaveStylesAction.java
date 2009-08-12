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
package eu.esdihumboldt.hale.rcp.views.map.style;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.geotools.styling.SLDTransformer;

import eu.esdihumboldt.hale.models.StyleService;

/**
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 *
 */
public class SaveStylesAction extends Action {
	
private static final Log log = LogFactory.getLog(SaveStylesAction.class);
	
	/**
	 * Creates an action that loads styles from a SLD file
	 */
	public SaveStylesAction() {
		super("Save to SLD file...", AS_PUSH_BUTTON);
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		StyleService styles = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		
		if (styles != null) {
			FileDialog files = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
			
			String[] extensions = new String[2]; 
			extensions[0]= "*.sld";
			extensions[1]= "*.xml";
			files.setFilterExtensions(extensions);
			
			String filename = files.open();
			File file = new File(filename);
			
			SLDTransformer trans = new SLDTransformer();
			trans.setIndentation(2);
			
			try {
				FileWriter writer = new FileWriter(file);
				trans.transform(styles.getStyle(), writer);
				writer.close();
			} catch (Exception e) {
				log.error("Error saving SLD file", e);
			}
		}
	}

}
