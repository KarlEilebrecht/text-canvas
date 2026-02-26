//@formatter:off
/*
 * BoxStyle
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
 * A box is a rectangle with optional surrounding line(s). The box style describes which lines to draw and which symbols to use.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public interface BoxStyle {

    /**
     * @return character to print for a box corner
     */
    char cornerChar();

    /**
     * @return character to print for a horizontal line
     */
    char horizontalLineChar();

    /**
     * @return character to print for a vertical line
     */
    char verticalLineChar();

    /**
     * @param side
     * @return true if the border segment for the given side should be drawn
     */
    boolean hasSideLine(BoxSide side);

    /**
     * @return true if this style does not have a visible border
     */
    default boolean suppressBorder() {
        for (BoxSide side : BoxSide.values()) {
            if (hasSideLine(side)) {
                return false;
            }
        }
        return true;
    }

}