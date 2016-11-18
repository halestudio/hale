package eu.esdihumboldt.hale.common.instance.geometry.curve;

/**
 * Constants related to curve geometry interpolation
 * 
 * @author Arun
 *
 */
public interface InterpolationConstant {

	/**
	 * Parameter name for the interpolation setting
	 */
	public static final String INTERPOL_MAX_POSITION_ERROR = "interpolation.maxerror";

	/**
	 * Default parameter value for the interpolation setting
	 */
	public static final double DEFAULT_INTERPOL_MAX_POSITION_ERROR = 0.1;

}
