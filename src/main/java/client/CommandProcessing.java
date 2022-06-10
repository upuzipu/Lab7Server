package client;

import cmd.ICommand;
import connection.*;
import ioManager.ResponseOut;

import java.io.IOException;
import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandProcessing implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(CommandProcessing.class.getName());
    private final NetPackage netPackage;
    private final Vehicles vehicles;
    private final CommunicationUDP communication;
    private final SocketAddress remote_address;
    public CommandProcessing(Vehicles vehicles, CommunicationUDP communication, NetPackage netPackage){
        this.vehicles = vehicles;
        this.netPackage = netPackage;
        this.communication = communication;
        this.remote_address = netPackage.getRemote_address();
    }
    @Override
    public void run() {
        vehicles.setOut(new ResponseOut(communication,remote_address));
        String cmd = netPackage.getCmd();
        if (vehicles.getCommandMap().containsKey(cmd)) {
            vehicles.getCommandMap().get(cmd).execute(vehicles, netPackage);
        }
        else if(cmd.equals("connect")){
            try {
                if (vehicles.getManagerDB().checkUser(netPackage.getUser()))
                    vehicles.getOut().writeln("Вы успешно авторизовались");
                else {
                    vehicles.getOut().writeln("Вы ввели неверный логин или пароль");
                    return;
                }
            } catch (SQLException e) {
                System.err.println("Ошибка доступа к database");
                return;
            }
            int it = 0;
            for (ICommand c : vehicles.getCommandMap().values()) {
                try {
                    if (it + 1 == vehicles.getCommandMap().size()) {
                        communication.send(new CommandPackage(c.getName(), c.getDescription(), c.hasArg(), c.hasObject(), true), remote_address);
                    } else {
                        communication.send(new CommandPackage(c.getName(), c.getDescription(), c.hasArg(), c.hasObject(), false), remote_address);
                    }
                    it++;
                }
                catch (IOException ex){
                    LOGGER.log(Level.WARNING,"Ошибка отправки пакета клиенту");
                }
            }
            return;
        }
        else {
            vehicles.getOut().writeln("Command not found (type \"help\" to get information about available commands)");
        }
        Thread sender = new SenderThread(communication,new NetResponse("",true),remote_address);
        sender.start();
    }
}
