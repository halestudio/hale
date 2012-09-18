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
