On $_target property, function generates the sequential identifier with configuration, 
Sequence: <% if (_params.sequence == "overall") { out << "\'Over all sequential IDs\'" } else {  out << "\'Per target instance type\'"}%>  
Prefix: <% out << "\'${_params.prefix}\'" %> 
Suffix: <% out << "\'${_params.suffix}\'" %>