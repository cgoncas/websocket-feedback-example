package com.liferay.websocket.whiteboard.standalone.example.feedback.chat;

import java.util.Date;

public interface Message {

	String MESSAGE = "message";

	String ADD_PEER = "add-peer";

	String REMOVE_PEER = "remove-peer";

	String[] AVATARS = {"avatar-deer", "avatar-bear", "avatar-owl", "avatar-fox", "avatar-dog", "avatar-bird", "avatar-bee", "avatar-panda", "avatar-monkey"};

	String getContent();

	String getSender();

	Date getReceived();

	String getType();

	String getPeer();

	String getAvatar();

	String getSession();

}
