package com.mirea.petshop.services;

import com.mirea.petshop.models.Product;
import com.mirea.petshop.repositories.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private IProductRepository iProductRepository;
    @Captor
    private ArgumentCaptor<Product> captor;
    private Product product1, product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1);
        product1.setTypeId(1);
        product1.setName("product1");
        product1.setPrice(1);
        product1.setDescription("description");

        product2 = new Product();
        product2.setId(2);
        product2.setTypeId(1);
        product2.setName("product2");
        product2.setPrice(2);
        product2.setDescription("description");
    }

    @Test
    void getAllProductsByTypeId() {
        Mockito.when(iProductRepository.findAllByTypeId(1)).thenReturn(List.of(product1, product2));
        assertEquals(2, iProductRepository.findAllByTypeId(1).size());
    }

    @Test
    void getProductById() {
        Mockito.when(iProductRepository.findById(2)).thenReturn(product2);
        assertEquals(product2, iProductRepository.findById(2));
    }

    @Test
    void getAll() {
        Mockito.when(iProductRepository.findAll()).thenReturn(List.of(product1, product2));
        assertEquals(List.of(product1, product2), iProductRepository.findAll());
    }
}
