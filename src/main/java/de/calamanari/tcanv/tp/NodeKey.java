//@formatter:off
/*
 * NodeKey
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

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A {@link NodeKey} is a wrapper around the current sibling path (starting with the {@link #root()}). In other words, the key of a node is the sequence of
 * selectors starting with the root node. Hence, the root's key is an empty path. This creates unique keys for all nodes independent of the source nodes'
 * identities for caching temporary information to layout the tree graph.
 * <p>
 * Equality is defined on the members of the path and their order.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public record NodeKey(int[] path) {

    /**
     * @return key with the single path element 0
     */
    public static NodeKey root() {
        return new NodeKey(new int[] { 0 });
    }

    /**
     * @return key of length 0 (invalid key)
     */
    public static NodeKey none() {
        return new NodeKey(new int[0]);
    }

    /**
     * @param path to be wrapped (by reference!)
     */
    public NodeKey(int[] path) {
        this.path = (path != null ? path : new int[0]);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(path);
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
        NodeKey other = (NodeKey) obj;
        return Arrays.equals(path, other.path);
    }

    /**
     * Creates a new extended key with the given sibling selector appended to the path
     * 
     * @param siblingSelector
     * @return new instance with copy of the path plus the given sibling
     */
    public NodeKey sibling(int siblingSelector) {
        int[] siblingPath = Arrays.copyOf(path, path.length + 1);
        siblingPath[path.length] = siblingSelector;
        return new NodeKey(siblingPath);
    }

    /**
     * Returns the direct parent key (subtracts the last path element)
     * 
     * @return new instance with parent path
     */
    public NodeKey parent() {
        if (path.length < 2) {
            throw new IllegalStateException("Node does not have a parent: " + this);
        }
        return new NodeKey(Arrays.copyOf(path, path.length - 1));
    }

    /**
     * @return number of path elements including root
     */
    public int length() {
        return path.length;
    }

    /**
     * @return true if this key identifies a node, otherwise it is a dummy
     */
    public boolean isValid() {
        return length() > 0;
    }

    /**
     * Tells whether the path only contains '0's, means this is the current outer left sibling's key
     * 
     * @return true if the path is valid and only contains zero
     */
    public boolean isPathOfZeros() {
        if (isValid()) {
            for (int i = 0; i < path.length; i++) {
                if (path[i] != 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + Arrays.stream(path).mapToObj(Integer::toString).collect(Collectors.joining("/")) + ")";
    }

}