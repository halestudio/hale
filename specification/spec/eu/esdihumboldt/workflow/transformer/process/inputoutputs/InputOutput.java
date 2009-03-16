package eu.esdihumboldt.workflow.transformer.process.inputoutputs;

import eu.esdihumboldt.workflow.exceptions.IncompatibleTransformersException;
import eu.esdihumboldt.workflow.processdescription.InputDescription;
import eu.esdihumboldt.workflow.repository.Transformer;
import eu.esdihumboldt.workflow.repository.Transformer.InputOutputStatus;
import java.util.UUID;

/**
 *  @author mgone
 */
// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.E748DC78-75B9-A624-D120-D2C3D40D736C]
// </editor-fold> 
public interface InputOutput {

    /**
     *  @return the {@link UUID} uniquely identifying this {@link input or output}.
     */
    public UUID getUniqueId();

    /**
     *  @return the process input/output identifier for identifying this {@link input}.
     */
    public String getIdentifier();

    /**
     *  Describes this input/output
     *       @return the {@link  InputDescription}
     */
    public InputDescription getDescription();

    /**
     *  @return the status of this input or output
     */
    public InputOutputStatus getStatus();

    /**
     *  This method is a convinience method for identifying which Transformer the precondition belongs
     *  to.
     *       @return
     */
    public Transformer getParentTransformer();

    /**
     *  This method sets the parent Transformer to which this input belongs to
     *       @param _parent
     */
    public void setParentTransformer(Transformer _parent);

    /**
     *  This method determines the compatibility of this input/output to another input/output
     *       @param inputOutput 
     *       @return
     * @throws IncompatibleTransformersException
     *
     */
    public boolean isCompatible(InputOutput inputOutput) throws IncompatibleTransformersException;
}

