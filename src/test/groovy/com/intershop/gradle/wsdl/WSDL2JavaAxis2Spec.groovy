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
package com.intershop.gradle.wsdl

import com.intershop.gradle.test.AbstractIntegrationSpec

class WSDL2JavaAxis2Spec extends AbstractIntegrationSpec {

    
/*
    
    def 'should set all parameters in task axis2'() {
        when:
        plugin.apply(project)
        project.extensions.wsdl.axis2Configs {
            testconfiguration {
                wsdlFile = "staticfiles/test.wsdl"
                packageName = "com.intershop.wsdl"
                namespacePackageMapping = ["a" : "com.intershop.a", "b" : "com.intershop.b", "c" : "com.intershop.c"]
                unpackClasses = true
                mode = "SERVER"
                generateServiceDeploymentDescriptor = true
                axisVersion = "12345"
                databindingName = "xmlbeans"
                language = "language"
                serviceName = "serviceName"
                portName = "portName"
                synchronizationMode = "SYNC"
                generateTestcase = true
                generateAllClasses = true
                namespacePackageMappingFile = "namespacePackageMappingFile.config"
                wsdlVersion = "2.0"
                noWrapping = true
                
            }
        }
        WSDL2JavaTask task = project.tasks.findByName("wsdl2java2Testconfiguration")
    
        
        then:
        task
        task.wsdlFile == project.file("staticfiles/test.wsdl")
        task.outputDirectory == project.file("build/generated/wsdl")
        task.language == "language"
        task.packageName == "com.intershop.wsdl"
        task.databindingName == "xmlbeans"
        task.serviceName == "serviceName"
        task.portName == "portName"
        task.synchronizationMode == WSDL2JavaTask.SynchronizationMode.SYNC
        task.generateServiceDeploymentDescriptor == true
        task.generateTestcase == true
        task.generateAllClasses == true
        task.namespacePackageMappingFile == project.file("namespacePackageMappingFile.config")
        task.namespacePackageMapping == ["a" : "com.intershop.a", "b" : "com.intershop.b", "c" : "com.intershop.c"]
        task.wsdlVersion == "2.0"
        task.unpackClasses == true
        task.noWrapping == true
        task.mode == WSDL2JavaTask.Mode.SERVER
        task.axisVersion == "12345"
        
        task.arguments == [
            "-uri",
            "${project.file('staticfiles/test.wsdl')}",
            "-o",
            "${project.file('build/generated/wsdl')}",
            "-l",
            "language",
            "-p",
            "com.intershop.wsdl",
            "-ss",
            "-sd",
            "-d",
            "xmlbeans",
            "-u",
            "-f",
            "--noBuildXML",
            "--noWSDL",
            "-sn",
            "serviceName",
            "-pn",
            "portName",
            "-s",
            "-t",
            "-g",
            "-wv",
            "2.0",
            "-em",
            "${project.file('namespacePackageMappingFile.config')}",
            "-uw",
            "-ns2p",
            "a=com.intershop.a,b=com.intershop.b,c=com.intershop.c"
        ]
        
    }
    
    def 'should handle null parameters in task axis2'() {
        when:
        plugin.apply(project)
        project.extensions.wsdl.axis2Configs {
            testconfiguration {
                
                
            }
        }
        WSDL2JavaTask task = project.tasks.findByName("wsdl2java2Testconfiguration")
    
        
        then:
        task
        task.wsdlFile == null
        task.outputDirectory == project.file("build/generated/wsdl")
        task.language == "java"
        task.packageName == null
        task.databindingName == "adb"
        task.serviceName == null
        task.portName == null
        task.synchronizationMode == null
        task.generateServiceDeploymentDescriptor == null
        task.generateTestcase == null
        task.generateAllClasses == Boolean.TRUE
        task.namespacePackageMappingFile == null
        task.namespacePackageMapping == null
        task.wsdlVersion == null
        task.unpackClasses == true
        task.noWrapping == null
        task.mode == null
        task.axisVersion == "1.6.2"
        
        task.arguments == [
            '-o',
            "${project.file('build/generated/wsdl')}",
            '-l',
            'java',
            '-d',
            'adb',
            '-u',
            '-f',
            '--noBuildXML',
            '--noWSDL',
            '-g'
        ]
    }
    */
}
