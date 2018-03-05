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

package eu.esdihumboldt.hale.common.align.merge.impl

import java.util.Optional

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.headless.impl.ProjectTransformationEnvironment;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import groovy.transform.CompileStatic;;;

/**
 * Alignment migration based on a alignment representing a matching between different schemas.
 *
 * @author Simon Templer
 */
@CompileStatic
class MatchingMigration extends AbstractMigration {

  final ProjectTransformationEnvironment project

  final boolean reverse

  MatchingMigration(ProjectTransformationEnvironment project, boolean reverse = false) {
    this.project = project
    this.reverse = reverse
  }

  protected Optional<EntityDefinition> findMatch(EntityDefinition entity) {
    if (reverse) {
      // match to target
      entity = AlignmentUtil.getAllDefaultEntity(entity, true)
      entity = AlignmentUtil.applySchemaSpace(entity, SchemaSpaceID.TARGET)
    }
    Collection<? extends Cell> cells = project.alignment.getCells(entity)

    if (cells.empty) {
      //XXX no replacement can be found -> what to do in this case?
      Optional.empty()
    }
    else {
      if (cells.size() == 1) {
        Cell cell = cells.iterator().next()

        if (cell.target && !reverse) {
          // replace by target
          Entity e = cell.target.values().iterator().next()
          Optional.ofNullable(e.definition)
        }
        else if (cell.source && reverse) {
          // replace by source
          Entity e = cell.source.values().iterator().next()
          Optional.ofNullable(e.definition)
        }
        else {
          Optional.empty()
        }
      }
      else {
        //XXX more than one cell - for now ignored

        Optional.empty()
      }
    }
  }

}
