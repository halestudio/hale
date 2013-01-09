/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.io.impl.internal;

import java.util.Collection;

/**
 * Stands for an alignment modifier.
 * 
 * @author Kai Schwierczek
 */
public class ModifierBean {

	private String cell;
	private Collection<String> disableForRelation;

	// private Map<String, String> properties = new HashMap<String, String>();

	/**
	 * Default constructor.
	 */
	public ModifierBean() {
	}

	/**
	 * Constructor using the specified cell id.
	 * 
	 * @param cell the cell id to set
	 */
	public ModifierBean(String cell) {
		this.cell = cell;
	}

	/**
	 * @return the cell id
	 */
	public String getCell() {
		return cell;
	}

	/**
	 * @param cell the cell id to set
	 */
	public void setCell(String cell) {
		this.cell = cell;
	}

	/**
	 * @return the disableForRelation
	 */
	public Collection<String> getDisableForRelation() {
		return disableForRelation;
	}

	/**
	 * @param disableForRelation the disableForRelation to set
	 */
	public void setDisableForRelation(Collection<String> disableForRelation) {
		this.disableForRelation = disableForRelation;
	}
}
