
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.MBeanInfo;
import java.util.Set;
import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;

/**
 * <p>Little class that explores a JMX server in order to read the attributes
 * that are defined in its beans. It is intended to be used as a simple
 * command interface. Their usage is the following:</p>
 *
 * <pre>
 * USAGE: java JmxExplorer options command
 *
 *  options:
 *    -U|--url: JMX url to connect to (default: service:jmx:rmi:///jndi/rmi://:9010/jmxrmi)
 *    -u|--username: username for the JMX connection (deafult: null)
 *    -p|--password: password for the JMX connection (deafult: null)
 *
 *  command:
 *    domains: list the JMX domains that exist in the server
 *    mbeans [&ltpattern&gt;]: list the mbeans in the server (if pattern is specified
 *                        only the ones that matches it)
 *    info &ltmbean&gt;: list the attribute names of the mbean specified
 *    attrs &ltmbean&gt; [&ltattr&gt;...]: list the attribute values of the mbean specified (if
 *                               no attrs are specified all attriutes are returned)
 * </pre>
 *
 * <p>The command lets you know the domains in the JMX server, list the mbeans
 * contained in it (you can filter using a pattern), get the attributes of a
 * bean and get the values of some bean attributes.</p>
 *
 * @author ricky
 */
public class JmxExplorer implements Closeable {

    //
    // CONSTANTS
    //

    /**
     * Default JMX url (localhost, port 9010)
     */
    static public final String DEFAULT_URL = "service:jmx:rmi:///jndi/rmi://:9010/jmxrmi";

    /**
     * Default username (no username)
     */
    static public final String DEFAULT_USERNAME = null;

    /**
     * Default password (no password)
     */
    static public final String DEFAULT_PASSWORD = null;

    /**
     * Default pattern to search in mbeans command (*:* means all mbeans)
     */
    static public final String DEFAULT_OBJECT_NAME = "*:*";

    //
    // OPTIONS
    //

    /**
     * the real jmx url to use
     */
    static private String url = DEFAULT_URL;

    /**
     * The real username to use
     */
    static private String username = DEFAULT_URL;

    /**
     * The real password to use
     */
    static private String password = DEFAULT_PASSWORD;

    /**
     * the real meban object or pattenr to use
     */
    static private String objName = DEFAULT_OBJECT_NAME;

    //
    // conection properties
    //

    /**
     * The jmx connector
     */
    JMXConnector jmxc = null;

    /**
     * The mbean connection
     */
    MBeanServerConnection mbsc = null;

    //
    // CONSTRUCTORS
    //

    /**
     * Constructor using the properties.
     * @param url The url to connect
     * @param username The username to use (null means no username/password)
     * @param password The password to use (null means no username/password)
     * @throws MalformedURLException Some error with the URL
     * @throws IOException Some exception reading the JMX
     */
    public JmxExplorer(String url, String username, String password) throws MalformedURLException, IOException {
        JMXServiceURL jmxUrl = new JMXServiceURL(url);
        Map<String, Object> env = null;
        if (username != null && !username.isEmpty()
                && password != null && !password.isEmpty()) {
            env = new HashMap<>(1);
            env.put(JMXConnector.CREDENTIALS, new String[]{username, password});

        }
        jmxc = JMXConnectorFactory.connect(jmxUrl, env);
        mbsc = jmxc.getMBeanServerConnection();
    }

    //
    // STATIC METHODS
    //

