/*
 * Copyright (c) 2023 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.xls.reader;

import java.util.Optional;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;

/**
 * Settings for reading a specific sheet in an Excel table.
 * 
 * @author Simon Templer
 */
public class SheetSettings {

	private final String identifiedByName;
	private final Integer identifiedByIndex;

	private QName typeName;
	private Boolean skipSheet;
	private Integer skipLines;

	/**
	 * Create sheet settings
	 * 
	 * @param identifiedByName optional sheet name to identify a sheet
	 * @param identifiedByIndex optional sheet index to identify a sheet
	 */
	public SheetSettings(String identifiedByName, Integer identifiedByIndex) {
		super();
		this.identifiedByName = identifiedByName;
		this.identifiedByIndex = identifiedByIndex;
	}

	/**
	 * @return the identifiedByName
	 */
	public String getIdentifiedByName() {
		return identifiedByName;
	}

	/**
	 * @return the identifiedByIndex
	 */
	public Integer getIdentifiedByIndex() {
		return identifiedByIndex;
	}

	/**
	 * @return the typeName
	 */
	public QName getTypeName() {
		return typeName;
	}

	/**
	 * @return the skipSheet
	 */
	public Boolean getSkipSheet() {
		return skipSheet;
	}

	/**
	 * @return the skipLines
	 */
	public Integer getSkipLines() {
		return skipLines;
	}

	/**
	 * @param typeName the typeName to set
	 */
	public void setTypeName(QName typeName) {
		this.typeName = typeName;
	}

	/**
	 * @param skipSheet the skipSheet to set
	 */
	public void setSkipSheet(Boolean skipSheet) {
		this.skipSheet = skipSheet;
	}

	/**
	 * @param skipLines the skipLines to set
	 */
	public void setSkipLines(Integer skipLines) {
		this.skipLines = skipLines;
	}

	/**
	 * Convert to a {@link Value}.
	 * 
	 * @return the value representation of the sheet settings
	 */
	public Value toValue() {
		ValueProperties props = new ValueProperties();
		if (identifiedByName != null) {
			props.put("identifiedByName", Value.of(identifiedByName));
		}
		if (identifiedByIndex != null) {
			props.put("identifiedByIndex", Value.of(identifiedByIndex));
		}
		if (typeName != null) {
			props.put("typeName", Value.of(typeName));
		}
		if (skipSheet != null) {
			props.put("skipSheet", Value.of(skipSheet));
		}
		if (skipLines != null) {
			props.put("skipLines", Value.of(skipLines));
		}
		return props.toValue();
	}

	/**
	 * Convert from a {@link Value}.
	 * 
	 * @param value the value to interpret as {@link SheetSettings}
	 * @return the sheet settings if applicable
	 */
	public static Optional<SheetSettings> fromValue(Value value) {
		ValueProperties props = value.as(ValueProperties.class);
		if (props != null) {
			String identifiedByName = props.getSafe("identifiedByName").as(String.class);
			Integer identifiedByIndex = props.getSafe("identifiedByIndex").as(Integer.class);
			QName typeName = props.getSafe("typeName").as(QName.class);
			Boolean skipSheet = props.getSafe("skipSheet").as(Boolean.class);
			Integer skipLines = props.getSafe("skipLines").as(Integer.class);

			SheetSettings settings = new SheetSettings(identifiedByName, identifiedByIndex);

			settings.setTypeName(typeName);
			settings.setSkipSheet(skipSheet);
			settings.setSkipLines(skipLines);

			return Optional.of(settings);
		}

		return Optional.empty();
	}

}
