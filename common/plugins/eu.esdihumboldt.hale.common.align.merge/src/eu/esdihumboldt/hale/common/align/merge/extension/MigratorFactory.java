/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.align.merge.extension;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.align.merge.MergeCellMigrator;

/**
 * Factory interface for merge migrator extension.
 * 
 * @author Simon Templer
 */
public interface MigratorFactory extends ExtensionObjectFactory<MergeCellMigrator> {

	/**
	 * States if the function with the given identifier is supported to be
	 * migrated by the migrator.
	 * 
	 * @param functionId the function identifier
	 * @return <code>true</code> if the function is supported,
	 *         <code>false</code> otherwise
	 */
	boolean supportsFunction(String functionId);

}
