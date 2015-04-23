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
    String filter (String content) {
        Matcher m = contentPattern.matcher(content)
        return m.replaceAll(replacement + replacementSuffix)
    }

}
