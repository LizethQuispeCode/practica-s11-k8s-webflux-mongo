package com.practica.s11;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaRepository repo;

    public TareaController(TareaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Flux<Tarea> listar() {
        return repo.findAll();
    }

    @PostMapping
    public Mono<Tarea> crear(@RequestBody Tarea tarea) {
        tarea.setId(null);
        return repo.save(tarea);
    }
}