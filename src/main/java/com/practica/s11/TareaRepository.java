package com.practica.s11;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TareaRepository extends ReactiveMongoRepository<Tarea, String> {
}