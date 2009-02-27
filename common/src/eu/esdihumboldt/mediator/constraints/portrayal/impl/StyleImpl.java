package eu.esdihumboldt.mediator.constraints.portrayal.impl;

import java.util.List;

import org.opengis.go.display.style.GraphicStyle;
import org.opengis.layer.LegendURL;
import org.opengis.layer.Style;
import org.opengis.layer.StyleSheetURL;
import org.opengis.layer.StyleURL;
import org.opengis.sld.FeatureStyle;
import org.opengis.util.InternationalString;

import eu.esdihumboldt.mediator.constraints.SpatialConstraint;
import eu.esdihumboldt.mediator.constraints.Constraint.ConstraintSource;

public class StyleImpl implements Style {

	private String name;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *  The unique identifier of the constraint int the database
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
	 * @param constraintSource the constraintSource to set
	 */
	public void setConstraintSource(ConstraintSource constraintSource) {
		this.constraintSource = constraintSource;
	}
	
	/**
	 * @see eu.esdihumboldt.mediator.constraints.Constraint#getConstraintSource()
	 */
	public ConstraintSource getConstraintSource() {
		return this.constraintSource;
	}
	
	
	
	/**
	 * @see eu.esdihumboldt.mediator.constraints.Constraint#isSatisfied()
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
	 * @param id unique identifier for the database.
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

	public List<FeatureStyle> getFeatureStyles() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<GraphicStyle> getGraphicStyles() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<LegendURL> getLegendURLs() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {		
		return this.name;
	}

	public void setName(String name) {		
		this.name = name;
	}

	public StyleSheetURL getStyleSheetURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public StyleURL getStyleURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public InternationalString getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

}
