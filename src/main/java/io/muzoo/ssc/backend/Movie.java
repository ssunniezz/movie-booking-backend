package io.muzoo.ssc.backend;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonInclude
@Table(name = "tbl_movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String title;
    private String director;
    private String description;
    private int duration;
    private String picUrl;

    @OneToOne
    @JoinColumn(name = "auditorium_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    private Auditorium auditorium;

    public Movie(String title, String director, String desc, int duration, String picUrl) {
        this.title = title;
        this.director = director;
        this.description = desc;
        this.duration = duration;
        this.picUrl = picUrl;
    }
}
