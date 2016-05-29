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
import java.net.URI;

import eu.esdihumboldt.hale.common.align.io.impl.CastorAlignmentIO;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.report.impl.DefaultIOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Alignment I/O tests based on {@link CastorAlignmentIO}.
 * 
 * @author Simon Templer
 */
public class CastorAlignmentIOTest extends DefaultAlignmentIOTest {

	@Override
	protected MutableAlignment loadAlignment(InputStream input, TypeIndex source, TypeIndex target)
			throws Exception {
		// assume no location update needed
		return CastorAlignmentIO.load(input, null, source, target, new PathUpdate(null, null));
	}

	@Override
	protected void addBaseAlignment(MutableAlignment align, final URI newBase, TypeIndex source,
			TypeIndex target) throws Exception {
		CastorAlignmentIO.addBaseAlignment(align, newBase, null, source, target,
				new DefaultIOReporter(new Locatable() {

					@Override
					public URI getLocation() {
						return newBase;
					}
				}, "addBaseAlignment", true));
	}

	@Override
	protected void saveAlignment(MutableAlignment align, OutputStream output) throws Exception {
		CastorAlignmentIO.save(align, output, new PathUpdate(null, null));
	}

	@Override
	protected boolean supportsAnnotations() {
		return false;
	}

	@Override
	protected boolean supportsDocumentation() {
		return false;
	}

	@Override
	protected boolean supportsComplexParameters() {
		return false;
	}

	@Override
	protected boolean supportsTransformationModes() {
		return false;
	}

}
