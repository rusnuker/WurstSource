// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.altmanager;

import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.math.BigInteger;
import java.io.ObjectInputStream;
import javax.crypto.KeyGenerator;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.io.ObjectOutputStream;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.nio.file.LinkOption;
import com.google.gson.JsonObject;
import java.nio.file.OpenOption;
import net.wurstclient.util.json.WsonObject;
import net.wurstclient.util.json.WsonArray;
import java.io.BufferedReader;
import com.google.gson.JsonParseException;
import net.wurstclient.util.json.JsonException;
import net.wurstclient.util.json.JsonUtils;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.GeneralSecurityException;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReport;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import net.wurstclient.files.WurstFolders;
import javax.crypto.Cipher;

public final class Encryption
{
    private static final String CHARSET = "UTF-8";
    private final Cipher encryptCipher;
    private final Cipher decryptCipher;
    
    public Encryption() {
        final KeyPair rsaKeyPair = this.getRsaKeyPair(WurstFolders.RSA.resolve("wurst_rsa.pub"), WurstFolders.RSA.resolve("wurst_rsa"));
        final SecretKey aesKey = this.getAesKey(WurstFolders.MAIN.resolve("key"), rsaKeyPair);
        try {
            (this.encryptCipher = Cipher.getInstance("AES/CFB8/NoPadding")).init(1, aesKey, new IvParameterSpec(aesKey.getEncoded()));
            (this.decryptCipher = Cipher.getInstance("AES/CFB8/NoPadding")).init(2, aesKey, new IvParameterSpec(aesKey.getEncoded()));
        }
        catch (final GeneralSecurityException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Creating AES ciphers"));
        }
    }
    
    public byte[] decrypt(final byte[] bytes) {
        try {
            return this.decryptCipher.doFinal(Base64.getDecoder().decode(bytes));
        }
        catch (final GeneralSecurityException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Decrypting bytes"));
        }
    }
    
    public String loadEncryptedFile(final Path path) throws IOException {
        try {
            return new String(this.decrypt(Files.readAllBytes(path)), "UTF-8");
        }
        catch (final ReportedException e) {
            throw new IOException(e);
        }
    }
    
