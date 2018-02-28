package org.mqttbee.api.mqtt.mqtt5.message.publish.puback;

import org.mqttbee.annotations.NotNull;
import org.mqttbee.annotations.Nullable;
import org.mqttbee.api.mqtt.mqtt5.message.Mqtt5ReasonCode;
import org.mqttbee.mqtt.message.MqttCommonReasonCode;

/**
 * MQTT Reason Codes that can be used in PUBACK packets according to the MQTT 5 specification.
 *
 * @author Silvio Giebl
 */
public enum Mqtt5PubAckReasonCode implements Mqtt5ReasonCode {

    SUCCESS(MqttCommonReasonCode.SUCCESS),
    NO_MATCHING_SUBSCRIBERS(MqttCommonReasonCode.NO_MATCHING_SUBSCRIBERS),
    UNSPECIFIED_ERROR(MqttCommonReasonCode.UNSPECIFIED_ERROR),
    IMPLEMENTATION_SPECIFIC_ERROR(MqttCommonReasonCode.IMPLEMENTATION_SPECIFIC_ERROR),
    NOT_AUTHORIZED(MqttCommonReasonCode.NOT_AUTHORIZED),
    TOPIC_NAME_INVALID(MqttCommonReasonCode.TOPIC_NAME_INVALID),
    PACKET_IDENTIFIER_IN_USE(MqttCommonReasonCode.PACKET_IDENTIFIER_IN_USE),
    QUOTA_EXCEEDED(MqttCommonReasonCode.QUOTA_EXCEEDED),
    PAYLOAD_FORMAT_INVALID(MqttCommonReasonCode.PAYLOAD_FORMAT_INVALID);

    private final int code;

    Mqtt5PubAckReasonCode(final int code) {
        this.code = code;
    }

    Mqtt5PubAckReasonCode(@NotNull final MqttCommonReasonCode reasonCode) {
        this(reasonCode.getCode());
    }

    /**
     * @return the byte code of this PUBACK Reason Code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the PUBACK Reason Code belonging to the given byte code.
     *
     * @param code the byte code.
     * @return the PUBACK Reason Code belonging to the given byte code or null if the byte code is not a valid PUBACK
     * Reason Code code.
     */
    @Nullable
    public static Mqtt5PubAckReasonCode fromCode(final int code) {
        for (final Mqtt5PubAckReasonCode reasonCode : values()) {
            if (reasonCode.code == code) {
                return reasonCode;
            }
        }
        return null;
    }

}