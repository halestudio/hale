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

package eu.esdihumboldt.specification.informationgrounding.requesthandler;

/**
 * Name: eu.esdihumboldt.informationgrounding.requesthandler / Relevancy<br/>
 * Purpose: This interface describes a Relevancy element. This is not a
 * descriptive element of a Grounding Service. Its goal is to provide the
 * relevancy weighting fulfilling the constraints which were imposed. This
 * information is important when a list of constraints has to be fulfilled.
 * 
 * @author Ana Belen Anton
 * @partner 02 / ETRA Research and Development
 * @version Framework v1.0
 */

public interface Relevancy {

	/**
	 * @return True if the complete list of constraints is satisfied by the
	 *         Grounding Service. This should be the "perfect match". False if
	 *         some constraint is not fulfilled.
	 */
	public boolean isPerfectMatch();

	/**
	 * @return the relevancy weighting when the Grounding Service is a not
	 *         perfect match. This is the valorisation of the Grounding Service
	 *         fulfilling the imposed constraints. Note: Value = 1 is the best
	 *         macth (not the perfect match)
	 */
	public float relevancyWeight();

}
