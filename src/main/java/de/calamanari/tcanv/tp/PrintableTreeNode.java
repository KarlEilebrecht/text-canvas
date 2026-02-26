//@formatter:off
/*
 * PrintableTreeNode
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

import de.calamanari.tcanv.BoxConnectionPoint;
import de.calamanari.tcanv.BoxSide;
import de.calamanari.tcanv.BoxStyle;
import de.calamanari.tcanv.DefaultBoxStyle;
import de.calamanari.tcanv.TextAlignment;
import de.calamanari.tcanv.TextCanvas;

/**
 * A {@link PrintableTreeNode} represents a single node in a tree along with availability to navigate to its siblings.
 * <p>
 * This interface must be implemented for printing custom trees.
 * <p>
 * <b>Important:</b> All methods except for the decoration methods will be called during the <b>scan phase</b> on the original data structure (top-down
 * recursively). The methods {@link #decorateNode(SiblingParentRelation, TextCanvas, int, int, int, int)} and
 * {@link #decorateParentConnector(SiblingParentRelation, TextCanvas, BoxConnectionPoint, BoxConnectionPoint)} will be called during the <b>drawing phase</b>
 * when the core dimensions are clear and the canvas has been setup. Hence, these methods will be called after the source tree traversal has finished.
 * <p>
 * The traversal happens only once but all collected {@link PrintableTreeNode} instances will be cached until the tree graph has been drawn to the a canvas.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public interface PrintableTreeNode {

    /**
     * Placeholder constants for missing siblings (a gap should be printed instead)
     */
    public static final PrintableTreeNode MISSING_SIBLING = new PrintableTreeNode() {

        private static final String MSG_MISSING = "Called on MISSING_SIBLING";

        @Override
        public String getNodeLabel() {
            throw new UnsupportedOperationException(MSG_MISSING);
        }

        @Override
        public int getNumberOfSiblings() {
            throw new UnsupportedOperationException(MSG_MISSING);
        }

        @Override
        public PrintableTreeNode getSiblingNode(int siblingSelector) {
            throw new UnsupportedOperationException(MSG_MISSING);
        }

        @Override
        public BoxStyle getBoxStyle(SiblingParentRelation siblingParentRelation) {
            throw new UnsupportedOperationException(MSG_MISSING);
        }

        @Override
        public void decorateNode(SiblingParentRelation siblingParentRelation, TextCanvas canvas, int upperLeftCornerX, int upperLeftCornerY, int width,
                int height) {
            throw new UnsupportedOperationException(MSG_MISSING);
        }

        @Override
        public void decorateParentConnector(SiblingParentRelation siblingParentRelation, TextCanvas canvas, BoxConnectionPoint from, BoxConnectionPoint to) {
            throw new UnsupportedOperationException(MSG_MISSING);
        }

    };

    /**
     * @return label of this node, lines may be separated by line delimiter
     */
    String getNodeLabel();

    /**
     * Returns the <i>expected</i> number of siblings.
     * <p>
     * The number may be higher than the actual number of siblings. For any sibling that does not exist a gap may be drawn to visualize that this node could
     * have had this sibling but does not. However, if this nodes does not have <i>any</i> siblings to be drawn, then this method should return 0.
     * 
     * @return number of sibling nodes of this node to be obtained by calling {@link #getSiblingNode(int)}
     */
    int getNumberOfSiblings();

    /**
     * Obtains the node corresponding to the given sibling node index
     * 
     * @param siblingSelector (0..n)
     * @return sibling node or {@link #MISSING_SIBLING} to indicate that this sibling is not present (to display gap instead)
     */
    PrintableTreeNode getSiblingNode(int siblingSelector);

    /**
     * Returns the style for the box to be drawn for this node.
     * <p>
     * The parentSiblingSelector may be used to print nodes in different styles depending on their sibling status.
     * 
     * @param siblingParentRelation relation or {@link DefaultParentRelation#NONE} if this node has no parent or the parent is not being printed
     * @return {@link DefaultBoxStyle#THIN} by default
     */
    default BoxStyle getBoxStyle(SiblingParentRelation siblingParentRelation) {
        return DefaultBoxStyle.THIN;
    }

    /**
     * Returns the height of this node based on the box style and the node's label, considering the maximum dimensions per node
     * 
     * @param siblingParentRelation relation or {@link DefaultParentRelation#NONE} if this node has no parent or the parent is not being printed
     * @param maxHeight limit (incl.)
     * @return vertical size of the node in characters
     */
    default int getPrintHeight(SiblingParentRelation siblingParentRelation, int maxHeight) {
        int borderOverhead = (getBoxStyle(siblingParentRelation).hasSideLine(BoxSide.TOP) ? 1 : 0)
                + (getBoxStyle(siblingParentRelation).hasSideLine(BoxSide.BOTTOM) ? 1 : 0);
        return Math.min(maxHeight, TextAlignment.computeTrimmedDimensions(getNodeLabel())[1] + borderOverhead);
    }

    /**
     * Returns the width of this node based on the box style and the node's label, considering the maximum dimensions per node
     * 
     * @param siblingParentRelation relation or {@link DefaultParentRelation#NONE} if this node has no parent or the parent is not being printed
     * @param maxWidth limit (incl.)
     * @return horizontal size of the node in characters
     */
    default int getPrintWidth(SiblingParentRelation siblingParentRelation, int maxWidth) {
        int borderOverhead = (getBoxStyle(siblingParentRelation).hasSideLine(BoxSide.LEFT) ? 1 : 0)
                + (getBoxStyle(siblingParentRelation).hasSideLine(BoxSide.RIGHT) ? 1 : 0);
        return Math.min(maxWidth, TextAlignment.computeTrimmedDimensions(getNodeLabel())[0] + borderOverhead);
    }

    /**
     * This method will be called after the box for a node has been drawn. It allows a specific implementation to decorate the box (e.g., display an additional
     * label at the connector).
     * <p>
     * The default implementation does nothing.
     * 
     * @param siblingParentRelation relation or {@link DefaultParentRelation#NONE} if this node has no parent or the parent is not being printed
     * @param canvas
     * @param upperLeftCornerX box horizontal coordinate
     * @param upperLeftCornerY box vertical coordinate
     * @param width of the box
     * @param height height of the box
     */
    default void decorateNode(SiblingParentRelation siblingParentRelation, TextCanvas canvas, int upperLeftCornerX, int upperLeftCornerY, int width,
            int height) {
        // no-op by default
    }

    /**
     * This method will be called after drawing a connector between a sibling and its parent.
     * <p>
     * The default implementation does nothing.
     * 
     * @param siblingParentRelation
     * @param canvas
     * @param from the connector's start point (at the parent)
     * @param to the connector's end point (at the sibling, this node)
     */
    default void decorateParentConnector(SiblingParentRelation siblingParentRelation, TextCanvas canvas, BoxConnectionPoint from, BoxConnectionPoint to) {
        // no-op by default
    }

}