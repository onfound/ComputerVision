package com.example.ilyad.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.opencv.android.Utils;

import org.opencv.core.*;

import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Detect {


    public Bitmap peopleDetect (Bitmap bitmap ) {
//        Bitmap bitmap = null;
        float execTime;
        // Закачиваем фотографию
//            URL url = new URL( path );
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();


//            BitmapFactory.Options opts = new BitmapFactory.Options();
//            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            bitmap = BitmapFactory.decodeResource(get, null, opts);
        long time = System.currentTimeMillis();

        // Создаем матрицу изображения для OpenCV и помещаем в нее нашу фотографию
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        // Переконвертируем матрицу с RGB на градацию серого
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY, 4);
        HOGDescriptor hog = new HOGDescriptor();

        //Получаем стандартный определитель людей и устанавливаем его нашему дескриптору
        MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector();
        hog.setSVMDetector(descriptors);

        // Определяем переменные, в которые будут помещены результаты поиска ( locations - прямоугольные области, weights - вес (можно сказать релевантность) соответствующей локации)
        MatOfRect locations = new MatOfRect();
        MatOfDouble weights = new MatOfDouble();

        // Собственно говоря, сам анализ фотографий. Результаты запишутся в locations и weights
        hog.detectMultiScale(mat, locations, weights);
        execTime = ( (float)( System.currentTimeMillis() - time ) ) / 1000f;

        //Переменные для выделения областей на фотографии
        Point rectPoint1 = new Point();
        Point rectPoint2 = new Point();
        Scalar fontColor = new Scalar(0, 0, 0);
        Point fontPoint = new Point();

        // Если есть результат - добавляем на фотографию области и вес каждой из них
        if (locations.rows() > 0) {
            List<Rect> rectangles = locations.toList();
            int i = 0;
            List<Double> weightList = weights.toList();
            for (Rect rect : rectangles) {
                float weigh = weightList.get(i++).floatValue();

                rectPoint1.x = rect.x;
                rectPoint1.y = rect.y;
                fontPoint.x  = rect.x;
                fontPoint.y  = rect.y - 4;
                rectPoint2.x = rect.x + rect.width;
                rectPoint2.y = rect.y + rect.height;
                final Scalar rectColor = new Scalar( 0  , 0 , 0  );

                // Добавляем на изображения найденную информацию

                Imgproc.rectangle(mat, rectPoint1, rectPoint2, rectColor, 2);
                Core.putText(mat,
                        String.format("%1.2f", weigh),
                        fontPoint, Core.FONT_HERSHEY_PLAIN, 1.5, fontColor,
                        2, Core.LINE_AA, false);

            }
        }
        fontPoint.x = 15;
        fontPoint.y = bitmap.getHeight() - 20;
        // Добавляем дополнительную отладочную информацию
        Core.putText(mat,
                "Processing time:" + execTime + " width:" + bitmap.getWidth() + " height:" + bitmap.getHeight() ,
                fontPoint, Core.FONT_HERSHEY_PLAIN, 1.5, fontColor,
                2, Core.LINE_AA, false);
        Utils.matToBitmap( mat , bitmap );
        return bitmap;
    }
}
