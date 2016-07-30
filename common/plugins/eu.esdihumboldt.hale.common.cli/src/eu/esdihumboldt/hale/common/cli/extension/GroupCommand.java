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

package eu.esdihumboldt.hale.common.cli.extension;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.esdihumboldt.hale.common.cli.Command;
import eu.esdihumboldt.hale.common.cli.extension.command.CommandExtension;
import eu.esdihumboldt.hale.common.cli.extension.command.CommandFactory;
import eu.esdihumboldt.hale.common.cli.extension.group.Group;
import eu.esdihumboldt.hale.common.cli.extension.group.GroupExtension;
import eu.esdihumboldt.hale.common.cli.impl.DelegatingCommand;

/**
 * Delegating command based on a registered command group.
 * 
 * @author Simon Templer
 */
public class GroupCommand extends DelegatingCommand {

	private static final Logger log = LoggerFactory.getLogger(GroupCommand.class);

	private final String id;
	private final Group group;

	private Map<String, Command> commands;

	/**
	 * Create a group command for the group with the given ID.
	 * 
	 * @param id the group ID
	 */
	public GroupCommand(String id) {
		super();
		this.id = id;

		if (id != null) {
			group = GroupExtension.getInstance().get(id);
		}
		else {
			group = null;
		}
	}

	/**
	 * Create the root command.
	 */
	public GroupCommand() {
		this(null);
	}

	@Override
	public String getShortDescription() {
		if (group != null) {
			group.getDescription();
		}
		return null;
	}

	@Override
	public Map<String, Command> getSubCommands() {
		init();

		return commands;
	}

	private void init() {
		if (this.commands != null) {
			return;
		}

		Map<String, Command> commands = new HashMap<>();

		// sub-groups
		for (Group candidate : GroupExtension.getInstance().getElements()) {
			if (Objects.equals(id, candidate.getParent())) {
				if (commands.put(candidate.getName(),
						new GroupCommand(candidate.getId())) != null) {
					log.error("Duplicate command " + candidate.getName());
				}
			}
		}

		// group commands
		for (CommandFactory factory : CommandExtension.getInstance().getFactories()) {
			if (Objects.equals(id, factory.getGroup())) {
				try {
					Command command = factory.createExtensionObject();
					if (commands.put(factory.getDisplayName(), command) != null) {
						log.error("Duplicate command " + factory.getDisplayName());
					}
				} catch (Exception e) {
					log.error("Could not create command", e);
				}

			}
		}

		this.commands = commands;
	}

}
