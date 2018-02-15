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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

class WSDL2JavaRunner @Inject constructor(val paramList: List<String>) : Runnable {

    companion object {
        val log: Logger = LoggerFactory.getLogger(WSDL2JavaRunner::class.java.name)
        const val MAIN_CLASS_NAME = "org.apache.axis.wsdl.WSDL2Java"
    }

    override fun run() {
        //SystemExitControl.forbidSystemExitCall()

        //try {
            org.apache.axis.wsdl.WSDL2Java.main(paramList.toTypedArray())
        //} catch (e: SystemExitControl.ExitTrappedException) {

        //}
        //SystemExitControl.enableSystemExitCall()
    }
}