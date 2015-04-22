package de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite

import com.google.javascript.jscomp.NodeTraversal
import com.google.javascript.rhino.JSDocInfo
import com.google.javascript.rhino.Node
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeUtils
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.getprop
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.name
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.str

/**
 * Created by cmahnke on 15.04.15.
 */
@Log4j
@CompileStatic
class WindowEventRewrite extends BaseRewrite implements NodeTraversal.Callback {
    static String OBJECT = 'window'
    static String METHOD = 'event'

    Node from = getprop(name(OBJECT), str(METHOD))

    WindowEventRewrite() {
    }

    public void visit(NodeTraversal t, Node n, Node parent) {
        /*
        if (n.isFunction()) {
            NodeUtils.addArgument(n, 'e')
            NodeUtils.prependFunction(n, JSConstants.getEventNode())
        }
        */
        if (n.isEquivalentToTyped(from)) {

            log.info('found node: ' + n.toString())
            updateFunction(n)
            //Check if there are children
            Node newGetOpt
            if (parent.isGetProp()) {
                //Check if srcElement needs to be rewritten to target
                if (n.getNext().isString() && n.getNext().getString() == 'srcElement') {
                    log.trace('Also rewriting srcElement to target')
                    if (!parent.getParent().getParent().isVar()) {
                        Node child = parent.getParent().getChildAtIndex(1).cloneNode()
                        if (!child.isString()) {
                            throw new IllegalStateException('window.event rewrite for childs that are not strings is not implemented yet on line ' + n.getLineno())
                        }
                        newGetOpt = getprop(getprop(name('e'), str('target')), child)
                        parent.getParent().getParent().replaceChild(parent.getParent(), newGetOpt)
                    } else {
                        newGetOpt = getprop(name('e'), str('target'))
                        parent.getParent().replaceChild(parent, newGetOpt)
                    }
                } else {
                    newGetOpt = getprop(name('e'), parent.getChildAtIndex(1).cloneNode())
                    parent.getParent().replaceChild(parent, newGetOpt)
                }
            } else if (parent.isIf() || parent.isCall() || parent.isNot() || parent.isAssign()) {
                if (n.getNext()!= null && n.getNext().isString() && n.getNext().getString() == 'srcElement') {
                    throw new IllegalStateException('window.event.srcElement rewrite for parent If is not implemented yet on line ' + n.getLineno())
                } else if (parent.getChildAtIndex(0).isEquivalentToTyped(from) || parent.getLastChild().isEquivalentToTyped(from)) {
                    parent.replaceChild(n, name('e'))
                } else {
                    throw new IllegalStateException('window.event rewrite for parent If is not implemented yet for complex expressions on line ' + n.getLineno())
                }
            } else {
                throw new IllegalStateException('window.event rewrite for other parents than GetProp, Call and If is not implemented yet on line ' + n.getLineno())
            }
        }
    }

    protected void updateFunction (Node n) {
        Node functionNode = getFuntionNode(n)
        if (functionNode!= null && !hasMarker(functionNode)) {
            //Update node
            NodeUtils.addArgument(functionNode, 'e')
            NodeUtils.prependFunction(functionNode, JSConstants.getEventNode())
            //set marker
            functionNode.setJSDocInfo(new Marker())
        }
    }

    protected Node getFuntionNode (Node n) {
        if (n.isFunction()) {
            return n
        } else if (n.getParent() != null){
            return getFuntionNode(n.getParent())
        } else {
            return null
        }
    }

    protected Boolean hasMarker (Node n) {
        if (n.isFunction() && n.getJSDocInfo() != null && n.getJSDocInfo() instanceof Marker) {
            return true
        }
        return false
    }

    class Marker extends JSDocInfo {
        static String MARKER = 'Function rewritten to replace window.event'

        Boolean functionRewritten () {
            return true
        }
    }
}
