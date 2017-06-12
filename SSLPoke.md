# SSL Poke - Java SSL test utility
This is for testing raw TCP / SSL connectivity and won't for HTTPS through proxies

# Build
```
javac SSLPoke.java
```

# Run
```
java SSLPoke server port
```

# Use an alternative TrustStore
Pass in JVM options for various trust stores
```
java -Djavax.net.ssl.trustStore=/path/to/truststore.jks -Djavax.net.ssl.trustStorePassword=changeit SSLPoke server port
```

# More output
Add: `-Djavax.net.debug=all` to java options

# Example
```
$ java -Djavax.net.debug=all SSLPoke gerrit.cdlis.co.uk 443
javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
        at sun.security.ssl.Alerts.getSSLException(Unknown Source)
        at sun.security.ssl.SSLSocketImpl.fatal(Unknown Source)
        at sun.security.ssl.Handshaker.fatalSE(Unknown Source)
        at sun.security.ssl.Handshaker.fatalSE(Unknown Source)
        at sun.security.ssl.ClientHandshaker.serverCertificate(Unknown Source)
        at sun.security.ssl.ClientHandshaker.processMessage(Unknown Source)
        at sun.security.ssl.Handshaker.processLoop(Unknown Source)
        at sun.security.ssl.Handshaker.process_record(Unknown Source)
        at sun.security.ssl.SSLSocketImpl.readRecord(Unknown Source)
        at sun.security.ssl.SSLSocketImpl.performInitialHandshake(Unknown Source)
        at sun.security.ssl.SSLSocketImpl.writeRecord(Unknown Source)
        at sun.security.ssl.AppOutputStream.write(Unknown Source)
        at sun.security.ssl.AppOutputStream.write(Unknown Source)
        at SSLPoke.main(SSLPoke.java:29)
Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
        at sun.security.validator.PKIXValidator.doBuild(Unknown Source)
        at sun.security.validator.PKIXValidator.engineValidate(Unknown Source)
        at sun.security.validator.Validator.validate(Unknown Source)
        at sun.security.ssl.X509TrustManagerImpl.validate(Unknown Source)
        at sun.security.ssl.X509TrustManagerImpl.checkTrusted(Unknown Source)
        at sun.security.ssl.X509TrustManagerImpl.checkServerTrusted(Unknown Source)
        ... 10 more
Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
        at sun.security.provider.certpath.SunCertPathBuilder.build(Unknown Source)
        at sun.security.provider.certpath.SunCertPathBuilder.engineBuild(Unknown Source)
        at java.security.cert.CertPathBuilder.build(Unknown Source)
        ... 16 more
```
