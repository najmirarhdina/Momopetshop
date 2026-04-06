package com.management.momopetshop.repository;

import com.management.momopetshop.model.StokMasuk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StokMasukRepository extends JpaRepository<StokMasuk, Integer> {
    boolean existsByIdProdukAndIdSupplier(Integer idProduk, Integer idSupplier);
    void deleteByIdProduk(Integer idProduk);
}