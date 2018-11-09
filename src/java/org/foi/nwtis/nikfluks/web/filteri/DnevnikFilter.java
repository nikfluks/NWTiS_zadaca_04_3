package org.foi.nwtis.nikfluks.web.filteri;

import java.io.IOException;
import java.util.Date;
import javax.ejb.EJB;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.foi.nwtis.nikfluks.ejb.eb.Dnevnik;
import org.foi.nwtis.nikfluks.ejb.sb.DnevnikFacade;

/**
 * Klasa je filter kroz kojeg prolaze svi zahtjevi korisnika na bazi urlPatterns=/*. Svaki takav zahtjev se zapisuje u tablicu
 * Dnevnik.
 *
 * @author Nikola
 * @version 1
 */
@WebFilter(filterName = "DnevnikFilter", urlPatterns = {"*.xhtml"}, dispatcherTypes = {DispatcherType.REQUEST})
public class DnevnikFilter implements Filter {

    @EJB
    DnevnikFacade dnevnikFacade;

    private FilterConfig filterConfig = null;

    /**
     * Inicijalizira vrijednost objekta filterConfig.
     *
     * @param filterConfig filterConfig vrijednost koja se sprema u lokalnu varijablu
     * @throws ServletException iznimka ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    /**
     * Sama metoda kroz koju prolazi svaki zahtjev korisnika.
     *
     * @param request podaci o zahtjevu korisnika
     * @param response podaci o odgovoru korisniku
     * @param chain lanac u nizu filtera
     * @throws IOException iznimmka IOException
     * @throws ServletException iznimka ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (filterConfig == null) {
            return;
        }
        long pocetak = System.currentTimeMillis();
        chain.doFilter(request, response);
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;

        zapisiUDnevnik(request, response, trajanje);
    }

    /**
     * Vraća vrijednost objekta filterConfig na null.
     */
    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * Zapisuje u dnevnik podatke koji su prošli kroz filter.
     *
     * @param request podaci o zahtjevu korisnika
     * @param response podaci o odgovoru korisniku
     * @param trajanje vremensko trajanje obrade korisničkog zahtjeva u milisekundama
     */
    private void zapisiUDnevnik(ServletRequest request, ServletResponse response, long trajanje) {
        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setKorisnik("nepoznat");
        dnevnik.setUrl(((HttpServletRequest) request).getRequestURL().toString());
        dnevnik.setIpadresa(request.getRemoteAddr());
        dnevnik.setVrijeme(new Date(System.currentTimeMillis()));
        dnevnik.setTrajanje((int) trajanje);
        dnevnik.setStatus(((HttpServletResponse) response).getStatus());
        dnevnikFacade.create(dnevnik);
    }

}
