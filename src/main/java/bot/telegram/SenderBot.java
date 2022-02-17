package bot.telegram;

import bot.telegram.repo.TelegramRepo;
import bot.telegram.repo.TelegramRepoImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SenderBot extends TelegramLongPollingBot {
    private static List<Long> oldChats = new ArrayList<>();//чаты, подтянутые из БД
    private static Map<Long, String> newChats = new HashMap<>();//чаты, сохраненные с момента последенего запуска программы
    private TelegramRepo telegramRepo = new TelegramRepoImpl();


    //обновление в бд
    public void synchronizeChats() {
        telegramRepo.addChatsToDB(newChats);
        moveNewsToOlds();
    }

    @Override
    public void onClosing() {
        synchronizeChats();
        super.onClosing();
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

//    public void sendOurSpecificPDF(InputFile fileToSend) throws TelegramApiException {
//        execute(new SendDocument().builder().chatId(().toString()).document(fileToSend).build());
//    }

    private void moveNewsToOlds() {
        oldChats.addAll(newChats.keySet());
        newChats.clear();
    }

    public void sendAll(InputFile file) {
        //рассылка для стареньких
        for (Long l : oldChats) {
            try {
                execute(SendMessage.builder().chatId(l.toString()).text(file + " Старенький").build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        for (Map.Entry<Long, String> pair : newChats.entrySet()) {
            try {
                execute(SendMessage.builder().chatId(pair.getKey().toString()).text(file + " новенький").build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        synchronizeChats();

    }


    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        if (!oldChats.contains(chatId)) {
            Long key = update.getMessage().getChatId();
            String value = update.getMessage().getFrom().getFirstName() + " "
                    + update.getMessage().getFrom().getLastName();
            newChats.put(key, value);
            System.out.println("Добавили в newChats "+ key + " "+ value);
            synchronizeChats();//проверить - будет писать что уже старый

        }
        System.out.println(update.getMessage().getFrom().getFirstName() + " "
                + update.getMessage().getFrom().getLastName() +
                " chatId = " + update.getMessage().getChat().getId());

//        sendAll("хуй");
//        chatIdMap.putIfAbsent(update.getChatMember().getChat().getId(),
//                update.getMessage().getFrom().getFirstName()+" "
//                        +update.getMessage().getFrom().getLastName()
//                        +" chatId = "+update.getMessage().getChat().getId());
//        chatId = update.getMessage().getChatId();
//        System.out.println(super.);
//        execute(Se)
//        sendOurSpecificPDF();
//            execute(new SendMessage(Long.toString(chatId),"Здарова"));

    }
}
