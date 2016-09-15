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

package eu.esdihumboldt.hale.ui.firststeps;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.eclipse.help.ILiveHelpAction;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * An IIntroAction, ILiveHelpAction and ICheatSheetAction to open a project.
 * <br>
 * Parameters are <code>closeIntro</code> (or param1 in case of a
 * cheatsheet/live help), <code>path</code> (param2), <code>type</code> (param3)
 * and <code>bundle</code> (param4). All are optional. <br>
 * Parameters for live help are to be separated by "||".<br>
 * <br>
 * If <code>closeIntro</code> is set to "true" an opened intro is closed when
 * the action is run.<br>
 * <br>
 * If <code>path</code> is specified the given path gets opened instead of
 * showing a dialog to choose the project. <br>
 * By default the path will be interpreted as a local file path. <br>
 * <br>
 * <code>type</code> can change this. Valid types are "file", "uri" and
 * "bundle". "uri" will interpret the path as a URI, nothing happens if the
 * given path is not a correct URI. <br>
 * "bundle" will interpret the path relative to the given Bundle (
 * <code>bundle</code> parameter).
 * 
 * @author Kai Schwierczek
 */
public class LoadProjectAction extends Action
		implements IIntroAction, ICheatSheetAction, ILiveHelpAction {

	/**
	 * Value for type parameter, specifying that the path is a file path.
	 */
	public static final String TYPE_FILE = "file";
	/**
	 * Value for type parameter, specifying that the path is an uri.
	 */
	public static final String TYPE_URI = "uri";
	/**
	 * Value for type parameter, specifying that the path is a bundle internal
	 * path.
	 */
	public static final String TYPE_BUNDLE = "bundle";

	private boolean closeIntro;
	private String path;
	private String type;
	private String bundle;

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if (Display.getCurrent() == null) {
			// execute in display thread
			PlatformUI.getWorkbench().getDisplay().asyncExec(this);
			return;
		}

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().forceActive();

		// close intro if specified and visible
		if (closeIntro) {
			IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
			if (introPart != null)
				PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
		}

		// executes event with last configuration
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		// load a given file or show open project dialog
		if (path != null) {
			if (TYPE_FILE.equalsIgnoreCase(type) || type == null)
				ps.load(new File(path).toURI());
			else if (TYPE_URI.equalsIgnoreCase(type))
				ps.load(URI.create(path));
			else if (TYPE_BUNDLE.equalsIgnoreCase(type))
				try {
//					ps.load(Platform.getBundle(bundle).getEntry(path).toURI());
					StringBuilder b = new StringBuilder();
					b.append("platform:/plugin/");
					b.append(bundle);
					if (path.length() > 0 && path.charAt(0) != '/') {
						b.append("/");
					}
					b.append(path);
					ps.load(new URI(b.toString()));
				} catch (URISyntaxException e) {
					throw new IllegalArgumentException(e);
				}
		}
		else
			ps.open();
	}

	/**
	 * @see org.eclipse.ui.intro.config.IIntroAction#run(org.eclipse.ui.intro.IIntroSite,
	 *      java.util.Properties)
	 */
	@Override
	public void run(IIntroSite site, Properties params) {
		boolean closeIntro = false;
		if ("true".equals(params.getProperty("closeIntro")))
			closeIntro = true;
		String path = params.getProperty("path");
		String type = params.getProperty("type", TYPE_FILE);
		String bundle = params.getProperty("bundle");
		execute(closeIntro, path, type, bundle);
	}

	/**
	 * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
	 *      org.eclipse.ui.cheatsheets.ICheatSheetManager)
	 */
	@Override
	public void run(String[] params, ICheatSheetManager manager) {
		boolean closeIntro = false;
		if (params.length > 0)
			closeIntro = "true".equals(params[0]);
		String path = null;
		if (params.length > 1)
			path = params[1];
		String type = TYPE_FILE;
		if (params.length > 2)
			type = params[2] == null ? type : params[2];
		String bundle = null;
		if (params.length > 3)
			bundle = params[3];
		execute(closeIntro, path, type, bundle);
	}

	/**
	 * @see org.eclipse.help.ILiveHelpAction#setInitializationString(java.lang.String)
	 */
	@Override
	public void setInitializationString(String data) {
		String[] params = data.split("\\|\\|");
		closeIntro = false;
		if (params.length > 0)
			closeIntro = "true".equals(params[0]);
		path = null;
		if (params.length > 1)
			path = params[1];
		type = TYPE_FILE;
		if (params.length > 2)
			type = params[2] == null ? type : params[2];
		bundle = null;
		if (params.length > 3)
			bundle = params[3];
	}

	/**
	 * Executes the action.
	 * 
	 * @param closeIntro whether to close the intro if a project was
	 *            opened/loaded or not
	 */
	public void execute(boolean closeIntro) {
		execute(closeIntro, null, null, null);
	}

	/**
	 * Executes the action.
	 * 
	 * @param closeIntro whether to close the intro if a project was
	 *            opened/loaded or not
	 * @param path the file to load, a dialog is shown if fileName is null
	 */
	public void execute(boolean closeIntro, String path) {
		execute(closeIntro, path, null, null);
	}

	/**
	 * Executes the action.
	 * 
	 * @param closeIntro whether to close the intro if a project was
	 *            opened/loaded or not
	 * @param path the file to load, a dialog is shown if fileName is null
	 * @param type the type of the path
	 */
	public void execute(boolean closeIntro, String path, String type) {
		execute(closeIntro, path, type, null);
	}

	/**
	 * Executes the action.
	 * 
	 * @param closeIntro whether to close the intro if a project was
	 *            opened/loaded or not
	 * @param path the file to load, a dialog is shown if fileName is null
	 * @param type the type of the path
	 * @param bundle the bundle in which to locate the path if it is a bundle
	 *            type path
	 */
	public void execute(boolean closeIntro, String path, String type, String bundle) {
		this.closeIntro = closeIntro;
		this.path = path;
		this.type = type;
		this.bundle = bundle;
		run();
	}
}
