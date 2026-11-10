package com.petmatch.mini.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.petmatch.mini.dto.PetForm;
import com.petmatch.mini.model.Pet;
import com.petmatch.mini.service.PetService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pets", petService.findAll());
        return "pets/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("petForm", new PetForm());
        model.addAttribute("editing", false);
        return "pets/form";
    }

    @PostMapping
    public String create(
        @Valid @ModelAttribute("petForm") PetForm petForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", false);
            return "pets/form";
        }

        Pet pet = petService.create(petForm);
        redirectAttributes.addFlashAttribute(
            "successMessage", "Mascota registrada correctamente."
        );
        return "redirect:/pets/" + pet.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") long id, Model model) {
        model.addAttribute("pet", findPet(id));
        return "pets/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") long id, Model model) {
        Pet pet = findPet(id);

        PetForm form = new PetForm();
        form.setName(pet.getName());
        form.setSpecies(pet.getSpecies());
        form.setAge(pet.getAge());
        form.setDescription(pet.getDescription());

        model.addAttribute("petForm", form);
        model.addAttribute("editing", true);
        model.addAttribute("petId", id);
        return "pets/form";
    }

    @PostMapping("/{id}")
    public String update(
        @PathVariable("id") long id,
        @Valid @ModelAttribute("petForm") PetForm petForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        findPet(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", true);
            model.addAttribute("petId", id);
            return "pets/form";
        }

        Pet updated = petService.update(id, petForm)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Pet not found"
            ));

        redirectAttributes.addFlashAttribute(
            "successMessage", "Mascota actualizada correctamente."
        );
        return "redirect:/pets/" + updated.getId();
    }

    @PostMapping("/{id}/delete")
    public String delete(
        @PathVariable("id") long id,
        RedirectAttributes redirectAttributes
    ) {
        if (!petService.delete(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Pet not found"
            );
        }

        redirectAttributes.addFlashAttribute(
            "successMessage", "Mascota eliminada correctamente."
        );
        return "redirect:/pets";
    }

    private Pet findPet(long id) {
        return petService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Pet not found"
            ));
    }
}