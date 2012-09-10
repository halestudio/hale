/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.internal;

import de.cs3d.ui.util.eclipse.extension.exclusive.PreferencesExclusiveExtension;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterLayoutFactory;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterLayoutService;
import eu.esdihumboldt.hale.ui.views.styledmap.internal.StyledMapBundle;
import eu.esdihumboldt.hale.ui.views.styledmap.preferences.StyledMapPreferenceConstants;

/**
 * Simon Templer
 * 
 * @author Simon Templer
 */
public class PainterLayoutManager extends
		PreferencesExclusiveExtension<PainterLayout, PainterLayoutFactory> implements
		PainterLayoutService {

	/**
	 * Default constructor.
	 */
	public PainterLayoutManager() {
		super(new PainterLayoutExtension(), StyledMapBundle.getDefault().getPreferenceStore(),
				StyledMapPreferenceConstants.CURRENT_MAP_LAYOUT);
	}

	/**
	 * @see PreferencesExclusiveExtension#getFallbackFactory()
	 */
	@Override
	protected PainterLayoutFactory getFallbackFactory() {
		return new DefaultFactory();
	}

}
