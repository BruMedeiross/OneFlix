package com.bms.oneflix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bms.oneflix.model.Movie;
import com.bms.oneflix.model.MovieDetail;
import com.bms.oneflix.util.ImageDownloadTask;
import com.bms.oneflix.util.MovieDetailTask;

import java.util.ArrayList;
import java.util.List;

//movie activity precisa implementar os detalhes do filme
public class MovieActivity extends AppCompatActivity implements MovieDetailTask.MovieDetailLoader {

    private TextView text_title;
    private TextView text_desc;
    private TextView text_cast;
    private RecyclerView rvsimilar;
    
    private MovieAdapter movieAdapter;
    private ImageView imgCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);


        text_title = findViewById(R.id.text_view_title);
        text_desc = findViewById(R.id.text_view_desc);
        text_cast = findViewById(R.id.text_view_cast);
        rvsimilar = findViewById(R.id.rv_similar);
        imgCover = findViewById(R.id.imageview_cover);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_arrow_left_24);
            getSupportActionBar().setTitle(null);
        }


        /*
        //ANTES - acessava o image view e trocava o desenhavel pela nova imagem atribuida
        LayerDrawable drawable =
                (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.shadows);
                                    //contextcompat:
                                    //é um recurso que permite a compatibilidade  com dispositivos antigos
                                    //layerdrawable para considerar suas config.
        if (drawable != null) {
            Drawable movieCover = ContextCompat.getDrawable(this, R.drawable.movie_4);
            // acessando por meio do id para mudar as propriedades
            drawable.setDrawableByLayerId(R.id.cover_drawable, movieCover);

            ((ImageView) findViewById(R.id.imageview_cover)).setImageDrawable(drawable); }

        }
*/
            //resumindo: pegue  a imagem movie
            // -> set/insira a imagem pelo id no layer(modelo de desenhavel)
            // -> subistitua-a na visualização no image view

                        //ANTES - DADOS FALSOS ANTES SETADOS DIRETAMENTE NO CODIGO
                        //QUE SERÃO IMPLEMENTADOS AGORA VIA ON RESULT
                        // text_title.setText("Batman Begins");
                        // text_desc.setText("O jovem Bruce Wayne cai em um poço e é atacado  por morcegos. Bruce descobre então loren ipson dolor, loren ipson dolor. loren ipson dolorloren ipson dolorloren ipson dolorloren ipson dolorloren ipson dolorloren ipson dolorloren ipson dolorloren ipson dolorloren ipson dolorloren ipson dolorloren ipson dolorloren ipson dolor ");
                        //text_cast.setText(getString(R.string.cast, "Nome Ator, " + "Nome Atriz, " + "Nome Elenco."));

        //aqui informa a quantodade de item que devem ser apresentados
        List<Movie> movies = new ArrayList<>();
                              //antes - DADOS FALSOS ANTES QUE SERAO SUBISTITUIDOS PELOS DADOS DA API
                                //for (int i = 0; i < 30; i++) { Movie movie = new Movie();movies.add(movie);}
        movieAdapter = new MovieAdapter(movies);
        rvsimilar.setAdapter(movieAdapter);

        //formato do layout grid, 3 colunas
        rvsimilar.setLayoutManager(new GridLayoutManager(this, 3));

        //aqui faz a requisição da informação da api de acordo com o id do click na activitymain
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt("id");
            MovieDetailTask movieDetailTask = new MovieDetailTask(this);
            movieDetailTask.setMovieDetailLoader(this);
            movieDetailTask.execute("https://tiagoaguiar.co/api/netflix/" + id);
            }

    }

    //metodo - espera que valide um id(no caso seta home)
    //finish - destroi a activity atual e volta a pág anterior
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    // implementada la em cima ^ - traz a requisição dos dados  pronta...
    //que compoe: text, descricao, elenco, capa e uma lista de opções semelhantes
        @Override
        public void onResult(MovieDetail movieDetail){
            text_title.setText(movieDetail.getMovie().getTitle());
            text_desc.setText(movieDetail.getMovie().getDesc());
            text_cast.setText(movieDetail.getMovie().getCast());

            //aki ira adicionar a capa do filme clicado com seus detalhes e sobras na imag
            ImageDownloadTask imageDownloadTask = new ImageDownloadTask(imgCover);
            imageDownloadTask.setShadowEnabled(true);
            imageDownloadTask.execute(movieDetail.getMovie().getCoverUrl());

            movieAdapter.setMovies(movieDetail.getMoviesSimilar());
            movieAdapter.notifyDataSetChanged();
        }


        //Utilizamos o adapter para mostrar o que e como será visto
        //no recycler na pág movie-activity - NO CASO OPÇOES SEMELHANTES
        private static class MovieHolder extends RecyclerView.ViewHolder {

            final ImageView imageViewCover;
            //setando via ID o XML movie_item_similar na classe criada:image view cover
            //o recycler reproduzirá então o conteúdo do movie_item_similar
            //neste caso só tem uma image view

            MovieHolder(@NonNull View itemView) {
                super(itemView);
                imageViewCover = itemView.findViewById(R.id.imageview_view_cover);
            }

        }

        //ADAPTER da rv - filmes semelhantes
        //VERTICAL - aprensentara a imagem dos filmes em formato de grid
        //para que sejam recycladas
        private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {

            private List<Movie> movies;

            private MovieAdapter(List<Movie> movies) {
                this.movies = movies;
            }

            void setMovies(List<Movie> movies) {
                this.movies.clear();
                this.movies.addAll(movies);

            }

            @NonNull
            @Override
            //Qual o layout que será manipulado por onCreateViewHolder? MOVIE_ITEM_SIMILAR
            //trazendo do layout MOVIE_ITEM_SIMILAR e que contem nele, no caso somete a imagem
            public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MovieHolder(getLayoutInflater().
                        inflate(R.layout.movie_item_similar, parent, false));
            }

            //aqui seta as imagens na area de opçoes semelhante
            @Override
            public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
                Movie movie = movies.get(position);
                new ImageDownloadTask(holder.imageViewCover).execute(movie.getCoverUrl());

            }
                //ANTES - FOI APAGADO
                //holder.imageViewCover.setImageResource(movie.getCoverUrl());
                //setImageResource() - recebe o conteuco referente a um resouce Id existente na pasta res/drawable.
                //depois executou por dados remotos


            @Override
            //Responsável por receber quantos itens de uma lista serão reciclados ".size"
            public int getItemCount() {
                return movies.size();
            }
        }


    }


