package client;

import connection.*;
import org.springframework.util.SerializationUtils;

import javax.persistence.criteria.Selection;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

public class Server {
    static Logger LOGGER = Logger.getLogger(Server.class.getName());
    private final CommunicationUDP communication;
    private final Vehicles vehicles;
    private final HashMap<SocketAddress, byte[]> que;

    public Server(Vehicles vehicles, CommunicationUDP communication) {
        this.vehicles = vehicles;
        this.communication = communication;
        que = new HashMap<>();
    }

    public void init() {
        try {
            ExecutorService executorService = Executors.newWorkStealingPool();
            Selector selector = Selector.open();
            communication.getChannel().register(selector, SelectionKey.OP_READ);
            while (vehicles.isRunning()) {
                BufferPacket bp;
                NetPackage netPackage;
                SocketAddress remote_address;
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    if (selectionKey.isReadable()) {
                        try {
                            bp = communication.receive();
                            remote_address = bp.getRemote_address();
                            if (que.containsKey(remote_address))
                                que.put(remote_address, joinByteArray(que.get(remote_address), bp.getByteBuffer()));
                            else que.put(remote_address, bp.getByteBuffer());
                            if (!bp.isFinish()) {
                                continue;
                            }
                            netPackage = (NetPackage) SerializationUtils.deserialize(que.get(bp.getRemote_address()));
                            netPackage.setRemote_address(remote_address);
                            que.remove(remote_address);
                        } catch (IOException e) {
                            LOGGER.log(Level.WARNING, "Ошибка прослушки порта");
                            continue;
                        } catch (IllegalArgumentException ex) {
                            continue;
                        } catch (IllegalStateException ex) {
                            LOGGER.log(Level.WARNING, "Получен поврежденный пакет");
                            continue;
                        }
                        executorService.submit(new CommandProcessing(vehicles, communication, netPackage));
                    }
                }
            }
            try {
                communication.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Ошибка закрытия сетевого канала");
            }
        } catch (IOException e) {
            System.out.println("123");
        }
    }

    public static byte[] joinByteArray(byte[] byte1, byte[] byte2) {

        byte[] result = new byte[byte1.length + byte2.length];

        System.arraycopy(byte1, 0, result, 0, byte1.length);
        System.arraycopy(byte2, 0, result, byte1.length, byte2.length);

        return result;

    }
}
