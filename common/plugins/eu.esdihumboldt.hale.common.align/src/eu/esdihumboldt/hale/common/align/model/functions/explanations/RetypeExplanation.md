Creates a $_target instance for each $_source instance in the source data set.
<% if (_params.structuralRename) {
  out << 'Furthermore child properties are copied, too, if the property names in source and target match.'
  if (_params.ignoreNamespaces) {
  	out << ' When comparing child property names, differing namespaces may be ignored.'
  }
  if (!_params.copyGeometries) {
    out << '\nGeometry objects are ignored and thus not copied.'
  }
} %>