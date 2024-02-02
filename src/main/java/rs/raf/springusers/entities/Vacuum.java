package rs.raf.springusers.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@Entity
@Table(name = "vacuum")
public class Vacuum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Status status;
    private int active;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
    private AtomicInteger dischargeCounter = new AtomicInteger(0);
}
