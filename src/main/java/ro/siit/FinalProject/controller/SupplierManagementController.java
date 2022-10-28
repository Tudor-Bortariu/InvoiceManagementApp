package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ro.siit.FinalProject.exception.ObjectNotFoundException;
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
                                    @RequestParam String phoneNumber,
                                    @RequestParam String county) {

        Supplier addedSupplier = new Supplier(UUID.randomUUID(), supplierName, phoneNumber, county);

        Authentication authentication = authenticationFacade.getAuthentication();

        addedSupplier.setUser(((CustomUserDetails)authentication.getPrincipal()).getUser());
        supplierRepository.saveAndFlush(addedSupplier);

        return new RedirectView("/supplierManagement");
    }

    @GetMapping("/delete/{id}")
    public RedirectView deleteSupplier(Model model, @PathVariable UUID id) {
        supplierRepository.deleteBySupplierId(id);
        return new RedirectView("/supplierManagement");
    }

    @GetMapping("/edit/{id}")
    public String editSupplierForm(Model model, @PathVariable UUID id) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        model.addAttribute("supplier", supplier.orElseThrow(ObjectNotFoundException::new));

        return "SupplierManagement/editForm";
    }

    @PostMapping("/edit")
    public RedirectView editInvoice(Model model,
                                    @RequestParam UUID supplierId,
                                    @RequestParam String updatedSupplierName,
                                    @RequestParam String updatedPhoneNumber,
                                    @RequestParam String updatedCounty) {

        Optional<Supplier> supplier = supplierRepository.findById(supplierId);

        supplier.orElseThrow(ObjectNotFoundException::new).setSupplierName(updatedSupplierName);
        supplier.get().setPhoneNumber(updatedPhoneNumber);
        supplier.get().setCounty(updatedCounty);

        supplierRepository.saveAndFlush(supplier.get());

        return new RedirectView("/supplierManagement");
    }
}
