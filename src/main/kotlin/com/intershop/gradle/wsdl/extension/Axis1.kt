/*
 * Copyright 2017 Intershop Communications AG.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intershop.gradle.wsdl.extension

import com.intershop.gradle.wsdl.extension.data.WSDLProperty
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * Axis 1 Configuration container.
 *
 * @constructur default constructor with project and configuration name.
 */
open class Axis1 @Inject constructor(name: String, objectFactory: ObjectFactory, layout: ProjectLayout):
    AbstractAxisConfig(name, objectFactory) {

    companion object {
        /**
         * Default timeout configuration value.
         */
        const val TIMEOUT = 240
    }

    // Integer
    private val timeoutProperty = objectFactory.property(Int::class.java)
    
    // Boolean
    private val noImportsProperty = objectFactory.property(Boolean::class.java)
    private val noWrappedProperty = objectFactory.property(Boolean::class.java)
    private val serverSideProperty = objectFactory.property(Boolean::class.java)
    private val skeletonDeployProperty = objectFactory.property(String::class.java)
    private val generateAllClassesProperty = objectFactory.property(Boolean::class.java)
    private val helperGenProperty = objectFactory.property(Boolean::class.java)
    private val wrapArraysProperty = objectFactory.property(Boolean::class.java)
    private val allowInvalidURLProperty = objectFactory.property(Boolean::class.java)

    // Strings
    private val deployScopeProperty = objectFactory.property(String::class.java)
    private val typeMappingVersionProperty = objectFactory.property(String::class.java)
    private val factoryProperty = objectFactory.property(String::class.java)
    private val userNameProperty = objectFactory.property(String::class.java)
    private val passwordProperty = objectFactory.property(String::class.java)
    private val implementationClassNameProperty = objectFactory.property(String::class.java)
    private val nsIncludeProperty = objectFactory.property(String::class.java)
    private val nsExcludeProperty = objectFactory.property(String::class.java)

    private val outputDirProperty = objectFactory.directoryProperty()

    init {
        noImportsProperty.convention(false)
        timeoutProperty.convention(TIMEOUT)
        noWrappedProperty.convention(false)
        serverSideProperty.convention(false)
        skeletonDeployProperty.convention("")
        deployScopeProperty.convention("")
        generateAllClassesProperty.convention(false)
        typeMappingVersionProperty.convention("1.2")
        factoryProperty.convention("")
        helperGenProperty.convention(false)
        userNameProperty.convention("")
        passwordProperty.convention("")
        implementationClassNameProperty.convention("")
        wrapArraysProperty.convention(false)
        allowInvalidURLProperty.convention(false)
        nsIncludeProperty.convention("")
        nsExcludeProperty.convention("")

        outputDirProperty.convention(layout.buildDirectory.dir(
                "${WSDLExtension.CODEGEN_OUTPUTPATH}/axis1/${name.replace(' ', '_')}"
        ))
    }

    /**
     * Names and values of a properties for use by the custom GeneratorFactory.
     */
    val wsdlProperties: NamedDomainObjectContainer<WSDLProperty>
            = objectFactory.domainObjectContainer(WSDLProperty::class.java)

    /**
     * Provider for noImports property.
     */
    val noImportsProvider: Provider<Boolean>
        get() = noImportsProperty

    /**
     * Only generate code for the WSDLExtension document that appears on the command line if this
     * value is true. The default behaviour is to generate files for all WSDLExtension documents,
     * the immediate one and all imported ones.
     *
     * @property noImports
     */
    var noImports : Boolean
        get() = noImportsProperty.get()
        set(value) = noImportsProperty.set(value)

    /**
     * Provider for timeout property.
     */
    val timeoutProvider: Provider<Int>
        get() = timeoutProperty

    /**
     * Timeout in seconds. The default is 240.
     * Use -1 to disable the timeout.
     *
     * @property timeout
     */
    var timeout : Int
        get() = timeoutProperty.get()
        set(value) = timeoutProperty.set(value)

    /**
     * Provider for noWrapped property.
     */
    val noWrappedProvider: Provider<Boolean>
        get() = noWrappedProperty

    /**
     * If this value is true, it turns off the special treatment of what is called "wrapped" document/literal
     * style operations. By default, WSDL2Java will recognize the following conditions:
     *  - If an input message has is a single part.
     *  - The part is an element.
     *  - The element has the same name as the operation
     *  - The element's complex type has no attributes
     * If this value is true, WSDL2Java will 'unwrap' the top level element, and treat each of the
     * components of the element as arguments to the operation. This type of WSDLExtension is the
     * default for Microsoft .NET web services, which wrap up RPC style arguments in this top level schema element.
     *
     * @property noWrapped
     */
    var noWrapped : Boolean
        get() = noWrappedProperty.get()
        set(value) = noWrappedProperty.set(value)

    /**
     * Provider for serverSide property.
     */
    val serverSideProvider: Provider<Boolean>
        get() = serverSideProperty

    /**
     * Emit the server-side bindings for the web service.
     *
     * @property serverSide
     */
    var serverSide : Boolean
        get() = serverSideProperty.get()
        set(value) = serverSideProperty.set(value)

    /**
     * Provider for skeletonDeploy property.
     */
    val skeletonDeployProvider: Provider<String>
        get() = skeletonDeployProperty

    /**
     * Deploy either the skeleton (true) or the implementation (false) in deploy.wsdd. In other words, for "true"
     * the service clause in the deploy.wsdd file will look something like:
     * <p><blockquote><pre>
     * <service name="AddressBook" Property="java:RPC">
     *     <parameter name="className" value="samples.addr.AddressBookSOAPBindingSkeleton"/>
     *     ...
     * </service>
     * </pre></blockquote></p>
     * and for "false" it would look like:
     * <p><blockquote><pre>
     * <service name="AddressBook" Property="java:RPC">
     *     <parameter name="className" value="samples.addr.AddressBookSOAPBindingImpl"/>
     *     ...
     * </service>
     * </pre></blockquote></p>
     * If this configuration is used, serverSide is automatically set to true.
     *
     * @property skeletonDeploy
     */
    var skeletonDeploy : String
        get() = skeletonDeployProperty.get()
        set(value) = skeletonDeployProperty.set(value)

    /**
     * Provider for deployScope property.
     */
    val deployScopeProvider: Provider<String>
        get() = deployScopeProperty

    /**
     * Add scope to deploy.wsdd:
     *   - APPLICATION -> "Application",
     *   - REQUEST     -> "Request", or
     *   - SESSION     -> "Session".
     *   If this option does not appear, no scope tag appears in deploy.wsdd,
     *   which the Axis runtime defaults to "Request".
     *
     *   @property deployScope
     */
    var deployScope : String
        get() = deployScopeProperty.get()
        set(value) = deployScopeProperty.set(value)

    /**
     * Provider for generateAllClasses property.
     */
    val generateAllClassesProvider: Provider<Boolean>
        get() = generateAllClassesProperty

    /**
     * Generate code for all elements, even unreferenced ones. By default,
     * WSDL2Java only generates code for those elements in the WSDLExtension file that are referenced.
     *
     * A note about what it means to be referenced. We cannot simply say: start with the services,
     * generate all bindings referenced by the service, generate all portTypes referenced by the referenced
     * bindings, etc. What if we're generating code from a WSDLExtension file that only contains portTypes, messages,
     * and types? If WSDL2Java used service as an anchor, and there's no service in the file, then nothing
     * will be generated. So the anchor is the lowest element that exists in the WSDLExtension file in the order:
     *  - types
     *  - portTypes
     *  - bindings
     *  - services
     * For example, if a WSDLExtension file only contained types, then all the listed types would be generated.
     * But if a WSDLExtension file contained types and a portType, then that portType will be generated and only those
     * types that are referenced by that portType.
     *
     * Note that the anchor is searched for in the WSDLExtension file appearing on the command line, not
     * in imported WSDLExtension files. This allows one WSDLExtension file to import constructs defined
     * in another WSDLExtension file without the nuisance of having all the imported WSDLExtension file's
     * constructs generated.
     *
     * @property generateAllClasses
     */
    var generateAllClasses : Boolean
        get() = generateAllClassesProperty.get()
        set(value) = generateAllClassesProperty.set(value)

    /**
     * Provider for typeMappingVersion property.
     */
    val typeMappingVersionProvider: Provider<String>
        get() = typeMappingVersionProperty

    /**
     * Indicate 1.1 or 1.2. The default is 1.2 (SOAP 1.2 JAX-RPC compliant).
     *
     * @property typeMappingVersion
     */
    var typeMappingVersion : String
        get() = typeMappingVersionProperty.get()
        set(value) = typeMappingVersionProperty.set(value)

    /**
     * Provider for factory property.
     */
    val factoryProvider: Provider<String>
        get() = factoryProperty

    /**
     * Used to extend the functionality of the WSDL2Java emitter.
     * The argument is the name of a class which extends JavaWriterFactory.
     *
     * @property factory
     */
    var factory : String
        get() = factoryProperty.get()
        set(value) = factoryProperty.set(value)

    /**
     * Provider for helperGen property.
     */
    val helperGenProvider: Provider<Boolean>
        get() = helperGenProperty

    /**
     * Emits separate Helper classes for meta data.
     *
     * @property helperGen
     */
    var helperGen : Boolean
        get() = helperGenProperty.get()
        set(value) = helperGenProperty.set(value)

    /**
     * Provider for userName property.
     */
    val userNameProvider: Provider<String>
        get() = userNameProperty

    /**
     * This username is used in resolving the WSDLExtension-URI provided as the input to WSDL2Java.
     * If the URI contains a username, this will override the command line switch. An example
     * of a URL with a username and password is: http://user:password@hostname:port/path/to/service?WSDL
     *
     * @property userName
     */
    var userName : String
        get() = userNameProperty.get()
        set(value) = userNameProperty.set(value)

    /**
     * Provider for password property.
     */
    val passwordProvider: Provider<String>
        get() = passwordProperty

    /**
     * This password is used in resolving the WSDLExtension-URI provided as the input to WSDL2Java.
     * If the URI contains a password, this will override the command line switch.
     *
     * @property password
     */
    var password : String
        get() = passwordProperty.get()
        set(value) = passwordProperty.set(value)

    /**
     * Provider for implementationClassName property.
     */
    val implementationClassNameProvider: Provider<String>
        get() = implementationClassNameProperty

    /**
     * Set the name of the implementation class. Especially useful when exporting an existing class as
     * a web service using java2wsdl followed by wsdl2java. If you are using the skeleton deploy option
     * you must make sure, after generation, that your implementation class implements the port type name
     * interface generated by wsdl2java. You should also make sure that all your exported methods throws
     * java.lang.RemoteException.
     *
     * @property implementationClassName
     */
    var implementationClassName : String
        get() = implementationClassNameProperty.get()
        set(value) = implementationClassNameProperty.set(value)

    /**
     * Provider for wrapArrays property.
     */
    val wrapArraysProvider: Provider<Boolean>
        get() = wrapArraysProperty

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
     *
     * @property wrapArrays
     */
    var wrapArrays : Boolean
        get() = wrapArraysProperty.get()
        set(value) = wrapArraysProperty.set(value)

    /**
     * Provider for allowInvalidURL property.
     */
    val allowInvalidURLProvider: Provider<Boolean>
        get() = allowInvalidURLProperty

    /**
     * This flag is used to allow Stub generation even if WSDLExtension endpoint URL is not a valid URL.
     * It's the responsibility of the user to update the endpoint value before using generated classes
     * default=false
     *
     * @property allowInvalidURL
     */
    var allowInvalidURL : Boolean
        get() = allowInvalidURLProperty.get()
        set(value) = allowInvalidURLProperty.set(value)

    /**
     * Provider for nsInclude property.
     */
    val nsIncludeProvider: Provider<String>
        get() = nsIncludeProperty

    /**
     * Namespace to specifically exclude from the generated code (defaults to
     * none excluded until first namespace included with -i option).
     *
     * @property nsInclude
     */
    var nsInclude : String
        get() = nsIncludeProperty.get()
        set(value) = nsIncludeProperty.set(value)

    /**
     * Provider for nsExclude property.
     */
    val nsExcludeProvider: Provider<String>
        get() = nsExcludeProperty

    /**
     * Namescape to specifically include in the generated code (defaults to
     * all namespaces unless specifically excluded with the -x option).
     *
     * @property nsExclude
     */
    var nsExclude : String
        get() = nsExcludeProperty.get()
        set(value) = nsExcludeProperty.set(value)

    /**
     * Provider for outputDir property.
     */
    val outputDirProvider: Provider<Directory>
        get() = outputDirProperty

    /**
     * Output directory for the generated code.
     *
     * @return file for output directory.
     */
    var outputDir: File
        get() = outputDirProperty.get().asFile
        set(value) = this.outputDirProperty.set(value)

    /**
     * Calculate the task name for the task.
     * @return task name for configuration
     */
    fun getTaskName(): String {
        return "axis1Wsdl2java${name.toCamelCase()}"
    }

    private fun String.toCamelCase() : String {
        return split(" ").joinToString("") {
            it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
    }
}
