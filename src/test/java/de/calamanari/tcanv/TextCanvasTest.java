//@formatter:off
/*
 * TextCanvasTest
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

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.calamanari.tcanv.TextAlignment.CENTER_BOTTOM;
import static de.calamanari.tcanv.TextAlignment.CENTER_CENTER;
import static de.calamanari.tcanv.TextAlignment.CENTER_TOP;
import static de.calamanari.tcanv.TextAlignment.LEFT_BOTTOM;
import static de.calamanari.tcanv.TextAlignment.LEFT_CENTER;
import static de.calamanari.tcanv.TextAlignment.LEFT_TOP;
import static de.calamanari.tcanv.TextAlignment.RIGHT_BOTTOM;
import static de.calamanari.tcanv.TextAlignment.RIGHT_CENTER;
import static de.calamanari.tcanv.TextAlignment.RIGHT_TOP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class TextCanvasTest {

    static final Logger LOGGER = LoggerFactory.getLogger(TextCanvasTest.class);

    @Test
    void testBasics() {

        TextCanvas canvas = new TextCanvas(1, 1);

        assertEquals(1, canvas.getHeight());
        assertEquals(1, canvas.getWidth());
        assertEquals(new CanvasFormat(1, 1), canvas.getFormat());

        assertEquals(0, canvas.getCursorX());
        assertEquals(0, canvas.getCursorY());
        assertEquals(" ", canvas.export());

        canvas.drawBox(DefaultBoxStyle.ASTERISK, 1, 1);
        assertEquals("*", canvas.export());
        canvas.setCursor(0, 0);
        assertEquals('*', canvas.read(false));
        assertEquals(0, canvas.getCursorX());
        assertEquals(0, canvas.getCursorY());
        canvas.write('#');
        canvas.setCursor(0, 0);
        assertEquals('#', canvas.read(true));
        assertEquals(1, canvas.getCursorX());
        assertEquals(0, canvas.getCursorY());
        assertEquals(-1, canvas.read(false));

        canvas.clear();
        assertEquals(0, canvas.getCursorX());
        assertEquals(0, canvas.getCursorY());
        assertEquals(" ", canvas.export());

    }

    @Test
    void testBasics2() {

        TextCanvas canvas = new TextCanvas(10, 10);

        assertEquals(0, canvas.getCursorX());
        assertEquals(0, canvas.getCursorY());

        canvas.setCursor(7, 7);
        assertEquals(7, canvas.getCursorX());
        assertEquals(7, canvas.getCursorY());

        canvas.setCursorX(9);
        canvas.setCursorY(6);
        assertEquals(9, canvas.getCursorX());
        assertEquals(6, canvas.getCursorY());

        canvas.setCursor(700, 900);
        assertEquals(700, canvas.getCursorX());
        assertEquals(900, canvas.getCursorY());

        assertThrows(IllegalArgumentException.class, () -> new TextCanvas(null));
        assertThrows(IllegalArgumentException.class, () -> new TextCanvas(5, 5, null));
        assertThrows(IllegalArgumentException.class, () -> new CanvasFormat(0, 0));

        canvas = new TextCanvas(new CanvasFormat(20, 15));

        assertEquals(15, canvas.getHeight());
        assertEquals(20, canvas.getWidth());

        canvas = new TextCanvas(new CanvasFormat(5, 5));
        canvas.drawLabel(5, 5, "line1\nline2\nline3\nline4\nline5", TextAlignment.CENTER_CENTER);

        assertEquals("""
                line1
                line2
                line3
                line4
                line5""", canvas.export());

        canvas.clear();

        canvas.drawBox(DefaultBoxStyle.THIN, 5, 5);
        canvas.setCursor(0, 0);

        assertEquals("""
                +---+
                |   |
                |   |
                |   |
                +---+""", canvas.export());

        canvas.drawLabel(-1, 5, "line1\nline2\nline3\nline4\nline5", TextAlignment.CENTER_CENTER);

        assertEquals("""
                +---+
                |   |
                |   |
                |   |
                +---+""", canvas.export());

    }

    @Test
    void testBasics3() {

        TextCanvas canvas = new TextCanvas(1, 1);

        canvas.setCursor(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> canvas.write('x'));
        assertThrows(IndexOutOfBoundsException.class, () -> canvas.write("bla"));

        TextCanvas canvas2 = new TextCanvas(5, 5, CanvasBoundCheckStrategy.IGNORE);
        canvas2.drawBox(DefaultBoxStyle.THIN, 5, 5);
        canvas2.setCursor(11, 11);
        canvas2.write('x');
        canvas2.write("bla");

        assertEquals("""
                +---+
                |   |
                |   |
                |   |
                +---+""", canvas2.export());

        assertEquals("TextCanvas [width=5, height=5, cbcStrategy=IGNORE]", canvas2.toString());

    }

    @Test
    void testBoxes() {

        TextCanvas canvas = new TextCanvas(60, 30);

        canvas.setCursor(1, 1);
        canvas.drawBox(DefaultBoxStyle.HASH, 16, 10, "Box 1");
        canvas.setCursor(40, 5);
        canvas.drawBox(DefaultBoxStyle.HASH, 16, 10, "Box 2");

        canvas.setCursor(6, 18);
        canvas.drawBox(DefaultBoxStyle.HASH, 16, 10, "Box 3");

        canvas.setCursor(20, 2);
        canvas.fillSquare(1, 1, '#');

        canvas.setCursor(27, 3);
        canvas.fillSquare(1, 1, 'X');

        canvas.setCursor(25, 2);
        canvas.drawBox(DefaultBoxStyle.DOUBLE, 6, 4, true);

        canvas.setCursor(40, 18);
        canvas.fillSquare(16, 10, ':');
        canvas.setCursor(40, 18);
        canvas.drawBox(DefaultBoxStyle.HASH, 16, 10, "\nLine above\nBox 4\nLine below", true);

        canvas.setCursor(10, 7);
        canvas.drawBox(DefaultBoxStyle.HASH, 34, 15, "Box 5", true);

        canvas.setCursor(0, 0);
        canvas.drawBox(DefaultBoxStyle.THIN, 60, 30, true);

        canvas.setCursor(35, 1);
        canvas.drawBox(DefaultBoxStyle.NONE, 7, 1, "Comment");

        canvas.drawLine(34, 1, 42, 1, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN, CharacterConflictResolver.PRESERVE);

        assertEquals("""
                +----------------------------------------------------------+
                |################                 -Comment-                |
                |#              #   #    ======                            |
                |#              #        = X  =                            |
                |#              #        =    =                            |
                |#    Box 1     #        ======         ################   |
                |#              #                       #              #   |
                |#        ##################################           #   |
                |#        #     #                       #  #           #   |
                |#        #     #                       #  # Box 2     #   |
                |################                       #  #           #   |
                |         #                             #  #           #   |
                |         #                             #  #           #   |
                |         #                             #  #           #   |
                |         #             Box 5           ################   |
                |         #                                #               |
                |         #                                #               |
                |         #                                #               |
                |     ################                  ################   |
                |     #   #          #                  #::#:::::::::::#   |
                |     #   #          #                  #::#:::::::::::#   |
                |     #   ##################################:::::::::::#   |
                |     #    Box 3     #                  #::Line above::#   |
                |     #              #                  #::::Box 4:::::#   |
                |     #              #                  #::Line below::#   |
                |     #              #                  #::::::::::::::#   |
                |     #              #                  #::::::::::::::#   |
                |     ################                  ################   |
                |                                                          |
                +----------------------------------------------------------+""", canvas.export());

        canvas.clear();

        canvas.setCursor(1, 1);
        canvas.drawBox(DefaultBoxStyle.TOP, 16, 10, "Box 1");
        canvas.setCursor(40, 5);
        canvas.drawBox(DefaultBoxStyle.TOP, 16, 10, "Box 2");

        canvas.setCursor(6, 18);
        canvas.drawBox(DefaultBoxStyle.TOP, 16, 10, "Box 3");

        canvas.setCursor(40, 18);
        canvas.fillSquare(16, 10, ':');
        canvas.setCursor(40, 18);
        canvas.drawBox(DefaultBoxStyle.TOP, 16, 10, "\nLine above\nBox 4\nLine below", true);

        canvas.setCursor(10, 7);
        canvas.drawBox(DefaultBoxStyle.TOP, 34, 15, "Box 5", true);

        canvas.setCursor(0, 0);
        canvas.drawBox(DefaultBoxStyle.THIN, 60, 30, true);

        assertEquals("""
                +----------------------------------------------------------+
                |----------------                                          |
                |                                                          |
                |                                                          |
                |                                                          |
                |     Box 1                             ----------------   |
                |                                                          |
                |         ----------------------------------               |
                |                                                          |
                |                                            Box 2         |
                |                                                          |
                |                                                          |
                |                                                          |
                |                                                          |
                |                       Box 5                              |
                |                                                          |
                |                                                          |
                |                                                          |
                |     ----------------                  ----------------   |
                |                                       ::::::::::::::::   |
                |                                       ::::::::::::::::   |
                |                                       ::::::::::::::::   |
                |          Box 3                        :::Line above:::   |
                |                                       :::::Box 4::::::   |
                |                                       :::Line below:::   |
                |                                       ::::::::::::::::   |
                |                                       ::::::::::::::::   |
                |                                       ::::::::::::::::   |
                |                                                          |
                +----------------------------------------------------------+""", canvas.export());

        canvas.clear();

        canvas.setCursor(1, 1);
        canvas.drawBox(DefaultBoxStyle.TOP_AND_BOTTOM_DOUBLE, 16, 10, "Box 1");
        canvas.setCursor(40, 5);
        canvas.drawBox(DefaultBoxStyle.TOP_AND_BOTTOM_DOUBLE, 16, 10, "Box 2");

        canvas.setCursor(6, 18);
        canvas.drawBox(DefaultBoxStyle.TOP_AND_BOTTOM_DOUBLE, 16, 10, "Box 3");

        canvas.setCursor(40, 18);
        canvas.fillSquare(16, 10, ':');
        canvas.setCursor(40, 18);
        canvas.drawBox(DefaultBoxStyle.TOP_AND_BOTTOM_DOUBLE, 16, 10, "\nLine above\nBox 4\nLine below", true);

        canvas.setCursor(10, 7);
        canvas.drawBox(DefaultBoxStyle.TOP_AND_BOTTOM_DOUBLE, 34, 15, "Box 5", true);

        canvas.setCursor(0, 0);
        canvas.drawBox(DefaultBoxStyle.THIN, 60, 30, true);

        assertEquals("""
                +----------------------------------------------------------+
                |================                                          |
                |                                                          |
                |                                                          |
                |                                                          |
                |     Box 1                             ================   |
                |                                                          |
                |         ==================================               |
                |                                                          |
                |                                            Box 2         |
                |================                                          |
                |                                                          |
                |                                                          |
                |                                                          |
                |                       Box 5           ================   |
                |                                                          |
                |                                                          |
                |                                                          |
                |     ================                  ================   |
                |                                       ::::::::::::::::   |
                |                                       ::::::::::::::::   |
                |         ==================================::::::::::::   |
                |          Box 3                        :::Line above:::   |
                |                                       :::::Box 4::::::   |
                |                                       :::Line below:::   |
                |                                       ::::::::::::::::   |
                |                                       ::::::::::::::::   |
                |     ================                  ================   |
                |                                                          |
                +----------------------------------------------------------+""", canvas.export());

        canvas.clear();

        canvas.setCursor(1, 1);
        canvas.drawBox(DefaultBoxStyle.DOTTED, 16, 10, "Box 1");
        canvas.setCursor(40, 5);
        canvas.drawBox(DefaultBoxStyle.DOTTED, 16, 10, "Box 2");

        canvas.setCursor(6, 18);
        canvas.drawBox(DefaultBoxStyle.DOTTED, 16, 10, "Box 3");

        canvas.setCursor(40, 18);
        canvas.fillSquare(16, 10, '*');
        canvas.setCursor(40, 18);
        canvas.drawBox(DefaultBoxStyle.DOTTED, 16, 10, "\nLine above\nBox 4\nLine below", true);

        canvas.setCursor(10, 7);
        canvas.drawBox(DefaultBoxStyle.DOTTED, 34, 15, "Box 5", true);

        canvas.setCursor(0, 0);
        canvas.drawBox(DefaultBoxStyle.THIN, 60, 30, true);

        assertEquals("""
                +----------------------------------------------------------+
                |................                                          |
                |:              :                                          |
                |:              :                                          |
                |:              :                                          |
                |:    Box 1     :                       ................   |
                |:              :                       :              :   |
                |:        ..................................           :   |
                |:        :     :                       :  :           :   |
                |:        :     :                       :  : Box 2     :   |
                |.........:......                       :  :           :   |
                |         :                             :  :           :   |
                |         :                             :  :           :   |
                |         :                             :  :           :   |
                |         :             Box 5           ...:............   |
                |         :                                :               |
                |         :                                :               |
                |         :                                :               |
                |     ....:...........                  ...:............   |
                |     :   :          :                  :**:***********:   |
                |     :   :          :                  :**:***********:   |
                |     :   ..................................***********:   |
                |     :    Box 3     :                  :**Line above**:   |
                |     :              :                  :****Box 4*****:   |
                |     :              :                  :**Line below**:   |
                |     :              :                  :**************:   |
                |     :              :                  :**************:   |
                |     ................                  ................   |
                |                                                          |
                +----------------------------------------------------------+""", canvas.export());

    }

    @Test
    void testTextAlignment() {

        TextCanvas canvas = new TextCanvas(30, 20);

        canvas.drawBox(DefaultBoxStyle.THIN, 30, 20, true);

        canvas.setCursor(6, 5);
        canvas.drawBox(DefaultBoxStyle.THIN, 16, 10, "Box 1");

        assertEquals("""
                +----------------------------+
                |                            |
                |                            |
                |                            |
                |                            |
                |     +--------------+       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |    Box 1     |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     +--------------+       |
                |                            |
                |                            |
                |                            |
                |                            |
                +----------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 30, 20, true);

        canvas.setCursor(6, 5);
        canvas.drawBox(DefaultBoxStyle.THIN, 16, 10, "Box 1", LEFT_TOP);

        assertEquals("""
                +----------------------------+
                |                            |
                |                            |
                |                            |
                |                            |
                |     +--------------+       |
                |     |Box 1         |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     +--------------+       |
                |                            |
                |                            |
                |                            |
                |                            |
                +----------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 30, 20, true);

        canvas.setCursor(6, 5);
        canvas.drawBox(DefaultBoxStyle.THIN, 16, 10, "Box 1", LEFT_CENTER);

        assertEquals("""
                +----------------------------+
                |                            |
                |                            |
                |                            |
                |                            |
                |     +--------------+       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |Box 1         |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     +--------------+       |
                |                            |
                |                            |
                |                            |
                |                            |
                +----------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 30, 20, true);

        canvas.setCursor(6, 5);
        canvas.drawBox(DefaultBoxStyle.THIN, 16, 10, "Box 1", LEFT_BOTTOM);

        assertEquals("""
                +----------------------------+
                |                            |
                |                            |
                |                            |
                |                            |
                |     +--------------+       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |Box 1         |       |
                |     +--------------+       |
                |                            |
                |                            |
                |                            |
                |                            |
                +----------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 30, 20, true);

        canvas.setCursor(6, 5);
        canvas.drawBox(DefaultBoxStyle.THIN, 16, 10, "Box 1", RIGHT_TOP);

        assertEquals("""
                +----------------------------+
                |                            |
                |                            |
                |                            |
                |                            |
                |     +--------------+       |
                |     |         Box 1|       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     +--------------+       |
                |                            |
                |                            |
                |                            |
                |                            |
                +----------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 30, 20, true);

        canvas.setCursor(6, 5);
        canvas.drawBox(DefaultBoxStyle.THIN, 16, 10, "Box 1", RIGHT_CENTER);

        assertEquals("""
                +----------------------------+
                |                            |
                |                            |
                |                            |
                |                            |
                |     +--------------+       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |         Box 1|       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     +--------------+       |
                |                            |
                |                            |
                |                            |
                |                            |
                +----------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 30, 20, true);

        canvas.setCursor(6, 5);
        canvas.drawBox(DefaultBoxStyle.THIN, 16, 10, "Box 1", RIGHT_BOTTOM);

        assertEquals("""
                +----------------------------+
                |                            |
                |                            |
                |                            |
                |                            |
                |     +--------------+       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |         Box 1|       |
                |     +--------------+       |
                |                            |
                |                            |
                |                            |
                |                            |
                +----------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 30, 20, true);

        canvas.setCursor(6, 5);
        canvas.drawBox(DefaultBoxStyle.THIN, 16, 10, "Box 1", CENTER_TOP);

        assertEquals("""
                +----------------------------+
                |                            |
                |                            |
                |                            |
                |                            |
                |     +--------------+       |
                |     |    Box 1     |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     +--------------+       |
                |                            |
                |                            |
                |                            |
                |                            |
                +----------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 30, 20, true);

        canvas.setCursor(6, 5);
        canvas.drawBox(DefaultBoxStyle.THIN, 16, 10, "Box 1", CENTER_CENTER);

        assertEquals("""
                +----------------------------+
                |                            |
                |                            |
                |                            |
                |                            |
                |     +--------------+       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |    Box 1     |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     +--------------+       |
                |                            |
                |                            |
                |                            |
                |                            |
                +----------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 30, 20, true);

        canvas.setCursor(6, 5);
        canvas.drawBox(DefaultBoxStyle.THIN, 16, 10, "Box 1", CENTER_BOTTOM);

        assertEquals("""
                +----------------------------+
                |                            |
                |                            |
                |                            |
                |                            |
                |     +--------------+       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |              |       |
                |     |    Box 1     |       |
                |     +--------------+       |
                |                            |
                |                            |
                |                            |
                |                            |
                +----------------------------+""", canvas.export());

    }

    @Test
    void testConnectorH() {
        TextCanvas canvas = new TextCanvas(40, 40);

        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // H - arrow-arrow
        // -----------------------------------------------
        canvas.drawLine(16, 5, 16, 5, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 6, 17, 6, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 7, 18, 7, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 8, 25, 8, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |               -                      |
                |               <>                     |
                |               <->                    |
                |               <-------->             |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // H - arrow-plain
        // -----------------------------------------------
        canvas.drawLine(16, 5, 16, 5, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 6, 17, 6, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 7, 18, 7, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 8, 25, 8, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |               -                      |
                |               <-                     |
                |               <--                    |
                |               <---------             |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // H - plain-arrow
        // -----------------------------------------------
        canvas.drawLine(16, 5, 16, 5, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 6, 17, 6, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 7, 18, 7, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 8, 25, 8, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |               -                      |
                |               ->                     |
                |               -->                    |
                |               --------->             |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // H - plain-plain
        // -----------------------------------------------
        canvas.drawLine(16, 5, 16, 5, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 6, 17, 6, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 7, 18, 7, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 8, 25, 8, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |               -                      |
                |               --                     |
                |               ---                    |
                |               ----------             |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

    }

    @Test
    void testConnectorHV() {
        TextCanvas canvas = new TextCanvas(40, 40);
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HV - arrow-arrow
        // -----------------------------------------------
        canvas.drawLine(2, 2, 3, 2, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(5, 2, 7, 2, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(9, 2, 12, 2, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(14, 2, 15, 3, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(17, 2, 18, 4, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(20, 2, 21, 5, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.TOP_ARROW);

        canvas.drawLine(12, 8, 13, 7, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(15, 11, 16, 9, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(18, 15, 19, 12, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);

        canvas.drawLine(3, 35, 2, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(7, 35, 5, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(12, 35, 9, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(15, 35, 14, 36, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(18, 35, 17, 37, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(21, 35, 20, 38, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | <V <+V <-+V <+ <+ <+                 |
                |              V  |  |                 |
                |                 V  |                 |
                |                    V                 |
                |                                      |
                |            A                         |
                |           <+                         |
                |               A                      |
                |               |                      |
                |              <+                      |
                |                  A                   |
                |                  |                   |
                |                  |                   |
                |                 <+                   |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | V> V+> V+-> +> +> +>                 |
                |             V  |  |                  |
                |                V  |                  |
                |                   V                  |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HV - arrow-plain
        // -----------------------------------------------
        canvas.drawLine(2, 2, 3, 2, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(5, 2, 7, 2, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(9, 2, 12, 2, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(14, 2, 15, 3, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(17, 2, 18, 4, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(19, 2, 21, 5, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.TOP_PLAIN);

        canvas.drawLine(12, 8, 13, 7, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(15, 9, 16, 7, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(18, 10, 19, 7, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);

        canvas.drawLine(3, 35, 2, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(7, 35, 5, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(12, 35, 9, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(15, 35, 14, 36, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(18, 35, 17, 37, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(21, 35, 20, 38, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | <+ <-+ <--+ <+ <+<-+                 |
                |              |  |  |                 |
                |                 |  |                 |
                |                    |                 |
                |                                      |
                |            |  |  |                   |
                |           <+  |  |                   |
                |              <+  |                   |
                |                 <+                   |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | +> +-> +--> +> +> +>                 |
                |             |  |  |                  |
                |                |  |                  |
                |                   |                  |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HV - plain-arrow
        // -----------------------------------------------
        canvas.drawLine(2, 2, 3, 2, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(5, 2, 7, 2, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(9, 2, 12, 2, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(14, 2, 15, 3, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(17, 2, 18, 4, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(20, 2, 21, 5, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.TOP_ARROW);

        canvas.drawLine(12, 8, 13, 7, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(15, 9, 16, 8, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(18, 10, 19, 8, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);

        canvas.drawLine(3, 35, 2, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(7, 35, 5, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(12, 35, 9, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(15, 35, 14, 36, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(18, 35, 17, 37, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(21, 35, 20, 38, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | +V -+V --+V -+ -+ -+                 |
                |              V  |  |                 |
                |                 V  |                 |
                |                    V                 |
                |                                      |
                |            A                         |
                |           -+  A  A                   |
                |              -+  |                   |
                |                 -+                   |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | V+ V+- V+-- +- +- +-                 |
                |             V  |  |                  |
                |                V  |                  |
                |                   V                  |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HV - plain-plain
        // -----------------------------------------------
        canvas.drawLine(2, 2, 3, 2, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(5, 2, 7, 2, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(9, 2, 12, 2, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(14, 2, 15, 3, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(17, 2, 18, 4, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(20, 2, 21, 5, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);

        canvas.drawLine(12, 8, 13, 7, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(15, 9, 16, 8, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(18, 10, 19, 8, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);

        canvas.drawLine(3, 35, 2, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(7, 35, 5, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(12, 35, 9, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(15, 35, 14, 36, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(18, 35, 17, 37, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(20, 35, 20, 38, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | -+ --+ ---+ -+ -+ -+                 |
                |              |  |  |                 |
                |                 |  |                 |
                |                    |                 |
                |                                      |
                |            |                         |
                |           -+  |  |                   |
                |              -+  |                   |
                |                 -+                   |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | +- +-- +--- +- +- +                  |
                |             |  |  |                  |
                |                |  |                  |
                |                   |                  |
                +--------------------------------------+""", canvas.export());

    }

    @Test
    void testConnectorHVH() {

        TextCanvas canvas = new TextCanvas(40, 40);
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HVH - arrow-arrow
        // -----------------------------------------------
        canvas.drawLine(2, 2, 2, 3, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 5, 3, 6, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 8, 3, 10, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 12, 3, 15, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 17, 4, 20, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 22, 4, 25, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 27, 5, 28, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 30, 6, 33, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);

        canvas.drawLine(10, 3, 9, 4, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(10, 6, 9, 8, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(10, 10, 9, 13, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(11, 15, 9, 18, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(11, 20, 9, 23, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(12, 25, 9, 26, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(13, 28, 9, 31, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);

        canvas.drawLine(16, 3, 16, 2, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 6, 17, 5, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 10, 17, 8, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 15, 17, 12, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 20, 18, 17, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 25, 18, 22, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 28, 19, 27, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 33, 20, 30, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | <             >                      |
                | >       >     <                      |
                |        <                             |
                | <              >                     |
                |  >     +>     <                      |
                |        +                             |
                | <+     <       >                     |
                |  +             +                     |
                |  >     +>     <+                     |
                |        |                             |
                | <+     +       >                     |
                |  |     <       +                     |
                |  +             |                     |
                |  >      +>    <+                     |
                |         |                            |
                | <+      |      +>                    |
                |  |     <+      |                     |
                |  |             |                     |
                |  +>     +>    <+                     |
                |         |                            |
                | <+      |      +>                    |
                |  |     <+      |                     |
                |  |             |                     |
                |  +>     +->   <+                     |
                |        <+                            |
                | <-+             +>                   |
                |   +>     +->  <-+                    |
                |          |                           |
                | <-+      |      +->                  |
                |   |    <-+      |                    |
                |   |             |                    |
                |   +->         <-+                    |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HVH - arrow-plain
        // -----------------------------------------------
        canvas.drawLine(2, 2, 2, 3, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 5, 3, 6, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 8, 3, 10, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 12, 3, 15, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 17, 4, 20, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 22, 4, 25, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 27, 5, 28, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 30, 6, 33, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);

        canvas.drawLine(10, 3, 9, 4, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(10, 6, 9, 8, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(10, 10, 9, 13, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(11, 15, 9, 18, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(11, 20, 9, 23, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(12, 25, 9, 26, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(13, 28, 9, 31, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);

        canvas.drawLine(16, 3, 16, 2, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 6, 17, 5, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 10, 17, 8, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 15, 17, 12, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 20, 18, 17, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 25, 18, 22, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 28, 19, 27, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 33, 20, 30, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | <             -                      |
                | -      +>     <                      |
                |        +                             |
                | <+             +                     |
                |  +     +>     <+                     |
                |        |                             |
                | <+     +       +                     |
                |  |             |                     |
                |  +     +>     <+                     |
                |        |                             |
                | <+     |       +                     |
                |  |     +       |                     |
                |  |             |                     |
                |  +     +->    <+                     |
                |        |                             |
                | <-+    |        +                    |
                |   |    +        |                    |
                |   |             |                    |
                |   +    +->    <-+                    |
                |        |                             |
                | <-+    |        +                    |
                |   |    +        |                    |
                |   |             |                    |
                |   +     +->   <-+                    |
                |        -+                            |
                | <-+             +-                   |
                |   +-    +-->  <-+                    |
                |         |                            |
                | <--+    |        +-                  |
                |    |   -+        |                   |
                |    |             |                   |
                |    +-         <--+                   |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HVH - plain-arrow
        // -----------------------------------------------
        canvas.drawLine(2, 2, 2, 3, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 5, 3, 6, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 8, 3, 10, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 12, 3, 15, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 17, 4, 20, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 22, 4, 25, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 27, 5, 28, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(2, 30, 6, 33, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);

        canvas.drawLine(10, 3, 9, 4, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(10, 6, 9, 8, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(10, 10, 9, 13, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(11, 15, 9, 18, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(11, 20, 9, 23, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(12, 25, 9, 26, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(13, 28, 9, 31, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);

        canvas.drawLine(16, 3, 16, 2, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 6, 17, 5, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 10, 17, 8, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 15, 17, 12, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 20, 18, 17, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 25, 18, 22, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 28, 19, 27, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(16, 33, 20, 30, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | -             >                      |
                | >       +     -                      |
                |        <+                            |
                | +             +>                     |
                | +>      +     +                      |
                |         |                            |
                | +      <+     +>                     |
                | |             |                      |
                | +>      +     +                      |
                |         |                            |
                | +       |     +>                     |
                | |      <+     |                      |
                | |             |                      |
                | +>      +-    +                      |
                |         |                            |
                | -+      |      +>                    |
                |  |     <+      |                     |
                |  |             |                     |
                |  +>     +-    -+                     |
                |         |                            |
                | -+      |      +>                    |
                |  |     <+      |                     |
                |  |             |                     |
                |  +>      +-   -+                     |
                |        <-+                           |
                | -+             +->                   |
                |  +->     +--  -+                     |
                |          |                           |
                | --+      |      +->                  |
                |   |    <-+      |                    |
                |   |             |                    |
                |   +->         --+                    |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HVH - plain-plain
        // -----------------------------------------------
        canvas.drawLine(2, 2, 2, 3, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 5, 3, 6, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 8, 3, 10, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 12, 3, 15, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 17, 4, 20, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 22, 4, 25, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 27, 5, 28, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(2, 30, 6, 33, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);

        canvas.drawLine(10, 3, 9, 4, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(10, 6, 9, 8, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(10, 10, 9, 13, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(11, 15, 9, 18, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(11, 20, 9, 23, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(12, 25, 9, 26, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(13, 28, 9, 31, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);

        canvas.drawLine(16, 3, 16, 2, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 6, 17, 5, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 10, 17, 8, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 15, 17, 12, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 20, 18, 17, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 25, 18, 22, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 28, 19, 27, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(16, 33, 20, 30, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | +             +                      |
                | +      +-     +                      |
                |        +                             |
                | -+             +                     |
                |  +     +-     -+                     |
                |        |                             |
                | -+     +       +                     |
                |  |             |                     |
                |  +     +-     -+                     |
                |        |                             |
                | -+     |       +                     |
                |  |     +       |                     |
                |  |             |                     |
                |  +      +-    -+                     |
                |         |                            |
                | -+      |      +-                    |
                |  |     -+      |                     |
                |  |             |                     |
                |  +-     +-    -+                     |
                |         |                            |
                | -+      |      +-                    |
                |  |     -+      |                     |
                |  |             |                     |
                |  +-     +--   -+                     |
                |        -+                            |
                | --+             +-                   |
                |   +-     +--  --+                    |
                |          |                           |
                | --+      |      +--                  |
                |   |    --+      |                    |
                |   |             |                    |
                |   +--         --+                    |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

    }

    @Test
    void testConnectorHVHC() {

        TextCanvas canvas = new TextCanvas(40, 40);
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HVHC - arrow-arrow
        // -----------------------------------------------
        canvas.drawLine(5, 30, 5, 31, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(5, 33, 5, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(15, 31, 15, 30, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(15, 35, 15, 33, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(25, 30, 27, 31, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(25, 33, 27, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(35, 31, 37, 30, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(35, 35, 37, 33, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | +-->      +-->      +-->      +----> |
                | +-->      +-->      +---->    +-->   |
                |                                      |
                | +-->      +-->      +-->      +----> |
                | |         |         |         |      |
                | +-->      +-->      +---->    +-->   |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HVHC - arrow-plain
        // -----------------------------------------------
        canvas.drawLine(5, 30, 5, 31, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(5, 33, 5, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(15, 31, 15, 30, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(15, 35, 15, 33, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(25, 30, 27, 31, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(25, 33, 27, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(35, 31, 37, 30, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(35, 35, 37, 33, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.LEFT_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | +-->      +---      +-->      +----- |
                | +---      +-->      +-----    +-->   |
                |                                      |
                | +-->      +---      +-->      +----- |
                | |         |         |         |      |
                | +---      +-->      +-----    +-->   |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HVHC - plain-arrow
        // -----------------------------------------------
        canvas.drawLine(5, 30, 5, 31, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(5, 33, 5, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(15, 31, 15, 30, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(15, 35, 15, 33, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(25, 30, 27, 31, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(25, 33, 27, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(35, 31, 37, 30, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(35, 35, 37, 33, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | +---      +-->       +--       +---> |
                | +-->      +---       +--->     +--   |
                |                                      |
                | +---      +-->       +--       +---> |
                | |         |          |         |     |
                | +-->      +---       +--->     +--   |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HVHC - plain-plain
        // -----------------------------------------------
        canvas.drawLine(5, 30, 5, 31, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(5, 33, 5, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(15, 31, 15, 30, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(15, 35, 15, 33, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(25, 30, 27, 31, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(25, 33, 27, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(35, 31, 37, 30, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(35, 35, 37, 33, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |  +--       +--       +--       +---- |
                |  +--       +--       +----     +--   |
                |                                      |
                |  +--       +--       +--       +---- |
                |  |         |         |         |     |
                |  +--       +--       +----     +--   |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

    }

    @Test
    void testConnectorHVHCT() {

        TextCanvas canvas = new TextCanvas(40, 40);
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HVHCT - arrow-arrow
        // -----------------------------------------------
        canvas.drawLine(3, 30, 3, 31, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(3, 33, 3, 35, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(10, 31, 10, 30, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(10, 35, 10, 33, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(20, 30, 22, 31, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(20, 33, 22, 35, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(30, 31, 32, 30, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(30, 35, 32, 33, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |  <--+   <--+      <----+      <--+   |
                |  <--+   <--+        <--+    <----+   |
                |                                      |
                |  <--+   <--+      <----+      <--+   |
                |     |      |           |         |   |
                |  <--+   <--+        <--+    <----+   |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HVHCT - arrow-plain
        // -----------------------------------------------
        canvas.drawLine(3, 30, 3, 31, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(3, 33, 3, 35, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(10, 31, 10, 30, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(10, 35, 10, 33, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(20, 30, 22, 31, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(20, 33, 22, 35, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(30, 31, 32, 30, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(30, 35, 32, 33, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.RIGHT_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |  <--+   ---+      <---+       --+    |
                |  ---+   <--+        --+     <---+    |
                |                                      |
                |  <--+   ---+      <---+       --+    |
                |     |      |          |         |    |
                |  ---+   <--+        --+     <---+    |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HVHCT - plain-arrow
        // -----------------------------------------------
        canvas.drawLine(3, 30, 3, 31, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(3, 33, 3, 35, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(10, 31, 10, 30, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(10, 35, 10, 33, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(20, 30, 22, 31, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(20, 33, 22, 35, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(30, 31, 32, 30, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(30, 35, 32, 33, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |  ---+   <--+      -----+      <--+   |
                |  <--+   ---+        <--+    -----+   |
                |                                      |
                |  ---+   <--+      -----+      <--+   |
                |     |      |           |         |   |
                |  <--+   ---+        <--+    -----+   |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // HVHCT - plain-plain
        // -----------------------------------------------
        canvas.drawLine(3, 30, 3, 31, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(3, 33, 3, 35, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(10, 31, 10, 30, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(10, 35, 10, 33, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(20, 30, 22, 31, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(20, 33, 22, 35, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(30, 31, 32, 30, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);
        canvas.drawLine(30, 35, 32, 33, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.RIGHT_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |  --+    --+       ----+       --+    |
                |  --+    --+         --+     ----+    |
                |                                      |
                |  --+    --+       ----+       --+    |
                |    |      |           |         |    |
                |  --+    --+         --+     ----+    |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

    }

    @Test
    void testConnectorV() {

        TextCanvas canvas = new TextCanvas(40, 40);
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // V - arrow-arrow
        // -----------------------------------------------
        canvas.drawLine(8, 10, 8, 10, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(10, 10, 10, 11, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(12, 10, 12, 12, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(14, 10, 14, 17, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |       | A A A                        |
                |         V | |                        |
                |           V |                        |
                |             |                        |
                |             |                        |
                |             |                        |
                |             |                        |
                |             V                        |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // V - arrow-plain
        // -----------------------------------------------
        canvas.drawLine(8, 10, 8, 10, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(10, 10, 10, 11, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(12, 10, 12, 12, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(14, 10, 14, 17, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |       | A A A                        |
                |         | | |                        |
                |           | |                        |
                |             |                        |
                |             |                        |
                |             |                        |
                |             |                        |
                |             |                        |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // V - plain-arrow
        // -----------------------------------------------
        canvas.drawLine(8, 10, 8, 10, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(10, 10, 10, 11, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(12, 10, 12, 12, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(14, 10, 14, 17, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |       | | | |                        |
                |         V | |                        |
                |           V |                        |
                |             |                        |
                |             |                        |
                |             |                        |
                |             |                        |
                |             V                        |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // V - plain-plain
        // -----------------------------------------------
        canvas.drawLine(8, 10, 8, 10, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(10, 10, 10, 11, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(12, 10, 12, 12, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(14, 10, 14, 17, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |       | | | |                        |
                |         | | |                        |
                |           | |                        |
                |             |                        |
                |             |                        |
                |             |                        |
                |             |                        |
                |             |                        |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

    }

    @Test
    void testConnectorVH() {

        TextCanvas canvas = new TextCanvas(40, 40);
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VH - arrow-arrow
        // -----------------------------------------------
        canvas.drawLine(2, 2, 3, 2, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(5, 2, 7, 2, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(9, 2, 12, 2, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(14, 2, 15, 3, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(17, 2, 18, 4, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(20, 2, 21, 5, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(20, 25, 15, 20, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.RIGHT_ARROW);
        canvas.drawLine(12, 11, 13, 10, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(15, 12, 16, 10, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(18, 13, 19, 10, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(3, 35, 2, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(10, 35, 8, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(18, 35, 15, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(24, 35, 23, 36, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(30, 35, 29, 37, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(36, 35, 35, 38, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | A> A+> A+-> A  A  A                  |
                |             +> |  |                  |
                |                +> |                  |
                |                   +>                 |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |            A  A  A                   |
                |           <+  |  |                   |
                |              <+  |                   |
                |                 <+                   |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |              <----+                  |
                |                   |                  |
                |                   |                  |
                |                   |                  |
                |                   |                  |
                |                   V                  |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | V>    V+>    V+->    +>    +>    +>  |
                |                      V     |     |   |
                |                            V     |   |
                |                                  V   |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VH - arrow-plain
        // -----------------------------------------------
        canvas.drawLine(2, 2, 3, 2, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(5, 2, 7, 2, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(9, 2, 12, 2, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(14, 2, 15, 3, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(17, 2, 18, 4, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(20, 2, 21, 5, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(12, 11, 13, 10, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(15, 12, 16, 10, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(18, 13, 19, 10, DefaultConnectorEndType.RIGHT_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(3, 35, 2, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(10, 35, 8, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(18, 35, 15, 35, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(24, 35, 23, 36, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(30, 35, 29, 37, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(36, 35, 35, 38, DefaultConnectorEndType.LEFT_ARROW, DefaultConnectorEndType.TOP_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | A+ A+- A+-- A  A  A                  |
                |             +- |  |                  |
                |                +- |                  |
                |                   +-                 |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |            |  |  |                   |
                |           <+  |  |                   |
                |              <+  |                   |
                |                 <+                   |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | +>    +->    +-->    +>    +>    +>  |
                |                      |     |     |   |
                |                            |     |   |
                |                                  |   |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VH - plain-arrow
        // -----------------------------------------------
        canvas.drawLine(2, 2, 3, 2, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(5, 2, 7, 2, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(9, 2, 12, 2, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(14, 2, 15, 3, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(17, 2, 18, 4, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(20, 2, 21, 5, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_ARROW);
        canvas.drawLine(12, 11, 13, 10, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(15, 12, 16, 10, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(18, 13, 19, 10, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(3, 35, 2, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(10, 35, 8, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(18, 35, 15, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(24, 35, 23, 36, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(30, 35, 29, 37, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(36, 35, 35, 38, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | +> +-> +--> |  |  |                  |
                |             +> |  |                  |
                |                +> |                  |
                |                   +>                 |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |            A  A  A                   |
                |           -+  |  |                   |
                |              -+  |                   |
                |                 -+                   |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | V+    V+-    V+--    +-    +-    +-  |
                |                      V     |     |   |
                |                            V     |   |
                |                                  V   |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VH - plain-plain
        // -----------------------------------------------
        canvas.drawLine(2, 2, 3, 2, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(5, 2, 7, 2, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(9, 2, 12, 2, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(14, 2, 15, 3, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(17, 2, 18, 4, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(20, 2, 21, 5, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.LEFT_PLAIN);
        canvas.drawLine(12, 11, 13, 10, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(15, 12, 16, 10, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(18, 13, 19, 10, DefaultConnectorEndType.RIGHT_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(3, 35, 2, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(10, 35, 8, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(18, 35, 15, 35, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(24, 35, 23, 36, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(30, 35, 29, 37, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(36, 35, 35, 38, DefaultConnectorEndType.LEFT_PLAIN, DefaultConnectorEndType.TOP_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | +- +-- +--- |  |  |                  |
                |             +- |  |                  |
                |                +- |                  |
                |                   +-                 |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |            |  |  |                   |
                |           -+  |  |                   |
                |              -+  |                   |
                |                 -+                   |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | +-    +--    +---    +-    +-    +-  |
                |                      |     |     |   |
                |                            |     |   |
                |                                  |   |
                +--------------------------------------+""", canvas.export());

    }

    @Test
    void testConnectorVHV() {

        TextCanvas canvas = new TextCanvas(40, 40);
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VHV - arrow-arrow
        // -----------------------------------------------
        canvas.drawLine(2, 2, 3, 2, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(5, 2, 6, 3, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(8, 2, 10, 3, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(12, 2, 15, 3, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(17, 2, 18, 4, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(20, 2, 22, 5, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(24, 2, 26, 6, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);

        canvas.drawLine(3, 10, 4, 9, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(6, 10, 8, 9, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(10, 10, 13, 9, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(15, 11, 16, 9, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(18, 12, 20, 9, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(22, 13, 24, 9, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);

        canvas.drawLine(3, 30, 2, 30, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(6, 30, 5, 31, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(10, 30, 8, 31, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(15, 30, 12, 31, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(18, 30, 17, 32, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(22, 30, 20, 33, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(26, 30, 24, 34, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | AV A  A   A    A  A   A              |
                |     V ++V +-+V ++ |   |              |
                |                 V +-+ +-+            |
                |                     V   |            |
                |                         V            |
                |                                      |
                |                                      |
                |   A ++A +-+A  A   A   A              |
                |  V  V   V    ++ +-+   |              |
                |              V  |   +-+              |
                |                 V   |                |
                |                     V                |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | VA  A   A    A  A   A   A            |
                |    V  V++ V+-+ ++   |   |            |
                |                V  +-+ +-+            |
                |                   V   |              |
                |                       V              |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VHV - arrow-plain
        // -----------------------------------------------
        canvas.drawLine(2, 2, 3, 2, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(5, 2, 6, 3, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(8, 2, 10, 3, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(12, 2, 15, 3, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(17, 2, 18, 4, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(20, 2, 22, 5, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(24, 2, 26, 6, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);

        canvas.drawLine(3, 10, 4, 9, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(6, 10, 8, 9, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(10, 10, 13, 9, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(15, 11, 16, 9, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(18, 12, 20, 9, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(22, 13, 24, 9, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);

        canvas.drawLine(3, 30, 2, 30, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(6, 30, 5, 31, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(10, 30, 8, 31, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(15, 30, 12, 31, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(18, 30, 17, 32, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(22, 30, 20, 33, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(26, 30, 24, 34, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.TOP_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | A| A  A   A    A  A   A              |
                |    ++ +-+ +--+ |  |   |              |
                |                ++ +-+ |              |
                |                     | +-+            |
                |                         |            |
                |                                      |
                |                                      |
                |  ++ +-+ +--+ ++   |   |              |
                |  V  V   V    |  +-+ +-+              |
                |              V  |   |                |
                |                 V   |                |
                |                     V                |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | |A  A   A    A  A   A   A            |
                |    ++ +-+ +--+  |   |   |            |
                |                ++ +-+   |            |
                |                   |   +-+            |
                |                       |              |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VHV - plain-arrow
        // -----------------------------------------------
        canvas.drawLine(2, 2, 3, 2, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(5, 2, 6, 3, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(8, 2, 10, 3, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(12, 2, 15, 3, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(17, 2, 18, 4, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(20, 2, 22, 5, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(24, 2, 26, 6, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);

        canvas.drawLine(3, 10, 4, 9, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(6, 10, 8, 9, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(10, 10, 13, 9, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(15, 11, 16, 9, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(18, 12, 20, 9, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(22, 13, 24, 9, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);

        canvas.drawLine(3, 30, 2, 30, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(6, 30, 5, 31, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(10, 30, 8, 31, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(15, 30, 12, 31, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(18, 30, 17, 32, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(22, 30, 20, 33, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(26, 30, 24, 34, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | |V ++ +-+ +--+ |  |   |              |
                |     V   V    V ++ +-+ |              |
                |                 V   | +-+            |
                |                     V   |            |
                |                         V            |
                |                                      |
                |                                      |
                |   A   A    A  A   A   A              |
                |  ++ +-+ +--+ ++   |   |              |
                |              |  +-+ +-+              |
                |                 |   |                |
                |                     |                |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | V| ++ +-+ +--+  |   |   |            |
                |    V  V   V    ++ +-+   |            |
                |                V  |   +-+            |
                |                   V   |              |
                |                       V              |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VHV - plain-plain
        // -----------------------------------------------
        canvas.drawLine(2, 2, 3, 2, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(5, 2, 6, 3, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(8, 2, 10, 3, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(12, 2, 15, 3, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(17, 2, 18, 4, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(20, 2, 22, 5, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(24, 2, 26, 6, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);

        canvas.drawLine(3, 10, 4, 9, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(6, 10, 8, 9, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(10, 10, 13, 9, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(15, 11, 16, 9, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(18, 12, 20, 9, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(22, 13, 24, 9, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);

        canvas.drawLine(3, 30, 2, 30, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(6, 30, 5, 31, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(10, 30, 8, 31, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(15, 30, 12, 31, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(18, 30, 17, 32, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(22, 30, 20, 33, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(26, 30, 24, 34, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.TOP_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                | ++ |  |   |    |  |   |              |
                |    ++ +-+ +--+ ++ |   |              |
                |                 | +-+ +-+            |
                |                     |   |            |
                |                         |            |
                |                                      |
                |                                      |
                |  ++ +-+ +--+  |   |   |              |
                |  |  |   |    ++ +-+   |              |
                |              |  |   +-+              |
                |                 |   |                |
                |                     |                |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                | ++  |   |    |  |   |   |            |
                |    ++ +-+ +--+ ++   |   |            |
                |                |  +-+ +-+            |
                |                   |   |              |
                |                       |              |
                |                                      |
                |                                      |
                |                                      |
                |                                      |
                +--------------------------------------+""", canvas.export());

    }

    @Test
    void testConnectorVHVU() {

        TextCanvas canvas = new TextCanvas(40, 40);
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VHVU - arrow-arrow
        // -----------------------------------------------
        canvas.drawLine(25, 2, 26, 2, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(25, 6, 27, 6, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(26, 10, 25, 10, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(27, 14, 25, 14, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(25, 18, 27, 20, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(25, 24, 27, 26, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(25, 30, 30, 29, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(25, 36, 30, 34, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                        AA            |
                |                        ||            |
                |                        ++            |
                |                                      |
                |                        A A           |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                        AA            |
                |                        ||            |
                |                        ++            |
                |                                      |
                |                        A A           |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                        A             |
                |                        |             |
                |                        | A           |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                        A             |
                |                        |             |
                |                        | A           |
                |                        | |           |
                |                        +-+           |
                |                             A        |
                |                        A    |        |
                |                        |    |        |
                |                        +----+        |
                |                                      |
                |                             A        |
                |                             |        |
                |                        A    |        |
                |                        |    |        |
                |                        +----+        |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VHVU - arrow-plain
        // -----------------------------------------------
        canvas.drawLine(25, 2, 26, 2, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(25, 6, 27, 6, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(26, 10, 25, 10, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(27, 14, 25, 14, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(25, 18, 27, 20, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(25, 24, 27, 26, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(25, 30, 30, 29, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(25, 36, 30, 34, DefaultConnectorEndType.BOTTOM_ARROW, DefaultConnectorEndType.BOTTOM_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                        A|            |
                |                        ||            |
                |                        ++            |
                |                                      |
                |                        A |           |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                        |A            |
                |                        ||            |
                |                        ++            |
                |                                      |
                |                        | A           |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                        A             |
                |                        |             |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                                      |
                |                        A             |
                |                        |             |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                             |        |
                |                        A    |        |
                |                        |    |        |
                |                        +----+        |
                |                                      |
                |                             |        |
                |                             |        |
                |                        A    |        |
                |                        |    |        |
                |                        +----+        |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VHVU - plain-arrow
        // -----------------------------------------------
        canvas.drawLine(25, 2, 26, 2, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(25, 6, 27, 6, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(26, 10, 25, 10, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(27, 14, 25, 14, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(25, 18, 27, 20, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(25, 24, 27, 26, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(25, 30, 30, 29, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);
        canvas.drawLine(25, 36, 30, 34, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                        |A            |
                |                        ||            |
                |                        ++            |
                |                                      |
                |                        | A           |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                        A|            |
                |                        ||            |
                |                        ++            |
                |                                      |
                |                        A |           |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                        |             |
                |                        |             |
                |                        | A           |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                        |             |
                |                        |             |
                |                        | A           |
                |                        | |           |
                |                        +-+           |
                |                             A        |
                |                        |    |        |
                |                        +----+        |
                |                                      |
                |                                      |
                |                             A        |
                |                             |        |
                |                        |    |        |
                |                        +----+        |
                |                                      |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VHVU - plain-plain
        // -----------------------------------------------
        canvas.drawLine(25, 2, 26, 2, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(25, 6, 27, 6, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(26, 10, 25, 10, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(27, 14, 25, 14, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(25, 18, 27, 20, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(25, 24, 27, 26, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(25, 30, 30, 29, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);
        canvas.drawLine(25, 36, 30, 34, DefaultConnectorEndType.BOTTOM_PLAIN, DefaultConnectorEndType.BOTTOM_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                        ||            |
                |                        ++            |
                |                                      |
                |                                      |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                                      |
                |                        ||            |
                |                        ++            |
                |                                      |
                |                                      |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                                      |
                |                        |             |
                |                        |             |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                                      |
                |                        |             |
                |                        |             |
                |                        | |           |
                |                        +-+           |
                |                                      |
                |                             |        |
                |                        |    |        |
                |                        +----+        |
                |                                      |
                |                                      |
                |                             |        |
                |                             |        |
                |                        |    |        |
                |                        +----+        |
                |                                      |
                +--------------------------------------+""", canvas.export());

    }

    @Test
    void testConnectorVHVUT() {

        TextCanvas canvas = new TextCanvas(40, 40);
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VHVUT - arrow-arrow
        // -----------------------------------------------
        canvas.drawLine(25, 4, 26, 4, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(25, 8, 27, 8, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(26, 12, 25, 12, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(27, 16, 25, 16, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(25, 20, 27, 22, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(25, 26, 27, 28, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(25, 32, 30, 31, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(25, 38, 30, 36, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                        ++            |
                |                        ||            |
                |                        VV            |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                        V V           |
                |                                      |
                |                        ++            |
                |                        ||            |
                |                        VV            |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                        V V           |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                        V |           |
                |                          |           |
                |                          V           |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                        V |           |
                |                          |           |
                |                          V           |
                |                        +----+        |
                |                        |    |        |
                |                        |    V        |
                |                        V             |
                |                                      |
                |                        +----+        |
                |                        |    |        |
                |                        |    V        |
                |                        |             |
                |                        V             |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VHVUT - arrow-plain
        // -----------------------------------------------
        canvas.drawLine(25, 4, 26, 4, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(25, 8, 27, 8, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(26, 12, 25, 12, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(27, 16, 25, 16, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(25, 20, 27, 22, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(25, 26, 27, 28, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(25, 32, 30, 31, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(25, 38, 30, 36, DefaultConnectorEndType.TOP_ARROW, DefaultConnectorEndType.TOP_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                        ++            |
                |                        ||            |
                |                        V|            |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                        V |           |
                |                                      |
                |                        ++            |
                |                        ||            |
                |                        |V            |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                        | V           |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                        V |           |
                |                          |           |
                |                          |           |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                        V |           |
                |                          |           |
                |                          |           |
                |                                      |
                |                        +----+        |
                |                        |    |        |
                |                        V             |
                |                                      |
                |                                      |
                |                        +----+        |
                |                        |    |        |
                |                        |             |
                |                        V             |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VHVUT - plain-arrow
        // -----------------------------------------------
        canvas.drawLine(25, 4, 26, 4, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(25, 8, 27, 8, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(26, 12, 25, 12, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(27, 16, 25, 16, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(25, 20, 27, 22, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(25, 26, 27, 28, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(25, 32, 30, 31, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_ARROW);
        canvas.drawLine(25, 38, 30, 36, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_ARROW);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                        ++            |
                |                        ||            |
                |                        |V            |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                        | V           |
                |                                      |
                |                        ++            |
                |                        ||            |
                |                        V|            |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                        V |           |
                |                                      |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                          |           |
                |                          V           |
                |                                      |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                          |           |
                |                          V           |
                |                        +----+        |
                |                        |    |        |
                |                        |    V        |
                |                        |             |
                |                                      |
                |                        +----+        |
                |                        |    |        |
                |                        |    V        |
                |                        |             |
                |                        |             |
                +--------------------------------------+""", canvas.export());

        canvas.clear();
        canvas.drawBox(DefaultBoxStyle.THIN, 40, 40);

        // VHVUT - plain-plain
        // -----------------------------------------------
        canvas.drawLine(25, 4, 26, 4, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(25, 8, 27, 8, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(26, 12, 25, 12, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(27, 16, 25, 16, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(25, 20, 27, 22, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(25, 26, 27, 28, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(25, 32, 30, 31, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_PLAIN);
        canvas.drawLine(25, 38, 30, 36, DefaultConnectorEndType.TOP_PLAIN, DefaultConnectorEndType.TOP_PLAIN);

        assertEquals("""
                +--------------------------------------+
                |                                      |
                |                                      |
                |                        ++            |
                |                        ||            |
                |                                      |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                                      |
                |                                      |
                |                        ++            |
                |                        ||            |
                |                                      |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                                      |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                          |           |
                |                          |           |
                |                                      |
                |                                      |
                |                        +-+           |
                |                        | |           |
                |                          |           |
                |                          |           |
                |                                      |
                |                        +----+        |
                |                        |    |        |
                |                        |             |
                |                                      |
                |                                      |
                |                        +----+        |
                |                        |    |        |
                |                        |             |
                |                        |             |
                +--------------------------------------+""", canvas.export());

    }

}
