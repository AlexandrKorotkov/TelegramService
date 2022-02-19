package bot.telegram;

import de.phip1611.Docx4JSRUtil;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xlsx4j.sml.Col;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PDFout {
    static void savePDF(String json, String path) throws FileNotFoundException, Docx4JException, MalformedURLException {
        List<String> name = chooseNames(json, "user_id");
        List<String> date = getValuesForGivenKey(json, "date");
        List<String> description = getValuesForGivenKey(json, "description");
        Collections.replaceAll(description,"", "не заполнил");
        WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(path));
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < name.size(); i++) {
            map.put("NAME" + i, name.get(i));
            map.put("ACTIVITY" + i, description.get(i));
        }
        Docx4JSRUtil.searchAndReplace(template, map);
        Docx4JSRUtil.searchAndReplace(template, Map.of("DATE", date.get(0)
        ));
        Docx4J.toPDF(template, new FileOutputStream("document.pdf"));
    }

    private static List<String> getValuesForGivenKey(String jsonArrayStr, String key) {
        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        return IntStream.range(0, jsonArray.length())
                .mapToObj(index -> ((JSONObject)jsonArray.get(index)).optString(key))
                .collect(Collectors.toList());
    }

    private static List<String> chooseNames(String json, String key){
        List<String> names = getValuesForGivenKey(json, key);
        UnaryOperator<String> replace = a -> {
            switch (a){
                case "1":
                    return "Алексанр";
                case "2":
                    return "Павел";
                case "3":
                    return "Женя";
                case "4":
                    return "Николай";
                case "5":
                    return "Сергей";
                case "6":
                    return "Дамир";
                default:
                    return "неизвестный";
            }
        };
        names.replaceAll(replace);
        return names;
    }
}
