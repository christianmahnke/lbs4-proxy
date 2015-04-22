package de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite

import com.google.javascript.rhino.Node
import groovy.transform.CompileStatic

import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.block
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.getprop
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.iF
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.name
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.not
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.str
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.var

/**
 * Created by cmahnke on 15.04.15.
 */
@CompileStatic
class JSConstants {

    public static Node getEventNode() {

        //Construct 'if(!e)var e = window.event'
        Node block = block(var(name('e', getprop(name('window'), str('event')))))
        return iF(not(name('e')), block)

        /* Without DSL
        //required nodes for 'e = e || window.event;'
        Node expr_result = new Node(130)
        Node assign = assign()

        //Set up nodes
        expr_result.addChildToBack(assign)
        assign.addChildToBack(name('e'))
        assign.addChildToBack(or())
        or.addChildrenToBack(name('e'))
        or.addChildrenToBack(getprop(name('window'), str('event')))

        return expr_result
        */
    }
}
