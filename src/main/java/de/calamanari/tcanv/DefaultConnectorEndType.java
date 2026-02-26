//@formatter:off
/*
 * DefaultConnectorEndType
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
 * Default set of connector end types.
 * <p>
 * The enum constants read as follows: <i>position</i>_<i>symbol</i>, where position is the location a line starts/ends at one of the {@link BoxSide}s of an
 * imaginary box.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public enum DefaultConnectorEndType implements ConnectorEndType {

    /**
     * <pre>
     *             
     *         +---------+
     *         |         |
     *    -----|         |
     *         |         |
     *         +---------+
     * 
     * </pre>
     */
    LEFT_PLAIN(BoxSide.LEFT, '-', false),

    /**
     * <pre>
     *             
     *         +---------+
     *         |         |
     *    ----&gt;|         |
     *         |         |
     *         +---------+
     * 
     * </pre>
     */
    LEFT_ARROW(BoxSide.LEFT, '>', true),

    /**
     * <pre>
     *             
     *         +---------+
     *         |         |
     *    ----+|         |
     *         |         |
     *         +---------+
     * 
     * </pre>
     */
    LEFT_PLUS(BoxSide.LEFT, '+', true),

    /**
     * <pre>
     *             
     *    +---------+
     *    |         |
     *    |         |-----
     *    |         |
     *    +---------+
     * 
     * </pre>
     */
    RIGHT_PLAIN(BoxSide.RIGHT, '-', false),

    /**
     * <pre>
     *             
     *    +---------+
     *    |         |
     *    |         |&lt;----
     *    |         |
     *    +---------+
     * 
     * </pre>
     */
    RIGHT_ARROW(BoxSide.RIGHT, '<', true),

    /**
     * <pre>
     *             
     *    +---------+
     *    |         |
     *    |         |+----
     *    |         |
     *    +---------+
     * 
     * </pre>
     */
    RIGHT_PLUS(BoxSide.RIGHT, '+', true),

    /**
     * <pre>
     *         |
     *         |             
     *    +---------+
     *    |         |
     *    +---------+
     * 
     * </pre>
     */
    TOP_PLAIN(BoxSide.TOP, '|', false),

    /**
     * <pre>
     *         |
     *         V             
     *    +---------+
     *    |         |
     *    +---------+
     * 
     * </pre>
     */
    TOP_ARROW(BoxSide.TOP, 'V', true),

    /**
     * <pre>
     *         |
     *         +             
     *    +---------+
     *    |         |
     *    +---------+
     * 
     * </pre>
     */
    TOP_PLUS(BoxSide.TOP, '+', true),

    /**
     * <pre>
     *    +---------+
     *    |         |
     *    +---------+
     *         |
     *         |
     * 
     * </pre>
     */
    BOTTOM_PLAIN(BoxSide.BOTTOM, '|', false),

    /**
     * <pre>
     *    +---------+
     *    |         |
     *    +---------+
     *         A
     *         |
     * 
     * </pre>
     */
    BOTTOM_ARROW(BoxSide.BOTTOM, 'A', true),

    /**
     * <pre>
     *    +---------+
     *    |         |
     *    +---------+
     *         +
     *         |
     * 
     * </pre>
     */
    BOTTOM_PLUS(BoxSide.BOTTOM, '+', true);

    private final BoxSide side;
    private final char symbol;
    private final boolean specialEndSymbol;

    DefaultConnectorEndType(BoxSide side, char symbol, boolean specialEndSymbol) {
        this.side = side;
        this.symbol = symbol;
        this.specialEndSymbol = specialEndSymbol;
    }

    @Override
    public char symbol() {
        return symbol;
    }

    @Override
    public boolean hasSpecialEndSymbol() {
        return specialEndSymbol;
    }

    @Override
    public BoxSide side() {
        return side;
    }

}