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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;

/**
 * Represents a message.
 * 
 * @author Simon Templer
 */
public class Message {

	private String text;

	@Nullable
	private String author;

	@Nullable
	private String category;

	@Nullable
	private String format;

	private boolean dismissed = false;

	private final List<String> tags = new ArrayList<>();

	private final List<Comment> comments = new ArrayList<>();

	@Nullable
	private Value customPayload;

	/**
	 * Create a new message.
	 * 
	 * @param text the message text
	 */
	public Message(String text) {
		super();
		this.text = text;
	}

	/**
	 * Create a new empty message.
	 */
	public Message() {
		super();
	}

	/**
	 * @return if the message was dismissed
	 */
	public boolean isDismissed() {
		return dismissed;
	}

	/**
	 * @param dismissed if the message was dismissed
	 * @return this for chaining
	 */
	public Message setDismissed(boolean dismissed) {
		this.dismissed = dismissed;
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
	public Message setText(String text) {
		this.text = text;
		return this;
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
	public Message setAuthor(String author) {
		this.author = author;
		return this;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 * @return this for chaining
	 */
	public Message setCategory(String category) {
		this.category = category;
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
	public Message setFormat(String format) {
		this.format = format;
		return this;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @return the comments
	 */
	public List<Comment> getComments() {
		return comments;
	}

	/**
	 * @param tag the tag to add
	 * @return this for chaining
	 */
	public Message addTag(String tag) {
		tags.add(tag);
		return this;
	}

	/**
	 * @param comment the comment to add
	 * @return this for chaining
	 */
	public Message addComment(Comment comment) {
		comments.add(comment);
		return this;
	}

	/**
	 * @return the custom payload
	 */
	public Value getCustomPayload() {
		return customPayload;
	}

	/**
	 * @param customPayload the custom payload to set
	 * @return this for chaining
	 */
	public Message setCustomPayload(Value customPayload) {
		this.customPayload = customPayload;
		return this;
	}

	/**
	 * Convert to a {@link Value}.
	 * 
	 * @return the value representation of the comment
	 */
	public ValueProperties toProperties() {
		ValueProperties props = new ValueProperties();
		if (author != null) {
			props.put("author", Value.of(author));
		}
		if (text != null) {
			props.put("text", Value.of(text));
		}
		if (format != null) {
			props.put("format", Value.of(format));
		}
		if (category != null) {
			props.put("category", Value.of(category));
		}
		if (dismissed) {
			props.put("dismissed", Value.of(dismissed));
		}
		if (customPayload != null) {
			props.put("payload", customPayload);
		}
		if (!tags.isEmpty()) {
			ValueList tagList = new ValueList(
					tags.stream().map(tag -> Value.of(tag)).collect(Collectors.toList()));
			props.put("tags", tagList.toValue());
		}
		if (!comments.isEmpty()) {
			ValueList commentList = new ValueList(comments.stream()
					.map(comment -> Value.of(comment)).collect(Collectors.toList()));
			props.put("comments", commentList.toValue());
		}
		return props;
	}

	/**
	 * Apply from a given {@link Value}, if the value represents a
	 * {@link Message}.
	 * 
	 * @param value the value to interpret as {@link Message}
	 * @return this for chaining
	 */
	@Nullable
	public Message applyFromValue(Value value) {
		ValueProperties props = value.as(ValueProperties.class);
		if (props != null) {
			String text = props.get("text").as(String.class);
			if (text != null && !text.isEmpty()) {
				setText(text) //
						.setAuthor(props.getSafe("author").as(String.class)) //
						.setFormat(props.getSafe("format").as(String.class)) //
						.setCategory(props.getSafe("category").as(String.class))//
						.setDismissed(props.getSafe("dismissed").as(Boolean.class, false)) //
						.setCustomPayload(props.get("payload"));

				ValueList tags = props.getSafe("tags").as(ValueList.class);
				this.tags.clear();
				if (tags != null) {
					for (Value tag : tags) {
						String tagString = tag.as(String.class);
						if (tagString != null) {
							addTag(tagString);
						}
					}
				}

				ValueList comments = props.getSafe("comments").as(ValueList.class);
				this.comments.clear();
				if (comments != null) {
					for (Value commentVal : comments) {
						Optional<Comment> comment = Comment.fromValue(commentVal);
						comment.ifPresent(c -> addComment(c));
					}
				}

			}
		}

		return this;
	}

}
