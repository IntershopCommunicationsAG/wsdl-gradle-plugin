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

class WSDL2JavaAxis1Spec extends AbstractIntegrationSpec {

    /*
    
    def 'should set all parameters in task axis1, all on'() {
        when:
        plugin.apply(project)
        project.extensions.wsdl.axis1Configs {
            testconfiguration {
                wsdlFile = "MyFile.wsdl"
                packageName = "com.intershop.wsdl"
                namespacePackageMapping = ["a" : "com.intershop.a", "b" : "com.intershop.b", "c" : "com.intershop.c"]
                namespacePackageMappingFile = "wsdl/package.mapping"
                wrapArrays = true
                noWrapping = true
                generateAllClasses = true
                axisVersion = "12345"
                timeout = -1
                userName = "UserName"
                password = "password"
                generateNoImports = true
            }
        }
        WSDL2JavaTask task = project.tasks.findByName("wsdl2java1Testconfiguration")
        def argumentReference = [
            project.file("MyFile.wsdl"),
            "-o" + project.file("build/generated/wsdl"),
            "-n",
            "-O-1",
            "-W",
            "-f" +  project.file("wsdl/package.mapping"),
            "-pcom.intershop.wsdl",
            "-a",
            "-w",
            "-UUserName",
            "-Ppassword",
            "-Na=com.intershop.a,b=com.intershop.b,c=com.intershop.c"
            ]
        
        if (logger.infoEnabled) {
            argumentReference << "-v"
        }
        
        if (logger.debugEnabled || logger.traceEnabled) {
            argumentReference << "-D"
        }
        
        then:
        task
        task.wsdlFile == project.file("MyFile.wsdl")
        task.packageName == "com.intershop.wsdl"
        task.namespacePackageMapping == ["a" : "com.intershop.a", "b" : "com.intershop.b", "c" : "com.intershop.c"]
        task.namespacePackageMappingFile == project.file("wsdl/package.mapping")
        task.wrapArrays == true
        task.noWrapping == true
        task.generateAllClasses == true
        task.axisVersion == "12345"
        task.timeout == -1
        task.userName == "UserName"
        task.password == "password"
        task.generateNoImports == Boolean.TRUE
        task.arguments == argumentReference
    }
    
    def 'should set all parameters in task axis1, all off'() {
        when:
        plugin.apply(project)
        project.extensions.wsdl.axis1Configs {
            testconfiguration {
                wsdlFile = "MyFile.wsdl"
                packageName = "com.intershop.wsdl"
                namespacePackageMapping = ["a" : "com.intershop.a", "b" : "com.intershop.b", "c" : "com.intershop.c"]
                namespacePackageMappingFile = "wsdl/package.mapping"
                wrapArrays = false
                noWrapping = false
                generateAllClasses = false
                axisVersion = "12345"
                timeout = -1
                userName = "UserName"
                password = "password"
                generateNoImports = false
            }
        }
        WSDL2JavaTask task = project.tasks.findByName("wsdl2java1Testconfiguration")
        def argumentReference = [
            project.file("MyFile.wsdl"),
            "-o" + project.file("build/generated/wsdl"),
            "-O-1",
            "-f" +  project.file("wsdl/package.mapping"),
            "-pcom.intershop.wsdl",
            "-UUserName",
            "-Ppassword",
            "-Na=com.intershop.a,b=com.intershop.b,c=com.intershop.c"
            ]
        
        if (logger.infoEnabled) {
            argumentReference << "-v"
        }
        
        if (logger.debugEnabled || logger.traceEnabled) {
            argumentReference << "-D"
        }
        
        then:
        task
        task.wsdlFile == project.file("MyFile.wsdl")
        task.packageName == "com.intershop.wsdl"
        task.namespacePackageMapping == ["a" : "com.intershop.a", "b" : "com.intershop.b", "c" : "com.intershop.c"]
        task.namespacePackageMappingFile == project.file("wsdl/package.mapping")
        task.wrapArrays == false
        task.noWrapping == false
        task.generateAllClasses == false
        task.axisVersion == "12345"
        task.timeout == -1
        task.userName == "UserName"
        task.password == "password"
        task.generateNoImports == false
        task.arguments == argumentReference
    }
    
    
    def 'should handle null values parameters in task axis1'() {
        when:
        plugin.apply(project)
        project.extensions.wsdl.axis1Configs {
            testconfiguration {
                
            }
        }
        Task task = project.tasks.findByName("wsdl2java1Testconfiguration")
    
        def argumentReference = [
            null,
            "-o" + project.file("build/generated/wsdl"),
            "-O240",
            "-a"
            ]
        
        if (logger.infoEnabled) {
            argumentReference << "-v"
        }
        
        if (logger.debugEnabled || logger.traceEnabled) {
            argumentReference << "-D"
        }
        
        
        then:
        task
        task.packageName == null
        task.namespacePackageMapping == null
        task.namespacePackageMappingFile == null
        task.wrapArrays == null
        task.noWrapping == null
        task.generateAllClasses == Boolean.TRUE
        task.axisVersion == "1.4"
        task.timeout == 240
        task.userName == null
        task.password == null
        task.generateNoImports == null
        
        task.arguments == argumentReference
    }
    */
}
