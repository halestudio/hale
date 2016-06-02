Populates the target property $_target with the groups captured from the regular expression analysis on the source property $_source.

The regular expression analysis is carried out based on the regular expression `$_params.regexPattern`.
If a source value matches the pattern, the transformation result is constructed based on the output format `$_params.outputFormat`.
Within the given format, curly braces wrapping group numbers are replaced by the content of the respective regular expression group.