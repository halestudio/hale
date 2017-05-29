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

package eu.esdihumboldt.cst.functions.geometric.join;

import java.util.function.BiFunction;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Interface for functions evaluating a spatial relation between two geometries.
 * 
 * @author Florian Esser
 */
public interface SpatialRelationEvaluator {

	/**
	 * Evaluators for typical spatial relations
	 * 
	 * @author Florian Esser
	 */
	public static enum StandardRelation {

		/**
		 * Checks if the f geometry contains the second geometry
		 */
		CONTAINS(build("contains", (f, s) -> {
			return f.contains(s);
		})),

		/**
		 * Checks if the f geometry is covered by the second geometry
		 */
		COVERED_BY(build("covered by", (f, s) -> {
			return f.coveredBy(s);
		})),

		/**
		 * Checks if the f geometry covers the second geometry
		 */
		COVERS(build("covers", (f, s) -> {
			return f.covers(s);
		})),

		/**
		 * Checks if the f geometry crosses the second geometry
		 */
		CROSSES(build("crosses", (f, s) -> {
			return f.crosses(s);
		})),

		/**
		 * Checks if the f geometry is equal to the second geometry
		 */
		EQUALS(build("equals", (f, s) -> {
			return f.equals(s);
		})),

		/**
		 * Checks if the f geometry intersects the second geometry
		 */
		INTERSECTS(build("intersects", (f, s) -> {
			return f.intersects(s);
		})),

		/**
		 * Checks if the f geometry overlaps the second geometry
		 */
		OVERLAPS(build("overlaps", (f, s) -> {
			return f.overlaps(s);
		})),

		/**
		 * Checks if the f geometry touches the second geometry
		 */
		TOUCHES(build("touches", (f, s) -> {
			return f.touches(s);
		})),

		/**
		 * Checks if the f geometry lies within the second geometry
		 */
		WITHIN(build("within", (f, s) -> {
			return f.within(s);
		}));

		private final SpatialRelationEvaluator evaluator;

		private StandardRelation(SpatialRelationEvaluator e) {
			this.evaluator = e;
		}

		/**
		 * @return the spatial relation evaluator
		 */
		public SpatialRelationEvaluator relation() {
			return evaluator;
		}

		/**
		 * Returns the {@link StandardRelation} that has the given title.
		 * 
		 * @param relation Title of the relation to return
		 * @return the {@link StandardRelation} with the given title or null if
		 *         none exists
		 */
		public static StandardRelation valueOfOrNull(String relation) {
			try {
				return StandardRelation.valueOf(relation);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
	}

	/**
	 * Evaluate the spatial relation for the given geometries
	 * 
	 * @param first The f geometry
	 * @param second The second geometry
	 * @return true if the spatial relation exists between the given geometries
	 */
	boolean evaluate(Geometry first, Geometry second);

	/**
	 * @return the displayable name of the spatial relation that is evaluated
	 */
	String getDescription();

	/**
	 * Builds a {@link SpatialRelationEvaluator} for a specific evaluation
	 * function.
	 * 
	 * @param description Description of the spatial relation evaluation, e.g.
	 *            "covers"
	 * @param evaluatorFunc Evaluation function
	 * @return the built <code>SpatialRelationEvaluator</code>
	 */
	static SpatialRelationEvaluator build(final String description,
			BiFunction<Geometry, Geometry, Boolean> evaluatorFunc) {
		return new SpatialRelationEvaluator() {

			@Override
			public String getDescription() {
				return description;
			}

			@Override
			public boolean evaluate(Geometry first, Geometry second) {
				return evaluatorFunc.apply(first, second);
			}
		};
	}
}
