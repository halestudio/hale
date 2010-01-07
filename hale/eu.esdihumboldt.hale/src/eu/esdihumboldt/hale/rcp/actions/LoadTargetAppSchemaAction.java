package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Action to load a target application Schema.
 * @author cjauss
 *
 */
public class LoadTargetAppSchemaAction extends Action{
	
private final IWorkbenchWindow window;
	
	public LoadTargetAppSchemaAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
		
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_OPENTARGETAPPSCHEMA);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_OPENTARGETAPPSCHEMA);
	}
	
	
	@Override
	public void run(){
		
	}

}
