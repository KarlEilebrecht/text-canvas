//@formatter:off
/*
 * SiblingParentRelation
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

/**
 * Contains information of a sibling node in relation to its parent
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public interface SiblingParentRelation {

    /**
     * @return total number of siblings at the parent (including missing expected siblings)
     */
    int parentNumberOfSiblings();

    /**
     * @return the position of sibling at its parent's list of siblings
     */
    int parentSiblingSelector();
}