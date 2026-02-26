//@formatter:off
/*
 * CanvasFormat
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
 * Dimensions of a {@link TextCanvas} (tuple of width and height)
 * 
 * @param width number of characters horizontally of the canvas, <code>&gt;0</code>
 * @param height number of characters vertically of the canvas, <code>&gt;0</code>
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public record CanvasFormat(int width, int height) {

    /**
     * @param width number of characters horizontally of the canvas, <code>&gt;0</code>
     * @param height number of characters vertically of the canvas, <code>&gt;0</code>
     * @throws IllegalArgumentException if any of the dimensions is &lt;=0
     */
    public CanvasFormat {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(String.format("expected: width > 0, height > 0, given: width=%d, height=%d", width, height));
        }

    }
}