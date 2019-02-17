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
package com.intershop.gradle.wsdl.tasks.axis1

import com.intershop.gradle.wsdl.extension.WSDLExtension
import com.intershop.gradle.wsdl.extension.data.WSDLProperty
import com.intershop.gradle.wsdl.tasks.AbstractWSDL2Java
import com.intershop.gradle.wsdl.utils.DeployScope
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.process.JavaForkOptions
import org.gradle.process.internal.DefaultJavaForkOptions

inline fun <reified T> ObjectFactory.property(): Property<T> = property(T::class.java)

open class WSDL2Java : AbstractWSDL2Java(){

    /**
     * Only generate code for the WSDLExtension document that appears on the command line if this value is true.
     * The default behaviour is to generate files for all WSDLExtension documents, the immediate one and all imported ones.
     */
    private val noImportsProperty = project.objects.property<Boolean>()

    @get:Optional
    @get:Input
    var noImports: Boolean
        get() = noImportsProperty.getOrElse(false)
        set(value) = noImportsProperty.set(value)

    fun provideNoImports(noImports: Provider<Boolean>) = noImportsProperty.set(noImports)

    /**
     * Timeout in seconds. The default is 240.
     * Use -1 to disable the timeout.
     */
    private val timeoutProperty = project.objects.property<Int>()

    @get:Optional
    @get:Input
    var timeoutConf: Int
        get() = timeoutProperty.getOrElse(240)
        set(value) = timeoutProperty.set(value)

    fun provideTimeout(timeout: Provider<Int>) = timeoutProperty.set(timeout)

    /**
     * If this value is true, it turns off the special treatment of what is called "wrapped" document/literal
     * style operations. By default, WSDL2Java will recognize the following conditions:
     *  - If an input message has is a single part.
     *  - The part is an element.
     *  - The element has the same name as the operation
     *  - The element's complex type has no attributes
     * If this value is true, WSDL2Java will 'unwrap' the top level element, and treat each of the components of
     * the element as arguments to the operation. This type of WSDLExtension is the default for Microsoft .NET web services,
     * which wrap up RPC style arguments in this top level schema element.
     */
    private val noWrappedProperty = project.objects.property<Boolean>()

    @get:Optional
    @get:Input
    var noWrapped: Boolean
        get() = noWrappedProperty.getOrElse(false)
        set(value) = noWrappedProperty.set(value)

    fun provideNoWrapped(noWrapped: Provider<Boolean>) = noWrappedProperty.set(noWrapped)

    /**
     * Emit the server-side bindings for the web service:
     *  - a skeleton class named <bindingName>Skeleton. This may or may not be emitted (see skeletonDeploy).
     *  - an implementation template class named <bindingName>Impl. Note that, if this class already exists, then it is not emitted.
     * deploy.wsdd
     * undeploy.wsdd
     */
    private val serverSideProperty = project.objects.property<Boolean>()

    @get:Optional
    @get:Input
    var serverSide: Boolean
        get() = serverSideProperty.getOrElse(false)
        set(value) = serverSideProperty.set(value)

    fun provideServerSide(serverSide: Provider<Boolean>) = serverSideProperty.set(serverSide)

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
    private val skeletonDeployProperty = project.objects.property<Boolean>()

    @get:Optional
    @get:Input
    var skeletonDeploy: Boolean
        get() = skeletonDeployProperty.getOrElse(false)
        set(value) = skeletonDeployProperty.set(value)

    fun provideSkeletonDeploy(skeletonDeploy: Provider<Boolean>) = skeletonDeployProperty.set(skeletonDeploy)

    /**
     * Add scope to deploy.wsdd:
     *   - APPLICATION -> "Application",
     *   - REQUEST     -> "Request", or
     *   - SESSION     -> "Session".
     *   If this option does not appear, no scope tag appears in deploy.wsdd,
     *   which the Axis runtime defaults to "Request".
     */
    private val deployScopeProperty = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var deployScope: String
        get() = deployScopeProperty.getOrElse(DeployScope.REQUEST.scope)
        set(value) = deployScopeProperty.set(value)

    fun provideDeployScope(deployScope: Provider<String>) = deployScopeProperty.set(deployScope)

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
     * Note that the anchor is searched for in the WSDLExtension file appearing on the command line, not in imported WSDLExtension
     * files. This allows one WSDLExtension file to import constructs defined in another WSDLExtension file without the nuisance of
     * having all the imported WSDLExtension file's constructs generated.
     */
    private val generateAllClassesProperty = project.objects.property<Boolean>()

