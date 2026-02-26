//@formatter:off
/*
 * DefaultBoxStyle
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
 * Enumeration of standard box styles.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public enum DefaultBoxStyle implements BoxStyle {

    /**
     * <pre>
     *      +-------+
     *      |       |
     *      | label |
     *      |       |
     *      +-------+
     * </pre>
     */
    THIN('-', '|', '+', BoxSide.values()),

    /**
     * <pre>
     *      =========
     *      =       =
     *      = label =
     *      =       =
     *      =========
     * </pre>
     */
    DOUBLE('=', '=', '=', BoxSide.values()),

    /**
     * <pre>
     *      #########
     *      #       #
     *      # label #
     *      #       #
     *      #########
     * </pre>
     */
    HASH('#', '#', '#', BoxSide.values()),

    /**
     * <pre>
     *      *********
     *      *       *
     *      * label *
     *      *       *
     *      *********
     * </pre>
     */
    ASTERISK('*', '*', '*', BoxSide.values()),

    /**
     * <pre>
     *      .........
     *      :       :
     *      : label :
     *      :       :
     *      .........
     * </pre>
     */
    DOTTED('.', ':', '.', BoxSide.values()),

    /**
     * <pre>
     *      :::::::::
     *      :       :
     *      : label :
     *      :       :
     *      :::::::::
     * </pre>
     */
    DOTTED_2(':', ':', ':', BoxSide.values()),

    /**
     * No border lines printed
     */
    NONE(' ', ' ', ' '),

    /**
     * <pre>
     *      
     *      
     *        label
     * 
     *      ---------
     * </pre>
     */
    BOTTOM('-', ' ', '-', BoxSide.BOTTOM),

    /**
     * <pre>
     *      
     *      
     *        label
     * 
     *      =========
     * </pre>
     */
    BOTTOM_DOUBLE('=', ' ', '=', BoxSide.BOTTOM),

    /**
     * <pre>
     *      ---------
     *      
     *        label
     * 
     * 
     * </pre>
     */
    TOP('-', ' ', '-', BoxSide.TOP),

    /**
     * <pre>
     *      ---------
     *              
     *        label 
     *             
     *      ---------
     * </pre>
     */
    TOP_AND_BOTTOM('-', ' ', '-', BoxSide.TOP, BoxSide.BOTTOM),

    /**
     * <pre>
     *      =========
     *      
     *        label
     * 
     * 
     * </pre>
     */
    TOP_DOUBLE('=', ' ', '=', BoxSide.TOP),

    /**
     * <pre>
     *      =========
     *              
     *        label 
     *             
     *      =========
     * </pre>
     */
    TOP_AND_BOTTOM_DOUBLE('=', ' ', '=', BoxSide.TOP, BoxSide.BOTTOM),

    /**
     * <pre>
     *      =========
     *      |       | 
     *      | label |
     *      |       |
     *      =========
     * </pre>
     */
    TOP_AND_BOTTOM_DOUBLE_SIDE_THIN('=', '|', '=', BoxSide.values()),

    /**
     * Label text surrounded by a frame of space characters.
     */
    SPACE(' ', ' ', ' ', BoxSide.values());

    private final char cornerChar;
    private final char horizontalLineChar;
    private final char verticalLineChar;
    private final BoxSide[] sides;

    DefaultBoxStyle(char horizontalLineChar, char verticalLineChar, char cornerChar, BoxSide... sides) {
        this.horizontalLineChar = horizontalLineChar;
        this.verticalLineChar = verticalLineChar;
        this.cornerChar = cornerChar;
        this.sides = sides;
    }

    @Override
    public char cornerChar() {
        return cornerChar;
    }

    @Override
    public char horizontalLineChar() {
        return horizontalLineChar;
    }

    @Override
    public char verticalLineChar() {
        return verticalLineChar;
    }

    @Override
    public boolean hasSideLine(BoxSide side) {
        for (BoxSide bSide : sides) {
            if (bSide == side) {
                return true;
            }
        }
        return false;
    }

}