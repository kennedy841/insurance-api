package it.snapoli.services.insurance.report;

import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class DocReplacer {

    private final XWPFDocument doc;

    public DocReplacer replaceText( String search, String replace){
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null  && text.contains(search)) {
                        text = text.replace(search, Optional.ofNullable(replace).orElse(""));
                        r.setText(text, 0);
                    }
                }
            }
        }
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null  && text.contains(search)) {
                                text = text.replace(search, Optional.ofNullable(replace).orElse(""));
                                r.setText(text,0);
                            }
                        }
                    }
                }
            }
        }

        return this;
    }
}
