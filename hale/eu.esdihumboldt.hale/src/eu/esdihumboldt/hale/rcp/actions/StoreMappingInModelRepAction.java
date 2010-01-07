package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Action for storing a mapping to the Model Repository.
 * @author cjauss
 *
 */
public class StoreMappingInModelRepAction extends Action{
	
	private final IWorkbenchWindow window;
	
	public StoreMappingInModelRepAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
		
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_STOREMAPPINGINMODELREP);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_STOREMAPPINGINMODELREP);
		
	}
	
	
	@Override
	public void run(){
		
	}
}
