//@formatter:off
/*
 * TreePrinterTest
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
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.calamanari.tcanv.BoxConnectionPoint;
import de.calamanari.tcanv.BoxStyle;
import de.calamanari.tcanv.DefaultBoxStyle;
import de.calamanari.tcanv.FrameConfig;
import de.calamanari.tcanv.TextCanvas;

import static de.calamanari.tcanv.tp.TreeLayout.BOTTOM_UP;
import static de.calamanari.tcanv.tp.TreeLayout.INDEX;
import static de.calamanari.tcanv.tp.TreeLayout.INDEX_SLIM;
import static de.calamanari.tcanv.tp.TreeLayout.INDEX_WIDE;
import static de.calamanari.tcanv.tp.TreeLayout.LEFT_TO_RIGHT;
import static de.calamanari.tcanv.tp.TreeLayout.RIGHT_TO_LEFT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class TreePrinterTest {

    static final Logger LOGGER = LoggerFactory.getLogger(TreePrinterTest.class);

    @Test
    void testTopDown() {

        TestTreeNode<?> root = setupTestTree(SimpleTreeNode.class);

        assertEquals(
                """
                        +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
                        |                                                                                                +----+                                                                                                 |
                        |                                                                                                |root|                                                                                                 |
                        |                                                                                                +----+                                                                                                 |
                        |                                                                                                   |                                                                                                   |
                        |                            +----------------------------------------------------------------------+----------------------------+                                                                      |
                        |                            |                                                                                                   |                                                                      |
                        |                           +-+                                                                                              +------+                                                                   |
                        |                           |I|                                                                                              |innerF|                                                                   |
                        |                           +-+                                                                                              +------+                                                                   |
                        |                            |                                                                                                   |                                                                      |
                        |                 +----------+---------------+                                                           +-----------------------+-------------------------------------------+                          |
                        |                 |                          |                                                           |                                                                   |                          |
                        |             +------+                   +------+                                                    +------+                                                            +------+                       |
                        |             |innerG|                   |innerH|                                                    |innerD|                                                            |innerE|                       |
                        |             +------+                   +------+                                                    +------+                                                            +------+                       |
                        |                 |                          |                                                           |                                                                   |                          |
                        |    +----------+-+-------+              +---+-+                           +-----------------------+-----+-----------------------+                         +------+-------+--+----------+               |
                        |    |          |         |              |     |                           |                       |                             |                         |      |       |             |               |
                        |  +--+      +-----+   +----+           +-+  +--+                      +------+                +------+                      +------+                    +--+   +--+    +--+     +-------------+        |
                        |  |L3|      | L4  |   | L5 |           |L|  |L2|                      |innerA|                |innerB|                      |innerC|                    |L6|   |L7|    |L8|     |L9 Long Label|        |
                        |  +--+      |line1|   |sub1|           +-+  +--+                      +------+                +------+                      +------+                    +--+   +--+    +--+     +-------------+        |
                        |            |line2|   +----+                                              |                       |                             |                                                                      |
                        |            +-----+                                                       |                       |                             |                                                                      |
                        |                                                                    +-----+-+                 +---+----+             +---------++--------+                                                             |
                        |                                                                    |       |                 |        |             |         |         |                                                             |
                        |                                                                    |       |                 |        |             |         |         |                                                             |
                        |                                                                  +---+   +---+             +---+    +---+         +---+     +---+   +------+                                                          |
                        |                                                                  |L10|   |L11|             |L12|    |L13|         |L14|     |L15|   |innerQ|                                                          |
                        |                                                                  +---+   +---+             +---+    +---+         +---+     +---+   +------+                                                          |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                      +---+                                                            |
                        |                                                                                                                                      |L16|                                                            |
                        |                                                                                                                                      +---+                                                            |
                        +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+""",
                new TreePrinter().print(root).export());

        root = setupTestTree(SpecialBoxStyleTreeNode.class);

        assertEquals(
                """
                        +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
                        |                                                                                                ######                                                                                                 |
                        |                                                                                                #root#                                                                                                 |
                        |                                                                                                ######                                                                                                 |
                        |                                                                                                   |                                                                                                   |
                        |                            +----------------------------------------------------------------------+----------------------------+                                                                      |
                        |                            |                                                                                                   |                                                                      |
                        |                           +-+                                                                                              +------+                                                                   |
                        |                           |I|                                                                                              |innerF|                                                                   |
                        |                           +-+                                                                                              +------+                                                                   |
                        |                            |                                                                                                   |                                                                      |
                        |                 +----------+---------------+                                                           +-----------------------+-------------------------------------------+                          |
                        |                 |                          |                                                           |                                                                   |                          |
                        |             +------+                   +------+                                                    +------+                                                            +------+                       |
                        |             |innerG|                   |innerH|                                                    |innerD|                                                            |innerE|                       |
                        |             +------+                   +------+                                                    +------+                                                            +------+                       |
                        |                 |                          |                                                           |                                                                   |                          |
                        |    +----------+-+-------+              +---+-+                           +-----------------------+-----+-----------------------+                         +------+-------+--+----------+               |
                        |    |          |         |              |     |                           |                       |                             |                         |      |       |             |               |
                        |  ====      =======   ======           ===  ====                      +------+                +------+                      +------+                    ====   ====    ====     ===============        |
                        |  |L3|      | L4  |   | L5 |           |L|  |L2|                      |innerA|                |innerB|                      |innerC|                    |L6|   |L7|    |L8|     |L9 Long Label|        |
                        |  ====      |line1|   |sub1|           ===  ====                      +------+                +------+                      +------+                    ====   ====    ====     ===============        |
                        |            |line2|   ======                                              |                       |                             |                                                                      |
                        |            =======                                                       |                       |                             |                                                                      |
                        |                                                                    +-----+-+                 +---+----+             +---------++--------+                                                             |
                        |                                                                    |       |                 |        |             |         |         |                                                             |
                        |                                                                    |       |                 |        |             |         |         |                                                             |
                        |                                                                  =====   =====             =====    =====         =====     =====   +------+                                                          |
                        |                                                                  |L10|   |L11|             |L12|    |L13|         |L14|     |L15|   |innerQ|                                                          |
                        |                                                                  =====   =====             =====    =====         =====     =====   +------+                                                          |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                      =====                                                            |
                        |                                                                                                                                      |L16|                                                            |
                        |                                                                                                                                      =====                                                            |
                        +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+""",
                new TreePrinter().print(root).export());

        root = setupTestTree(DecoratedTreeNode.class);

        assertEquals(
                """
                        +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
                        |                                                                                                X----+                                                                                                 |
                        |                                                                                                |root|                                                                                                 |
                        |                                                                                                +----+                                                                                                 |
                        |                                                                                                   |                                                                                                   |
                        |                            +----------------------------------------------------------------------+----------------------------+                                                                      |
                        |                            V                                                                                                   V                                                                      |
                        |                           X-+                                                                                              X------+                                                                   |
                        |                           |I|                                                                                              |innerF|                                                                   |
                        |                           +-+                                                                                              +------+                                                                   |
                        |                            |                                                                                                   |                                                                      |
                        |                 +----------+---------------+                                                           +-----------------------+-------------------------------------------+                          |
                        |                 V                          V                                                           V                                                                   V                          |
                        |             X------+                   X------+                                                    X------+                                                            X------+                       |
                        |             |innerG|                   |innerH|                                                    |innerD|                                                            |innerE|                       |
                        |             +------+                   +------+                                                    +------+                                                            +------+                       |
                        |                 |                          |                                                           |                                                                   |                          |
                        |    +----------+-+-------+              +---+-+                           +-----------------------+-----+-----------------------+                         +------+-------+--+----------+               |
                        |    V          V         V              V     V                           V                       V                             V                         V      V       V             V               |
                        |  X--+      X-----+   X----+           X-+  X--+                      X------+                X------+                      X------+                    X--+   X--+    X--+     X-------------+        |
                        |  |L3|      | L4  |   | L5 |           |L|  |L2|                      |innerA|                |innerB|                      |innerC|                    |L6|   |L7|    |L8|     |L9 Long Label|        |
                        |  +--+      |line1|   |sub1|           +-+  +--+                      +------+                +------+                      +------+                    +--+   +--+    +--+     +-------------+        |
                        |            |line2|   +----+                                              |                       |                             |                                                                      |
                        |            +-----+                                                       |                       |                             |                                                                      |
                        |                                                                    +-----+-+                 +---+----+             +---------++--------+                                                             |
                        |                                                                    |       |                 |        |             |         |         |                                                             |
                        |                                                                    V       V                 V        V             V         V         V                                                             |
                        |                                                                  X---+   X---+             X---+    X---+         X---+     X---+   X------+                                                          |
                        |                                                                  |L10|   |L11|             |L12|    |L13|         |L14|     |L15|   |innerQ|                                                          |
                        |                                                                  +---+   +---+             +---+    +---+         +---+     +---+   +------+                                                          |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                        V                                                              |
                        |                                                                                                                                      X---+                                                            |
                        |                                                                                                                                      |L16|                                                            |
                        |                                                                                                                                      +---+                                                            |
                        +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+""",
                new TreePrinter().print(root).export());

        root = setupTestTree(SimpleTreeNode.class);

        assertEquals(
                """
                        +-----------------------------------------------------------------------------------------------------------------------------------------------------+
                        |                                                                       +----+                                                                        |
                        |                                                                       |root|                                                                        |
                        |                                                                       +----+                                                                        |
                        |                                                                          |                                                                          |
                        |                            +---------------------------------------------+----------------------------+                                             |
                        |                            |                                                                          |                                             |
                        |                           +-+                                                                     +------+                                          |
                        |                           |I|                                                                     |innerF|                                          |
                        |                           +-+                                                                     +------+                                          |
                        |                            |                                                                          |                                             |
                        |                 +----------+---------------+                                  +-----------------------+------------------+                          |
                        |                 |                          |                                  |                                          |                          |
                        |             +------+                   +------+                           +------+                                   +------+                       |
                        |             |innerG|                   |innerH|                           |innerD|                                   |innerE|                       |
                        |             +------+                   +------+                           +------+                                   +------+                       |
                        |                 |                          |                                  |                                          |                          |
                        |    +----------+-+-------+              +---+-+                    +----------++----------+             +------+-------+--+----------+               |
                        |    |          |         |              |     |                    |          |           |             |      |       |             |               |
                        |  +--+      +-----+   +----+           +-+  +--+               +------+   +------+    +------+        +--+   +--+    +--+     +-------------+        |
                        |  |L3|      | L4  |   | L5 |           |L|  |L2|               |innerA|   |innerB|    |innerC|        |L6|   |L7|    |L8|     |L9 Long Label|        |
                        |  +--+      |line1|   |sub1|           +-+  +--+               +------+   +------+    +------+        +--+   +--+    +--+     +-------------+        |
                        |            |line2|   +----+                                       |          |           |                                                          |
                        |            +-----+                                               ...        ...         ...                                                         |
                        +-----------------------------------------------------------------------------------------------------------------------------------------------------+""",
                new TreePrinter().print(root, 4).export());

        assertEquals("""
                +----------------------------------------------------------+
                |                          +----+                          |
                |                          |root|                          |
                |                          +----+                          |
                |                             |                            |
                |              +--------------+-------------+              |
                |              |                            |              |
                |             +-+                       +------+           |
                |             |I|                       |innerF|           |
                |             +-+                       +------+           |
                |              |                            |              |
                |      +-------+----+                 +-----+----+         |
                |      |            |                 |          |         |
                |  +------+     +------+          +------+   +------+      |
                |  |innerG|     |innerH|          |innerD|   |innerE|      |
                |  +------+     +------+          +------+   +------+      |
                |      |            |                 |          |         |
                |     ...          ...               ...        ...        |
                +----------------------------------------------------------+""", new TreePrinter().print(root, 3).export());

        assertEquals("""
                +--------+
                | +----+ |
                | |root| |
                | +----+ |
                |    |   |
                |   ...  |
                +--------+""", new TreePrinter().print(root, 1).export());

        assertEquals("""
                +-----+
                | ... |
                +-----+""", new TreePrinter().print(root, 0).export());

        assertEquals("""
                +---------------------+
                |                     |
                |                     |
                |                     |
                |                     |
                |         ...         |
                |                     |
                |                     |
                |                     |
                |                     |
                +---------------------+""",
                new TreePrinter(new VerticalTreeDrawingPolicy(FrameConfig.frame10x5(), TreeLayoutConfig.getDefault(), false)).print(root, 0).export());

        assertEquals("""
                +--------+
                | <null> |
                +--------+""", new TreePrinter().print(null, 0).export());

        assertEquals("""
                +---+
                |   |
                +---+""", new TreePrinter().print(SimpleTreeNode.MISSING_SIBLING).export());

        assertEquals("""
                +-------------------+
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                +-------------------+""", new TreePrinter(new VerticalTreeDrawingPolicy(FrameConfig.frame10x5(), TreeLayoutConfig.getDefault(), false))
                .print(SimpleTreeNode.MISSING_SIBLING).export());

    }

    @Test
    void testBottomUp() {

        TestTreeNode<?> root = setupTestTree(SimpleTreeNode.class);

        assertEquals(
                """
                        +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
                        |                                                                                                                                      +---+                                                            |
                        |                                                                                                                                      |L16|                                                            |
                        |                                                                                                                                      +---+                                                            |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                        |                                                              |
                        |                                                                  +---+   +---+             +---+    +---+         +---+     +---+   +------+                                                          |
                        |                                                                  |L10|   |L11|             |L12|    |L13|         |L14|     |L15|   |innerQ|                                                          |
                        |                                                                  +---+   +---+             +---+    +---+         +---+     +---+   +------+                                                          |
                        |                                                                    |       |                 |        |             |         |         |                                                             |
                        |                                                                    |       |                 |        |             |         |         |                                                             |
                        |                                                                    +-----+-+                 +---+----+             +---------++--------+                                                             |
                        |            +-----+                                                       |                       |                             |                                                                      |
                        |            | L4  |   +----+                                              |                       |                             |                                                                      |
                        |  +--+      |line1|   | L5 |           +-+  +--+                      +------+                +------+                      +------+                    +--+   +--+    +--+     +-------------+        |
                        |  |L3|      |line2|   |sub1|           |L|  |L2|                      |innerA|                |innerB|                      |innerC|                    |L6|   |L7|    |L8|     |L9 Long Label|        |
                        |  +--+      +-----+   +----+           +-+  +--+                      +------+                +------+                      +------+                    +--+   +--+    +--+     +-------------+        |
                        |    |          |         |              |     |                           |                       |                             |                         |      |       |             |               |
                        |    +----------+-+-------+              +---+-+                           +-----------------------+-----+-----------------------+                         +------+-------+--+----------+               |
                        |                 |                          |                                                           |                                                                   |                          |
                        |             +------+                   +------+                                                    +------+                                                            +------+                       |
                        |             |innerG|                   |innerH|                                                    |innerD|                                                            |innerE|                       |
                        |             +------+                   +------+                                                    +------+                                                            +------+                       |
                        |                 |                          |                                                           |                                                                   |                          |
                        |                 +----------+---------------+                                                           +-----------------------+-------------------------------------------+                          |
                        |                            |                                                                                                   |                                                                      |
                        |                           +-+                                                                                              +------+                                                                   |
                        |                           |I|                                                                                              |innerF|                                                                   |
                        |                           +-+                                                                                              +------+                                                                   |
                        |                            |                                                                                                   |                                                                      |
                        |                            +----------------------------------------------------------------------+----------------------------+                                                                      |
                        |                                                                                                   |                                                                                                   |
                        |                                                                                                +----+                                                                                                 |
                        |                                                                                                |root|                                                                                                 |
                        |                                                                                                +----+                                                                                                 |
                        +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+""",
                new TreePrinter(BOTTOM_UP).print(root).export());

        root = setupTestTree(SpecialBoxStyleTreeNode.class);

        assertEquals(
                """
                        +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
                        |                                                                                                                                      =====                                                            |
                        |                                                                                                                                      |L16|                                                            |
                        |                                                                                                                                      =====                                                            |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                        |                                                              |
                        |                                                                  =====   =====             =====    =====         =====     =====   +------+                                                          |
                        |                                                                  |L10|   |L11|             |L12|    |L13|         |L14|     |L15|   |innerQ|                                                          |
                        |                                                                  =====   =====             =====    =====         =====     =====   +------+                                                          |
                        |                                                                    |       |                 |        |             |         |         |                                                             |
                        |                                                                    |       |                 |        |             |         |         |                                                             |
                        |                                                                    +-----+-+                 +---+----+             +---------++--------+                                                             |
                        |            =======                                                       |                       |                             |                                                                      |
                        |            | L4  |   ======                                              |                       |                             |                                                                      |
                        |  ====      |line1|   | L5 |           ===  ====                      +------+                +------+                      +------+                    ====   ====    ====     ===============        |
                        |  |L3|      |line2|   |sub1|           |L|  |L2|                      |innerA|                |innerB|                      |innerC|                    |L6|   |L7|    |L8|     |L9 Long Label|        |
                        |  ====      =======   ======           ===  ====                      +------+                +------+                      +------+                    ====   ====    ====     ===============        |
                        |    |          |         |              |     |                           |                       |                             |                         |      |       |             |               |
                        |    +----------+-+-------+              +---+-+                           +-----------------------+-----+-----------------------+                         +------+-------+--+----------+               |
                        |                 |                          |                                                           |                                                                   |                          |
                        |             +------+                   +------+                                                    +------+                                                            +------+                       |
                        |             |innerG|                   |innerH|                                                    |innerD|                                                            |innerE|                       |
                        |             +------+                   +------+                                                    +------+                                                            +------+                       |
                        |                 |                          |                                                           |                                                                   |                          |
                        |                 +----------+---------------+                                                           +-----------------------+-------------------------------------------+                          |
                        |                            |                                                                                                   |                                                                      |
                        |                           +-+                                                                                              +------+                                                                   |
                        |                           |I|                                                                                              |innerF|                                                                   |
                        |                           +-+                                                                                              +------+                                                                   |
                        |                            |                                                                                                   |                                                                      |
                        |                            +----------------------------------------------------------------------+----------------------------+                                                                      |
                        |                                                                                                   |                                                                                                   |
                        |                                                                                                ######                                                                                                 |
                        |                                                                                                #root#                                                                                                 |
                        |                                                                                                ######                                                                                                 |
                        +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+""",
                new TreePrinter(BOTTOM_UP).print(root).export());

        root = setupTestTree(DecoratedTreeNode.class);

        assertEquals(
                """
                        +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
                        |                                                                                                                                      X---+                                                            |
                        |                                                                                                                                      |L16|                                                            |
                        |                                                                                                                                      +---+                                                            |
                        |                                                                                                                                       0|                                                              |
                        |                                                                                                                                        |                                                              |
                        |                                                                                                                                        |                                                              |
                        |                                                                  X---+   X---+             X---+    X---+         X---+     X---+   X------+                                                          |
                        |                                                                  |L10|   |L11|             |L12|    |L13|         |L14|     |L15|   |innerQ|                                                          |
                        |                                                                  +---+   +---+             +---+    +---+         +---+     +---+   +------+                                                          |
                        |                                                                   0|      1|                1|       2|            0|        2|        3|                                                             |
                        |                                                                    |       |                 |        |             |         |         |                                                             |
                        |                                                                    +-----+-+                 +---+----+             +---------++--------+                                                             |
                        |            X-----+                                                       |                       |                             |                                                                      |
                        |            | L4  |   X----+                                              |                       |                             |                                                                      |
                        |  X--+      |line1|   | L5 |           X-+  X--+                      X------+                X------+                      X------+                    X--+   X--+    X--+     X-------------+        |
                        |  |L3|      |line2|   |sub1|           |L|  |L2|                      |innerA|                |innerB|                      |innerC|                    |L6|   |L7|    |L8|     |L9 Long Label|        |
                        |  +--+      +-----+   +----+           +-+  +--+                      +------+                +------+                      +------+                    +--+   +--+    +--+     +-------------+        |
                        |   0|         1|        2|             0|    1|                          0|                      1|                            2|                        0|     1|      2|            3|               |
                        |    +----------+-+-------+              +---+-+                           +-----------------------+-----+-----------------------+                         +------+-------+--+----------+               |
                        |                 |                          |                                                           |                                                                   |                          |
                        |             X------+                   X------+                                                    X------+                                                            X------+                       |
                        |             |innerG|                   |innerH|                                                    |innerD|                                                            |innerE|                       |
                        |             +------+                   +------+                                                    +------+                                                            +------+                       |
                        |                0|                         1|                                                          0|                                                                  1|                          |
                        |                 +----------+---------------+                                                           +-----------------------+-------------------------------------------+                          |
                        |                            |                                                                                                   |                                                                      |
                        |                           X-+                                                                                              X------+                                                                   |
                        |                           |I|                                                                                              |innerF|                                                                   |
                        |                           +-+                                                                                              +------+                                                                   |
                        |                           0|                                                                                                  1|                                                                      |
                        |                            +----------------------------------------------------------------------+----------------------------+                                                                      |
                        |                                                                                                   |                                                                                                   |
                        |                                                                                                X----+                                                                                                 |
                        |                                                                                                |root|                                                                                                 |
                        |                                                                                                +----+                                                                                                 |
                        +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+""",
                new TreePrinter(BOTTOM_UP).print(root).export());

        root = setupTestTree(SimpleTreeNode.class);

        assertEquals(
                """
                        +-----------------------------------------------------------------------------------------------------------------------------------------------------+
                        |            +-----+                                               ...        ...         ...                                                         |
                        |            | L4  |   +----+                                       |          |           |                                                          |
                        |  +--+      |line1|   | L5 |           +-+  +--+               +------+   +------+    +------+        +--+   +--+    +--+     +-------------+        |
                        |  |L3|      |line2|   |sub1|           |L|  |L2|               |innerA|   |innerB|    |innerC|        |L6|   |L7|    |L8|     |L9 Long Label|        |
                        |  +--+      +-----+   +----+           +-+  +--+               +------+   +------+    +------+        +--+   +--+    +--+     +-------------+        |
                        |    |          |         |              |     |                    |          |           |             |      |       |             |               |
                        |    +----------+-+-------+              +---+-+                    +----------++----------+             +------+-------+--+----------+               |
                        |                 |                          |                                  |                                          |                          |
                        |             +------+                   +------+                           +------+                                   +------+                       |
                        |             |innerG|                   |innerH|                           |innerD|                                   |innerE|                       |
                        |             +------+                   +------+                           +------+                                   +------+                       |
                        |                 |                          |                                  |                                          |                          |
                        |                 +----------+---------------+                                  +-----------------------+------------------+                          |
                        |                            |                                                                          |                                             |
                        |                           +-+                                                                     +------+                                          |
                        |                           |I|                                                                     |innerF|                                          |
                        |                           +-+                                                                     +------+                                          |
                        |                            |                                                                          |                                             |
                        |                            +---------------------------------------------+----------------------------+                                             |
                        |                                                                          |                                                                          |
                        |                                                                       +----+                                                                        |
                        |                                                                       |root|                                                                        |
                        |                                                                       +----+                                                                        |
                        +-----------------------------------------------------------------------------------------------------------------------------------------------------+""",
                new TreePrinter(BOTTOM_UP).print(root, 4).export());

        assertEquals("""
                +----------------------------------------------------------+
                |     ...          ...               ...        ...        |
                |      |            |                 |          |         |
                |  +------+     +------+          +------+   +------+      |
                |  |innerG|     |innerH|          |innerD|   |innerE|      |
                |  +------+     +------+          +------+   +------+      |
                |      |            |                 |          |         |
                |      +-------+----+                 +-----+----+         |
                |              |                            |              |
                |             +-+                       +------+           |
                |             |I|                       |innerF|           |
                |             +-+                       +------+           |
                |              |                            |              |
                |              +--------------+-------------+              |
                |                             |                            |
                |                          +----+                          |
                |                          |root|                          |
                |                          +----+                          |
                +----------------------------------------------------------+""", new TreePrinter(BOTTOM_UP).print(root, 3).export());

        assertEquals("""
                +--------+
                |   ...  |
                |    |   |
                | +----+ |
                | |root| |
                | +----+ |
                +--------+""", new TreePrinter(BOTTOM_UP).print(root, 1).export());

        assertEquals("""
                +-----+
                | ... |
                +-----+""", new TreePrinter(BOTTOM_UP).print(root, 0).export());

        assertEquals("""
                +---------------------+
                |                     |
                |                     |
                |                     |
                |                     |
                |         ...         |
                |                     |
                |                     |
                |                     |
                |                     |
                +---------------------+""",
                new TreePrinter(new VerticalTreeDrawingPolicy(FrameConfig.frame10x5(), TreeLayoutConfig.getDefault(), true)).print(root, 0).export());

        assertEquals("""
                +--------+
                | <null> |
                +--------+""", new TreePrinter(BOTTOM_UP).print(null, 0).export());

        assertEquals("""
                +---+
                |   |
                +---+""", new TreePrinter(BOTTOM_UP).print(SimpleTreeNode.MISSING_SIBLING).export());

        assertEquals("""
                +-------------------+
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                +-------------------+""", new TreePrinter(new VerticalTreeDrawingPolicy(FrameConfig.frame10x5(), TreeLayoutConfig.getDefault(), true))
                .print(SimpleTreeNode.MISSING_SIBLING).export());

    }

    @Test
    void testLeftToRight() {

        TestTreeNode<?> root = setupTestTree(SimpleTreeNode.class);

        assertEquals("""
                +-------------------------------------------------------------------+
                |                                                                   |
                |                                +--+                               |
                |                              +-|L3|                               |
                |                              | +--+                               |
                |                              |                                    |
                |                              |                                    |
                |                              |                                    |
                |                              | +-----+                            |
                |                              | | L4  |                            |
                |                     +------+ +-|line1|                            |
                |                 +---|innerG|-+ |line2|                            |
                |                 |   +------+ | +-----+                            |
                |                 |            |                                    |
                |                 |            |                                    |
                |                 |            | +----+                             |
                |                 |            | | L5 |                             |
                |          +-+    |            +-|sub1|                             |
                |        +-|I|----+              +----+                             |
                |        | +-+    |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |              +-+                                |
                |        |        |            +-|L|                                |
                |        |        |   +------+ | +-+                                |
                |        |        +---|innerH|-+                                    |
                |        |            +------+ |                                    |
                |        |                     | +--+                               |
                |        |                     +-|L2|                               |
                |        |                       +--+                               |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                         +---+            |
                |        |                                    +----|L10|            |
                |        |                                    |    +---+            |
                |        |                       +------+     |                     |
                |        |                     +-|innerA|-----+                     |
                |        |                     | +------+     |    +---+            |
                |        |                     |              +----|L11|            |
                |        |                     |                   +---+            |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                | +----+ |                     |                                    |
                | |root|-+                     |                                    |
                | +----+ |                     |                                    |
                |        |                     |                   +---+            |
                |        |                     | +------+     +----|L12|            |
                |        |                     +-|innerB|-----+    +---+            |
                |        |                     | +------+     |                     |
                |        |            +------+ |              |                     |
                |        |          +-|innerD|-+              |                     |
                |        |          | +------+ |              |    +---+            |
                |        |          |          |              +----|L13|            |
                |        |          |          |                   +---+            |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                   +---+            |
                |        | +------+ |          |              +----|L14|            |
                |        +-|innerF|-+          |              |    +---+            |
                |          +------+ |          |              |                     |
                |                   |          |              |                     |
                |                   |          |              |                     |
                |                   |          | +------+     |                     |
                |                   |          +-|innerC|-----+    +---+            |
                |                   |            +------+     +----|L15|            |
                |                   |                         |    +---+            |
                |                   |                         |                     |
                |                   |                         |                     |
                |                   |                         |                     |
                |                   |                         |    +------+   +---+ |
                |                   |                         +----|innerQ|---|L16| |
                |                   |                              +------+   +---+ |
                |                   |                                               |
                |                   |                                               |
                |                   |                                               |
                |                   |                                               |
                |                   |                                               |
                |                   |                                               |
                |                   |            +--+                               |
                |                   |          +-|L6|                               |
                |                   |          | +--+                               |
                |                   |          |                                    |
                |                   |          |                                    |
                |                   |          | +--+                               |
                |                   |          +-|L7|                               |
                |                   |          | +--+                               |
                |                   | +------+ |                                    |
                |                   +-|innerE|-+                                    |
                |                     +------+ |                                    |
                |                              | +--+                               |
                |                              +-|L8|                               |
                |                              | +--+                               |
                |                              |                                    |
                |                              |                                    |
                |                              |                                    |
                |                              | +-------------+                    |
                |                              +-|L9 Long Label|                    |
                |                                +-------------+                    |
                |                                                                   |
                |                                                                   |
                +-------------------------------------------------------------------+""", new TreePrinter(LEFT_TO_RIGHT).print(root).export());

        root = setupTestTree(SpecialBoxStyleTreeNode.class);

        assertEquals("""
                +-------------------------------------------------------------------+
                |                                                                   |
                |                                ====                               |
                |                              +-|L3|                               |
                |                              | ====                               |
                |                              |                                    |
                |                              |                                    |
                |                              |                                    |
                |                              | =======                            |
                |                              | | L4  |                            |
                |                     +------+ +-|line1|                            |
                |                 +---|innerG|-+ |line2|                            |
                |                 |   +------+ | =======                            |
                |                 |            |                                    |
                |                 |            |                                    |
                |                 |            | ======                             |
                |                 |            | | L5 |                             |
                |          +-+    |            +-|sub1|                             |
                |        +-|I|----+              ======                             |
                |        | +-+    |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |              ===                                |
                |        |        |            +-|L|                                |
                |        |        |   +------+ | ===                                |
                |        |        +---|innerH|-+                                    |
                |        |            +------+ |                                    |
                |        |                     | ====                               |
                |        |                     +-|L2|                               |
                |        |                       ====                               |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                         =====            |
                |        |                                    +----|L10|            |
                |        |                                    |    =====            |
                |        |                       +------+     |                     |
                |        |                     +-|innerA|-----+                     |
                |        |                     | +------+     |    =====            |
                |        |                     |              +----|L11|            |
                |        |                     |                   =====            |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                | ###### |                     |                                    |
                | #root#-+                     |                                    |
                | ###### |                     |                                    |
                |        |                     |                   =====            |
                |        |                     | +------+     +----|L12|            |
                |        |                     +-|innerB|-----+    =====            |
                |        |                     | +------+     |                     |
                |        |            +------+ |              |                     |
                |        |          +-|innerD|-+              |                     |
                |        |          | +------+ |              |    =====            |
                |        |          |          |              +----|L13|            |
                |        |          |          |                   =====            |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                   =====            |
                |        | +------+ |          |              +----|L14|            |
                |        +-|innerF|-+          |              |    =====            |
                |          +------+ |          |              |                     |
                |                   |          |              |                     |
                |                   |          |              |                     |
                |                   |          | +------+     |                     |
                |                   |          +-|innerC|-----+    =====            |
                |                   |            +------+     +----|L15|            |
                |                   |                         |    =====            |
                |                   |                         |                     |
                |                   |                         |                     |
                |                   |                         |                     |
                |                   |                         |    +------+   ===== |
                |                   |                         +----|innerQ|---|L16| |
                |                   |                              +------+   ===== |
                |                   |                                               |
                |                   |                                               |
                |                   |                                               |
                |                   |                                               |
                |                   |                                               |
                |                   |                                               |
                |                   |            ====                               |
                |                   |          +-|L6|                               |
                |                   |          | ====                               |
                |                   |          |                                    |
                |                   |          |                                    |
                |                   |          | ====                               |
                |                   |          +-|L7|                               |
                |                   |          | ====                               |
                |                   | +------+ |                                    |
                |                   +-|innerE|-+                                    |
                |                     +------+ |                                    |
                |                              | ====                               |
                |                              +-|L8|                               |
                |                              | ====                               |
                |                              |                                    |
                |                              |                                    |
                |                              |                                    |
                |                              | ===============                    |
                |                              +-|L9 Long Label|                    |
                |                                ===============                    |
                |                                                                   |
                |                                                                   |
                +-------------------------------------------------------------------+""", new TreePrinter(LEFT_TO_RIGHT).print(root).export());

        root = setupTestTree(DecoratedTreeNode.class);

        assertEquals("""
                +-------------------------------------------------------------------+
                |                                                                   |
                |                                X--+                               |
                |                              +>|L3|                               |
                |                              | +--+                               |
                |                              |                                    |
                |                              |                                    |
                |                              |                                    |
                |                              | X-----+                            |
                |                              | | L4  |                            |
                |                     X------+ +>|line1|                            |
                |                 +-->|innerG|-+ |line2|                            |
                |                 |   +------+ | +-----+                            |
                |                 |            |                                    |
                |                 |            |                                    |
                |                 |            | X----+                             |
                |                 |            | | L5 |                             |
                |          X-+    |            +>|sub1|                             |
                |        +>|I|----+              +----+                             |
                |        | +-+    |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |                                                 |
                |        |        |              X-+                                |
                |        |        |            +>|L|                                |
                |        |        |   X------+ | +-+                                |
                |        |        +-->|innerH|-+                                    |
                |        |            +------+ |                                    |
                |        |                     | X--+                               |
                |        |                     +>|L2|                               |
                |        |                       +--+                               |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                                          |
                |        |                                         X---+            |
                |        |                                    +--->|L10|            |
                |        |                                    |    +---+            |
                |        |                       X------+     |                     |
                |        |                     +>|innerA|-----+                     |
                |        |                     | +------+     |    X---+            |
                |        |                     |              +--->|L11|            |
                |        |                     |                   +---+            |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                |        |                     |                                    |
                | X----+ |                     |                                    |
                | |root|-+                     |                                    |
                | +----+ |                     |                                    |
                |        |                     |                   X---+            |
                |        |                     | X------+     +--->|L12|            |
                |        |                     +>|innerB|-----+    +---+            |
                |        |                     | +------+     |                     |
                |        |            X------+ |              |                     |
                |        |          +>|innerD|-+              |                     |
                |        |          | +------+ |              |    X---+            |
                |        |          |          |              +--->|L13|            |
                |        |          |          |                   +---+            |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                                    |
                |        |          |          |                   X---+            |
                |        | X------+ |          |              +--->|L14|            |
                |        +>|innerF|-+          |              |    +---+            |
                |          +------+ |          |              |                     |
                |                   |          |              |                     |
                |                   |          |              |                     |
                |                   |          | X------+     |                     |
                |                   |          +>|innerC|-----+    X---+            |
                |                   |            +------+     +--->|L15|            |
                |                   |                         |    +---+            |
                |                   |                         |                     |
                |                   |                         |                     |
                |                   |                         |                     |
                |                   |                         |    X------+   X---+ |
                |                   |                         +--->|innerQ|-->|L16| |
                |                   |                              +------+   +---+ |
                |                   |                                               |
                |                   |                                               |
                |                   |                                               |
                |                   |                                               |
                |                   |                                               |
                |                   |                                               |
                |                   |            X--+                               |
                |                   |          +>|L6|                               |
                |                   |          | +--+                               |
                |                   |          |                                    |
                |                   |          |                                    |
                |                   |          | X--+                               |
                |                   |          +>|L7|                               |
                |                   |          | +--+                               |
                |                   | X------+ |                                    |
                |                   +>|innerE|-+                                    |
                |                     +------+ |                                    |
                |                              | X--+                               |
                |                              +>|L8|                               |
                |                              | +--+                               |
                |                              |                                    |
                |                              |                                    |
                |                              |                                    |
                |                              | X-------------+                    |
                |                              +>|L9 Long Label|                    |
                |                                +-------------+                    |
                |                                                                   |
                |                                                                   |
                +-------------------------------------------------------------------+""", new TreePrinter(LEFT_TO_RIGHT).print(root).export());

        root = setupTestTree(SimpleTreeNode.class);

        assertEquals("""
                +------------------------------------------------+
                |                                                |
                |                                +--+            |
                |                              +-|L3|            |
                |                              | +--+            |
                |                              |                 |
                |                              |                 |
                |                              |                 |
                |                              | +-----+         |
                |                              | | L4  |         |
                |                     +------+ +-|line1|         |
                |                 +---|innerG|-+ |line2|         |
                |                 |   +------+ | +-----+         |
                |                 |            |                 |
                |                 |            |                 |
                |                 |            | +----+          |
                |                 |            | | L5 |          |
                |          +-+    |            +-|sub1|          |
                |        +-|I|----+              +----+          |
                |        | +-+    |                              |
                |        |        |                              |
                |        |        |                              |
                |        |        |                              |
                |        |        |                              |
                |        |        |                              |
                |        |        |                              |
                |        |        |              +-+             |
                |        |        |            +-|L|             |
                |        |        |   +------+ | +-+             |
                |        |        +---|innerH|-+                 |
                |        |            +------+ |                 |
                |        |                     | +--+            |
                |        |                     +-|L2|            |
                |        |                       +--+            |
                |        |                                       |
                |        |                                       |
                |        |                                       |
                |        |                                       |
                |        |                                       |
                |        |                                       |
                |        |                                       |
                | +----+ |                                       |
                | |root|-+                                       |
                | +----+ |                       +------+ .      |
                |        |                     +-|innerA|-.      |
                |        |                     | +------+ .      |
                |        |                     |                 |
                |        |                     |                 |
                |        |            +------+ | +------+ .      |
                |        |          +-|innerD|-+-|innerB|-.      |
                |        |          | +------+ | +------+ .      |
                |        |          |          |                 |
                |        |          |          |                 |
                |        |          |          |                 |
                |        |          |          | +------+ .      |
                |        |          |          +-|innerC|-.      |
                |        |          |            +------+ .      |
                |        |          |                            |
                |        |          |                            |
                |        |          |                            |
                |        | +------+ |                            |
                |        +-|innerF|-+                            |
                |          +------+ |            +--+            |
                |                   |          +-|L6|            |
                |                   |          | +--+            |
                |                   |          |                 |
                |                   |          |                 |
                |                   |          | +--+            |
                |                   |          +-|L7|            |
                |                   |          | +--+            |
                |                   | +------+ |                 |
                |                   +-|innerE|-+                 |
                |                     +------+ |                 |
                |                              | +--+            |
                |                              +-|L8|            |
                |                              | +--+            |
                |                              |                 |
                |                              |                 |
                |                              |                 |
                |                              | +-------------+ |
                |                              +-|L9 Long Label| |
                |                                +-------------+ |
                |                                                |
                |                                                |
                +------------------------------------------------+""", new TreePrinter(LEFT_TO_RIGHT).print(root, 4).export());

        assertEquals("""
                +--------------------------------+
                |                                |
                |                     +------+ . |
                |                 +---|innerG|-. |
                |                 |   +------+ . |
                |          +-+    |              |
                |        +-|I|----+              |
                |        | +-+    |              |
                |        |        |   +------+ . |
                |        |        +---|innerH|-. |
                |        |            +------+ . |
                |        |                       |
                | +----+ |                       |
                | |root|-+                       |
                | +----+ |                       |
                |        |                       |
                |        |                       |
                |        |            +------+ . |
                |        |          +-|innerD|-. |
                |        | +------+ | +------+ . |
                |        +-|innerF|-+            |
                |          +------+ |            |
                |                   | +------+ . |
                |                   +-|innerE|-. |
                |                     +------+ . |
                |                                |
                +--------------------------------+""", new TreePrinter(LEFT_TO_RIGHT).print(root, 3).export());

        assertEquals("""
                +----------+
                | +----+ . |
                | |root|-. |
                | +----+ . |
                +----------+""", new TreePrinter(LEFT_TO_RIGHT).print(root, 1).export());

        assertEquals("""
                +-----+
                | ... |
                +-----+""", new TreePrinter(LEFT_TO_RIGHT).print(root, 0).export());

        assertEquals("""
                +---------------------+
                |                     |
                |                     |
                |                     |
                |                     |
                |         ...         |
                |                     |
                |                     |
                |                     |
                |                     |
                +---------------------+""",
                new TreePrinter(new HorizontalTreeDrawingPolicy(FrameConfig.frame10x5(), TreeLayoutConfig.getDefault(), false)).print(root, 0).export());

        assertEquals("""
                +--------+
                | <null> |
                +--------+""", new TreePrinter(LEFT_TO_RIGHT).print(null, 0).export());

        assertEquals("""
                +---+
                |   |
                +---+""", new TreePrinter(LEFT_TO_RIGHT).print(SimpleTreeNode.MISSING_SIBLING).export());

        assertEquals("""
                +-------------------+
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                +-------------------+""", new TreePrinter(new HorizontalTreeDrawingPolicy(FrameConfig.frame10x5(), TreeLayoutConfig.getDefault(), false))
                .print(SimpleTreeNode.MISSING_SIBLING).export());

    }

    @Test
    void testRightToLeft() {

        TestTreeNode<?> root = setupTestTree(SimpleTreeNode.class);

        assertEquals("""
                +-------------------------------------------------------------------+
                |                                                                   |
                |                               +--+                                |
                |                               |L3|-+                              |
                |                               +--+ |                              |
                |                                    |                              |
                |                                    |                              |
                |                                    |                              |
                |                            +-----+ |                              |
                |                            | L4  | |                              |
                |                            |line1|-+ +------+                     |
                |                            |line2| +-|innerG|---+                 |
                |                            +-----+ | +------+   |                 |
                |                                    |            |                 |
                |                                    |            |                 |
                |                             +----+ |            |                 |
                |                             | L5 | |            |                 |
                |                             |sub1|-+            |    +-+          |
                |                             +----+              +----|I|-+        |
                |                                                 |    +-+ |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                +-+              |        |        |
                |                                |L|-+            |        |        |
                |                                +-+ | +------+   |        |        |
                |                                    +-|innerH|---+        |        |
                |                                    | +------+            |        |
                |                               +--+ |                     |        |
                |                               |L2|-+                     |        |
                |                               +--+                       |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |            +---+                                         |        |
                |            |L10|----+                                    |        |
                |            +---+    |                                    |        |
                |                     |     +------+                       |        |
                |                     +-----|innerA|-+                     |        |
                |            +---+    |     +------+ |                     |        |
                |            |L11|----+              |                     |        |
                |            +---+                   |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     | +----+ |
                |                                    |                     +-|root| |
                |                                    |                     | +----+ |
                |            +---+                   |                     |        |
                |            |L12|----+     +------+ |                     |        |
                |            +---+    +-----|innerB|-+                     |        |
                |                     |     +------+ |                     |        |
                |                     |              | +------+            |        |
                |                     |              +-|innerD|-+          |        |
                |            +---+    |              | +------+ |          |        |
                |            |L13|----+              |          |          |        |
                |            +---+                   |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |            +---+                   |          |          |        |
                |            |L14|----+              |          | +------+ |        |
                |            +---+    |              |          +-|innerF|-+        |
                |                     |              |          | +------+          |
                |                     |              |          |                   |
                |                     |              |          |                   |
                |                     |     +------+ |          |                   |
                |            +---+    +-----|innerC|-+          |                   |
                |            |L15|----+     +------+            |                   |
                |            +---+    |                         |                   |
                |                     |                         |                   |
                |                     |                         |                   |
                |                     |                         |                   |
                | +---+   +------+    |                         |                   |
                | |L16|---|innerQ|----+                         |                   |
                | +---+   +------+                              |                   |
                |                                               |                   |
                |                                               |                   |
                |                                               |                   |
                |                                               |                   |
                |                                               |                   |
                |                                               |                   |
                |                               +--+            |                   |
                |                               |L6|-+          |                   |
                |                               +--+ |          |                   |
                |                                    |          |                   |
                |                                    |          |                   |
                |                               +--+ |          |                   |
                |                               |L7|-+          |                   |
                |                               +--+ |          |                   |
                |                                    | +------+ |                   |
                |                                    +-|innerE|-+                   |
                |                                    | +------+                     |
                |                               +--+ |                              |
                |                               |L8|-+                              |
                |                               +--+ |                              |
                |                                    |                              |
                |                                    |                              |
                |                                    |                              |
                |                    +-------------+ |                              |
                |                    |L9 Long Label|-+                              |
                |                    +-------------+                                |
                |                                                                   |
                |                                                                   |
                +-------------------------------------------------------------------+""", new TreePrinter(RIGHT_TO_LEFT).print(root).export());

        root = setupTestTree(SpecialBoxStyleTreeNode.class);

        assertEquals("""
                +-------------------------------------------------------------------+
                |                                                                   |
                |                               ====                                |
                |                               |L3|-+                              |
                |                               ==== |                              |
                |                                    |                              |
                |                                    |                              |
                |                                    |                              |
                |                            ======= |                              |
                |                            | L4  | |                              |
                |                            |line1|-+ +------+                     |
                |                            |line2| +-|innerG|---+                 |
                |                            ======= | +------+   |                 |
                |                                    |            |                 |
                |                                    |            |                 |
                |                             ====== |            |                 |
                |                             | L5 | |            |                 |
                |                             |sub1|-+            |    +-+          |
                |                             ======              +----|I|-+        |
                |                                                 |    +-+ |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                ===              |        |        |
                |                                |L|-+            |        |        |
                |                                === | +------+   |        |        |
                |                                    +-|innerH|---+        |        |
                |                                    | +------+            |        |
                |                               ==== |                     |        |
                |                               |L2|-+                     |        |
                |                               ====                       |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |            =====                                         |        |
                |            |L10|----+                                    |        |
                |            =====    |                                    |        |
                |                     |     +------+                       |        |
                |                     +-----|innerA|-+                     |        |
                |            =====    |     +------+ |                     |        |
                |            |L11|----+              |                     |        |
                |            =====                   |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     | ###### |
                |                                    |                     +-#root# |
                |                                    |                     | ###### |
                |            =====                   |                     |        |
                |            |L12|----+     +------+ |                     |        |
                |            =====    +-----|innerB|-+                     |        |
                |                     |     +------+ |                     |        |
                |                     |              | +------+            |        |
                |                     |              +-|innerD|-+          |        |
                |            =====    |              | +------+ |          |        |
                |            |L13|----+              |          |          |        |
                |            =====                   |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |            =====                   |          |          |        |
                |            |L14|----+              |          | +------+ |        |
                |            =====    |              |          +-|innerF|-+        |
                |                     |              |          | +------+          |
                |                     |              |          |                   |
                |                     |              |          |                   |
                |                     |     +------+ |          |                   |
                |            =====    +-----|innerC|-+          |                   |
                |            |L15|----+     +------+            |                   |
                |            =====    |                         |                   |
                |                     |                         |                   |
                |                     |                         |                   |
                |                     |                         |                   |
                | =====   +------+    |                         |                   |
                | |L16|---|innerQ|----+                         |                   |
                | =====   +------+                              |                   |
                |                                               |                   |
                |                                               |                   |
                |                                               |                   |
                |                                               |                   |
                |                                               |                   |
                |                                               |                   |
                |                               ====            |                   |
                |                               |L6|-+          |                   |
                |                               ==== |          |                   |
                |                                    |          |                   |
                |                                    |          |                   |
                |                               ==== |          |                   |
                |                               |L7|-+          |                   |
                |                               ==== |          |                   |
                |                                    | +------+ |                   |
                |                                    +-|innerE|-+                   |
                |                                    | +------+                     |
                |                               ==== |                              |
                |                               |L8|-+                              |
                |                               ==== |                              |
                |                                    |                              |
                |                                    |                              |
                |                                    |                              |
                |                    =============== |                              |
                |                    |L9 Long Label|-+                              |
                |                    ===============                                |
                |                                                                   |
                |                                                                   |
                +-------------------------------------------------------------------+""", new TreePrinter(RIGHT_TO_LEFT).print(root).export());

        root = setupTestTree(DecoratedTreeNode.class);

        assertEquals("""
                +-------------------------------------------------------------------+
                |                                                                   |
                |                               X--+0                               |
                |                               |L3|-+                              |
                |                               +--+ |                              |
                |                                    |                              |
                |                                    |                              |
                |                                    |                              |
                |                            X-----+ |                              |
                |                            | L4  |1|                              |
                |                            |line1|-+ X------+0                    |
                |                            |line2| +-|innerG|---+                 |
                |                            +-----+ | +------+   |                 |
                |                                    |            |                 |
                |                                    |            |                 |
                |                             X----+ |            |                 |
                |                             | L5 |2|            |                 |
                |                             |sub1|-+            |    X-+0         |
                |                             +----+              +----|I|-+        |
                |                                                 |    +-+ |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                                 |        |        |
                |                                X-+0             |        |        |
                |                                |L|-+            |        |        |
                |                                +-+ | X------+1  |        |        |
                |                                    +-|innerH|---+        |        |
                |                                    | +------+            |        |
                |                               X--+1|                     |        |
                |                               |L2|-+                     |        |
                |                               +--+                       |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |                                                          |        |
                |            X---+0                                        |        |
                |            |L10|----+                                    |        |
                |            +---+    |                                    |        |
                |                     |     X------+0                      |        |
                |                     +-----|innerA|-+                     |        |
                |            X---+1   |     +------+ |                     |        |
                |            |L11|----+              |                     |        |
                |            +---+                   |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     |        |
                |                                    |                     | X----+ |
                |                                    |                     +-|root| |
                |                                    |                     | +----+ |
                |            X---+1                  |                     |        |
                |            |L12|----+     X------+1|                     |        |
                |            +---+    +-----|innerB|-+                     |        |
                |                     |     +------+ |                     |        |
                |                     |              | X------+0           |        |
                |                     |              +-|innerD|-+          |        |
                |            X---+2   |              | +------+ |          |        |
                |            |L13|----+              |          |          |        |
                |            +---+                   |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |                                    |          |          |        |
                |            X---+0                  |          |          |        |
                |            |L14|----+              |          | X------+1|        |
                |            +---+    |              |          +-|innerF|-+        |
                |                     |              |          | +------+          |
                |                     |              |          |                   |
                |                     |              |          |                   |
                |                     |     X------+2|          |                   |
                |            X---+2   +-----|innerC|-+          |                   |
                |            |L15|----+     +------+            |                   |
                |            +---+    |                         |                   |
                |                     |                         |                   |
                |                     |                         |                   |
                |                     |                         |                   |
                | X---+0  X------+3   |                         |                   |
                | |L16|---|innerQ|----+                         |                   |
                | +---+   +------+                              |                   |
                |                                               |                   |
                |                                               |                   |
                |                                               |                   |
                |                                               |                   |
                |                                               |                   |
                |                                               |                   |
                |                               X--+0           |                   |
                |                               |L6|-+          |                   |
                |                               +--+ |          |                   |
                |                                    |          |                   |
                |                                    |          |                   |
                |                               X--+1|          |                   |
                |                               |L7|-+          |                   |
                |                               +--+ |          |                   |
                |                                    | X------+1|                   |
                |                                    +-|innerE|-+                   |
                |                                    | +------+                     |
                |                               X--+2|                              |
                |                               |L8|-+                              |
                |                               +--+ |                              |
                |                                    |                              |
                |                                    |                              |
                |                                    |                              |
                |                    X-------------+3|                              |
                |                    |L9 Long Label|-+                              |
                |                    +-------------+                                |
                |                                                                   |
                |                                                                   |
                +-------------------------------------------------------------------+""", new TreePrinter(RIGHT_TO_LEFT).print(root).export());

        root = setupTestTree(SimpleTreeNode.class);

        assertEquals("""
                +------------------------------------------------+
                |                                                |
                |            +--+                                |
                |            |L3|-+                              |
                |            +--+ |                              |
                |                 |                              |
                |                 |                              |
                |                 |                              |
                |         +-----+ |                              |
                |         | L4  | |                              |
                |         |line1|-+ +------+                     |
                |         |line2| +-|innerG|---+                 |
                |         +-----+ | +------+   |                 |
                |                 |            |                 |
                |                 |            |                 |
                |          +----+ |            |                 |
                |          | L5 | |            |                 |
                |          |sub1|-+            |    +-+          |
                |          +----+              +----|I|-+        |
                |                              |    +-+ |        |
                |                              |        |        |
                |                              |        |        |
                |                              |        |        |
                |                              |        |        |
                |                              |        |        |
                |                              |        |        |
                |             +-+              |        |        |
                |             |L|-+            |        |        |
                |             +-+ | +------+   |        |        |
                |                 +-|innerH|---+        |        |
                |                 | +------+            |        |
                |            +--+ |                     |        |
                |            |L2|-+                     |        |
                |            +--+                       |        |
                |                                       |        |
                |                                       |        |
                |                                       |        |
                |                                       |        |
                |                                       |        |
                |                                       |        |
                |                                       |        |
                |                                       | +----+ |
                |                                       +-|root| |
                |      . +------+                       | +----+ |
                |      .-|innerA|-+                     |        |
                |      . +------+ |                     |        |
                |                 |                     |        |
                |                 |                     |        |
                |      . +------+ | +------+            |        |
                |      .-|innerB|-+-|innerD|-+          |        |
                |      . +------+ | +------+ |          |        |
                |                 |          |          |        |
                |                 |          |          |        |
                |                 |          |          |        |
                |      . +------+ |          |          |        |
                |      .-|innerC|-+          |          |        |
                |      . +------+            |          |        |
                |                            |          |        |
                |                            |          |        |
                |                            |          |        |
                |                            | +------+ |        |
                |                            +-|innerF|-+        |
                |            +--+            | +------+          |
                |            |L6|-+          |                   |
                |            +--+ |          |                   |
                |                 |          |                   |
                |                 |          |                   |
                |            +--+ |          |                   |
                |            |L7|-+          |                   |
                |            +--+ |          |                   |
                |                 | +------+ |                   |
                |                 +-|innerE|-+                   |
                |                 | +------+                     |
                |            +--+ |                              |
                |            |L8|-+                              |
                |            +--+ |                              |
                |                 |                              |
                |                 |                              |
                |                 |                              |
                | +-------------+ |                              |
                | |L9 Long Label|-+                              |
                | +-------------+                                |
                |                                                |
                |                                                |
                +------------------------------------------------+""", new TreePrinter(RIGHT_TO_LEFT).print(root, 4).export());

        assertEquals("""
                +--------------------------------+
                |                                |
                | . +------+                     |
                | .-|innerG|---+                 |
                | . +------+   |                 |
                |              |    +-+          |
                |              +----|I|-+        |
                |              |    +-+ |        |
                | . +------+   |        |        |
                | .-|innerH|---+        |        |
                | . +------+            |        |
                |                       |        |
                |                       | +----+ |
                |                       +-|root| |
                |                       | +----+ |
                |                       |        |
                |                       |        |
                | . +------+            |        |
                | .-|innerD|-+          |        |
                | . +------+ | +------+ |        |
                |            +-|innerF|-+        |
                |            | +------+          |
                | . +------+ |                   |
                | .-|innerE|-+                   |
                | . +------+                     |
                |                                |
                +--------------------------------+""", new TreePrinter(RIGHT_TO_LEFT).print(root, 3).export());

        assertEquals("""
                +----------+
                | . +----+ |
                | .-|root| |
                | . +----+ |
                +----------+""", new TreePrinter(RIGHT_TO_LEFT).print(root, 1).export());

        assertEquals("""
                +-----+
                | ... |
                +-----+""", new TreePrinter(RIGHT_TO_LEFT).print(root, 0).export());

        assertEquals("""
                +---------------------+
                |                     |
                |                     |
                |                     |
                |                     |
                |         ...         |
                |                     |
                |                     |
                |                     |
                |                     |
                +---------------------+""",
                new TreePrinter(new HorizontalTreeDrawingPolicy(FrameConfig.frame10x5(), TreeLayoutConfig.getDefault(), true)).print(root, 0).export());

        assertEquals("""
                +--------+
                | <null> |
                +--------+""", new TreePrinter(RIGHT_TO_LEFT).print(null, 0).export());

        assertEquals("""
                +---+
                |   |
                +---+""", new TreePrinter(RIGHT_TO_LEFT).print(SimpleTreeNode.MISSING_SIBLING).export());

        assertEquals("""
                +-------------------+
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                +-------------------+""", new TreePrinter(new HorizontalTreeDrawingPolicy(FrameConfig.frame10x5(), TreeLayoutConfig.getDefault(), true))
                .print(SimpleTreeNode.MISSING_SIBLING).export());

    }

    @Test
    void testIndex() {

        TestTreeNode<?> root = setupTestTree(NoBoxStyleTreeNode.class);

        assertEquals("""
                +------------------------------+
                | root                         |
                |  |                           |
                |  +--I                        |
                |  |  |                        |
                |  |  +---innerG               |
                |  |  |    |                   |
                |  |  |    +--L3               |
                |  |  |    |                   |
                |  |  |    +--L4               |
                |  |  |    |  line1            |
                |  |  |    |  line2            |
                |  |  |    |                   |
                |  |  |    +--L5               |
                |  |  |       sub1             |
                |  |  |                        |
                |  |  +---innerH               |
                |  |       |                   |
                |  |       +--L                |
                |  |       |                   |
                |  |       +--L2               |
                |  |                           |
                |  +--innerF                   |
                |      |                       |
                |      +--innerD               |
                |      |   |                   |
                |      |   +--innerA           |
                |      |   |   |               |
                |      |   |   +--L10          |
                |      |   |   |               |
                |      |   |   +--L11          |
                |      |   |                   |
                |      |   |                   |
                |      |   |                   |
                |      |   +--innerB           |
                |      |   |   |               |
                |      |   |   |               |
                |      |   |   |               |
                |      |   |   +--L12          |
                |      |   |   |               |
                |      |   |   +--L13          |
                |      |   |                   |
                |      |   +--innerC           |
                |      |       |               |
                |      |       +--L14          |
                |      |       |               |
                |      |       |               |
                |      |       |               |
                |      |       +--L15          |
                |      |       |               |
                |      |       +--innerQ       |
                |      |           |           |
                |      |           +--L16      |
                |      |                       |
                |      +--innerE               |
                |          |                   |
                |          +--L6               |
                |          |                   |
                |          +--L7               |
                |          |                   |
                |          +--L8               |
                |          |                   |
                |          +--L9 Long Label    |
                |                              |
                +------------------------------+""", new TreePrinter(INDEX).print(root).export());

        assertEquals("""
                +---------------------+
                | root                |
                | +-I                 |
                | | +-innerG          |
                | | | +-L3            |
                | | | +-L4            |
                | | | | line1         |
                | | | | line2         |
                | | | +-L5            |
                | | |   sub1          |
                | | +-innerH          |
                | |   +-L             |
                | |   +-L2            |
                | +-innerF            |
                |   +-innerD          |
                |   | +-innerA        |
                |   | | +-L10         |
                |   | | +-L11         |
                |   | |               |
                |   | +-innerB        |
                |   | | |             |
                |   | | +-L12         |
                |   | | +-L13         |
                |   | +-innerC        |
                |   |   +-L14         |
                |   |   |             |
                |   |   +-L15         |
                |   |   +-innerQ      |
                |   |     +-L16       |
                |   +-innerE          |
                |     +-L6            |
                |     +-L7            |
                |     +-L8            |
                |     +-L9 Long Label |
                +---------------------+""", new TreePrinter(INDEX_SLIM).print(root).export());

        assertEquals("""
                +---------------------+
                | root                |
                |   I                 |
                |     innerG          |
                |       L3            |
                |       L4            |
                |       line1         |
                |       line2         |
                |       L5            |
                |       sub1          |
                |     innerH          |
                |       L             |
                |       L2            |
                |   innerF            |
                |     innerD          |
                |       innerA        |
                |         L10         |
                |         L11         |
                |                     |
                |       innerB        |
                |                     |
                |         L12         |
                |         L13         |
                |       innerC        |
                |         L14         |
                |                     |
                |         L15         |
                |         innerQ      |
                |           L16       |
                |     innerE          |
                |       L6            |
                |       L7            |
                |       L8            |
                |       L9 Long Label |
                +---------------------+""", new TreePrinter(TreeLayout.INDEX_SLIM_NO_CONNECTORS).print(root).export());

        assertEquals("""
                +-------------------------------------------------------+
                | root                                                  |
                |  |                                                    |
                |  +--------I                                           |
                |  |        |                                           |
                |  |        +---------innerG                            |
                |  |        |           |                               |
                |  |        |           +-------L3                      |
                |  |        |           |                               |
                |  |        |           +-------L4                      |
                |  |        |           |       line1                   |
                |  |        |           |       line2                   |
                |  |        |           |                               |
                |  |        |           +-------L5                      |
                |  |        |                   sub1                    |
                |  |        |                                           |
                |  |        +---------innerH                            |
                |  |                    |                               |
                |  |                    +-------L                       |
                |  |                    |                               |
                |  |                    +-------L2                      |
                |  |                                                    |
                |  +--------innerF                                      |
                |             |                                         |
                |             +-------innerD                            |
                |             |         |                               |
                |             |         +-------innerA                  |
                |             |         |          |                    |
                |             |         |          +------...           |
                |             |         |                               |
                |             |         +-------innerB                  |
                |             |         |          |                    |
                |             |         |          +------...           |
                |             |         |                               |
                |             |         +-------innerC                  |
                |             |                    |                    |
                |             |                    +------...           |
                |             |                                         |
                |             +-------innerE                            |
                |                       |                               |
                |                       +-------L6                      |
                |                       |                               |
                |                       +-------L7                      |
                |                       |                               |
                |                       +-------L8                      |
                |                       |                               |
                |                       +-------L9 Long Label           |
                |                                                       |
                +-------------------------------------------------------+""", new TreePrinter(INDEX_WIDE).print(root, 4).export());

        root = setupTestTree(SpecialBoxStyleTreeNode.class);

        assertEquals("""
                +------------------------------+
                | ######                       |
                | #root#                       |
                | ######                       |
                |  |                           |
                |  |  +-+                      |
                |  +--|I|                      |
                |  |  +-+                      |
                |  |   |                       |
                |  |   |  +------+             |
                |  |   +--|innerG|             |
                |  |   |  +------+             |
                |  |   |   |                   |
                |  |   |   |  ====             |
                |  |   |   +--|L3|             |
                |  |   |   |  ====             |
                |  |   |   |                   |
                |  |   |   |  =======          |
                |  |   |   |  |L4   |          |
                |  |   |   +--|line1|          |
                |  |   |   |  |line2|          |
                |  |   |   |  =======          |
                |  |   |   |                   |
                |  |   |   |  ======           |
                |  |   |   +--|L5  |           |
                |  |   |      |sub1|           |
                |  |   |      ======           |
                |  |   |                       |
                |  |   |  +------+             |
                |  |   +--|innerH|             |
                |  |      +------+             |
                |  |       |                   |
                |  |       |  ===              |
                |  |       +--|L|              |
                |  |       |  ===              |
                |  |       |                   |
                |  |       |  ====             |
                |  |       +--|L2|             |
                |  |          ====             |
                |  |                           |
                |  |  +------+                 |
                |  +--|innerF|                 |
                |     +------+                 |
                |      |                       |
                |      |  +------+             |
                |      +--|innerD|             |
                |      |  +------+             |
                |      |   |                   |
                |      |   |  +------+         |
                |      |   +--|innerA|         |
                |      |   |  +------+         |
                |      |   |   |               |
                |      |   |   |  =====        |
                |      |   |   +--|L10|        |
                |      |   |   |  =====        |
                |      |   |   |               |
                |      |   |   |  =====        |
                |      |   |   +--|L11|        |
                |      |   |      =====        |
                |      |   |                   |
                |      |   |                   |
                |      |   |                   |
                |      |   |  +------+         |
                |      |   +--|innerB|         |
                |      |   |  +------+         |
                |      |   |   |               |
                |      |   |   |               |
                |      |   |   |               |
                |      |   |   |  =====        |
                |      |   |   +--|L12|        |
                |      |   |   |  =====        |
                |      |   |   |               |
                |      |   |   |  =====        |
                |      |   |   +--|L13|        |
                |      |   |      =====        |
                |      |   |                   |
                |      |   |  +------+         |
                |      |   +--|innerC|         |
                |      |      +------+         |
                |      |       |               |
                |      |       |  =====        |
                |      |       +--|L14|        |
                |      |       |  =====        |
                |      |       |               |
                |      |       |               |
                |      |       |               |
                |      |       |  =====        |
                |      |       +--|L15|        |
                |      |       |  =====        |
                |      |       |               |
                |      |       |  +------+     |
                |      |       +--|innerQ|     |
                |      |          +------+     |
                |      |           |           |
                |      |           |  =====    |
                |      |           +--|L16|    |
                |      |              =====    |
                |      |                       |
                |      |  +------+             |
                |      +--|innerE|             |
                |         +------+             |
                |          |                   |
                |          |  ====             |
                |          +--|L6|             |
                |          |  ====             |
                |          |                   |
                |          |  ====             |
                |          +--|L7|             |
                |          |  ====             |
                |          |                   |
                |          |  ====             |
                |          +--|L8|             |
                |          |  ====             |
                |          |                   |
                |          |  ===============  |
                |          +--|L9 Long Label|  |
                |             ===============  |
                |                              |
                +------------------------------+""", new TreePrinter(INDEX).print(root).export());

        root = setupTestTree(SimpleTreeNode.class);

        assertEquals("""
                +-----------------------------+
                | +----+                      |
                | |root|                      |
                | +----+                      |
                |  |                          |
                |  |  +-+                     |
                |  +--|I|                     |
                |  |  +-+                     |
                |  |   |                      |
                |  |   |  +------+            |
                |  |   +--|innerG|            |
                |  |   |  +------+            |
                |  |   |   |                  |
                |  |   |   |  +--+            |
                |  |   |   +--|L3|            |
                |  |   |   |  +--+            |
                |  |   |   |                  |
                |  |   |   |  +-----+         |
                |  |   |   |  |L4   |         |
                |  |   |   +--|line1|         |
                |  |   |   |  |line2|         |
                |  |   |   |  +-----+         |
                |  |   |   |                  |
                |  |   |   |  +----+          |
                |  |   |   +--|L5  |          |
                |  |   |      |sub1|          |
                |  |   |      +----+          |
                |  |   |                      |
                |  |   |  +------+            |
                |  |   +--|innerH|            |
                |  |      +------+            |
                |  |       |                  |
                |  |       |  +-+             |
                |  |       +--|L|             |
                |  |       |  +-+             |
                |  |       |                  |
                |  |       |  +--+            |
                |  |       +--|L2|            |
                |  |          +--+            |
                |  |                          |
                |  |  +------+                |
                |  +--|innerF|                |
                |     +------+                |
                |      |                      |
                |      |  +------+            |
                |      +--|innerD|            |
                |      |  +------+            |
                |      |   |                  |
                |      |   |  +------+        |
                |      |   +--|innerA|        |
                |      |   |  +------+        |
                |      |   |    |             |
                |      |   |    +-...         |
                |      |   |                  |
                |      |   |  +------+        |
                |      |   +--|innerB|        |
                |      |   |  +------+        |
                |      |   |    |             |
                |      |   |    +-...         |
                |      |   |                  |
                |      |   |  +------+        |
                |      |   +--|innerC|        |
                |      |      +------+        |
                |      |        |             |
                |      |        +-...         |
                |      |                      |
                |      |  +------+            |
                |      +--|innerE|            |
                |         +------+            |
                |          |                  |
                |          |  +--+            |
                |          +--|L6|            |
                |          |  +--+            |
                |          |                  |
                |          |  +--+            |
                |          +--|L7|            |
                |          |  +--+            |
                |          |                  |
                |          |  +--+            |
                |          +--|L8|            |
                |          |  +--+            |
                |          |                  |
                |          |  +-------------+ |
                |          +--|L9 Long Label| |
                |             +-------------+ |
                |                             |
                +-----------------------------+""", new TreePrinter(INDEX).print(root, 4).export());

        assertEquals("""
                +---------------------+
                | +----+              |
                | |root|              |
                | +----+              |
                |  |                  |
                |  |  +-+             |
                |  +--|I|             |
                |  |  +-+             |
                |  |   |              |
                |  |   |  +------+    |
                |  |   +--|innerG|    |
                |  |   |  +------+    |
                |  |   |    |         |
                |  |   |    +-...     |
                |  |   |              |
                |  |   |  +------+    |
                |  |   +--|innerH|    |
                |  |      +------+    |
                |  |        |         |
                |  |        +-...     |
                |  |                  |
                |  |  +------+        |
                |  +--|innerF|        |
                |     +------+        |
                |      |              |
                |      |  +------+    |
                |      +--|innerD|    |
                |      |  +------+    |
                |      |    |         |
                |      |    +-...     |
                |      |              |
                |      |  +------+    |
                |      +--|innerE|    |
                |         +------+    |
                |           |         |
                |           +-...     |
                |                     |
                +---------------------+""", new TreePrinter(INDEX).print(root, 3).export());

        assertEquals("""
                +---------+
                | +----+  |
                | |root|  |
                | +----+  |
                |   |     |
                |   +-... |
                |         |
                +---------+""", new TreePrinter(INDEX).print(root, 1).export());

        assertEquals("""
                +-----+
                | ... |
                +-----+""", new TreePrinter(INDEX).print(root, 0).export());

        assertEquals("""
                +---------------------+
                |                     |
                |                     |
                |                     |
                |                     |
                |         ...         |
                |                     |
                |                     |
                |                     |
                |                     |
                +---------------------+""",
                new TreePrinter(new IndexTreeDrawingPolicy(FrameConfig.frame10x5(), TreeLayoutConfig.index(), false)).print(root, 0).export());

        assertEquals("""
                +--------+
                | <null> |
                +--------+""", new TreePrinter(INDEX).print(null, 0).export());

        assertEquals("""
                +---+
                |   |
                +---+""", new TreePrinter(INDEX).print(SimpleTreeNode.MISSING_SIBLING).export());

        assertEquals("""
                +-------------------+
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                |                   |
                +-------------------+""", new TreePrinter(new IndexTreeDrawingPolicy(FrameConfig.frame10x5(), TreeLayoutConfig.index(), false))
                .print(SimpleTreeNode.MISSING_SIBLING).export());

    }

    @Test
    void testSpecialCases() {
        assertThrows(UnsupportedOperationException.class, PrintableTreeNode.MISSING_SIBLING::getNodeLabel);
        assertThrows(UnsupportedOperationException.class, PrintableTreeNode.MISSING_SIBLING::getNumberOfSiblings);
        assertThrows(UnsupportedOperationException.class, () -> PrintableTreeNode.MISSING_SIBLING.getSiblingNode(0));
        assertThrows(UnsupportedOperationException.class, () -> PrintableTreeNode.MISSING_SIBLING.getBoxStyle(null));
        assertThrows(UnsupportedOperationException.class, () -> PrintableTreeNode.MISSING_SIBLING.decorateNode(null, null, 0, 0, 0, 0));
        assertThrows(UnsupportedOperationException.class, () -> PrintableTreeNode.MISSING_SIBLING.decorateParentConnector(null, null, null, null));

        NodeFormatInfo info1 = new NodeFormatInfo(new SimpleTreeNode("foo"), DefaultBoxStyle.ASTERISK, Arrays.asList("line1", "line2"),
                new NodeKey[] { new NodeKey(new int[] { 1, 0 }), new NodeKey(new int[] { 1, 1 }) }, 50, 50, false, false);

        NodeFormatInfo info2 = new NodeFormatInfo(new SimpleTreeNode("foo"), DefaultBoxStyle.ASTERISK, Arrays.asList("line1", "line2"),
                new NodeKey[] { new NodeKey(new int[] { 1, 0 }), new NodeKey(new int[] { 1, 1 }) }, 50, 50, false, false);

        NodeFormatInfo info3 = new NodeFormatInfo(new SimpleTreeNode("foo"), DefaultBoxStyle.ASTERISK, Arrays.asList("line1", "line2"),
                new NodeKey[] { new NodeKey(new int[] { 1, 0 }), new NodeKey(new int[] { 1, 1 }) }, 51, 50, false, false);

        assertEquals(info1, info2);
        assertNotEquals(info1, info3);

        assertEquals(info1.hashCode(), info2.hashCode());
        assertNotEquals(info2.hashCode(), info3.hashCode());

        NodeFormatInfo info4 = new NodeFormatInfo(null, DefaultBoxStyle.ASTERISK, Arrays.asList("line1", "line2"),
                new NodeKey[] { new NodeKey(new int[] { 1, 0 }), new NodeKey(new int[] { 1, 1 }) }, 51, 50, false, false);

        assertEquals(
                "NodeFormatInfo [node=null, boxStyle=ASTERISK, representation=[line1, line2], siblingNodeKeys=[NodeKey(1/0), NodeKey(1/1)], totalWidth=51, "
                        + "totalHeight=50, positionX=0, positionY=0, isMissing=false, drawPlaceholderAppendix=false]",
                info4.toString());

        NodeKey key = new NodeKey(new int[] { 1, 2, 3 });

        assertEquals("NodeKey(1/2/3)", key.toString());

        assertEquals(new NodeKey(new int[] { 1, 2 }), key.parent());

        NodeKey root = key.parent().parent();

        assertThrows(IllegalStateException.class, root::parent);

    }

    private static <T extends TestTreeNode<T>> T setupTestTree(Class<T> clazz) {

        T leaf1 = createTestNode(clazz, "L");
        T leaf2 = createTestNode(clazz, "L2");

        T leaf3 = createTestNode(clazz, "L3");
        T leaf4 = createTestNode(clazz, "L4\nline1\nline2");
        T leaf5 = createTestNode(clazz, "L5\nsub1");

        T leaf6 = createTestNode(clazz, "L6");
        T leaf7 = createTestNode(clazz, "L7");
        T leaf8 = createTestNode(clazz, "L8");
        T leaf9 = createTestNode(clazz, "L9 Long Label");

        T leaf10 = createTestNode(clazz, "L10");
        T leaf11 = createTestNode(clazz, "L11");

        T leaf12 = createTestNode(clazz, "L12");
        T leaf13 = createTestNode(clazz, "L13");

        T leaf14 = createTestNode(clazz, "L14");
        T leaf15 = createTestNode(clazz, "L15");

        T leaf16 = createTestNode(clazz, "L16");

        T innerQ = createTestNode(clazz, "innerQ", leaf16);

        T innerA = createTestNode(clazz, "innerA", leaf10, leaf11, null);
        T innerB = createTestNode(clazz, "innerB", null, leaf12, leaf13);
        T innerC = createTestNode(clazz, "innerC", leaf14, null, leaf15, innerQ);

        T innerD = createTestNode(clazz, "innerD", innerA, innerB, innerC);

        T innerE = createTestNode(clazz, "innerE", leaf6, leaf7, leaf8, leaf9);

        T innerF = createTestNode(clazz, "innerF", innerD, innerE);

        T innerG = createTestNode(clazz, "innerG", leaf3, leaf4, leaf5);
        T innerH = createTestNode(clazz, "innerH", leaf1, leaf2);

        T innerI = createTestNode(clazz, "I", innerG, innerH);

        return createTestNode(clazz, "root", innerI, innerF);

    }

    private static <T extends TestTreeNode<T>> T createTestNode(Class<T> clazz, String label) {
        try {
            T res = clazz.getDeclaredConstructor(new Class[0]).newInstance();
            res.label = label;
            return res;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SafeVarargs
    private static <T extends TestTreeNode<T>> T createTestNode(Class<T> clazz, String label, T... siblings) {
        try {
            T res = clazz.getDeclaredConstructor(new Class[0]).newInstance();
            res.label = label;
            res.addSiblings(siblings);
            return res;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static <T extends TestTreeNode<T>> T createTestNode(TestTreeNode<T> example, String label) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) example.getClass();
        try {
            T res = clazz.getDeclaredConstructor(new Class[0]).newInstance();
            res.label = label;
            return res;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    abstract static class TestTreeNode<T extends TestTreeNode<T>> implements PrintableTreeNode {

        @Override
        public int hashCode() {
            return Objects.hash(label, siblings);
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
            @SuppressWarnings("unchecked")
            TestTreeNode<T> other = (TestTreeNode<T>) obj;
            return Objects.equals(label, other.label) && Objects.equals(siblings, other.siblings);
        }

        String label = null;

        List<T> siblings = new ArrayList<>();

        @Override
        public String getNodeLabel() {
            return label;
        }

        @Override
        public int getNumberOfSiblings() {
            return siblings.size();
        }

        @Override
        public PrintableTreeNode getSiblingNode(int siblingSelector) {
            PrintableTreeNode res = siblingSelector < siblings.size() ? siblings.get(siblingSelector) : null;
            if (res == null) {
                res = SimpleTreeNode.MISSING_SIBLING;
            }
            return res;
        }

        @SuppressWarnings("unchecked")
        public void addSiblings(T... siblings) {
            this.siblings.addAll(Arrays.asList(siblings));
        }

        public void addSiblings(String... labels) {
            for (String l : labels) {
                siblings.add(createTestNode(this, l));
            }
        }

    }

    static class SimpleTreeNode extends TestTreeNode<SimpleTreeNode> {

        public SimpleTreeNode() {
            //
        }

        public SimpleTreeNode(String label) {
            this.label = label;
        }

    }

    static class SpecialBoxStyleTreeNode extends TestTreeNode<SpecialBoxStyleTreeNode> {

        public SpecialBoxStyleTreeNode() {
            //
        }

        public SpecialBoxStyleTreeNode(String label) {
            this.label = label;
        }

        @Override
        public BoxStyle getBoxStyle(SiblingParentRelation siblingParentRelation) {
            if (siblingParentRelation == DefaultParentRelation.NONE) {
                return DefaultBoxStyle.HASH;
            }
            else if (getNumberOfSiblings() > 0) {
                return DefaultBoxStyle.THIN;
            }
            else {
                return DefaultBoxStyle.TOP_AND_BOTTOM_DOUBLE_SIDE_THIN;
            }
        }

    }

    static class DecoratedTreeNode extends TestTreeNode<DecoratedTreeNode> {

        public DecoratedTreeNode() {
            //
        }

        public DecoratedTreeNode(String label) {
            this.label = label;
        }

        @Override
        public void decorateNode(SiblingParentRelation siblingParentRelation, TextCanvas canvas, int upperLeftCornerX, int upperLeftCornerY, int width,
                int height) {
            canvas.setCursor(upperLeftCornerX, upperLeftCornerY);
            canvas.write('X');
        }

        @Override
        public void decorateParentConnector(SiblingParentRelation siblingParentRelation, TextCanvas canvas, BoxConnectionPoint from, BoxConnectionPoint to) {
            canvas.setCursor(to.x(), to.y());
            switch (to.side()) {
            case LEFT:
                canvas.write('>');
                break;
            case RIGHT:
                canvas.setCursor(to.x(), to.y() - 1);
                canvas.write("" + siblingParentRelation.parentSiblingSelector());
                break;
            case TOP:
                canvas.write('V');
                break;
            case BOTTOM:
                canvas.setCursor(to.x() - 1, to.y());
                canvas.write("" + siblingParentRelation.parentSiblingSelector());
                break;
            }

        }

    }

    static class NoBoxStyleTreeNode extends TestTreeNode<NoBoxStyleTreeNode> {

        public NoBoxStyleTreeNode() {
            //
        }

        public NoBoxStyleTreeNode(String label) {
            this.label = label;
        }

        @Override
        public BoxStyle getBoxStyle(SiblingParentRelation siblingParentRelation) {
            return DefaultBoxStyle.NONE;
        }

    }

}