    /**
     * Throws a IllegalArgumentException writing the usage of the command.
     * @param message The local error to show
     */
    static public void usage(String message) {
        throw new IllegalArgumentException(
                new StringBuilder(System.lineSeparator())
                .append(System.lineSeparator())
                .append("ERROR: ").append(message)
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("USAGE: java JmxExplorer options command")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("  options:")
                .append(System.lineSeparator())
                .append("    -U|--url: JMX url to connect to (default: ").append(DEFAULT_URL).append(")")
                .append(System.lineSeparator())
                .append("    -u|--username: username for the JMX connection (deafult: ").append(DEFAULT_USERNAME).append(")")
                .append(System.lineSeparator())
                .append("                   Use \"-\" to request the password interactively")
                .append(System.lineSeparator())
                .append("    -p|--password: password for the JMX connection (deafult: ").append(DEFAULT_PASSWORD).append(")")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("  command:")
                .append(System.lineSeparator())
                .append("    local: list the local JMVs in the machine (pid, JMX url and command line)")
                .append(System.lineSeparator())
                .append("           \"tools.jar\" is needed, please add ${JAVA_HOME}/lib/tools.jar to the classpath")
                .append(System.lineSeparator())
                .append("    domains: list the JMX domains that exist in the server")
                .append(System.lineSeparator())
                .append("    mbeans [<pattern>]: list the mbeans in the server (if pattern is specified ")
                .append(System.lineSeparator())
                .append("                        only the ones that matches it)")
                .append(System.lineSeparator())
                .append("    info <mbean>: list the attribute names of the mbean specified")
                .append(System.lineSeparator())
                .append("    attrs <mbean> [<attr>...]: list the attribute values of the mbean specified (if ")
                .append(System.lineSeparator())
                .append("                               no attrs are specified all attributes are returned)")
                .append(System.lineSeparator())
                .toString()
        );
    }

    /**
     * Method that checks if there is an argument at position i.
     * @param args The list of arguments
     * @param i The position
     * @return true if args[i] contains an argument, false otherwise
     */
    static public boolean checkArgument(String[] args, int i) {
        return i < args.length;
    }

    /**
     * Method that returns the argument in position i if it exists. If there is
     * no argument at that position usage is invoked and program terminated.
     * @param arg The argument name we are looking for the value
     * @param args The list of arguments
     * @param i The position
     * @return The argument value or usage exception
     */
    static public String parseString(String arg, String[] args, int i) {
        if (!checkArgument(args, i)) {
            usage("The argument " + arg + " should have an argument value");
        }
        return args[i];
    }

    /**
     * Method that parses the arguments and fills the static properties to call
     * the JMX server. it must be called at the beginning of the program.
     * @param args The list of arguments
     * @return the position when all arguments (-X or --xxxx) are read
     */
    static public int parseArguments(String[] args) {
        int i = 0;
        while (i < args.length && args[i].startsWith("-")) {
            switch (args[i]) {
                case "-U":
                case "--url":
                    url = parseString(args[i], args, ++i);
                    break;
                case "-u":
                case "--username":
                    username = parseString(args[i], args, ++i);
                    break;
                case "-p":
                case "--password":
                    password = parseString(args[i], args, ++i);
                    if ("-".equals(password)) {
                        // request using command line
                        password = new String(System.console().readPassword("Password: "));
                    }
                    break;
                default:
                    usage("Invalid option " + args[i]);
            }
            i++;
        }
        return i;
    }

