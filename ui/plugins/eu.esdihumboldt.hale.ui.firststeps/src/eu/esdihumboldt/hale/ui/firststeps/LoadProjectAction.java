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

package eu.esdihumboldt.hale.ui.firststeps;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * An IIntroAction and ICheatSheetAction to open a project. <br>
 * Parameters are <code>closeIntro</code> (or param1 in case of a cheatsheet),
 * <code>path</code> (param2), <code>type</code> (param3) and
 * <code>bundle</code> (param4). All are optional. <br>
 * <br>
 * If <code>closeIntro</code> is set to "true" an opened intro is closed after a
 * project was successfully opened. <br>
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
public class LoadProjectAction extends Action implements IIntroAction, ICheatSheetAction {
	public static final String TYPE_FILE = "file";
	public static final String TYPE_URI = "uri";
	public static final String TYPE_BUNDLE = "bundle";

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		execute(false, null, null, null);
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
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(ProjectService.class);
		ProjectInfo before = ps.getProjectInfo();
		// load a given file or show open project dialog
		if (path != null) {
			if (TYPE_FILE.equalsIgnoreCase(type) || type == null)
				ps.load(new File(path).toURI());
			else if (TYPE_URI.equalsIgnoreCase(type))
				ps.load(URI.create(path));
			else if (TYPE_BUNDLE.equalsIgnoreCase(type))
				try {
					ps.load(Platform.getBundle(bundle).getEntry(path).toURI());
				} catch (URISyntaxException e) {
					throw new IllegalArgumentException(e);
				}
		} else
			ps.open();

		// closeIntro if a project was loaded
		if (before != ps.getProjectInfo() && closeIntro) {
			IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
			if (introPart != null)
				PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
		}
	}
}
