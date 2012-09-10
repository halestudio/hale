/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.commons.mediator.context.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.LanguageConstraint;
import eu.esdihumboldt.specification.mediator.constraints.MetadataConstraint;
import eu.esdihumboldt.specification.mediator.constraints.PortrayalConstraint;
import eu.esdihumboldt.specification.mediator.constraints.QualityConstraint;
import eu.esdihumboldt.specification.mediator.constraints.ResolutionConstraint;
import eu.esdihumboldt.specification.mediator.constraints.SpatialConstraint;
import eu.esdihumboldt.specification.mediator.constraints.TemporalConstraint;
import eu.esdihumboldt.specification.mediator.constraints.ThematicConstraint;
import eu.esdihumboldt.specification.mediator.context.Context;
import eu.esdihumboldt.specification.mediator.context.DefaultContext;

/**
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: DefaultContextImpl.java,v 1.5 2007-11-27 13:27:57 pitaeva Exp $
 */
public class DefaultContextImpl implements DefaultContext, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Attributes ..............................................................
	private LanguageConstraint languageConstraint;
	private MetadataConstraint metadataConstraint;
	private QualityConstraint qualityConstraint;
	private ResolutionConstraint resolutionConstraint;
	private ThematicConstraint thematicConstraint;
	private TemporalConstraint temporalConstraint;
	private SpatialConstraint spatialConstraint;
	private PortrayalConstraint portrayalConstraint;

	private long id;
	private UUID uuid;
	private String title;

	// Constructors ............................................................

	/**
	 * Full constructor for {@link DefaultContextImpl}.
	 * 
	 * @param _languageCons
	 * @param _metadataConstraint
	 * @param _qualityConstraint
	 * @param _resoCons
	 * @param _thematicCons
	 * @param _temporalCons
	 * @param _spatialCons
	 * @param _portrayalCons
	 *            TODO remove inconsistent naming.
	 */
	public DefaultContextImpl(LanguageConstraint _languageCons,
			MetadataConstraint _metadataConstraint,
			QualityConstraint _qualityConstraint,
			ResolutionConstraint _resoCons, ThematicConstraint _thematicCons,
			TemporalConstraint _temporalCons, SpatialConstraint _spatialCons,
			PortrayalConstraint _portrayalCons) {
		this.languageConstraint = _languageCons;
		this.metadataConstraint = _metadataConstraint;
		this.portrayalConstraint = _portrayalCons;
		this.qualityConstraint = _qualityConstraint;
		this.resolutionConstraint = _resoCons;
		this.spatialConstraint = _spatialCons;
		this.temporalConstraint = _temporalCons;
		this.thematicConstraint = _thematicCons;
		this.uuid = UUID.randomUUID();

	}

	// DefaultContext operations ...............................................

	public DefaultContextImpl() {
		// this.uuid = UUID.randomUUID();
	}

	public LanguageConstraint getLanguageConstraint() {
		return this.languageConstraint;
	}

	public MetadataConstraint getMetadataConstraint() {
		return this.metadataConstraint;
	}

	public PortrayalConstraint getPortrayalConstraint() {
		return this.portrayalConstraint;
	}

	public QualityConstraint getQualityConstraint() {
		return this.qualityConstraint;
	}

	public ResolutionConstraint getResolutionConstraint() {
		return this.resolutionConstraint;
	}

	public SpatialConstraint getSpatialConstraint() {
		return this.spatialConstraint;
	}

	public TemporalConstraint getTemporalConstraint() {
		return this.temporalConstraint;
	}

	public ThematicConstraint getThematicConstraint() {
		return this.thematicConstraint;
	}

	public Map<ContextType, Set<Constraint>> getAllConstraints() {
		// builds up a mapping of all the constraints that belongs to the
		// default context
		Map<ContextType, Set<Constraint>> allDef_Constraints = new HashMap<ContextType, Set<Constraint>>();
		allDef_Constraints.put(this.getContextType(),
				this.assembleConstraintSet());
		return allDef_Constraints;
	}

	public Set<Constraint> getAllConstraints(ContextType _type) {
		if (_type == this.getContextType()) {
			return this.assembleConstraintSet();
		} else {
			throw new RuntimeException("Wrong ContextType (" + _type
					+ ") presented to DefaultContext!");
		}

	}

	public Set<Constraint> getCombinedConstraints(ContextType type) {
		return this.assembleConstraintSet();
	}

	public UUID getContextID() {
		return this.uuid;
	}

	public void setContextID(UUID uuid) {
		this.uuid = uuid;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ContextType getContextType() {
		return Context.ContextType.Default;
	}

	protected Set<Constraint> assembleConstraintSet() {
		Set<Constraint> def_Constraints = new HashSet<Constraint>();
		def_Constraints.add(this.languageConstraint);
		def_Constraints.add(this.metadataConstraint);
		def_Constraints.add(this.portrayalConstraint);
		def_Constraints.add(this.qualityConstraint);
		def_Constraints.add(this.resolutionConstraint);
		def_Constraints.add(this.spatialConstraint);
		def_Constraints.add(this.temporalConstraint);
		def_Constraints.add(this.thematicConstraint);
		return def_Constraints;
	}

	/**
	 * 
	 * @return unique identifier for the database.
	 */
	public long getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            unique identifier for the database.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the uuid
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * @param languageConstraint
	 *            the languageConstraint to set
	 */
	public void setLanguageConstraint(LanguageConstraint languageConstraint) {
		this.languageConstraint = languageConstraint;
	}

	/**
	 * @param metadataConstraint
	 *            the metadataConstraint to set
	 */
	public void setMetadataConstraint(MetadataConstraint metadataConstraint) {
		this.metadataConstraint = metadataConstraint;
	}

	/**
	 * @param qualityConstraint
	 *            the qualityConstraint to set
	 */
	public void setQualityConstraint(QualityConstraint qualityConstraint) {
		this.qualityConstraint = qualityConstraint;
	}

	/**
	 * @param resolutionConstraint
	 *            the resolutionConstraint to set
	 */
	public void setResolutionConstraint(
			ResolutionConstraint resolutionConstraint) {
		this.resolutionConstraint = resolutionConstraint;
	}

	/**
	 * @param thematicConstraint
	 *            the thematicConstraint to set
	 */
	public void setThematicConstraint(ThematicConstraint thematicConstraint) {
		this.thematicConstraint = thematicConstraint;
	}

	/**
	 * @param temporalConstraint
	 *            the temporalConstraint to set
	 */
	public void setTemporalConstraint(TemporalConstraint temporalConstraint) {
		this.temporalConstraint = temporalConstraint;
	}

	/**
	 * @param spatialConstraint
	 *            the spatialConstraint to set
	 */
	public void setSpatialConstraint(SpatialConstraint spatialConstraint) {
		this.spatialConstraint = spatialConstraint;
	}

	/**
	 * @param portrayalConstraint
	 *            the portrayalConstraint to set
	 */
	public void setPortrayalConstraint(PortrayalConstraint portrayalConstraint) {
		this.portrayalConstraint = portrayalConstraint;
	}
}
