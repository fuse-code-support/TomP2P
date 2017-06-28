package net.tomp2p.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

public class RconController {

	private RconView rconView;

	public void start() {
		rconView = new RconView();
		rconView.make();
		rconView.getJFrame().setSize(700, 400);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				System.out.println("useless, because Hook runs in a separate Thread...");
			}
		}, "Shutdown-thread"));

		rconView.getSendTestMessageButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Test message to masterpeer - Button pressed");
				try {
					SimpleSctpClient.sendDummy("Test message to masterpeer", null, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		rconView.getSendDirectedMessageButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Directed message - Button pressed");
				if (rconView.getIpField().getText() == null || rconView.getIdField().getText() == null) {
					System.out.println("either ipfield or idfield is null");
					return;
				} else {
					try {
						SimpleSctpClient.sendDummy(rconView.getMessageField().getText(), rconView.getIdField().getText(), rconView.getIpField().getText());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		rconView.getSendDirectedNatPeerButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Reverse connection setup - Button pressed");
			}
		});

		rconView.getPermanentPeerConnectionButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Permanent PeerConnection - Button pressed");
				try {
					//SimpleSctpClient.connectFirst(rconView.getMessageField().getText());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}
}
