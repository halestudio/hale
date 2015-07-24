/*
 * Copyright (c) 2015 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.groovy.helper



/**
 * TODO Type description
 * @author sameer sheikh
 */
public class HelperFunctionSpecification {

	private final String description;
	private final List<HelperFunctionArgument> arguments;
	private final String resultDescription;


	public HelperFunctionSpecification(String result, String description, HelperFunctionArgument ... arguments){

		this.arguments = Arrays.asList(arguments);
		this.resultDescription = result;
		this.description = description;
	}



	public String getDescription() {
		return description;
	}



	public List<HelperFunctionArgument> getArguments() {
		return arguments;
	}


	public String getResultDescription() {
		return resultDescription;
	}
}
