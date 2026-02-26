//@formatter:off
/*
 * FrameConfig
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
 * A {@link FrameConfig} bundles the settings of a frame (area around a rectangle, optionally surrounded by a printed border).
 * 
 * @param boxStyle to print a visible frame around a rectangle
 * @param indentLeft space to left before printing a diagram (includes the space required for printing the surrounding box)
 * @param indentTop space at the top before printing a diagram (includes the space required for printing the surrounding box)
 * @param indentRight space to right after printing a diagram (includes the space required for printing the surrounding box)
 * @param indentBottom space at the bottom after printing a diagram (includes the space required for printing the surrounding box)
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public record FrameConfig(BoxStyle boxStyle, int indentLeft, int indentTop, int indentRight, int indentBottom) {

    /**
     * @return default setting with a thin box surrounding the diagram, horizontal indent 2 characters, vertical indent 1 character
     */
    public static FrameConfig getDefault() {
        return new FrameConfig(DefaultBoxStyle.THIN, 2, 1, 2, 1);
    }

    /**
     * @return thin box surrounding the diagram, leaving 10 characters to left and to the right empty, resp. 5 characters at the top and at the bottom
     */
    public static FrameConfig frame10x5() {
        return new FrameConfig(DefaultBoxStyle.THIN, 10, 5, 10, 5);
    }

}