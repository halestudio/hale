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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.xls.AbstractAnalyseTable;

/**
 * Class collecting information and settings on reading
 * 
 * @author Simon Templer
 */
public class ReaderSettings {

	/**
	 * Parameter for the reader specifying that multiple sheets should be read.
	 */
	public static final String PARAMETER_MULTI_SHEET = "multiSheet";

	/**
	 * Parameter with detailed settings per sheet.
	 */
	public static final String PARAMETER_SHEET_SETTINGS = "sheetSettings";

	/**
	 * Collect information and settings on a single sheet.
	 */
	public static class SheetInfo {

		private final String name;
		private final int index;
		private final boolean empty;
		private final SheetSettings settings;

		/**
		 * @param name name of the sheet
		 * @param index index of the sheet
		 * @param empty if the sheet is empty
		 */
		public SheetInfo(String name, int index, boolean empty) {
			super();
			this.name = name;
			this.index = index;
			this.empty = empty;

			this.settings = new SheetSettings(name, index);
		}

		/**
		 * @return the settings
		 */
		public SheetSettings getSettings() {
			return settings;
		}

		/**
		 * @param settings the settings to set
		 */
		public void applySettings(SheetSettings settings) {
			if (settings.getTypeName() != null) {
				this.settings.setTypeName(settings.getTypeName());
			}
			if (settings.getSkipSheet() != null) {
				this.settings.setSkipSheet(settings.getSkipSheet());
			}
			if (settings.getSkipLines() != null) {
				this.settings.setSkipLines(settings.getSkipLines());
			}
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * @return the empty
		 */
		public boolean isEmpty() {
			return empty;
		}
	}

	private final List<SheetInfo> sheets;

	/**
	 * Constructor.
	 * 
	 * @param sheets information in sheets
	 */
	private ReaderSettings(List<SheetInfo> sheets) {
		super();
		this.sheets = sheets;
	}

	/**
	 * @return the list of sheets to read
	 */
	public Collection<? extends SheetInfo> getSheetsToRead() {
		return sheets.stream().filter(s -> !s.isEmpty() && (s.getSettings().getSkipSheet() == null
				|| s.getSettings().getSkipSheet() == false)).collect(Collectors.toList());
	}

	/**
	 * Load reader settings for Excel file reader.
	 * 
	 * @param reader the Excel reader
	 * @return the loaded settings
	 * @throws InvalidFormatException if the source file has a wrong format or
	 *             is tried to be read with the wrong format
	 * @throws IOException if the source file can't be read
	 */
	public static ReaderSettings load(XLSInstanceReader reader)
			throws InvalidFormatException, IOException {

		// determine file type
		boolean xlsx = isXlsxContentType(reader.getContentType());

		List<SheetInfo> sheets = new ArrayList<>();
		Map<String, Integer> indices = new HashMap<>();

		// determine basic sheet information from source file
		try (InputStream in = reader.getSource().getInput()) {
			Workbook wb = AbstractAnalyseTable.loadWorkbook(in, reader.getSource().getLocation(),
					xlsx);
			for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) {
				String sheetName = wb.getSheetName(sheet);
				boolean empty = wb.getSheetAt(sheet).getFirstRowNum() == -1;
				sheets.add(new SheetInfo(sheetName, sheet, empty));

				if (sheetName != null) {
					indices.put(sheetName, sheet);
				}
			}
		}

		// general setting for skipping lines
		int skipNlines;
		// originally the setting was a boolean to skip the first line
		Boolean skipType = reader.getParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES)
				.as(Boolean.class);

		if (skipType == null) {
			skipNlines = reader.getParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES)
					.as(Integer.class, 0);
		}
		else if (skipType) {
			skipNlines = 1;
		}
		else {
			skipNlines = 0;
		}
		// apply to all sheets as default
		for (SheetInfo sheet : sheets) {
			sheet.getSettings().setSkipLines(skipNlines);
		}

		// determine if multi sheet mode, defaults to false for backwards
		// compatibility
		boolean multiSheet = reader.getParameter(PARAMETER_MULTI_SHEET).as(Boolean.class, false);

		if (multiSheet) {
			// interpret typename parameter as comma separated list similar to
			// writer
			String typeNames = reader.getParameter(CommonSchemaConstants.PARAM_TYPENAME)
					.as(String.class);
			String[] typeNamesParts = typeNames.split(",");
			for (int i = 0; i < typeNamesParts.length && i < sheets.size(); i++) {
				QName typeName = QName.valueOf(typeNamesParts[i]);
				sheets.get(i).getSettings().setTypeName(typeName);
			}
		}
		else {
			// limit to this sheet
			int sheetNum = reader.getParameter(InstanceTableIOConstants.SHEET_INDEX).as(int.class,
					0);
			for (int i = 0; i < sheets.size(); i++) {
				sheets.get(i).getSettings().setSkipSheet(i != sheetNum);
			}

			if (sheetNum < sheets.size()) {
				QName typeName = QName.valueOf(
						reader.getParameter(CommonSchemaConstants.PARAM_TYPENAME).as(String.class));
				if (typeName != null) {
					// set type name to use
					sheets.get(sheetNum).getSettings().setTypeName(typeName);
				}
			}
		}

		// read detailed sheet settings
		Collection<? extends SheetSettings> settingList = readSheetSettings(
				reader.getParameter(PARAMETER_SHEET_SETTINGS));

		// add settings to info
		// first by index
		settingList.stream().forEach(s -> {
			Integer index = s.getIdentifiedByIndex();
			if (index != null && index < sheets.size()) {
				sheets.get(index).applySettings(s);
			}
		});
		// then by name
		settingList.stream().forEach(s -> {
			String name = s.getIdentifiedByName();
			if (name != null) {
				Integer index = indices.get(name);
				if (index != null && index < sheets.size()) {
					sheets.get(index).applySettings(s);
				}
			}
		});

		return new ReaderSettings(sheets);
	}

	/**
	 * Determine if the given content type represents an XLSX file.
	 * 
	 * @param contentType the content type to test
	 * @return if the given content type represents an XLSX file
	 */
	public static boolean isXlsxContentType(IContentType contentType) {
		if (contentType == null || contentType.getId().equals("eu.esdihumboldt.hale.io.xls.xls")) {
			return false;
		}
		return true;
	}

	private static Collection<? extends SheetSettings> readSheetSettings(Value value) {
		List<SheetSettings> settings = new ArrayList<>();

		ValueList values = value.as(ValueList.class);
		if (values != null) {
			for (Value item : values) {
				SheetSettings.fromValue(item).ifPresent(settings::add);
			}
		}

		return settings;
	}

}
