package com.bms.oneflix.model;


import java.util.List;

//aqui contem as propriedades que contem a parte de filmes similares
//no caso um filme(vindo da classe movie com titulo, desc,cast)
// e uma lista com filmes similares
public class MovieDetail {
    private final Movie movie;
    private final List<Movie> moviesSimilar;

    //contrutor
    public MovieDetail(Movie movie, List<Movie> moviesSimilar) {
        this.movie = movie;
        this.moviesSimilar = moviesSimilar;
    }

    public Movie getMovie() {
        return movie;
    }

    public List<Movie> getMoviesSimilar() {
        return moviesSimilar;
    }

}
