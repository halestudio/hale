Fügt aus einzelnen Ordinaten aus den Quell-Attributen Punkt-Geometrien zusammen ($x ist x, $y ist y<%= z ? " und $z ist z)." : ').' %>
Die erstellten Geometrien werden im Ziel im Attribut $_target abgelegt.
<%
if (_params.referenceSystem) {
  out.print "Als Koordinaten-Referenzsystem wird ${_params.referenceSystem} verwendet."
}
%>