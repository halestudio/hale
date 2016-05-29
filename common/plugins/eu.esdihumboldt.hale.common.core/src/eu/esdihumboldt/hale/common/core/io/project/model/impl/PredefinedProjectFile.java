/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.io.project.model.impl;

import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.io.ByteStreams;

import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.util.io.InputSupplier;

/**
 * Project file that takes its content from an existing source. This kind of
 * project file is only usable for writing a project file, e.g. when generating
 * a project.
 * 
 * @author Simon Templer
 */
public class PredefinedProjectFile implements ProjectFile {

	private final InputSupplier<? extends InputStream> source;

	/**
	 * Create a pre-defined project file with the given input supplier
	 * specifying the file content.
	 * 
	 * @param source the file content source
	 */
	public PredefinedProjectFile(InputSupplier<? extends InputStream> source) {
		this.source = source;
	}

	@Override
	public void load(InputStream in) throws Exception {
		throw new UnsupportedOperationException("Project file only applicable for writing");
	}

	@Override
	public void reset() {
		// ignore
	}

	@Override
	public void apply() {
		// ignore
	}

	@Override
	public void store(LocatableOutputSupplier<OutputStream> target) throws Exception {
		try (OutputStream out = target.getOutput(); InputStream in = source.getInput()) {
			ByteStreams.copy(in, out);
		}
	}

}
