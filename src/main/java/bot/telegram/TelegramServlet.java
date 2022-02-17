package bot.telegram;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

@WebServlet("/sendPDF")
public class TelegramServlet extends HttpServlet {
    private SenderBot bot;
    private HikariDataSource dataSource;

    @Override
    public void init(ServletConfig config) throws ServletException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername("telegram");
        hikariConfig.setPassword("strongpassword");
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setJdbcUrl("jdbc:postgresql://34.116.245.1:5432/telegramdb");
        hikariConfig.setMaximumPoolSize(20);
        System.out.println("sout Database connection inits success.");
        this.dataSource = new HikariDataSource(hikariConfig);
        System.out.println("sout Database has been connected.");

        try {
            this.bot = new SenderBot();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonString = req.getParameter("jsonString");
        try {
            PDFout.savePDF(jsonString);
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
        bot.sendAll(new InputFile(new File("document.pdf")));
//        super.doPost(req, resp);
    }

//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        bot.sendAll("GET запрос через /sendPDF");
//        super.doGet(req, resp);
//    }
}
