package com.mirea.petshop.repositories;

import com.mirea.petshop.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllByTypeId(int typeId);
    Product findById(int id);
}
