package com.bms.oneflix.model;

import java.util.List;

//aqui as propriedades da area de categoria
// titulo(categ) e lista de filmes
public class Category {
    private String name;
    private List<Movie> movies;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
