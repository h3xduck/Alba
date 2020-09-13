package com.marsanpat.alba.Controller;

import java.util.Arrays;

import static com.marsanpat.alba.Controller.MessageController.PROTOCOL_STANDARD_MESSAGE_LENGTH;

/**
 * The protocolBuilder is in charge of constructing messages compliant with the AlbaProtocol.
 */
public class ProtocolBuilder {

    /**
     * Constructs the message according to the AlbaProtocol.
     * Note that it does not return a PROTOCOL_STANDARD_MESSAGE_LENGTH-long message, this must be managed later when
     * writing to the stream.
     * @param header
     * @param content
     * @return
     */
    public String constructMessage(String header, String content){
        byte[] fillerArray = new byte[PROTOCOL_STANDARD_MESSAGE_LENGTH];
        Arrays.fill(fillerArray, (byte) 0);
        String result = header.concat(content).concat(ProtocolParser.PROTOCOL_SEPARATOR).concat(Arrays.toString(fillerArray));
        return result;
    }

    /**
     * Constructs the message according to the AlbaProtocol, but with the header and message already joined.
     * Note that it does not return a PROTOCOL_STANDARD_MESSAGE_LENGTH-long message, this must be managed later when
     * writing to the stream.
     * @param string
     * @return
     */
    public String constructMessage(String string){
        byte[] fillerArray = new byte[PROTOCOL_STANDARD_MESSAGE_LENGTH];
        Arrays.fill(fillerArray, (byte) 0);
        String result = string.concat(ProtocolParser.PROTOCOL_SEPARATOR).concat(Arrays.toString(fillerArray));
        return result;
    }

}
