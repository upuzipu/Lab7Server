package cmd;

import client.Vehicles;
import collection.UserToken;
import connection.NetPackage;

import java.util.HashMap;

public class ClearCommand implements ICommand {


    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {

        return "clear - Очистить коллекцию";
    }

    @Override
    public void execute(Vehicles env, String arg, UserToken user) {
        env.getCollectionManager().clear(env.getOut(),user);
    }

    @Override
    public void execute(Vehicles env, NetPackage netPackage) {
        env.getCollectionManager().clear(env.getOut(), netPackage.getUser());
    }

    @Override
    public boolean hasObject() {
        return false;
    }

    @Override
    public boolean hasArg() {
        return false;
    }

    public static void register(HashMap<String, ICommand> commandMap) {
        ICommand cmd = new ClearCommand();
        commandMap.put(cmd.getName(), cmd);
    }
}
