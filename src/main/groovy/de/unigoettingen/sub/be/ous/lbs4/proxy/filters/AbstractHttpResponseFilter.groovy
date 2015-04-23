package de.unigoettingen.sub.be.ous.lbs4.proxy.filters

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.DefaultHttpRequest
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion

import java.nio.charset.Charset
import java.util.regex.Matcher

/**
 * Created by cmahnke on 19.04.15.
 */
@Log4j
@CompileStatic
abstract class AbstractHttpResponseFilter extends AbstractFilter implements HttpResponseFilter {

    AbstractHttpResponseFilter(String url) {
        super(url)
    }

    public static FullHttpResponse updateResponse(FullHttpResponse resp, String content) {
        Charset charset = findCharset(content)
        //Readd content in declared Charset
        ByteBuf c = Unpooled.wrappedBuffer(content.getBytes(charset))
        HttpVersion v = resp.protocolVersion
        HttpResponseStatus s = resp.getStatus()
        FullHttpResponse response = new DefaultFullHttpResponse(v, s, c)
        log.trace("Created a new FullHttpResponse object with version ${v} and status ${s}")
        //Copy headers
        for (h in resp.headers()) {
            response.headers().add(h.key, h.value)
        }
        //Set Size
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes())
        log.trace('Copied headers')
        return response
    }

    public static String getContent(FullHttpResponse fhr) {
        Charset charset = findCharset(fhr.content().toString(Charset.defaultCharset()))
        return fhr.content().toString(charset)
    }

    protected HttpRequest updateHttpRequest(HttpRequest req, String uri) {
        HttpMethod m = req.getMethod()
        HttpVersion v = req.getProtocolVersion()
        return new DefaultHttpRequest(v, m, uri)
    }

    @Override
    FullHttpResponse filter(HttpRequest request, FullHttpResponse fhr) {
        String content = filter(getContent(fhr))
        return updateResponse(fhr, content)
    }

    protected static Charset findCharset(String str) {
        //Convert into UTF-8 for internal processing
        String charset = null
        for (String l : str.readLines()) {
            if (l.contains('charset=')) {
                charset = l.replaceAll('.*charset=([\\w-]*).*', '$1')
                log.debug("Got content in charset ${charset}")
            }
        }
        if (charset == null) {
            return Charset.defaultCharset()
        } else if (Charset.isSupported(charset)) {
            return Charset.forName(charset)
        } else {
            throw new IllegalStateException("Charset ${charset} is not supported")
        }
    }

    abstract String filter(String str)
}
