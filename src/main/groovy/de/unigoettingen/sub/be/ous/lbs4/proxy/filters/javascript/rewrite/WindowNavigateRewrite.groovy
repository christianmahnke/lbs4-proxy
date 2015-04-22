package de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite

import com.google.javascript.jscomp.NodeTraversal
import com.google.javascript.rhino.Node
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.getprop
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.name
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.str
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.call
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.expr_result

/**
 * Created by cmahnke on 16.04.15.
 */
@Log4j
@CompileStatic
class WindowNavigateRewrite extends BaseRewrite implements NodeTraversal.Callback {
    static String OBJECT = 'window'
    static String METHOD = 'navigate'

    public void visit(NodeTraversal t, Node n, Node parent) {
        if (n.isString() && parent.isGetProp() && n.getString() == METHOD && parent.getFirstChild().isString() && parent.getFirstChild().getString() == OBJECT) {
            Node call = parent.getParent()
            if (!call.isCall()) {
                throw new IllegalStateException('window.navigate rewrite failed, since no Call is given on line ' + n.getLineno())
            }
            Node location = call.getChildAtIndex(1)
            Node newCall
            Node windowLocationHref = getprop(getprop(name('window'), str('location')),str('href'))
            if (location.isString()) {
                String url = location.getString()
                log.info("JS: Got ${OBJECT}.${METHOD} for ${url}")
                newCall = de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.assign(windowLocationHref, str(url))
            } else if (location.isName()) {
                log.info("JS: Got ${OBJECT}.${METHOD} for variable ${location.getString()}")
                //newCall = de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.assign(windowLocationHref, name(location.getString())))
                newCall = de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.assign(windowLocationHref, location.cloneNode())
            } else {
                throw new IllegalStateException('window.navigate rewrite for other then String and Name not implemented yet on line ' + n.getLineno())
            }
            parent.getParent().getParent().replaceChild(call, newCall)

        }

    }
}