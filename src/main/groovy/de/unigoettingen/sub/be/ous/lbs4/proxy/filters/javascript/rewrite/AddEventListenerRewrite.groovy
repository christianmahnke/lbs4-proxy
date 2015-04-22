package de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite

import com.google.javascript.jscomp.NodeTraversal
import com.google.javascript.rhino.Node
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.getprop
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.name
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.nil
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.str

/**
 * Created by cmahnke on 16.04.15.
 */
@Log4j
@CompileStatic
class AddEventListenerRewrite  extends BaseRewrite implements NodeTraversal.Callback {
    static String METHOD = 'attachEvent'

    public void visit(NodeTraversal t, Node n, Node parent) {

        if (n.isString() && parent.isGetProp() && n.getString() == METHOD) {
            Node target = parent.getChildAtIndex(0)
            String method = n.getString()
            Node call = parent.getParent()
            String rawEvent = call.getChildAtIndex(1).getString()
            String event
            if (rawEvent.startsWith('on')) {
                event = rawEvent.substring(2)
            } else {
                event = rawEvent
            }
            Node callback = call.getChildAtIndex(2)
            if (!target.isGetElem()) {
                log.info("JS: Found call for ${METHOD} on ${target.getString()} for event (on)${event}, callback ${callback.getString()}")
                //rewrite call
                Node getProp = getprop(name(target.getString()), str('addEventListener'))
                Node newCall = de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.call(getProp, (Node) str(event), (Node) name(callback.getString()), nil())
                parent.getParent().getParent().replaceChild(call, newCall)
            } else {
                String targetDesc = target.firstChild.getString()
                log.info("JS: Found call for ${METHOD} on element of array ${targetDesc} for event (on)${event}, callback ${callback.getString()}")
                Node getProp = getprop(target.cloneNode(), str('addEventListener'))
                Node newCall = de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.call(getProp, (Node) str(event), (Node) name(callback.getString()), nil())
            }
        }
    }
}
