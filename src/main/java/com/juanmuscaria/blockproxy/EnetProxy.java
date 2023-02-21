package com.juanmuscaria.blockproxy;

import com.juanmuscaria.blockproxy.jna.enet.Enet;
import com.juanmuscaria.blockproxy.jna.enet.structures.*;
import com.juanmuscaria.blockproxy.jna.types.Size_t;
import com.juanmuscaria.blockproxy.jna.types.Uint16_t;
import com.juanmuscaria.blockproxy.jna.types.Uint32_t;
import com.juanmuscaria.blockproxy.jna.types.Uint8_t;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EnetProxy implements Runnable{
    private static final int CHANNELS = 100; //Define a quantidade maxima de canais a ser usados.
    private static final int INCOMING_BANDWIDTH = 0;//Define o tamanho do tráfego de entrada, deixe 0 para qualquer tamanho.
    private static final int OUTGOING_BANDWIDTH = 0;//Define o tamanho do tráfego de saida, deixe 0 para qualquer tamanho.
    private static final int MAX_CONNECTIONS = 16;//Define o maximo de peers (jogadores) que podem conectar com o servidor

    ENetHost server;
    ENetHost client;
    ENetPeer serverPeer;
    ENetPeer clientPeer;
    private final ENetEvent eNetEvent = new ENetEvent();
    public ConcurrentLinkedQueue<String> injectedToServer = new ConcurrentLinkedQueue<>();
    public ConcurrentLinkedQueue<String> injectedToClient = new ConcurrentLinkedQueue<>();
    public boolean dumpClient = true;
    public boolean dumpServer = true;

    public EnetProxy() {
        ENetAddress eNetAddress = new ENetAddress();
        eNetAddress.port = new Uint16_t(15151);
        eNetAddress.host = new Uint32_t(Enet.ENET_HOST_ANY);
        server = Enet.INSTANCE.enet_host_create(eNetAddress,new Size_t(MAX_CONNECTIONS),new Size_t(CHANNELS),new Uint32_t(INCOMING_BANDWIDTH),new Uint32_t(OUTGOING_BANDWIDTH));
    }

    @Override
    public void run() {
        while (true) {
            while (Enet.INSTANCE.enet_host_service(server, eNetEvent,new Uint32_t(100)) != 0) {
                if (eNetEvent.type == Enet.ENetEventType.ENET_EVENT_TYPE_CONNECT){
                    System.out.println("Client connected.");
                    ENetAddress eNetAddress = new ENetAddress();
                    eNetAddress.port = new Uint16_t(BlockProxy.PORT);
                    System.out.println(Enet.INSTANCE.enet_address_set_host_ip(eNetAddress, BlockProxy.IP));
                    client = Enet.INSTANCE.enet_host_create(null ,new Size_t(1),new Size_t(2),new Uint32_t(0),new Uint32_t(0));
                    serverPeer = Enet.INSTANCE.enet_host_connect(client, eNetAddress, new Size_t(2), new Uint32_t(0));
                    clientPeer = eNetEvent.peer;
                }
                else if (eNetEvent.type== Enet.ENetEventType.ENET_EVENT_TYPE_RECEIVE){
                    if (dumpClient) System.out.printf("Client to Server: %s \n", Utils.bytesToHex(eNetEvent.packet.data.getByteArray(0, eNetEvent.packet.dataLength.intValue())));
                    Enet.INSTANCE.enet_peer_send(client.peers, new Uint8_t(eNetEvent.channelID), copyPacket(eNetEvent.packet));
                    Enet.INSTANCE.enet_packet_destroy(eNetEvent.packet);
                    Enet.INSTANCE.enet_host_flush(client);
                }
                else if (eNetEvent.type == Enet.ENetEventType.ENET_EVENT_TYPE_DISCONNECT){
                    System.out.println("Client disconnected.");
                    Enet.INSTANCE.enet_host_destroy(client);
                    clientPeer = null;
                    client = null;
                }
            }
            if (client != null) {
                while (Enet.INSTANCE.enet_host_service(client, eNetEvent,new Uint32_t(100)) !=0) {
                    if (eNetEvent.type == Enet.ENetEventType.ENET_EVENT_TYPE_CONNECT){
                        System.out.println("Connected to server.");
                    }
                    else if (eNetEvent.type== Enet.ENetEventType.ENET_EVENT_TYPE_RECEIVE){
                        if (dumpServer) System.out.printf("Server to Client: %s \n", Utils.bytesToHex(eNetEvent.packet.data.getByteArray(0, eNetEvent.packet.dataLength.intValue())));
                        Enet.INSTANCE.enet_peer_send(server.peers, new Uint8_t(eNetEvent.channelID),copyPacket(eNetEvent.packet));
                        Enet.INSTANCE.enet_packet_destroy(eNetEvent.packet);
                        Enet.INSTANCE.enet_host_flush(server);
                    }
                    else if (eNetEvent.type == Enet.ENetEventType.ENET_EVENT_TYPE_DISCONNECT){
                        System.out.println("Disconnected from server.");
                        Enet.INSTANCE.enet_peer_reset(clientPeer);
                    }
                }
            }

            if (!injectedToClient.isEmpty()) {
                sendRawPacket(injectedToClient.poll(), server.peers);
            }

            if (!injectedToServer.isEmpty() && client != null) {
                sendRawPacket(injectedToServer.poll(), client.peers);
            }
        }
    }

    public void sendRawPacket(String rawData, ENetPeer peer) {
        System.out.println("Sending injected data> " + rawData);
        byte[] bytes = Utils.hexStringToByteArray(rawData);
        Pointer data = new Memory(bytes.length+1);
        ENetPacket packet = Enet.INSTANCE.enet_packet_create(data,new Size_t(bytes.length+1), new Uint32_t(Enet.ENetPacketFlag.ENET_PACKET_FLAG_RELIABLE));
        packet.data.write(0,bytes,0,bytes.length);
        Enet.INSTANCE.enet_peer_send(peer, new Uint8_t(0), packet);
    }

    public ENetPacket copyPacket(ENetPacket original) {
        byte[] bytes = original.data.getByteArray(0,original.dataLength.intValue());
        Pointer data = new Memory(bytes.length+1);
        ENetPacket packet = Enet.INSTANCE.enet_packet_create(data,new Size_t(bytes.length+1), new Uint32_t(original.flags));
        packet.data.write(0,bytes,0,bytes.length);
        return packet;
    }
}
