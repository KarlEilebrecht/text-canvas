//@formatter:off
/*
 * ConnectorShape
 * Copyright 2026 Karl Eilebrecht
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"):
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
//@formatter:on

package de.calamanari.tcanv;

/**
 * Enumeration of all supported connector shapes (ways to connect two points assuming boxes are connected on a canvas)
 */
enum ConnectorShape {

    /**
     * Direct horizontal line without angles.
     * <p>
     * 
     * <pre>
     * (1)-------------(2)
     * </pre>
     */
    H_LINE,

    /**
     * Line with a single angle, first segment horizontal, second segment vertical.
     * <p>
     * 
     * <pre>
     * (1)-------+          +-------(1)       (2)                         (2)
     *           |          |                  |                           |
     *           |    or    |            or    |            or             |
     *           |          |                  |                           |
     *          (2)        (2)                 +-------(1)       (1)-------+
     * </pre>
     */
    HV_LINE,

    /**
     * Double-angled line, first segment horizontal, second vertical, third horizontal
     * 
     * <pre>
     * (1)---+                  +---(1)
     *       |                  |
     *       |        or        |
     *       |                  |
     *       +---(2)      (2)---+
     * </pre>
     */
    HVH_LINE,

    /**
     * C-shaped connection (side-to-side), first segment horizontal, second vertical, third horizontal
     * 
     * <pre>
     *  +---(1)
     *  |
     *  |  
     *  |
     *  +---(2)
     * </pre>
     */
    HVHC_LINE,

    /**
     * Turned C-shaped connection (side-to-side), first segment horizontal, second vertical, third horizontal
     * 
     * <pre>
     * (1)---+ 
     *       | 
     *       | 
     *       | 
     * (2)---+
     * </pre>
     */
    HVHCT_LINE,

    /**
     * Direct vertical line without angles.
     * <p>
     * 
     * <pre>
     * o
     * |
     * |
     * </pre>
     */
    V_LINE,

    /**
     * Line with a single angle, first segment vertical, second segment horizontal.
     * <p>
     * 
     * <pre>
     * (1)                         (1)        (2)-------+          +-------(2)
     *  |                           |                   |          |
     *  |            or             |     or            |    or    |    
     *  |                           |                   |          |
     *  +-------(2)       (2)-------+                  (1)        (1)
     * </pre>
     */
    VH_LINE,

    /**
     * Double-angled line, first segment vertical, second horizontal, third vertical
     * 
     * <pre>
     * (1)              (2)
     *  |                |
     *  |                |
     *  +---+   or   +---+
     *      |        |
     *      |        |
     *     (2)      (1)
     * </pre>
     */
    VHV_LINE,

    /**
     * U-shaped connection (top-top, bottom-bottom), first segment vertical, second horizontal, third vertical
     * 
     * <pre>
     * (1)        (2)
     *  |          | 
     *  |          | 
     *  |          | 
     *  +----------+
     * </pre>
     */
    VHVU_LINE,

    /**
     * Turned U-shaped connection (top-top, bottom-bottom), first segment vertical, second horizontal, third vertical
     * 
     * <pre>
     *  +----------+
     *  |          |
     *  |          |
     *  |          |
     * (1)        (2)
     * </pre>
     */
    VHVUT_LINE;

}