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
package eu.esdihumboldt.hale.rcp.views.map;

import eu.esdihumboldt.hale.Messages;

/**
 * The split styles for displaying the map
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public enum SplitStyle {
	
	/** show only the reference data */
	SOURCE,
	/** show only the transformed data */
	TARGET,
	/** horizontal splitting */
	HORIZONTAL,
	/** vertical splitting */
	VERTICAL,
	/** diagonal splitting (up) */
	DIAGONAL_UP,
	/** diagonal splitting (down) */
	DIAGONAL_DOWN,
	/** overlay */
	OVERLAY;

	/**
	 * @see Enum#toString()
	 */
	@Override
	public String toString() {
		switch (this) {
		case SOURCE:
			return Messages.SplitStyle_ComboBoxText1;
		case TARGET:
			return Messages.SplitStyle_ComboBoxText2;
		case HORIZONTAL:
			return Messages.SplitStyle_ComboBoxText3;
		case VERTICAL:
			return Messages.SplitStyle_ComboBoxText4;
		case DIAGONAL_UP:
			return Messages.SplitStyle_ComboBoxText5;
		case DIAGONAL_DOWN:
			return Messages.SplitStyle_ComboBoxText6;
		case OVERLAY:
			return Messages.SplitStyle_ComboBoxText7;
		default:
			return super.toString();
		}
	}
}
