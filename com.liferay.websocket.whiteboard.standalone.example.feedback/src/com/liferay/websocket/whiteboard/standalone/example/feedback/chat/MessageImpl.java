package com.liferay.websocket.whiteboard.standalone.example.feedback.chat;

import java.util.Date;

public class MessageImpl implements Message {

	private String content;

	private String sender;

	private Date received;

	private String type;

	private String peer;

	private String avatar;

	private String session;

	private MessageImpl(String type) { this.type = type;}

	public static Message getSimpleMessage(
		String sender, Date received, String content) {

		MessageImpl message = new MessageImpl( Message.MESSAGE);
		message.sender = sender;
		message.received = received;
		message.content = content;

		return message;
	}

	public static Message getAddPeerMessage(Peer peer) {
		MessageImpl message = new MessageImpl( Message.ADD_PEER);
		message.peer = peer.getName();
		message.avatar = peer.getAvatar();
		message.session = peer.getSession();

		return message;
	}

	public static Message getRemovePeerMessage(Peer peer) {
		MessageImpl message = new MessageImpl( Message.REMOVE_PEER);
		message.peer = peer.getName();
		message.avatar = peer.getAvatar();
		message.session = peer.getSession();

		return message;
	}

	public String getContent() {
		return content;
	}

	public String getSender() {
		return sender;
	}

	public Date getReceived() {
		return received;
	}

	public String getType() {
		return type;
	}

	public String getPeer() {
		return peer;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getSession() {
		return session;
	}

}
