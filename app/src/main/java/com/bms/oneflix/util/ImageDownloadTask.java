package com.bms.oneflix.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bms.oneflix.R;
import com.bms.oneflix.model.Category;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
//realizando os downloads de imagens de maneira assincrona - via url
//o que espera url, um mapa de bits - bitmap
public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewWeakReference;
    //atribuir as sombras na imagem
    private boolean shadowEnabled;

    //contrutor image view
    public ImageDownloadTask(ImageView imageView) {
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    public void setShadowEnabled(boolean shadowEnabled) {
        this.shadowEnabled = shadowEnabled;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urlImg = params[0];

        HttpsURLConnection urlConnection = null;
        //url null para fechar conexao abaixo
        try {
            URL url = new URL(urlImg);

            urlConnection = (HttpsURLConnection) url.openConnection();
            //abrindo conexão

            int statusCode = urlConnection.getResponseCode();
            //recebendo status
            if (statusCode != 200)
                return null;

            //convertento inputstring num bitmap
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null)
                //bitmapfactory - classe que cria bitmap
                return BitmapFactory.decodeStream(inputStream);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
            //fechando conexao
        }

        return null;
    }


    @Override

    protected void onPostExecute(Bitmap bitmap) {

        if (isCancelled())
            bitmap = null;

        ImageView imageView = imageViewWeakReference.get();

        if (imageView != null && bitmap != null) {
            //atribuindo as combras as  capas
            if (shadowEnabled) {
                LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(imageView.getContext(),
                        R.drawable.shadows);
                if (drawable != null){
                    BitmapDrawable bitmapdrawable = new BitmapDrawable(bitmap);
                    drawable.setDrawableByLayerId(R.id.cover_drawable, bitmapdrawable);
                    imageView.setImageDrawable(drawable);
                }

            }else{

                if (bitmap.getWidth() < imageView.getWidth() || bitmap.getHeight() < imageView.getHeight()) {
                    //redimencionando todos os bitmap para padronizar o tam.
                    Matrix matrix = new Matrix();
                    matrix.postScale(
                            (float) imageView.getWidth() / (float) bitmap.getWidth(),
                            (float) imageView.getHeight() / (float) bitmap.getHeight()
                    );
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                    //escalando bitmap: recebe o novo tamanho
                }
                imageView.setImageBitmap(bitmap);
                //apos tratamento da imagem seta nos campo
            }
        }
    }
}
