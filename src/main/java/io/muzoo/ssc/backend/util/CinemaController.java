package io.muzoo.ssc.backend.util;

import io.muzoo.ssc.backend.Auditorium;
import io.muzoo.ssc.backend.Movie;
import io.muzoo.ssc.backend.Screening;
import io.muzoo.ssc.backend.Seat;
import io.muzoo.ssc.backend.repository.AuditoriumRepository;
import io.muzoo.ssc.backend.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class CinemaController {

    @Autowired
    MovieRepository movieRepository;
    @Autowired
    AuditoriumRepository auditoriumRepository;
    @PostMapping("/api/addMovie")
    public ResponseDTO addMovie(HttpServletRequest request) {
        try {
            String title = request.getParameter("title");
            String director = request.getParameter("director");
            String description = request.getParameter("description");
            int duration = Integer.parseInt(request.getParameter("duration"));
            String picUrl = request.getParameter("picUrl");

            movieRepository.save(new Movie(title, director, description, duration, picUrl));
            return ResponseDTO.builder().success(true).build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/api/deleteMovie")
    public ResponseDTO deleteMovieById(HttpServletRequest request) {
        try {
            int movie_id = Integer.parseInt(request.getParameter("id"));
            Optional<Movie> deleteMovie = movieRepository.findById(movie_id);
            if (deleteMovie.isPresent()) {
                Auditorium auditorium = deleteMovie.get().getAuditorium();
                if (auditorium != null) {
                    auditorium.setMovie(null);
                    auditoriumRepository.save(auditorium);
                }
            }
            movieRepository.deleteById(movie_id);
            return ResponseDTO.builder().success(true).build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    @GetMapping("/api/getMovies")
    public Iterator<Movie> getMovies() {
        return movieRepository.findAll().iterator();
    }

    @PostMapping("/api/addAuditorium")
    public ResponseDTO addAuditorium(HttpServletRequest request) {
        try {
            String name = request.getParameter("name");

            Auditorium auditorium = new Auditorium(name);

            // every auditorium have only 2 screening per day
            Screening screening1 = new Screening(12, auditorium);
            Screening screening2 = new Screening(16, auditorium);
            List<Screening> screeningList = List.of(new Screening[]{screening1, screening2});

            // every auditorium have only 30 seats
            char[] rows = new char[] {'A', 'B', 'C', 'D', 'E'};
            int[] numbers = new int[] {1,2,3,4,5,6};
            List<Seat> seats = new ArrayList<>();

            for (char row: rows) {
                for (int number: numbers) {
                    Seat newSeat = new Seat(row, number ,auditorium);
                    seats.add(newSeat);
                }
            }

            auditorium.setScreenings(screeningList);
            auditorium.setSeats(seats);

            auditoriumRepository.save(auditorium);
            return ResponseDTO.builder().success(true).build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/api/deleteAuditorium")
    public ResponseDTO deleteAuditoriumById(HttpServletRequest request) {
        try {
            int auditorium_id = Integer.parseInt(request.getParameter("id"));
            Optional<Auditorium> deleteAuditorium = auditoriumRepository.findById(auditorium_id);
            if (deleteAuditorium.isPresent()) {
                Movie movie = deleteAuditorium.get().getMovie();
                if (movie != null) {
                    movie.setAuditorium(null);
                    movieRepository.save(movie);
                }
            }
            auditoriumRepository.deleteById(auditorium_id);

            return ResponseDTO.builder().success(true).build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    @GetMapping("/api/getAuditoriums")
    public Iterator<Auditorium> getAuditoriums() {
        return auditoriumRepository.findAll().iterator();
    }

    @PostMapping("/api/getAuditoriumById")
    public Auditorium getAuditoriumsById(HttpServletRequest request) {
        Optional<Auditorium> auditoriumOpt = auditoriumRepository.findById(Integer.valueOf(request.getParameter("id")));

        if (auditoriumOpt.isEmpty()) {
            return null;
        }

        Auditorium auditorium = auditoriumOpt.get();

        if (auditorium.getEndSeatId() == 0) {
            int startId = Math.toIntExact(auditorium.getSeats().get(0).getId());
            auditorium.setStartSeatId(startId);
            auditorium.setEndSeatId(startId+29);
            auditoriumRepository.save(auditorium);
        }

        return auditorium;
    }

    @PostMapping("/api/link")
    public ResponseDTO link(HttpServletRequest request) {
        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int auditoriumId = Integer.parseInt(request.getParameter("auditoriumId"));

            Optional<Movie> movie = movieRepository.findById(movieId);
            Optional<Auditorium> auditorium = auditoriumRepository.findById(auditoriumId);

            if (movie.isPresent() && auditorium.isPresent()) {
                movie.get().setAuditorium(auditorium.get());
                auditorium.get().setMovie(movie.get());

                auditoriumRepository.save(auditorium.get());
                movieRepository.save(movie.get());
                return ResponseDTO.builder().success(true).build();
            }

            return ResponseDTO.builder()
                    .success(false)
                    .error("Invalid movie_id or auditorium_id")
                    .build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }

    }
    @PostMapping("/api/unlink")
    public ResponseDTO unlink(HttpServletRequest request) {
        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int auditoriumId = Integer.parseInt(request.getParameter("auditoriumId"));

            Optional<Movie> movie = movieRepository.findById(movieId);
            Optional<Auditorium> auditorium = auditoriumRepository.findById(auditoriumId);

            if (movie.isPresent() && auditorium.isPresent()) {
                movie.get().setAuditorium(null);
                auditorium.get().setMovie(null);

                auditoriumRepository.save(auditorium.get());
                movieRepository.save(movie.get());
                return ResponseDTO.builder().success(true).build();
            }

            return ResponseDTO.builder()
                    .success(false)
                    .error("Invalid movie_id or auditorium_id")
                    .build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }
}