    public JsonElement parseFile(final Path path) throws IOException, JsonException {
        try {
            Throwable t = null;
            try {
                final BufferedReader reader = Files.newBufferedReader(path);
                try {
                    return JsonUtils.JSON_PARSER.parse(this.loadEncryptedFile(path));
                }
                finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
            finally {
                if (t == null) {
                    final Throwable exception;
                    t = exception;
                }
                else {
                    final Throwable exception;
                    if (t != exception) {
                        t.addSuppressed(exception);
                    }
                }
            }
        }
        catch (final JsonParseException e) {
            throw new JsonException((Throwable)e);
        }
    }
    
    public WsonArray parseFileToArray(final Path path) throws IOException, JsonException {
        final JsonElement json = this.parseFile(path);
        if (!json.isJsonArray()) {
            throw new JsonException();
        }
        return new WsonArray(json.getAsJsonArray());
    }
    
    public WsonObject parseFileToObject(final Path path) throws IOException, JsonException {
        final JsonElement json = this.parseFile(path);
        if (!json.isJsonObject()) {
            throw new JsonException();
        }
        return new WsonObject(json.getAsJsonObject());
    }
    
    public byte[] encrypt(final byte[] bytes) {
        try {
            return Base64.getEncoder().encode(this.encryptCipher.doFinal(bytes));
        }
        catch (final GeneralSecurityException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Encrypting bytes"));
        }
    }
    
    public void saveEncryptedFile(final Path path, final String content) throws IOException {
        try {
            Files.write(path, this.encrypt(content.getBytes("UTF-8")), new OpenOption[0]);
        }
        catch (final ReportedException e) {
            throw new IOException(e);
        }
    }
    
    public void toEncryptedJson(final JsonObject json, final Path path) throws IOException, JsonException {
        try {
            this.saveEncryptedFile(path, JsonUtils.PRETTY_GSON.toJson((JsonElement)json));
        }
        catch (final JsonParseException e) {
            throw new JsonException((Throwable)e);
        }
    }
    
    private KeyPair getRsaKeyPair(final Path publicFile, final Path privateFile) {
        if (Files.notExists(publicFile, new LinkOption[0]) || Files.notExists(privateFile, new LinkOption[0])) {
            return this.createRsaKeys(publicFile, privateFile);
        }
        try {
            return this.loadRsaKeys(publicFile, privateFile);
        }
        catch (final GeneralSecurityException | ReflectiveOperationException | IOException e) {
            System.err.println("Couldn't load RSA keypair!");
            e.printStackTrace();
            return this.createRsaKeys(publicFile, privateFile);
        }
    }
    
    private SecretKey getAesKey(final Path path, final KeyPair pair) {
        if (Files.notExists(path, new LinkOption[0])) {
            return this.createAesKey(path, pair);
        }
        try {
            return this.loadAesKey(path, pair);
        }
        catch (final GeneralSecurityException | IOException e) {
            System.err.println("Couldn't load AES key!");
            e.printStackTrace();
            return this.createAesKey(path, pair);
        }
    }
    
    private KeyPair createRsaKeys(final Path publicFile, final Path privateFile) {
        try {
            System.out.println("Generating RSA keypair.");
            final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            final KeyPair pair = generator.generateKeyPair();
            final KeyFactory factory = KeyFactory.getInstance("RSA");
            Throwable t = null;
            try {
                final ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(publicFile, new OpenOption[0]));
                try {
                    final RSAPublicKeySpec keySpec = factory.getKeySpec(pair.getPublic(), RSAPublicKeySpec.class);
                    out.writeObject(keySpec.getModulus());
                    out.writeObject(keySpec.getPublicExponent());
                }
                finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
            finally {
                if (t == null) {
                    final Throwable exception;
                    t = exception;
                }
                else {
                    final Throwable exception;
                    if (t != exception) {
                        t.addSuppressed(exception);
                    }
                }
            }
            Throwable t2 = null;
            try {
                final ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(privateFile, new OpenOption[0]));
                try {
                    final RSAPrivateKeySpec keySpec2 = factory.getKeySpec(pair.getPrivate(), RSAPrivateKeySpec.class);
                    out.writeObject(keySpec2.getModulus());
                    out.writeObject(keySpec2.getPrivateExponent());
                }
                finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
            finally {
                if (t2 == null) {
                    final Throwable exception2;
                    t2 = exception2;
                }
                else {
                    final Throwable exception2;
                    if (t2 != exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
            }
            return pair;
        }
        catch (final GeneralSecurityException | IOException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Creating RSA keypair"));
        }
    }
    
    private SecretKey createAesKey(final Path path, final KeyPair pair) {
        try {
            System.out.println("Generating AES key.");
            final KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128);
            final SecretKey key = keygen.generateKey();
            final Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(1, pair.getPublic());
            Files.write(path, rsaCipher.doFinal(key.getEncoded()), new OpenOption[0]);
            return key;
        }
        catch (final GeneralSecurityException | IOException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Creating AES key"));
        }
    }
    
    private KeyPair loadRsaKeys(final Path publicFile, final Path privateFile) throws GeneralSecurityException, ReflectiveOperationException, IOException {
        final KeyFactory factory = KeyFactory.getInstance("RSA");
        Throwable t = null;
        PublicKey publicKey;
        try {
            final ObjectInputStream in = new ObjectInputStream(Files.newInputStream(publicFile, new OpenOption[0]));
            try {
                publicKey = factory.generatePublic(new RSAPublicKeySpec((BigInteger)in.readObject(), (BigInteger)in.readObject()));
            }
            finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        finally {
            if (t == null) {
                final Throwable exception;
                t = exception;
            }
            else {
                final Throwable exception;
                if (t != exception) {
                    t.addSuppressed(exception);
                }
            }
        }
        Throwable t2 = null;
        PrivateKey privateKey;
        try {
            final ObjectInputStream in2 = new ObjectInputStream(Files.newInputStream(privateFile, new OpenOption[0]));
            try {
                privateKey = factory.generatePrivate(new RSAPrivateKeySpec((BigInteger)in2.readObject(), (BigInteger)in2.readObject()));
            }
            finally {
                if (in2 != null) {
                    in2.close();
                }
            }
        }
        finally {
            if (t2 == null) {
                final Throwable exception2;
                t2 = exception2;
            }
            else {
                final Throwable exception2;
                if (t2 != exception2) {
                    t2.addSuppressed(exception2);
                }
            }
        }
        return new KeyPair(publicKey, privateKey);
    }
    
    private SecretKey loadAesKey(final Path path, final KeyPair pair) throws GeneralSecurityException, IOException {
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, pair.getPrivate());
        return new SecretKeySpec(cipher.doFinal(Files.readAllBytes(path)), "AES");
    }
}
