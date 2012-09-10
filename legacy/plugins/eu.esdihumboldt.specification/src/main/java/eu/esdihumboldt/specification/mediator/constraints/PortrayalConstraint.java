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
package eu.esdihumboldt.specification.mediator.constraints;

import java.util.List;
import java.util.Set;

import eu.esdihumboldt.specification.mediator.constraints.portrayal.NamedLayer;
import eu.esdihumboldt.specification.mediator.constraints.portrayal.UserLayer;

/**
 * A PortrayalConstraint Interface allows access to the Style Inforamtion
 * details like:
 * <ul>
 * <li>NamedStyleDescription,</li>
 * <li>UserStyleDescription.</li>
 * 
 * </ul>
 * 
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id$
 * 
 */
public interface PortrayalConstraint extends Constraint {

	/**
	 * Allows access to the styles structure, if the StyledLayerDescriptor not
	 * used.
	 * 
	 * 
	 * @return List of named styles.
	 * 
	 */
	public List<org.opengis.style.Style> getStyle();

	/**
	 * 
	 * @return the Name, that is an optional element of the SLD.
	 */
	public String getName();

	/**
	 * 
	 * @return the Title, that is an optional element of the SLD.
	 */
	public String getTitle();

	/**
	 * 
	 * @return the Abstract, that is an optional element of the SLD.
	 */
	public String getAbstract();

	/**
	 * 
	 * @return the List of the NamedLayer, defined for this SLD.
	 */

	public Set<NamedLayer> getNamedLayer();

	/**
	 * 
	 * @return the List of the UserLayer, defined for this SLD.
	 */
	public Set<UserLayer> getUserLayer();

}
