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
package eu.esdihumboldt.workflow.transformer.inputOutputs;



import eu.esdihumboldt.annotations.spec.ReferenceSpecification;
import eu.esdihumboldt.workflow.transformer.domaintypes.DomainMetadata;
import java.net.URI;


/**This interface defines the Units of measure supported for an input or output of
 * a process. A specific input or output of a WPS instance will always have just
 * one measure type (length, area, speed, weight etc)
 * 
 * @author Moses Gone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
@ReferenceSpecification("OGC 05-007r7:1.0.0 9.3.1")
public interface SupportedUOMs  {

    /** Reference to default unit of measure
     * 
     * @return URI
     */
    public String getDefaultUOM();


    /**Unit of measure of this numerical output (or input)
     * 
     * @return UOM of type DomainMetadata
     */
    public DomainMetadata getUOM();



}

