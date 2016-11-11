package com.liferay.websocket.whiteboard.standalone.example.feedback.chat;

public class PeerImpl implements Peer {

	private String name;
	
	private String avatar;
	
	private String session;
	
	public PeerImpl(String name, String avatar, String session) {
		this.name = name;
		this.avatar = avatar;
		this.session = session;
	}

	public String getName() {
		return name;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getSession() {
		return session;
	}

}
