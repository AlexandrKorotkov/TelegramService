package bot.telegram.repo;

import java.util.List;
import java.util.Map;

public interface TelegramRepo {
    List<Long> getChatsFromDB();
    void addChatsToDB(Map<Long, String> chats);
}
