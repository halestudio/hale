<%
	if (!_constraintsEvaluated) {
		out << "Weist dem Attribut $_target alle Werte des Collectors `$_params.collector` zu.\n\nFalls der Typ von $_target ein Referenztyp ist, werden die vom Collector gesammelten Werte vor ihrer Zuweisung in Referenzen umgewandelt.\n\nFalls das Attribut $_target die gesammelten Werte nicht selbst aufnehmen kann, muss ein Kind-Attribut existieren, das Referenzen aufnehmen kann. Es wird dann für jeden gesammelten Wert im Collectors `$_params.collector` eine Instanz von $_target erzeugt und der Wert dem Kind-Attribut zugewiesen. Gibt es kein passendes Kind-Attribut, schlägt die Transformation fehl."
	}
	else if (_isReference) {
		out << "Weist dem Attribut $_target alle Werte des Collectors `$_params.collector` als Referenzen zu.\n\nWenn es sich bei den gesammelten Werten um gültige XML-Ids handelt, werden sie durch Voranstellen des Zeichens '#' in lokale Referenzen umgewandelt."
	}
	else if (_hasValue) {
		out << "Weist dem Attribut $_target alle Werte des Collectors `$_params.collector` zu."
	}
	else {
		out << "Weist einem Kind-Attribut von $_target, das Referenzen aufnehmen kann (z. B. falls vorhanden `href`), alle Werte des Collectors `$_params.collector` als Referenzen zu.\n\nEs wird für jeden gesammelten Wert im Collectors `$_params.collector` eine Instanz von $_target erzeugt und der Wert dem Kind-Attribut zugewiesen. Handelt es sich bei dem Wert um eine gültige XML-Id, wird er durch Voranstellen des Zeichens '#' in eine lokale Referenz umgewandelt.\n\nGibt es kein passendes Kind-Attribut, schlägt die Transformation fehl."
	}
%>
