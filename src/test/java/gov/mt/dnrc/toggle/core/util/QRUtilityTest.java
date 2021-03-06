package gov.mt.dnrc.toggle.core.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import gov.mt.dnrc.toggle.software.models.Software;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * QR Code Utility test to ensure the integrity of the QR codes work.
 */
public class QRUtilityTest {

    private static final String QR_IMAGE_TYPE = "png";
    private static final String CHARSET_TYPE = "UTF-8";

    @Test
    public void generateQRCodeBasedOnSoftwareObjectTest() {

        Software software = new Software();
        software.setId(1L);
        software.setName("SQL Server Management Studio");
        software.setVendor("Microsoft");
        software.setVersion("2018 R2");

        // Preliminary check of object.
        assertNotNull(software);

        // Try with resources building the byte array stream for generating the encoded image file.
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            // Set the Hint Type encoding properties for the bit matrix encoder
            Map<EncodeHintType, Object> enumMap = new EnumMap<>(EncodeHintType.class);
            enumMap.put(EncodeHintType.CHARACTER_SET, CHARSET_TYPE);
            enumMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            enumMap.put(EncodeHintType.MARGIN, 4);

            int matrixWidth = 250;
            int matrixHeight = 250;

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(software.toString(), BarcodeFormat.QR_CODE, matrixWidth, matrixHeight, enumMap);

            int width = byteMatrix.getWidth();
            int height = byteMatrix.getHeight();

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics();

            // Image pixel settings.
            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);

            // Create the QR code based on the current pixel your working with.
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            // Write the file to the output stream.
            ImageIO.write(bufferedImage, QR_IMAGE_TYPE, byteArrayOutputStream);

            assertTrue(byteArrayOutputStream.size() > 0);

            // Encode the image so we can view it from the web
            byte[] encodeBase64 = Base64.encodeBase64(byteArrayOutputStream.toByteArray());

            assertTrue("Image is not base64 encoded.", new String(encodeBase64, CHARSET_TYPE).matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$"));

            // Build the image source format so the browser can easily view the image.
            StringBuilder encodedImage = new StringBuilder();
            encodedImage.append("data:image/png;base64,");
            encodedImage.append(new String(encodeBase64, CHARSET_TYPE));

            // Return the image string result.
            assertTrue("Encoded String does not contain the image data type", encodedImage.toString().startsWith("data:image/png;base64,"));
        } catch (WriterException | IOException e) {
            fail("Unable to write the QR Image: {}" + e);
        }

    }
}

