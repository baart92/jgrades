package org.jgrades.lic.service;

import org.jgrades.lic.api.model.Licence;
import org.jgrades.logging.JgLogger;
import org.jgrades.logging.JgLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VersionRule implements ValidationRule {
    private static final JgLogger LOGGER = JgLoggerFactory.getLogger(VersionRule.class);

    private String version;

    @Autowired
    public VersionRule(@Value("${lic.product.release.version}") String version) {
        this.version = version;
    }

    @Override
    public boolean isValid(Licence licence) {
        LOGGER.debug("Start checking VersionRule for licence with uid {}", licence.getUid());
        LOGGER.debug("Running release of software: {}", version);
        return licence.getProduct().getVersion().equalsIgnoreCase(version);
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
