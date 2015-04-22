package de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util

import com.google.javascript.rhino.Node
import groovy.transform.CompileStatic

/**
 * Created by cmahnke on 26.02.15.
 */
@CompileStatic
class NodeUtils {

    public static Node getFunctionNode (Node n) {
        Node cur = n
        while (!cur.getParent().isFunction()) {
            cur = cur.getParent()
        }
        assert cur.getParent().isFunction()
        return cur.getParent()
    }

    public static addArgument (Node func, String name) {
        if(!func.isFunction()) {
            throw new IllegalStateException('not a function node')
        }
        //Type 83 PARAM_LIST
        //Child 0: NAME
        //Child 1: PARAM_LIST
        //Child 2: BLOCK
        Node paramList = func.getChildAtIndex(1)
        Node n = Node.newString(38, name)
        paramList.addChildrenToBack(n)
    }

    public static prependFunction(Node func, Node node) {
        if(!func.isFunction()) {
            throw new IllegalStateException('not a function node')
        }
        //Type 83 PARAM_LIST
        //Child 0: NAME
        //Child 1: PARAM_LIST
        //Child 2: BLOCK
        Node block = func.getChildAtIndex(2)
        block.addChildToFront(node)
    }

    public Node getParamList(Node n) {
        if (n.getType() == 105) {
            assert n.getFirstChild().getType() == 38
            String functionName = n.getFirstChild().getString()
            Node params = n.getFirstChild().getNext()
            assert params.getType() == 83
            return params
        }
    }

}
