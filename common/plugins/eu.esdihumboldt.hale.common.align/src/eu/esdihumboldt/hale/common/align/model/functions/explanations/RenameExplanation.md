For each value in $_source adds the same value to the $_target property. If necessary a conversion is applied.
<% if (_params.structuralRename) {
  out << 'Furthermore child properties are copied, too, if the property names in source and target match.'
  if (_params.ignoreNamespaces) {
  	out << ' When comparing child property names, differing namespaces may be ignored.'
  }
}
if (!_params.copyGeometries) {
  out << '\nGeometry objects are ignored and thus not copied.'
} %>