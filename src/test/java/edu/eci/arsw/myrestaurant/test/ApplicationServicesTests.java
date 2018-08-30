package edu.eci.arsw.myrestaurant.test;

import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServices;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ApplicationServicesTests {

    @Autowired
    RestaurantOrderServices ros;

    /*
     * Clases de equivalencia, calculateTableBill.
     * CF1: Todos los productos son de tipo DRINK por lo que se les aplica un iva del 16%
     * CF2: Los producto son de tipo diferente a DRINK por lo que se les aplica un iva del 19%
     * CE1: Combinación de productos, se aplicaran ivas del 16% y 19% dependiendo del producto.
     */
    
    @Test
    public void claseFronteraUno() {
        try {
            int actual = ros.calculateTableBill(5);
            int esperadoSinIva = 1300 * 4 + 2500 * 2;
            int iva = (int) ((1300 * 4 + 2500 * 2) * 0.16);
            org.junit.Assert.assertEquals(actual, esperadoSinIva + iva);
        } catch (OrderServicesException ex) {
            Logger.getLogger(ApplicationServicesTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void claseFronteraDos() {
        try {
            int actual = ros.calculateTableBill(4);
            int esperadoSinIva = 10000 * 3 + 12300 * 2 + 3000;
            int iva = (int) ((10000 * 3 + 12300 * 2 + 3000) * 0.19);
            org.junit.Assert.assertEquals(actual, esperadoSinIva + iva);
        } catch (OrderServicesException ex) {
            Logger.getLogger(ApplicationServicesTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void claseEquivalenciaUno() {
        try {
            int actual = ros.calculateTableBill(3);
            int esperadoSinIva = 12300 * 2 + 1300 * 2;
            int iva = (int) ((12300 * 2 * 0.19) + (1300 * 2 * 0.16));
            org.junit.Assert.assertEquals(actual, esperadoSinIva + iva);
        } catch (OrderServicesException ex) {
            Logger.getLogger(ApplicationServicesTests.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
