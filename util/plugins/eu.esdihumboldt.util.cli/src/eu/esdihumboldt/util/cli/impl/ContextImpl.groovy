package eu.esdihumboldt.util.cli.impl

import eu.esdihumboldt.util.cli.CommandContext
import groovy.transform.Immutable

@Immutable
class ContextImpl implements CommandContext {

	String baseCommand
	String commandName
}
