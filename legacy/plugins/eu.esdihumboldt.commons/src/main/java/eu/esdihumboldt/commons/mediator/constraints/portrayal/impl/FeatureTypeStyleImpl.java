package eu.esdihumboldt.commons.mediator.constraints.portrayal.impl;

import java.io.Serializable;

import eu.esdihumboldt.specification.mediator.constraints.Constraint.ConstraintSource;
import eu.esdihumboldt.specification.mediator.constraints.SpatialConstraint;
import eu.esdihumboldt.specification.mediator.constraints.portrayal.FeatureTypeStyle;

public class FeatureTypeStyleImpl implements FeatureTypeStyle, Serializable {
	/**
	 * A FeatureTypeStyle contains styling information specific to one feature
	 * type. This is the SLD level that separates the 'layer' handling from the
	 * 'feature' handling.
	 */

	private String name;
	private String title;
	private String abstractFTS;
	private String featureTypeName;
	private String semanticTypeIdentifier; // TODO: return correct type
	private String rule; // TODO: return correct type

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The unique identifier of the constraint int the database
	 */

	private long id;

	/**
	 * The status of this constraint.
	 */
	private boolean satisfied = false;

	/**
	 * The unique identifier in the current VM.
	 */
	private long uid;

	/**
	 * the {@link ConstraintSource} of this {@link SpatialConstraint}.
	 */
	private ConstraintSource constraintSource;

	/**
	 * @param constraintSource
	 *            the constraintSource to set
	 */
	public void setConstraintSource(ConstraintSource constraintSource) {
		this.constraintSource = constraintSource;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#getConstraintSource()
	 */
	public ConstraintSource getConstraintSource() {
		return this.constraintSource;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#isSatisfied()
	 */
	public boolean isSatisfied() {
		// TODO Auto-generated method stub
		return this.satisfied;
	}

	/**
	 * @return the Uid that has been assigned to this SpatialConstraint.
	 */
	public long getUid() {
		return this.uid;
	}

	/**
	 * @return unique identifier for the database.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            unique identifier for the database.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param satisfied
	 */
	public void setSatisfied(boolean satisfied) {
		this.satisfied = satisfied;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the abstractFTS
	 */
	public String getAbstract() {
		return abstractFTS;
	}

	/**
	 * @param abstractFTS
	 *            the abstractFTS to set
	 */
	public void setAbstract(String abstractFTS) {
		this.abstractFTS = abstractFTS;
	}

	/**
	 * @return the featureTypeName
	 */
	public String getFeatureTypeName() {
		return featureTypeName;
	}

	/**
	 * @param featureTypeName
	 *            the featureTypeName to set
	 */
	public void setFeatureTypeName(String featureTypeName) {
		this.featureTypeName = featureTypeName;
	}

	/**
	 * @return the semanticTypeIdentifier
	 */
	public String getSemanticTypeIdentifier() {
		return semanticTypeIdentifier;
	}

	/**
	 * @param semanticTypeIdentifier
	 *            the semanticTypeIdentifier to set
	 */
	public void setSemanticTypeIdentifier(String semanticTypeIdentifier) {
		this.semanticTypeIdentifier = semanticTypeIdentifier;
	}

	/**
	 * @return the rule
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * @param rule
	 *            the rule to set
	 */
	public void setRule(String rule) {
		this.rule = rule;
	}

}
