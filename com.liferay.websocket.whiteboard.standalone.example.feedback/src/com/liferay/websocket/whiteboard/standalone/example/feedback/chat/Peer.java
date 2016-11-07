package com.liferay.websocket.whiteboard.standalone.example.feedback.chat;

import javax.websocket.Session;

public class Peer {
	
	private String name;
	
	private String avatar;
	
	private Session session;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}
