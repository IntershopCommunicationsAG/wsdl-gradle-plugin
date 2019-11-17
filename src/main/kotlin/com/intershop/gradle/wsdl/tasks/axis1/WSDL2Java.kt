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

import com.intershop.gradle.wsdl.extension.data.WSDLProperty
import com.intershop.gradle.wsdl.tasks.AbstractWSDL2Java
import com.intershop.gradle.wsdl.utils.DeployScope
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaForkOptions
import org.gradle.process.internal.DefaultJavaDebugOptions
import org.gradle.process.internal.DefaultJavaForkOptions

/**
 * Funcion to declare a property.
 */
inline fun <reified T> ObjectFactory.property(): Property<T> = property(T::class.java)

/**
 * This task generates the source code from existing WSDL with axis1 files with a specific configuration.
 *
 * @constructor constructor initialize a WSDL2Java task with a worker executtor.
 */
open class WSDL2Java : AbstractWSDL2Java(){

    private val noImportsProperty = project.objects.property<Boolean>()

    /**
     * Only generate code for the WSDLExtension document that appears on the command line
     * if this value is true. The default behaviour is to generate files for all WSDLExtension
     * documents, the immediate one and all imported ones.
     *
     * @property noImports default value is false
     */
    @get:Input
    var noImports: Boolean
        get() = noImportsProperty.getOrElse(false)
        set(value) = noImportsProperty.set(value)

    /**
     * Add provider for noImports.
     */
    fun provideNoImports(noImports: Provider<Boolean>) = noImportsProperty.set(noImports)

    private val timeoutProperty = project.objects.property<Int>()

    /**
     * Timeout in seconds. The default is 240.
     * Use -1 to disable the timeout.
     *
     * @property timeoutConf default value is 240
     */
    @get:Input
    var timeoutConf: Int
        get() = timeoutProperty.getOrElse(240)
        set(value) = timeoutProperty.set(value)

    /**
     * Add provider for timeoutConf.
     */
    fun provideTimeout(timeout: Provider<Int>) = timeoutProperty.set(timeout)

    private val noWrappedProperty = project.objects.property<Boolean>()

    /**
     * If this value is true, it turns off the special treatment of what is called "wrapped" document/literal
     * style operations. By default, WSDL2Java will recognize the following conditions:
     *  - If an input message has is a single part.
     *  - The part is an element.
     *  - The element has the same name as the operation
     *  - The element's complex type has no attributes
     * If this value is true, WSDL2Java will 'unwrap' the top level element, and treat each of the
     * components of the element as arguments to the operation. This type of WSDLExtension is the
     * default for Microsoft .NET web services,
     * which wrap up RPC style arguments in this top level schema element.
     *
     * @property noWrapped default value is false.
     */
    @get:Input
    var noWrapped: Boolean
        get() = noWrappedProperty.getOrElse(false)
        set(value) = noWrappedProperty.set(value)

    /**
     * Add provider for noWrapped.
     */
    fun provideNoWrapped(noWrapped: Provider<Boolean>) = noWrappedProperty.set(noWrapped)

    private val serverSideProperty = project.objects.property<Boolean>()

    /**
     * Emit the server-side bindings for the web service:
     *  - a skeleton class named <bindingName>Skeleton. This may or may not be emitted (see skeletonDeploy).
     *  - an implementation template class named <bindingName>Impl. Note that, if this class already exists,
     *  then it is not emitted. (deploy.wsdd and undeploy.wsdd)
     *
     * @property serverSide default value is false
     */
    @get:Input
    var serverSide: Boolean
        get() = serverSideProperty.getOrElse(false)
        set(value) = serverSideProperty.set(value)

    /**
     * Add provider for serverSide.
     */
    fun provideServerSide(serverSide: Provider<Boolean>) = serverSideProperty.set(serverSide)

    private val skeletonDeployProperty = project.objects.property<String>()

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
     *
     * @property skeletonDeploy default value is false
     */
    @get:Optional
    @get:Input
    var skeletonDeploy: String
        get() = skeletonDeployProperty.getOrElse("")
        set(value) = skeletonDeployProperty.set(value)

    /**
     * Add provider for skeletonDeploy.
     */
    fun provideSkeletonDeploy(skeletonDeploy: Provider<String>) = skeletonDeployProperty.set(skeletonDeploy)

    private val deployScopeProperty = project.objects.property(String::class.java)

