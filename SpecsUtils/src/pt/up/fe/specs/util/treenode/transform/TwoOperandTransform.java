/**
 * Copyright 2016 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package pt.up.fe.specs.util.treenode.transform;

import java.util.Arrays;

import pt.up.fe.specs.util.treenode.TreeNode;

public abstract class TwoOperandTransform<K extends TreeNode<K>> extends ANodeTransform<K> {

    public TwoOperandTransform(String type, K node1, K node2) {
        super(type, Arrays.asList(node1, node2));
    }

    public K getNode1() {
        return getOperands().get(0);
    }

    public K getNode2() {
        return getOperands().get(1);
    }

    @Override
    public String toString() {
        String baseHex = Integer.toHexString(getNode1().hashCode());
        String newHex = Integer.toHexString(getNode2().hashCode());

        return getType() + " node1(" + baseHex + ") node2(" + newHex + ")";
    }

}
