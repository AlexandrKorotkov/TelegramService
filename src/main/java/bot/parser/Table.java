package bot.parser;


import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.util.Arrays;
import java.util.List;

public class Table {
    private PdfPTable table;
    private static final List<String> headerName = Arrays.asList("Name", "Activity");
    private static final Font HEADER_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    private static final Font COLS_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 14);

    public PdfPTable createTable(List<String> names, List<String> description){
        table = new PdfPTable(headerName.size());
        headerName.forEach(title -> {
            PdfPCell header = new PdfPCell();
            header.setBorderWidth(1);
            header.setVerticalAlignment(1);
            header.setHorizontalAlignment(1);
            header.setPadding(10);
            header.setPhrase(new Phrase(title, HEADER_FONT));
            table.addCell(header);
        });
        for(int i = 0; i < names.size(); i++){
            //add names
            PdfPCell cell = new PdfPCell();
            cell.setBorderWidth(1);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            cell.setPhrase(new Phrase(names.get(i), COLS_FONT));
            table.addCell(cell);

            //add description
            cell.setPhrase(new Phrase(description.get(i), COLS_FONT));
            table.addCell(cell);
        }
        return table;
    }
}
