/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.io.gml.writer.internal;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.SingleTypeInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.util.Pair;

/**
 * Handler for writing instances split by feature type.
 * 
 * @author Florian Esser
 */
public class PerTypePartsHandler implements MultipartHandler {

	private final Map<TypeDefinition, URI> typeToTargetMapping;
	private final Map<String, TypeDefinition> idToTypeMapping;

	/**
	 * Create the handler
	 * 
	 * @param typeToTargetMapping Mapping between feature type and target file
	 * @param idToTypeMapping Mapping between the GML IDs and the associated
	 *            feature type
	 */
	public PerTypePartsHandler(Map<TypeDefinition, URI> typeToTargetMapping,
			Map<String, TypeDefinition> idToTypeMapping) {
		this.typeToTargetMapping = typeToTargetMapping;
		this.idToTypeMapping = idToTypeMapping;
	}

	@Override
	public String getTargetFilename(InstanceCollection part, URI location) {
		if (!(part instanceof SingleTypeInstanceCollection)) {
			throw new IllegalArgumentException(
					"This handler only supports SingleTypeInstanceCollections");
		}
		SingleTypeInstanceCollection stic = (SingleTypeInstanceCollection) part;
		// TODO Does not work for two feature types with same local part
		// but different namespace

		return getTargetFilename(stic.getType().getName(), location);
	}

	/**
	 * Build the target file name for a given type name
	 * 
	 * @param typeName Type name
	 * @param location Original target location
	 * @return The modified file name
	 */
	public static String getTargetFilename(QName typeName, URI location) {
		Path origPath = Paths.get(location).normalize();
		Pair<String, String> nameAndExt = DefaultMultipartHandler
				.getFileNameAndExtension(origPath.toString());

		return String.format("%s%s%s.%s.%s", origPath.getParent().toString(), File.separator,
				nameAndExt.getFirst(), typeName.getLocalPart(), nameAndExt.getSecond());
	}

	@Override
	public PrefixAwareStreamWriter getDecoratedWriter(PrefixAwareStreamWriter writer, URI target) {
		Map<String, URI> idToTargetMapping = idToTypeMapping.entrySet().stream().collect(
				Collectors.toMap(Map.Entry::getKey, e -> typeToTargetMapping.get(e.getValue())));
		LocalReferenceUpdater updater = new LocalReferenceUpdater(idToTargetMapping, target);

		return new ReferenceUpdatingStreamWriter(writer, updater);
	}
}