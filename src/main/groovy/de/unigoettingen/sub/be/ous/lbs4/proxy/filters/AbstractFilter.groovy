package de.unigoettingen.sub.be.ous.lbs4.proxy.filters

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.HttpRequest

import java.nio.charset.Charset
import java.util.regex.Pattern

/**
 * Created by cmahnke on 24.02.15.
 */
@Log4j
@CompileStatic
abstract class AbstractFilter implements Filter {
    String url
    Pattern urlPattern

    AbstractFilter (String url) {
        this.url = url
        urlPattern = Pattern.compile(url)
        log.trace("Set up abstract content filter with search for URL pattern ${url}")
    }

    @Override
    Boolean matches(HttpRequest request) {
        log.trace("Checking request for ${request.getUri()} with URL pattern ${url}")
        return urlPattern.matcher(request.getUri()).matches()
    }

    @Override
    String getMatchIdentifier() {
        return url
    }

    protected String byteBudToString (ByteBuf bb) {
        return bb.toString(Charset.defaultCharset())
    }

}
