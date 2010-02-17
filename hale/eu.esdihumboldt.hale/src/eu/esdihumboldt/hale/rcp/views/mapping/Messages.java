package eu.esdihumboldt.hale.rcp.views.mapping;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.rcp.views.mapping.messages"; //$NON-NLS-1$
	public static String CellDetails_AugmentationTitle;
	public static String CellDetails_Entity1Title;
	public static String CellDetails_Entity2Title;
	public static String CellDetails_FilterTitle;
	public static String CellDetails_NameText;
	public static String CellDetails_TransformationTitle;
	public static String CellDetails_ValueText;
	public static String CellSelector_DeleteButtonToolTipText;
	public static String CellSelector_EditButtonToolTipText;
	public static String CellSelector_NextButtonToolTipText;
	public static String CellSelector_PrevButtonToolTipText;
	public static String CellSelector_SynchButtonToolTipText;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
