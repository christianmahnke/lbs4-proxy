package de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite

import com.google.javascript.jscomp.AbstractCompiler
import com.google.javascript.jscomp.NodeTraversal
import com.google.javascript.jscomp.SourceFile
import com.google.javascript.rhino.Node
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.ClosureCompilerUtils

/**
 * Created by cmahnke on 17.04.15.
 */
class RewriteContainer {
    List<BaseRewrite> rewriter = new ArrayList<BaseRewrite>()
    Boolean changed = false

    RewriteContainer() {

    }

    public String rewrite(String content) {
        SourceFile sf = ClosureCompilerUtils.getSource('rewrite-string.js', content)
        return rewrite(sf)
    }

    public String rewrite(InputStream is) {
        SourceFile sf = ClosureCompilerUtils.getSource('rewrite-stream.js', is)
        return rewrite(sf)
    }

    public String rewrite(SourceFile sf) {
        Node node = ClosureCompilerUtils.parse(sf)
        String result = ''

        for (r in rewriter) {
            NodeTraversal.traverse(ClosureCompilerUtils.getCompiler(), node, r)
        }
        return ClosureCompilerUtils.toSource(node)
    }

    public void addRewite(BaseRewrite r) {
        rewriter.add(r)
        changed = true
    }

    private BaseRewrite combineRewriter() {
        BaseRewrite br = new BaseRewrite() {
            public void visit(NodeTraversal t, Node n, Node parent) { }
        }
        return br
    }
}
