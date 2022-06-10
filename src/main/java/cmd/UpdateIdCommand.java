package cmd;

import client.Vehicles;
import collection.Car;
import collection.UserToken;
import connection.NetPackage;
import ioManager.RequestElement;

import java.util.HashMap;

public class UpdateIdCommand implements ICommand {

    @Override
    public String getName() {
        return "update_id";
    }

    @Override
    public String getDescription() {

        return "update_id - Обновить значение элемента коллекции, id которого равен заданному";
    }

    @Override
    public void execute(Vehicles env, String arg, UserToken user) {
        int id;
        if (!arg.isEmpty())
            try{
                id = Integer.parseInt(arg);
            }
            catch (NumberFormatException ex){
                env.getOut().writeln("Аргумент должен быть целым числом");
                return;
            }
        else{
            System.err.println("Требуется ввести аргумент");
            return;
        }
        RequestElement reqEl = new RequestElement(env.getIn(), env.getOut(), !env.isScript());
        Car o = reqEl.readElement(user);
        env.getCollectionManager().updateById(env.getOut(), id,o,user);
    }

    @Override
    public void execute(Vehicles env, NetPackage netPackage) {
    env.getCollectionManager().updateById(env.getOut(), Integer.parseInt(netPackage.getArg()),netPackage.getCar(), netPackage.getUser());
    }

    @Override
    public boolean hasObject() {
        return true;
    }

    @Override
    public boolean hasArg() {
        return true;
    }

    public static void register(HashMap<String, ICommand> commandMap) {
        ICommand cmd = new UpdateIdCommand();
        commandMap.put(cmd.getName(), cmd);
    }
}
