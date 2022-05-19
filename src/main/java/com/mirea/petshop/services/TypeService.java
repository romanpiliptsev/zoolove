package com.mirea.petshop.services;

import com.mirea.petshop.models.Type;
import com.mirea.petshop.repositories.ITypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeService {
    private ITypeRepository iTypeRepository;

    @Autowired
    public TypeService(ITypeRepository iTypeRepository) {
        this.iTypeRepository = iTypeRepository;
    }

    public List<Type> getAllTypes() {
        return iTypeRepository.findAll();
    }
}
