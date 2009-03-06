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

package eu.esdihumboldt.workflow.repository;

//~--- non-JDK imports --------------------------------------------------------
import eu.esdihumboldt.workflow.processdescription.ProcessBrief;
import eu.esdihumboldt.workflow.transformer.inputOutputs.ComplexData;
import eu.esdihumboldt.workflow.transformer.inputOutputs.LiteralData;
import eu.esdihumboldt.workflow.transformer.inputOutputs.ProcessInput;
import eu.esdihumboldt.workflow.transformer.inputOutputs.ProcessOutput;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

/**
 * This is the superinterface for all algorithm implementations which perform
 * transformations upon the response retrieved from the DAS. The
 * Transformers form a Stack which can be processed either on-the-fly,
 * thereby working on the actual Response, or in a non-destructive manner,
 * preserving the original answer. Each Transformer implementation is
 * required to provide {@link MetaData} that describes the processing it applies so
 * that a full {@link Lineage} for a transformed dataset can be maintained.
 *
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface Transformer {

    /**
     * This operation is used at workflow construction time to retrieve the
     * fixed Set of preconditions that define this {@link Transformer}. If
     * called at execution time, it will provide {@link MediatorComplexRequest}s
     * that also contain those constraints dependent on the client contraints.
     *
     * @return a Set of {@link MediatorComplexRequest}s, each one containing the
     * constraints that need to be fulfilled to satisfy the precondition.
     */
    public Set<ProcessInput> getProcessInputs();

    /**
     * This operation is used at workflow construction time to retrieve the
     * fixed {@link Set} of postcondition that define this {@link Transformer}.
     * If called at execution time, it has to return {@link DesignTimeInputOutput}
     * object containing the constraints depending on the concrete request as
     * well.
     *
     * @return a  {@link DesignTimeInputOutput}s, with the
     * constraints that describe the postcondition.
     */
    public ProcessOutput getProcessOutput();

    /**
     * @return the {@link ProcessStatus} that this {@link Transformer} currently has.
     */
    public ProcessStatus getProcessStatus();
    /**
     * @return the Process Identifier of this {@link Transformer}. It maps to the WPS
     * process identifier
     */
    public String getProcessIdentifier();
    /**
     *
     * @return grounding of this transformer that points to the WPS that does the
     * actual Transformation
     */
    public URI getGrounding();

    /**
     * This enumeration summarizes the states that a certain Transformer
     * can be in.
     */
    public enum ProcessStatus {

        /** None or at least one of the preconditions of Transformer in Question
         * has  been satisfied with an  a grounding or a {@link Transformer} yet.
         *  Transfomer is in  a waiting  mode for all preconditions to be fulfilled
         * with either a grounding or another transfomer's postcondition
         *
         */
        waiting,
        /** All Transformer's preconditions satisfied either by a Transfomer or by
         * a grounding but it is not yet being executed */
        ready,
    }

    /**
     *
     * @return
     */
    public Transformer getTargetTransformer();

    /**
     *
     * @param target
     */
    public void setOutputTarget(Transformer target);

    /**
     * retrieves only the inputs which are of type complexData
     * @return A set of ComplexData
     */
    public Set<ComplexData> getComplexDataInputs();

    /**
     * retrieves only the inputs which are of type LiteralData
     * @return A set of LiteralData
     */
    public Set<LiteralData> getLiteralDataInputs();

    /**
     * This enumerator, list the various type of a transformers . A trasformer can 
     * only perform one type of transformation corresponding to a particular constraint.
     * Alternatively, in case of a processing Transformer, this can be aby
     */
    public enum ProcessType {
                /**
         * Identifies a Transformer that performs transformation on the service constraint
         */
        ServiceTransformer,
                /**
         * Identifies a Transformer that performs transformation on the Logical constraint
         */
        LogicalTransformer,
        /**
         * Identifies a Transformer that performs transformation on the Quality constraint
         */
        QualityTransformer,
                /**
         * Identifies a Transformer that performs transformation on the Metadata constraint
         */
        MetadataTransformer,
        /**
         * Identifies a Transformer that performs transformation on the Language constraint
         */
        LanguageTransformer,
        /**
         * Identifies a Transformer that performs transformation on the Spatial constraint. especially the CRS
         */
        SpatialTransformer,
        /**
         * Identifies a Transformer that performs transformation on the thematic constraint
         */
        ThematicTransformer,
        /**
         * Identifies a Data processing transfomer and not a harmonization transformer
         */
        ProcessingTransformer;

    }

       /**
     * retrieves the description of this Transformer process
     * @return
     */
    public ProcessBrief getProcessDescription() ;
    /**
     * returns the unique id of this Transformer
     * @return
     */
    public UUID getTransformerId();
    /**
     * This method is used to determine if a transformer is the terminal tranformer
     * in the chain i.e the last transformer in the workflow. A terminal Transfomer
     * does not have  a next in chain transormer or a target Transformer. In addition,
     * its output is not directed to any other transformer
     * @return true if the transformer is terminal and false if it source to
     * the next transformer in the chain
     */
    public boolean isterminalTransformer();


    /**
     *
     * @return The process type of this transfomer
     */
    public ProcessType getProcessType();

}
