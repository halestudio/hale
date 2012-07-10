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

package eu.esdihumboldt.hale.ui.codelist.selector;

import java.net.URI;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.ui.codelist.internal.Messages;

/**
 * Allows selecting a file as source for a code list
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FileSelector implements CodeListSelector {
//	private static final ALogger log = ALoggerFactory.getLogger(FileSelector.class);
	
	private final Composite fileComposite;
	
	private final FileFieldEditor fileEditor;

	/**
	 * Constructor
	 * 
	 * @param parent the parent composite
	 */
	public FileSelector(Composite parent) {
		fileComposite = new Composite(parent, SWT.NONE);
		fileComposite.setLayout(new GridLayout(3, false));
		fileEditor = new FileFieldEditor("file", Messages.FileSelector_1, fileComposite); //$NON-NLS-1$ //$NON-NLS-2$
		fileEditor.setEmptyStringAllowed(false);
		fileEditor.setFileExtensions(new String[]{"*.xml"}); //$NON-NLS-1$
	}

	/**
	 * @see CodeListSelector#getCodeList()
	 */
	@Override
	public CodeList getCodeList() {
//		String fileName = fileEditor.getStringValue();
//		
//		URI location;
//		// try a URI
//		try {
//			location = new URI(fileName);
//		} catch (URISyntaxException e) {
//			// fall back to file
//			File file = new File(fileName);
//			location = file.toURI();
//		}
//		
//		try {
//			XmlCodeList codeList = new XmlCodeList(location.toURL().openStream(), location);
//			return codeList;
//		} catch (Exception e) {
//			log.error("Error loading code list"); //$NON-NLS-1$
//			return null;
//		}
		
		//FIXME update
		return null;
	}

	/**
	 * @see CodeListSelector#getControl()
	 */
	@Override
	public Control getControl() {
		return fileComposite;
	}

	/**
	 * Set the code list location
	 * 
	 * @param location the code list location
	 */
	public void setLocation(URI location) {
		fileEditor.setStringValue(location.toString());
	}

}
