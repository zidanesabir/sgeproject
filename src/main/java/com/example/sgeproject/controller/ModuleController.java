package com.example.sgeproject.controller;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Module;
import com.example.sgeproject.service.FormationService;
import com.example.sgeproject.service.ModuleService;
import com.example.sgeproject.service.ProfesseurService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/gestionModules") // Base mapping for admin module operations
public class ModuleController {

    private final ModuleService moduleService;
    private final FormationService formationService;
    private final ProfesseurService professeurService;

    public ModuleController(ModuleService moduleService, FormationService formationService, ProfesseurService professeurService) {
        this.moduleService = moduleService;
        this.formationService = formationService;
        this.professeurService = professeurService;
    }

    @GetMapping // Handles GET /admin/gestionModules
    public String listModules(Model model) {
        List<Module> modules = moduleService.getAllModules();
        model.addAttribute("modules", modules);
        return "gestionModules"; // Renders src/main/resources/templates/gestionModules.html
    }

    @GetMapping("/new") // Handles GET /admin/gestionModules/new
    public String showCreateModuleForm(Model model) {
        model.addAttribute("module", new Module());
        model.addAttribute("formations", formationService.getAllFormations()); // For dropdown
        model.addAttribute("professeurs", professeurService.getAllProfesseurs()); // For dropdown
        return "createModuleForm"; // Create this Thymeleaf template
    }

    @PostMapping("/save") // Handles POST /admin/gestionModules/save
    public String saveModule(@ModelAttribute Module module, RedirectAttributes redirectAttributes) {
        try {
            moduleService.saveModule(module);
            redirectAttributes.addFlashAttribute("successMessage", "Module added successfully!");
            return "redirect:/admin/gestionModules";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding module: " + e.getMessage());
            return "redirect:/admin/gestionModules/new";
        }
    }

    @GetMapping("/edit/{id}") // Handles GET /admin/gestionModules/edit/{id}
    public String showEditModuleForm(@PathVariable Long id, Model model) {
        Module module = moduleService.getModuleById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + id));
        model.addAttribute("module", module);
        model.addAttribute("formations", formationService.getAllFormations()); // For dropdown
        model.addAttribute("professeurs", professeurService.getAllProfesseurs()); // For dropdown
        return "editModuleForm"; // Create this Thymeleaf template
    }

    @PostMapping("/update/{id}") // Handles POST /admin/gestionModules/update/{id}
    public String updateModule(@PathVariable Long id, @ModelAttribute Module module, RedirectAttributes redirectAttributes) {
        try {
            moduleService.updateModule(id, module);
            redirectAttributes.addFlashAttribute("successMessage", "Module updated successfully!");
            return "redirect:/admin/gestionModules";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/gestionModules";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating module: " + e.getMessage());
            return "redirect:/admin/gestionModules/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}") // Handles POST /admin/gestionModules/delete/{id}
    public String deleteModule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            moduleService.deleteModule(id);
            redirectAttributes.addFlashAttribute("successMessage", "Module deleted successfully!");
            return "redirect:/admin/gestionModules";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting module: " + e.getMessage());
            return "redirect:/admin/gestionModules";
        }
    }
}