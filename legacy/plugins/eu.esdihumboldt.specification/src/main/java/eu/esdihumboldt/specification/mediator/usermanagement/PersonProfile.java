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
package eu.esdihumboldt.specification.mediator.usermanagement;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import javax.security.cert.X509Certificate;

/**
 * The PersonProfile element contains the profile of the trading partner user:
 * <ul>
 * <li>Title contains the title of the user, related to gender and marital
 * status,
 * <li>
 * <li>AcademicTitle contains the academic title of the user e.g. Dr.,
 * <li>
 * <li>LastName contains the last or family name of user,</li>
 * <li>FirstName contains the first or given name of user,</li>
 * <li>MiddleName contains the middle name of the user if applicable,</li>
 * <li>FullName contains the full name of the user. This is for display purposes
 * on the application's user-interface,</li>
 * <li>ListOfCorrespondenceLanguage identifies the language in which all
 * correspondences with the entity identified by the Party should be,
 * <li>
 * <li>NumberFormat the format an application should use for display to this
 * user,</li>
 * <li>DateFormat the format an application should use for display to this user,
 * </li>
 * <li>TimeFormat the format an application should use for display to this user,
 * </li>
 * <li>PersonTimezone the timezone of the user for applications to use in
 * adjusting the display,</li>
 * <li>ListOfX509Certificate contains a list of one or more X509Certificate,</li>
 * <li>PersonAddress contains a user-specific address within his organization,</li>
 * <li>GeneralNotes contains any free-form text pertinent to the PersonProfile.
 * This element may contain notes or any other similar information that is not
 * contained explicitly in another structure. You should not assume that the
 * receiving application is capable of doing more than storing and/or displaying
 * this information.</li>
 * </ul>
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: PersonProfile.java,v 1.3 2007-11-09 07:37:53 pitaeva Exp $
 * 
 */
public interface PersonProfile {

	/**
	 * @return TitleType the title of the user.
	 */
	public Object getTitle();

	/**
	 * 
	 * The Title descirbes the title of the user, related to gender and marital
	 * status.
	 * 
	 */
	public enum TitleType {
		/** e.g. Mr. */
		MaleTitle,
		/** e.g. Ms. */
		FamaleTitle
	}

	/**
	 * 
	 * @return AcademicTitleType the academic title of the user.
	 */
	public Object getAcademicTitle();

	public enum AcademicTitleType {
		/** e.g. Prof. */
		ProfessorTitle,
		/** e.g. PhD. */
		DoctorTitle,
		/** e.g. no title */
		OtherTitle
	}

	/**
	 * 
	 * @return the first or given name of user.
	 * 
	 */
	public String getFirstName();

	/**
	 * 
	 * @return the last or family name of user.
	 */

	public String getLastName();

	/**
	 * 
	 * @return the middle name of the user if applicable.
	 */
	public String getMiddleName();

	/**
	 * 
	 * @return the full name of the user.
	 */
	public String getFullName();

	/**
	 * 
	 * 
	 * @return a List of Languages, in descending order of priority. The
	 *         Language on position 0 has the highest, the Language on position
	 *         n-1 the lowest priority.
	 */

	public Object getLanguage();

	/**
	 * 
	 * @return the Number Format an application should use for display to this
	 *         user.
	 */
	public NumberFormat getNumberFormat();

	/**
	 * 
	 * @return the DateFormat an application should use for display to this
	 *         user.
	 */
	public DateFormat getDateFormat();

	/**
	 * 
	 * @return the TimeFormat an application should use for display to this
	 *         user.
	 */
	public SimpleDateFormat getTimeFormat();

	/**
	 * 
	 * @return the timezone of the user for applications to use in adjusting the
	 *         display.
	 */
	public TimeZone getTimeZone();

	/**
	 * 
	 * @return a list of one or more X509Certificate for this user.
	 * @Deprecated too complex for the prototype needs, can be used for the
	 *             Production
	 */
	@Deprecated
	public List<X509Certificate> getX509Certificate();

	/**
	 * 
	 * @return the PersonAddress, that contains a user-specific address within
	 *         his organization.
	 */
	public Address getPersonalAddress();

	/**
	 * 
	 * @return GeneralNotes, that contains any free-form text pertinent to the
	 *         PersonProfile. This element may contain notes or any other
	 *         similar information that is not contained explicitly in another
	 *         structure. You should not assume that the receiving application
	 *         is capable of doing more than storing and/or displaying this
	 *         information.
	 */
	public String getGeneralNotes();

}
