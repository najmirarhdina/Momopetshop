package com.management.momopetshop.repository;

import com.management.momopetshop.model.DetailTransaksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailTransaksiRepository extends JpaRepository<DetailTransaksi, Integer> {
    void deleteByIdProduk(Integer idProduk);
}