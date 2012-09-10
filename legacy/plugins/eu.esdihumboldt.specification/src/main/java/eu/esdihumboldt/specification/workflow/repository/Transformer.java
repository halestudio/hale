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
package eu.esdihumboldt.specification.workflow.repository;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

import org.opengis.metadata.MetaData;
import org.opengis.metadata.lineage.Lineage;

import eu.esdihumboldt.specification.mediator.MediatorComplexRequest;
import eu.esdihumboldt.specification.mediator.TypeKey;
import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.workflow.process.Description;
import eu.esdihumboldt.specification.workflow.transformer.inputoutputs.ComplexData;
import eu.esdihumboldt.specification.workflow.transformer.inputoutputs.LiteralDataInput;
import eu.esdihumboldt.specification.workflow.transformer.inputoutputs.ProcessInput;
import eu.esdihumboldt.specification.workflow.transformer.inputoutputs.ProcessOutput;

/**
 * This is the superinterface for all algorithm implementations which perform
 * transformations upon the response retrieved from the DAS. The Transformers
 * form a Stack which can be processed either on-the-fly, thereby working on the
 * actual Response, or in a non-destructive manner, preserving the original
 * answer. Each Transformer implementation is required to provide
 * {@link MetaData} that describes the processing it applies so that a full
 * {@link Lineage} for a transformed dataset can be maintained.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface Transformer extends Serializable {

	/**
	 * This operation is used at workflow construction time to retrieve the
	 * fixed Set of preconditions that define this {@link Transformer}. If
	 * called at execution time, it will provide {@link MediatorComplexRequest}s
	 * that also contain those constraints dependent on the client contraints.
	 * 
	 * @return a Set of {@link MediatorComplexRequest}s, each one containing the
	 *         constraints that need to be fulfilled to satisfy the
	 *         precondition.
	 */
	public Set<ProcessInput> getInputs();

	/**
	 * This operation is used at workflow construction time to retrieve the
	 * fixed {@link Set} of postcondition that define this {@link Transformer}.
	 * If called at execution time, it has to return
	 * {@link DesignTimeInputOutput} object containing the constraints depending
	 * on the concrete request as well.
	 * 
	 * @return a {@link DesignTimeInputOutput}s, with the constraints that
	 *         describe the postcondition.
	 */
	public ProcessOutput getOutput();

	/**
	 * @return the {@link ProcessStatus} that this {@link Transformer} currently
	 *         has.
	 */
	public ProcessStatus getStatus();

	/**
	 * @return the Process Identifier of this {@link Transformer}. It maps to
	 *         the WPS process identifier
	 */
	public String getIdentifier();

	/**
	 * 
	 * @return grounding of this transformer that points to the WPS that does
	 *         the actual Transformation
	 */
	public URI getGrounding();

	/**
	 * This enumeration summarizes the states that a certain Transformer can be
	 * in.
	 */
	public enum ProcessStatus {

		/**
		 * None or at least one of the preconditions of Transformer in Question
		 * has been satisfied with an a grounding or a {@link Transformer} yet.
		 * Transfomer is in a waiting mode for all preconditions to be fulfilled
		 * with either a grounding or another transfomer's postcondition
		 * 
		 */
		waiting,
		/**
		 * All Transformer's preconditions satisfied either by a Transfomer or
		 * by a grounding but it is not yet being executed
		 */
		ready,
	}

	/**
	 * Each of the inputs or output a Transformer has a certain status which is
	 * important for the status of the Transformer during design and
	 * construction time. For possible status values during the whole lifecycle
	 * of the {@link Transformer}, please refer to the {@link ProcessStatus}
	 * description.
	 */
	public enum InputOutputStatus {

		/**
		 * There is no Transformer or AccessRequest satisfying this input for
		 * the case of a transformer Input. Or one or more Inputs have in a
		 * transformer have not been satisfied for the case of a Transformer
		 * output .
		 */
		unsatisfied,
		/**
		 * A status where either a Transformer or a grounding service has been
		 * attached to an input that satisfies this input. It is an output, all
		 * the inputs in a given transformer has been satisfied.
		 */
		satisfied,
	}

	/**
	 * retrieves only the inputs which are of type complexData
	 * 
	 * @return A set of ComplexData
	 */
	public Set<ComplexData> getComplexInputs();

	/**
	 * retrieves only the inputs which are of type LiteralData
	 * 
	 * @return A set of LiteralData
	 */
	public Set<LiteralDataInput> getLiteralInputs();

	/**
	 * This enumerator, list the various type of a transformers . A trasformer
	 * can only perform one type of transformation corresponding to a particular
	 * constraint. Alternatively, in case of a processing Transformer, this can
	 * be aby
	 */
	public enum ProcessType {

		/**
		 * Identifies a Transformer that performs transformation on the service
		 * constraint
		 */
		ServiceTransformer,
		/**
		 * Identifies a Transformer that performs transformation on the Logical
		 * constraint
		 */
		LogicalTransformer,
		/**
		 * Identifies a Transformer that performs transformation on the Quality
		 * constraint
		 */
		QualityTransformer,
		/**
		 * Identifies a Transformer that performs transformation on the Metadata
		 * constraint
		 */
		MetadataTransformer,
		/**
		 * Identifies a Transformer that performs transformation on the Language
		 * constraint
		 */
		LanguageTransformer,
		/**
		 * Identifies a Transformer that performs transformation on the Spatial
		 * constraint. especially the CRS.
		 */
		SpatialTransformer,
		/**
		 * Identifies a Transformer that performs transformation on the thematic
		 * constraint
		 */
		ThematicTransformer,
		/**
		 * Identifies a Data processing transfomer and not a harmonization
		 * transformer
		 */
		ProcessingTransformer;

	}

	/**
	 * retrieves the description of this Transformer process
	 * 
	 * @return
	 */
	public Description getProcessDescription();

	/**
	 * returns the unique id of this Transformer
	 * 
	 * @return
	 */
	public UUID getTransformerId();

	/**
	 * 
	 * @return The process type of this transfomer
	 */
	public ProcessType getType();

	/**
	 * 
	 * @return The constraint type that this Transfomer transforms or a default
	 *         TransformerType
	 */
	public TypeKey getTransformationType();

	/**
	 * Constraints that are shared between several preconditions/ with
	 * postcondition of a single Transformer can be used to specify
	 * cross-parameter constraints on that Transformer. A typical
	 * cross-parameter constraint is e.g. a common spatial reference system of
	 * all input layers of a process.This method is used to share a given
	 * constraint among the Transformer inputs and or with Transformer output.
	 * 
	 * @param inputs
	 *            Inputs in this transformer that will share a given constraints
	 * @param shareOutput
	 *            True if the constraint is also shared with the output
	 * @param sharedConstraint
	 *            the constraint that will be shared among inputs
	 */
	public void shareConstraint(Set<ComplexData> inputs, boolean shareOutput,
			Constraint sharedConstraint);

}
