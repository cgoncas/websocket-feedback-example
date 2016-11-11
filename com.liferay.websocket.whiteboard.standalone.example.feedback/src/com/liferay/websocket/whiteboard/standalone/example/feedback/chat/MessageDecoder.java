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
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE h:mm a"); 

	@Override
	public Message decode(final String textMessage) throws DecodeException {

		JsonObject jsonObject = Json.createReader(new StringReader(textMessage)).readObject();

		String type = jsonObject.getString("type");
		
		if (Message.MESSAGE.equals(type)) {
			Date received = new Date();
			
			try {
				received = dateFormat.parse(jsonObject.getString("received"));
			}
			catch (Exception e) {
			}

			return MessageImpl.getSimpleMessage(
				jsonObject.getString("sender"),
				received, jsonObject.getString("message"));
		}
		else if (Message.ADD_PEER.equals(type)) {
			return MessageImpl.getAddPeerMessage(
				new PeerImpl(
					jsonObject.getString("peer"),
					jsonObject.getString("avatar"),
					jsonObject.getString("session")));
		}
		else if (Message.REMOVE_PEER.equals(type)) {
			return MessageImpl.getRemovePeerMessage(
				new PeerImpl(
					jsonObject.getString("peer"),
					jsonObject.getString("avatar"),
					jsonObject.getString("session")));
		}

		throw new DecodeException(textMessage, "The type " + type + " is not a valid Message type");
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
	
}