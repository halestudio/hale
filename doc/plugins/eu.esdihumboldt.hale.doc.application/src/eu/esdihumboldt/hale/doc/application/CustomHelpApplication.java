/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.doc.application;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.help.internal.base.HelpApplication;

/**
 * Extends the {@link HelpApplication} to deal with the issue that starting the
 * application fails because the platform location does not exits.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class CustomHelpApplication extends HelpApplication {

	/**
	 * @see HelpApplication#start(IApplicationContext)
	 */
	@Override
	public synchronized Object start(IApplicationContext context) throws Exception {
		// ensure that the directory where the .connection file is placed is
		// created
		File metadata = new File(Platform.getLocation().toFile(), ".metadata/"); //$NON-NLS-1$
		metadata.mkdirs();

		return super.start(context);
	}

}
