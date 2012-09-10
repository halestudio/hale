/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

/**
 * 
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface WfsPreferenceConstants {

	/**
	 * The maximum number of recent WFS
	 */
	public final int MAX_RECENT_WFS = 10;

	/**
	 * The name of the recent WFS key prefix
	 */
	public final String KEY_RECENT_WFS_PREFIX = "wfs.recent.url"; //$NON-NLS-1$

	/**
	 * The name of the recent WFS count key
	 */
	public final String KEY_RECENT_WFS_COUNT = "wfs.recent.count"; //$NON-NLS-1$

}
