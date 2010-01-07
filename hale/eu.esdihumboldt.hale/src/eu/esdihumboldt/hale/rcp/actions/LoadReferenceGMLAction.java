package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;


/**
 * Action to load target geodata from GML.
 * @author cjauss
 *
 */
public class LoadReferenceGMLAction extends Action{
	
	private final IWorkbenchWindow window;
	
	
	public LoadReferenceGMLAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
		
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_OPENREFGML);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_OPENREFGML);
		
	}
	
	
	@Override
	public void run(){
		
	}
}
