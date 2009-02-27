package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;


/**
 * Action for loading geodata from a local GML file.
 * @author cjauss
 *
 */
public class LoadGMLFromFileAction extends Action{
	
	private final IWorkbenchWindow window;
	
	public LoadGMLFromFileAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
	
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_OPENGML);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_OPENGML);
	}
	
	@Override
	public void run(){
		
	}

}
