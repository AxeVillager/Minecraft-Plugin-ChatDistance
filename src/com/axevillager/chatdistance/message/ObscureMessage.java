package com.axevillager.chatdistance.message;

import com.axevillager.chatdistance.configuration.Configuration;
import com.axevillager.chatdistance.player.CustomPlayer;

/**
 * ObscureMessage created by AxeVillager on 2018/05/21.
 */

public class ObscureMessage {

    private final String originalMessage;
    private final String obscureMessage;
    private final CustomPlayer sender;
    private final CustomPlayer receiver;
    private final double chatRange;
    private final double distance;

    public ObscureMessage(final String originalMessage, final CustomPlayer from, final CustomPlayer receiver, final double chatRange, final double distance) {
        this.originalMessage = originalMessage;
        this.sender = from;
        this.receiver = receiver;
        this.chatRange = chatRange;
        this.distance = distance;
        obscureMessage = createObscureMessage();
    }

    private String createObscureMessage() {
        final double noiseRange = chatRange / Configuration.OBSCURE_CHAT_RANGE_DIVISOR;
        final double noiseDistance = distance - (chatRange - noiseRange);
        final double obscurityPercentage = noiseDistance / (noiseRange * Configuration.OBSCURE_CHAT_PERCENTAGE_AMPLIFIER);
        final StringBuilder result = new StringBuilder();

        if (distance <= chatRange) {
            int ignoreIndex = 0;
            char[] messageArray = originalMessage.toCharArray();
            for (int i = 0; i < messageArray.length; i++) {
                final double rnd = Math.random();
                char character = messageArray[i];
                if (character == '§') {
                    ignoreIndex = i + 1;
                }
                if (rnd <= obscurityPercentage && character != '§' && character != messageArray[ignoreIndex]) {
                    character = ' ';
                }
                result.append(character);
            }
        }
        return new String (result);
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public String getObscureMessage() {
        return obscureMessage;
    }

    public CustomPlayer getSender() {
        return sender;
    }

    public CustomPlayer getReceiver() {
        return receiver;
    }

    public double getChatRange() {
        return chatRange;
    }

    public double getDistance() {
        return distance;
    }
}
