/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.align.model.annotations.messages;

import java.util.Optional;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;

/**
 * Represents a comment to a message.
 * 
 * @author Simon Templer
 */
public class Comment {

	@Nullable
	private String author;

	private String text;

	@Nullable
	private String format;

	/**
	 * Create a new comment.
	 * 
	 * @param text the comment text
	 */
	public Comment(String text) {
		super();
		this.text = text;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 * @return this for chaining
	 */
	public Comment setAuthor(String author) {
		this.author = author;
		return this;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 * @return this for chaining
	 */
	public Comment setText(String text) {
		this.text = text;
		return this;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 * @return this for chaining
	 */
	public Comment setFormat(String format) {
		this.format = format;
		return this;
	}

	/**
	 * Convert to a {@link Value}.
	 * 
	 * @return the value representation of the comment
	 */
	public Value toValue() {
		ValueProperties props = new ValueProperties();
		if (author != null) {
			props.put("author", Value.of(author));
		}
		if (text != null) {
			props.put("text", Value.of(text));
		}
		if (format != null) {
			props.put("format", Value.of(author));
		}
		return props.toValue();
	}

	/**
	 * Convert from a {@link Value}.
	 * 
	 * @param value the value to interpret as {@link Comment}
	 * @return the comment if a valid comment could be created
	 */
	@Nullable
	public static Optional<Comment> fromValue(Value value) {
		ValueProperties props = value.as(ValueProperties.class);
		if (props != null) {
			String text = props.get("text").as(String.class);
			if (text != null && !text.isEmpty()) {
				Comment comment = new Comment(text) //
						.setAuthor(props.getSafe("author").as(String.class)) //
						.setFormat(props.getSafe("format").as(String.class));
				return Optional.of(comment);
			}
		}

		return Optional.empty();
	}

}
