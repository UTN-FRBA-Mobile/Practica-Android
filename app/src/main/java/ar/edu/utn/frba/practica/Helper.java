package ar.edu.utn.frba.practica;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by emanuel on 17/9/17.
 */

public class Helper {

    static String readInputStream(InputStream inputStream) {
        try {
            byte buffer[] = new byte[1024];
            int l;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while ((l = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, l);
            }
            return outputStream.toString("UTF-8");
        } catch (IOException e) {
            return "";
        }
    }

    static void replace(StringBuilder buffer, String searchValue, String newValue) {
        int index = buffer.indexOf(searchValue);
        if (index >= 0) {
            buffer.replace(index, index + searchValue.length(), newValue);
        }
    }
}
