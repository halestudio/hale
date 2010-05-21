package eu.esdihumboldt.hale.rcp.views.tasks;

import org.eclipse.osgi.util.NLS;

@SuppressWarnings("all")
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.rcp.views.tasks.messages"; //$NON-NLS-1$
	public static String TaskTreeView_CommentText;
	public static String TaskTreeView_description_tooltip;
	public static String TaskTreeView_NumberText;
	public static String TaskTreeView_SourceNodeTitle;
	public static String TaskTreeView_StatusText;
	public static String TaskTreeView_TargetNodeTitle;
	public static String TaskTreeView_TitleDescriptionText;
	public static String TaskTreeView_value_tooltip;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
