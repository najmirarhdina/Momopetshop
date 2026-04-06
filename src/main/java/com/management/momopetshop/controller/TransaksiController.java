package com.management.momopetshop.controller;

import com.management.momopetshop.dto.TransaksiRequest;
import com.management.momopetshop.dto.TransaksiResponse;
import com.management.momopetshop.service.TransaksiService;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/api/transaksi")
@CrossOrigin(origins = "*")
public class TransaksiController {

    private final TransaksiService transaksiService;

    public TransaksiController(TransaksiService transaksiService) {
        this.transaksiService = transaksiService;
    }

    // ================= CREATE =================
    @PostMapping
    public TransaksiResponse simpan(@RequestBody TransaksiRequest request) {
        return transaksiService.simpanTransaksi(request);
    }

    // ================= GET ALL =================
    @GetMapping
    public List<TransaksiResponse> getAll() {
        return transaksiService.getAll();
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public TransaksiResponse getById(@PathVariable Integer id) {
        return transaksiService.getById(id);
    }

    // =================================================
    // ✅ PAGINATION (PAGE + SIZE SAJA)
    // =================================================
    @GetMapping("/pagination")
    public Page<TransaksiResponse> getPagination(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return transaksiService.getPagination(page, size);
    }
}
