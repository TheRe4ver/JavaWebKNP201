package step.learning.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import step.learning.dto.entities.ChatMessage;
import step.learning.services.db.DbProvider;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ChatDao {
    private final DbProvider dbProvider;
    private final String dbPrefix;
    private final Logger logger;

    @Inject
    public ChatDao(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, Logger logger) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.logger = logger;
    }
    public boolean add(ChatMessage chatMessage) {
        String sql = "INSERT INTO " + dbPrefix + "chat (" +
                "`user`, `message` )" +
                "VALUES (?,?) " ;
        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
            prep.setString(1, chatMessage.getUser());
            prep.setString(2, chatMessage.getMessage());
            prep.executeUpdate();
            return true;
        }
        catch (SQLException ex){
            logger.log(Level.WARNING,ex.getMessage() + "----" + sql);
        }
        return false;
    }
    public boolean install() {
        String sql = "CREATE TABLE " + dbPrefix + "chat (" +
                "user BIGINT UNSIGNED," +
                "message TEXT NOT NULL," +
                "moment DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE = INNODB, DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_unicode_ci";
        try(Statement statement = dbProvider.getConnection().createStatement()) {
            statement.executeUpdate(sql);
            return true;
        }
        catch (SQLException ex){
            logger.log(Level.WARNING,ex.getMessage() + "----" + sql);
        }
        return false;
    }
}
