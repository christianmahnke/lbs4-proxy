package de.unigoettingen.sub.be.ous.lbs4.proxy.filters

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by cmahnke on 23.02.15.
 */
@Log4j
@CompileStatic
class RegExContentFilter extends AbstractHttpResponseFilter {
    String search
    String replacement
    Pattern contentPattern

    RegExContentFilter (String url, String search, String replacement) {
        super(url)
        this.search = search
        this.replacement = replacement

        contentPattern = Pattern.compile(search, Pattern.DOTALL)
        log.trace("Set up content filter with search ${search}, ${replacement} for URL ${url}")
    }

    /*
    @Override
    FullHttpResponse filter(HttpRequest request, FullHttpResponse fhr) {
        String content = getContent(fhr)
        log.trace("Checking content (${search}):\n${content}")
        Matcher m = contentPattern.matcher(content)
        String result =  m.replaceAll(replacement)
        log.trace("Result of replacement(${replacement}):\n${result}")
        return updateResponse(fhr, result)
    }
    */

    @Override
    String filter (String content) {
        Matcher m = contentPattern.matcher(content)
        return m.replaceAll(replacement)
    }

}
