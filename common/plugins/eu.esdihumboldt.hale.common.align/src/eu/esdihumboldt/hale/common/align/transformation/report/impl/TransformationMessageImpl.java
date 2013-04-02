/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.transformation.report.impl;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationMessage;
import eu.esdihumboldt.hale.common.core.report.impl.MessageImpl;

/**
 * Default {@link TransformationMessage} implementation
 * 
 * @author Simon Templer
 */
@Immutable
public class TransformationMessageImpl extends MessageImpl implements TransformationMessage {

	private final String cellId;

	/**
	 * Create a new transformation message.
	 * 
	 * @param cell the cell the message is associated to
	 * @param message the message
	 * @param throwable the throwable associated to the message, may be
	 *            <code>null</code>
	 */
	public TransformationMessageImpl(Cell cell, String message, Throwable throwable) {
		super(message, throwable);

		this.cellId = cell.getId();
	}

	/**
	 * Create a new transformation message.
	 * 
	 * @param cell the cell the message is associated to
	 * @param message the message
	 * @param throwable the throwable associated to the message, may be
	 *            <code>null</code>
	 * @param stackTrace the associated stack trace, or <code>null</code>
	 */
	public TransformationMessageImpl(Cell cell, String message, Throwable throwable,
			String stackTrace) {
		super(message, throwable, stackTrace);

		this.cellId = cell.getId();
	}

	/**
	 * Create a new transformation message.
	 * 
	 * @param cellId the identifier of the cell the message is associated to
	 * @param message the message
	 * @param throwable the throwable associated to the message, may be
	 *            <code>null</code>
	 * @param stackTrace the associated stack trace, or <code>null</code>
	 */
	public TransformationMessageImpl(String cellId, String message, Throwable throwable,
			String stackTrace) {
		super(message, throwable, stackTrace);

		this.cellId = cellId;
	}

	/**
	 * @see TransformationMessage#getCellId()
	 */
	@Override
	public String getCellId() {
		return cellId;
	}

}
