//@formatter:off
/*
 * TextCanvas
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

package de.calamanari.tcanv;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * {@link TextCanvas} is a simple character area for printing textual diagrams. It comes with rudimentary support for drawing boxes and lines.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class TextCanvas {

    /**
     * Array holding the characters of the canvas, <code>height x width</code>
     */
    private final char[][] canvas;

    /**
     * Dimensions of this canvas
     */
    private final CanvasFormat format;

    /**
     * Strategy if the cursor is outside the canvas
     */
    private final CanvasBoundCheckStrategy cbcStrategy;

    /**
     * Current horizontal cursor position
     */
    private int cursorX = 0;

    /**
     * Current vertical cursor position
     */
    private int cursorY = 0;

    /**
     * Creates a new canvas with the given dimensions.
     * 
     * @param format dimensions of the canvas, not null
     * @param cbcStrategy, not null
     * @throws IllegalArgumentException if the dimensions are wrong or the given strategy was null
     */
    public TextCanvas(CanvasFormat format, CanvasBoundCheckStrategy cbcStrategy) {
        if (format == null || cbcStrategy == null) {
            throw new IllegalArgumentException(
                    String.format("expected: format != null, cbcStrategy != null, given: format=%s, cbcStrategy=%s", format, cbcStrategy));
        }
        this.format = format;
        this.canvas = new char[format.height()][format.width()];
        this.cbcStrategy = cbcStrategy;
        clear();
    }

    /**
     * Creates a new canvas of the given dimensions with {@link CanvasBoundCheckStrategy#ERROR}
     * 
     * @param format dimensions of the canvas, not null
     * @throws IllegalArgumentException if the dimensions are wrong or the given strategy was null
     */
    public TextCanvas(CanvasFormat format) {
        this(format, CanvasBoundCheckStrategy.ERROR);
    }

    /**
     * Creates a new canvas with the given dimensions.
     * 
     * @param width
     * @param height
     * @param cbcStrategy
     * @throws IllegalArgumentException if the dimensions are wrong or the given strategy was null
     */
    public TextCanvas(int width, int height, CanvasBoundCheckStrategy cbcStrategy) {
        this(new CanvasFormat(width, height), cbcStrategy);
    }

    /**
     * Creates a new canvas of the specified size with {@link CanvasBoundCheckStrategy#ERROR}
     * 
     * @param width
     * @param height
     */
    public TextCanvas(int width, int height) {
        this(width, height, CanvasBoundCheckStrategy.ERROR);
    }

    /**
     * Resets the canvas to its initial state (all blank)
     */
    public void clear() {
        for (int y = 0; y < format.height(); y++) {
            Arrays.fill(canvas[y], ' ');
        }
        setCursor(0, 0);
    }

    /**
     * Returns the height of this canvas.
     * 
     * @return height of the canvas (number of characters)
     */
    public int getHeight() {
        return format.height();
    }

    /**
     * Returns the width of this canvas.
     * <p>
     * Note: When calling {@link #export()} the effective width will be greater because of the added line breaks after each line.
     * 
     * @return width of the canvas (number of characters)
     */
    public int getWidth() {
        return format.width();
    }

    /**
     * @return dimensions of this canvas
     */
    public CanvasFormat getFormat() {
        return format;
    }

    /**
     * Positions the cursor on the canvas
     * <p>
     * <b>Note:</b> Setting the cursor position outside the canvas is not an error. Errors may be thrown first when you try to {@link #write(char)} outside the
     * canvas.
     * 
     * @param x
     * @param y
     */
    public void setCursor(int x, int y) {
        setCursorX(x);
        setCursorY(y);
    }

    /**
     * @return the current horizontal coordinate on the canvas
     */
    public int getCursorX() {
        return cursorX;
    }

    /**
     * Positions the cursor on the current line, not changing the vertical position.
     * <p>
     * <b>Note:</b> Setting the cursor position outside the canvas is not an error. Errors may be thrown first when you try to {@link #write(char)} outside the
     * canvas.
     * 
     * @param x the horizontal coordinate on the canvas
     */
    public void setCursorX(int x) {
        this.cursorX = x;
    }

    /**
     * @return the current vertical coordinate on the canvas
     */
    public int getCursorY() {
        return cursorY;
    }

    /**
     * Positions the cursor on the current line, not changing the vertical position.
     * <p>
     * <b>Note:</b> Setting the cursor position outside the canvas is not an error. Errors may be thrown first when you try to {@link #write(char)} outside the
     * canvas.
     * 
     * @param y the vertical coordinate on the canvas
     */
    public void setCursorY(int y) {
        this.cursorY = y;
    }

    /**
     * @return true if the current position of the cursor is within the bounds of the canvas (could write a character at this position)
     */
    public boolean isCursorPositionValid() {
        return (cursorX >= 0 && cursorX < format.width() && cursorY >= 0 && cursorY < format.height());
    }

    /**
     * Prints an opaque box (current cursor position is the upper left corner).
     * 
     * @param style
     * @param width
     * @param height
     */
    public void drawBox(BoxStyle style, int width, int height) {
        this.drawBox(style, width, height, false);
    }

    /**
     * Prints a box (current cursor position is the upper left corner).
     * 
     * @param style
     * @param width
     * @param height
     * @param transparent
     */
    public void drawBox(BoxStyle style, int width, int height, boolean transparent) {
        this.drawBox(style, width, height, "", TextAlignment.CENTER_CENTER, transparent);
    }

    /**
     * Prints an opaque box with an optional label in its center (current cursor position is the upper left corner), text , aligned
     * {@link TextAlignment#CENTER_CENTER}.
     * 
     * @param style
     * @param width
     * @param height
     * @param label
     */
    public void drawBox(BoxStyle style, int width, int height, String label) {
        drawBox(style, width, height, label, TextAlignment.CENTER_CENTER, false);
    }

    /**
     * Prints an opaque box with an optional label in its center (current cursor position is the upper left corner).
     * 
     * @param style
     * @param width
     * @param height
     * @param label
     * @param alignment option for the label text
     */
    public void drawBox(BoxStyle style, int width, int height, String label, TextAlignment alignment) {
        drawBox(style, width, height, label, alignment, false);
    }

    /**
     * Prints a box with an optional label in its center (current cursor position is the upper left corner), text aligned {@link TextAlignment#CENTER_CENTER}.
     * 
     * @param style
     * @param width
     * @param height
     * @param label
     * @param transparent if true, the space inside the box will not be cleaned.
     */
    public void drawBox(BoxStyle style, int width, int height, String label, boolean transparent) {
        drawBox(style, width, height, label, TextAlignment.CENTER_CENTER, transparent);

    }

    /**
     * Prints a box with an optional label in its center (current cursor position is the upper left corner).
     * 
     * @param style
     * @param width
     * @param height
     * @param label
     * @param alignment option for the label text
     * @param transparent if true, the space inside the box will not be cleaned.
     */
    public void drawBox(BoxStyle style, int width, int height, String label, TextAlignment alignment, boolean transparent) {

        int leftUpperCornerX = cursorX;
        int leftUpperCornerY = cursorY;

        if (!transparent) {
            fillSquare(width, height, ' ');
        }
        if (!style.suppressBorder()) {
            drawBoxBorder(style, width, height, leftUpperCornerX, leftUpperCornerY);
            if (label != null && !label.isBlank()) {
                cursorX = leftUpperCornerX + 1;
                cursorY = leftUpperCornerY + 1;
                drawLabel(width - 2, height - 2, label, alignment, transparent);
            }
        }
        else if (label != null && !label.isBlank()) {
            cursorX = leftUpperCornerX;
            cursorY = leftUpperCornerY;
            drawLabel(width, height, label, alignment, transparent);
        }

    }

    /**
     * Draws the relevant border lines of a box (depends on the style)
     * 
     * @param style
     * @param width
     * @param height
     * @param leftUpperCornerX
     * @param leftUpperCornerY
     */
    private void drawBoxBorder(BoxStyle style, int width, int height, int leftUpperCornerX, int leftUpperCornerY) {
        if (style.hasSideLine(BoxSide.TOP)) {
            cursorY = leftUpperCornerY;
            cursorX = leftUpperCornerX;
            write(style.cornerChar());
            cursorX = leftUpperCornerX + width - 1;
            cursorY = leftUpperCornerY;
            write(style.cornerChar());
        }
        if (style.hasSideLine(BoxSide.BOTTOM)) {
            cursorX = leftUpperCornerX;
            cursorY = leftUpperCornerY + height - 1;
            write(style.cornerChar());
            cursorX = leftUpperCornerX + width - 1;
            cursorY = leftUpperCornerY + height - 1;
            write(style.cornerChar());
        }

        for (int x = leftUpperCornerX + 1; x < (leftUpperCornerX + width) - 1; x++) {
            cursorX = x;
            cursorY = leftUpperCornerY;
            if (style.hasSideLine(BoxSide.TOP)) {
                write(style.horizontalLineChar());
            }
            cursorX = x;
            cursorY = leftUpperCornerY + height - 1;
            if (style.hasSideLine(BoxSide.BOTTOM)) {
                write(style.horizontalLineChar());
            }
        }
        for (int y = leftUpperCornerY + 1; y < (leftUpperCornerY + height) - 1; y++) {
            cursorX = leftUpperCornerX;
            cursorY = y;
            if (style.hasSideLine(BoxSide.LEFT)) {
                write(style.verticalLineChar());
            }
            cursorX = leftUpperCornerX + width - 1;
            cursorY = y;
            if (style.hasSideLine(BoxSide.RIGHT)) {
                write(style.verticalLineChar());
            }
        }
    }

    /**
     * Fills the specified rectangular area (current cursor position is the upper left corner) with the given character
     * 
     * @param width
     * @param height
     * @param ch to be repeated across the given area
     */
    public void fillSquare(int width, int height, char ch) {
        int leftUpperCornerX = cursorX;
        int leftUpperCornerY = cursorY;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width; i++) {
            sb.append(ch);
        }
        String fillLine = sb.toString();
        for (int y = leftUpperCornerY; y < leftUpperCornerY + height; y++) {
            cursorY = y;
            cursorX = leftUpperCornerX;
            write(fillLine);
        }
    }

    /**
     * Prints a label. A label is an opaque square with some text (current cursor position is the upper left corner).
     * <p>
     * <b>Note:</b> If height or width is &lt;=0 then this method ignored the request and behaves like a no-op.
     * 
     * @param width
     * @param height
     * @param label
     * @param alignment label text alignment option
     */
    public void drawLabel(int width, int height, String label, TextAlignment alignment) {
        drawLabel(width, height, label, alignment, false);
    }

    /**
     * Prints a label. A label is a square (optionally transparent) with some text (current cursor position is the upper left corner).
     * <p>
     * <b>Note:</b> If height or width is &lt;=0 then this method ignored the request and behaves like a no-op.
     * 
     * @param width
     * @param height
     * @param label
     * @param alignment label text alignment option
     * @param transparent if true, the space inside the label will not be cleaned.
     */
    private void drawLabel(int width, int height, String label, TextAlignment alignment, boolean transparent) {
        if (width <= 0 || height <= 0) {
            return;
        }
        List<String> formattedLines = alignment.apply(label, width, height);
        int leftUpperCornerX = cursorX;
        int leftUpperCornerY = cursorY;
        for (int i = 0; i < formattedLines.size(); i++) {
            cursorX = leftUpperCornerX;
            cursorY = leftUpperCornerY + i;
            write(formattedLines.get(i), transparent);
        }
    }

    /**
     * Draws a line connecting the given points printing the specified connector ends.
     * <p>
     * The actual form of the line will be derived from the positions and the connector ends.
     * 
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @param connectorFrom
     * @param connectorTo
     */
    public void drawLine(int fromX, int fromY, int toX, int toY, ConnectorEndType connectorFrom, ConnectorEndType connectorTo) {
        drawLine(fromX, fromY, toX, toY, connectorFrom, connectorTo, CharacterConflictResolver.OVERWRITE);
    }

    /**
     * Draws a line connecting the given points printing the specified connector ends.
     * <p>
     * The actual form of the line will be derived from the positions and the connector ends. A conflict resolver determines what to print if there are existing
     * characters on the path of the connector line.
     * 
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @param connectorFrom
     * @param connectorTo
     * @param conflictResolver resolution function to handle an existing character, takes the existing character and the proposed character as input, returns
     *            the character decided to be written (not affecting explicit connector endpoints)
     */
    public void drawLine(int fromX, int fromY, int toX, int toY, ConnectorEndType connectorFrom, ConnectorEndType connectorTo,
            BinaryOperator<Character> conflictResolver) {
        drawConnector(new ConnectorDescriptor(connectorFrom, fromX, fromY, connectorTo, toX, toY, conflictResolver));
    }

    /**
     * Draws the connector according to the given descriptor
     * 
     * @param descriptor
     */
    private void drawConnector(ConnectorDescriptor descriptor) {
        drawConnectorEnd(descriptor.fromX, descriptor.fromY, descriptor.fromEndType, descriptor.conflictResolver);
        drawConnectorEnd(descriptor.toX, descriptor.toY, descriptor.toEndType, descriptor.conflictResolver);
        switch (descriptor.shape) {
        case H_LINE:
            drawConnectorH(descriptor);
            break;
        case HV_LINE:
            drawConnectorHV(descriptor);
            break;
        case HVH_LINE:
            drawConnectorHVH(descriptor);
            break;
        case HVHC_LINE:
            drawConnectorHVHC(descriptor, false);
            break;
        case HVHCT_LINE:
            drawConnectorHVHC(descriptor, true);
            break;
        case V_LINE:
            drawConnectorV(descriptor);
            break;
        case VH_LINE:
            drawConnectorVH(descriptor);
            break;
        case VHV_LINE:
            drawConnectorVHV(descriptor);
            break;
        case VHVU_LINE:
            drawConnectorVHVU(descriptor, false);
            break;
        case VHVUT_LINE:
            drawConnectorVHVU(descriptor, true);
            break;
        }
    }

    /**
     * Draws a connection with shape {@link ConnectorShape#H_LINE}
     * 
     * @param descriptor
     */
    private void drawConnectorH(ConnectorDescriptor descriptor) {
        if (!descriptor.suppressHorizontalLine) {
            int y = descriptor.lineFromY;
            if (descriptor.lineFromY != descriptor.lineToY && descriptor.lineFromX > descriptor.lineToX) {
                y = descriptor.lineToY;
            }
            drawHorizontalLine(descriptor.lineFromX, descriptor.lineToX, y, descriptor.conflictResolver);
        }
    }

    /**
     * Draws a connection with shape {@link ConnectorShape#HV_LINE}
     * 
     * @param descriptor
     */
    private void drawConnectorHV(ConnectorDescriptor descriptor) {
        if (!descriptor.suppressHorizontalLine) {
            drawHorizontalLine(descriptor.lineFromX, descriptor.lineToX, descriptor.lineFromY, descriptor.conflictResolver);
        }
        if (!descriptor.suppressVerticalLine) {
            drawVerticalLine(descriptor.lineToX, descriptor.lineFromY, descriptor.lineToY, descriptor.conflictResolver);
        }
        if (!descriptor.suppressHorizontalLine || !descriptor.suppressVerticalLine) {
            cursorX = descriptor.lineToX;
            cursorY = descriptor.lineFromY;
            write('+', descriptor.conflictResolver);
        }
    }

    /**
     * Draws a connection with shape {@link ConnectorShape#HVH_LINE}
     * 
     * @param descriptor
     */
    private void drawConnectorHVH(ConnectorDescriptor descriptor) {
        int midX = descriptor.lineFromX + ((int) Math.ceil((descriptor.lineToX - descriptor.lineFromX) / 2.0));
        if (descriptor.lineFromX > descriptor.lineToX) {
            midX = descriptor.lineFromX - ((int) Math.ceil((descriptor.lineFromX - descriptor.lineToX) / 2.0));
        }
        if (!descriptor.suppressHorizontalLine) {
            drawHorizontalLine(descriptor.lineFromX, midX, descriptor.lineFromY, descriptor.conflictResolver);
            if (midX != descriptor.lineToX) {
                drawHorizontalLine(midX, descriptor.lineToX, descriptor.lineToY, descriptor.conflictResolver);
            }
        }
        if (!descriptor.suppressVerticalLine) {
            drawVerticalLine(midX, descriptor.lineFromY, descriptor.lineToY, descriptor.conflictResolver);
        }
        if (!descriptor.suppressHorizontalLine || !descriptor.suppressVerticalLine) {
            cursorX = midX;
            cursorY = descriptor.lineFromY;
            write('+', descriptor.conflictResolver);
            cursorX = midX;
            cursorY = descriptor.lineToY;
            write('+', descriptor.conflictResolver);
        }
    }

    /**
     * Draws a connection with shape {@link ConnectorShape#HVHC_LINE} or {@link ConnectorShape#HVHCT_LINE}
     * 
     * @param descriptor
     * @param turned
     */
    private void drawConnectorHVHC(ConnectorDescriptor descriptor, boolean turned) {
        int extX = Math.min(descriptor.lineFromX, descriptor.lineToX) - 2;
        if (turned) {
            extX = Math.max(descriptor.lineFromX, descriptor.lineToX) + 2;
        }
        drawHorizontalLine(descriptor.lineFromX, extX, descriptor.lineFromY, descriptor.conflictResolver);
        drawHorizontalLine(descriptor.lineToX, extX, descriptor.lineToY, descriptor.conflictResolver);
        if (!descriptor.suppressVerticalLine) {
            drawVerticalLine(extX, descriptor.lineFromY, descriptor.lineToY, descriptor.conflictResolver);
        }
        cursorX = extX;
        cursorY = descriptor.lineFromY;
        write('+', descriptor.conflictResolver);
        cursorX = extX;
        cursorY = descriptor.lineToY;
        write('+', descriptor.conflictResolver);
    }

    /**
     * Draws a connection with shape {@link ConnectorShape#V_LINE}
     * 
     * @param descriptor
     */
    private void drawConnectorV(ConnectorDescriptor descriptor) {
        if (!descriptor.suppressVerticalLine) {
            int x = descriptor.lineFromX;
            if (descriptor.lineFromX != descriptor.lineToX && descriptor.lineFromY > descriptor.lineToY) {
                x = descriptor.lineToX;
            }
            drawVerticalLine(x, descriptor.lineFromY, descriptor.lineToY, descriptor.conflictResolver);
        }
    }

    /**
     * Draws a connection with shape {@link ConnectorShape#VH_LINE}
     * 
     * @param descriptor
     */
    private void drawConnectorVH(ConnectorDescriptor descriptor) {
        if (!descriptor.suppressVerticalLine) {
            drawVerticalLine(descriptor.lineFromX, descriptor.lineFromY, descriptor.lineToY, descriptor.conflictResolver);
        }
        if (!descriptor.suppressHorizontalLine) {
            drawHorizontalLine(descriptor.lineFromX, descriptor.lineToX, descriptor.lineToY, descriptor.conflictResolver);
        }
        if (!descriptor.suppressHorizontalLine || !descriptor.suppressVerticalLine) {
            cursorX = descriptor.lineFromX;
            cursorY = descriptor.lineToY;
            write('+', descriptor.conflictResolver);
        }
    }

    /**
     * Draws a connection with shape {@link ConnectorShape#VHV_LINE}
     * 
     * @param descriptor
     */
    private void drawConnectorVHV(ConnectorDescriptor descriptor) {
        int midY = descriptor.lineFromY + ((int) Math.ceil((descriptor.lineToY - descriptor.lineFromY) / 2.0));
        if (descriptor.lineFromY > descriptor.lineToY) {
            midY = descriptor.lineFromY - ((int) Math.ceil((descriptor.lineFromY - descriptor.lineToY) / 2.0));
        }
        if (!descriptor.suppressVerticalLine) {
            drawVerticalLine(descriptor.lineFromX, descriptor.lineFromY, midY, descriptor.conflictResolver);
            if (midY != descriptor.lineToY) {
                drawVerticalLine(descriptor.lineToX, midY, descriptor.lineToY, descriptor.conflictResolver);
            }
        }
        if (!descriptor.suppressHorizontalLine) {
            drawHorizontalLine(descriptor.lineFromX, descriptor.lineToX, midY, descriptor.conflictResolver);
        }
        if (!descriptor.suppressHorizontalLine || !descriptor.suppressVerticalLine) {
            cursorX = descriptor.lineFromX;
            cursorY = midY;
            write('+', descriptor.conflictResolver);
            cursorX = descriptor.lineToX;
            cursorY = midY;
            write('+', descriptor.conflictResolver);
        }
    }

    /**
     * Draws a connection with shape {@link ConnectorShape#VHVU_LINE} or {@link ConnectorShape#VHVUT_LINE}
     * 
     * @param descriptor
     * @param turned
     */
    private void drawConnectorVHVU(ConnectorDescriptor descriptor, boolean turned) {
        int extY = Math.max(descriptor.lineFromY, descriptor.lineToY) + 1;
        if (turned) {
            extY = Math.min(descriptor.lineFromY, descriptor.lineToY) - 1;
        }
        drawVerticalLine(descriptor.lineFromX, descriptor.lineFromY, extY, descriptor.conflictResolver);
        drawVerticalLine(descriptor.lineToX, descriptor.lineToY, extY, descriptor.conflictResolver);
        if (!descriptor.suppressHorizontalLine) {
            drawHorizontalLine(descriptor.lineFromX, descriptor.lineToX, extY, descriptor.conflictResolver);
        }
        cursorX = descriptor.lineFromX;
        cursorY = extY;
        write('+', descriptor.conflictResolver);
        cursorX = descriptor.lineToX;
        cursorY = extY;
        write('+', descriptor.conflictResolver);
    }

    /**
     * Prints a vertical line by repeating the character '-'.
     * 
     * @param fromX (incl.)
     * @param toX (incl.)
     * @param y
     * @param conflictResolver resolution function to handle an existing character, takes the existing character and the proposed character as input, returns
     *            the character decided to be written
     */
    private void drawHorizontalLine(int fromX, int toX, int y, BinaryOperator<Character> conflictResolver) {
        if (toX < fromX) {
            int temp = fromX;
            fromX = toX;
            toX = temp;
        }
        cursorY = y;
        cursorX = fromX;
        drawHorizontalLine('-', (toX - fromX) + 1, conflictResolver);
    }

    /**
     * Prints a horizontal line by repeating the given character length times starting from the current cursor position.
     * 
     * @param lineChar
     * @param length
     * @param conflictResolver resolution function to handle an existing character, takes the existing character and the proposed character as input, returns
     *            the character decided to be written
     */
    private void drawHorizontalLine(char lineChar, int length, BinaryOperator<Character> conflictResolver) {
        drawLine(lineChar, length, conflictResolver, false);
    }

    /**
     * Prints a vertical line by repeating the character '|'.
     * 
     * @param x
     * @param fromY (incl.)
     * @param toY (incl.)
     * @param conflictResolver resolution function to handle an existing character, takes the existing character and the proposed character as input, returns
     *            the character decided to be written
     */
    private void drawVerticalLine(int x, int fromY, int toY, BinaryOperator<Character> conflictResolver) {
        if (toY < fromY) {
            int temp = fromY;
            fromY = toY;
            toY = temp;
        }
        cursorY = fromY;
        cursorX = x;
        drawVerticalLine('|', (toY - fromY) + 1, conflictResolver);
    }

    /**
     * Prints a vertical line by repeating the given character length times starting from the current cursor position.
     * 
     * @param lineChar
     * @param length
     * @param conflictResolver resolution function to handle an existing character, takes the existing character and the proposed character as input, returns
     *            the character decided to be written
     */
    private void drawVerticalLine(char lineChar, int length, BinaryOperator<Character> conflictResolver) {
        drawLine(lineChar, length, conflictResolver, true);
    }

    /**
     * Prints a vertical line by repeating the given character length times starting from the current cursor position.
     * 
     * @param lineChar
     * @param length
     * @param conflictResolver resolution function to handle an existing character, takes the existing character and the proposed character as input, returns
     *            the character decided to be written
     * @param vertical true to draw a vertical line the character decided to be written
     */
    private void drawLine(char lineChar, int length, BinaryOperator<Character> conflictResolver, boolean vertical) {
        if (length > 0) {
            int startX = cursorX;
            int startY = cursorY;
            for (int i = 0; i < length; i++) {
                cursorX = vertical ? startX : startX + i;
                cursorY = vertical ? startY + i : startY;
                char currentChar = lineChar;
                int existingChar = read(false);
                if (existingChar > -1) {
                    currentChar = conflictResolver.apply((char) existingChar, lineChar);
                }
                write(currentChar);
            }
        }
    }

    /**
     * Writes the connector symbol at the given coordinates
     * 
     * @param x
     * @param y
     * @param type to obtain the symbol from
     */
    private void drawConnectorEnd(int x, int y, ConnectorEndType type, BinaryOperator<Character> conflictResolver) {
        cursorX = x;
        cursorY = y;
        char currentChar = type.symbol();
        int existingChar = read(false);
        if (existingChar > -1) {
            currentChar = conflictResolver.apply((char) existingChar, currentChar);
        }
        write(currentChar);
    }

    /**
     * Writes a String at the current cursor position
     * 
     * @param s to be written
     */
    public void write(String s) {
        write(s, false);
    }

    /**
     * @param s
     * @param transparent if true, any leading or training whitespace will not be written
     */
    private void write(String s, boolean transparent) {
        assertCanWrite(s);
        int startIdx = computeStartIdx(s, transparent);
        int endIdx = computeEndIdx(s, transparent);
        cursorX = cursorX + startIdx;
        for (int i = startIdx; i < endIdx; i++) {
            char ch = s.charAt(i);
            if (!writeInternal(ch)) {
                return;
            }
        }
    }

    /**
     * @param s
     * @param transparent
     * @return effective start (first character) of the trimmed version of the text
     */
    private int computeStartIdx(String s, boolean transparent) {
        int startIdx = 0;
        if (transparent) {
            for (int i = 0; i < s.length(); i++) {
                if (Character.isWhitespace(s.charAt(i))) {
                    startIdx++;
                }
                else {
                    break;
                }
            }
        }
        return startIdx;
    }

    /**
     * @param s
     * @param transparent
     * @return effective end (last character) of the trimmed version of the text
     */
    private int computeEndIdx(String s, boolean transparent) {
        int endIdx = s.length();
        if (transparent) {
            for (int i = s.length() - 1; i > -1; i--) {
                if (Character.isWhitespace(s.charAt(i))) {
                    endIdx--;
                }
                else {
                    break;
                }
            }
        }
        return endIdx;
    }

    /**
     * Writes a single character at the current cursor position and moves the cursor
     * 
     * @param ch
     */
    public void write(char ch) {
        assertCanWrite(ch);
        writeInternal(ch);
    }

    /**
     * Writes a single character at the current cursor position and moves the cursor
     * 
     * @param ch
     * @param conflictResolver resolution function to handle an existing character, takes the existing character and the proposed character as input, returns
     *            the character decided to be written
     */
    private void write(char ch, BinaryOperator<Character> conflictResolver) {
        int existingChar = read(false);
        if (existingChar > -1) {
            ch = conflictResolver.apply((char) existingChar, ch);
        }
        write(ch);
    }

    /**
     * Returns the character at the current cursor position and optionally moves the cursor by one
     * 
     * @param moveCursor if true, increases the horizontal cursor position by one
     * @return character at the current position or <code>-1</code> if the cursor position is outside the canvas
     */
    public int read(boolean moveCursor) {
        int res = -1;
        if (isCursorPositionValid()) {
            res = canvas[cursorY][cursorX];
            if (moveCursor) {
                cursorX++;
            }
        }
        return res;
    }

    /**
     * writes the character if the cursor is inside the canvas boundaries and moves the cursor to the right.
     * 
     * @param ch to be written
     * @return true if the character has been written
     */
    private boolean writeInternal(char ch) {
        if (isCursorPositionValid()) {
            canvas[cursorY][cursorX] = ch;
            cursorX++;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Depending on the {@link CanvasBoundCheckStrategy} this method throws an exception if the cursor is outside the canvas
     * 
     * @param s candidate to be printed
     */
    private void assertCanWrite(String s) {
        if (cbcStrategy != CanvasBoundCheckStrategy.IGNORE) {
            int required = (s == null ? 4 : s.length());
            if (cursorX < 0 || cursorX + required > format.width() || cursorY < 0 || cursorY >= format.height()) {
                throw new IndexOutOfBoundsException(
                        String.format("Cannot write outside canvas bounds (width=%d, height=%d) limit: cursor as (%d, %d), text='%s'(%d)", format.width(),
                                format.height(), cursorX, cursorY, s, required));
            }
        }
    }

    /**
     * Depending on the {@link CanvasBoundCheckStrategy} this method throws an exception if the cursor is outside the canvas
     * 
     * @param ch candidate to be printed
     */
    private void assertCanWrite(char ch) {
        if (cbcStrategy != CanvasBoundCheckStrategy.IGNORE && !isCursorPositionValid()) {
            throw new IndexOutOfBoundsException(String.format("Cannot write outside canvas bounds (width=%d, height=%d) limit: cursor as (%d, %d), char='%s'",
                    format.width(), format.height(), cursorX, cursorY, ch));

        }
    }

    /**
     * Turns the canvas into a continuous string, for printing.
     * 
     * @return string representation of this canvas for output
     */
    public String export() {
        StringBuilder sb = new StringBuilder((format.height() * format.width()) + (canvas.length * 2));
        for (char[] line : canvas) {
            if (!sb.isEmpty()) {
                sb.append("\n");
            }
            sb.append(new String(line));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [width=" + format.width() + ", height=" + format.height() + ", cbcStrategy=" + cbcStrategy + "]";
    }
}