    @get:Optional
    @get:Input
    var generateAllClasses: Boolean
        get() = generateAllClassesProperty.getOrElse(false)
        set(value) = generateAllClassesProperty.set(value)

    fun provideGenerateAllClasses(generateAllClasses: Provider<Boolean>) = generateAllClassesProperty.set(generateAllClasses)

    /**
     * Indicate 1.1 or 1.2. The default is 1.2 (SOAP 1.2 JAX-RPC compliant).
     */
    private val typeMappingVersionProperty = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var typeMappingVersion: String
        get() = typeMappingVersionProperty.getOrElse("1.2")
        set(value) = typeMappingVersionProperty.set(value)

    fun provideTypeMappingVersion(typeMappingVersion: Provider<String>) = typeMappingVersionProperty.set(typeMappingVersion)

    /**
     * Used to extend the functionality of the WSDL2Java emitter.
     * The argument is the name of a class which extends JavaWriterFactory.
     */
    private val factoryProperty = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var factory: String
        get() = factoryProperty.getOrElse("")
        set(value) = factoryProperty.set(value)

    fun provideFactory(factory: Provider<String>) = factoryProperty.set(factory)

    /**
     * Emits separate Helper classes for meta data.
     */
    private val helperGenProperty = project.objects.property<Boolean>()
    
    @get:Optional
    @get:Input
    var helperGen: Boolean
        get() = helperGenProperty.getOrElse(false)
        set(value) = helperGenProperty.set(value)

    fun provideHelperGen(helperGen: Provider<Boolean>) = helperGenProperty.set(helperGen)

    /**
     * This username is used in resolving the WSDLExtension-URI provided as the input to WSDL2Java.
     * If the URI contains a username, this will override the command line switch. An example
     * of a URL with a username and password is: http://user:password@hostname:port/path/to/service?WSDL
     */
    private val userNameProperty = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var userName: String
        get() = userNameProperty.getOrElse("")
        set(value) = userNameProperty.set(value)

    fun provideUserName(userName: Provider<String>) = userNameProperty.set(userName)

    /**
     * This password is used in resolving the WSDLExtension-URI provided as the input to WSDL2Java.
     * If the URI contains a password, this will override the command line switch.
     */
    private val passwordProperty = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var password: String
        get() = passwordProperty.getOrElse("")
        set(value) = passwordProperty.set(value)

    fun providePassword(password: Provider<String>) = passwordProperty.set(password)

    /**
     * Set the name of the implementation class. Especially useful when exporting an existing class as
     * a web service using java2wsdl followed by wsdl2java. If you are using the skeleton deploy option
     * you must make sure, after generation, that your implementation class implements the port type name
     * interface generated by wsdl2java. You should also make sure that all your exported methods throws
     * java.lang.RemoteException.
     */
    private val implementationClassNameProperty = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var implementationClassName: String
        get() = implementationClassNameProperty.getOrElse("")
        set(value) = implementationClassNameProperty.set(value)

    fun provideImplementationClassName(implementationClassName: Provider<String>)
            = implementationClassNameProperty.set(implementationClassName)

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
    private val wrapArraysProperty = project.objects.property<Boolean>()

    @get:Optional
    @get:Input
    var wrapArrays: Boolean
        get() = wrapArraysProperty.getOrElse(false)
        set(value) = wrapArraysProperty.set(value)

    fun provideWrapArrays(wrapArrays: Provider<Boolean>) = wrapArraysProperty.set(wrapArrays)

    /**
     * namescape to specifically include in the generated code (defaults to
     * all namespaces unless specifically excluded with the -x option)
     */
    private val nsIncludeProperty = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var nsInclude: String
        get() = nsIncludeProperty.getOrElse("")
        set(value) = nsIncludeProperty.set(value)

    fun provideNsInclude(nsInclude: Provider<String>) = nsIncludeProperty.set(nsInclude)

    /*
     * namespace to specifically exclude from the generated code (defaults to
     * none excluded until first namespace included with -i option)
     */
    private val nsExcludeProperty = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var nsExclude: String
        get() = nsExcludeProperty.getOrElse("")
        set(value) = nsExcludeProperty.set(value)

