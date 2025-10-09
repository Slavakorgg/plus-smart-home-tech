package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "conditions")
public class Condition {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ToString.Include
    @Column(name = "sensor_id")
    private String sensorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ConditionType type;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "operation")
    private ConditionOperation operation;

    @ToString.Include
    @Column(name = "value")
    private Integer value;

}