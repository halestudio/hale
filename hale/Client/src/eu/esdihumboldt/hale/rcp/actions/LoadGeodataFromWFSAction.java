package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;






/**
 * Action to load user data from a WFS.
 * @author cjauss
 *
 */
public class LoadGeodataFromWFSAction extends Action{
	
	
	public LoadGeodataFromWFSAction(String _name, IWorkbenchWindow _window){
		
		super(_name);
		// The id is used to refer to the action in the menu
		setId(IClientCommandIDs.CMD_OPENWFS);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_OPENWFS);
	}
	
	@Override
	public void run(){
		
	}
}
