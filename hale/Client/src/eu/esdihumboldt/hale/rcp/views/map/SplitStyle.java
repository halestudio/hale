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
			return "Reference data only";
		case TARGET:
			return "Transformed data only";
		case HORIZONTAL:
			return "Split horizontally";
		case VERTICAL:
			return "Split vertically";
		case DIAGONAL_UP:
			return "Split diagonally (up)";
		case DIAGONAL_DOWN:
			return "Split diagonally (down)";
		case OVERLAY:
			return "Overlay";
		default:
			return super.toString();
		}
	}
}
