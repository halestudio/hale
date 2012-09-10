/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.modelrepository.abstractfc;

import java.util.Date;

/**
 * This interface gives access to information on the version of a resource, such
 * as an ConceptualSchema.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface VersionInformation {

	/**
	 * @return the major version number of the object to which this
	 *         VersionInformation is bound. Objects with the same
	 *         MajorVersionNumber should be backwards- and forwards compatible.
	 */
	public int getMajorVersionNumber();

	/**
	 * @return the minor version number of the object to which this
	 *         VersionInformation is bound. Objects with the same
	 *         MinorVersionNumber should have an identical API, whereas objects
	 *         which differ only in their MinorVersionNumber may have
	 *         differences that leave them compatible (i.e. additional
	 *         operations)
	 */
	public int getMinorVersionNumber();

	/**
	 * @return a Build number that should be incremented with every even very
	 *         small change to the object to which this VersionInformation is
	 *         bound.
	 */
	public int getBuildNumber();

	/**
	 * @return the Date on which this Version of the object to which this
	 *         VersionInformation is bound to was released.
	 */
	public Date getVersionDate();

	/**
	 * @return the Date until which the object to which this VersionInformation
	 *         is bound is valid for use.
	 */
	public Date getExpirationDate();

}
