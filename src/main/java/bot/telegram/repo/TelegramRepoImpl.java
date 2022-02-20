package bot.telegram.repo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TelegramRepoImpl implements TelegramRepo {
    private final DataSource dataSource;
    private static final String SQL_SAVE_CHAT = "insert into chats (id, full_name) values (?, ?)";
    private static final String SQL_FIND_ALL = "select id from chats";

    public TelegramRepoImpl(DataSource dataSource) {
             this.dataSource = dataSource;
            }

    @Override
    public List<Long> getChatsFromDB() {
        List<Long> chatList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    chatList.add(resultSet.getLong(1));
                }
            }
            return chatList;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void addChatsToDB(Map<Long, String> chats) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SAVE_CHAT)) {
            for (Map.Entry<Long, String> pair : chats.entrySet()) {
                statement.setLong(1, pair.getKey());
                statement.setString(2, pair.getValue());
                int affectedRows = statement.executeUpdate();
                if (affectedRows != 1) {
                    throw new SQLException("Can't update chatId in DB " + pair.getKey() + " " + pair.getValue());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
