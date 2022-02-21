package bot.telegram;

import bot.telegram.repo.TelegramRepo;
import bot.telegram.repo.TelegramRepoImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.sql.DataSource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SenderBot extends TelegramLongPollingBot {
    private static List<Long> oldChats = new ArrayList<>();//чаты, подтянутые из БД
    private static Map<Long, String> newChats = new HashMap<>();//чаты, сохраненные с момента последенего запуска программы
    private TelegramRepo telegramRepo;

    public SenderBot(DataSource dataSource) {
        this.telegramRepo = new TelegramRepoImpl(dataSource);
    }

    //обновление данных в бд
    public void synchronizeChats() {
        telegramRepo.addChatsToDB(newChats);
        moveNewsToOlds();
    }

    @Override
    public void onClosing() {
        super.onClosing();
        synchronizeChats();
    }

    @Override
    public void onRegister() {
        //заполняем список уже существующими ID чатов
        oldChats.addAll(telegramRepo.getChatsFromDB());
        super.onRegister();
    }

    @Override
    public String getBotUsername() {
        return "pdf_sender_bot";
    }

    @Override
    public String getBotToken() {
        return "5121355453:AAHz9S07uwS4D-djpGXdl1B22U9qpOv2lRs";
    }

    //перемещение списка новых юзеров в список старых.
    private void moveNewsToOlds() {
        oldChats.addAll(newChats.keySet());
        newChats.clear();
    }

    //сначала рассылка по старым, потом по новым. После рассылки происходит обновление чатов.
    //Все новые при следующей рассылке будут уже в старых.
    public void sendAll(InputFile file) {
        for (Long l : oldChats) {
            try {
                execute(SendDocument.builder().document(file).chatId(l.toString()).build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        for (Map.Entry<Long, String> pair : newChats.entrySet()) {
            try {
                execute(SendDocument.builder().document(file).chatId(pair.getKey().toString()).build());

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        synchronizeChats();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        if ("/start".equals(update.getMessage().getText())) {
            try {
                execute(SendMessage.builder().chatId(chatId.toString()).text("Здарова").disableNotification(true).build());
//                execute(SendPhoto.builder().chatId(chatId.toString()).photo(new InputFile(
//                        new File("src/main/webapp/111.jpg"))).build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if (!oldChats.contains(chatId)) {
            Long key = update.getMessage().getChatId();
            String value = update.getMessage().getFrom().getFirstName() + " "
                    + update.getMessage().getFrom().getLastName();
            newChats.put(key, value);
        }

    }
}
