package com.bms.oneflix.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bms.oneflix.model.Category;
import com.bms.oneflix.model.Movie;

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

// tarefa que fara em background
//processo paralelo para não travar a tela principal

//AsyncTask<Params, Progress, Result>

public class CategoryTask extends AsyncTask<String, Void, List<Category>> {

    private final WeakReference<Context> context;
    /* this field leaks a context obj... potencial: referencia fraca
    destruir a atividade se necessario, ou pelo ciclo de vida ou outro motivo
    feita pelo proprio android
    */
    private ProgressDialog dialog;
    //criando progress dialog
    private CategoryLoader categoryLoader;

    public CategoryTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    //
    public  void setCategoryLoader(CategoryLoader categoryLoader){
        this.categoryLoader =  categoryLoader;
    }

    //main - thread
    //aqui parecera a barra de carregamento
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = this.context.get();

        if (context != null)
            dialog = ProgressDialog.show(context, "CARREGANDO...", "", true);
    }

    //thread background - executado paralelamente bscando o json da internet
    @Override
    protected List<Category> doInBackground(String... params) {

        String url = params[0];
        //param 0: esta acessando a URL informada do execute(main activity)

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

            //lista pronta de categorias (string) -> vira do json novamente
            //converte a string em json
            List<Category> categories = getCategories(new JSONObject(jsonAsString));

            //fechando conexão na internet apos obter os dados
            in.close();

            return categories;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    private List<Category> getCategories(JSONObject json) throws JSONException {

        //metodo conversor de json para objetos java
        List<Category> categories = new ArrayList<>();

        //pegando 1 dado:  category -> title
        JSONArray categoryArray = json.getJSONArray("category");
        for (int i = 0; i < categoryArray.length(); i++) {
            JSONObject category = categoryArray.getJSONObject(i);
            //propriedades que estão em category - escrever como esta no json - title
            String title = category.getString("title");

            //pegando 2 dado: movie -> cover_url
            List<Movie> movies = new ArrayList<>();
            JSONArray movieArray = category.getJSONArray("movie");
            for (int j = 0; j < categoryArray.length(); j++) {
                JSONObject movie = movieArray.getJSONObject(j);

                //propriedades que estão em movie - escrever como esta no json - cover_url
                String coverUrl = movie.getString("cover_url");
                int id = movie.getInt("id");

                Movie movieObj = new Movie();
                movieObj.setCoverUrl(coverUrl);
                movieObj.setId(id);

                movies.add(movieObj);
            }

            //categoria atual - categoryObj
            Category categoryObj = new Category();
            //setando os dados coletados title e movie
            categoryObj.setName(title);
            categoryObj.setMovies(movies);

            categories.add(categoryObj);
        }
        return categories;
    }


    //main - thread
    @Override
    protected void onPostExecute(List<Category> categories) {
        super.onPostExecute(categories);
        dialog.dismiss();
        //listener - se atribuir executar o metodo on resul
        if (categoryLoader!= null)
            categoryLoader.onResult(categories);
    }

    //METODO CONVERSOR - RETORNA STRING
    //BYTES LIDOS - STREAM - STRING
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
    //escuta as alterações
    public interface CategoryLoader{
        void onResult(List<Category>categories);
    }

}
