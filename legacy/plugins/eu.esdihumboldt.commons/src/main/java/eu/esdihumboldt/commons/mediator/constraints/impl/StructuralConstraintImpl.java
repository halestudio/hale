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

package eu.esdihumboldt.commons.mediator.constraints.impl;

import java.util.List;
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.StructuralConstraint;

/**
 * 
 * @author mgone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class StructuralConstraintImpl implements StructuralConstraint {

	UUID uuid = UUID.randomUUID();

	private boolean writeable = false;

	private List<String> list;

	private boolean status;

	private long id;

	private ConstraintSource constraintSource;

	private boolean sharedConstraint = false;

	public StructuralConstraintImpl() {
		this.status = false;
	}

	public StructuralConstraintImpl(List<String> schemas,
			ConstraintSource context) {
		this.list = schemas;
		this.constraintSource = context;
	}

	public List<String> getSupportedSchema() {
		return this.list;
	}

	public boolean isSatisfied() {
		return this.status;
	}

	public ConstraintSource getConstraintSource() {
		return this.constraintSource;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UUID getIdentifier() {
		return this.uuid;
	}

	public void setIdentifier(UUID identifier) {
		this.uuid = identifier;
	}

	public boolean isFinalized() {
		return this.writeable;
	}

	public void setFinalized(boolean write) {
		writeable = write;
	}

	public boolean compatible(Constraint constraint) {
		// Checks whether the this input schema is compatible with a given set
		// of
		// input schema of another input or output
		if (constraint.getClass() == this.getClass()) {
			for (String schema : this.getSupportedSchema()) {

				if (((StructuralConstraintImpl) constraint)
						.getSupportedSchema().contains(schema)) {
					return true;
				}
			}

		}
		return false;
	}

	public void setSatisfied(boolean _satisfied) {
		this.status = _satisfied;
	}

	public void setShared(boolean shared) {
		this.sharedConstraint = shared;
	}

	public boolean isShared() {
		return sharedConstraint;
	}
}
