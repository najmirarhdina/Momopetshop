package com.management.momopetshop.service;

import com.management.momopetshop.DataAlreadyExistsException;
import com.management.momopetshop.DataImmutableException;
import com.management.momopetshop.DataNotFoundException;
import com.management.momopetshop.dto.ProdukRequest;
import com.management.momopetshop.model.Kategori;
import com.management.momopetshop.model.Produk;
import com.management.momopetshop.model.Supplier;
import com.management.momopetshop.repository.DetailTransaksiRepository;
import com.management.momopetshop.repository.KategoriRepository;
import com.management.momopetshop.repository.ProdukRepository;
import com.management.momopetshop.repository.StokMasukRepository;
import com.management.momopetshop.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdukService {

    private final ProdukRepository produkRepository;
    private final KategoriRepository kategoriRepository;
    private final SupplierRepository supplierRepository;
    private final StokMasukRepository stokMasukRepository;
    private final DetailTransaksiRepository detailTransaksiRepository;

    public ProdukService(
            ProdukRepository produkRepository,
            KategoriRepository kategoriRepository,
            SupplierRepository supplierRepository,
            StokMasukRepository stokMasukRepository,
            DetailTransaksiRepository detailTransaksiRepository
    ) {
        this.produkRepository = produkRepository;
        this.kategoriRepository = kategoriRepository;
        this.supplierRepository = supplierRepository;
        this.stokMasukRepository = stokMasukRepository;
        this.detailTransaksiRepository = detailTransaksiRepository;
    }

    // ================= GET ALL =================
    public List<Produk> findAll() {
        return produkRepository.findAll();
    }

    // ================= GET ALL (PAGINATION) =================
    public Page<Produk> findPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return produkRepository.findAll(pageable);
    }

    // ================= GET BY ID =================
    public Produk findById(Integer id) {
        return produkRepository.findById(id)
                .orElseThrow(() ->
                        new DataNotFoundException("Produk dengan ID " + id + " tidak ditemukan"));
    }

    // ================= CREATE =================
    public Produk save(ProdukRequest req) {

        Kategori kategori = kategoriRepository.findById(req.getIdKategori())
                .orElseThrow(() ->
                        new DataNotFoundException("Kategori tidak dapat ditemukan"));

        Supplier supplier = supplierRepository.findById(req.getIdSupplier())
                .orElseThrow(() ->
                        new DataNotFoundException("Supplier tidak dapat ditemukan"));

        boolean exists = produkRepository
                .existsByNamaProdukAndKategoriAndSupplier(
                        req.getNamaProduk(), kategori, supplier);

        if (exists) {
            throw new DataAlreadyExistsException(
                    "Produk dengan nama '" + req.getNamaProduk() +
                    "', kategori '" + kategori.getNamaKategori() +
                    "', dan supplier '" + supplier.getNamaSupplier() +
                    "' sudah terdaftar"
            );
        }

        Produk produk = new Produk();
        produk.setNamaProduk(req.getNamaProduk());
        produk.setHarga(req.getHarga());
        produk.setKategori(kategori);
        produk.setSupplier(supplier);
        produk.setStok(0);

        return produkRepository.save(produk);
    }

    // ================= UPDATE =================
    public Produk update(Integer id, ProdukRequest req) {

        Produk produk = findById(id);

        if (!produk.getKategori().getIdKategori().equals(req.getIdKategori())) {
            throw new DataImmutableException("ID kategori tidak boleh diubah");
        }

        if (!produk.getSupplier().getIdSupplier().equals(req.getIdSupplier())) {
            throw new DataImmutableException("ID supplier tidak boleh diubah");
        }

        boolean exists = produkRepository
                .existsByNamaProdukAndKategoriAndSupplierAndIdProdukNot(
                        req.getNamaProduk(),
                        produk.getKategori(),
                        produk.getSupplier(),
                        id
                );

        if (exists) {
            throw new DataAlreadyExistsException(
                    "Produk dengan nama '" + req.getNamaProduk() +
                    "', kategori '" + produk.getKategori().getNamaKategori() +
                    "', dan supplier '" + produk.getSupplier().getNamaSupplier() +
                    "' sudah terdaftar"
            );
        }

        produk.setNamaProduk(req.getNamaProduk());
        produk.setHarga(req.getHarga());

        return produkRepository.save(produk);
    }

    // ================= DELETE =================
    @Transactional
    public void delete(Integer id) {
        if (!produkRepository.existsById(id)) {
            throw new DataNotFoundException(
                    "Produk dengan ID " + id + " tidak ditemukan");
        }

        // 1. Hapus detail_transaksi dulu (FK ke id_produk)
        detailTransaksiRepository.deleteByIdProduk(id);

        // 2. Hapus stok_masuk (FK ke id_produk)
        stokMasukRepository.deleteByIdProduk(id);

        // 3. Baru hapus produk
        produkRepository.deleteById(id);
    }
}