    fun provideNsExclude(nsExclude: Provider<String>) = nsExcludeProperty.set(nsExclude)

    /*
     * Names and values of a properties for use by the custom GeneratorFactory
     */
    @Internal
    var wsdlProperties: NamedDomainObjectContainer<WSDLProperty>? = null

    @get:Input
    val wsdlPropertiesList: List<String>
        get() {
            val properties: MutableList<String> = mutableListOf()

            wsdlProperties?.all {
                properties.add("${it.name}=${it.value}")
            }

            return properties.toList()
        }

    /**
     * This flag is used to allow Stub generation even if WSDLExtension endpoint URL is not a valid URL.
     * It's the responsibility of the user to update the endpoint value before using generated classes
     * default=false
     */
    private val allowInvalidURLProperty = project.objects.property<Boolean>()

    @get:Optional
    @get:Input
    var allowInvalidURL: Boolean
        get() = allowInvalidURLProperty.getOrElse(false)
        set(value) = allowInvalidURLProperty.set(value)

    fun provideAllowInvalidURL(allowInvalidURL: Provider<Boolean>) = allowInvalidURLProperty.set(allowInvalidURL)

    @get:InputFiles
    private val toolsclasspathfiles : FileCollection by lazy {
        val returnFiles = project.files()
        // find files of original JASPER and Eclipse compiler
        returnFiles.from(project.configurations.findByName(WSDLExtension.WSDL_AXIS1_CONFIGURATION_NAME))
        returnFiles
    }

    private var javaOptions: JavaForkOptions = DefaultJavaForkOptions((project as ProjectInternal).fileResolver)

    @TaskAction
    fun run() {

        if (internalForkOptionsAction != null) {
            project.logger.debug("Add configured JavaForkOptions to WSDL2Java Axis2 code generation runner.")
            (internalForkOptionsAction as Action<in JavaForkOptions>).execute(javaOptions)
        }

        project.javaexec {
            it.classpath = toolsclasspathfiles
            it.main = "org.apache.axis.wsdl.WSDL2Java"
            it.args = calculateParameters()
            javaOptions.copyTo(it)
        }
    }


    private fun calculateParameters() : List<String> {
        val parameters: MutableList<String> = mutableListOf()

        addAttribute(parameters, outputDir.absolutePath, "--output")

        addFlag(parameters, noImports, "--noImports")
        addAttribute(parameters, timeoutConf.toString(), "--timeout")
        addFlag(parameters, noWrapped, "--noWrapped")
        addFlag(parameters, wrapArrays,"--wrapArrays")
        addFlag(parameters, serverSide, "--server-side")
        addAttribute(parameters, nsInclude, "--nsInclude")
        addAttribute(parameters, nsExclude, "--nsInclude")

        if(skeletonDeploy) {
            addAttribute(parameters, "true", "--skeletonDeploy")
        } else {
            addAttribute(parameters, "false", "--skeletonDeploy")
        }

        namespacePackageMappingList.forEach {
            addAttribute(parameters, it, "--NStoPkg")
        }

        if (namespacePackageMappingFile != null) {
            val filePath: String = namespacePackageMappingFile?.absolutePath ?: ""
            addAttribute(parameters, filePath, "--fileNStoPkg")
        }

        addAttribute(parameters, packageName, "--package")
        addAttribute(parameters, deployScope,"--deployScope")
        addFlag(parameters, generateTestcase,"--testCase")
        addFlag(parameters, generateAllClasses, "--all")
        addAttribute(parameters, typeMappingVersion, "--typeMappingVersion")
        addAttribute(parameters, factory, "--factory")
        addFlag(parameters, helperGen, "--helperGen")
        addAttribute(parameters, userName ,"--user")
        addAttribute(parameters, password ,"--password")
        addAttribute(parameters, implementationClassName, "--implementationClassName")

        addFlag(parameters, allowInvalidURL, "--allowInvalidURL")

        wsdlPropertiesList.forEach {
            addAttribute(parameters, it, "--property")
        }

        // Add verbose logging
        addFlag(parameters, logger.isInfoEnabled, "--verbose")

        // Add debug logging
        addFlag(parameters, logger.isDebugEnabled || logger.isTraceEnabled, "--Debug")

        args.forEach {
            parameters.add(it)
        }

        parameters.add(wsdlFile.toURI().toURL().toString())

        return parameters
    }
}