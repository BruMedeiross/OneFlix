package com.bms.oneflix.util;

import android.app.ProgressDialog;
import android.content.Context;

import android.os.AsyncTask;


import com.bms.oneflix.model.Movie;
import com.bms.oneflix.model.MovieDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

//tipo de retorno objeto de detalhes
public class MovieDetailTask extends AsyncTask <String, Void, MovieDetail> {

    private final WeakReference<Context> context;
    //context - possibilita a chamada de operações na aplicação

    private ProgressDialog dialog;
    //criando progress dialog

    private MovieDetailLoader movieDetailLoader;
    //detalhes do filme

    //contrutor contexto
    public MovieDetailTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    public void setMovieDetailLoader(MovieDetailLoader movieDetailLoader) {
        this.movieDetailLoader = movieDetailLoader;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = this.context.get();
        //SETA A DIALOG
        if (context != null)
            dialog = ProgressDialog.show(context, "CARREGANDO...", "", true);
    }

    @Override
    protected MovieDetail doInBackground(String... params) {

        String url = params[0];
        //param 0: esta acessando a URL informada do execute(main activity???)

        try {
            URL requesturl = new URL(url);

            //abrindo a conexao - open conection
            HttpsURLConnection urlConnection = (HttpsURLConnection) requesturl.openConnection();

            //tempo de espera de leitura
            urlConnection.setReadTimeout(2000);
            //se internet caiu qto tempo deve espera para retornar erro == readTimeOut
            urlConnection.setConnectTimeout(2000);

            //pegando a resposta do servidor
            int responseCode = urlConnection.getResponseCode();

            //status code - resposta do servidor
            // se for maior q 400 é algum tipo de erro
            if (responseCode > 400) {
                throw new IOException("Erro na comunicação do servidor");
            }

            //se estiver ok = url aberta, coneao feita e status code ok
            // input stream - sequencia de bytes que dispepeito a uma info(imagem, textos, etc)
            InputStream inputStream = urlConnection.getInputStream();

            //CONVERTE STREAM PARA STRING, NESTE CASO
            BufferedInputStream in = new BufferedInputStream(inputStream);

            //formato json como string
            String jsonAsString = toString(in);

            //lista pronta de detalhes do filme
            //converte a string em json
            MovieDetail movieDetail = getMovieDetail(new JSONObject(jsonAsString));

            //fechando conexão na internet apos obter os dados
            in.close();

            //retorna
            return movieDetail;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    //criando um metodo novo e converter as propriedds
    private MovieDetail getMovieDetail(JSONObject json) throws JSONException {

        //pegar as propriedds do json que precisa
        int id = json.getInt("id");
        String title = json.getString("title");
        String desc = json.getString("desc");
        String cast = json.getString("cast");
        String coverUrl = json.getString("cover_url");

        //apos pegar essas propriedades
        //cria uma lista de filmes similares
        List<Movie> movies = new ArrayList<>();
        JSONArray movieArray = json.getJSONArray("movie");
        for (int j = 0; j < movieArray.length(); j++) {
            JSONObject movie = movieArray.getJSONObject(j);

            //cada filme similar pelo id e capa(url)
            String c = movie.getString("cover_url");
            int idSimilar = movie.getInt("id");

            Movie similar = new Movie();
            similar.setId(idSimilar);
            similar.setCoverUrl(c);

            //colecao de filmes similares
            movies.add(similar);
        }
        //cria o filme com os detalhes
        Movie movie = new Movie();
        movie.setId(id);
        movie.setCoverUrl(coverUrl);
        movie.setTitle(title);
        movie.setDesc(desc);
        movie.setCast(cast);

        return new MovieDetail(movie, movies);
        //por fim retornara um filme com detalhes, e uma lista  de filmes similares
    }

    //executa o metodo loader que é o onResult
    @Override
    protected void onPostExecute(MovieDetail movieDetail) {
        super.onPostExecute(movieDetail);
        dialog.dismiss();

        if (movieDetailLoader != null)
            movieDetailLoader.onResult(movieDetail);
    }

    private String toString(InputStream is) throws IOException {

        //arrey de bytes
        byte[] bytes = new byte[1024];
        //saida de dados - outputstream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int lidos;
        while ((lidos = is.read(bytes)) > 0) {
            baos.write(bytes, 0, lidos);
        }
        //assim traformando todos esses bytes em caracteres em format de string
        return new String(baos.toByteArray());
    }

    //escuta as alterações - retorna um detalhe de filme
    public interface MovieDetailLoader{
        void onResult(MovieDetail movieDetail);
    }


}
