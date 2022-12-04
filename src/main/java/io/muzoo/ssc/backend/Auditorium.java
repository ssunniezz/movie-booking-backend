package io.muzoo.ssc.backend;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonInclude
@Table(name = "tbl_auditorium")
public class Auditorium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String name;
    private int startSeatId;
    private int endSeatId;

    @OneToOne
    @JoinColumn(name = "movie_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    private Movie movie;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "auditorium")
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    private List<Screening> screenings;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "auditorium")
    @JsonIgnore
    private List<Seat> seats;

    public Auditorium(String name) {
        this.name = name;
    }

}
