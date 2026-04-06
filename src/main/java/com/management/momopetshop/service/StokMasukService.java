package com.management.momopetshop.service;

import com.management.momopetshop.DataImmutableException;
import com.management.momopetshop.DataNotFoundException;
import com.management.momopetshop.model.Produk;
import com.management.momopetshop.model.StokMasuk;
import com.management.momopetshop.repository.ProdukRepository;
import com.management.momopetshop.repository.StokMasukRepository;
import com.management.momopetshop.repository.SupplierRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service
public class StokMasukService {

    private final StokMasukRepository repository;
    private final ProdukRepository produkRepository;
    private final SupplierRepository supplierRepository;

    public StokMasukService(
            StokMasukRepository repository,
            ProdukRepository produkRepository,
            SupplierRepository supplierRepository
    ) {
        this.repository = repository;
        this.produkRepository = produkRepository;
        this.supplierRepository = supplierRepository;
    }

    /* ================= CREATE ================= */
    @Transactional
    public StokMasuk save(StokMasuk stokMasuk) {

        Produk produk = produkRepository.findById(stokMasuk.getIdProduk())
                .orElseThrow(() ->
                        new DataNotFoundException("ID Produk tidak tersedia"));

        if (!supplierRepository.existsById(stokMasuk.getIdSupplier())) {
            throw new DataNotFoundException("ID Supplier tidak tersedia");
        }

        // ✅ Langsung save — setiap isi stok jadi record baru
        StokMasuk saved = repository.save(stokMasuk);

        produk.setStok(produk.getStok() + stokMasuk.getJumlah());
        produkRepository.save(produk);

        return saved;
    }

    /* ================= FIND ALL ================= */
    public List<StokMasuk> findAll() {
        return repository.findAll();
    }

    /* ================= FIND ALL (PAGINATION) ================= */
    public Page<StokMasuk> findPagination(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    /* ================= FIND BY ID ================= */
    public StokMasuk findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new DataNotFoundException("Data stok masuk tidak ditemukan"));
    }

    /* ================= UPDATE (AKUMULASI) ================= */
    @Transactional
    public StokMasuk update(Integer id, StokMasuk request) {

        StokMasuk existing = repository.findById(id)
                .orElseThrow(() ->
                        new DataNotFoundException("Data stok masuk tidak ditemukan"));

        if (!existing.getIdProduk().equals(request.getIdProduk()) ||
            !existing.getIdSupplier().equals(request.getIdSupplier())) {
            throw new DataImmutableException(
                    "ID Produk dan ID Supplier tidak boleh diubah");
        }

        if (request.getJumlah() <= 0) {
            throw new IllegalArgumentException("Jumlah tambahan harus lebih dari 0");
        }

        Produk produk = produkRepository.findById(existing.getIdProduk())
                .orElseThrow(() ->
                        new DataNotFoundException("Produk tidak ditemukan"));

        Integer tambahan = request.getJumlah();
        existing.setJumlah(existing.getJumlah() + tambahan);
        produk.setStok(produk.getStok() + tambahan);
        existing.setTanggal(request.getTanggal());

        produkRepository.save(produk);
        return repository.save(existing);
    }

    /* ================= DELETE ================= */
    @Transactional
    public void delete(Integer id) {

        StokMasuk stokMasuk = repository.findById(id)
                .orElseThrow(() ->
                        new DataNotFoundException("Data stok masuk tidak ditemukan"));

        Produk produk = produkRepository.findById(stokMasuk.getIdProduk())
                .orElseThrow(() ->
                        new DataNotFoundException("Produk tidak ditemukan"));

        produk.setStok(produk.getStok() - stokMasuk.getJumlah());
        produkRepository.save(produk);

        repository.deleteById(id);
    }
}