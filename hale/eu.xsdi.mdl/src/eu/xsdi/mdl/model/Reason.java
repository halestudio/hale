/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
 */
package eu.xsdi.mdl.model;

import eu.esdihumboldt.goml.align.Entity;
import eu.xsdi.mdl.model.reason.ReasonRule;

/**
 * The {@link Reason} object provides information on why a certain mismatch 
 * occurs. The Reason part of a Mismatch is based on the Rule which was 
 * fulfilled, i.e. identified the mismatch.
 * 
 * A mismatch identification can be based on properties of the schema elements,
 * on the property of the declared mapping or on properties of instance data 
 * used for verification.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class Reason {
	
	private Entity entity1;

	private Entity entity2;

	private EntityCharacteristic characteristic;
	
	private ReasonRule reasonRule;
	
	private String comment;
	
	/**
	 * @param e1 the first mapped {@link Entity}.
	 * @param e2 the second mapped {@link Entity}.
	 * @param characteristic the specific schema or instance 
	 * {@link EntityCharacteristic} that is different between the mapped 
	 * {@link Entity}s.
	 */
	public Reason(Entity e1, Entity e2, EntityCharacteristic characteristic) {
		this.characteristic = characteristic;
		this.entity1 = e1;
		this.entity2 = e2;
	}
	
	
	
	/**
	 * @return the entity characteristic that is different between Entity1 and
	 * Entity2.
	 */
	public EntityCharacteristic getCharacteristic() {
		return characteristic;
	}

	/**
	 * @param characteristic the characteristic to set
	 */
	public void setCharacteristic(EntityCharacteristic characteristic) {
		this.characteristic = characteristic;
	}

	/**
	 * @return the reasonRule
	 */
	public ReasonRule getReasonRule() {
		return reasonRule;
	}

	/**
	 * @param reasonRule the reasonRule to set
	 */
	public void setReasonRule(ReasonRule reasonRule) {
		this.reasonRule = reasonRule;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @param entity1 the entity1 to set
	 */
	public void setEntity1(Entity entity1) {
		this.entity1 = entity1;
	}

	/**
	 * @param entity2 the entity2 to set
	 */
	public void setEntity2(Entity entity2) {
		this.entity2 = entity2;
	}

	/**
	 * @return the first {@link Entity} that is mapped via the Cell from which 
	 * this {@link Mismatch} {@link Reason} comes.
	 */
	public Entity getEntity1() {
		return entity1;
	}

	/**
	 * @return the second {@link Entity} that is mapped via the Cell from which 
	 * this {@link Mismatch} {@link Reason} comes.
	 */
	public Entity getEntity2() {
		return entity2;
	}

	public String toString() {
		return "Reason: " + this.entity1.getLocalname() + "<-" 
						+ "->" + this.entity2.getLocalname();
	}

	/**
	 * @return a human-readable description of the mismatch reason represented
	 * by this object.
	 */
	public String getDescription() {
		return this.comment;
	}
	
	public enum EntityCharacteristic {
		ClassExistence("Class Existence"),
		ClassTaxonomicStructure("Class Taxonomic Structure"),
		ClassMembershipConstraint("Class Membership Constraint"),
		AssociationExistence("Association Existence"),
		AssociationTypeConstraint("Association Type Constraint"),
		AssociationValueConstraint("Association Value Constraint"),
		AssociationFunctionalConstraint("Association Functional Constraint"),
		AssociationCardinalityConstraint("Association Cardinality Constraint"),
		AttributeExistence("Attribute Existence"),
		AttributeTypeConstraint("Attribute Type Constraint"),
		AttributeValueConstraint("Attribute Value Constraint"),
		AttributeFunctionalConstraint("Attribute Functional Constraint"),
		AttributeCardinalityConstraint("Attribute Cardinality Constraint"),
		AssociationConcreteType("Association Concrete Type"),
		AssociationConcreteValue("Association Concrete Value"),
		AssociationConcreteCardinality("Association Concrete Cardinality"),
		AttributeConcreteType("Attribute Concrete Type"),
		AttributeConcreteValue("Attribute Concrete Value"),
		AttributeConcreteCardinality("Attribute Concrete Cardinality"),
		AttributeDerivedAssociation("Attribute Derived Association"),
		AttributeDerivedConcreteValueExtrema("Attribute Concrete Value Extrema"),
		AttributeDerivedPopulatedValues("Attribute Populated Values");
		
		private String characteristic;
		
		private EntityCharacteristic(String characteristic) {
			this.characteristic = characteristic;
		}
		
		public String toString() {
			return this.characteristic;
		}
	}
	
}
