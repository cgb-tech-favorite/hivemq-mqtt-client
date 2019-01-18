/*
 * Copyright 2018 The MQTT Bee project
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

package org.mqttbee.internal.mqtt.handler.subscribe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.internal.mqtt.handler.publish.incoming.MqttSubscribedPublishFlow;
import org.mqttbee.internal.mqtt.message.subscribe.MqttStatefulSubscribe;
import org.mqttbee.internal.mqtt.message.subscribe.MqttSubscribe;
import org.mqttbee.internal.mqtt.message.subscribe.suback.MqttSubAck;

/**
 * @author Silvio Giebl
 */
class MqttSubscribeWithFlow extends MqttSubOrUnsubWithFlow {

    private final @NotNull MqttSubscribe subscribe;
    private final @NotNull MqttSubscriptionFlow<MqttSubAck> flow;

    MqttSubscribeWithFlow(
            final @NotNull MqttSubscribe subscribe, final @NotNull MqttSubscriptionFlow<MqttSubAck> flow) {

        this.subscribe = subscribe;
        this.flow = flow;
    }

    @NotNull MqttSubscribe getSubscribe() {
        return subscribe;
    }

    @Override
    @NotNull MqttSubscriptionFlow<MqttSubAck> getFlow() {
        return flow;
    }

    @Nullable MqttSubscribedPublishFlow getPublishFlow() {
        return (flow instanceof MqttSubscribedPublishFlow) ? (MqttSubscribedPublishFlow) flow : null;
    }

    static class Stateful extends MqttSubOrUnsubWithFlow.Stateful {

        private final @NotNull MqttStatefulSubscribe subscribe;
        private final @NotNull MqttSubscriptionFlow<MqttSubAck> flow;

        Stateful(final @NotNull MqttStatefulSubscribe subscribe, final @NotNull MqttSubscriptionFlow<MqttSubAck> flow) {
            this.subscribe = subscribe;
            this.flow = flow;
        }

        @NotNull MqttStatefulSubscribe getSubscribe() {
            return subscribe;
        }

        @Override
        @NotNull MqttSubscriptionFlow<MqttSubAck> getFlow() {
            return flow;
        }

        @Nullable MqttSubscribedPublishFlow getPublishFlow() {
            return (flow instanceof MqttSubscribedPublishFlow) ? (MqttSubscribedPublishFlow) flow : null;
        }
    }
}
