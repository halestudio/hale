package eu.esdihumboldt.hale.rcp.views.table;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.rcp.views.table.messages"; //$NON-NLS-1$
	public static String InstanceServiceFeatureSelector_defaultReturnText;
	public static String InstanceServiceFeatureSelector_SourceReturnText;
	public static String InstanceServiceFeatureSelector_TargetReturnText;
	public static String TransformedTableView_SynchToolTipText;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
