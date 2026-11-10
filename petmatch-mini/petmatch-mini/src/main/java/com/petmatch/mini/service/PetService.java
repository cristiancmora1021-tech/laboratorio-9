package com.petmatch.mini.service;

import com.petmatch.mini.model.Pet;
import com.petmatch.mini.dto.PetForm;
import com.petmatch.mini.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    public Optional<Pet> findById(long id) {
        return petRepository.findById(id);
    }

    public boolean delete(long id) {
        if (petRepository.existsById(id)) {
            petRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Pet> update(long id, PetForm petForm) {
        return petRepository.findById(id).map(pet -> {
            pet.setName(petForm.getName());
            pet.setSpecies(petForm.getSpecies());
            pet.setAge(petForm.getAge());
            pet.setDescription(petForm.getDescription());
            return petRepository.save(pet);
        });
    }

    // Se incluyen ambos métodos de guardado por si el controlador usa alguno de los dos
    public Pet create(PetForm petForm) {
        Pet pet = new Pet();
        pet.setName(petForm.getName());
        pet.setSpecies(petForm.getSpecies());
        pet.setAge(petForm.getAge());
        pet.setDescription(petForm.getDescription());
        return petRepository.save(pet);
    }
    
    public Pet save(Pet pet) {
        return petRepository.save(pet);
    }
}