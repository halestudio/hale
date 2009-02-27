
package eu.esdihumboldt.workflow.processdescription;

import eu.esdihumboldt.workflow.transformer.domaintypes.DomainMetadata;



public interface InputDescription extends Description {



    /**
     * Provides a metadata about this input
     * @return
     */
    public DomainMetadata getMetadata();


}

