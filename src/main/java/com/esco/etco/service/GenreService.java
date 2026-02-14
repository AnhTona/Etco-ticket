package com.esco.etco.service;

import com.esco.etco.entity.Genre;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.genre.ResCreateGenreDTO;
import com.esco.etco.entity.response.genre.ResUpdateGenreDTO;
import com.esco.etco.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface GenreService {

    Optional<Genre> fetchByid(long id);

    ResultPaginationDTO getAllGenre(Specification<Genre> spec, Pageable pageable);

    void deleteGenre(long id) throws IdInvalidException;

    ResCreateGenreDTO convertToResCreateGenreDTO(Genre genre);

    ResUpdateGenreDTO convertToResUpdateGenreDTO(Genre genre);
}
