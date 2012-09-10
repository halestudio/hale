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

import java.util.UUID;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;

/**
 * This constraint is used to define the type of a processing Transformer that
 * is not a harmonization Transformer
 * 
 * @author mgone
 */
public class TransformerType implements Constraint {

	private static final long serialVersionUID = 7438636912465756746L;

	public TransformerType() {
	}

	public boolean isSatisfied() {
		return false;
	}

	public ConstraintSource getConstraintSource() {
		return null;
	}

	public long getId() {
		return 1;
	}

	public void setId(long id) {

	}

	public UUID getIdentifier() {
		return UUID.randomUUID();
	}

	public void setIdentifier(UUID identifier) {

	}

	public boolean isFinalized() {
		return false;
	}

	public void setFinalized(boolean write) {

	}

	public void setSatisfied(boolean _satisfied) {

	}

	public boolean compatible(Constraint constraint) {
		return false;
	}

	public void setShared(boolean shared) {

	}

	public boolean isShared() {
		return false;
	}

}
