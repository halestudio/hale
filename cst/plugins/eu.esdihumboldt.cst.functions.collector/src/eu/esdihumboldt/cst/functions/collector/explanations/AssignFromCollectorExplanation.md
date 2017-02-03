<%
	if (!_constraintsEvaluated) {
		out << "Assigns all values of the collector `$_params.collector` to the $_target property.\n\nIf the type of $_target is a reference type, the collected values will be converted to references before they are assigned.\n\nIn case the collected values cannot be assigned to $_target directly, a child attribute where references can be assigned is required. Then, for every collected value in the collector `$_params.collector` an instance of $_target is created and the value is assigned to the child attribute. If no such reference child attribute is found, the transformation will fail."
	}
	else if (_isReference) {
		out << "Assigns all values of the collector `$_params.collector` to the $_target property as references.\n\nIf the collected values are valid XML Ids, they will be converted to local references by prepending the '#' character."
	}
	else if (_hasValue) {
		out << "Assigns all values of the collector `$_params.collector` to the $_target property."
	}
	else {
		out << "Assigns all values of the collector `$_params.collector` to a child property of $_target that can be assigned references (e.g. `href`).\n\nFor every collected value an instance of $_target is created and the value is assigned to the child property. If the collected values are valid XML Ids, they will be converted to local references by prepending the '#' character.\n\nIf there is no child property for references, the transformation will fail."
	}
%>
