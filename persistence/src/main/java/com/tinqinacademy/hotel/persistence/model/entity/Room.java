package com.tinqinacademy.hotel.persistence.model.entity;

import com.tinqinacademy.hotel.persistence.model.enums.BathroomType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "room_no", nullable = false, unique = true, length = 10)
    private String roomNo;

    @Column(name = "floor", nullable = false)
    private Integer floor;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "bathroom_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BathroomType bathroomType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "rooms_beds",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "bed_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Bed> beds;
}
