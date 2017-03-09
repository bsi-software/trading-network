package com.bsiag.ethereum.fxtradingnetwork.events.account;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeGenerator {

  public static final String IMAGE_FILE_TYPE = "png";

  public static byte[] generate(String address, int size) {
    try {
      Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
      hintMap.put(EncodeHintType.MARGIN, 0);
      hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
      hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

      QRCodeWriter qrWriter = new QRCodeWriter();
      BitMatrix qrMatrix = qrWriter.encode(address, BarcodeFormat.QR_CODE, size, size, hintMap);
      int width = qrMatrix.getWidth();
      BufferedImage image = new BufferedImage(width, width,
          BufferedImage.TYPE_INT_RGB);
      image.createGraphics();

      Graphics2D graphics = (Graphics2D) image.getGraphics();
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, width, width);
      graphics.setColor(Color.BLACK);

      for (int i = 0; i < width; i++) {
        for (int j = 0; j < width; j++) {
          if (qrMatrix.get(i, j)) {
            graphics.fillRect(i, j, 1, 1);
          }
        }
      }

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ImageIO.write(image, IMAGE_FILE_TYPE, bos);

      return bos.toByteArray();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
