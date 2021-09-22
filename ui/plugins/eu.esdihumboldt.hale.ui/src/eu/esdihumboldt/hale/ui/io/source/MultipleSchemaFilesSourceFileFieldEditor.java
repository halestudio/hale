/*
 * Copyright (c) 2021 wetransform GmbH
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

package eu.esdihumboldt.hale.ui.io.source;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.ui.util.io.ExtendedFileFieldEditor;

/**
 * Implementation of {@link AbstractMultipleFilesSourceFileFieldEditor} for
 * importing multiple Schema(s).
 * 
 * @author Kapil Agnihotri
 */
public class MultipleSchemaFilesSourceFileFieldEditor
		extends AbstractMultipleFilesSourceFileFieldEditor {

	/**
	 * @see ExtendedFileFieldEditor#ExtendedFileFieldEditor(String, String,
	 *      boolean, int, Composite, int )
	 */
	@SuppressWarnings("javadoc")
	public MultipleSchemaFilesSourceFileFieldEditor(String name, String labelText,
			int validationStrategy, Composite parent, URI projectURI, int style) {
		super(name, labelText, validationStrategy, parent, projectURI, style);
	}

	/**
	 * 
	 * @see eu.esdihumboldt.hale.ui.io.source.AbstractMultipleFilesSourceFileFieldEditor#restrictExtensions(java.lang.String)
	 */
	@Override
	protected String restrictExtensions(String path) {
		String msg = null;

		List<String> filepaths = getFilepathsAsList(path);
		// check if the selected files are with different extensions.
		Set<String> allSelectedFileExtensions = filepaths.stream()
				.map(p -> p.substring(p.lastIndexOf("."))).collect(Collectors.toSet());
		if (allSelectedFileExtensions.size() > 1) {
			msg = "All files must be of the same format!";
			return msg;
		}

		// check if selected files are of the extensions that doesn't allow
		// multiple schema imports.
		List<String> extensions = Arrays.asList(".csv", ".xml");
		long count = filepaths.stream().filter(p -> extensions.stream().anyMatch(p::contains))
				.count();
		if (filepaths.size() > 1 && count > 1) {
			msg = String.format("Cannot import multiple files of %s extension.",
					allSelectedFileExtensions.iterator().next());
		}
		return msg;
	}

}