    /**
     * Add scope to deploy.wsdd:
     *   - APPLICATION -> "Application",
     *   - REQUEST     -> "Request", or
     *   - SESSION     -> "Session".
     *   If this option does not appear, no scope tag appears in deploy.wsdd,
     *   which the Axis runtime defaults to "Request".
     *
     *   @property deployScope default falue is 'Request'.
     */
    @get:Optional
    @get:Input
    var deployScope: String
        get() = deployScopeProperty.getOrElse(DeployScope.REQUEST.scope)
        set(value) = deployScopeProperty.set(value)

    /**
     * Add provider for deployScope.
     */
    fun provideDeployScope(deployScope: Provider<String>) = deployScopeProperty.set(deployScope)

    private val generateAllClassesProperty = project.objects.property<Boolean>()

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
     * Note that the anchor is searched for in the WSDLExtension file appearing on the command line, not in
     * imported WSDLExtension files. This allows one WSDLExtension file to import constructs defined in another
     * WSDLExtension file without the nuisance of having all the imported WSDLExtension file's constructs generated.
     *
     * @property generateAllClasses default value is false
     */
    @get:Input
    var generateAllClasses: Boolean
        get() = generateAllClassesProperty.getOrElse(false)
        set(value) = generateAllClassesProperty.set(value)

    /**
     * Add provider for generateAllClasses.
     */
    fun provideGenerateAllClasses(generateAllClasses: Provider<Boolean>) =
            generateAllClassesProperty.set(generateAllClasses)

    private val typeMappingVersionProperty = project.objects.property(String::class.java)

    /**
     * Indicate 1.1 or 1.2. The default is 1.2 (SOAP 1.2 JAX-RPC compliant).
     *
     * @property typeMappingVersion default value is '1.2'
     */
    @get:Optional
    @get:Input
    var typeMappingVersion: String
        get() = typeMappingVersionProperty.getOrElse("1.2")
        set(value) = typeMappingVersionProperty.set(value)

    /**
     * Add provider for typeMappingVersion.
     */
    fun provideTypeMappingVersion(typeMappingVersion: Provider<String>) =
            typeMappingVersionProperty.set(typeMappingVersion)

    private val factoryProperty = project.objects.property(String::class.java)

    /**
     * Used to extend the functionality of the WSDL2Java emitter.
     * The argument is the name of a class which extends JavaWriterFactory.
     *
     * @property factory default value is ""
     */
    @get:Optional
    @get:Input
    var factory: String
        get() = factoryProperty.getOrElse("")
        set(value) = factoryProperty.set(value)

    /**
     * Add provider for factory.
     */
    fun provideFactory(factory: Provider<String>) = factoryProperty.set(factory)

    private val helperGenProperty = project.objects.property<Boolean>()

    /**
     * Emits separate Helper classes for meta data.
     *
     * @property helperGen default value is false
     */
    @get:Input
    var helperGen: Boolean
        get() = helperGenProperty.getOrElse(false)
        set(value) = helperGenProperty.set(value)

    /**
     * Add provider for helperGen.
     */
    fun provideHelperGen(helperGen: Provider<Boolean>) = helperGenProperty.set(helperGen)

    private val userNameProperty = project.objects.property(String::class.java)

    /**
     * This username is used in resolving the WSDLExtension-URI provided as the input to WSDL2Java.
     * If the URI contains a username, this will override the command line switch. An example
     * of a URL with a username and password is: http://user:password@hostname:port/path/to/service?WSDL
     *
     * @property userName default value is ""
     */
    @get:Optional
    @get:Input
    var userName: String
        get() = userNameProperty.getOrElse("")
        set(value) = userNameProperty.set(value)

    /**
     * Add provider for userName.
     */
    fun provideUserName(userName: Provider<String>) = userNameProperty.set(userName)

    private val passwordProperty = project.objects.property(String::class.java)

    /**
     * This password is used in resolving the WSDLExtension-URI provided as the input to WSDL2Java.
     * If the URI contains a password, this will override the command line switch.
     *
     * @property password default value is ""
     */
    @get:Optional
    @get:Input
    var password: String
        get() = passwordProperty.getOrElse("")
        set(value) = passwordProperty.set(value)

    /**
     * Add provider for password.
     */
    fun providePassword(password: Provider<String>) = passwordProperty.set(password)

    private val implementationClassNameProperty = project.objects.property(String::class.java)

