package com.management.momopetshop.controller;

import com.management.momopetshop.dto.ProdukRequest;
import com.management.momopetshop.model.Produk;
import com.management.momopetshop.service.ProdukService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produk")
@CrossOrigin(origins = "*")
public class ProdukController {

    private final ProdukService produkService;

    public ProdukController(ProdukService produkService) {
        this.produkService = produkService;
    }

    // ================= GET ALL =================
    @GetMapping
    public List<Produk> getAll() {
        return produkService.findAll();
    }

    // ================= GET ALL (PAGINATION) =================
    @GetMapping("/pagination")
    public Page<Produk> getPagination(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return produkService.findPagination(page, size);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public Produk getById(@PathVariable Integer id) {
        return produkService.findById(id);
    }

    // ================= CREATE =================
    @PostMapping
    public Produk create(@RequestBody ProdukRequest req) {
        return produkService.save(req);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public Produk update(
            @PathVariable Integer id,
            @RequestBody ProdukRequest req
    ) {
        return produkService.update(id, req);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        produkService.delete(id);
        return "Produk berhasil dihapus";
    }
}
