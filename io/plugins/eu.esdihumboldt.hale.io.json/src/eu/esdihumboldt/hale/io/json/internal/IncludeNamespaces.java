/*
 * Copyright (c) 2022 wetransform GmbH
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

package eu.esdihumboldt.hale.io.json.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Class that manages the namespaces and can be used to include the namespaces
 * in the geoJson or Json exported files. Not thread safe.
 *
 * @author Simon Templer
 */
public class IncludeNamespaces implements NamespaceManager {

	/**
	 * prefixes mapped to namespaces
	 */
	public final BiMap<String, String> namespaces = HashBiMap.create();

	private final String genPrefix = "n";
	private final Pattern genPattern = Pattern.compile(Pattern.quote(genPrefix) + "([0-9]+)");
	private int lastGenerated = 0;

	/**
	 * Constructor
	 */
	public IncludeNamespaces() {
		super();

		// by default add the empty namespace as default namespace
		addNamespace(XMLConstants.NULL_NS_URI, Optional.of(""));
	}

	@Override
	public Map<String, String> getNamespaces() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableMap(namespaces);
	}

	@Override
	public Optional<String> getNamespace(String prefix) {
		return Optional.ofNullable(namespaces.get(prefix));
	}

	/**
	 * @see eu.esdihumboldt.hale.io.json.internal.NamespaceManager#getPrefix(java.lang.String)
	 */
	@Override
	public String getPrefix(String namespace) {
		return addNamespace(namespace, Optional.empty());
	}

	/**
	 * @see eu.esdihumboldt.hale.io.json.internal.NamespaceManager#addNamespace(java.lang.String,
	 *      java.util.Optional)
	 */
	@Override
	public String addNamespace(String namespace, Optional<String> desiredPrefix) {
		// check existing prefix
		Optional<String> prefix = Optional.ofNullable(namespaces.inverse().get(namespace));
		return prefix.orElseGet(() -> {
			// try desired
			String desired = desiredPrefix.orElse(null);
			if (desired != null && !namespaces.containsKey(desired)) {
				namespaces.put(desired, namespace);
				return desired;
			}

			// generate numbered prefix

			// try based on last generated
			String candidate = genPrefix + (++lastGenerated);
			if (!namespaces.containsKey(candidate)) {
				namespaces.put(candidate, namespace);
				return candidate;
			}

			// otherwise, find next number
			OptionalInt max = namespaces.keySet().stream().map(p -> {
				Matcher matcher = genPattern.matcher(p);
				if (matcher.matches()) {
					String numberStr = matcher.group(1);
					return Integer.parseInt(numberStr);
				}
				else {
					return null;
				}
			}).filter(number -> number != null).mapToInt(Integer::intValue).max();
			if (max.isPresent()) {
				// try max + 1
				lastGenerated = max.getAsInt();
				candidate = genPrefix + (++lastGenerated);
				if (!namespaces.containsKey(candidate)) {
					namespaces.put(candidate, namespace);
					return candidate;
				}
			}

			throw new IllegalStateException(
					"Unable to generate namespace prefix, last candidate was: " + candidate);
		});
	}

	/**
	 * @see eu.esdihumboldt.hale.io.json.internal.NamespaceManager#setPrefix(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void setPrefix(String namespace, String prefix) {
		if (namespace == null) {
			return;
		}

		String old = namespaces.put(prefix, namespace);
		if (old != null && !old.equals(namespace)) {
			Optional<String> desiredPrefix = Optional.empty();

			// desired prefixes for specific namespaces
			switch (old) {
			case "":
				// "null namespace"
				desiredPrefix = Optional.of("x");
				break;
			}

			addNamespace(old, desiredPrefix);
		}
	}

	@Override
	public void addPrefixes(Map<String, String> prefixes) {
		prefixes.forEach((namespace, prefix) -> {
			addNamespace(namespace, Optional.ofNullable(prefix));
		});
	}

}
