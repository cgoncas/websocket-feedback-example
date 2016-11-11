package com.liferay.websocket.whiteboard.standalone.example.feedback.chat;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;

public class MessageEncoder implements Encoder.Text<Message> {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE h:mm a"); 

	@Override
	public String encode(final Message message) throws EncodeException {
		if (Message.MESSAGE.equals(message.getType())) {
			return Json.createObjectBuilder()
			 	   .add("type", message.getType())
					.add("message", message.getContent())
					.add("sender", message.getSender())
					.add("received", dateFormat.format(message.getReceived()))
					.build().toString();
		}
		
		if (Message.ADD_PEER.equals(message.getType()) || Message.REMOVE_PEER.equals(message.getType())) {
			return Json.createObjectBuilder()
			 		.add("type", message.getType())
					.add("peer", message.getPeer())
					.add("avatar", message.getAvatar())
					.add("session", message.getSession())
					.build().toString();
		}
		
		throw new EncodeException(message, "The Message doesn't have a valid type " + message.getType());
	}

	@Override
	public void destroy() {
		// NO OP
	}

	@Override
	public void init(EndpointConfig arg0) {
		// NO OP
	}
	
}
