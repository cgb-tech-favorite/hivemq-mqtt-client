/*
 * Copyright 2018 dc-square and the HiveMQ MQTT Client Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hivemq.client.internal.mqtt.handler;

import com.hivemq.client.internal.mqtt.MqttClientConfig;
import com.hivemq.client.internal.mqtt.MqttClientSslConfigImpl;
import com.hivemq.client.internal.mqtt.codec.encoder.MqttEncoder;
import com.hivemq.client.internal.mqtt.handler.auth.MqttAuthHandler;
import com.hivemq.client.internal.mqtt.handler.connect.MqttConnAckSingle;
import com.hivemq.client.internal.mqtt.handler.connect.MqttConnectHandler;
import com.hivemq.client.internal.mqtt.handler.disconnect.MqttDisconnectHandler;
import com.hivemq.client.internal.mqtt.handler.ssl.SslUtil;
import com.hivemq.client.internal.mqtt.handler.websocket.MqttWebSocketInitializer;
import com.hivemq.client.internal.mqtt.ioc.ConnectionScope;
import com.hivemq.client.internal.rx.SingleFlow;
import com.hivemq.client.mqtt.MqttWebSocketConfig;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import dagger.Lazy;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.net.ssl.SSLException;
import java.net.URISyntaxException;

/**
 * Initializes:
 * <ul>
 * <li>the SSL handlers (optional)</li>
 * <li>the WebSocket handlers (optional)</li>
 * <li>the basic MQTT handlers: Encoder, AuthHandler, ConnectHandler, DisconnectHandler</li>
 * </ul>
 *
 * @author Silvio Giebl
 * @author David Katz
 */
@ConnectionScope
public class MqttChannelInitializer extends ChannelInitializer<Channel> {

    private final @NotNull MqttClientConfig clientConfig;
    private final @NotNull SingleFlow<Mqtt5ConnAck> connAckFlow;

    private final @NotNull MqttEncoder encoder;
    private final @NotNull MqttConnectHandler connectHandler;
    private final @NotNull MqttDisconnectHandler disconnectHandler;
    private final @NotNull MqttAuthHandler authHandler;

    private final @NotNull Lazy<MqttWebSocketInitializer> webSocketInitializer;

    @Inject
    MqttChannelInitializer(
            final @NotNull MqttClientConfig clientConfig, final @NotNull SingleFlow<Mqtt5ConnAck> connAckFlow,
            final @NotNull MqttEncoder encoder, final @NotNull MqttConnectHandler connectHandler,
            final @NotNull MqttDisconnectHandler disconnectHandler, final @NotNull MqttAuthHandler authHandler,
            final @NotNull Lazy<MqttWebSocketInitializer> webSocketInitializer) {

        this.clientConfig = clientConfig;
        this.connAckFlow = connAckFlow;
        this.encoder = encoder;
        this.connectHandler = connectHandler;
        this.disconnectHandler = disconnectHandler;
        this.authHandler = authHandler;
        this.webSocketInitializer = webSocketInitializer;
    }

    @Override
    protected void initChannel(final @NotNull Channel channel) throws Exception {
        final MqttClientSslConfigImpl sslConfig = clientConfig.getRawSslConfig();
        if (sslConfig != null) {
            initSsl(channel, sslConfig);
        }
        final MqttWebSocketConfig webSocketConfig = clientConfig.getRawWebSocketConfig();
        if (webSocketConfig != null) {
            initWebSocketMqtt(channel, webSocketConfig);
        } else {
            initMqtt(channel);
        }
    }

    public void initMqtt(final @NotNull Channel channel) {
        channel.pipeline().addLast(MqttEncoder.NAME, encoder)
                .addLast(MqttAuthHandler.NAME, authHandler)
                .addLast(MqttConnectHandler.NAME, connectHandler)
                .addLast(MqttDisconnectHandler.NAME, disconnectHandler);
    }

    private void initWebSocketMqtt(final @NotNull Channel channel, final @NotNull MqttWebSocketConfig webSocketConfig)
            throws URISyntaxException {

        webSocketInitializer.get().initChannel(channel, webSocketConfig);
    }

    private void initSsl(final @NotNull Channel channel, final @NotNull MqttClientSslConfigImpl sslConfig)
            throws SSLException {

        SslUtil.initChannel(channel, sslConfig);
    }

    @Override
    public void exceptionCaught(final @NotNull ChannelHandlerContext ctx, final @NotNull Throwable cause) {
        ctx.close();
        MqttConnAckSingle.onError(clientConfig, connAckFlow, cause);
    }
}
