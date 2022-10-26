package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ro.siit.FinalProject.model.CustomUserDetails;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.JpaSupplierRepository;
import ro.siit.FinalProject.service.IAuthenticationFacade;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("supplierManagement")
public class SupplierManagementController {

    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @Autowired
    public JpaSupplierRepository supplierRepository;

    @GetMapping("")
    public String supplierManagement(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("suppliers", supplierRepository.findAllSuppliersByUser(authenticatedUser));

        return "SupplierManagement/supplierManagement";
    }

    @GetMapping("/addSupplier")
    public String addSupplierForm(Model model) {
        return "SupplierManagement/addSupplier";
    }

    @PostMapping("supplierManagement/addSupplier")
    public RedirectView addSupplier(Model model,
                                    @RequestParam String supplierName,
                                    @RequestParam String phoneNumber) {

        Supplier addedSupplier = new Supplier(UUID.randomUUID(), supplierName, phoneNumber);

        Authentication authentication = authenticationFacade.getAuthentication();

        addedSupplier.setUser(((CustomUserDetails)authentication.getPrincipal()).getUser());
        supplierRepository.saveAndFlush(addedSupplier);

        return new RedirectView("/invoiceManagement");
    }

    @GetMapping("/delete/{id}")
    public RedirectView deleteSupplier(Model model, @PathVariable UUID id) {
        supplierRepository.deleteBySupplierId(id);
        return new RedirectView("/supplierManagement");
    }

    @GetMapping("/edit/{id}")
    public String editSupplierForm(Model model, @PathVariable UUID id) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        model.addAttribute("supplier", supplier.get());

        return "SupplierManagement/editForm";
    }

    @PostMapping("/edit")
    public RedirectView editInvoice(Model model,
                                    @RequestParam UUID supplierId,
                                    @RequestParam String updatedSupplierName,
                                    @RequestParam String updatedPhoneNumber) {

        Optional<Supplier> supplier = supplierRepository.findById(supplierId);

        supplier.get().setSupplierName(updatedSupplierName);
        supplier.get().setPhoneNumber(updatedPhoneNumber);

        supplierRepository.save(supplier.get());

        return new RedirectView("/supplierManagement");
    }
}
