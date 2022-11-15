package ro.siit.FinalProject.InvoiceManagementProject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ro.siit.FinalProject.controller.HomeController;
import ro.siit.FinalProject.controller.InvoiceManagementController;
import ro.siit.FinalProject.controller.SecurityController;
import ro.siit.FinalProject.controller.SupplierManagementController;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class InvoiceManagementProjectApplicationTests {

	@Autowired
	InvoiceManagementController invoiceManagementController;

	@Autowired
	SupplierManagementController supplierManagementController;

	@Autowired
	HomeController homeController;

	@Autowired
	SecurityController securityController;

	@Test
	void contextLoads() {
	}

	@Test
	public void invoiceControllerLoads(){
		Assertions.assertNotNull(invoiceManagementController);
	}

	@Test
	public void supplierControllerLoads(){
		Assertions.assertNotNull(supplierManagementController);
	}

	@Test
	public void securityControllerLoads(){
		Assertions.assertNotNull(securityController);
	}

	@Test
	public void homeControllerLoads(){
		Assertions.assertNotNull(homeController);
	}

}
