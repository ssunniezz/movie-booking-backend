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
@Table(name = "tbl_screening",
    uniqueConstraints = @UniqueConstraint(columnNames = {"start", "auditorium_id"}))
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int start;

    @ManyToOne
    @JoinColumn(name = "auditorium_id")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JsonIgnore
    private Auditorium auditorium;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "screening")
    @JsonIgnore
    private List<SeatReserved> seatReservedList;

    public Screening(int start, Auditorium auditorium) {
        this.start = start;
        this.auditorium = auditorium;
    }
}
