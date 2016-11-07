package com.liferay.websocket.whiteboard.standalone.example.feedback.chat;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public Message decode(final String textMessage) throws DecodeException {

        JsonObject jsonObject = Json.createReader(new StringReader(textMessage)).readObject();

        Message message = new Message(jsonObject.getString("type"));
        
        if (Message.MESSAGE.equals(message.getType())) { 
	        message.setContent(jsonObject.getString("message"));
	        message.setSender(jsonObject.getString("sender"));
	        
	        try {
				message.setReceived(dateFormat.parse(jsonObject.getString("received")));
			} catch (Exception e) {
				message.setReceived(new Date());
			}
        }
        
        else if (Message.ADD_PEER.equals(message.getType()) || Message.REMOVE_PEER.equals(message.getType())) { 
        	message.setPeer(jsonObject.getString("peer"));
        	message.setAvatar(jsonObject.getString("avatar"));
        	message.setSession(jsonObject.getString("session"));
        }
        
        return message;
    }

	@Override
	public void destroy() {
		// NO OP
	}

	@Override
	public void init(EndpointConfig arg0) {
		// No OP
	}

	@Override
	public boolean willDecode(String testMessage) {
		return true;
	}
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("EEE h:mm a"); 

}