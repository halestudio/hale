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

package eu.esdihumboldt.hale.ui.service.project.internal.resources;

import org.joda.time.DateTime;

/**
 * Timestamped value. Comparable with newer timestamps less than older
 * timestamps.
 * 
 * @param <T> the value type
 * @author Simon Templer
 */
public class Timestamped<T> implements Comparable<Timestamped<?>> {

	private final T value;

	private final DateTime stamp;

	/**
	 * Create a timestamped value with the current timestamp.
	 * 
	 * @param value the value
	 */
	public Timestamped(T value) {
		this(value, DateTime.now());
	}

	/**
	 * Create a timestamped value with the given timestamp.
	 * 
	 * @param value the value
	 * @param stamp the timestamp
	 */
	public Timestamped(T value, DateTime stamp) {
		super();
		this.value = value;
		this.stamp = stamp;
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * @return the associated timestamp
	 */
	public DateTime getStamp() {
		return stamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stamp == null) ? 0 : stamp.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Timestamped<?> other = (Timestamped<?>) obj;
		if (stamp == null) {
			if (other.stamp != null)
				return false;
		}
		else if (!stamp.equals(other.stamp))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(Timestamped<?> o) {
		if (o == null) {
			return -1;
		}

		int result = getStamp().compareTo(o.getStamp()) * -1; // newest first
		if (result == 0 && getValue() instanceof Comparable<?>) {
			try {
				result = ((Comparable<Object>) getValue()).compareTo(o.getValue());
			} catch (Exception e) {
				// ignore
			}
		}
		return result;
	}

}
