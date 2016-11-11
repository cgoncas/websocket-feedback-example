package com.liferay.websocket.whiteboard.standalone.example.feedback;

import com.liferay.websocket.whiteboard.standalone.example.feedback.chat.MessageImpl;
import com.liferay.websocket.whiteboard.standalone.example.feedback.chat.PeerImpl;
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

	private static Map<Peer, Session> peers = new ConcurrentHashMap<>();
	
	private static final Logger logger = Logger.getLogger(ChatWebSocketEndpoint.class.getName());

	@Override
	public void onOpen(final Session session, EndpointConfig endpointConfig) {
		List<String> userNames = session.getRequestParameterMap().get("userName");

		session.addMessageHandler( new MessageHandler.Whole<Message>() {
			
			@Override
			public void onMessage(Message message) {
				for (Map.Entry<Peer, Session> peerEntry : peers.entrySet()) {
					sendMessage(peerEntry.getValue(), message);
				}
			}
		});

		Peer peer = new PeerImpl(
			userNames.get(0),
			Message.AVATARS[(new Random()).nextInt(Message.AVATARS.length)],
			session.getId());

		sendJoinMessages(session, peer);
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		Peer peer = getPeer(session);

		System.out.println(peer.getName() + " left the room " + closeReason.getReasonPhrase());

		peers.remove(peer);

		for (Session peerSession : peers.values()) {
			sendMessage(
				peerSession,
				MessageImpl.getSimpleMessage(
				peer.getName(),
				new Date(),
				peer.getName() + " left the MODCONF Presentation Chat"));

			sendMessage(
				peerSession, MessageImpl.getRemovePeerMessage(peer));
		}
	}
	
	private static void sendMessage(Session session, Message message) {
		try {
			session.getBasicRemote().sendObject(message);
		} 
		catch (IOException ie) {
			logger.log(Level.SEVERE , "Can't send the message " + message, ie);
		}
		catch (EncodeException ee) {
			logger.log(Level.SEVERE , ee.getMessage(), ee);
		}
	}

	private static Peer getPeer(Session session) {
		for (Map.Entry<Peer, Session> peerEntry : peers.entrySet()) {
			if (session.getId().equals(peerEntry.getValue().getId())) {
				return peerEntry.getKey();
			}
		}

		return null;
	}

	private void sendJoinMessages(Session session, Peer peer) {
		sendMessage(
			session,
			MessageImpl.getSimpleMessage(
				"System", new Date(), "Welcome " + peer.getName()));

		System.out.println(peer.getName() + " join the chat room.");

		for (Map.Entry<Peer, Session> peerEntry : peers.entrySet()) {
			sendMessage(
				peerEntry.getValue(),
				MessageImpl.getSimpleMessage(
					peer.getName(),
					new Date(),
					peer.getName() + " join the MODCONF Presentation Chat"));

			sendMessage(
				peerEntry.getValue(), MessageImpl.getAddPeerMessage(peer));

			sendMessage(session, MessageImpl.getAddPeerMessage(peerEntry.getKey()));
		}

		sendMessage(session, MessageImpl.getAddPeerMessage(peer));

		peers.put(peer, session);
	}

}
