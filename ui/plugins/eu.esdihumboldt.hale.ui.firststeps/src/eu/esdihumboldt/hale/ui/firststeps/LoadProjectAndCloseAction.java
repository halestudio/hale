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

import java.util.Properties;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * An IIntroAction to open the load project dialog and close the intro part at
 * the same time.
 * 
 * @author Kai Schwierczek
 */
public class LoadProjectAndCloseAction implements IIntroAction {

	@Override
	public void run(IIntroSite site, Properties params) {
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(ProjectService.class);
		ps.open();
		IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
		PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
	}

}
