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
package com.intershop.gradle.wsdl.tasks.axis2

import com.intershop.gradle.wsdl.extension.WSDLExtension
import com.intershop.gradle.wsdl.tasks.AbstractWSDL2Java
import com.intershop.gradle.wsdl.utils.Databinding
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.process.internal.JavaExecHandleBuilder

class WSDL2Java extends AbstractWSDL2Java {

    static final String MAIN_CLASS_NAME = 'org.apache.axis2.wsdl.WSDL2Java'

    /**
     * Generate code only for async style. When this option is used the generated
     * stubs will have only the asynchronous invocation methods. Switched off by default.
     */
    @Optional
    @Input
    boolean async = false

    /**
     * Generate code only for sync style . When this option is used the generated stubs
     * will have only the synchronous invocation methods. Switched off by default.
     * When async is set to true, this takes precedence.
     */
    @Optional
    @Input
    boolean sync = false

    /**
     * Generates server side code (i.e. skeletons). Default is false.
     */
    @Optional
    @Input
    boolean serverSide = false

    /**
     * Generates the service descriptor (i.e. server.xml). Default is false.
     * Only valid if serverSide is true, the server side code generation option.
     */
    @Optional
    @Input
    boolean serviceDescription = false

    /**
     * Specifies the Databinding framework.
     * Valid values are
     *  - xmlbeans -> XMLBEANS,
     *  - adb      -> ADB,
     *  - jibx     -> JIBX, and
     *  - none     -> NONE.
     *  Default is adb.
     */
    @Optional
    @Input
    String databindingMethod = Databinding.ADB.toString()

    /**
     * Generates all the classes. This option is valid only if serverSide otpion is true. If the value is true,
     * the client code (stubs) will also be generated along with the skeleton.
     */
    @Optional
    @Input
    boolean generateAllClasses = false

    /**
     * Unpack classes. This option specifies whether to unpack the classes and
     * generate separate classes for the databinders.
     */
    @Optional
    @Input
    boolean unpackClasses = false

    /**
     * Specifies the service name to be code generated. If the service name is not specified,
     * then the first service will be picked.
     */
    @Optional
    @Input
    String serviceName

    /**
     * Specifies the port name to be code generated. If the port name is not specified,
     * then the first port (of the selected service) will be picked.
     */
    @Optional
    @Input
    String portName

    /**
     * Generate an interface for the service skeleton.
     */
    @Optional
    @Input
    boolean serversideInterface	= false

    /**
     * WSDL Version. Valid Options : 2, 2.0, 1.1
     */
    @Optional
    @Input
    String wsdlVersion

    /**
     * Flattens the generated files
     */
    @Optional
    @Input
    boolean flattenFiles = false

    /**
     * Switch on un-wrapping, if this value is true.
     */
    @Optional
    @Input
    boolean unwrapParams = false

    /**
     * Use XMLBeans .xsdconfig file if this value is true.
     * This is only valid if  databindingMethod is 'xmlbeans'.
     */
    @Optional
    @Input
    boolean xsdconfig = false

    /**
     * Generate code for all ports
     */
    @Optional
    @Input
    boolean allPorts = false

    /**
     * Generate Axis 1.x backword compatible code
     */
    @Optional
    @Input
    boolean backwordCompatible = false

    /**
     * Suppress namespace prefixes (Optimzation that reduces size of soap request/response)
     */
    @Optional
    @Input
    boolean suppressPrefixes = false

    /**
     * Don't generate a MessageReceiver in the generated sources
     */
    @Optional
    @Input
    boolean noMessageReceiver = false

    /**
     * Additional arguments
     */
    @Optional
    @Input
    List<String> addArgs = []

    /**
     * A directory path for generated sources
     */
    @OutputDirectory
    File  outputDirectory

    List<JavaExecHandleBuilder> prepareExec()
    {
       File file = getWsdlFile()
       List<JavaExecHandleBuilder> list = new ArrayList<JavaExecHandleBuilder>()

        if (file.isDirectory())
        {
            file.eachFile() { fileItem ->

                JavaExecHandleBuilder exechandler = generateWsdl(fileItem)
                list.add(exechandler)
            }
        }
        else
        {
            JavaExecHandleBuilder exechandler = generateWsdl(file)
            list.add(exechandler)
        }
        return list
    }



    /**
     * Prepares the JavaExecHandlerBuilder for the task.
     *
     * @return JavaExecHandleBuilder
     */
        JavaExecHandleBuilder generateWsdl(File file) {
        JavaExecHandleBuilder javaExec = new JavaExecHandleBuilder(getFileResolver())
        getJavaOptions().copyTo(javaExec)

        FileCollection axis2CodegenConfiguration = getProject().getConfigurations().getAt(WSDLExtension.WSDLAXIS2_CONFIGURATION_NAME)

        List<String> args = []

        addAttribute(args, file.getAbsolutePath(), '-uri')

            addAttribute(args, 'java', '--language')
        addAttribute(args, getPackageName(), '--package')

        addFlag(args, getAsync(), '--async')
        if(! getAsync()) {
            addFlag(args, getSync(), '--sync')
        }
        if(getAsync() && getSync()) {
            project.logger.warn('Generate code only for async style, because "async" was set to true')
        }

        addFlag(args, getGenerateTestcase(), '--test-case')
        addFlag(args, getServerSide(), '--server-side')
        addFlag(args, getServiceDescription(), '--service-description')
        addAttribute(args, getDatabindingMethod(), '--databinding-method')
        addFlag(args, getGenerateAllClasses(),'--generate-all')
        addFlag(args, getUnpackClasses(), '--unpack-classes')
        addAttribute(args, getServiceName(), '--service-name')
        addAttribute(args, getPortName(), '--port-name')

        if (getNamespacePackageMapping()) {
            String attr = ''
            getNamespacePackageMapping().each {
                attr += "${it.key}=${it.value},"
            }
            addAttribute(args, attr.subSequence(0, attr.length() - 1).toString(), '--namespace2package')
        }

        addFlag(args, getServersideInterface(), '--serverside-interface')
        addAttribute(args, getWsdlVersion(), '--wsdl-version')


        addAttribute(args, 'src' , '--source-folder')
        addAttribute(args, 'resources', '--resource-folder')

        addAttribute(args, getOutputDirectory()?.absolutePath, '--output')
        if(getOutputDirectory()) {
            getOutputDirectory().mkdirs()
        }

        addAttribute(args, getNamespacePackageMappingFile()?.absolutePath, '--external-mapping')
        addFlag(args, getFlattenFiles(), '--flatten-files')
        addFlag(args, getUnwrapParams(), '--unwrap-params')

        if(getDatabindingMethod() == Databinding.XMLBEANS.toString()) {
            addFlag(args, getXsdconfig(), '-xsdconfig')
        }

        addFlag(args, getAllPorts(), '--all-ports')
        addFlag(args, true, '--over-ride')
        addFlag(args, getBackwordCompatible(), '--backword-compatible')
        addFlag(args, getSuppressPrefixes(), '--suppress-prefixes')
        addFlag(args, true, '--noBuildXML')
        addFlag(args, true, '--noWSDL')
        addFlag(args, getNoMessageReceiver(), '--noMessageReceiver')

        addArgs.each {
            args << it
        }

        return javaExec
                .setClasspath(axis2CodegenConfiguration)
                .setMain(MAIN_CLASS_NAME)
                .setArgs(args)
    }

}

