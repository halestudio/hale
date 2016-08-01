/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.common.cli.project;

import static eu.esdihumboldt.hale.app.transform.ExecUtil.warn;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Visitor for collecting project files.
 * 
 * @author Simon Templer
 */
public class ProjectsVisitor implements FileVisitor<Path> {

	private final List<Path> collectedFiles = new ArrayList<>();

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {
		// check all directories
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if (accept(file)) {
			collectedFiles.add(file);
		}
		return FileVisitResult.CONTINUE;
	}

	private boolean accept(Path file) {
		// check if the file is a project file

		// XXX for now simply by extension
		String fileName = file.getFileName().toString();
		return fileName.endsWith(".halex") || fileName.endsWith(".halez")
				|| fileName.endsWith(".hale");
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		// ignore, but log
		warn("Could not access file " + file);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	/**
	 * @return the list of files collected from the directory
	 */
	public List<Path> getCollectedFiles() {
		return Collections.unmodifiableList(collectedFiles);
	}
}
