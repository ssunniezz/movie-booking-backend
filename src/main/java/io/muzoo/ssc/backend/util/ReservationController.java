package io.muzoo.ssc.backend.util;

import io.muzoo.ssc.backend.Screening;
import io.muzoo.ssc.backend.Seat;
import io.muzoo.ssc.backend.SeatReserved;
import io.muzoo.ssc.backend.User;
import io.muzoo.ssc.backend.repository.ScreeningRepository;
import io.muzoo.ssc.backend.repository.SeatRepository;
import io.muzoo.ssc.backend.repository.SeatReservedRepository;
import io.muzoo.ssc.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class ReservationController {

    @Autowired
    SeatRepository seatRepository;
    @Autowired
    SeatReservedRepository seatReservedRepository;

    @Autowired
    ScreeningRepository screeningRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/api/reserveSeat")
    public ResponseDTO reserveSeat(HttpServletRequest request) {

        try {
            long seatId = Long.parseLong(request.getParameter("seatId"));
            String username = request.getParameter("username");
            long screeningId = Long.parseLong(request.getParameter("screeningId"));

            Seat seat = seatRepository.findSeatById(seatId);
            User user = userRepository.findByUsername(username);
            Screening screening = screeningRepository.findScreeningById(screeningId);

            if (user == null || seat == null || screening == null) {
                return ResponseDTO.builder()
                        .success(false)
                        .error("invalid username or seat or screening")
                        .build();
            }

            if (seat.getAuditorium().getId() != screening.getAuditorium().getId()) {
                return ResponseDTO.builder()
                        .success(false)
                        .error("seat does not belong to this auditorium")
                        .build();
            }

            seatReservedRepository.save(new SeatReserved(seat, user, screening));
            return ResponseDTO.builder().success(true).build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/api/cancelReservedSeat")
    public ResponseDTO cancelReservation(HttpServletRequest request) {
        try {
            long screeningId = Long.parseLong(request.getParameter("screeningId"));
            int seatId = Integer.parseInt(request.getParameter("seatId"));

            Screening screening = screeningRepository.findScreeningById(screeningId);
            if (screening == null) {
                return ResponseDTO.builder()
                        .success(false)
                        .error("invalid screening")
                        .build();
            }

            List<SeatReserved> seatReservedList = screening.getSeatReservedList();
            for (SeatReserved seatReserved: seatReservedList) {
                if (seatReserved.getSeat().getId() == seatId) {
                    seatReservedList.remove(seatReserved);
                    seatReservedRepository.deleteById(seatReserved.getId());
                    break;
                }
            }

            return ResponseDTO.builder().success(true).build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/api/clearReservedSeats")
    public ResponseDTO clearAllReservationsByScreening(HttpServletRequest request) {
        try {
            long screeningId = Long.parseLong(request.getParameter("screeningId"));

            Screening screening = screeningRepository.findScreeningById(screeningId);
            if (screening == null) {
                return ResponseDTO.builder()
                        .success(false)
                        .error("invalid screening")
                        .build();
            }

            List<SeatReserved> seatReservedList = screening.getSeatReservedList();
            int length = seatReservedList.size();
            for (int i=0; i<length; i++) {
                SeatReserved seatReserved = seatReservedList.remove(0);
                seatReservedRepository.deleteById(seatReserved.getId());
            }

            return ResponseDTO.builder().success(true).build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/api/getReservedSeats")
    public List<SeatReserved> getSeatReservedByScreening(HttpServletRequest request) {
        try {
            long screeningId = Long.parseLong(request.getParameter("screeningId"));

            Screening screening = screeningRepository.findScreeningById(screeningId);
            if (screening == null) {
                    return new ArrayList<>();
                }
            screening.getSeatReservedList().sort(new Comparator<SeatReserved>() {
                @Override
                public int compare(SeatReserved o1, SeatReserved o2) {
                    return o1.getSeat().getId().compareTo(o2.getSeat().getId());
                }
            });

            List<SeatReserved> list = List.copyOf(screening.getSeatReservedList());
            for (SeatReserved seat: list) {
                seat.setScreening(null);
                seat.setUser(null);
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
