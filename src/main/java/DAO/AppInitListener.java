package DAO;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String dbPath = sce.getServletContext()
                .getRealPath("/WEB-INF/db/currency_exchange.db");

        if (dbPath == null) {
            throw new RuntimeException("Cannot resolve path to database");
        }

        File dbFile = new File(dbPath);
        Connect.init(dbFile.getAbsolutePath());
    }
}