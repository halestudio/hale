package eu.esdihumboldt.commons.mediator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.InterfaceController;
import eu.esdihumboldt.specification.mediator.MediatorComplexRequest;
import eu.esdihumboldt.specification.mediator.TypeKey;
import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.Constraint.ConstraintSource;
import eu.esdihumboldt.specification.mediator.context.Context;
import eu.esdihumboldt.specification.modelrepository.abstractfc.Concept;

public class MediatorComplexRequestImpl implements MediatorComplexRequest {

	private Context context;
	private Map<TypeKey, Constraint> constraints;
	private UUID identifier;
	private Concept taskConcept;

	public MediatorComplexRequestImpl() {

		constraints = new TreeMap<TypeKey, Constraint>();
	}

	public Constraint getConstraint(TypeKey key) {
		return constraints.get(key);
	}

	public Map<TypeKey, Constraint> getConstraints() {
		return constraints;
	}

	public Map<TypeKey, Constraint> getConstraints(ConstraintSource type) {

		HashMap<TypeKey, Constraint> map = new HashMap<TypeKey, Constraint>();

		// Get all TypeKey used
		Set<TypeKey> keys = constraints.keySet();

		// Find all Constraints with ConstraintSource equals to given type
		for (TypeKey key : keys) {
			Constraint c = this.constraints.get(key);
			if (c.getConstraintSource() == type) {
				map.put(key, c);
			}
		}

		return map;
	}

	public Context getContext() {
		return context;
	}

	public UUID getIdentifier() {
		return identifier;
	}

	public InterfaceController getInitiator() {
		// TODO Auto-generated method stub
		return null;
	}

	public Concept getTaskConcept() {
		return taskConcept;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @param constraints
	 *            the constraints to set
	 */
	public void setConstraints(Map<TypeKey, Constraint> constraints) {
		this.constraints = constraints;
	}

	public void putConstraint(TypeKey key, Constraint constraint) {
		this.constraints.put(key, constraint);
	}

	public void setIdentifier(UUID identifier) {
		this.identifier = identifier;
	}

	public void setTaskConcept(Concept taskConcept) {
		this.taskConcept = taskConcept;
	}
}
