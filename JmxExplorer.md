# JMX Explorer - CLI Utility to explore JMX endpoints
For exploring when all you have is a command prompt and java from command-line

# Build
```
javac JmxExplorer.java
```

# Usage
```
USAGE: java JmxExplorer options command

  options:
    -U|--url: JMX url to connect to (default: service:jmx:rmi:///jndi/rmi://:9010/jmxrmi)
    -u|--username: username for the JMX connection (deafult: null)
                   Use "-" to request the password interactively
    -p|--password: password for the JMX connection (deafult: null)

  command:
    local: list the local JMVs in the machine (pid, JMX url and command line)
           "tools.jar" is needed, please add ${JAVA_HOME}/lib/tools.jar to the classpath
    domains: list the JMX domains that exist in the server
    mbeans [<pattern>]: list the mbeans in the server (if pattern is specified
                        only the ones that matches it)
    info <mbean>: list the attribute names of the mbean specified
    attrs <mbean> [<attr>...]: list the attribute values of the mbean specified (if
                               no attrs are specified all attributes are returned)
```
