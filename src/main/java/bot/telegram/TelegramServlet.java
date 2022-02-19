package bot.telegram;

import bot.parser.CreatePdf;
import com.itextpdf.text.DocumentException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/sendPDF")
public class TelegramServlet extends HttpServlet {
    private SenderBot bot;
    private HikariDataSource dataSource;

    @Override
    public void init(ServletConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername("telegram");
        hikariConfig.setPassword("strongpassword");
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setJdbcUrl("jdbc:postgresql://34.116.245.1:5432/telegramdb");
        hikariConfig.setMaximumPoolSize(20);
        this.dataSource = new HikariDataSource(hikariConfig);
        try {
            bot = new SenderBot();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String test = new String();
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            test = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        }
        try {
            CreatePdf pdf = new CreatePdf();
            pdf.create("", test);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        bot.sendAll(new InputFile(new File("document.pdf")));
    }

}
