//@formatter:off
/*
 * TreeDrawingPolicy
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

import de.calamanari.tcanv.CanvasFormat;
import de.calamanari.tcanv.TextCanvas;

/**
 * A {@link TreeDrawingPolicy} does the actual drawing to a canvas after scanning a given tree.
 * <p>
 * It is guaranteed that {@link #scan(PrintableTreeNode, int)} will be called before {@link #draw(TextCanvas)} to allow the policy to initially collect all the
 * required information. Hence, policy instances are usually <b>stateful</b> and not suitable for concurrent use. However, it is recommended to implement them
 * to be sequentially reusable (reset before scan).
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public interface TreeDrawingPolicy {

    /**
     * Analyzes the tree of nodes to determine dimensions and key information for the intended layout.
     * 
     * @param rootNode node to start with
     * @param maxDepth maximum number of levels to be fully drawn
     * @return dimensions of the canvas required to draw the graph
     */
    CanvasFormat scan(PrintableTreeNode rootNode, int maxDepth);

    /**
     * Draws the tree graph into the canvas based on the information collected during the scan.
     * 
     * @param canvas target
     */
    void draw(TextCanvas canvas);

}