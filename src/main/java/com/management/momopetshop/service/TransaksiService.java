package com.management.momopetshop.service;

import com.management.momopetshop.dto.*;
import com.management.momopetshop.InvalidRequestException;
import com.management.momopetshop.model.*;
import com.management.momopetshop.repository.ProdukRepository;
import com.management.momopetshop.repository.TransaksiRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransaksiService {

    private final TransaksiRepository transaksiRepository;
    private final ProdukRepository produkRepository;

    public TransaksiService(
            TransaksiRepository transaksiRepository,
            ProdukRepository produkRepository
    ) {
        this.transaksiRepository = transaksiRepository;
        this.produkRepository = produkRepository;
    }

    // =============================
    // CREATE TRANSAKSI
    // =============================
    @Transactional
    public TransaksiResponse simpanTransaksi(TransaksiRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidRequestException("Items tidak boleh kosong");
        }

        Transaksi transaksi = new Transaksi();
        transaksi.setIdUser(request.getIdUser());
        transaksi.setTanggal(LocalDateTime.now());
        transaksi.setTotal(BigDecimal.ZERO);

        List<DetailTransaksi> detailList = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (TransaksiItemDTO item : request.getItems()) {

            if (item.getQty() == null || item.getQty() <= 0) {
                throw new InvalidRequestException("Qty tidak valid");
            }

            Produk produk = produkRepository.findById(item.getIdProduk())
                    .orElseThrow(() ->
                            new RuntimeException("Produk tidak ditemukan: " + item.getIdProduk())
                    );

            // ✅ Harga selalu dari DB, tidak dari request
            if (produk.getStok() < item.getQty()) {
                throw new InvalidRequestException(
                    "Stok produk '" + produk.getNamaProduk() + "' tidak mencukupi" +
                    " (tersisa: " + produk.getStok() + ")"
                );
            }

            produk.setStok(produk.getStok() - item.getQty());
            produkRepository.save(produk);

            BigDecimal subtotal = produk.getHarga()
                    .multiply(BigDecimal.valueOf(item.getQty()));

            DetailTransaksi detail = new DetailTransaksi();
            detail.setTransaksi(transaksi);
            detail.setIdProduk(item.getIdProduk());
            detail.setQty(item.getQty());
            detail.setHarga(produk.getHarga());
            detail.setSubtotal(subtotal);

            detailList.add(detail);
            total = total.add(subtotal);
        }

        transaksi.setTotal(total);
        transaksi.setDetailTransaksi(detailList);

        Transaksi saved = transaksiRepository.save(transaksi);

        // ✅ Fetch ulang dengan JOIN FETCH agar detail ter-load
        Transaksi withDetail = transaksiRepository
                .findByIdWithDetail(saved.getIdTransaksi())
                .orElse(saved);

        return mapToResponse(withDetail);
    }

    // =============================
    // GET ALL
    // =============================
    @Transactional(readOnly = true)
    public List<TransaksiResponse> getAll() {

        List<TransaksiResponse> responses = new ArrayList<>();

        // ✅ Pakai findAllWithDetail() bukan findAll()
        for (Transaksi t : transaksiRepository.findAllWithDetail()) {
            responses.add(mapToResponse(t));
        }
        return responses;
    }

    // =============================
    // GET BY ID
    // =============================
    @Transactional(readOnly = true)
    public TransaksiResponse getById(Integer id) {

        // ✅ Pakai findByIdWithDetail() bukan findById()
        Transaksi transaksi = transaksiRepository.findByIdWithDetail(id)
                .orElseThrow(() ->
                        new RuntimeException("Transaksi tidak ditemukan: " + id)
                );

        return mapToResponse(transaksi);
    }

    // =================================================
    // PAGINATION
    // =================================================
    @Transactional(readOnly = true)
    public Page<TransaksiResponse> getPagination(int page, int size) {

        if (page < 0) {
            throw new InvalidRequestException("Page tidak boleh kurang dari 0");
        }
        if (size <= 0) {
            throw new InvalidRequestException("Size harus lebih dari 0");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("idTransaksi").ascending()
        );

        return transaksiRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    // =============================
    // MAPPING ENTITY → RESPONSE
    // =============================
    private TransaksiResponse mapToResponse(Transaksi transaksi) {

        TransaksiResponse res = new TransaksiResponse();
        res.setIdTransaksi(transaksi.getIdTransaksi());
        res.setIdUser(transaksi.getIdUser());
        res.setTanggal(transaksi.getTanggal());
        res.setTotal(transaksi.getTotal());

        List<DetailTransaksiResponse> items = new ArrayList<>();

        if (transaksi.getDetailTransaksi() != null) {
            for (DetailTransaksi d : transaksi.getDetailTransaksi()) {

                DetailTransaksiResponse dr = new DetailTransaksiResponse();
                dr.setIdProduk(d.getIdProduk());
                dr.setQty(d.getQty());
                dr.setHarga(d.getHarga());
                dr.setSubtotal(d.getSubtotal());

                items.add(dr);
            }
        }

        res.setItems(items);
        return res;
    }
}