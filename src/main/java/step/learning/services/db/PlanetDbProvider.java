package step.learning.services.db;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Singleton;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

@Singleton
public class PlanetDbProvider implements DbProvider {
    private Connection connection;
    @Override
    public Connection getConnection() {
        if(this.connection == null) {
            // load configuration
            // locate file
            JsonObject config;
            try (Reader reader = new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("db_config.json")))) {
                config = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            catch (NullPointerException e) {
                throw new RuntimeException("Resource not found");
            }
            JsonObject planetScale = config.get("DataProviders").getAsJsonObject().get("PlanetScale").getAsJsonObject();
            try {
                // Class.forName("com.mysql.cj.jdbc.Driver");
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                this.connection = DriverManager.getConnection(
                        planetScale.get("url").getAsString(),
                        planetScale.get("user").getAsString(),
                        planetScale.get("password").getAsString());
            }
            catch ( SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return this.connection;
    }
}
