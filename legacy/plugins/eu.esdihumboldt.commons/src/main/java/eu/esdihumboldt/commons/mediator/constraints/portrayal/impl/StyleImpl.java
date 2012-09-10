package eu.esdihumboldt.commons.mediator.constraints.portrayal.impl;

import java.util.List;

import org.opengis.style.Description;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Style;
import org.opengis.style.StyleVisitor;
import org.opengis.style.Symbolizer;
import org.opengis.util.InternationalString;

import eu.esdihumboldt.specification.mediator.constraints.Constraint.ConstraintSource;
import eu.esdihumboldt.specification.mediator.constraints.SpatialConstraint;

public class StyleImpl implements Style {

	private String name;

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

	public InternationalString getAbstract() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InternationalString getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public void accept(StyleVisitor arg0) {
		// TODO Auto-generated method stub

	}

	public List<? extends FeatureTypeStyle> featureTypeStyles() {
		// TODO Auto-generated method stub
		return null;
	}

	public Symbolizer getDefaultSpecification() {
		// TODO Auto-generated method stub
		return null;
	}

	public Description getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDefault() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see org.opengis.style.Style#accept(org.opengis.style.StyleVisitor,
	 *      java.lang.Object)
	 */
	@Override
	public Object accept(StyleVisitor visitor, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

}
