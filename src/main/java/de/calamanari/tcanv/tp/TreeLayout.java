//@formatter:off
/*
 * TreeLayout
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

package de.calamanari.tcanv.tp;

import de.calamanari.tcanv.FrameConfig;

/**
 * Enumeration of some default layouts.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public enum TreeLayout {

    /**
     * Prints the tree with default settings: {@link VerticalTreeDrawingPolicy}, root node at the top
     * <p>
     * 
     * <pre>
     *           +---+
     *           | A |
     *           +---+
     *             |
     *      +------+------+
     *      |      |      |
     *    +---+  +---+  +---+
     *    | B |  | C |  | D |
     *    +---+  +---+  +---+
     * </pre>
     */
    TOP_DOWN,

    /**
     * Prints the tree with default settings: {@link VerticalTreeDrawingPolicy}, root node at the bottom
     * <p>
     * 
     * <pre>
     *    +---+  +---+  +---+
     *    | B |  | C |  | D |
     *    +---+  +---+  +---+ 
     *      |      |      |
     *      +------+------+
     *             |
     *           +---+
     *           | A |
     *           +---+
     * </pre>
     */
    BOTTOM_UP,

    /**
     * Prints the tree with default settings: {@link VerticalTreeDrawingPolicy}, root node at the top
     * <p>
     * 
     * <pre>
     *              +---+
     *           +--+ B |
     *           |  +---+
     *           |
     *    +---+  |  +---+
     *    | A |--+--+ C |
     *    +---+  |  +---+
     *           |
     *           |  +---+
     *           +--+ D |
     *              +---+
     * </pre>
     */
    LEFT_TO_RIGHT,

    /**
     * Prints the tree with default settings: {@link VerticalTreeDrawingPolicy}, root node at the bottom
     * <p>
     * 
     * <pre>
     *    +---+
     *    | B +--+
     *    +---+  |
     *           |
     *    +---+  |  +---+
     *    | C |--+--+ A |
     *    +---+  |  +---+
     *           |
     *    +---+  |
     *    | D +--+
     *    +---+
     * </pre>
     */
    RIGHT_TO_LEFT,

    /**
     * Prints the tree with default settings: {@link IndexTreeDrawingPolicy}
     * <p>
     * 
     * <pre>
     * +--------+
     * |  A     |
     * |  |     |
     * |  +--B  |
     * |  |     |
     * |  +--C  |
     * |  |     |
     * |  +--D  |
     * +--------+
     * </pre>
     */
    INDEX,

    /**
     * Prints the tree with default settings: {@link IndexTreeDrawingPolicy}, minimal indentation
     * <p>
     * 
     * <pre>
     * +--------+
     * |  A     |
     * |  +-B   |
     * |  +-C   |
     * |  +-D   |
     * +--------+
     * </pre>
     */
    INDEX_SLIM,

    /**
     * Prints the tree with default settings: {@link IndexTreeDrawingPolicy}, minimal indentation, no connectors
     * <p>
     * 
     * <pre>
     * +--------+
     * |  A     |
     * |    B   |
     * |    C   |
     * |    D   |
     * +--------+
     * </pre>
     */
    INDEX_SLIM_NO_CONNECTORS,

    /**
     * Prints the tree with default settings: {@link IndexTreeDrawingPolicy}, horizontal indentation 5
     * <p>
     * 
     * <pre>
     *    A
     *    |
     *    +-----B
     *    |
     *    +-----C
     *    |
     *    +-----D
     * </pre>
     */
    INDEX_WIDE;

    /**
     * @return new instance of a drawing policy with standard settings
     */
    public TreeDrawingPolicy createDrawingPolicy() {
        switch (this) {
        case TOP_DOWN:
            return new VerticalTreeDrawingPolicy(FrameConfig.getDefault(), TreeLayoutConfig.getDefault(), false);
        case BOTTOM_UP:
            return new VerticalTreeDrawingPolicy(FrameConfig.getDefault(), TreeLayoutConfig.getDefault(), true);
        case LEFT_TO_RIGHT:
            return new HorizontalTreeDrawingPolicy(FrameConfig.getDefault(), TreeLayoutConfig.getDefault(), false);
        case RIGHT_TO_LEFT:
            return new HorizontalTreeDrawingPolicy(FrameConfig.getDefault(), TreeLayoutConfig.getDefault(), true);
        case INDEX:
            return new IndexTreeDrawingPolicy(FrameConfig.getDefault(), TreeLayoutConfig.index(), false);
        case INDEX_SLIM:
            return new IndexTreeDrawingPolicy(FrameConfig.getDefault(), TreeLayoutConfig.indexSlim(), false);
        case INDEX_SLIM_NO_CONNECTORS:
            return new IndexTreeDrawingPolicy(FrameConfig.getDefault(), TreeLayoutConfig.indexSlim(), true);
        case INDEX_WIDE:
            return new IndexTreeDrawingPolicy(FrameConfig.getDefault(), TreeLayoutConfig.indexWide(), false);
        default:
            throw new IllegalStateException("not implemented");
        }
    }

}