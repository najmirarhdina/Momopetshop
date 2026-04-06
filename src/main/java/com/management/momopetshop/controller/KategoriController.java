package com.management.momopetshop.controller;

import com.management.momopetshop.model.Kategori;
import com.management.momopetshop.service.KategoriService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kategori")
@CrossOrigin(origins = "*")
public class KategoriController {

    private final KategoriService kategoriService;

    public KategoriController(KategoriService kategoriService) {
        this.kategoriService = kategoriService;
    }

    // ================= CREATE =================
    @PostMapping
    public Kategori create(@RequestBody Kategori kategori) {
        return kategoriService.save(kategori);
    }

    // ================= GET ALL =================
    @GetMapping
    public List<Kategori> getAll() {
        return kategoriService.findAll();
    }

    // ================= GET ALL (PAGINATION) =================
    @GetMapping("/pagination")
    public Page<Kategori> getPagination(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return kategoriService.findPagination(page, size);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public Kategori getById(@PathVariable Integer id) {
        return kategoriService.findById(id);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public Kategori update(
            @PathVariable Integer id,
            @RequestBody Kategori kategori) {
        return kategoriService.update(id, kategori);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        kategoriService.delete(id);
        return ResponseEntity.ok("Kategori berhasil dihapus");
    }
}
