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

    /** Only generate given WSDL and no imports. */
    Boolean generateNoImports;

    /** Timeout. */
    Integer timeout

    /**
     * Prefer generating JavaBean classes like ArrayOfString
     * or certain schema array patterns (default is to use String []))
     */
    Boolean wrapArrays

    /** UserName to access an external WSDL. */
    String userName

    /** Password to access the WSDL-URI. */
    String password

    Axis1(String name) {
        super(name)
    }

    String getTaskName() {
        "axis1Wsdl2java" + GUtil.toCamelCase(name);
    }

    Integer getTimeout() {
        return (timeout) ? timeout : 240;
    }
}
