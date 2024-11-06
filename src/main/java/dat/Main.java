package dat;

import dat.config.ApplicationConfig;

public class Main
{
    public static void main(String[] args) {
        //1.1 Starter Javalin serveren op med JPA-config
        ApplicationConfig.startServer(7777);
       // Populate.main(args);
    }

}
