package com.bms.oneflix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bms.oneflix.model.Category;
import com.bms.oneflix.model.Movie;
import com.bms.oneflix.util.CategoryTask;
import com.bms.oneflix.util.ImageDownloadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryTask.CategoryLoader {

    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.rv_main);
        //aqui a lista que apresentaram as categorias
        List<Category> categories = new ArrayList<>();

        //apagado - vide la em baixo


        //adapter principal - main activity
        // layout principal que reciclara as categorias
        // linear layout - serão reciclados verticalmente
        mainAdapter = new MainAdapter(categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mainAdapter);


        CategoryTask categoryTask = new CategoryTask(this);
        //instanciando tarefa json

        categoryTask.setCategoryLoader(this);
        //implementando o loader para ouvir os eventos de successo assincronos

        categoryTask.execute("https://tiagoaguiar.co/api/netflix/home");
        //execute: onde devo buscar a info da api via https...

    }

    @Override
    public void onResult(List<Category> categories) {
        mainAdapter.setCategories(categories);
        mainAdapter.notifyDataSetChanged();}
        //notify - notificando as mudancas
        // dados chegaram na main activity


    //categoriy holder
    private static class CategoryHolder extends RecyclerView.ViewHolder{
        //um ttitulo e uma rv de filmes
        TextView textViewTitle;
        RecyclerView recyclerViewMovie;

        //setamos o ID os itens contidos no Caterogy item
        //um texto e uma recyclerv de imagem
        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            recyclerViewMovie = itemView.findViewById(R.id.recycler_view_movie);
        }
    }

    // mainadapter - adapter é o layout principal
    // Utilizamos o adapter para mostrar o que e como será visto
    // como apresentará - vertical
    // o que apresentara  - titulo da categoria e lista de filmes -
    private class MainAdapter extends RecyclerView.Adapter<CategoryHolder> {

        private List<Category> categories;

        private MainAdapter(List<Category> categories){
            this.categories = categories;
        }


        @NonNull
        @Override
        //Qual o layout que será manipulado por onCreateViewHolder?
        //category item
        public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CategoryHolder(getLayoutInflater().inflate(R.layout.category_item, parent ,false));
        }


        //aqui ira passar as propriedades que serao recicladas na category
        // horizontalmente
        //Titulo (getName)
        //Imagem (getMovie)
        @Override
        public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
            Category category = categories.get(position); //posição
            holder.textViewTitle.setText(category.getName());//titulo da categoria
            holder.recyclerViewMovie.setAdapter(new MovieAdapter(category.getMovies()));//imagem
            holder.recyclerViewMovie.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.HORIZONTAL,false));
            //ESSE GERENCIADOR DE LAYOUR - aprensetara texto+posição+imagem
            //sua orientacao: SERÃO RECICLADOS HORIZONTALMENTE
        }

        @Override
        //Responsável por receber quantos itens de uma lista serão reciclados ".size"
        public int getItemCount() {
            return categories.size();
        }

        void setCategories(List<Category> categories) {
            this.categories.clear();
            this.categories.addAll(categories);
        }
    }


    //MOVIE HOLDER - somente a parte de filme
    private static class MovieHolder extends RecyclerView.ViewHolder{

        final ImageView imageViewCover;
        //setando via ID o XML(movieitem) na classe criada:imaggeviewcover
        //o xml repoduzira a imagem contida no res drawable

        public MovieHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
        //onitemclicklistener: é o evento que escuta os cliques dos adapters principais

            super(itemView);
            imageViewCover = itemView.findViewById(R.id.image_view_cover);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(getAdapterPosition());
                }
            });
        }

    }

    //movie holder = adaper de filmes
    //horizontal aprensentara os filmes
    //implements - onitem
    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> implements OnItemClickListener{

        private List<Movie> movies;

        private MovieAdapter(List<Movie> movies){
            this.movies = movies;
        }

        //click - abrir a activity passa o id da activit de detalhes para fazer a requisção
        @Override
        public void onClick(int position) {
            //antes de ligar as activitys como so tem ate o id o restante ficara invalido
            if(movies.get(position).getId()<=3){

            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
            //sai da main activity para a movie, passando o id selecionado como parametro
            intent.putExtra("id", movies.get(position).getId());
            startActivity(intent);
            }
        }

        @NonNull
        @Override
        //Qual o layout que será manipulado por onCreateViewHolder? movie item
        //trazendo do layout movie item o que contem nele, no caso somete a imagem
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.movie_item, parent ,false);
            return new MovieHolder(view, this);
        }

        //aqui seta a imagem na classe movie via downloadtas
        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
            Movie movie = movies.get(position);
            new ImageDownloadTask(holder.imageViewCover).execute(movie.getCoverUrl());}
                    //ANTES - a imagem era setada localmente na pasta res
                    //holder.imageVIewCover.setImageResource(movie.getCoverUrl());
                    //setImageResource() - recebe o conteuco referente a um resouce Id existente na pasta res/drawable.
           //new ImageDownloadTask(holder.imageViewCover).execute(movie.getCoverUrl());
            //AGORA = pelo imagedownload,nao busca localmente, mas via url



        @Override
        //Responsável por receber quantos itens de uma lista serão reciclados ".size"
        public int getItemCount() {
            return movies.size();
        }
    }

    //qual foi o envento de click
    interface OnItemClickListener{
        void onClick (int position);
    }





}

 /*
                                     EXCLUIDA ESTA PARTE QUE TINHA DADOS FAKES
                                     PARA INSERIR OS DADOS REAIS VIA API

                                      for (int j = 0; j < 1; j++) {
                                            Category category = new Category();
                                            category.setName("Categoria " + j);
                                            //looping de categoria

                                            //aqui a lista de filmes desta categoria
                                            List<Movie> movies = new ArrayList<>();
                                            for (int i = 0; i < 15; i++) {
                                                Movie movie = new Movie();
                                               // movie.setCoverUrl(R.drawable.movie_4);
                                                movies.add(movie);
                                                //looping de filmes

                                                category.setMovies(movies);
                                                categories.add(category);
                                                //seta o filme na categoria
                                            }
                                        }
                                        */
