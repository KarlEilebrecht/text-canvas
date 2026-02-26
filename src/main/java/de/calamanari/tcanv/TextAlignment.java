//@formatter:off
/*
 * TextAlignment
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

import java.util.ArrayList;
import java.util.List;

/**
 * Text alignment options, e.g. for labels
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public enum TextAlignment {

    /**
     * Performs a left-alignment, e.g.:
     * 
     * <pre>
     * 'text', 7, 3: 'text   '
     *               '       '
     *               '       '
     * </pre>
     * 
     * If the input text is too long it will be truncated.
     */
    LEFT_TOP,

    /**
     * Performs a left-alignment, e.g.:
     * 
     * <pre>
     * 'text', 7, 3: '       '
     *               'text   '
     *               '       '
     * </pre>
     * 
     * If the input text is too long it will be truncated.
     */
    LEFT_CENTER,

    /**
     * Performs a left-alignment, e.g.:
     * 
     * <pre>
     * 'text', 7, 3: '       '
     *               '       '
     *               'text   '
     * </pre>
     * 
     * If the input text is too long it will be truncated.
     */
    LEFT_BOTTOM,

    /**
     * Performs a center-alignment, e.g.:
     * 
     * <pre>
     * 'text', 7, 3: '  text  '
     *               '        '
     *               '        '
     * </pre>
     * 
     * If the input text is too long it will be truncated.
     */
    CENTER_TOP,

    /**
     * Performs a center-alignment, e.g.:
     * 
     * <pre>
     * 'text', 8, 3: '        '
     *               '  text  '
     *               '        '
     * </pre>
     * 
     * If the input text is too long it will be truncated.
     */
    CENTER_CENTER,

    /**
     * Performs a center-alignment, e.g.:
     * 
     * <pre>
     * 'text', 8, 3: '        '
     *               '        '
     *               '  text  '
     * </pre>
     * 
     * If the input text is too long it will be truncated.
     */
    CENTER_BOTTOM,

    /**
     * Performs a right-alignment, e.g.:
     * 
     * <pre>
     * 'text', 8, 3: '    text'
     *               '        '
     *               '        '
     * </pre>
     * 
     * If the input text is too long it will be truncated.
     */
    RIGHT_TOP,

    /**
     * Performs a right-alignment, e.g.:
     * 
     * <pre>
     * 'text', 8, 3: '        '
     *               '    text'
     *               '        '
     * </pre>
     * 
     * If the input text is too long it will be truncated.
     */
    RIGHT_CENTER,

    /**
     * Performs a right-alignment, e.g.:
     * 
     * <pre>
     * 'text', 8, 3: '        '
     *               '        '
     *               '    text'
     * </pre>
     * 
     * If the input text is too long it will be truncated.
     */
    RIGHT_BOTTOM;

    /**
     * Aligns the given string (padded with space characters) or truncates it if it is longer than <code>width</code>
     * 
     * @param s to be aligned
     * @param width
     * @return given string potentially left and right-padded
     */
    private String alignLine(String s, int width) {
        switch (this) {
        case LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM:
            return leftAlign(s, width);
        case CENTER_TOP, CENTER_CENTER, CENTER_BOTTOM:
            return center(s, width);
        case RIGHT_TOP, RIGHT_CENTER, RIGHT_BOTTOM:
            return rightAlign(s, width);
        default:
            throw new IllegalStateException("not implemented");
        }
    }

    /**
     * Splits the given string into lines using common separators
     * 
     * @param s
     * @return lines split from the given string
     */
    private static String[] splitLines(String s) {
        return String.valueOf(s).split("\\r?\\n|\\r");
    }

    /**
     * Aligns the given string (padded with space characters horizontally and vertically) or truncates it if it is longer than <code>width</code> or results in
     * more lines than <code>height</code>.
     * 
     * @param s
     * @param width
     * @param height
     * @return list of aligned lines
     * @throws NullPointerException if s was null
     */
    public List<String> apply(String s, int width, int height) {
        List<String> formattedLines = splitLines(s, width, height);

        int verticalSpace = height - formattedLines.size();
        switch (this) {
        case LEFT_TOP, RIGHT_TOP, CENTER_TOP: {
            while (formattedLines.size() < height) {
                formattedLines.add(alignLine("", width));
            }
            break;
        }
        case LEFT_CENTER, RIGHT_CENTER, CENTER_CENTER: {
            if (verticalSpace > 1) {
                for (int i = 0; i < verticalSpace / 2; i++) {
                    formattedLines.add(0, alignLine("", width));
                }
            }
            while (formattedLines.size() < height) {
                formattedLines.add(alignLine("", width));
            }
            break;
        }
        case LEFT_BOTTOM, RIGHT_BOTTOM, CENTER_BOTTOM: {
            while (formattedLines.size() < height) {
                formattedLines.add(0, alignLine("", width));
            }
            break;
        }
        }
        return formattedLines;
    }

    /**
     * Creates a list of the effective lines of text to be written in an area of a defined width and height
     * 
     * @param s raw text (to be split)
     * @param width number of characters available per line
     * @param height number of lines available
     * @return list of text lines to be printed (trimmed)
     */
    private List<String> splitLines(String s, int width, int height) {
        List<String> formattedLines = new ArrayList<>();
        for (String raw : splitLines(s)) {
            String trimmed = raw.trim();
            boolean eol = false;
            while (!eol && formattedLines.size() < height) {
                String part = trimmed;
                if (part.length() > width) {
                    part = part.substring(0, width);
                    if (trimmed.length() > width) {
                        trimmed = trimmed.substring(width).trim();
                        eol = trimmed.isEmpty();
                    }
                    else {
                        eol = true;
                    }
                }
                else {
                    eol = true;
                }
                part = alignLine(part, width);
                formattedLines.add(part);
            }
            if (formattedLines.size() == height) {
                break;
            }
        }
        return formattedLines;
    }

    /**
     * This method returns the effective text width and height (number of text lines) ignoring the surrounding whitespace allowing the caller to adjust the
     * input for a best fit or to detect empty cases.
     * 
     * @param s
     * @param width
     * @param height
     * @return int[maxLineWidth, numberOfLines]
     * @throws NullPointerException if s was null
     */
    public static int[] computeTrimmedDimensions(String s, int width, int height) {
        List<String> formattedLines = CENTER_CENTER.apply(s, width, height);
        int maxLineWidth = 0;
        int numberOfLines = 0;
        for (String line : formattedLines) {
            int len = line.trim().length();
            if (len > 0) {
                numberOfLines++;
                maxLineWidth = Math.max(maxLineWidth, len);
            }
        }
        return new int[] { maxLineWidth, numberOfLines };

    }

    /**
     * This method returns the effective text width and height (number of text lines) ignoring the surrounding whitespace allowing the caller to adjust the
     * input for a best fit or to detect empty cases. As opposed to {@link #computeTrimmedDimensions(String, int, int)} here we don't make assumptions about the
     * available size and return the data purely based on the input text.
     * 
     * @param s
     * @return int[maxLineWidth, numberOfLines]
     * @throws NullPointerException if s was null
     */
    public static int[] computeTrimmedDimensions(String s) {
        String[] lines = splitLines(s);
        int maxLineWidth = 0;
        int numberOfLines = 0;
        for (String line : lines) {
            int len = line.trim().length();
            if (len > 0) {
                numberOfLines++;
                maxLineWidth = Math.max(maxLineWidth, len);
            }
        }
        return new int[] { maxLineWidth, numberOfLines };
    }

    /**
     * Centers the given string to the given width.
     * <p>
     * If the string is too long, it will be truncated to the given width.
     * 
     * @param s to be centered
     * @param width
     * @return given string potentially left and right-padded
     * @throws NullPointerException if s was null
     */
    public static final String center(String s, int width) {
        s = s.trim();
        int space = width - s.length();
        if (space > 1) {
            int spaceBefore = space / 2;
            int spaceAfter = space - spaceBefore;
            String format = "%" + spaceBefore + "s%s%" + spaceAfter + "s";
            return String.format(format, "", s, "");
        }
        else if (space < 0) {
            return s.substring(0, width);
        }
        else {
            return s;
        }
    }

    /**
     * Left-aligns the given string to the given width.
     * <p>
     * If the string is too long, it will be truncated to the given width.
     * 
     * @param s to be left-aligned
     * @param width
     * @return given string potentially right-padded.
     * @throws NullPointerException if s was null
     */
    public static final String leftAlign(String s, int width) {
        s = s.trim();
        int space = width - s.length();
        if (space > 1) {
            String format = "%s%" + space + "s";
            return String.format(format, s, "");
        }
        else if (space < 0) {
            return s.substring(0, width);
        }
        else {
            return s;
        }
    }

    /**
     * Right-aligns the given string to the given width.
     * <p>
     * If the string is too long, it will be truncated to the given width.
     * 
     * @param s to be right-aligned
     * @param width
     * @return given string potentially left-padded.
     * @throws NullPointerException if s was null
     */
    public static final String rightAlign(String s, int width) {
        s = s.trim();
        int space = width - s.length();
        if (space > 1) {
            String format = "%" + space + "s%s";
            return String.format(format, "", s);
        }
        else if (space < 0) {
            return s.substring(0, width);
        }
        else {
            return s;
        }
    }

}