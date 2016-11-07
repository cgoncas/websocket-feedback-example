package com.liferay.websocket.whiteboard.standalone.example.feedback.chat;

import java.util.Date;

public class Message {
	
	public static final String MESSAGE = "message";

	public static final String ADD_PEER = "add-peer";

	public static final String REMOVE_PEER = "remove-peer";
	
	public static final String[] AVATARS = {"avatar-deer", "avatar-bear", "avatar-owl", "avatar-fox", "avatar-dog", "avatar-bird", "avatar-bee", "avatar-panda", "avatar-monkey"};
	
	public Message(String message) {
		type = message;
	}
	
	private String content;
    
	private String sender;
    
    private Date received;
    
    private String type;
    
    private String peer;
    
    private String avatar;
    
    private String session; 
	
    public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public Date getReceived() {
		return received;
	}
	public void setReceived(Date received) {
		this.received = received;
	}
	public String getType() {
		return type;
	}
	public String getPeer() {
		return peer;
	}
	public void setPeer(String peer) {
		this.peer = peer;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}

}
