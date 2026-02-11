package com.esco.etco.service.impl;

import com.esco.etco.entity.Genre;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.genre.ResCreateGenreDTO;
import com.esco.etco.entity.response.genre.ResUpdateGenreDTO;
import com.esco.etco.repository.GenreRepository;
import com.esco.etco.service.GenreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository){
        this.genreRepository = genreRepository;
    }

    @Override
    public Optional<Genre> fetchByid(long id) {
        return this.genreRepository.findById(id);
    }

    @Override
    public ResultPaginationDTO getAllGenre(Specification<Genre> spec, Pageable pageable) {
        Page<Genre> pageGenre = this.genreRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageGenre.getTotalPages());
        mt.setTotal(pageGenre.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(pageGenre.getContent());

        return rs;
    }


    @Override
    public void deleteGenre(long id) {
        this.genreRepository.deleteById(id);
    }

    @Override
    public ResCreateGenreDTO convertToResCreateGenreDTO(Genre genre) {
        genre = this.genreRepository.save(genre);
        ResCreateGenreDTO res = new ResCreateGenreDTO();
        res.setCreatedBy(genre.getCreatedBy());
        res.setCreatedAt(genre.getCreatedAt());
        return res;
    }

    @Override
    public ResUpdateGenreDTO convertToResUpdateGenreDTO(Genre genre) {
        genre = this.genreRepository.save(genre);
        ResUpdateGenreDTO res = new ResUpdateGenreDTO();
        res.setUpdatedBy(genre.getUpdatedBy());
        res.setUpdatedAt(genre.getUpdatedAt());
        return res;
    }
}