    /**
     * Method that uses the attach API to show the list of Java VMs that are
     * running in the machine. It needs the "tools.jar" in the classpath. But
     * reflection is used in order to not need this package for compilation.
     * If this command is executed without that JAR an error is thrown.
     *
     * @throws Exception Some error with reflection
     */
    static public void listVMUrls() throws Exception {
        try {
            Class vmClazz = Class.forName("com.sun.tools.attach.VirtualMachine");
            Class vmDescClazz = Class.forName("com.sun.tools.attach.VirtualMachineDescriptor");
            Method vmClazzList = vmClazz.getMethod("list");
            List<Object> vmDescList = (List<Object>) vmClazzList.invoke(null);
            if (vmDescList != null) {
                for (Object vmDesc : vmDescList) {
                    try {
                        Method vmClazzAttach = vmClazz.getMethod("attach", vmDescClazz);
                        Object vm = vmClazzAttach.invoke(null, vmDesc);
                        Method vmClazzAgentProperties = vmClazz.getMethod("getAgentProperties");
                        Properties props = (Properties) vmClazzAgentProperties.invoke(vm);
                        String jmxUrl = props.getProperty("com.sun.management.jmxremote.localConnectorAddress");
                        String command = props.getProperty("sun.java.command");
                        Method vmClazzId = vmClazz.getMethod("id");
                        Object id = vmClazzId.invoke(vm);
                        System.out.println(String.format("%s %s %s", id, (jmxUrl == null) ? "-" : jmxUrl, command));
                    } catch (Exception e) {
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            usage("Listing VMs needs the \"tools.jar\" in the classpath (${JAVA_HOME}/lib/tools.jar).");
        }
    }

    //
    // COMMON METHODS
    //

    /**
     * Method that returns the domains in the server.
     * @return The list of domains in the jmx server
     * @throws Exception Some errror
     */
    public Set<String> listDomains() throws Exception {
        return new HashSet<>(Arrays.asList(mbsc.getDomains()));
    }

    /**
     * Method to list the mbeans in the jmx server based on a pattern.
     * @param name The pattern to search
     * @return The names of the objects
     * @throws Exception Some error
     */
    public Set<ObjectName> listMBeans(String name) throws Exception {
        return mbsc.queryNames(new ObjectName(name), null);
    }

    /**
     * Method to list the attribute definition of a mbean.
     * @param name The name of the mbean
     * @return The list of attribute definitions
     * @throws Exception Some error
     */
    public Set<MBeanAttributeInfo> listAttributes(String name) throws Exception {
        MBeanInfo info = mbsc.getMBeanInfo(new ObjectName(name));
        return new HashSet<>(Arrays.asList(info.getAttributes()));
    }

    /**
     * Method to retrieve the values of a list of attributes in ambean.
     * @param name The name of the mbean
     * @param attr The array of attribute names to retrieve
     * @return The list of attributes returned by the jmx server
     * @throws Exception Some error
     */
    public Set<Attribute> getAttributes(String name, String... attr) throws Exception {
        return new HashSet<>(mbsc.getAttributes(new ObjectName(name), attr).asList());
    }

    /**
     * Method to retrieve all the attribute values in a mbean.
     * @param name The name of the mbean
     * @return The list of attributes of the mbean
     * @throws Exception Some error
     */
    public Set<Attribute> getAttributes(String name) throws Exception {
        Set<MBeanAttributeInfo> attrs = listAttributes(name);
        List<String> names = new ArrayList<>(attrs.size());
        for (MBeanAttributeInfo attr : attrs) {
            names.add(attr.getName());
        }
        return getAttributes(name, names.toArray(new String[0]));
    }

    /**
     * The close method
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        jmxc.close();
    }

    /**
     * main method to execute. It parses the command line, creates the
     * JmxExplorer and calls the proper method.
     * @param args The arguments
     * @throws Exception Some error
     */
    public static void main(String[] args) throws Exception {
        // parse the arguments
        int i = parseArguments(args);
        // now the arguments can be
        //   domains
        //   mbeans [name]
        //   info name
        //   attr name [attr...]
        if (i < args.length) {
            if ("local".equals(args[i])) {
                listVMUrls();
            } else {
                // create the explorer
                try (JmxExplorer jmx = new JmxExplorer(url, username, password)) {
                    switch (args[i]) {
                        case "domains":
                            // list the domains in the server
                            for (String domain : jmx.listDomains()) {
                                System.out.println(domain);
                            }
                            break;
                        case "mbeans":
                            // list mbeans, a pattern is optional
                            if (checkArgument(args, ++i)) {
                                objName = parseString("mbeans", args, i);
                            }
                            for (ObjectName name : jmx.listMBeans(objName)) {
                                System.err.println(name);
                            }
                            break;
                        case "info":
                            // get the info from a mbean
                            objName = parseString("info", args, ++i);
                            for (MBeanAttributeInfo attr : jmx.listAttributes(objName)) {
                                System.err.println(attr.getName() + "(" + attr.getType() + ")" + ": " + attr.getDescription());
                            }
                            break;
                        case "attrs":
                            // get attribute values from a mbean
                            // read the mbean name
                            objName = parseString("attrs", args, ++i);
                            // read the attribute names if passed
                            List<String> names = new ArrayList<>();
                            while (checkArgument(args, ++i)) {
                                names.add(parseString("attrs", args, i));
                            }
                            Set<Attribute> attrs = null;
                            if (names.isEmpty()) {
                                attrs = jmx.getAttributes(objName);
                            } else {
                                attrs = jmx.getAttributes(objName, names.toArray(new String[0]));
                            }
                            for (Attribute attr : attrs) {
                                System.err.println(attr.getName() + ": " + attr.getValue());
                            }
                            break;
                        default:
                            usage("Invalid command");
                            break;
                    }
                }
            }
        } else {
            usage("No command specified");
        }
    }

}
