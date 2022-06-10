package cmd;

import client.Vehicles;
import collection.UserToken;
import connection.NetPackage;

import java.util.HashMap;

public class HelpCommand implements ICommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {

        return "help - Вывести справку по доступным командам";
    }

    @Override
    public void execute(Vehicles env, String arg, UserToken user) {
        for (ICommand cmd : env.getCommandMap().values()){
            env.getOut().writeln(cmd.getDescription());
        }
    }

    @Override
    public void execute(Vehicles env, NetPackage netPackage) {
        for (ICommand cmd : env.getCommandMap().values()){
            env.getOut().writeln(cmd.getDescription());
        }
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
        ICommand cmd = new HelpCommand();
        commandMap.put(cmd.getName(), cmd);
    }
}
