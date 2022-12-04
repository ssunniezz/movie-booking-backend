package io.muzoo.ssc.backend;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonInclude
@Table(name = "tbl_seat",
    uniqueConstraints = @UniqueConstraint(columnNames = {"row", "number", "auditorium_id"}))
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private char row;

    private int number;

    @ManyToOne
    @JoinColumn(name = "auditorium_id")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JsonIgnore
    private Auditorium auditorium;

    public Seat(char row, int number, Auditorium auditorium) {
        this.row = row;
        this.number = number;
        this.auditorium = auditorium;
    }
}
