//@formatter:off
/*
 * NodeFormatInfo
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.calamanari.tcanv.BoxStyle;
import de.calamanari.tcanv.DefaultBoxStyle;

/**
 * A {@link NodeFormatInfo} contains initially collected information about a node to be drawn.
 * 
 * @param node reference to the original node (for accessing the decoration callbacks)
 * @param boxStyle layout for this node's box
 * @param representation the exact textual representation of this node (a rectangle, all lines of the same length, optional surrounding box included)
 * @param siblingNodeKeys keys of this node's sibling nodes
 * @param totalWidth the horizontal size of a node including the size of its sub-tree
 * @param totalHeight the vertical size of a node including the size of its sub-tree
 * @param positionX relative horizontal position of the node considering its neighbors (earlier printed siblings, related to the same parent node)
 * @param positionY relative vertical position of the node considering its neighbors (earlier printed siblings, related to the same parent node)
 * @param isMissing if true than this is a missing sibling that should be drawn as a gap
 * @param drawPlaceholderAppendix if true this tree could not be drawn completely (max depth reached) and the node has further siblings that should be depicted
 *            as '...'
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public record NodeFormatInfo(PrintableTreeNode node, BoxStyle boxStyle, List<String> representation, NodeKey[] siblingNodeKeys, int totalWidth, int totalHeight,
        int positionX, int positionY, boolean isMissing, boolean drawPlaceholderAppendix) {

    /**
     * Pseudo info for missing root node
     */
    protected static final NodeFormatInfo MISSING_INFO = NodeFormatInfo.gapInfo(Collections.unmodifiableList(Arrays.asList(" ")), 1, 1);

    /**
     * Pseudo info for <code>null</code>
     */
    protected static final NodeFormatInfo NULL_INFO = NodeFormatInfo.gapInfo(Collections.unmodifiableList(Arrays.asList("      ")), 6, 1);

    /**
     * Pseudo info for <code>...</code>
     */
    protected static final NodeFormatInfo MORE_INFO = NodeFormatInfo.gapInfo(Collections.unmodifiableList(Arrays.asList("   ")), 3, 1);

    /**
     * @param node reference to the original node (for accessing the decoration callbacks)
     * @param boxStyle layout for this node's box
     * @param representation the exact textual representation of this node (a rectangle, all lines of the same length, optional surrounding box included)
     * @param siblingNodeKeys keys of this node's sibling nodes
     * @param totalWidth the horizontal size of a node including the size of its sub-tree
     * @param totalHeight the vertical size of a node including the size of its sub-tree
     * @param isMissing if true than this is a missing sibling that should be drawn as a gap
     * @param drawPlaceholderAppendix if true this tree could not be drawn completely (max depth reached) and the node has further siblings that should be
     *            depicted as '...'
     */
    public NodeFormatInfo(PrintableTreeNode node, BoxStyle boxStyle, List<String> representation, NodeKey[] siblingNodeKeys, int totalWidth, int totalHeight,
            boolean isMissing, boolean drawPlaceholderAppendix) {
        this(node, boxStyle, representation, siblingNodeKeys, totalWidth, totalHeight, 0, 0, isMissing, drawPlaceholderAppendix);
    }

    /**
     * Supplementary function to create a placeholder info for a missing node
     * 
     * @param gap the local gap
     * @param totalWidth
     * @param totalHeight
     * @return node info for missing node
     */
    public static NodeFormatInfo gapInfo(List<String> gap, int totalWidth, int totalHeight) {
        return new NodeFormatInfo(PrintableTreeNode.MISSING_SIBLING, DefaultBoxStyle.NONE, gap, new NodeKey[0], totalWidth, totalHeight, true, false);
    }

    /**
     * Creates a new node info with the given horizontal position.
     * 
     * @param positionX relative horizontal position of the node considering its neighbors (earlier printed siblings, related to the same parent node)
     * @return new node info
     */
    public NodeFormatInfo withPositionX(int positionX) {
        return new NodeFormatInfo(this.node, this.boxStyle, this.representation, this.siblingNodeKeys, this.totalWidth, this.totalHeight, positionX,
                this.positionY, this.isMissing, this.drawPlaceholderAppendix);
    }

    /**
     * Creates a new node info with the given vertical position.
     * 
     * @param positionY relative vertical position of the node considering its neighbors (earlier printed siblings, related to the same parent node)
     * @return new node info
     */
    public NodeFormatInfo withPositionY(int positionY) {
        return new NodeFormatInfo(this.node, this.boxStyle, this.representation, this.siblingNodeKeys, this.totalWidth, this.totalHeight, this.positionX,
                positionY, this.isMissing, this.drawPlaceholderAppendix);
    }

    /**
     * @return width of the textual representation of the node, optional surrounding box included
     */
    public int simpleWidth() {
        return representation.isEmpty() ? 0 : representation.get(0).length();
    }

    /**
     * @return height of the textual representation of the node, optional surrounding box included
     */
    public int simpleHeight() {
        return representation.size();
    }

    /**
     * @return true if this node has sibling nodes
     */
    public boolean hasSiblings() {
        return siblingNodeKeys.length > 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(representation);
        result = prime * result + Arrays.hashCode(siblingNodeKeys);
        result = prime * result + Objects.hash(boxStyle, drawPlaceholderAppendix, isMissing, node, positionX, positionY, totalHeight, totalWidth);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NodeFormatInfo other = (NodeFormatInfo) obj;
        return Objects.equals(boxStyle, other.boxStyle) && drawPlaceholderAppendix == other.drawPlaceholderAppendix && isMissing == other.isMissing
                && Objects.equals(node, other.node) && positionX == other.positionX && positionY == other.positionY
                && Objects.equals(representation, other.representation) && Arrays.equals(siblingNodeKeys, other.siblingNodeKeys)
                && totalHeight == other.totalHeight && totalWidth == other.totalWidth;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [node=" + node + ", boxStyle=" + boxStyle + ", representation=" + Objects.toString(representation)
                + ", siblingNodeKeys=" + Arrays.toString(siblingNodeKeys) + ", totalWidth=" + totalWidth + ", totalHeight=" + totalHeight + ", positionX="
                + positionX + ", positionY=" + positionY + ", isMissing=" + isMissing + ", drawPlaceholderAppendix=" + drawPlaceholderAppendix + "]";
    }

}