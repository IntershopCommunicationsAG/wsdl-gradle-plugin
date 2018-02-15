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
package com.intershop.gradle.wsdl.tasks.axis2

import org.apache.axis2.wsdl.WSDL2Code
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

class WSDL2JavaRunner @Inject constructor(private val paramList: List<String>) : Runnable {

    companion object {
        val log: Logger = LoggerFactory.getLogger(WSDL2JavaRunner::class.java.name)
    }

    override fun run() {
        println(paramList)
        WSDL2Code.main(paramList.toTypedArray())
    }
}