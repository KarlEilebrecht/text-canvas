//@formatter:off
/*
 * ConnectorDescriptor
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

import java.util.function.BinaryOperator;

/**
 * A {@link ConnectorDescriptor} contains the meta data to connect two points, assuming a from-to relationship (e.g., to connect two boxes).
 * <p>
 * The constructor takes the coordinates and auto-selects the best fitting {@link ConnectorShape}. Hence, this class centralizes the logic to compute the path
 * between two points.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class ConnectorDescriptor {

    /**
     * coordinate provided to constructor
     */
    int fromX;

    /**
     * coordinate provided to constructor
     */
    int fromY;

    /**
     * coordinate provided to constructor
     */
    int toX;

    /**
     * coordinate provided to constructor
     */
    int toY;

    /**
     * horizontal line segments to draw
     */
    int lineFromX;

    /**
     * vertical line segments to draw
     */
    int lineFromY;

    /**
     * horizontal line segments to draw
     */
    int lineToX;

    /**
     * vertical line segments to draw
     */
    int lineToY;

    /**
     * if true, there must not be any horizontal line segment (collapsed/suppressed)
     */
    boolean suppressHorizontalLine = false;

    /**
     * if true, there must not be any vertical line segment (collapsed/suppressed)
     */
    boolean suppressVerticalLine = false;

    /**
     * symbol to be displayed at the start of the line
     */
    ConnectorEndType fromEndType;

    /**
     * symbol to be displayed at the end of the line
     */
    ConnectorEndType toEndType;

    /**
     * auto-detected
     */
    ConnectorShape shape;

    /**
     * Tells how to deal with existing characters
     */
    BinaryOperator<Character> conflictResolver;

    /**
     * @param fromEndType describes the start of the line to be drawn
     * @param fromX
     * @param fromY
     * @param toEndType describes the end of the line to be drawn
     * @param toX
     * @param toY
     * @param conflictResolver resolution function to handle an existing character, takes the existing character and the proposed character as input, returns
     *            the character decided to be written (not affecting explicit connector endpoints)
     */
    public ConnectorDescriptor(ConnectorEndType fromEndType, int fromX, int fromY, ConnectorEndType toEndType, int toX, int toY,
            BinaryOperator<Character> conflictResolver) {
        this.fromEndType = fromEndType;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toEndType = toEndType;
        this.toX = toX;
        this.toY = toY;
        this.lineFromX = fromX;
        this.lineFromY = fromY;
        this.lineToX = toX;
        this.lineToY = toY;
        this.shape = determineConnectorShape(fromX, fromY, toX, toY, fromEndType, toEndType);
        this.initLineDimensions();
        this.conflictResolver = conflictResolver;
    }

    /**
     * Based on the shape, the coordinates and the end point information determines the length of the line segments.
     */
    private void initLineDimensions() {
        switch (shape) {
        case H_LINE, HVH_LINE: {
            initLineDimensionsConnectHorizontalToHorizontal();
            break;
        }
        case HV_LINE: {
            initLineDimensionsConnectHorizontalToVertical();
            break;
        }
        case HVHC_LINE: {
            initLineDimensionsConnectCShape();
            break;
        }
        case HVHCT_LINE: {
            initLineDimensionsConnectTurnedCShape();
            break;
        }
        case V_LINE, VHV_LINE: {
            initLineDimensionsConnectVerticalToVertical();
            break;
        }
        case VH_LINE: {
            initLineDimensionsVerticalToHorizontal();
            break;
        }
        case VHVU_LINE: {
            initLineDimensionsConnectUShape();
            break;
        }
        case VHVUT_LINE: {
            initLineDimensionsConnectTurnedUShape();
            break;
        }
        }
    }

    /**
     * Configures the settings for the shapes {@link ConnectorShape#H_LINE} and {@link ConnectorShape#HVH_LINE}.
     */
    private void initLineDimensionsConnectHorizontalToHorizontal() {
        initConnectorLengthHorizontalToHorizontal();
        if (Math.abs(fromY - toY) < 2) {
            suppressVerticalLine = true;
        }
        else if (Math.abs(fromX - toX) == 1 && (shape == ConnectorShape.H_LINE || (fromEndType.hasSpecialEndSymbol() && toEndType.hasSpecialEndSymbol()))) {
            decLineToY();
        }

    }

    /**
     * Length computation (considers connector ends)
     */
    private void initConnectorLengthHorizontalToHorizontal() {
        int endPointSymbolSize = (fromEndType.hasSpecialEndSymbol() ? 1 : 0) + (toEndType.hasSpecialEndSymbol() ? 1 : 0);
        int len = Math.abs(fromX - toX) - endPointSymbolSize;
        if (len < 0) {
            if (fromX != toX || fromY != toY) {
                suppressHorizontalLine = true;
            }
        }
        else {
            if (fromEndType.hasSpecialEndSymbol()) {
                incLineFromX();
            }
            if (toEndType.hasSpecialEndSymbol()) {
                decLineToX();
            }
        }
    }

    /**
     * Configures the settings for the shape {@link ConnectorShape#HV_LINE}
     */
    private void initLineDimensionsConnectHorizontalToVertical() {
        initConnectorLengthHorizontalToVerticalStartSection();
        initConnectorLengthHorizontalToVerticalEndSection();
    }

    /**
     * Horizontal length computation (considers connector ends)
     */
    private void initConnectorLengthHorizontalToVerticalStartSection() {
        int endPointSymbolSize = fromEndType.hasSpecialEndSymbol() ? 1 : 0;
        if (fromY == toY && toEndType.hasSpecialEndSymbol()) {
            endPointSymbolSize++;
        }
        int len = Math.abs(fromX - toX) - endPointSymbolSize;
        if (len < 0) {
            suppressHorizontalLine = true;
        }
        else {
            if (fromEndType.hasSpecialEndSymbol()) {
                incLineFromX();
            }
            if (fromY == toY && toEndType.hasSpecialEndSymbol()) {
                decLineToX();
            }
        }
    }

    /**
     * Vertical length computation (considers connector ends)
     */
    private void initConnectorLengthHorizontalToVerticalEndSection() {
        int endPointSymbolSize = toEndType.hasSpecialEndSymbol() ? 1 : 0;
        if (fromX == toX && fromEndType.hasSpecialEndSymbol()) {
            endPointSymbolSize++;
        }
        int len = Math.abs(fromY - toY) - endPointSymbolSize;
        if (len < 0) {
            suppressVerticalLine = true;
        }
        else {
            if (toEndType.hasSpecialEndSymbol()) {
                decLineToY();
            }
            if (fromX == toX && toEndType.hasSpecialEndSymbol()) {
                incLineFromY();
            }
        }
    }

    /**
     * Configures the settings for the shape {@link ConnectorShape#HVHC_LINE}
     */
    private void initLineDimensionsConnectCShape() {
        if (fromEndType.hasSpecialEndSymbol()) {
            lineFromX--;
        }
        if (toEndType.hasSpecialEndSymbol()) {
            lineToX--;
        }
        if (Math.abs(fromY - toY) < 2) {
            suppressVerticalLine = true;
        }
    }

    /**
     * Configures the settings for the shape {@link ConnectorShape#HVHCT_LINE}
     */
    private void initLineDimensionsConnectTurnedCShape() {
        if (fromEndType.hasSpecialEndSymbol()) {
            lineFromX++;
        }
        if (toEndType.hasSpecialEndSymbol()) {
            lineToX++;
        }
        if (Math.abs(fromY - toY) < 2) {
            suppressVerticalLine = true;
        }
    }

    /**
     * Configures the settings for the shapes {@link ConnectorShape#V_LINE} and {@link ConnectorShape#VHV_LINE}.
     */
    private void initLineDimensionsConnectVerticalToVertical() {
        initConnectorLengthVerticalToVertical();
        if (Math.abs(fromX - toX) < 2) {
            suppressHorizontalLine = true;
        }
        else if (Math.abs(fromY - toY) == 1 && (shape == ConnectorShape.V_LINE || (fromEndType.hasSpecialEndSymbol() && toEndType.hasSpecialEndSymbol()))) {
            decLineToX();
        }
    }

    /**
     * Length computation (considers connector ends)
     */
    private void initConnectorLengthVerticalToVertical() {
        int endPointSymbolSize = (fromEndType.hasSpecialEndSymbol() ? 1 : 0) + (toEndType.hasSpecialEndSymbol() ? 1 : 0);
        int len = Math.abs(fromY - toY) - endPointSymbolSize;
        if (len < 0) {
            if (fromX != toX || fromY != toY) {
                suppressVerticalLine = true;
            }
        }
        else {
            if (fromEndType.hasSpecialEndSymbol()) {
                incLineFromY();
            }
            if (toEndType.hasSpecialEndSymbol()) {
                decLineToY();
            }
        }
    }

    /**
     * Configures the settings for the shape {@link ConnectorShape#VH_LINE}
     */
    private void initLineDimensionsVerticalToHorizontal() {
        initConnectorVerticalToHorizontalStartSection();
        initConnectorVerticalToHorizontalEndSection();
    }

    /**
     * Vertical length computation (considers connector ends)
     */
    private void initConnectorVerticalToHorizontalStartSection() {
        int endPointSymbolSize = fromEndType.hasSpecialEndSymbol() ? 1 : 0;
        if (fromX == toX && toEndType.hasSpecialEndSymbol()) {
            endPointSymbolSize++;
        }
        int len = Math.abs(fromY - toY) - endPointSymbolSize;
        if (len < 0) {
            suppressVerticalLine = true;
        }
        else {
            if (fromEndType.hasSpecialEndSymbol()) {
                incLineFromY();
            }
            if (fromX == toX && toEndType.hasSpecialEndSymbol()) {
                decLineToY();
            }
        }
    }

    /**
     * Horizontal length computation (considers connector ends)
     */
    private void initConnectorVerticalToHorizontalEndSection() {
        int endPointSymbolSize = toEndType.hasSpecialEndSymbol() ? 1 : 0;
        if (fromY == toY && fromEndType.hasSpecialEndSymbol()) {
            endPointSymbolSize++;
        }
        int len = Math.abs(fromX - toX) - endPointSymbolSize;
        if (len < 0) {
            suppressHorizontalLine = true;
        }
        else {
            if (toEndType.hasSpecialEndSymbol()) {
                decLineToX();
            }
            if (fromY == toY && fromEndType.hasSpecialEndSymbol()) {
                incLineFromX();
            }
        }
    }

    /**
     * Configures the settings for the shape {@link ConnectorShape#VHVU_LINE}
     */
    private void initLineDimensionsConnectUShape() {
        if (fromEndType.hasSpecialEndSymbol()) {
            lineFromY++;
        }
        if (toEndType.hasSpecialEndSymbol()) {
            lineToY++;
        }
        if (Math.abs(fromX - toX) < 2) {
            suppressHorizontalLine = true;
        }
    }

    /**
     * Configures the settings for the shape {@link ConnectorShape#VHVUT_LINE}
     */
    private void initLineDimensionsConnectTurnedUShape() {
        if (fromEndType.hasSpecialEndSymbol()) {
            lineFromY--;
        }
        if (toEndType.hasSpecialEndSymbol()) {
            lineToY--;
        }
        if (Math.abs(fromX - toX) < 2) {
            suppressHorizontalLine = true;
        }
    }

    /**
     * shortens a horizontal segment by (relatively) increasing the x-coordinate to start drawing
     */
    private void incLineFromX() {
        if (fromX < toX) {
            lineFromX++;
        }
        else if (fromX > toX) {
            lineFromX--;
        }
    }

    /**
     * shortens a vertical segment by (relatively) increasing the y-coordinate to start drawing
     */
    private void incLineFromY() {
        if (fromY < toY) {
            lineFromY++;
        }
        else if (fromY > toY) {
            lineFromY--;
        }
    }

    /**
     * shortens a horizontal segment by (relatively) decreasing the x-coordinate to end drawing
     */
    private void decLineToX() {
        if (fromX < toX) {
            lineToX--;
        }
        else if (fromX > toX) {
            lineToX++;
        }
    }

    /**
     * shortens a vertical segment by (relatively) decreasing the y-coordinate to end drawing
     */
    private void decLineToY() {
        if (fromY < toY) {
            lineToY--;
        }
        else if (fromY > toY) {
            lineToY++;
        }
    }

    /**
     * Determines the connector shape based on the connector forms and the coordinates
     * 
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @param connectorFrom
     * @param connectorTo
     * @return auto-detected shape
     */
    private static ConnectorShape determineConnectorShape(int fromX, int fromY, int toX, int toY, ConnectorEndType connectorFrom,
            ConnectorEndType connectorTo) {

        ConnectorShape res = determineConnectorShape(connectorFrom, connectorTo);
        switch (res) {
        case HVH_LINE: {
            if (fromY == toY) {
                res = ConnectorShape.H_LINE;
            }
            break;
        }
        case VHV_LINE: {
            if (fromX == toX) {
                res = ConnectorShape.V_LINE;
            }
            break;
        }
        // $CASES-OMITTED$
        default:
        }
        return res;
    }

    /**
     * @param connectorFrom
     * @param connectorTo
     * @return shape
     */
    private static ConnectorShape determineConnectorShape(ConnectorEndType connectorFrom, ConnectorEndType connectorTo) {

        switch (connectorFrom.side()) {
        case LEFT: {
            switch (connectorTo.side()) {
            case LEFT:
                return ConnectorShape.HVHC_LINE;
            case RIGHT:
                return ConnectorShape.HVH_LINE;
            case TOP, BOTTOM:
                return ConnectorShape.HV_LINE;
            }
            break;
        }
        case RIGHT: {
            switch (connectorTo.side()) {
            case LEFT:
                return ConnectorShape.HVH_LINE;
            case RIGHT:
                return ConnectorShape.HVHCT_LINE;
            case TOP, BOTTOM:
                return ConnectorShape.HV_LINE;
            }
            break;
        }
        case TOP: {
            switch (connectorTo.side()) {
            case TOP:
                return ConnectorShape.VHVUT_LINE;
            case BOTTOM:
                return ConnectorShape.VHV_LINE;
            case LEFT, RIGHT:
                return ConnectorShape.VH_LINE;
            }
            break;
        }
        case BOTTOM: {
            switch (connectorTo.side()) {
            case TOP:
                return ConnectorShape.VHV_LINE;
            case BOTTOM:
                return ConnectorShape.VHVU_LINE;
            case LEFT, RIGHT:
                return ConnectorShape.VH_LINE;
            }
            break;
        }
        }
        throw new IllegalStateException("not implemented");
    }

}