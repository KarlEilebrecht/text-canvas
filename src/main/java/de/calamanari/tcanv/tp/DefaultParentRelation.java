//@formatter:off
/*
 * DefaultParentRelation
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
 * Keeps together the parent key and information about the relation
 * 
 * @param parentKey
 * @param parentNumberOfSiblings
 * @param parentSiblingSelector
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public record DefaultParentRelation(NodeKey parentKey, int parentNumberOfSiblings, int parentSiblingSelector) implements ParentRelation {

    /**
     * Constant to express that there is no parent relation
     */
    public static final ParentRelation NONE = new DefaultParentRelation(NodeKey.none(), -1, -1);

}