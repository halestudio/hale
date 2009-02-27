package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Action to start the preferences editor.
 * @author cjauss
 *
 */
public class PreferencesAction extends Action{
	
	private final IWorkbenchWindow window;
	
	public PreferencesAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
	
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_PREFERENCES);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_PREFERENCES);
	}
	
	
	@Override
	public void run(){
		
	}
}
