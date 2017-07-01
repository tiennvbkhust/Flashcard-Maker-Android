package com.piapps.flashcard.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.MenuItem;
import android.widget.ImageView;

import com.piapps.flashcard.R;
import com.piapps.flashcard.model.Flashcard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by abduaziz on 2/15/17.
 */

public class Utils {

    public static String LABEL_IMPORTANT = "important";
    public static String LABEL_TODO = "todo";
    public static String LABEL_DICTIONARY = "dictionary";

    public static List<Flashcard> getFlashcardsInAlphabeticalOrder(List<Flashcard> list) {

        Collections.sort(list, new Comparator<Flashcard>() {
            @Override
            public int compare(Flashcard flashcard, Flashcard t1) {
                if (flashcard.getTitle().toUpperCase().toCharArray()[0] > t1.getTitle().toUpperCase().toCharArray()[0])
                    return 1;
                else if (flashcard.getTitle().toUpperCase().toCharArray()[0] < t1.getTitle().toUpperCase().toCharArray()[0])
                    return -1;
                else
                    return 0;

            }
        });

        return list;
    }

    public static List<Flashcard> getMostUsedFlashcards(List<Flashcard> list) {

        Collections.sort(list, new Comparator<Flashcard>() {
            @Override
            public int compare(Flashcard flashcard, Flashcard t1) {
                if (Integer.parseInt(flashcard.getUseCount()) < Integer.parseInt(t1.getUseCount()))
                    return 1;
                else if (Integer.parseInt(flashcard.getUseCount()) > Integer.parseInt(t1.getUseCount()))
                    return -1;
                else
                    return 0;

            }
        });

        int n = 10;
        if (list.size() < n)
            n = list.size();
        return getFlashcardsInAlphabeticalOrder(list.subList(0, n));
    }

    public static List<Flashcard> getFlashcardsByLabel(List<Flashcard> list, String label) {
        List<Flashcard> imList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLabel().contains(label))
                imList.add(list.get(i));
        }
        return getFlashcardsInAlphabeticalOrder(imList);
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    public static String intToHexColor(int color) {
        String hexColor = String.format("#%06X", (0xFFFFFF & color));
        return hexColor;
    }

    public static void tintMenuIcon(Context context, MenuItem item,@ColorInt int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, color);
        item.setIcon(wrapDrawable);
    }

    public static String saveToInternalStorage(Bitmap bitmapImage,Context context,String imageName){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,imageName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static Bitmap loadImageFromStorage(String path, String imageName) {
        try {
            File f=new File(path, imageName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteExistingImage(String path,String imageName){
        File fdelete = new File(path, imageName);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + path + " " + imageName);
            } else {
                System.out.println("file not Deleted :" + path);
            }
        }
    }

}
