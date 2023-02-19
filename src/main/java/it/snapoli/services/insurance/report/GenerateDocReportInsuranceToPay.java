package it.snapoli.services.insurance.report;

import it.snapoli.services.insurance.config.FiscalCodeFactory;
import it.snapoli.services.insurance.customers.CustomerEntity;
import it.snapoli.services.insurance.insurance.InsuranceEntity;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class GenerateDocReportInsuranceToPay {

    @SneakyThrows
    public ByteArrayInputStream generate(CustomerEntity customer, List<InsuranceEntity> insuranceEntities){
        XWPFDocument doc = new XWPFDocument(getUrl());

        new DocReplacer(doc)
                .replaceText("${COMPANY_NAME} ",customer.getDisplayName())
                .replaceText("${ADDRESS}",customer.getAddress())
                .replaceText("${RESIDENZA_VALUE}",customer.getCity())
                .replaceText("${CAP}",customer.getZipcode())
                .replaceText("${PIVA}",customer.getPiva())
                .replaceText("${DATE}", LocalDate.now().toString())
                .replaceText("${TOTAL}", insuranceEntities.stream().map(InsuranceEntity::getToPay).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).toString())

        ;

        XWPFTable table = doc.getTableArray(0);
        XWPFTableRow rowTemplate = table.getRow(1);

        var pos = new AtomicInteger(1);

        insuranceEntities.forEach(insuranceEntity -> {
            try {
                CTRow newCtRow = CTRow.Factory.parse(rowTemplate.getCtRow().newInputStream());
                XWPFTableRow newRow = new XWPFTableRow(newCtRow, table);
                newRow.getCell(0).setText(insuranceEntity.getId()+"");
                newRow.getCell(1).setText(Optional.ofNullable(insuranceEntity.getInsuredGood().getReference()).orElse(""));
                newRow.getCell(2).setText(insuranceEntity.getInsuredGood().getType()+" "+ Optional.ofNullable(insuranceEntity.getInsuredGood().getName()).orElse(""));
                newRow.getCell(3).setText(insuranceEntity.getToPay().toString());
                newRow.getCell(4).setText(insuranceEntity.getStartTime().toString());
                newRow.getCell(5).setText(insuranceEntity.getEndTime().toString());
                newRow.getCell(6).setText(insuranceEntity.getInsuredGood().getType());
                table.addRow(newRow,pos.getAndIncrement());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });




        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        doc.write(byteArrayOutputStream);
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }



    private static InputStream getUrl() {
        return FiscalCodeFactory.class.getClassLoader().getResourceAsStream("META-INF/resources/estrattoconto_insurance.docx");
    }
}
