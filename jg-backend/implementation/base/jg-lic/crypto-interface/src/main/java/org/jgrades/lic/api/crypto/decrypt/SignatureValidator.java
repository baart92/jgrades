package org.jgrades.lic.api.crypto.decrypt;

import org.apache.commons.io.FileUtils;
import org.jgrades.lic.api.crypto.utils.KeyStoreContentExtractor;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.cert.X509Certificate;

import static org.jgrades.lic.api.crypto.utils.LicConstants.SIGNATURE_PROVIDER_INTERFACE;

class SignatureValidator {
    private final KeyStoreContentExtractor keyExtractor;

    public SignatureValidator(KeyStoreContentExtractor keyExtractor) {
        this.keyExtractor = keyExtractor;
    }

    public boolean signatureValidated(File encryptedLicenceFile, File signatureFile) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        X509Certificate certificate = keyExtractor.getCertificateForVerification();
        PublicKey publicKey = certificate.getPublicKey();

        Signature signature = Signature.getInstance(SIGNATURE_PROVIDER_INTERFACE);
        signature.initVerify(publicKey);
        try {
            signature.update(FileUtils.readFileToByteArray(encryptedLicenceFile));

            return signature.verify(FileUtils.readFileToByteArray(signatureFile));
        } catch (SignatureException e) {
            return false;
        }
    }
}
