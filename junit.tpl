{{- range .Results }}
  <testsuite name="{{ .Target }}" tests="{{ len .Vulnerabilities }}">
    {{- range .Vulnerabilities }}
      <testcase name="{{ .VulnerabilityID }}">
        <failure type="Vulnerability">
          <![CDATA[
            {{ .VulnerabilityID }}: {{ .Title }}
            Description: {{ .Description }}
            Severity: {{ .Severity }}
            Package: {{ .PkgName }} ({{ .InstalledVersion }})
          ]]>
        </failure>
      </testcase>
    {{- end }}
  </testsuite>
{{- end }}
