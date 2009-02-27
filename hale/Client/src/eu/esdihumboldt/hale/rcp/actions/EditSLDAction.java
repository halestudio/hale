package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Action to start a SLD Editor
 * @author cjauss
 *
 */
public class EditSLDAction extends Action{
	
	private final IWorkbenchWindow window;
	
	public EditSLDAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_EDITSLD);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_EDITSLD);
	}
	
	
	@Override
	public void run(){
		
	}
}