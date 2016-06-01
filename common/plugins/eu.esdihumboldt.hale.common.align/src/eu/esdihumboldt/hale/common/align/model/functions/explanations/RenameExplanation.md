For each value in $_source adds the same value to the $_target property. If necessary a conversion is applied.
<% if (_params.structuralRename == 'true') {
  out << 'Furthermore child properties are copied, too, if the property names in source and target match.'
  if (_params.ignoreNamespaces == 'true') {
  	out << ' When comparing child property names, differing namespaces may be ignored.'
  }
}
if (_params.copyGeometries == 'false') {
  out << '\nGeometry objects are ignored and thus not copied.'
} %>