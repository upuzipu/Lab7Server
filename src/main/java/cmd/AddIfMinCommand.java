package cmd;

import client.Vehicles;
import collection.Car;
import collection.UserToken;
import connection.NetPackage;
import ioManager.RequestElement;

import java.util.HashMap;

public class AddIfMinCommand implements ICommand {

    @Override
    public String getName() {
        return "add_if_min";
    }

    @Override
    public String getDescription() {

        return "add_if_max - Добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции";
    }

    @Override
    public void execute(Vehicles env, String arg, UserToken user) {
        RequestElement reqEl = new RequestElement(env.getIn(), env.getOut(), !env.isScript());
        Car o = reqEl.readElement(user);
        env.getCollectionManager().addIfMin(env.getOut(), o,user);
    }

    @Override
    public void execute(Vehicles env, NetPackage netPackage) {
        env.getCollectionManager().addIfMin(env.getOut(), netPackage.getCar(), netPackage.getUser());
    }

    @Override
    public boolean hasObject() {
        return true;
    }

    @Override
    public boolean hasArg() {
        return false;
    }

    public static void register(HashMap<String, ICommand> commandMap) {
        ICommand cmd = new AddIfMinCommand();
        commandMap.put(cmd.getName(), cmd);
    }
}
