package de.unigoettingen.sub.be.ous.lbs4.proxy.filters

import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.BaseRewrite
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.RewriteContainer

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpRequest

import java.nio.charset.Charset

/**
 * Created by cmahnke on 24.02.15.
 */
@Log4j
@TypeChecked
class JavaScriptContentFilter extends AbstractHttpResponseFilter {

    RewriteContainer rewriter = new RewriteContainer()

    JavaScriptContentFilter(String url) {
        super(url)
    }

    @Override
    FullHttpResponse filter(HttpRequest request, FullHttpResponse fhr) {
        String content = getContent(fhr)
        //log.trace("Checking content (JS:\n${content}")
        String result = ''
        try {
            result = rewriter.rewrite(content)
        } catch (Exception e) {
            log.error("JavaScript rewrite failed for ! ${request.getUri()}", e)
            throw new IllegalStateException("JS: Rewrite of ${request.getUri()} failed", e)
        }
        return updateResponse(fhr, result)
    }

    public void addRewite (BaseRewrite r) {
        rewriter.addRewite(r)
    }

    @Override
    String filter (String content) {
        throw new IllegalStateException('Not implemented')
        //log.trace("Checking content (JS:\n${content}")
        String result = ''
        try {
            result = rewriter.rewrite(content)
        } catch (Exception e) {
            log.error("JavaScript rewrite failed", e)
            throw new IllegalStateException("JS: Rewrite failed", e)
        }
        return result
    }

}
