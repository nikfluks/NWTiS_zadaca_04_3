package org.foi.nwtis.nikfluks.web.slusaci;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.nikfluks.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa je web slušač i nakon što se pokrene aplikacija ona sprema servlet kontekst. Također dohvaća putanju konfiguracijske
 * datoteke i sprema ju u atribut servlet konteksta.
 *
 * @author Nikola
 * @version 1
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    private static ServletContext servletContext;

    /**
     * Pokreće se kod pokretanja aplikacija.
     *
     * @param sce ServletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();

        String datoteka = servletContext.getInitParameter("konfiguracija");
        String putanja = servletContext.getRealPath("/WEB-INF") + java.io.File.separator;
        BP_Konfiguracija bpk = new BP_Konfiguracija(putanja + datoteka);

        servletContext.setAttribute("BP_Konfig", bpk);
    }

    /**
     * Pokreće se kod brisanja (undeploy) aplikacije.
     *
     * @param sce ServletContextEvent
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        servletContext.removeAttribute("BP_Konfig");
    }

    /**
     * Getter za servletContext.
     *
     * @return servletContext
     */
    public static ServletContext getServletContext() {
        return servletContext;
    }
}
