//@formatter:off
/*
 * HorizontalTreeDrawingPolicy
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
import java.util.List;
import java.util.stream.Collectors;

import de.calamanari.tcanv.BoxConnectionPoint;
import de.calamanari.tcanv.BoxSide;
import de.calamanari.tcanv.CanvasFormat;
import de.calamanari.tcanv.DefaultConnectorEndType;
import de.calamanari.tcanv.FrameConfig;
import de.calamanari.tcanv.TextCanvas;

import static de.calamanari.tcanv.tp.NodeFormatInfo.MISSING_INFO;
import static de.calamanari.tcanv.tp.NodeFormatInfo.MORE_INFO;
import static de.calamanari.tcanv.tp.NodeFormatInfo.NULL_INFO;

/**
 * A {@link HorizontalTreeDrawingPolicy} draws a tree into a canvas.
 * <p>
 * By default, the root node will be printed at the left side with the siblings to the right. Alternatively (<code>rightToLeft=true</code>), the root can be
 * printed at the right side spawning the tree to the left.
 * <p>
 * 
 * <pre>
                +---+        +---+        
             +--+ B |        | B +--+         
             |  +---+        +---+  |          
             |                      |       
      +---+  |  +---+        +---+  |  +---+       
      | A |--+--+ C |        | C |--+--+ A |       
      +---+  |  +---+        +---+  |  +---+        
             |                      |
             |  +---+        +---+  |       
             +--+ D |        | D +--+         
                +---+        +---+
 * </pre>
 * <p>
 * <b>Important:</b> Instances are <b>stateful</b>. They may be reused (strictly sequentially) but not concurrently.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class HorizontalTreeDrawingPolicy extends AbstractStandardTreeDrawingPolicy {

    /**
     * For a clean layout all nodes of a level are vertically aligned. Therefore, this list stores the effective maximum height of each level of the tree.
     */
    private final List<Integer> levelMaxSimpleNodeWidth = new ArrayList<>();

    /**
     * This flag controls whether we draw the tree from the left to the right (default, <code>false</code>) or vice-versa (<code>true</code>).
     */
    private final boolean rightToLeft;

    /**
     * Creates a custom policy from the given settings
     * 
     * @param frameConfig
     * @param treeLayoutConfig
     * @param rightToLeft if true, the root node will be printed at the right side of the diagram, otherwise on the left (default)
     */
    public HorizontalTreeDrawingPolicy(FrameConfig frameConfig, TreeLayoutConfig treeLayoutConfig, boolean rightToLeft) {
        super(frameConfig, treeLayoutConfig);
        this.rightToLeft = rightToLeft;
    }

    /**
     * Determines the maximum simple node width per level and updates the cache.
     * 
     * @param level current tree level
     * @param simpleNodeWidth current node's width
     */
    private void updateMaxSimpleNodeWidth(int level, int simpleNodeWidth) {
        // ensure capacity
        while (levelMaxSimpleNodeWidth.size() <= level) {
            levelMaxSimpleNodeWidth.add(0);
        }
        levelMaxSimpleNodeWidth.set(level, Math.max(levelMaxSimpleNodeWidth.get(level), simpleNodeWidth));
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
    @Override
    protected int computeSubTreeWidth(NodeKey key, PrintableTreeNode node, SiblingParentRelation siblingParentRelation, int maxDepth) {
        int simpleNodeWidth = node.getPrintWidth(siblingParentRelation, treeLayoutConfig.maxNodeWidth());
        int numberOfSiblings = node.getNumberOfSiblings();
        int subWidth = 0;
        if (numberOfSiblings > 0 && key.length() < maxDepth) {
            for (int i = 0; i < numberOfSiblings; i++) {
                PrintableTreeNode sibling = node.getSiblingNode(i);
                if (sibling != PrintableTreeNode.MISSING_SIBLING) {
                    subWidth = Math.max(subWidth, getSubTreeWidth(key.sibling(i), sibling, new DefaultSiblingParentRelation(numberOfSiblings, i), maxDepth));
                }
            }
        }
        else if (numberOfSiblings > 0) {
            // appendix width
            subWidth = 2;
            updateMaxSimpleNodeWidth(key.length() - 1, simpleNodeWidth + 2);
        }
        updateMaxSimpleNodeWidth(key.length() - 1, simpleNodeWidth);
        return simpleNodeWidth + subWidth + treeLayoutConfig.horizontalSpacing();
    }

    /**
     * Creates a standard horizontal gap to be displayed for a missing node
     * 
     * @param numberOfSiblings
     * @param simpleNodeHeight
     * @return gap
     */
    private List<String> createVerticalGap(int numberOfSiblings, int simpleNodeHeight) {
        int len = numberOfSiblings == 2 ? simpleNodeHeight * 2 : 3;
        List<String> gap = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            gap.add(" ");
        }
        return gap;
    }

    /**
     * Computes the height of the sub-tree below this node
     * 
     * @param key
     * @param node
     * @param siblingParentRelation
     * @param maxDepth maximum level of the tree to be fully drawn
     * @return vertical size of all siblings and sibling's siblings combined
     */
    @Override
    protected int computeSubTreeHeight(NodeKey key, PrintableTreeNode node, SiblingParentRelation siblingParentRelation, int maxDepth) {
        int simpleNodeHeight = node.getPrintHeight(siblingParentRelation, treeLayoutConfig.maxNodeHeight());
        int numberOfSiblings = node.getNumberOfSiblings();
        int subHeight = 0;

        if (numberOfSiblings > 0 && key.length() < maxDepth) {
            for (int i = 0; i < numberOfSiblings; i++) {
                subHeight = updateSubTreeHeight(key, node, numberOfSiblings, i, simpleNodeHeight, subHeight, maxDepth);
            }
        }
        else if (numberOfSiblings > 0) {
            // appendix height
            subHeight = 3;
        }
        return Math.max(simpleNodeHeight, subHeight) + (siblingParentRelation.parentNumberOfSiblings() > 1 ? (treeLayoutConfig.verticalSpacing() / 2) : 0);
    }

    /**
     * Considers this sibling in total height computation (recursively)
     * 
     * @param key
     * @param node
     * @param numberOfSiblings
     * @param siblingIdx
     * @param simpleParentHeight
     * @param currentSubHeight
     * @param maxDepth
     * @return updated current total height
     */
    private int updateSubTreeHeight(NodeKey key, PrintableTreeNode node, int numberOfSiblings, int siblingIdx, int simpleParentHeight, int currentSubHeight,
            int maxDepth) {
        PrintableTreeNode sibling = node.getSiblingNode(siblingIdx);
        NodeKey siblingKey = key.sibling(siblingIdx);
        if (sibling != PrintableTreeNode.MISSING_SIBLING && siblingKey.length() <= maxDepth) {
            int subTreeHeight = getSubTreeHeight(siblingKey, sibling, new DefaultSiblingParentRelation(numberOfSiblings, siblingIdx), maxDepth)
                    + (siblingIdx > 0 ? 1 : 0);
            int relativePositionY = computeRelativeVerticalPosition(siblingKey, numberOfSiblings, simpleParentHeight, currentSubHeight, subTreeHeight);
            currentSubHeight = currentSubHeight + subTreeHeight;
            if (isSpacingRequired(siblingKey, numberOfSiblings)) {
                currentSubHeight = currentSubHeight + (treeLayoutConfig.verticalSpacing() / 2);
            }
            NodeFormatInfo info = nodeFormatInfoCache.get(siblingKey);
            nodeFormatInfoCache.put(siblingKey, info.withPositionY(relativePositionY));
        }
        else {
            List<String> gap = createVerticalGap(numberOfSiblings, simpleParentHeight);
            nodeFormatInfoCache.put(key.sibling(siblingIdx), NodeFormatInfo.gapInfo(gap, 0, currentSubHeight));
            currentSubHeight = currentSubHeight + gap.size() - (siblingIdx < numberOfSiblings - 1 ? 1 : 0);
        }
        return currentSubHeight;
    }

    /**
     * Derives the relative Y-position from the current sub height and the tree height
     * 
     * @param key
     * @param parentNumberOfSiblings
     * @param simpleParentHeight
     * @param currentSubHeight
     * @param subTreeHeight
     * @return relative vertical position (from the begin of the current element)
     */
    private int computeRelativeVerticalPosition(NodeKey key, int parentNumberOfSiblings, int simpleParentHeight, int currentSubHeight, int subTreeHeight) {
        int relativePositionY = currentSubHeight;
        if (isSpacingRequired(key, parentNumberOfSiblings)) {
            relativePositionY = relativePositionY + (treeLayoutConfig.verticalSpacing() / 2);
        }
        if (parentNumberOfSiblings == 1) {
            relativePositionY = (int) Math.ceil(treeLayoutConfig.verticalSpacing() / 4.0);
        }
        if (subTreeHeight < simpleParentHeight && parentNumberOfSiblings == 1) {
            relativePositionY = relativePositionY + ((simpleParentHeight - subTreeHeight) / 2);
        }
        return relativePositionY;
    }

    /**
     * Given a node that has siblings but the node is already at the maximum depth, the appendix visualizes the existence of further sibling.
     * 
     * @param canvas
     * @param absParentX lower left corner's X of the last printable node to draw the appendix for
     * @param absParentY lower left corner's Y of the last printable node to draw the appendix for
     * @param parentWidth width of the last printable node to draw the appendix for
     * @param parentHeight height of the last printable node to draw the appendix for
     */
    private void drawPlaceholderAppendix(TextCanvas canvas, int absParentX, int absParentY, int parentWidth, int parentHeight) {
        if (rightToLeft) {
            int absX = absParentX - 1;
            int absY = absParentY + (parentHeight / 2);
            canvas.setCursor(absX, absY);
            canvas.write("-");
            for (int i = -1; i < 2; i++) {
                canvas.setCursor(absX - 1, absY + i);
                canvas.write(".");
            }
        }
        else {
            int absX = absParentX + parentWidth;
            int absY = absParentY + (parentHeight / 2);
            canvas.setCursor(absX, absY);
            canvas.write("-");
            for (int i = -1; i < 2; i++) {
                canvas.setCursor(absX + 1, absY + i);
                canvas.write(".");
            }
        }
    }

    /**
     * Draws the sub-tree starting at the given node
     * 
     * @param canvas
     * @param key
     * @param parentRelation
     * @param maxDepth maximum level of the tree to be fully drawn
     * @param heightOffset vertical offset (depends on the remainder of the tree)
     */
    private void drawSubTree(TextCanvas canvas, NodeKey key, ParentRelation parentRelation, int heightOffset) {
        int widthOffset = computeAbsPositionX(canvas, key);

        NodeFormatInfo info = nodeFormatInfoCache.get(key);
        int absTotalX = widthOffset + info.positionX();
        int absTotalY = heightOffset + (isSpacingRequired(key, parentRelation.parentNumberOfSiblings()) ? (treeLayoutConfig.verticalSpacing() / 2) : 0)
                + info.positionY();
        int absLocalY = absTotalY + (info.totalHeight() / 2) - (info.simpleHeight() / 2);

        drawNodeRepresentation(canvas, absTotalX, absLocalY, info);

        if (parentRelation.parentKey().isValid()) {
            drawParentConnector(canvas, key, info, parentRelation, widthOffset, heightOffset);
        }
        info.node().decorateNode(parentRelation, canvas, absTotalX, absLocalY, info.simpleWidth(), info.simpleHeight());

        if (info.drawPlaceholderAppendix()) {
            drawPlaceholderAppendix(canvas, absTotalX, absLocalY, info.simpleWidth(), info.simpleHeight());
        }
        else if (info.hasSiblings()) {
            drawSiblings(canvas, key, info, absTotalY);
        }
    }

    /**
     * Connects any node - except for the start node - to its parent.
     * 
     * @param canvas
     * @param key
     * @param info
     * @param parentRelation
     * @param maxDepth maximum level of the tree to be fully drawn
     * @param widthOffset horizontal offset (depends on the remainder of the tree)
     * @param heightOffset vertical offset (depends on the remainder of the tree)
     */
    private final void drawParentConnector(TextCanvas canvas, NodeKey key, NodeFormatInfo info, ParentRelation parentRelation, int widthOffset,
            int heightOffset) {
        NodeFormatInfo parentInfo = nodeFormatInfoCache.get(parentRelation.parentKey());

        int absTotalY = heightOffset + (isSpacingRequired(key, parentRelation.parentNumberOfSiblings()) ? (treeLayoutConfig.verticalSpacing() / 2) : 0)
                + info.positionY();
        int absLocalY = absTotalY + (info.totalHeight() / 2) - (info.simpleHeight() / 2);

        int absParentNodeRightStartY = heightOffset + (parentInfo.totalHeight() / 2) - (parentInfo.simpleHeight() / 2);
        int absParentNodeRightMidY = absParentNodeRightStartY + (parentInfo.simpleHeight() / 2);
        int lineStartY = absParentNodeRightMidY;
        int lineStartX = computeConnectorStartX(canvas, parentRelation.parentKey(), parentInfo);
        int lineEndX = computeConnectorEndX(widthOffset, info);
        int lineEndY = absLocalY + (info.simpleHeight() / 2);

        if (parentRelation.parentNumberOfSiblings() == 1) {
            lineStartY = lineEndY;
        }
        canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN,
                DEFAULT_LINE_CROSSING_RESOLVER);
        if (rightToLeft) {
            info.node().decorateParentConnector(parentRelation, canvas, new BoxConnectionPoint(BoxSide.LEFT, lineStartX, lineStartY),
                    new BoxConnectionPoint(BoxSide.RIGHT, lineEndX, lineEndY));
        }
        else {
            info.node().decorateParentConnector(parentRelation, canvas, new BoxConnectionPoint(BoxSide.RIGHT, lineStartX, lineStartY),
                    new BoxConnectionPoint(BoxSide.LEFT, lineEndX, lineEndY));
        }
    }

    /**
     * Draws the sibling nodes recursively.
     * 
     * @param canvas
     * @param key
     * @param node
     * @param heightOffset vertical offset (depends on the remainder of the tree)
     */
    private final void drawSiblings(TextCanvas canvas, NodeKey key, NodeFormatInfo info, int heightOffset) {
        int numberOfSiblings = info.siblingNodeKeys().length;
        for (int i = 0; i < numberOfSiblings / 2; i++) {
            NodeKey siblingKey = key.sibling(i);
            NodeFormatInfo siblingInfo = nodeFormatInfoCache.get(siblingKey);
            PrintableTreeNode sibling = siblingInfo.node();
            if (sibling != PrintableTreeNode.MISSING_SIBLING) {
                drawSubTree(canvas, key.sibling(i), new DefaultParentRelation(key, numberOfSiblings, i), heightOffset);
            }
        }
        for (int i = numberOfSiblings - 1; i > (numberOfSiblings / 2); i--) {
            NodeKey siblingKey = key.sibling(i);
            NodeFormatInfo siblingInfo = nodeFormatInfoCache.get(siblingKey);
            PrintableTreeNode sibling = siblingInfo.node();
            if (sibling != PrintableTreeNode.MISSING_SIBLING) {
                drawSubTree(canvas, key.sibling(i), new DefaultParentRelation(key, numberOfSiblings, i), heightOffset);
            }
        }
        int mid = numberOfSiblings / 2;
        NodeKey siblingKey = key.sibling(mid);
        NodeFormatInfo siblingInfo = nodeFormatInfoCache.get(siblingKey);
        PrintableTreeNode sibling = siblingInfo.node();
        if (sibling != PrintableTreeNode.MISSING_SIBLING) {
            drawSubTree(canvas, key.sibling(mid), new DefaultParentRelation(key, numberOfSiblings, mid), heightOffset);
        }
    }

    /**
     * Computes the horizontal position of the connector start between parent and sibling
     * 
     * @param canvas
     * @param parentKey
     * @param parentInfo
     * @return horizontal position to place the line start point of the connector
     */
    private final int computeConnectorStartX(TextCanvas canvas, NodeKey parentKey, NodeFormatInfo parentInfo) {
        if (rightToLeft) {
            return computeAbsPositionX(canvas, parentKey) - 1;
        }
        else {
            return computeAbsPositionX(canvas, parentKey) + parentInfo.positionX() + parentInfo.simpleWidth();
        }
    }

    /**
     * Computes the horizontal position of the connector end between parent and sibling
     * 
     * @param widthOffset
     * @param info
     * @return horizontal position to place the line end point of the connector
     */
    private final int computeConnectorEndX(int widthOffset, NodeFormatInfo info) {
        if (rightToLeft) {
            return widthOffset + info.positionX() + info.simpleWidth();
        }
        else {
            return widthOffset + info.positionX() - 1;
        }
    }

    /**
     * Computes the node's absolute upper left corner's horizontal position
     * 
     * @param canvas
     * @param key
     * @return absolute X of the upper left corner
     */
    private int computeAbsPositionX(TextCanvas canvas, NodeKey key) {
        int res = 0;
        for (int i = 0; i < key.length() - 1; i++) {
            res = res + levelMaxSimpleNodeWidth.get(i) + treeLayoutConfig.horizontalSpacing();
        }
        if (rightToLeft) {
            int drawingWidth = canvas.getWidth() - frameConfig.indentLeft() - frameConfig.indentRight();
            res = drawingWidth - res - nodeFormatInfoCache.get(key).simpleWidth();
        }
        res = res + frameConfig.indentLeft();
        return res;
    }

    /**
     * @return width of the canvas to draw the tree
     */
    public int computeCanvasWidth() {
        return (levelMaxSimpleNodeWidth.isEmpty() ? 1
                : levelMaxSimpleNodeWidth.stream().collect(Collectors.summingInt(i -> i))
                        + ((levelMaxSimpleNodeWidth.size() - 1) * treeLayoutConfig.horizontalSpacing()))
                + frameConfig.indentLeft() + frameConfig.indentRight();
    }

    /**
     * @return height of the canvas to draw the tree
     */
    public int computeCanvasHeight() {
        return (!nodeFormatInfoCache.containsKey(NodeKey.root()) ? 1 : nodeFormatInfoCache.get(NodeKey.root()).totalHeight()) + frameConfig.indentTop()
                + frameConfig.indentBottom();
    }

    @Override
    public CanvasFormat scan(PrintableTreeNode rootNode, int maxDepth) {
        this.nodeFormatInfoCache.clear();
        this.levelMaxSimpleNodeWidth.clear();
        if (rootNode == null) {
            nodeFormatInfoCache.put(NodeKey.root(), NULL_INFO);
            updateMaxSimpleNodeWidth(0, 6);
        }
        else if (rootNode == PrintableTreeNode.MISSING_SIBLING) {
            nodeFormatInfoCache.put(NodeKey.root(), MISSING_INFO);
            updateMaxSimpleNodeWidth(0, 1);
        }
        else if (maxDepth == 0) {
            nodeFormatInfoCache.put(NodeKey.root(), MORE_INFO);
            updateMaxSimpleNodeWidth(0, 3);
        }
        else {
            getOrCreateNodeFormatInfo(NodeKey.root(), rootNode, DefaultParentRelation.NONE, maxDepth);
        }
        return new CanvasFormat(computeCanvasWidth(), computeCanvasHeight());
    }

    @Override
    public void draw(TextCanvas canvas) {
        if (!handleDefaults(canvas)) {
            drawSubTree(canvas, NodeKey.root(), DefaultParentRelation.NONE, frameConfig.indentTop());
        }
    }

}
