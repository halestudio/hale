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

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;

/**
 * File import source to import multiple schema files.
 * 
 * @param <P> the supported {@link IOProvider} type
 * 
 * @author Kapil Agnihotri
 */
public class MultipleSchemaFilesSource<P extends ImportProvider>
		extends AbstractMultipleFilesSource<SchemaReader> {

	/**
	 * 
	 * @see eu.esdihumboldt.hale.ui.io.source.AbstractMultipleFilesSource#getSourceFile(org.eclipse.swt.widgets.Composite,
	 *      java.net.URI)
	 */
	@Override
	public AbstractMultipleFilesSourceFileFieldEditor getSourceFile(Composite parent,
			URI projectLocation) {
		return new MultipleSchemaFilesSourceFileFieldEditor("sourceFile", "Source files:",
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, parent, projectLocation,
				SWT.MULTI | SWT.SHEET);
	}
}
