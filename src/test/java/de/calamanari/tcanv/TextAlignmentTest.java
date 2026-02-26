//@formatter:off
/*
 * TextAlignmentTest
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

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class TextAlignmentTest {

    @Test
    void testLineAlignment() {

        assertEquals("  foo  ", TextAlignment.center("foo", 7));
        assertEquals("foo    ", TextAlignment.leftAlign("foo", 7));
        assertEquals("    foo", TextAlignment.rightAlign("foo", 7));

        assertEquals("fo", TextAlignment.center("foo", 2));
        assertEquals("fo", TextAlignment.leftAlign("foo", 2));
        assertEquals("fo", TextAlignment.rightAlign("foo", 2));

        assertEquals("foo", TextAlignment.center("foo", 3));
        assertEquals("foo", TextAlignment.leftAlign("foo", 3));
        assertEquals("foo", TextAlignment.rightAlign("foo", 3));

        assertThrows(NullPointerException.class, () -> TextAlignment.rightAlign(null, 3));

    }

    @Test
    void testComputeTrimmedDimensions() {

        assertArrayEquals(new int[] { 0, 0 }, TextAlignment.computeTrimmedDimensions(""));
        assertArrayEquals(new int[] { 1, 1 }, TextAlignment.computeTrimmedDimensions("a"));
        assertArrayEquals(new int[] { 4, 1 }, TextAlignment.computeTrimmedDimensions("abba"));
        assertArrayEquals(new int[] { 4, 2 }, TextAlignment.computeTrimmedDimensions("  abba  \n  baba"));
        assertArrayEquals(new int[] { 8, 2 }, TextAlignment.computeTrimmedDimensions("  abba  \n  ali baba"));
        assertArrayEquals(new int[] { 8, 2 }, TextAlignment.computeTrimmedDimensions("  abba  \n  ali baba\n"));

        assertArrayEquals(new int[] { 1, 1 }, TextAlignment.computeTrimmedDimensions("a", 3, 1));
        assertArrayEquals(new int[] { 3, 1 }, TextAlignment.computeTrimmedDimensions("abba", 3, 1));
        assertArrayEquals(new int[] { 3, 2 }, TextAlignment.computeTrimmedDimensions("  abba  \n  baba", 3, 2));
        assertArrayEquals(new int[] { 3, 1 }, TextAlignment.computeTrimmedDimensions("  abba  \n  ali baba", 3, 1));
        assertArrayEquals(new int[] { 3, 1 }, TextAlignment.computeTrimmedDimensions("  abba  \n  ali baba\n", 3, 1));

    }

    @Test
    void testLabelAlignment() {

        String label = "Fluffy, Tuffy, and Muffy\nwent to town.\nThey all died\n  in a terrible accident.";

        assertEquals("""
                _________________________
                _________________________
                _________________________
                _________________________
                _________________________
                _________________________
                Fluffy,_Tuffy,_and_Muffy
                ______went_to_town.______
                ______They_all_died______
                _in_a_terrible_accident._""", combine(TextAlignment.CENTER_BOTTOM.apply(label, 25, 10)));

        assertEquals("""
                _________________________
                _________________________
                _________________________
                Fluffy,_Tuffy,_and_Muffy
                ______went_to_town.______
                ______They_all_died______
                _in_a_terrible_accident._
                _________________________
                _________________________
                _________________________""", combine(TextAlignment.CENTER_CENTER.apply(label, 25, 10)));

        assertEquals("""
                Fluffy,_Tuffy,_and_Muffy
                ______went_to_town.______
                ______They_all_died______
                _in_a_terrible_accident._
                _________________________
                _________________________
                _________________________
                _________________________
                _________________________
                _________________________""", combine(TextAlignment.CENTER_TOP.apply(label, 25, 10)));

        assertEquals("""
                _________________________
                _________________________
                _________________________
                _________________________
                _________________________
                _________________________
                Fluffy,_Tuffy,_and_Muffy
                went_to_town.____________
                They_all_died____________
                in_a_terrible_accident.__""", combine(TextAlignment.LEFT_BOTTOM.apply(label, 25, 10)));

        assertEquals("""
                _________________________
                _________________________
                _________________________
                Fluffy,_Tuffy,_and_Muffy
                went_to_town.____________
                They_all_died____________
                in_a_terrible_accident.__
                _________________________
                _________________________
                _________________________""", combine(TextAlignment.LEFT_CENTER.apply(label, 25, 10)));

        assertEquals("""
                Fluffy,_Tuffy,_and_Muffy
                went_to_town.____________
                They_all_died____________
                in_a_terrible_accident.__
                _________________________
                _________________________
                _________________________
                _________________________
                _________________________
                _________________________""", combine(TextAlignment.LEFT_TOP.apply(label, 25, 10)));

        assertEquals("""
                _________________________
                _________________________
                _________________________
                _________________________
                _________________________
                _________________________
                Fluffy,_Tuffy,_and_Muffy
                ____________went_to_town.
                ____________They_all_died
                __in_a_terrible_accident.""", combine(TextAlignment.RIGHT_BOTTOM.apply(label, 25, 10)));

        assertEquals("""
                _________________________
                _________________________
                _________________________
                Fluffy,_Tuffy,_and_Muffy
                ____________went_to_town.
                ____________They_all_died
                __in_a_terrible_accident.
                _________________________
                _________________________
                _________________________""", combine(TextAlignment.RIGHT_CENTER.apply(label, 25, 10)));

        assertEquals("""
                Fluffy,_Tuffy,_and_Muffy
                ____________went_to_town.
                ____________They_all_died
                __in_a_terrible_accident.
                _________________________
                _________________________
                _________________________
                _________________________
                _________________________
                _________________________""", combine(TextAlignment.RIGHT_TOP.apply(label, 25, 10)));

    }

    private static String combine(List<String> lines) {
        return lines.stream().collect(Collectors.joining("\n")).replace(' ', '_');
    }

}
