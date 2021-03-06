package org.jgrades.lic.api.crypto.encrypt;

import org.apache.commons.io.FileUtils;
import org.jgrades.lic.api.crypto.utils.KeyStoreContentExtractor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class LicenceSigningProviderTest {
    private LicenceSigningProvider signingProvider;

    @Before
    public void setUp() throws Exception {
        File keystore = new ClassPathResource("jg-ks-test.jceks").getFile();
        File secData = new ClassPathResource("sec-test.dat").getFile();
        KeyStoreContentExtractor extractor = new KeyStoreContentExtractor(keystore, secData);

        signingProvider = new LicenceSigningProvider(extractor);
        assertThat(signingProvider).isNotNull();
    }

    @Test
    public void shouldGenerateSignatureForEncryptedLicence() throws Exception {
        // given
        File encryptedLicenceFile = new ClassPathResource("encrypted.lic").getFile();
        File signatureFile = new ClassPathResource("encrypted.lic.sign").getFile();

        // when
        byte[] signatureBytes = signingProvider.sign(FileUtils.readFileToByteArray(encryptedLicenceFile));

        // then
        assertThat(signatureBytes).isEqualTo(FileUtils.readFileToByteArray(signatureFile));
    }
}
