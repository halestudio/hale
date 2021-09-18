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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.ui.util.io.ExtendedFileFieldEditor;

/**
 * Implementation of {@link AbstractMultipleFilesSourceFileFieldEditor} for
 * importing multiple instances with support for relative URIs with regard to
 * the current project's location.
 * 
 * @author Kapil Agnihotri
 */
public class MultipleInstanceFilesSourceFileFieldEditor
		extends AbstractMultipleFilesSourceFileFieldEditor {

	/**
	 * @see ExtendedFileFieldEditor#ExtendedFileFieldEditor(String, String,
	 *      boolean, int, Composite, int )
	 */
	@SuppressWarnings("javadoc")
	public MultipleInstanceFilesSourceFileFieldEditor(String name, String labelText,
			int validationStrategy, Composite parent, URI projectURI, int style) {
		super(name, labelText, validationStrategy, parent, projectURI, style);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.source.AbstractMultipleFilesSourceFileFieldEditor#restrictExtensions(java.lang.String)
	 */
	@Override
	protected String restrictExtensions(String path) {
		// return an empty string as for now as all the extensions are allowed
		// for reading multiple instances at once.

		List<String> filepaths = getFilepathsAsList(path);

		// check if the selected files are with different extensions.
		Set<String> allSelectedFileExtensions = filepaths.stream()
				.map(p -> p.substring(p.lastIndexOf("."))).collect(Collectors.toSet());
		if (allSelectedFileExtensions.size() > 1) {
			return "All files must be of the same format!";
		}

		// return empty string if everything is perfect. Cannot return null as
		// it will be returned from the
		// MultipleSchemaFilesSourceFileFieldEditor.
		return "";
	}

}
