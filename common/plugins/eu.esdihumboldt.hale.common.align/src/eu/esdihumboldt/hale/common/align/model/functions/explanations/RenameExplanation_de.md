Für jeden Wert in $_source wird dieser im Ziel als Attribut $_target hinzugefügt.
Falls notwendig wird eine Datentyp-Konvertierung angewandt.
<% if (_params.structuralRename) {
  out << 'Außerdem werden Unterattribute kopiert, falls die jeweiligen Attributnamen in Quelle und Ziel übereinstimmen.'
  if (_params.ignoreNamespaces) {
  	out << ' Beim Vergleich der Attributnamen werden die jeweiligen Namensräume nicht berücksichtigt.'
  }
}
if (!_params.copyGeometries) {
  out << '\nGeometrie-Objekte werden nicht mit übernommen.'
} %>