//@formatter:off
/*
 * VerticalTreeDrawingPolicy
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
import java.util.Arrays;
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
 * A {@link VerticalTreeDrawingPolicy} draws a tree into a canvas.
 * <p>
 * By default, the root node will be printed at the top with the siblings below. Alternatively (<code>bottomUp=true</code>), the root can be printed at the
 * bottoms spawning the tree above.
 * <p>
 * 
 * <pre>
             +---+                +---+  +---+  +---+ 
             | A |                | B |  | C |  | D |  
             +---+                +---+  +---+  +---+  
               |                    |      |      |    
        +------+------+             +------+------+ 
        |      |      |                    |    
      +---+  +---+  +---+                +---+    
      | B |  | C |  | D |                | A |     
      +---+  +---+  +---+                +---+
 * </pre>
 * <p>
 * <b>Important:</b> Instances are <b>stateful</b>. They may be reused (strictly sequentially) but not concurrently.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class VerticalTreeDrawingPolicy extends AbstractStandardTreeDrawingPolicy {

    /**
     * For a clean layout all nodes of a level are vertically aligned. Therefore, this list stores the effective maximum height of each level of the tree.
     */
    private final List<Integer> levelMaxSimpleNodeHeight = new ArrayList<>();

    /**
     * This flag controls whether we draw the tree from the top to the bottom (default, <code>false</code>) or vice-versa (<code>true</code>).
     */
    private final boolean bottomUp;

    /**
     * Creates a custom policy from the given settings
     * 
     * @param frameConfig
     * @param treeLayoutConfig
     * @param bottomUp if true, the root node will be printed at the bottom of the diagram, otherwise at the top (default)
     */
    public VerticalTreeDrawingPolicy(FrameConfig frameConfig, TreeLayoutConfig treeLayoutConfig, boolean bottomUp) {
        super(frameConfig, treeLayoutConfig);
        this.bottomUp = bottomUp;
    }

    /**
     * Determines the maximum simple node height per level and updates the cache.
     * 
     * @param level current tree level
     * @param simpleNodeHeight current node's height
     */
    private void updateMaxSimpleNodeHeight(int level, int simpleNodeHeight) {
        // ensure capacity
        while (levelMaxSimpleNodeHeight.size() <= level) {
            levelMaxSimpleNodeHeight.add(0);
        }
        levelMaxSimpleNodeHeight.set(level, Math.max(levelMaxSimpleNodeHeight.get(level), simpleNodeHeight));
    }

    /**
     * Creates a standard horizontal gap to be displayed for a missing node
     * 
     * @param numberOfSiblings
     * @param simpleNodeWidth
     * @return gap
     */
    private List<String> createHorizontalGap(int numberOfSiblings, int simpleNodeWidth) {
        String format = "%" + (numberOfSiblings == 2 ? simpleNodeWidth * 2 : 3) + "s";
        return Arrays.asList(String.format(format, ""));
    }

    /**
     * Computes the width of the sub-tree below this node recursively
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
                subWidth = updateSubTreeWidth(key, node, numberOfSiblings, i, simpleNodeWidth, subWidth, maxDepth);
            }
            if (numberOfSiblings > 1) {
                subWidth = subWidth + (treeLayoutConfig.horizontalSpacing() / 2);
            }
        }
        else if (numberOfSiblings > 0) {
            // appendix width
            subWidth = 3;
        }
        return Math.max(simpleNodeWidth, subWidth) + (siblingParentRelation.parentNumberOfSiblings() > 1 ? treeLayoutConfig.horizontalSpacing() : 0);
    }

    /**
     * Considers this sibling in total width computation (recursively)
     * 
     * @param key
     * @param node
     * @param numberOfSiblings
     * @param siblingIdx
     * @param simpleParentWidth
     * @param currentSubWidth current total horizontal width
     * @param maxDepth maximum level of the tree to be fully drawn
     * @return updated current total width
     */
    private int updateSubTreeWidth(NodeKey key, PrintableTreeNode node, int numberOfSiblings, int siblingIdx, int simpleParentWidth, int currentSubWidth,
            int maxDepth) {
        PrintableTreeNode sibling = node.getSiblingNode(siblingIdx);
        NodeKey siblingKey = key.sibling(siblingIdx);
        if (sibling != PrintableTreeNode.MISSING_SIBLING && siblingKey.length() <= maxDepth) {
            int subTreeWidth = getSubTreeWidth(siblingKey, sibling, new DefaultSiblingParentRelation(numberOfSiblings, siblingIdx), maxDepth)
                    + (siblingIdx > 0 ? 1 : 0);
            int relativePositionX = currentSubWidth;
            if (subTreeWidth < simpleParentWidth && numberOfSiblings == 1) {
                relativePositionX = relativePositionX + ((simpleParentWidth - subTreeWidth) / 2) + (treeLayoutConfig.horizontalSpacing() / 2);
            }
            else if (isSpacingRequired(siblingKey, numberOfSiblings)) {
                relativePositionX = relativePositionX + (treeLayoutConfig.horizontalSpacing() / 2);
            }
            currentSubWidth = currentSubWidth + subTreeWidth;
            NodeFormatInfo info = nodeFormatInfoCache.get(siblingKey);
            nodeFormatInfoCache.put(siblingKey, info.withPositionX(relativePositionX));
        }
        else {
            List<String> gap = createHorizontalGap(numberOfSiblings, simpleParentWidth);
            nodeFormatInfoCache.put(key.sibling(siblingIdx), NodeFormatInfo.gapInfo(gap, currentSubWidth, 0));
            currentSubWidth = currentSubWidth + gap.get(0).length() - (siblingIdx < numberOfSiblings - 1 ? 1 : 0);
        }
        return currentSubWidth;
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
                PrintableTreeNode sibling = node.getSiblingNode(i);
                if (sibling != PrintableTreeNode.MISSING_SIBLING) {
                    subHeight = Math.max(subHeight, getSubTreeHeight(key.sibling(i), sibling, new DefaultSiblingParentRelation(numberOfSiblings, i), maxDepth));
                }
            }
        }
        else if (numberOfSiblings > 0) {
            // appendix height
            subHeight = 2;
            updateMaxSimpleNodeHeight(key.length() - 1, simpleNodeHeight + 2);
        }
        updateMaxSimpleNodeHeight(key.length() - 1, simpleNodeHeight);
        return simpleNodeHeight + subHeight + treeLayoutConfig.verticalSpacing();
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
        if (bottomUp) {
            int absX = absParentX + (parentWidth / 2);
            int absY = absParentY - 1;
            canvas.setCursor(absX, absY);
            canvas.write("|");
            canvas.setCursor(absX - 1, absY - 1);
            canvas.write("...");
        }
        else {
            int absX = absParentX + (parentWidth / 2);
            int absY = absParentY + parentHeight;
            canvas.setCursor(absX, absY);
            canvas.write("|");
            canvas.setCursor(absX - 1, absY + 1);
            canvas.write("...");
        }
    }

    /**
     * Draws the sub-tree starting at the given node
     * 
     * @param canvas
     * @param key
     * @param parentRelation
     * @param maxDepth maximum level of the tree to be fully drawn
     * @param widthOffset horizontal offset (depends on the remainder of the tree)
     */
    private void drawSubTree(TextCanvas canvas, NodeKey key, ParentRelation parentRelation, int widthOffset) {
        int heightOffset = computeAbsPositionY(canvas, key);

        NodeFormatInfo info = nodeFormatInfoCache.get(key);
        int absTotalX = widthOffset + (isSpacingRequired(key, parentRelation.parentNumberOfSiblings()) ? (treeLayoutConfig.horizontalSpacing() / 2) : 0)
                + info.positionX();
        int absLocalX = absTotalX + (info.totalWidth() / 2) - (info.simpleWidth() / 2);
        int absTotalY = heightOffset + info.positionY();

        drawNodeRepresentation(canvas, absLocalX, absTotalY, info);

        if (parentRelation.parentKey().isValid()) {
            drawParentConnector(canvas, key, info, parentRelation, widthOffset, heightOffset);
        }

        info.node().decorateNode(parentRelation, canvas, absLocalX, absTotalY, info.simpleWidth(), info.simpleHeight());

        if (info.drawPlaceholderAppendix()) {
            drawPlaceholderAppendix(canvas, absLocalX, absTotalY, info.simpleWidth(), info.simpleHeight());
        }
        else if (info.hasSiblings()) {
            drawSiblings(canvas, key, info, absTotalX);
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

        int absTotalX = widthOffset + (isSpacingRequired(key, parentRelation.parentNumberOfSiblings()) ? (treeLayoutConfig.horizontalSpacing() / 2) : 0)
                + info.positionX();
        int absLocalX = absTotalX + (info.totalWidth() / 2) - (info.simpleWidth() / 2);

        int absParentNodeBottomStartX = widthOffset + (parentInfo.totalWidth() / 2) - (parentInfo.simpleWidth() / 2);
        int absParentNodeBottomMidX = absParentNodeBottomStartX + (parentInfo.simpleWidth() / 2);
        int lineStartX = absParentNodeBottomMidX;
        int lineStartY = computeConnectorStartY(canvas, parentRelation.parentKey(), parentInfo);
        int lineEndX = absLocalX + (info.simpleWidth() / 2);
        int lineEndY = computeConnectorEndY(heightOffset, info);

        if (parentRelation.parentNumberOfSiblings() == 1) {
            lineStartX = lineEndX;
        }
        canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN,
                DEFAULT_LINE_CROSSING_RESOLVER);

        if (bottomUp) {
            info.node().decorateParentConnector(parentRelation, canvas, new BoxConnectionPoint(BoxSide.TOP, lineStartX, lineStartY),
                    new BoxConnectionPoint(BoxSide.BOTTOM, lineEndX, lineEndY));
        }
        else {
            info.node().decorateParentConnector(parentRelation, canvas, new BoxConnectionPoint(BoxSide.BOTTOM, lineStartX, lineStartY),
                    new BoxConnectionPoint(BoxSide.TOP, lineEndX, lineEndY));
        }
    }

    /**
     * Draws the sibling nodes recursively.
     * 
     * @param canvas
     * @param key
     * @param info
     * @param widthOffset horizontal offset (depends on the remainder of the tree)
     */
    private final void drawSiblings(TextCanvas canvas, NodeKey key, NodeFormatInfo info, int widthOffset) {
        int numberOfSiblings = info.siblingNodeKeys().length;
        for (int i = 0; i < numberOfSiblings / 2; i++) {
            NodeKey siblingKey = key.sibling(i);
            NodeFormatInfo siblingInfo = nodeFormatInfoCache.get(siblingKey);
            PrintableTreeNode sibling = siblingInfo.node();
            if (sibling != PrintableTreeNode.MISSING_SIBLING) {
                drawSubTree(canvas, siblingKey, new DefaultParentRelation(key, numberOfSiblings, i), widthOffset);
            }
        }
        for (int i = numberOfSiblings - 1; i > (numberOfSiblings / 2); i--) {
            NodeKey siblingKey = key.sibling(i);
            NodeFormatInfo siblingInfo = nodeFormatInfoCache.get(siblingKey);
            PrintableTreeNode sibling = siblingInfo.node();
            if (sibling != PrintableTreeNode.MISSING_SIBLING) {
                drawSubTree(canvas, siblingKey, new DefaultParentRelation(key, numberOfSiblings, i), widthOffset);
            }
        }
        int mid = numberOfSiblings / 2;
        NodeKey siblingKey = key.sibling(mid);
        NodeFormatInfo siblingInfo = nodeFormatInfoCache.get(siblingKey);
        PrintableTreeNode sibling = siblingInfo.node();
        if (sibling != PrintableTreeNode.MISSING_SIBLING) {
            drawSubTree(canvas, siblingKey, new DefaultParentRelation(key, numberOfSiblings, mid), widthOffset);
        }
    }

    /**
     * Computes the vertical position of the connector start between parent and sibling
     * 
     * @param canvas
     * @param parentKey
     * @param parentInfo
     * @return vertical position to place the line start point of the connector
     */
    private final int computeConnectorStartY(TextCanvas canvas, NodeKey parentKey, NodeFormatInfo parentInfo) {
        if (bottomUp) {
            return computeAbsPositionY(canvas, parentKey) - 1;
        }
        else {
            return computeAbsPositionY(canvas, parentKey) + parentInfo.positionY() + parentInfo.simpleHeight();
        }
    }

    /**
     * Computes the vertical position of the connector end between parent and sibling
     * 
     * @param heightOffset
     * @param info
     * @return vertical position to place the line end point of the connector
     */
    private final int computeConnectorEndY(int heightOffset, NodeFormatInfo info) {
        if (bottomUp) {
            return heightOffset + info.positionY() + info.simpleHeight();
        }
        else {
            return heightOffset + info.positionY() - 1;
        }
    }

    /**
     * Computes the node's absolute upper left corner's vertical position
     * 
     * @param canvas
     * @param key
     * @return absolute Y of the upper left corner
     */
    private int computeAbsPositionY(TextCanvas canvas, NodeKey key) {
        int res = 0;
        for (int i = 0; i < key.length() - 1; i++) {
            res = res + levelMaxSimpleNodeHeight.get(i) + treeLayoutConfig.verticalSpacing();
        }
        if (bottomUp) {
            int drawingHeight = canvas.getHeight() - frameConfig.indentTop() - frameConfig.indentBottom();
            res = drawingHeight - res - nodeFormatInfoCache.get(key).simpleHeight();
        }
        res = res + frameConfig.indentTop();
        return res;
    }

    /**
     * @return width of the canvas to draw the tree
     */
    private int computeCanvasWidth() {
        return (!nodeFormatInfoCache.containsKey(NodeKey.root()) ? 1 : nodeFormatInfoCache.get(NodeKey.root()).totalWidth()) + frameConfig.indentLeft()
                + frameConfig.indentRight();
    }

    /**
     * @return height of the canvas to draw the tree
     */
    private int computeCanvasHeight() {
        return (levelMaxSimpleNodeHeight.isEmpty() ? 1
                : levelMaxSimpleNodeHeight.stream().collect(Collectors.summingInt(i -> i))
                        + ((levelMaxSimpleNodeHeight.size() - 1) * treeLayoutConfig.verticalSpacing()))
                + frameConfig.indentTop() + frameConfig.indentBottom();
    }

    @Override
    public CanvasFormat scan(PrintableTreeNode rootNode, int maxDepth) {
        this.nodeFormatInfoCache.clear();
        this.levelMaxSimpleNodeHeight.clear();
        if (rootNode == null) {
            nodeFormatInfoCache.put(NodeKey.root(), NULL_INFO);
            updateMaxSimpleNodeHeight(0, 1);
        }
        else if (rootNode == PrintableTreeNode.MISSING_SIBLING) {
            nodeFormatInfoCache.put(NodeKey.root(), MISSING_INFO);
            updateMaxSimpleNodeHeight(0, 1);
        }
        else if (maxDepth == 0) {
            nodeFormatInfoCache.put(NodeKey.root(), MORE_INFO);
            updateMaxSimpleNodeHeight(0, 1);
        }
        else {
            getOrCreateNodeFormatInfo(NodeKey.root(), rootNode, DefaultParentRelation.NONE, maxDepth);
        }
        return new CanvasFormat(computeCanvasWidth(), computeCanvasHeight());
    }

    @Override
    public void draw(TextCanvas canvas) {
        if (!handleDefaults(canvas)) {
            drawSubTree(canvas, NodeKey.root(), DefaultParentRelation.NONE, frameConfig.indentLeft());
        }
    }

}
