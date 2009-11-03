/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.commandHandlers;

import java.util.Calendar;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;

/**
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 *
 */
public class NewProjectHandler extends AbstractHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (MessageDialog.openQuestion(HandlerUtil.getActiveShell(event), 
				"New Alignment Project", "This will reset the Alignment Project, unsaved changes will be lost. Do you want to continue?")) {
			// clean alignment service
			AlignmentService as = (AlignmentService) 
					PlatformUI.getWorkbench().getService(AlignmentService.class);
			as.cleanModel();
			
			// clean instance service
			InstanceService is = (InstanceService) 
					PlatformUI.getWorkbench().getService(InstanceService.class);
			is.cleanInstances();
			
			// clean schema service
			SchemaService ss = (SchemaService) 
					PlatformUI.getWorkbench().getService(SchemaService.class);
			ss.cleanSourceSchema();
			ss.cleanTargetSchema();
			
			// clean the project Service
			ProjectService ps = (ProjectService) 
					PlatformUI.getWorkbench().getService(ProjectService.class);
			ps.setInstanceDataPath(null);
			ps.setProjectCreatedDate(Calendar.getInstance().getTime().toString());
			ps.setSourceSchemaPath(null);
			ps.setTargetSchemaPath(null);
			
			System.gc();
		}
		
		return null;
	}

}
