package dev.m1stwng.taskflow.common.persistence;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@MappedSuperclass
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;
}
