package de.unigoettingen.sub.be.ous.lbs4.proxy.filters

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpRequest

import java.util.regex.Matcher

/**
 * Created by cmahnke on 24.02.15.
 */
@Log4j
@CompileStatic
class PrependContentFilter extends RegExContentFilter {
    String searchStart = '^(.*)$'
    String replacementSuffix = '\n$1'


    PrependContentFilter(String url, String search, String replacement) {
        super(url, search, replacement)
        this.search = searchStart
    }


    PrependContentFilter(String url, String replacement) {
        super(url, null, replacement)
        this.search = searchStart
    }

    @Override
    FullHttpResponse filter(HttpRequest request, FullHttpResponse fhr) {
        String content = getContent(fhr)
        log.trace("Checking content (${search}):\n${content}")
        Matcher m = contentPattern.matcher(content)
        String result = m.replaceAll(replacement + replacementSuffix)
        log.trace("Result of replacement(${replacement + replacementSuffix}):\n${result}")
        return updateResponse(fhr, result)
    }

}
