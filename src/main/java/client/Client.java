package client;

import cmd.LoginCommand;
import collection.UserToken;

import java.sql.SQLException;

public class Client {
    private UserToken user;
    private final Vehicles vehicles;
    public Client(Vehicles vehicles){
        this.vehicles = vehicles;
        user = null;
    }
    public void init() {
        while (vehicles.isRunning()) {

            String s = vehicles.getIn().readline();
            if (s == null)
                if (vehicles.isScript())
                    break;
                else
                    continue;

            String cmd = "";
            String arg = "";

            String[] sArr = s.split("\\s");
            if (sArr.length==1)
                cmd = sArr[0];
            if (sArr.length>1)
            {
                cmd = sArr[0];
                arg = sArr[1];
            }
            System.out.println("cmd[0] = " + cmd);
            try {
            if (cmd.equals("login"))
                user = LoginCommand.login(vehicles);
            else if (cmd.equals("register"))
                LoginCommand.register(vehicles.getManagerDB());
            else
                if (user!=null && vehicles.getManagerDB().checkUser(user))
                if (vehicles.getCommandMap().containsKey(cmd)) {
                    vehicles.getCommandMap().get(cmd).execute(vehicles, arg, user);
                }
                else {
                    vehicles.getOut().writeln("Command not found");
                }
                else{
                    vehicles.getOut().writeln("Вы не авторизированный пользователь");
                }
            } catch (SQLException e) {
                System.err.println("Ошибка доступа к Базе данных!");}
        }
    }
}

