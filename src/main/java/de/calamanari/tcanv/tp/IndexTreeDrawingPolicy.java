//@formatter:off
/*
 * IndexTreeDrawingPolicy
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

import de.calamanari.tcanv.BoxConnectionPoint;
import de.calamanari.tcanv.BoxSide;
import de.calamanari.tcanv.BoxStyle;
import de.calamanari.tcanv.CanvasFormat;
import de.calamanari.tcanv.DefaultConnectorEndType;
import de.calamanari.tcanv.FrameConfig;
import de.calamanari.tcanv.TextAlignment;
import de.calamanari.tcanv.TextCanvas;

import static de.calamanari.tcanv.tp.NodeFormatInfo.MISSING_INFO;
import static de.calamanari.tcanv.tp.NodeFormatInfo.MORE_INFO;
import static de.calamanari.tcanv.tp.NodeFormatInfo.NULL_INFO;

/**
 * The {@link IndexTreeDrawingPolicy} draws the tree in the form of an index (or directory) from left to right, top down.
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
 * <p>
 * <b>Important:</b> Instances are <b>stateful</b>. They may be reused (strictly sequentially) but not concurrently.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class IndexTreeDrawingPolicy extends AbstractStandardTreeDrawingPolicy {

    /**
     * If true, no lines will be drawn between parents and siblings
     */
    private final boolean suppressConnectors;

    /**
     * Creates a custom policy with the given settings
     * 
     * @param frameConfig
     * @param treeLayoutConfig
     * @param suppressConnectors if true, no connector lines will be drawn between parents and siblings
     */
    public IndexTreeDrawingPolicy(FrameConfig frameConfig, TreeLayoutConfig treeLayoutConfig, boolean suppressConnectors) {
        super(frameConfig, treeLayoutConfig);
        this.suppressConnectors = suppressConnectors;
    }

    @Override
    protected TextAlignment getTextAlignment() {
        return TextAlignment.LEFT_TOP;
    }

    /**
     * @return width of the canvas to draw the tree
     */
    public int computeCanvasWidth() {
        return (!nodeFormatInfoCache.containsKey(NodeKey.root()) ? 1 : nodeFormatInfoCache.get(NodeKey.root()).totalWidth()) + frameConfig.indentLeft()
                + frameConfig.indentRight();
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
        if (rootNode == null) {
            nodeFormatInfoCache.put(NodeKey.root(), NULL_INFO);
        }
        else if (rootNode == PrintableTreeNode.MISSING_SIBLING) {
            nodeFormatInfoCache.put(NodeKey.root(), MISSING_INFO);
        }
        else if (maxDepth == 0) {
            nodeFormatInfoCache.put(NodeKey.root(), MORE_INFO);
        }
        else {
            getOrCreateNodeFormatInfo(NodeKey.root(), rootNode, DefaultParentRelation.NONE, maxDepth);
        }
        return new CanvasFormat(computeCanvasWidth(), computeCanvasHeight());
    }

    @Override
    public void draw(TextCanvas canvas) {
        if (!handleDefaults(canvas)) {
            drawSubTree(canvas, NodeKey.root(), DefaultParentRelation.NONE, frameConfig.indentLeft(), frameConfig.indentTop(), 0);
        }
    }

    @Override
    protected int computeSubTreeWidth(NodeKey key, PrintableTreeNode node, SiblingParentRelation siblingParentRelation, int maxDepth) {
        int simpleNodeWidth = node.getPrintWidth(siblingParentRelation, treeLayoutConfig.maxNodeWidth());
        int numberOfSiblings = node.getNumberOfSiblings();
        int subWidth = (siblingParentRelation.parentSiblingSelector() >= 0 ? (2 * treeLayoutConfig.horizontalSpacing()) : 0);
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
            subWidth = subWidth + 3;
        }
        return Math.max(simpleNodeWidth, (2 * treeLayoutConfig.horizontalSpacing()) + subWidth);
    }

    /**
     * Creates a standard horizontal gap to be displayed for a missing node
     * 
     * @return gap
     */
    private List<String> createVerticalGap() {
        int vSize = Math.min(1, treeLayoutConfig.verticalSpacing());
        List<String> gap = new ArrayList<>(vSize);
        for (int i = 0; i < vSize; i++) {
            gap.add(" ");
        }
        return gap;
    }

    @Override
    protected int computeSubTreeHeight(NodeKey key, PrintableTreeNode node, SiblingParentRelation siblingParentRelation, int maxDepth) {
        int simpleNodeHeight = node.getPrintHeight(siblingParentRelation, treeLayoutConfig.maxNodeHeight());
        int numberOfSiblings = node.getNumberOfSiblings();
        int subHeight = 0;
        if (numberOfSiblings > 0 && key.length() < maxDepth) {
            for (int i = 0; i < numberOfSiblings; i++) {
                PrintableTreeNode sibling = node.getSiblingNode(i);
                NodeKey siblingKey = key.sibling(i);
                int relativePositionY = subHeight;
                if (sibling != PrintableTreeNode.MISSING_SIBLING) {
                    subHeight = subHeight + getSubTreeHeight(key.sibling(i), sibling, new DefaultSiblingParentRelation(numberOfSiblings, i), maxDepth);
                    NodeFormatInfo info = nodeFormatInfoCache.get(siblingKey);
                    nodeFormatInfoCache.put(siblingKey, info.withPositionY(relativePositionY));
                }
                else {
                    List<String> gap = createVerticalGap();
                    nodeFormatInfoCache.put(key.sibling(i), NodeFormatInfo.gapInfo(gap, 0, subHeight));
                    subHeight = subHeight + gap.size() + 1;
                }
            }
        }
        else if (numberOfSiblings > 0) {
            // appendix height
            subHeight = subHeight + treeLayoutConfig.verticalSpacing() + 1;
        }
        return simpleNodeHeight + subHeight + treeLayoutConfig.verticalSpacing();
    }

    /**
     * Recursively draws the sub-tree starting at the given node
     * 
     * @param canvas
     * @param key
     * @param parentRelation
     * @param widthOffset
     * @param heightOffset
     * @param absParentY vertical position of the parent (for drawing the connection between sibling and parent)
     */
    private void drawSubTree(TextCanvas canvas, NodeKey key, ParentRelation parentRelation, int widthOffset, int heightOffset, int absParentY) {

        NodeFormatInfo info = nodeFormatInfoCache.get(key);
        int absX = widthOffset + info.positionX();
        int absY = heightOffset + info.positionY();

        drawNodeRepresentation(canvas, absX, absY, info);

        BoxStyle boxStyle = info.boxStyle();
        if (!suppressConnectors && parentRelation.parentKey().isValid()) {
            int absSiblingX = widthOffset + info.positionX() - 1;
            int absSiblingY = heightOffset + info.positionY();
            if (boxStyle.hasSideLine(BoxSide.TOP) && boxStyle.hasSideLine(BoxSide.BOTTOM)) {
                absSiblingY = absSiblingY + ((info.simpleHeight() - 1) / 2);
            }
            drawParentConnector(canvas, key, parentRelation, absSiblingX, absSiblingY, absParentY);

        }
        info.node().decorateNode(parentRelation, canvas, absX, absY, info.simpleWidth(), info.simpleHeight());

        if (info.drawPlaceholderAppendix()) {
            drawPlaceholderAppendix(canvas, absX, absY, info.simpleWidth(), info.simpleHeight());
        }
        else if (info.hasSiblings()) {
            drawSiblings(canvas, key, info, absX, absY);
        }
    }

    /**
     * Draws the connecting line between a sibling and its parent
     * 
     * @param canvas
     * @param key
     * @param parentRelation
     * @param absSiblingX upper left corner of the sibling to be connected
     * @param absSiblingY upper left corner of the sibling to be connected
     * @param absParentY vertical position of the parent
     */
    private void drawParentConnector(TextCanvas canvas, NodeKey key, ParentRelation parentRelation, int absSiblingX, int absSiblingY, int absParentY) {

        NodeFormatInfo info = nodeFormatInfoCache.get(key);
        NodeFormatInfo parentInfo = nodeFormatInfoCache.get(key.parent());

        int absParentBottomX = absSiblingX - (2 * treeLayoutConfig.horizontalSpacing())
                + Math.min((parentInfo.simpleWidth() + 1) / 2, treeLayoutConfig.horizontalSpacing());
        int absParentBottomY = absParentY + parentInfo.simpleHeight();

        canvas.drawLine(absSiblingX, absSiblingY, absParentBottomX, absParentBottomY, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN,
                DEFAULT_LINE_CROSSING_RESOLVER);
        info.node().decorateParentConnector(parentRelation, canvas, new BoxConnectionPoint(BoxSide.LEFT, absSiblingX, absSiblingY),
                new BoxConnectionPoint(BoxSide.BOTTOM, absParentBottomX, absParentBottomY));
    }

    /**
     * Given a node that has siblings but the node is already at the maximum depth, the appendix visualizes the existence of further sibling.
     * 
     * @param canvas
     * @param absParentX
     * @param absParentY
     * @param parentWidth
     * @param parentHeight
     */
    private void drawPlaceholderAppendix(TextCanvas canvas, int absParentX, int absParentY, int parentWidth, int parentHeight) {

        int startX = absParentX + Math.min((parentWidth + 1) / 2, treeLayoutConfig.horizontalSpacing());
        int startY = absParentY + parentHeight;

        int endX = absParentX + Math.max(0, (2 * treeLayoutConfig.horizontalSpacing()) - 1);
        int endY = startY + treeLayoutConfig.verticalSpacing();

        if (!suppressConnectors) {
            canvas.drawLine(startX, startY, endX, endY, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_PLAIN,
                    DEFAULT_LINE_CROSSING_RESOLVER);
        }

        canvas.setCursor(endX + 1, endY);
        canvas.write("...");

    }

    /**
     * Draws the siblings of the given node, recursively, left below, top-down.
     * 
     * @param canvas
     * @param key
     * @param info
     * @param absX upper left corner of the node to draw the siblings for
     * @param absY upper left corner of the node to draw the siblings for
     */
    private void drawSiblings(TextCanvas canvas, NodeKey key, NodeFormatInfo info, int absX, int absY) {

        int widthOffset = absX + (2 * treeLayoutConfig.horizontalSpacing());
        int heightOffset = absY + info.simpleHeight() + treeLayoutConfig.verticalSpacing();

        int numberOfSiblings = info.siblingNodeKeys().length;
        for (int i = 0; i < numberOfSiblings; i++) {
            NodeKey siblingKey = key.sibling(i);
            NodeFormatInfo siblingInfo = nodeFormatInfoCache.get(siblingKey);
            PrintableTreeNode sibling = siblingInfo.node();
            if (sibling != PrintableTreeNode.MISSING_SIBLING) {
                drawSubTree(canvas, key.sibling(i), new DefaultParentRelation(key, numberOfSiblings, i), widthOffset, heightOffset, absY);
            }
        }

    }

}
