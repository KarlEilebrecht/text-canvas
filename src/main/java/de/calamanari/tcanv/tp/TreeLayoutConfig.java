//@formatter:off
/*
 * TreeLayoutConfig
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

/**
 * A {@link TreeLayoutConfig} controls how to print the nodes of the tree and the spacing among them.
 * 
 * @param horizontalSpacing number of space characters horizontally between two elements
 * @param verticalSpacing number of space characters vertically between two elements
 * @param maxNodeWidth maximum horizontal size of a box representing a single node
 * @param maxNodeWidth maximum vertical size of a box representing a single node
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public record TreeLayoutConfig(int horizontalSpacing, int verticalSpacing, int maxNodeWidth, int maxNodeHeight) {

    /**
     * Default number of characters between two nodes in a tree horizontally
     */
    public static final int DEFAULT_HORIZONTAL_SPACING = 3;

    /**
     * Default number of characters between two nodes in a tree vertically
     */
    public static final int DEFAULT_VERTICAL_SPACING = 3;

    /**
     * Default maximum horizontal size of a single node in a tree: {@value} characters (including an optional border)
     */
    public static final int DEFAULT_MAX_NODE_WIDTH = 25;

    /**
     * Default maximum vertical size of a single node in a tree: {@value} lines (including an optional border)
     */
    public static final int DEFAULT_MAX_NODE_HEIGHT = 5;

    /**
     * @return default settings
     */
    public static TreeLayoutConfig getDefault() {
        return new TreeLayoutConfig(DEFAULT_HORIZONTAL_SPACING, DEFAULT_VERTICAL_SPACING, DEFAULT_MAX_NODE_WIDTH, DEFAULT_MAX_NODE_HEIGHT);
    }

    /**
     * @return default settings for an index layout (length=100, extra vertical spacing 1)
     */
    public static TreeLayoutConfig index() {
        return new TreeLayoutConfig(2, 1, 100, DEFAULT_MAX_NODE_HEIGHT);
    }

    /**
     * @return default settings for an index layout (length=100, no vertical spacing)
     */
    public static TreeLayoutConfig indexSlim() {
        return new TreeLayoutConfig(1, 0, 100, DEFAULT_MAX_NODE_HEIGHT);
    }

    /**
     * @return default settings for an index layout (length=100, extra vertical spacing 1)
     */
    public static TreeLayoutConfig indexWide() {
        return new TreeLayoutConfig(5, 1, 100, DEFAULT_MAX_NODE_HEIGHT);
    }

}