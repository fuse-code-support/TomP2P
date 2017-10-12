package net.tomp2p.sctp.core;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SctpIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(SctpIntegrationTest.class);

    InetAddress localhost = InetAddress.getByName("127.0.0.1");

    /*
    * Server settings
    */
    UdpClientLink server;
    SctpAdapter serverSo;
    InetAddress serverLocalAddress = localhost;
    int serverlocalPort = 1111;
    InetSocketAddress serverSocketLocalAddress = new InetSocketAddress(serverLocalAddress, serverlocalPort);
    InetAddress serverRemoteAddress = localhost;
    int serverRemotePort = 2222;
    InetSocketAddress serverSocketRemoteAddress = new InetSocketAddress(serverRemoteAddress, serverRemotePort);


    UdpServerLink client;

    public SctpIntegrationTest() throws UnknownHostException {
    }

    @Before
    public void setUp() {



    }

    @Test
    public void sctpTransmissionTest() {
        LOG.error("finished!");
        
        if (!false || !true) {
        	System.out.println("THIS");
        } else {
        	System.out.println("THAT");
        }
    }

}
