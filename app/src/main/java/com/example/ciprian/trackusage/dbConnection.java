package com.example.ciprian.trackusage;

import android.os.AsyncTask;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class dbConnection extends AsyncTask<String, String, String>{

    private String aConnectionStr;
    private Connection connection;

    dbConnection(){
        String hostName = "finalyp.database.windows.net";
        String dbName = "FYP_APP";
        String user = "ciprian9";
        String password = "Alexandra1";
        this.aConnectionStr = String.format("jdbc:jtds:sqlserver://%s:1433;DatabaseName=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);
        this.connection = null;
    }

    @Override
    protected  String doInBackground(String... params) {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(aConnectionStr);
            insertDB(params[0]);
            connection.close();
        }
        catch (Exception e) {
            Log.e("error: ", e.getMessage());
        }
        return "";
    }

    private void insertDB(String ConType){
        try {
            String query = "INSERT INTO CONNECTIVITY(ID, ConType) Values(1, '" + ConType + "')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        }
        catch(Exception e)
        {
            Log.e("error: ", e.getMessage());
        }
    }
}
