package com.rhodes.demo.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.*;

/**
 * Created by xiet on 2015/7/24.
 */
public class PictureUtil {

    /**
     * 把bitmap转换成String
     *
     * @param filePath
     * @return
     */
    public static String bitmapToString(String filePath) {

        Bitmap bm = getSmallBitmap(filePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);

    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param imagesrc
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 根据路径删除图片
     *
     * @param path
     */
    public static void deleteTempFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static Bitmap revitionImageSize(String path, int sizeW, int sizeH) throws IOException {
        File file = new File(path);
        // 取得图片
        InputStream temp = new FileInputStream(file);
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 这个参数代表，不为bitmap分配内存空间，只记录一些该图片的信息（例如图片大小），说白了就是为了内存优化
        options.inJustDecodeBounds = true;
        // 通过创建图片的方式，取得options的内容（这里就是利用了java的地址传递来赋值）
        BitmapFactory.decodeStream(temp, null, options);
        // 关闭流
        temp.close();

        // 生成压缩的图片
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            // 这一步是根据要设置的大小，使宽和高都能满足
            if ((options.outWidth >> i <= sizeW) && (options.outHeight >> i <= sizeH)) {
                // 重新取得流，注意：这里一定要再次加载，不能二次使用之前的流！
                temp = new FileInputStream(file);
                // 这个参数表示 新生成的图片为原始图片的几分之一。
                options.inSampleSize = (int) Math.pow(2.0D, i);
                // 这里之前设置为了true，所以要改为false，否则就创建不出图片
                options.inJustDecodeBounds = false;

                bitmap = BitmapFactory.decodeStream(temp, null, options);

                temp.close();
                break;
            }
            i += 1;
        }
        return bitmap;
    }

    public static Bitmap revitionImageSize(String path, int inSampleSize) throws IOException {
        File file = new File(path);

        // 生成压缩的图片
        Bitmap bitmap = null;
        // 取得图片
        InputStream temp = new FileInputStream(file);
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // 这个参数代表，不为bitmap分配内存空间，只记录一些该图片的信息（例如图片大小），说白了就是为了内存优化
            options.inJustDecodeBounds = true;
            // 通过创建图片的方式，取得options的内容（这里就是利用了java的地址传递来赋值）
            BitmapFactory.decodeStream(temp, null, options);
            // 关闭流
            temp.close();


            // 这一步是根据要设置的大小，使宽和高都能满足
            // 重新取得流，注意：这里一定要再次加载，不能二次使用之前的流！
            temp = new FileInputStream(file);
            // 这个参数表示 新生成的图片为原始图片的几分之一。
            options.inSampleSize = inSampleSize;
            // 这里之前设置为了true，所以要改为false，否则就创建不出图片
            options.inJustDecodeBounds = false;

            bitmap = BitmapFactory.decodeStream(temp, null, options);

        } catch (Exception e) {

        } finally {
            if (temp != null) {
                // 关闭流
                temp.close();
            }
        }

        return bitmap;
    }

    /**
     * 添加到图库
     */
    public static void galleryAddPic(Context context, String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static void savePng2Jpeg(String path, String outPath) {
        if (path != null) {
            FileOutputStream fos = null;
            try {
                Bitmap bm = PictureUtil.revitionImageSize(path, 1);
                fos = new FileOutputStream(outPath);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
