package cmd;

import client.Vehicles;
import collection.UserToken;
import connection.NetPackage;

public interface ICommand {
    String getName();
    String getDescription();
    void execute(Vehicles env, String arg, UserToken user);
    void execute (Vehicles env, NetPackage netPackage);
    boolean hasObject();
    boolean hasArg();
}
