package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Action for local storage of an alignment.
 * @author cjauss
 *
 */
public class StoreAlignmentsLocalAction extends Action{
	
	private final IWorkbenchWindow window;
	
	public StoreAlignmentsLocalAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
		
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_STOREALIGNMENTLOCAL);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_STOREALIGNMENTLOCAL);
		
	}
	
	
	@Override
	public void run(){
		
	}
}
