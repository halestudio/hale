Analysiert den Wert des Quell-Attibuts $_source anhand des regulären Ausdrucks `$_params.regexPattern`.
Wird eine Übereinstimmung gefunden wird diese anhand des Musters `$_params.outputFormat` umformatiert.
In geschwungenen Klammern eingebetette Nummern werden dabei durch den Inhalt der entsprechenden *Gruppe* des regulären Ausdrucks ersetzt.
Das Ergebnis wird im Ziel-Attribut $_target abgelegt.