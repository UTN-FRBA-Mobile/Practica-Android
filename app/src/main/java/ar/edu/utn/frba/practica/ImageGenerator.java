package ar.edu.utn.frba.practica;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.ByteArrayOutputStream;

/**
 * Created by emanuel on 10/9/17.
 */

public class ImageGenerator {

    /// Genera una imagen y retorna los bytes para ser escritos en un archivo
    public static byte[] generateImage() {
        int size = 300;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bitmap = Bitmap.createBitmap(size, size, conf); // this creates a MUTABLE bitmap
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        canvas.rotate((float)Math.random() * 180 - 90, size / 2, size / 2);
        paint.setColor(0xFF000000 + (int)(Math.random() * 0xFFFFFF));
        paint.setTextSize((float)Math.random() * 20 + 30);
        canvas.drawText("Imagen Generada", 1, size / 2, paint);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
}
