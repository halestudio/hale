package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Action to start the source Schema extraction process.
 * @author cjauss
 *
 */
public class StartSourceSchemaExtractionAction extends Action{
	
	private final IWorkbenchWindow window;
	
	public StartSourceSchemaExtractionAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
		
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_STARTSOURCESCHEMAEXTRACTION);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_STARTSOURCESCHEMAEXTRACTION);
	
	}
	
	
	@Override
	public void run(){
		
	}
}
