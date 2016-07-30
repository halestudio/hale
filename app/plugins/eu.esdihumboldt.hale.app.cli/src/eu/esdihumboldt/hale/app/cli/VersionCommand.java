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

package eu.esdihumboldt.hale.app.cli;

import java.util.List;

import eu.esdihumboldt.hale.common.cli.Command;
import eu.esdihumboldt.hale.common.cli.CommandContext;
import eu.esdihumboldt.hale.common.core.HalePlatform;

/**
 * Command that prints the hale version.
 * 
 * @author Simon Templer
 */
public class VersionCommand implements Command {

	@Override
	public int run(List<String> args, CommandContext context) {
		System.out.println(HalePlatform.getCoreVersion());
		return 0;
	}

	@Override
	public String getShortDescription() {
		return "Print the hale version";
	}

}
