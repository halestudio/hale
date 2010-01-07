package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Action to start the automatic alignment process.
 * @author cjauss
 *
 */
public class StartAutoAlignmentAction extends Action{
	
	private final IWorkbenchWindow window;
	
	public StartAutoAlignmentAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
	
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_STARTAUTOALIGNMENT);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_STARTAUTOALIGNMENT);
	
	}
	
	
	@Override
	public void run(){
		
	}
}
