package eu.esdihumboldt.hale.rcp.actions;


/**
 * Interface defining the client's command IDs.
 * For association of an action with a command, use IAction.setActionDefinitionId(commandId).
 *
 * @author cjauss
 *
 */
public interface IClientCommandIDs {
	
	public static final String CMD_OPENFILE ="client.actions.openShapefile";
	
	public static final String CMD_OPENGML = "client.actions.openGML";
	
	public static final String CMD_OPENWFS ="client.actions.openWFS";
	
	public static final String CMD_OPENTARGETAPPSCHEMA = "client.actions.openTargetAppSchema";
	
	public static final String CMD_OPENTARGETAPPSCHEMAFROMREP = "client.actions.openTargetAppSchemaFromRep";
	
	public static final String CMD_OPENREFGML = "client.actions.openReferenceGML";
	
	public static final String CMD_LOADALIGNMENT = "client.actions.loadAlignment";
	
	public static final String CMD_STOREALIGNMENTLOCAL = "client.actions.storeAlignmentLocal";
	
	public static final String CMD_LOADTRANSFORMATIONRULELOCAL = "client.actions.loadTransformationRuleLocal";
	
	public static final String CMD_STORETRANSFORMATIONRULELOCAL = "client.actions.storeTransformationRuleLocal";
	
	public static final String CMD_STOREMAPPINGINMODELREP = "client.actions.storeMappingInModelRepository";
	
	public static final String CMD_STARTAUTOALIGNMENT = "client.actions.startAutoAlignment";
	
	public static final String CMD_STARTSOURCESCHEMAEXTRACTION = "client.actions.startSourceSchemaExtraction";
	
	public static final String CMD_CHANGESLD = "client.actions.changeSLD";
	
	public static final String CMD_EDITSLD = "client.actions.editSLD";
	
	public static final String CMD_PREFERENCES = "client.actions.preferences";
}