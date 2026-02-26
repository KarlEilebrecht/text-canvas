//@formatter:off
/*
 * TreePrinter
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

import de.calamanari.tcanv.TextCanvas;

/**
 * {@link TreePrinter} allows to print any tree-like structure in textual form.
 * <p>
 * This implementation abstracts the boiler-plate code and provides a simple interface {@link PrintableTreeNode} that can be implemented for any type of
 * top-down navigable data structure. As a result the structure can be printed quickly in textual form (e.g., to the command-line), which may help debugging.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class TreePrinter {

    /**
     * Drawing policy of this printer
     */
    private final TreeDrawingPolicy treeDrawingPolicy;

    /**
     * Creates a printer with the given custom policy
     * 
     * @param treeDrawingPolicy
     */
    public TreePrinter(TreeDrawingPolicy treeDrawingPolicy) {
        this.treeDrawingPolicy = treeDrawingPolicy;
    }

    /**
     * Creates a printer with the given default layout
     * 
     * @param layout
     */
    public TreePrinter(TreeLayout layout) {
        this(layout.createDrawingPolicy());
    }

    /**
     * Creates a printer with default settings: {@link TreeLayout#TOP_DOWN}
     */
    public TreePrinter() {
        this(TreeLayout.TOP_DOWN);
    }

    /**
     * Creates a new canvas, draws the tree and returns the canvas (full depth).
     * 
     * @param rootNode
     * @return canvas with the diagram
     */
    public TextCanvas print(PrintableTreeNode rootNode) {
        return print(rootNode, Integer.MAX_VALUE);
    }

    /**
     * Creates a new canvas, draws the tree and returns the canvas.
     * 
     * @param rootNode to start at
     * @param maxDepth maxiumum number of levels to be fully drawn
     * @return canvas with the diagram
     */
    public TextCanvas print(PrintableTreeNode rootNode, int maxDepth) {
        TextCanvas canvas = new TextCanvas(treeDrawingPolicy.scan(rootNode, maxDepth));
        treeDrawingPolicy.draw(canvas);
        return canvas;
    }

}
