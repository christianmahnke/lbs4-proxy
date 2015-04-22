package de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util

import com.google.javascript.rhino.Node
import groovy.transform.CompileStatic

/**
 * Created by cmahnke on 26.02.15.
 * Just for Ingo, provides a DSL for simple AST creation
 */
@CompileStatic
class NodeBuilder {

    public static Node block() {
        return new Node(125)
    }

    public static Node block(Node... nodes) {
        Node block = new Node(125)
        for (n in nodes) {
            block.addChildrenToBack(n)
        }
        return block
    }

    public static Node expr_result() {
        return new Node(130)
    }

    public static Node expr_result(Node... nodes) {
        Node expr_result =  new Node(130)
        for (n in nodes) {
            expr_result.addChildrenToBack(n)
        }
        return expr_result
    }

    public static Node assign() {
        return new Node(86)
    }

    public static Node assign(Node... nodes) {
        Node assign = new Node(86)
        for (n in nodes) {
            assign.addChildrenToBack(n)
        }
        return assign
    }

    public static Node getprop() {
        return new Node(33)
    }

    public static Node getprop(Node... nodes) {
        Node block = new Node(33)
        if(nodes.length > 2) {
            throw new IllegalStateException("GETPROP only takes two childs, got ${nodes.length}")
        }
        for (n in nodes) {
            block.addChildrenToBack(n)
        }
        return block
    }

    public static Node or() {
        return new Node(100)
    }

    public static Node or(Node... nodes) {
        Node or = new Node(100)
        for (n in nodes) {
            or.addChildrenToBack(n)
        }
        return or
    }

    public static Node var() {
        return new Node(118)
    }

    public static Node var(Node... nodes) {
        Node var = new Node(118)
        for (n in nodes) {
            var.addChildrenToBack(n)
        }
        return var
    }

    public static Node not() {
        return new Node(26)
    }

    public static Node not(Node... nodes) {
        Node not = new Node(26)
        for (n in nodes) {
            not.addChildrenToBack(n)
        }
        return not
    }

    public static Node iF() {
        return new Node(108)
    }

    public static Node iF (Node condition, Node block) {
        if (!block.isBlock()) {
            throw new IllegalStateException('Not a block')
        }
        Node i = iF()
        i.addChildrenToBack(condition)
        i.addChildrenToBack(block)
        return i
    }

    public static Node name(String name) {
        return Node.newString(38, name)
    }

    public static Node name(String name, Node... nodes) {
        Node nameN = Node.newString(38, name)
        for (n in nodes) {
            nameN.addChildrenToBack(n)
        }
        return nameN
    }

    public static Node str(String str) {
        return Node.newString(40, str)
    }

    public static Node str(String str, Node... nodes) {
        Node strN = Node.newString(40, str)
        for (n in nodes) {
            strN.addChildrenToBack(n)
        }
        return strN
    }

    public static Node call() {
        return new Node(37)
    }

    public static Node call(Node... nodes) {
        Node call = new Node(37)
        for (n in nodes) {
            call.addChildrenToBack(n)
        }
        return call
    }

    public static Node nil() {
        return new Node(41)
    }


}
