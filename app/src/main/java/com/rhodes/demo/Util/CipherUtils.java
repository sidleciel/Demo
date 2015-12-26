package com.rhodes.demo.Util;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * Created by lala on 15/2/12.
 */
public class CipherUtils {
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    /**
     * decoderBase64File:(将base64字符解码保存文件). <br/>
     *
     * @param base64Code 编码后的字串
     * @param savePath   文件保存路径
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static void decoderBase64File(String base64Code, String savePath) throws Exception {

        byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }


    public static long doCheckCrc32(File fileName) {
        long checksum = 0;
        CheckedInputStream cis = null;
        try {
            long fileSize = 0;
            // Computer CRC32 checksum
            cis = new CheckedInputStream(
                    new FileInputStream(fileName), new CRC32());

            byte[] buf = new byte[128];
            while (cis.read(buf) >= 0) {
            }

            checksum = cis.getChecksum().getValue();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                cis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return checksum;
    }

    public static void main(String[] args) {
        System.out.println(CipherUtils.doCheckCrc32(new File("/Users/lala/Documents/pokemon_emerald.wld")));


    }

}


