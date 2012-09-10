package eu.esdihumboldt.specification.mediator.constraints.portrayal;

public interface FeatureTypeStyle {
	/**
	 * A FeatureTypeStyle contains styling information specific to one feature
	 * type. This is the SLD level that separates the 'layer' handling from the
	 * 'feature' handling.
	 */

	public String getName();

	public String getTitle();

	public String getAbstract();

	public String getFeatureTypeName();

	public String getSemanticTypeIdentifier(); // TODO: return correct type

	public String getRule(); // TODO: return correct type

}
