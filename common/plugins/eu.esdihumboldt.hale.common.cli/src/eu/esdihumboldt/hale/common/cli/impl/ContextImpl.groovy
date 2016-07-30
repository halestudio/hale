package eu.esdihumboldt.hale.common.cli.impl

import eu.esdihumboldt.hale.common.cli.CommandContext;
import groovy.transform.Immutable

@Immutable
class ContextImpl implements CommandContext {

  String baseCommand
  String commandName

}
