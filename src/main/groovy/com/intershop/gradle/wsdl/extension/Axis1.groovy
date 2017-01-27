/*
 * Copyright 2015 Intershop Communications AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.intershop.gradle.wsdl.extension

import org.gradle.util.GUtil

class Axis1 extends BaseAxisConfig {

    /**
     * Only generate code for the WSDL document that appears on the command line if this value is true.
     * The default behaviour is to generate files for all WSDL documents, the immediate one and all imported ones.
     */
    boolean noImports = false

    /**
     * Timeout in seconds. The default is 240.
     * Use -1 to disable the timeout.
     */
    int timeout = 240

    /**
     * If this value is true, it turns off the special treatment of what is called "wrapped" document/literal
     * style operations. By default, WSDL2Java will recognize the following conditions:
     *  - If an input message has is a single part.
     *  - The part is an element.
     *  - The element has the same name as the operation
     *  - The element's complex type has no attributes
     * If this value is true, WSDL2Java will 'unwrap' the top level element, and treat each of the components of
     * the element as arguments to the operation. This type of WSDL is the default for Microsoft .NET web services,
     * which wrap up RPC style arguments in this top level schema element.
     */
    boolean noWrapped = false

    /**
     * Emit the server-side bindings for the web service:
     *  - a skeleton class named <bindingName>Skeleton. This may or may not be emitted (see skeletonDeploy).
     *  - an implementation template class named <bindingName>Impl. Note that, if this class already exists, then it is not emitted.
     * deploy.wsdd
     * undeploy.wsdd
     */
    boolean serverSide = false

    /**
     * Deploy either the skeleton (true) or the implementation (false) in deploy.wsdd. In other words, for "true"
     * the service clause in the deploy.wsdd file will look something like:
     * <p><blockquote><pre>
     * <service name="AddressBook" provider="java:RPC">
     *     <parameter name="className" value="samples.addr.AddressBookSOAPBindingSkeleton"/>
     *     ...
     * </service>
     * </pre></blockquote></p>
     * and for "false" it would look like:
     * <p><blockquote><pre>
     * <service name="AddressBook" provider="java:RPC">
     *     <parameter name="className" value="samples.addr.AddressBookSOAPBindingImpl"/>
     *     ...
     * </service>
     * </pre></blockquote></p>
     * If this configuration is used, serverSide is automatically set to true.
     */
    boolean skeletonDeploy = false

    /**
     * Add scope to deploy.wsdd:
     *   - APPLICATION -> "Application",
     *   - REQUEST     -> "Request", or
     *   - SESSION     -> "Session".
     *   If this option does not appear, no scope tag appears in deploy.wsdd,
     *   which the Axis runtime defaults to "Request".
     */
    String deployScope

    /**
     * Generate code for all elements, even unreferenced ones. By default,
     * WSDL2Java only generates code for those elements in the WSDL file that are referenced.
     *
     * A note about what it means to be referenced. We cannot simply say: start with the services,
     * generate all bindings referenced by the service, generate all portTypes referenced by the referenced
     * bindings, etc. What if we're generating code from a WSDL file that only contains portTypes, messages,
     * and types? If WSDL2Java used service as an anchor, and there's no service in the file, then nothing
     * will be generated. So the anchor is the lowest element that exists in the WSDL file in the order:
     *  - types
     *  - portTypes
     *  - bindings
     *  - services
     * For example, if a WSDL file only contained types, then all the listed types would be generated.
     * But if a WSDL file contained types and a portType, then that portType will be generated and only those
     * types that are referenced by that portType.
     *
     * Note that the anchor is searched for in the WSDL file appearing on the command line, not in imported WSDL
     * files. This allows one WSDL file to import constructs defined in another WSDL file without the nuisance of
     * having all the imported WSDL file's constructs generated.
     */
    boolean generateAllClasses = false

    /**
     * Indicate 1.1 or 1.2. The default is 1.2 (SOAP 1.2 JAX-RPC compliant).
     */
    String typeMappingVersion = '1.2'

    /**
     * Used to extend the functionality of the WSDL2Java emitter.
     * The argument is the name of a class which extends JavaWriterFactory.
     */
    String factory

    /**
     * Emits separate Helper classes for meta data.
     */
    boolean helperGen = false

    /**
     * This username is used in resolving the WSDL-URI provided as the input to WSDL2Java.
     * If the URI contains a username, this will override the command line switch. An example
     * of a URL with a username and password is: http://user:password@hostname:port/path/to/service?WSDL
     */
    String userName

    /**
     * This password is used in resolving the WSDL-URI provided as the input to WSDL2Java.
     * If the URI contains a password, this will override the command line switch.
     */
    String password

    /**
     * Set the name of the implementation class. Especially useful when exporting an existing class as
     * a web service using java2wsdl followed by wsdl2java. If you are using the skeleton deploy option
     * you must make sure, after generation, that your implementation class implements the port type name
     * interface generated by wsdl2java. You should also make sure that all your exported methods throws
     * java.lang.RemoteException.
     */
    String implementationClassName

    /**
     * When processing a schema like this:
     * <p><blockquote><pre>
     * <element name="array">
     *    <complexType>
     *       <sequence>
     *          <element name="item" type="xs:string"/>
     *       </sequence>
     *    </complexType>
     * </element>
     * </pre></blockquote></p>
     * The default behavior (as of Axis 1.2 final) is to map this XML construct to a Java String
     * array (String[]). If you would rather a specific JavaBean class (i.e. ArrayOfString) be
     * generated for these types of schemas, you may specify the -w or --wrapArrays option.
     */
    boolean wrapArrays = false

    /**
     * Output path for all sources
     */
    File outputDir

    /**
     * Name of the source set for generated Java code
     * default value is 'main'
     */
    String sourceSetName

    String getSourceSetName() {
        if(! this.sourceSetName) {
            return WSDLExtension.DEFAULT_SOURCESET_NAME
        } else {
            return this.sourceSetName
        }
    }

    /**
     * Additional ars for xjc
     */
    List<String> args = []

    void arg(String parameter) {
        args.add(parameter)
    }

    Axis1(String name) {
        super(name)
    }

    String getTaskName() {
        "axis1Wsdl2java" + GUtil.toCamelCase(name);
    }
}
