package de.unigoettingen.sub.be.ous.lbs4.proxy

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpRequest

import org.littleshoot.proxy.HttpFilters
import org.littleshoot.proxy.HttpFiltersSourceAdapter
import org.littleshoot.proxy.HttpProxyServer
import org.littleshoot.proxy.impl.DefaultHttpProxyServer

/**
 * Created by cmahnke on 12.02.15.
 *
 * Requirements for this Proxy
 *
 */
@Log4j
@CompileStatic
class LBS4Proxy {
    static void main(args) {
        log.info('Starting Proxy server')
        HttpProxyServer server =
                DefaultHttpProxyServer.bootstrap()
                        .withPort(8080)
                        .withFiltersSource(new HttpFiltersSourceAdapter() {
                    @Override
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new LBS4HttpFiltersAdapter(originalRequest)
                    }
                    @Override
                    public int getMaximumRequestBufferSizeInBytes() {
                        return 1024*1024
                    }
                    @Override
                    public int getMaximumResponseBufferSizeInBytes() {
                        return 1024*1024
                    }

                })
                        .start()
    }
}
