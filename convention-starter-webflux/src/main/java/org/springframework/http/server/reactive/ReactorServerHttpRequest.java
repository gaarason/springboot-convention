/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.springframework.http.server.reactive;

import gaarason.convention.common.provider.LogProvider;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.ssl.SslHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServerRequest;

import javax.net.ssl.SSLSession;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

/**
 * org.springframework:spring-web:5.3.4 -> org.springframework.http.server.reactive.ReactorServerHttpRequest.class
 */

/**
 * Adapt {@link ServerHttpRequest} to the Reactor {@link HttpServerRequest}.
 *
 * @since 5.0
 */
class ReactorServerHttpRequest extends AbstractServerHttpRequest {

    private static final AtomicLong LOG_PREFIX_INDEX = new AtomicLong();

    private final HttpServerRequest request;

    private final NettyDataBufferFactory bufferFactory;

    public ReactorServerHttpRequest(HttpServerRequest request, NettyDataBufferFactory bufferFactory) throws URISyntaxException {

        super(ReactorServerHttpRequest.initUri(request), "", new NettyHeadersAdapter(request.requestHeaders()));
        Assert.notNull(bufferFactory, "DataBufferFactory must not be null");
        this.request = request;
        this.bufferFactory = bufferFactory;
    }

    private static URI initUri(HttpServerRequest request) throws URISyntaxException {
        Assert.notNull(request, "HttpServerRequest must not be null");
        return new URI(ReactorServerHttpRequest.resolveBaseUrl(request) + ReactorServerHttpRequest.resolveRequestUri(request));
    }

    private static URI resolveBaseUrl(HttpServerRequest request) throws URISyntaxException {
        String scheme = ReactorServerHttpRequest.getScheme(request);
        String header = request.requestHeaders().get(HttpHeaderNames.HOST);
        if (header != null) {
            int portIndex;
            if (header.startsWith("[")) {
                portIndex = header.indexOf(':', header.indexOf(']'));
            } else {
                portIndex = header.indexOf(':');
            }
            if (portIndex != -1) {
                try {
                    return new URI(scheme, null, header.substring(0, portIndex), Integer.parseInt(header.substring(portIndex + 1)), null, null, null);
                } catch (NumberFormatException ex) {
                    throw new URISyntaxException(header, "Unable to parse port", portIndex);
                }
            } else {
                return new URI(scheme, header, null, null);
            }
        } else {
            InetSocketAddress localAddress = request.hostAddress();
            Assert.state(localAddress != null, "No host address available");
            return new URI(scheme, null, localAddress.getHostString(), localAddress.getPort(), null, null, null);
        }
    }

    private static String getScheme(HttpServerRequest request) {
        return request.scheme();
    }

    private static String resolveRequestUri(HttpServerRequest request) {
        String uri = request.uri();
        for (int i = 0; i < uri.length(); i++) {
            char c = uri.charAt(i);
            if (c == '/' || c == '?' || c == '#') {
                break;
            }
            if (c == ':' && (i + 2 < uri.length())) {
                if (uri.charAt(i + 1) == '/' && uri.charAt(i + 2) == '/') {
                    for (int j = i + 3; j < uri.length(); j++) {
                        c = uri.charAt(j);
                        if (c == '/' || c == '?' || c == '#') {
                            return uri.substring(j);
                        }
                    }
                    return "";
                }
            }
        }
        return uri;
    }

    @Override
    public String getMethodValue() {
        return request.method().name();
    }

    @Override
    protected MultiValueMap<String, HttpCookie> initCookies() {
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        for (CharSequence name : request.cookies().keySet()) {
            for (Cookie cookie : request.cookies().get(name)) {
                HttpCookie httpCookie = new HttpCookie(name.toString(), cookie.value());
                cookies.add(name.toString(), httpCookie);
            }
        }
        return cookies;
    }

    @Override
    @Nullable
    public InetSocketAddress getLocalAddress() {
        return request.hostAddress();
    }

    @Override
    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return request.remoteAddress();
    }

    @Override
    @Nullable
    protected SslInfo initSslInfo() {
        Channel channel = ((Connection) request).channel();
        SslHandler sslHandler = channel.pipeline().get(SslHandler.class);
        if (sslHandler == null && channel.parent() != null) { // HTTP/2
            sslHandler = channel.parent().pipeline().get(SslHandler.class);
        }
        if (sslHandler != null) {
            SSLSession session = sslHandler.engine().getSession();
            return new DefaultSslInfo(session);
        }
        return null;
    }

    ///////////////////////////
    // 记录请求体
    ///////////////////////////
    private final StringBuilder bodyStringBuilder = new StringBuilder();

    @Override
    public Flux<DataBuffer> getBody() {

        LogProvider logProvider = LogProvider.getInstance();

        Flux<DataBuffer> fluxBody = request.receive().retain().map(bufferFactory::wrap);

        // http请求体日志
        if (logProvider.isLogHttpProviderReceivedRequestBody()) {
            return fluxBody.map(dataBuffer -> {
                bodyStringBuilder.append(StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer()));
                return dataBuffer;
            }).doFinally((s) -> logProvider.printHttpProviderReceivedRequestBodyLog(bodyStringBuilder::toString));
        } else {
            return fluxBody;
        }
    }

    // @Override
    // public Flux<DataBuffer> getBody() {
    // return this.request.receive().retain().map(this.bufferFactory::wrap);
    // }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getNativeRequest() {
        return (T) request;
    }

    @Override
    @Nullable
    protected String initId() {
        if (request instanceof Connection) {
            return ((Connection) request).channel().id().asShortText() + "-" + ReactorServerHttpRequest.LOG_PREFIX_INDEX.incrementAndGet();
        }
        return null;
    }

}
