package com.esco.etco.controller.admin;

import com.esco.etco.entity.Genre;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.genre.ResCreateGenreDTO;
import com.esco.etco.entity.response.genre.ResUpdateGenreDTO;
import com.esco.etco.service.GenreService;
import com.esco.etco.util.annotation.ApiMessage;
import com.esco.etco.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping("/genres")
    @ApiMessage("Tạo thể loại sự kiện mới")
    public ResponseEntity<ResCreateGenreDTO> createGenre(@Valid @RequestBody Genre genre) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.genreService.convertToResCreateGenreDTO(genre));
    }

    @PutMapping("/genres")
    @ApiMessage("Cập nhật thể loại sự kiện")
    public ResponseEntity<ResUpdateGenreDTO> updateGenre(@Valid @RequestBody Genre genre) throws IdInvalidException {
        Optional<Genre> existing = this.genreService.fetchByid(genre.getId());
        if (existing.isEmpty()) {
            throw new IdInvalidException("Genre với id = " + genre.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.genreService.convertToResUpdateGenreDTO(genre));
    }

    @GetMapping("/genres/{id}")
    @ApiMessage("Lấy chi tiết thể loại sự kiện")
    public ResponseEntity<Genre> getGenreById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Genre> genre = this.genreService.fetchByid(id);
        if (genre.isEmpty()) {
            throw new IdInvalidException("Genre với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(genre.get());
    }

    @GetMapping("/genres")
    @ApiMessage("Lấy danh sách thể loại sự kiện")
    public ResponseEntity<ResultPaginationDTO> getAllGenres(
            @Filter Specification<Genre> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.genreService.getAllGenre(spec, pageable));
    }

    @DeleteMapping("/genres/{id}")
    @ApiMessage("Xoá thể loại sự kiện")
    public ResponseEntity<Void> deleteGenre(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Genre> genre = this.genreService.fetchByid(id);
        if (genre.isEmpty()) {
            throw new IdInvalidException("Genre với id = " + id + " không tồn tại");
        }
        this.genreService.deleteGenre(id);
        return ResponseEntity.ok(null);
    }
}