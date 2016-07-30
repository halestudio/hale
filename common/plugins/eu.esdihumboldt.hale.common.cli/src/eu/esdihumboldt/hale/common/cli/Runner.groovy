package eu.esdihumboldt.hale.common.cli

import eu.esdihumboldt.hale.common.cli.bash.BashCompletion
import eu.esdihumboldt.hale.common.cli.extension.GroupCommand
import eu.esdihumboldt.hale.common.cli.impl.ContextImpl
import groovy.transform.CompileStatic

@CompileStatic
class Runner extends GroupCommand {

	private final String baseCommand

	Runner(String baseCommand) {
		this.baseCommand = baseCommand
	}

	final String shortDescription = 'hale command line utility'

	int run(String[] args) {
		CommandContext context = new ContextImpl(baseCommand, null)

		if (args) {
			// support --version
			if (args[0] == '--version') {
				args[0] = 'version'
			}

			// helper for bash completion
			if (args[0] == '--complete') {
				// next arg must be index of word to complete
				int currentWord
				try {
					currentWord = args[1] as int
				} catch (e) {
					return 1
				}

				// determine list of words (strip "--complete <index>")
				def words = args.length > 2 ? args[2..-1] as List : []

				// if current word is not included, add empty string as argument
				if (words.size() == currentWord) {
					words << ''
				}

				// strip first word (which is the base command)
				words = words.size() > 1 ? words[1..-1] : []

				BashCompletion completion = bashCompletion((List<String>) words, currentWord - 1)
				if (completion) {
					println completion.command
					return 0
				}
				else {
					return 1
				}
			}
		}

		run(args as List, context)
	}

}
