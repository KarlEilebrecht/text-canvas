//@formatter:off
/*
 * CanvasBoundCheckStrategy
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
 * The {@link CanvasBoundCheckStrategy} determines how to react on requests to draw outside the canvas boundaries.
 */
public enum CanvasBoundCheckStrategy {

    /**
     * If the coordinates to write content are outside the canvas, silently ignore the request.
     * <p>
     * This is mainly intended for debugging to find out what's actually going on.
     */
    IGNORE,

    /**
     * If the coordinates to write content are outside the canvas, raise an exception.
     */
    ERROR;

}