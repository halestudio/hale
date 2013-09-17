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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.projects.internal;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.Version;

import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.server.projects.ProjectScavenger;
import eu.esdihumboldt.util.Identifiers;

/**
 * Project related commands backed by the {@link ProjectScavenger} service.
 * 
 * @author Simon Templer
 */
public class ProjectCommands implements CommandProvider {

	private final Identifiers<String> identifiers = new Identifiers<String>("", true);

	private ProjectScavenger projects;

	/**
	 * @param projects the projects to set
	 */
	public void setProjects(ProjectScavenger projects) {
		this.projects = projects;
	}

	/**
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuilder builder = new StringBuilder();

		builder.append("---Administration of HALE projects---\r\n");
		builder.append("\tplist - list all available projects\n");
		builder.append("\tpinfo (<Id>|<Project>) - show detailed information on a project\n");
		builder.append("\tpstart (<Id>|<Project>) - activate the specified project\n");
		builder.append("\tpstop (<Id>|<Project>) - deactivate the specified project\n");
		builder.append("\tpupdate - update the projects from the configured project location\n");

		return builder.toString();
	}

	/**
	 * @param ci the command interpreter where this command runs
	 */
	public synchronized void _plist(CommandInterpreter ci) {
		if (projects != null) {
			if (projects.getResources().isEmpty()) {
				ci.println(" No projects available");
			}
			else {
				ci.println(" Id\tState\t\tProject");
				for (String project : projects.getResources()) {
					StringBuilder builder = new StringBuilder();

					builder.append(' ');
					builder.append(identifiers.getId(project));
					builder.append('\t');
					builder.append(projects.getStatus(project));
					builder.append('\t');
					builder.append(project);

					ci.println(builder.toString());
				}
			}
		}
	}

	/**
	 * @param ci the command interpreter where this command runs
	 */
	public synchronized void _pinfo(CommandInterpreter ci) {
		if (projects != null) {
			String project = getProjectArg(ci);
			if (project == null) {
				return;
			}

			ci.println("Project:");
			ci.println("\t" + project);

			ci.println("State:");
			ci.println("\t" + projects.getStatus(project));

			ProjectInfo info = projects.getInfo(project);
			if (info != null) {
				String name = info.getName();
				if (name != null && !name.isEmpty()) {
					ci.println("Name:");
					ci.println("\t" + name);
				}

				String author = info.getAuthor();
				if (author != null && !author.isEmpty()) {
					ci.println("Author:");
					ci.println("\t" + author);
				}

				String description = info.getDescription();
				if (description != null && !description.isEmpty()) {
					ci.println("Description:");
					ci.println("\t" + description);
				}

				Date modified = info.getModified();
				if (modified != null) {
					ci.println("Last modified:");
					ci.println("\t" + DateFormat.getDateTimeInstance().format(modified));
				}

				Date created = info.getCreated();
				if (created != null) {
					ci.println("Created:");
					ci.println("\t" + DateFormat.getDateTimeInstance().format(created));
				}

				Version version = info.getHaleVersion();
				if (version != null) {
					ci.println("HALE version:");
					ci.println("\t" + version);
				}
			}
		}
	}

	/**
	 * @param ci the command interpreter where this command runs
	 */
	public synchronized void _pstart(CommandInterpreter ci) {
		if (projects != null) {
			String project = getProjectArg(ci);
			if (project == null) {
				return;
			}

			projects.activate(project);
			ci.execute("plist");
		}
	}

	/**
	 * @param ci the command interpreter where this command runs
	 */
	public synchronized void _pstop(CommandInterpreter ci) {
		if (projects != null) {
			String project = getProjectArg(ci);
			if (project == null) {
				return;
			}

			projects.deactivate(project);
			ci.execute("plist");
		}
	}

	private String getProjectArg(CommandInterpreter ci) {
		StringBuilder args = new StringBuilder();
		boolean first = true;
		String arg;
		while ((arg = ci.nextArgument()) != null) {
			if (first) {
				first = false;
			}
			else {
				args.append(' ');
			}
			args.append(arg);
		}

		String project = args.toString();
		if (!projects.getResources().contains(project)) {
			// assume it's an ID
			return identifiers.getObject(project);
		}
		if (!projects.getResources().contains(project)) {
			ci.println("Project does not exist");
			return null;
		}
		return project;
	}

	/**
	 * @param ci the command interpreter where this command runs
	 */
	public synchronized void _pupdate(CommandInterpreter ci) {
		if (projects != null) {
			projects.triggerScan();
			ci.println("Update complete, current projects:");
			ci.execute("plist");
		}
	}

}
