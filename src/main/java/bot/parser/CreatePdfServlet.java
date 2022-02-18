package bot.parser;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CreatePdfServlet {
    public void create(String path, String json) throws IOException, DocumentException {
        List<String> names = chooseNames(json, "user_id");
        List<String> dates = getValuesForGivenKey(json, "date");
        List<String> description = getValuesForGivenKey(json, "description");
        Collections.replaceAll(description,"", "did not fill in");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
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
        Paragraph date = new Paragraph("Report on " + dates.get(0), font);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(10);
        document.add(date);
        //TABLE
        document.add(new Table().createTable(names, description));
        //CLOSE
        document.close();
        writer.close();
        try(OutputStream outputStream = new FileOutputStream("baos.pdf")) {
            baos.writeTo(outputStream);
        }
    }

    private List<String> getValuesForGivenKey(String jsonArrayStr, String key) {
        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        return IntStream.range(0, jsonArray.length())
                .mapToObj(index -> ((JSONObject)jsonArray.get(index)).optString(key))
                .collect(Collectors.toList());
    }

    private List<String> chooseNames(String json, String key){
        List<String> names = getValuesForGivenKey(json, key);
        UnaryOperator<String> replace = a -> {
            switch (a){
                case "1":
                    return "Alexandr";
                case "2":
                    return "Oleg";
                case "3":
                    return "Eugenia";
                case "4":
                    return "Nikolai";
                case "5":
                    return "Sergey";
                case "6":
                    return "Damir";
                default:
                    return "unknown";
            }
        };
        names.replaceAll(replace);
        return names;
    }
}
