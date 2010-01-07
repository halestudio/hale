package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Action for loading a target schema from the Model Repository.
 * @author cjauss
 *
 */
public class LoadTargetAppSchemaFromRepAction extends Action{
	
private final IWorkbenchWindow window;
	
	public LoadTargetAppSchemaFromRepAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_OPENTARGETAPPSCHEMAFROMREP);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_OPENTARGETAPPSCHEMAFROMREP);
	}
	
	
	@Override
	public void run(){
		
	}
}
