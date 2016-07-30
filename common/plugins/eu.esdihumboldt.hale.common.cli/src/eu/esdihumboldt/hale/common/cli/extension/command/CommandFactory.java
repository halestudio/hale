/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.common.cli.extension.command;

import javax.annotation.Nullable;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.cli.Command;

/**
 * Command factory.
 * 
 * @author Simon Templer
 */
public interface CommandFactory extends ExtensionObjectFactory<Command> {

	/**
	 * Get the identifier of the command group the command is associated to.
	 * 
	 * @return the command group identifier
	 */
	@Nullable
	String getGroup();

}
