package eu.esdihumboldt.hale.common.instance.geometry.curve;

/**
 * Constants related to curve geometry interpolation
 * 
 * @author Arun
 *
 */
public interface InterpolationConstant {

	/**
	 * Parameter name for all geometries' coordinates move to grid
	 */
	public static final String INTERPOL_GEOMETRY_MOVE_ALL_TO_GRID = "interpolation.geometries.moveToGrid";

	/**
	 * Parameter name for the interpolation setting
	 */
	public static final String INTERPOL_MAX_POSITION_ERROR = "interpolation.maxError";

	/**
	 * Default parameter value for the interpolation setting
	 */
	public static final double DEFAULT_INTERPOL_MAX_POSITION_ERROR = 0.1;

	/**
	 * Default parameter value for the all geometry on grid parameter
	 */
	public static final boolean DEFAULT_INTERPOL_GEOMETRY_MOVE_ALL_TO_GRID = false;
}
