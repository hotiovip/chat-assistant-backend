package de.hotiovip.chatAppBackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "threads_information")
@Getter
@Setter
public class ThreadsInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ElementCollection
    @CollectionTable(name = "user_threads", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "thread_id")
    private List<String> threadIds;
}
