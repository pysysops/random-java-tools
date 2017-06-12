# SSL Server - Java SSL echo server
This is for testing SSL keys / certs.

# Build
```
javac SSLserver.java
```

# Run
```
java SSLserver port
```

# Use an alternative TrustStore
Pass in JVM options for various trust stores
```
java -Djavax.net.ssl.keyStore=KeyStoreName -Djavax.net.ssl.keyStorePassword=changeit SSLserver port
```

# More output
Add: `-Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.debug=ssl` to java options
