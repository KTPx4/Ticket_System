package modules;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static String getPathFromUri(Context context, Uri uri) {
        String filePath = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String fileName = cursor.getString(index);
                File file = new File(context.getCacheDir(), fileName);

                try (InputStream input = context.getContentResolver().openInputStream(uri);
                     FileOutputStream output = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = input.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                    filePath = file.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return filePath;
    }

}
