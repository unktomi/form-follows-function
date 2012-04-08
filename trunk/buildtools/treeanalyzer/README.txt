F3 ASTs can be analyzed using AST/XML facility. When compiler is
invoked with -XDtreexsl=<.xsl> option, the compiler generates XML
view of AST and invokes user supplied XSL sheet(s) on those.

This may be used to measure various source metrics like number of binds 
(local, instance/static, object literal binds) and so on. When such a
measurement is done for a project containing many .f3 source files, each 
XSL application on a compilation unit can dump statistic for that specific
compilation unit into a file. For example, "bindstat.xsl" measures number
of binds and outputs the same into a set of properties. Such measurements
can be combined to get project-wide stat using PropsCombiner.java in this
directory.
