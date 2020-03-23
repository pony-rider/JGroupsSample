package com.example;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableScheduling
public class JGroupsApplication implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JChannel channel;

    private JChannel getUDPChannel() throws Exception {
        List<Protocol> protocolStack = Arrays.asList(
                new UDP().setMcastPort(45588),
                new PING(),
                new MERGE3().setMaxInterval(30000L).setMinInterval(1000),
                new FD_SOCK(),
                new FD_ALL(),
                new VERIFY_SUSPECT().setTimeout(1500),
                new BARRIER(),
                new NAKACK2(),
                new UNICAST3(),
                new STABLE(),
                new GMS(),
                new MFC(),
                new FRAG2(),
                new RSVP(),
                new STATE_TRANSFER()
        );
        channel = new JChannel(protocolStack);
        return channel;
    }

    public static void main(String[] args) {
        SpringApplication.run(JGroupsApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        JChannel channel = getUDPChannel();
        channel.setDiscardOwnMessages(true);
        channel.setReceiver(new ReceiverAdapter() {
            @Override
            public void receive(Message msg) {
                System.out.println("received msg from " + msg.getSrc() + ": " + msg.getObject());
            }
        });
        channel.connect("ChatCluster");
    }

    @Scheduled(fixedRate = 5000, initialDelay = 1000)
    public void sendMessage() throws Exception {
        String message = UUID.randomUUID().toString();
        logger.info("Send message '{}'", message);
        Message msg = new Message(null, message);
        channel.send(msg);
    }
}
