//@formatter:off
/*
 * CharacterConflictResolver
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
 * Default implementations for resolving conflicts between an existing character and a character to be written.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public enum CharacterConflictResolver implements BinaryOperator<Character> {

    /**
     * Writes the character to be written ignoring the existing character.
     */
    OVERWRITE,

    /**
     * If there is any other character than whitespace then this character will be preserved (proposed character will not be written).
     */
    PRESERVE;

    @Override
    public Character apply(Character existingChar, Character updateChar) {
        if (this == PRESERVE && !Character.isWhitespace(existingChar)) {
            return existingChar;
        }
        else {
            return updateChar;
        }
    }

}