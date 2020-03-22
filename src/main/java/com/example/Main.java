package com.example;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class Main {
    public static void main(String[] args) throws Exception {
        JChannel channel = new JChannel();
        channel.setReceiver(new ReceiverAdapter() {
            public void receive(Message msg) {
                System.out.println("received msg from " + msg.getSrc() + ": " + msg.getObject());
            }
        });
        channel.connect("MyCluster");
        channel.send(new Message(null, "hello world"));
        //channel.close();

    }
}
