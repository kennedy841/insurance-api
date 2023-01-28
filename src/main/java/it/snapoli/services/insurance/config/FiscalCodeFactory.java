package it.snapoli.services.insurance.config;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import it.okkam.validation.FiscalCodeConf;
import it.okkam.validation.FiscalCodeValidator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.enterprise.inject.Produces;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FiscalCodeFactory {
    @Produces
    public FiscalCodeConf fiscalCodeConf() throws IOException, URISyntaxException {
        int maxComuneNameLength = 25;
        String maleValue = "M";
        int yearStart = 8;
        int yearEnd = 10;
        int monthStart = 3;
        int monthEnd = 5;
        int dayStart = 0;
        int dayEnd = 2;
        String codiciIstatStr = readLocalFile();

        return FiscalCodeValidator.getFiscalCodeConf(codiciIstatStr,
                maxComuneNameLength, maleValue, yearStart, yearEnd, monthStart, monthEnd, dayStart, dayEnd);

    }

    @SneakyThrows
    @Produces
    public static Towns towns() {
        CSVParser csvParser = new CSVParserBuilder().withSeparator('\t').build();

        ArrayList<Towns.Entry> values = new ArrayList<>();
        ;

        try(CSVReader reader = new CSVReaderBuilder(new InputStreamReader(getUrl()))
                .withCSVParser(csvParser)   // custom CSV parser
                .withSkipLines(1)           // skip the first line, header info
                .build()){
            List<String[]> r = reader.readAll();
            r.forEach(x -> {
                values.add(new Towns.Entry(x[1],x[1]));
            });
        }

        return new Towns(values.stream().distinct().collect(Collectors.toList()));

    }

    @RequiredArgsConstructor
    @Getter
    public static class Towns {
        private final List<Entry> values;

        @RequiredArgsConstructor
        @Getter
        @EqualsAndHashCode
        public static class Entry {
            private final String value;
            private final String id;


        }
    }


    private static String readLocalFile() throws IOException {
        InputStream resource = getUrl();
        DataInputStream dis = new DataInputStream(resource);
        return new String(dis.readAllBytes());
    }

    private static InputStream getUrl() {
        return FiscalCodeFactory.class.getClassLoader().getResourceAsStream("META-INF/resources/lista-comuni.csv");
    }
}
