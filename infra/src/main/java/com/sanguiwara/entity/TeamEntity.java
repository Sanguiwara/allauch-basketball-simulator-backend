package com.sanguiwara.entity;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "teams")
public class TeamEntity {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_category", nullable = false)
    private AgeCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<PlayerEntity> players = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private ClubEntity club;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamEntity other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
