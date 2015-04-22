package de.unigoettingen.sub.be.ous.lbs4.proxy.filters

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpRequest

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by cmahnke on 20.04.15.
 */
@CompileStatic
@Log4j
class RequestRewriteFilter extends AbstractFilter implements URLFilter {
    String from
    String to

    Pattern urlPattern

    RequestRewriteFilter (String url) {
        super(url)
    }

    RequestRewriteFilter (String url, String from, String to) {
        super(url)
        urlPattern = Pattern.compile(url)
        log.trace("Set up RequestRewriteFilter with search for URL pattern ${url}")
        this.from = from
        urlPattern = Pattern.compile(from, Pattern.DOTALL)
        this.to = to
    }

    public String filter (final HttpRequest request) {
        String reqUri = request.getUri()
        Matcher m = urlPattern.matcher(reqUri)
        return m.replaceAll(to)
    }

    @Override
    Boolean matches(HttpRequest request) {
        log.trace("Checking request for ${request.getUri()} with URL pattern ${urlPattern}")
        return urlPattern.matcher(request.getUri()).matches()
    }

}
