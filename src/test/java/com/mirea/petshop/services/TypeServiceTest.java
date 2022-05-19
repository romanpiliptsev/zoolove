package com.mirea.petshop.services;

import com.mirea.petshop.models.Type;
import com.mirea.petshop.repositories.ITypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TypeServiceTest {
    @InjectMocks
    private TypeService typeService;
    @Mock
    private ITypeRepository iTypeRepository;
    @Captor
    private ArgumentCaptor<Type> captor;
    private Type type1, type2, type3;

    @BeforeEach
    void setUp() {
        type1 = new Type();
        type1.setId(1);
        type1.setName("type1");

        type2 = new Type();
        type2.setId(2);
        type2.setName("type2");

        type3 = new Type();
        type3.setId(3);
        type3.setName("type3");
    }

    @Test
    void getAllTypes() {
        Mockito.when(iTypeRepository.findAll()).thenReturn(List.of(type1, type2, type3));
        assertEquals(List.of(type1, type2, type3), iTypeRepository.findAll());
    }
}
