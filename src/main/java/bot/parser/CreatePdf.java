package bot.parser;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CreatePdf {
    public void create(String path, String json) throws IOException, DocumentException {
        List<String> names = getValuesForGivenKey(json, "fio");
        List<String> dates = getValuesForGivenKey(json, "date");
        JSONObject jsonObject = new JSONObject(dates.get(0));
        String month = jsonObject.getString("month");
        int dayOfMonth = jsonObject.getInt("dayOfMonth");
        List<String> description = getValuesForGivenKey(json, "description");
        Collections.replaceAll(description,"", "did not fill in");

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path + "document.pdf"));
        document.open();
        //IMAGE
        Image pdfImage = Image.getInstance(new URL("https://avatars.githubusercontent.com/u/22486016?v=4"));
        pdfImage.setAlignment(Element.ALIGN_CENTER);
        document.add(pdfImage);
        //TEAM
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Paragraph head = new Paragraph("BLUE TEAM", font);
        head.setAlignment(Element.ALIGN_CENTER);
        head.setSpacingAfter(10);
        document.add(head);
        //REPORT
        Paragraph date = new Paragraph("Report on " + month + " " + dayOfMonth, font);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(10);
        document.add(date);
        //TABLE
        document.add(new Table().createTable(names, description));
        document.close();
        writer.close();
    }

    private List<String> getValuesForGivenKey(String jsonArrayStr, String key) {
        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        return IntStream.range(0, jsonArray.length())
                .mapToObj(index -> ((JSONObject)jsonArray.get(index)).optString(key))
                .collect(Collectors.toList());
    }
}
