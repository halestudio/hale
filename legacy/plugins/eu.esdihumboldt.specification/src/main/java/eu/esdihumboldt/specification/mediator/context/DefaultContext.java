/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.specification.mediator.context;

import eu.esdihumboldt.specification.mediator.constraints.LanguageConstraint;
import eu.esdihumboldt.specification.mediator.constraints.MetadataConstraint;
import eu.esdihumboldt.specification.mediator.constraints.PortrayalConstraint;
import eu.esdihumboldt.specification.mediator.constraints.QualityConstraint;
import eu.esdihumboldt.specification.mediator.constraints.ResolutionConstraint;
import eu.esdihumboldt.specification.mediator.constraints.SpatialConstraint;
import eu.esdihumboldt.specification.mediator.constraints.TemporalConstraint;
import eu.esdihumboldt.specification.mediator.constraints.ThematicConstraint;

/**
 * A DefaultContext Interface allows access to default details like:
 * <ul>
 * <li>Default Thematic Constraint,</li>
 * <li>Default Language Constraint,</li>
 * <li>Default Quality Constraint,</li>
 * <li>Default Temporal Constraint,</li>
 * <li>Default Spatial Constraint.</li>
 * </ul>
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: DefaultContext.java,v 1.2 2007-11-06 09:32:37 pitaeva Exp $
 */
public interface DefaultContext extends Context {

	/**
	 * 
	 * 
	 * @return Returns Default Thematic Constraint, if User or Organization
	 *         Thematic Constraint is not specified.
	 */
	public ThematicConstraint getThematicConstraint();

	/**
	 * 
	 * @return Returns Default Language Constraint, if User Language Constraint
	 *         is not specified.
	 */
	public LanguageConstraint getLanguageConstraint();

	/**
	 * 
	 * @return Returns Default Quality Constraint, if User or Organization
	 *         Quality Constraint is not specified.
	 * 
	 */
	public QualityConstraint getQualityConstraint();

	/**
	 * 
	 * @return Returns Default Temporal Constraint, if User or Organization
	 *         Temporal Constraint is not specified.
	 * 
	 */
	public TemporalConstraint getTemporalConstraint();

	/**
	 * 
	 * @return Returns Default Spatial Constraint, if User or Organization
	 *         Spatial Constraint is not specified.
	 * 
	 */
	public SpatialConstraint getSpatialConstraint();

	/**
	 * 
	 * @return Returns Default Portrayal Constraint, if User or Organization
	 *         Portrayal Constraint is not specified.
	 * 
	 */
	public PortrayalConstraint getPortrayalConstraint();

	/**
	 * 
	 * @return Returns Default Resolution Constraint, if User or Organization
	 *         Resolution Constraint is not specified.
	 * 
	 */
	public ResolutionConstraint getResolutionConstraint();

	/**
	 * 
	 * @return Default Metadata constraint, if User or Organization Metadata
	 *         Constraint is not specified.
	 * 
	 */
	public MetadataConstraint getMetadataConstraint();

}
