Fills the $_target property with point geometries created from source properties, where the $x property is x, the $y property is y<%= z ? " and the $z property is z." : '.' %>
<%
if (_params.referenceSystem) {
  out.print "The reference system ${_params.referenceSystem} is used."
}
%>