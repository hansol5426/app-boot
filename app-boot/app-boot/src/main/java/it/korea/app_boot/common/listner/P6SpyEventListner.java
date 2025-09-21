package it.korea.app_boot.common.listner;

import java.sql.SQLException;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.spy.P6SpyOptions;

import it.korea.app_boot.common.config.P6spySqlFormatter;

public class P6SpyEventListner extends JdbcEventListener{

    @Override
    public void onAfterConnectionClose(ConnectionInformation connectionInformation, SQLException e) {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(P6spySqlFormatter.class.getName());    
    }

    

}
