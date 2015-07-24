package org.jgrades.logging.logger.configuration.strategy;

import com.google.common.io.Resources;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


public class TypeConfiguration implements ConfigurationStrategy{

    private static final String LOG_BACK_CONFIGURATION_PER_TYPE = "src/main/resources/logback_per_type_logging.xml";

    @Override
    public FileChannel getFileChannel() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(getLogbackConfigurationFile(), "rw");
        return raf.getChannel();
    }

    @Override
    public List<Element> getListCurrentLogFileSize(Document xmlFile) {
        List<Element> elementList = new ArrayList<>();
        XPathExpression<Element> xpath;

        xpath = XPathFactory.instance().compile("/configuration/appender[1]/sift/appender/rollingPolicy/triggeringPolicy[1]/MaxFileSize",
                Filters.element());
        elementList.add(xpath.evaluateFirst(xmlFile));

        xpath = XPathFactory.instance().compile("/configuration/appender[2]/sift/appender/rollingPolicy/triggeringPolicy[1]/MaxFileSize",
                Filters.element());
        elementList.add(xpath.evaluateFirst(xmlFile));

        xpath = XPathFactory.instance().compile("/configuration/appender[3]/sift/appender/rollingPolicy/triggeringPolicy[1]/MaxFileSize",
                Filters.element());
        elementList.add(xpath.evaluateFirst(xmlFile));


        return elementList;

    }

    @Override
    public String getConfigurationFilePath() {
        return LOG_BACK_CONFIGURATION_PER_TYPE;
    }

    @Override
    public List<Element> getListCurrentLogFileStorageTimeLimit(Document xmlFile) {
        List<Element> elementList = new ArrayList<>();
        XPathExpression<Element> xpath;

        xpath = XPathFactory.instance().compile("/configuration/appender[1]/sift/appender/rollingPolicy/triggeringPolicy[2]/MaxBackupIndex",
                Filters.element());
        elementList.add(xpath.evaluateFirst(xmlFile));

        xpath = XPathFactory.instance().compile("/configuration/appender[2]/sift/appender/rollingPolicy/triggeringPolicy[2]/MaxBackupIndex",
                Filters.element());
        elementList.add(xpath.evaluateFirst(xmlFile));

        xpath = XPathFactory.instance().compile("/configuration/appender[3]/sift/appender/rollingPolicy/triggeringPolicy[2]/MaxBackupIndex",
                Filters.element());
        elementList.add(xpath.evaluateFirst(xmlFile));


        return elementList;

    }

    private String getLogbackConfigurationFile() throws IOException {
        URL url = Resources.getResource(LOG_BACK_CONFIGURATION_PER_TYPE);
        return url.getPath();
    }


}
