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

package eu.esdihumboldt.hale.common.align.io;

import java.io.InputStream;
import java.io.OutputStream;

import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentIO;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Alignment I/O tests based on {@link JaxbAlignmentIO}.
 * 
 * @author Simon Templer
 */
public class JaxbAlignmentIOTest extends DefaultAlignmentIOTest {

	@Override
	protected MutableAlignment loadAlignment(InputStream input, TypeIndex source, TypeIndex target)
			throws Exception {
		return JaxbAlignmentIO.load(input, null, source, target);
	}

	@Override
	protected void saveAlignment(MutableAlignment align, OutputStream output) throws Exception {
		JaxbAlignmentIO.save(align, null, output);
	}

	@Override
	protected boolean supportsAnnotations() {
		return true;
	}

	@Override
	protected boolean supportsDocumentation() {
		return true;
	}

	@Override
	protected boolean supportsComplexParameters() {
		return true;
	}

}
