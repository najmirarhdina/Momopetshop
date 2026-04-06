package com.management.momopetshop.controller;

import com.management.momopetshop.model.Supplier;
import com.management.momopetshop.service.SupplierService;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
@CrossOrigin(origins = "*")
public class SupplierController {

    private final SupplierService service;

    public SupplierController(SupplierService service) {
        this.service = service;
    }

    // ================= GET ALL =================
    @GetMapping
    public List<Supplier> getAll() {
        return service.getAll();
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public Supplier getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    // ================= CREATE =================
    @PostMapping
    public Supplier create(@RequestBody Supplier supplier) {
        return service.save(supplier);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public Supplier update(@PathVariable Integer id,
                           @RequestBody Supplier supplier) {
        return service.update(id, supplier);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "Supplier berhasil dihapus";
    }

    // =================================================
    // ✅ PAGINATION (PAGE + SIZE SAJA)
    // =================================================
    @GetMapping("/pagination")
    public Page<Supplier> getPagination(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return service.getPagination(page, size);
    }
}