    /**
     * Set the name of the implementation class. Especially useful when exporting an existing class as
     * a web service using java2wsdl followed by wsdl2java. If you are using the skeleton deploy option
     * you must make sure, after generation, that your implementation class implements the port type name
     * interface generated by wsdl2java. You should also make sure that all your exported methods throws
     * java.lang.RemoteException.
     *
     * @property implementationClassName default value is ""
     */
    @get:Optional
    @get:Input
    var implementationClassName: String
        get() = implementationClassNameProperty.getOrElse("")
        set(value) = implementationClassNameProperty.set(value)

    /**
     * Add provider for implementationClassName.
     */
    fun provideImplementationClassName(implementationClassName: Provider<String>)
            = implementationClassNameProperty.set(implementationClassName)

    private val wrapArraysProperty = project.objects.property<Boolean>()

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
     * @property wrapArrays default value is false
     */
    @get:Input
    var wrapArrays: Boolean
        get() = wrapArraysProperty.getOrElse(false)
        set(value) = wrapArraysProperty.set(value)

    /**
     * Add provider for wrapArrays.
     */
    fun provideWrapArrays(wrapArrays: Provider<Boolean>) = wrapArraysProperty.set(wrapArrays)

    private val nsIncludeProperty = project.objects.property(String::class.java)

    /**
     * namescape to specifically include in the generated code (defaults to
     * all namespaces unless specifically excluded with the -x option).
     *
     * @property nsInclude default value is ""
     */
    @get:Optional
    @get:Input
    var nsInclude: String
        get() = nsIncludeProperty.getOrElse("")
        set(value) = nsIncludeProperty.set(value)

    /**
     * Add provider for nsInclude.
     */
    fun provideNsInclude(nsInclude: Provider<String>) = nsIncludeProperty.set(nsInclude)

    private val nsExcludeProperty = project.objects.property(String::class.java)

    /**
     * namespace to specifically exclude from the generated code (defaults to
     * none excluded until first namespace included with -i option).
     *
     * @property nsExclude default value is ""
     */
    @get:Optional
    @get:Input
    var nsExclude: String
        get() = nsExcludeProperty.getOrElse("")
        set(value) = nsExcludeProperty.set(value)

    /**
     * Add provider for nsExclude.
     */
    fun provideNsExclude(nsExclude: Provider<String>) = nsExcludeProperty.set(nsExclude)

    @Internal
    var wsdlProperties: NamedDomainObjectContainer<WSDLProperty>? = null

    /**
     * Names and values of a properties for use by the custom GeneratorFactory.
     *
     * @property wsdlPropertiesList list of WSDL properties
     */
    @get:Input
    val wsdlPropertiesList: List<String>
        get() {
            val properties: MutableList<String> = mutableListOf()

            wsdlProperties?.all {
                properties.add("${it.name}=${it.value}")
            }

            return properties.toList()
        }

    private val allowInvalidURLProperty = project.objects.property<Boolean>()

    /**
     * This flag is used to allow Stub generation even if WSDLExtension endpoint URL is not a valid URL.
     * It's the responsibility of the user to update the endpoint value before using generated classes
     * default=false
     *
     * @property allowInvalidURL default value is false
     */
    @get:Input
    var allowInvalidURL: Boolean
        get() = allowInvalidURLProperty.getOrElse(false)
        set(value) = allowInvalidURLProperty.set(value)

    /**
     * Add provider for allowInvalidURL.
     */
    fun provideAllowInvalidURL(allowInvalidURL: Provider<Boolean>) = allowInvalidURLProperty.set(allowInvalidURL)

    /**
     * Classpath for Axis 2 files stored in a configuration
     *
     * @property toolsClasspath file collection of libraries
     */
    @get:Classpath
    val toolsClasspath : ConfigurableFileCollection = project.files()

    private var javaOptions: JavaForkOptions = DefaultJavaForkOptions((project as ProjectInternal).fileResolver, (project as ProjectInternal).services.get(FileCollectionFactory::class.java), DefaultJavaDebugOptions())

    /**
     * This is the task action and generates Java source files.
     */
    @TaskAction
    fun run() {

        if (internalForkOptionsAction != null) {
            project.logger.debug("Add configured JavaForkOptions to WSDL2Java Axis2 code generation runner.")
            (internalForkOptionsAction)?.execute(javaOptions)
        }

        project.javaexec {
            it.classpath = toolsClasspath
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

        if(skeletonDeploy.isNotEmpty()) {
            if(skeletonDeploy.toBoolean()) {
                addAttribute(parameters, "true", "--skeletonDeploy")
            } else {
                addAttribute(parameters, "false", "--skeletonDeploy")
            }
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
