Für jeden Wert in $_source wird dieser im Ziel als Eigenschaft $_target hinzugefügt.
Falls notwendig wird eine Datentyp-Konvertierung angewandt.
<% if (_params.structuralRename) {
  out << 'Außerdem werden Untereigenschaften ebenfalls kopiert, falls die jeweiligen Eigenschaftsnamen in Quelle und Ziel übereinstimmen.'
  if (_params.ignoreNamespaces) {
  	out << ' Beim Vergleich der Eigenschaftsnamen werden die jeweiligen Namensräume nicht berücksichtigt.'
  }
}
if (!_params.copyGeometries) {
  out << '\nGeometrie-Objekte werden nicht mit übernommen.'
} %>