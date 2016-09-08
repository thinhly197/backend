package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.entities.Email;
import com.ascend.campaign.exceptions.CodeNotFoundException;
import com.ascend.campaign.exceptions.EmailDuplicateException;
import com.ascend.campaign.exceptions.EmailFormatException;
import com.ascend.campaign.repositories.EmailRepo;
import com.ascend.campaign.utils.EmailValidator;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@Slf4j
public class FileService {
    @NonNull
    private final EmailValidator validator;

    @NonNull
    private final EmailRepo emailRepo;

    @NonNull
    private final DataSource dataSource;

    @Autowired
    public FileService(EmailValidator validator, EmailRepo emailRepo, DataSource dataSource) {
        this.validator = validator;
        this.emailRepo = emailRepo;
        this.dataSource = dataSource;
    }

    public String importData(Long emailGroupId, MultipartFile file) throws Exception {
        StopWatch stopWatch = new StopWatch("CSV Import");
        stopWatch.start("CSV Parser");
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setLineSeparator("\n");
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);
        Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
        stopWatch.stop();

        stopWatch.start("Parse String[] to String");
        List<String> datas = parser.parseAll(reader).parallelStream().map(x -> x[0]).collect(toList());
        stopWatch.stop();

        stopWatch.start("Mail validation");
        boolean isValid = isValidate(datas);
        stopWatch.stop();
        if (isValid) {
            stopWatch.start("Delete older emails ");
            String deleteSql = "delete from email where emailgroup_id =" + emailGroupId;
            final String sql = "insert into email(emailgroup_id, email, created_at) values (?, ?, ?)";
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute(deleteSql);
            connection.commit();
            stopWatch.stop();
            stopWatch.start("Insert DB");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            datas.stream().forEach(x -> {
                try {
                    preparedStatement.setLong(1, emailGroupId);
                    preparedStatement.setString(2, x);
                    preparedStatement.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
                    preparedStatement.addBatch();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
            preparedStatement.executeBatch();
            connection.commit();
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
            stopWatch.stop();
        }
        log.info(stopWatch.prettyPrint());

        return CampaignEnum.SUCCESSFULLY.getContent();
    }


    public ByteArrayOutputStream exportData(Long emailGroup) throws Exception {
        ByteArrayOutputStream csvResult = new ByteArrayOutputStream();
        Writer outputWriter = new OutputStreamWriter(csvResult);

        CsvWriterSettings writerSettings = new CsvWriterSettings();
        writerSettings.setQuoteAllFields(true);
        writerSettings.setHeaders("Email");

        CsvWriter writer = new CsvWriter(outputWriter, writerSettings);
        writer.writeHeaders();

        List<Email> codeVIPList = Optional.ofNullable(emailRepo.findByEmailGroupId(emailGroup))
                .orElseThrow(CodeNotFoundException::new);

        codeVIPList.forEach(x -> writer.writeRow(x.getEmail()));
        writer.close();

        return csvResult;
    }

    private boolean isValidate(List<String> datas) throws Exception {
        Optional<String> invalid = datas.parallelStream().filter(x -> !validator.isValid(x)).findAny();
        if (invalid.isPresent()) {
            throw new EmailFormatException(Errors.EMAIL_FORMAT_INVALID);
        }

        Set<String> allItems = new HashSet<>();
        List<String> duplicate = datas.stream()
                .filter(n -> !allItems.add(n)).collect(Collectors.toList());

        if (!duplicate.isEmpty()) {
            throw new EmailDuplicateException();
        }

        return true;
    }

    public ByteArrayOutputStream exportVIPEmailTemplate() {
        ByteArrayOutputStream csvResult = new ByteArrayOutputStream();
        Writer outputWriter = new OutputStreamWriter(csvResult);

        CsvWriterSettings writerSettings = new CsvWriterSettings();
        writerSettings.setQuoteAllFields(true);
        writerSettings.setHeaders("Email");

        CsvWriter writer = new CsvWriter(outputWriter, writerSettings);
        writer.writeHeaders();
        writer.close();

        return csvResult;
    }
}