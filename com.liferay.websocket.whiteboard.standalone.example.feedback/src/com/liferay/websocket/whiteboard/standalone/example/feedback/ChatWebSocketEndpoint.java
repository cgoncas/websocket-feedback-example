package com.liferay.websocket.whiteboard.standalone.example.feedback;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.liferay.websocket.whiteboard.standalone.example.feedback.chat.Message;
import com.liferay.websocket.whiteboard.standalone.example.feedback.chat.Peer;

/**
 * @author Cristina González
 */
public class ChatWebSocketEndpoint extends Endpoint {

	private static Map<Session, Peer> peers = new ConcurrentHashMap<>();
	
	private static final Logger logger = Logger.getLogger(ChatWebSocketEndpoint.class.getName());

	@Override
	public void onOpen(final Session session, EndpointConfig endpointConfig) {		
		List<String> userNames = session.getRequestParameterMap().get("userName");

		String userName = userNames.get(0);

		sendMessage(session, welcomeMessage(userName));
		
		Peer peer = new Peer();
		
		peer.setName(userName);
		peer.setSession(session);
		peer.setAvatar(Message.AVATARS[(new Random()).nextInt(Message.AVATARS.length)]);

		System.out.println(userName + " join the chat room.");
		
		Message joinMessage = joinMessage(userName);

		Message peerMessage = addPeer(peer);

		for (Peer peerValue : peers.values()) {
			sendMessage(peerValue.getSession(), joinMessage);
			sendMessage(peerValue.getSession(), peerMessage);
			sendMessage(session, addPeer(peerValue));
		}
		
		sendMessage(session, addPeer(peer));

		peers.put(session, peer);
		
		session.addMessageHandler(new MessageHandler.Whole<Message>() {

			@Override
			public void onMessage(Message message) {
				for (Session peerSession : peers.keySet()) {
					sendMessage(peerSession, message);
				}
			}
		});
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		Peer peer = peers.get(session);

		System.out.println(peer.getName() + " left the room");
		
		peers.remove(session);

		for (Session peerSession : peers.keySet()) {
			sendMessage(peerSession, leftMessage(peer.getName()));

			sendMessage(peerSession, removePeer(peer));
		}
	}
	
	private static void sendMessage(Session session, Message message) {
		try {
			session.getBasicRemote().sendObject(message);
		} 
		catch (IOException ie) {			
			logger.log(Level.SEVERE , "Can't send the message " + message, ie);
		} catch (EncodeException ee) {
			logger.log(Level.SEVERE , ee.getMessage(), ee);
		}
	}
	
	private static Message addPeer(Peer peer) {
		Message message = new Message(Message.ADD_PEER);
		
		message.setPeer(peer.getName());
		message.setAvatar(peer.getAvatar());
		message.setSession(peer.getSession().getId());

		return message;
	}
	
	private static Message removePeer(Peer peer) {
		Message message = new Message(Message.REMOVE_PEER);
		
		message.setPeer(peer.getName());
		message.setAvatar(peer.getAvatar());
		message.setSession(peer.getSession().getId());

		return message;
	}

	private static Message welcomeMessage(String userName) {
		Message message = new Message(Message.MESSAGE);

		message.setContent("Welcome " + userName);
		message.setSender("@CGCastellano");
		message.setReceived(new Date());

		return message;
	}

	private static Message leftMessage(String userName) {
		Message message = new Message(Message.MESSAGE);

		message.setContent(userName + " left the MODCONF Presentation Chat");
		message.setSender(userName);
		message.setReceived(new Date());

		return message;
	}

	private static Message joinMessage(String userName) {
		Message message = new Message(Message.MESSAGE);

		message.setContent(userName + " join the MODCONF Presentation Chat");
		message.setSender(userName);
		message.setReceived(new Date());

		return message;
	}
	
}
