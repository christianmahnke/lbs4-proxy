package de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite

import com.google.javascript.jscomp.NodeTraversal
import com.google.javascript.rhino.Node
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import org.apache.log4j.Level

/**
 * Created by cmahnke on 16.04.15.
 */
@Log4j
@CompileStatic
abstract class BaseRewrite implements NodeTraversal.Callback {
    Boolean shouldTraverse = true

    @Override
    public boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
        return shouldTraverse
    }

    /**
     * Just dump nodes to the logger
     * @param t
     * @param n
     * @param parent
     */

    @Override
    public void visit(NodeTraversal t, Node n, Node parent) {
        dumpNodes(n, parent)
    }

    public dumpNodes(Node n, Node parent) {
        if (parent != null) {
            log.info("${n.getType()}: Node ${n.toString(true, false, true)}, Parent ${parent.toString(true, false, true)}")
        } else {
            log.info("${n.getType()}: Node ${n.toString(true, false, true)}")
        }
    }

    protected logNodes(Node n, Node parent) {
        if (log.getLevel() == Level.TRACE) {
            dumpNodes(n, parent)
        }
    }
    public String failMessage (Node n) {
        return " on line ${n.getLineno()} (comments aren't counted)"
    }

}
