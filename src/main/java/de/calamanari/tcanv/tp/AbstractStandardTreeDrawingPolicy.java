//@formatter:off
/*
 * AbstractStandardTreeDrawingPolicy
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

import de.calamanari.tcanv.FrameConfig;
import de.calamanari.tcanv.TextAlignment;
import de.calamanari.tcanv.TextCanvas;

import static de.calamanari.tcanv.tp.NodeFormatInfo.MISSING_INFO;
import static de.calamanari.tcanv.tp.NodeFormatInfo.MORE_INFO;
import static de.calamanari.tcanv.tp.NodeFormatInfo.NULL_INFO;

/**
 * Base functionality for tree drawing policies with the ability to cache information about the nodes to be displayed.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public abstract class AbstractStandardTreeDrawingPolicy implements TreeDrawingPolicy {

    /**
     * Character conflict resolver that avoids replacing '|' with '-' and vice-versa. Crossings will be represented by '+'.
     */
    protected static final BinaryOperator<Character> DEFAULT_LINE_CROSSING_RESOLVER = (characterFound, characterToBeWritten) -> {
        switch (characterFound) {
        case '+':
            return '+';
        case '|':
            return characterToBeWritten == '-' ? '+' : characterToBeWritten;
        case '-':
            return characterToBeWritten == '|' ? '+' : characterToBeWritten;
        default:
            return characterToBeWritten;
        }
    };

    /**
     * Required to compute the dimensions of the canvas and the absolute positions
     */
    protected final FrameConfig frameConfig;

    /**
     * Configures the node dimensions and the gap size among them
     */
    protected final TreeLayoutConfig treeLayoutConfig;

    /**
     * Stores dimensional information about all the nodes in the tree to be drawn, see {@link #scan(PrintableTreeNode, int)}
     */
    protected final Map<NodeKey, NodeFormatInfo> nodeFormatInfoCache = new HashMap<>();

    /**
     * Canvas to prepare node representations
     * <p>
     * The problem is that we need to render a node to determine its exact dimensions required to compute the canvas to later insert the node representation.
     * Instead of rendering the node twice, we render it into the temp canvas and extract/cache the representation until we can copy it into the target canvas
     * at the final position.
     */
    protected final TextCanvas tempCanvas;

    /**
     * Creates a custom policy from the given settings
     * 
     * @param frameConfig
     * @param treeLayoutConfig
     */
    protected AbstractStandardTreeDrawingPolicy(FrameConfig frameConfig, TreeLayoutConfig treeLayoutConfig) {
        if (frameConfig == null || treeLayoutConfig == null) {
            throw new IllegalArgumentException(String.format(
                    "Arguments frameConfig and treeLayoutConfig are mandatory, given: frameConfig=%s, treeLayoutConfig=%s", frameConfig, treeLayoutConfig));
        }
        this.frameConfig = frameConfig;
        this.treeLayoutConfig = treeLayoutConfig;
        this.tempCanvas = new TextCanvas(treeLayoutConfig.maxNodeWidth(), treeLayoutConfig.maxNodeHeight());
    }

    /**
     * Creates a rectangular node representation (set of lines, padded with spaces) of the given node based on the label, the box style and the configured
     * limits.
     * 
     * @param node
     * @param siblingParentRelation
     * @return node representation to be printed later
     */
    protected List<String> createNodeRepresentation(PrintableTreeNode node, SiblingParentRelation siblingParentRelation) {
        int simpleNodeWidth = Math.min(treeLayoutConfig.maxNodeWidth(), node.getPrintWidth(siblingParentRelation, treeLayoutConfig.maxNodeWidth()));
        int simpleNodeHeight = Math.min(treeLayoutConfig.maxNodeHeight(), node.getPrintHeight(siblingParentRelation, treeLayoutConfig.maxNodeHeight()));
        tempCanvas.clear();
        tempCanvas.drawBox(node.getBoxStyle(siblingParentRelation), simpleNodeWidth, simpleNodeHeight, node.getNodeLabel(), getTextAlignment());
        List<String> res = new ArrayList<>(simpleNodeHeight);
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < simpleNodeHeight; y++) {
            sb.setLength(0);
            tempCanvas.setCursor(0, y);
            for (int x = 0; x < simpleNodeWidth; x++) {
                sb.append((char) tempCanvas.read(true));
            }
            res.add(sb.toString());
        }
        return res;
    }

    /**
     * @return text alignment used for label formatting
     */
    protected TextAlignment getTextAlignment() {
        return TextAlignment.CENTER_CENTER;
    }

    /**
     * Creates a list of the keys of this node's sibling node keys, this will later allow traversal for drawing without bothering the node again
     * 
     * @param key
     * @param node
     * @return list of sibling node keys
     */
    protected NodeKey[] createSiblingNodeKeyList(NodeKey key, PrintableTreeNode node) {
        int numberOfSiblings = node.getNumberOfSiblings();
        if (numberOfSiblings == 0) {
            return new NodeKey[0];
        }
        NodeKey[] res = new NodeKey[numberOfSiblings];
        for (int i = 0; i < numberOfSiblings; i++) {
            res[i] = key.sibling(i);
        }
        return res;
    }

    /**
     * Obtains the node info from the cache or creates it if not present
     * 
     * @param key
     * @param node
     * @param siblingParentRelation
     * @param maxDepth maximum level of the tree to be fully drawn
     * @return node info
     */
    protected NodeFormatInfo getOrCreateNodeFormatInfo(NodeKey key, PrintableTreeNode node, SiblingParentRelation siblingParentRelation, int maxDepth) {
        NodeFormatInfo info = nodeFormatInfoCache.get(key);
        if (info == null) {
            int totalWidth = computeSubTreeWidth(key, node, siblingParentRelation, maxDepth);
            int totalHeight = computeSubTreeHeight(key, node, siblingParentRelation, maxDepth);
            boolean drawPlaceholderAppendix = node.getNumberOfSiblings() > 0 && key.length() == maxDepth;

            info = new NodeFormatInfo(node, node.getBoxStyle(siblingParentRelation), createNodeRepresentation(node, siblingParentRelation),
                    createSiblingNodeKeyList(key, node), totalWidth, totalHeight, false, drawPlaceholderAppendix);
            nodeFormatInfoCache.put(key, info);
        }
        return info;
    }

    /**
     * Obtains the width of the sub-tree below this node
     * 
     * @param key
     * @param node
     * @param siblingParentRelation
     * @param maxDepth maximum level of the tree to be fully drawn
     * @return horizontal size of all siblings and sibling's siblings combined
     */
    protected int getSubTreeWidth(NodeKey key, PrintableTreeNode node, SiblingParentRelation siblingParentRelation, int maxDepth) {
        NodeFormatInfo info = getOrCreateNodeFormatInfo(key, node, siblingParentRelation, maxDepth);
        return info.totalWidth();
    }

    /**
     * Obtains the height of the sub-tree below this node
     * 
     * @param key
     * @param node
     * @param siblingParentRelation
     * @param maxDepth maximum level of the tree to be fully drawn
     * @return vertical size of all siblings and sibling's siblings combined
     */
    protected int getSubTreeHeight(NodeKey key, PrintableTreeNode node, SiblingParentRelation siblingParentRelation, int maxDepth) {
        NodeFormatInfo info = getOrCreateNodeFormatInfo(key, node, siblingParentRelation, maxDepth);
        return info.totalHeight();
    }

    /**
     * Draws the prepared node representation at the the given coordinates (upper left corner
     * 
     * @param canvas
     * @param x
     * @param y
     * @param info
     */
    protected void drawNodeRepresentation(TextCanvas canvas, int x, int y, NodeFormatInfo info) {
        for (int i = 0; i < info.representation().size(); i++) {
            canvas.setCursor(x, y + i);
            canvas.write(info.representation().get(i));
        }
    }

    /**
     * Computes the width of the sub-tree below this node
     * 
     * @param key
     * @param node
     * @param siblingParentRelation
     * @param maxDepth maximum level of the tree to be fully drawn
     * @return horizontal size of all siblings and sibling's siblings combined
     */
    protected abstract int computeSubTreeWidth(NodeKey key, PrintableTreeNode node, SiblingParentRelation siblingParentRelation, int maxDepth);

    /**
     * Computes the height of the sub-tree below this node
     * 
     * @param key
     * @param node
     * @param siblingParentRelation
     * @param maxDepth maximum level of the tree to be fully drawn
     * @return vertical size of all siblings and sibling's siblings combined
     */
    protected abstract int computeSubTreeHeight(NodeKey key, PrintableTreeNode node, SiblingParentRelation siblingParentRelation, int maxDepth);

    /**
     * Tells whether we need to add extra space before this element.
     * <p>
     * The default implementation returns true if this is not the zero-path (outer left) and there are more than one sibling at this node's parent.<br>
     * The first condition avoids unnecessary initial indentation, the second rule takes into account that if there is just one sibling then we don't need extra
     * indentation.
     * 
     * @param key
     * @param parentNumberOfSiblings
     * @return true if we need to insert some whitespace before the current node and the node before
     */
    protected boolean isSpacingRequired(NodeKey key, int parentNumberOfSiblings) {
        if (key.isPathOfZeros()) {
            return false;
        }
        return parentNumberOfSiblings > 1;
    }

    /**
     * Handles default drawing, for example root node was missing or null
     * 
     * @param canvas
     * @return true if this is a default scenario, nothing further to be drawn
     */
    protected boolean handleDefaults(TextCanvas canvas) {
        canvas.drawBox(frameConfig.boxStyle(), canvas.getWidth(), canvas.getHeight());
        NodeFormatInfo rootNodeInfo = nodeFormatInfoCache.get(NodeKey.root());
        if (rootNodeInfo == null || rootNodeInfo == NULL_INFO) {
            canvas.setCursor(frameConfig.indentLeft(), frameConfig.indentTop());
            canvas.write("<null>");
            return true;
        }
        else if (rootNodeInfo == MORE_INFO) {
            canvas.setCursor(frameConfig.indentLeft(), frameConfig.indentTop());
            canvas.write("...");
            return true;
        }
        else if (rootNodeInfo == MISSING_INFO) {
            return true;
        }
        return false;
    }

}
