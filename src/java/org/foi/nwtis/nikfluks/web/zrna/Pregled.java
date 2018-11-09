package org.foi.nwtis.nikfluks.web.zrna;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.foi.nwtis.nikfluks.ejb.sb.ParkiralistaFacade;
import org.foi.nwtis.nikfluks.ejb.eb.Parkiralista;
import org.foi.nwtis.nikfluks.ejb.sb.MeteoKlijentZrno;
import org.foi.nwtis.nikfluks.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.nikfluks.rest.klijenti.GMKlijent;
import org.foi.nwtis.nikfluks.rest.klijenti.OWMKlijent;
import org.foi.nwtis.nikfluks.web.kontrole.Izbornik;
import org.foi.nwtis.nikfluks.web.podaci.Lokacija;
import org.foi.nwtis.nikfluks.web.podaci.MeteoPrognoza;
import org.foi.nwtis.nikfluks.web.podaci.ParkiralisteIPrognoza;
import org.foi.nwtis.nikfluks.web.slusaci.SlusacAplikacije;

/**
 * Klasa je Manage Bean, sesijskog dosega. Služi za obradu korisničkih zahtjeva.
 *
 * @author Nikola
 * @version 1
 */
@Named(value = "pregled")
@SessionScoped
public class Pregled implements Serializable {

    @EJB
    private MeteoKlijentZrno meteoKlijentZrno;

    @EJB
    private ParkiralistaFacade parkiralistaFacade;

    private Integer id;
    private String naziv;
    private String adresa;
    private List<Izbornik> popisPostojecihParkiralista = new ArrayList<>();
    private List<String> listaOdabranihParkiralista = new ArrayList<>();
    private List<Izbornik> popisOdabranihParkiralista = new ArrayList<>();
    private List<String> listaOdabranihParkiralistaZaVracanje = new ArrayList<>();
    private List<ParkiralisteIPrognoza> popisParkiralistaIMeteoPodataka = new ArrayList<>();
    private String greska = "";
    private boolean prikaziGumbUpisi = false;
    private boolean prikaziGumbAzuriraj = false;
    private boolean prikaziPrognoze = false;
    private boolean prikaziGumbPrognoze = false;
    private String gumbPrognozeNaziv = "Prognoze";
    private String gmapikey;
    private String apikey;
    private OWMKlijent owmk;
    private GMKlijent gmk;

    /**
     * Konstruktor klase.
     */
    public Pregled() {
        dohvatiPodatkeIzKonfiguracije();
    }

    /**
     * Proslijeđuje grešku koja se prikazuje na ekranu korisnika.
     *
     * @param greska greška koja se prikazuje
     */
    public void proslijediGresku(String greska) {
        this.greska = greska;
        FacesMessage facesGreska = new FacesMessage(greska);
        FacesContext.getCurrentInstance().addMessage(null, facesGreska);
    }

    /**
     * Metoda služi za dohvat podataka iz kofiguracijske datoteke.
     *
     * @return true ako su uspješno dohvaćeni podaci, inače false
     */
    private boolean dohvatiPodatkeIzKonfiguracije() {
        try {
            BP_Konfiguracija bpk = (BP_Konfiguracija) SlusacAplikacije.getServletContext().getAttribute("BP_Konfig");
            apikey = bpk.getApiKey();
            owmk = new OWMKlijent(apikey);
            gmapikey = bpk.getGmApiKey();
            gmk = new GMKlijent(gmapikey);
            return true;
        } catch (Exception ex) {
            proslijediGresku("Greška kod dohvaćanja podataka iz konfiguracije!");
            return false;
        }
    }

    /**
     * Prazni polja za unos (id, naziv, adresa).
     */
    public void isprazniPolja() {
        setId(null);
        setNaziv("");
        setAdresa("");
    }

    /**
     * Provjera veličinu liste listaOdabranihParkiralista, te ako je veličina 1 postavlja prikaziGumbAzuriraj na true, inače na
     * false.
     */
    public void provjeriGumbAzuriraj() {
        if (listaOdabranihParkiralista.size() == 1) {
            prikaziGumbAzuriraj = true;
        } else {
            prikaziGumbAzuriraj = false;
        }
    }

    /**
     * Provjerava je li popis popisOdabranihParkiralista prazan, te ako je postavlja prikaziGumbPrognoze na false, inače na true.
     */
    public void provjeriGumbPrognoze() {
        if (popisOdabranihParkiralista.isEmpty()) {
            prikaziGumbPrognoze = false;
        } else {
            prikaziGumbPrognoze = true;
        }
    }

    /**
     * Na svaki pritisak na gumb Prognoze mjenja se naziv gumba naizmjenično Prognoza i Zatvori prognoze.
     */
    public void provjeriGumbPrognozeNaziv() {
        if (prikaziPrognoze) {
            prikaziPrognoze = false;
            gumbPrognozeNaziv = "Prognoze";
        } else {
            prikaziPrognoze = true;
            gumbPrognozeNaziv = "Zatvori prognoze";
        }
    }

    /**
     * Prazni liste listaOdabranihParkiralista i listaOdabranihParkiralistaZaVracanje te zove metodu za provjeru prikaza gumba
     * Ažuriraj.
     */
    public void ocistiListeIProvjeriGumbAzuriraj() {
        listaOdabranihParkiralista.clear();
        listaOdabranihParkiralistaZaVracanje.clear();
        provjeriGumbAzuriraj();
    }

    /**
     * Dodaje novo parkiralište u bazu s unesem podacima te prazni polja za unos.
     *
     * @return prazni string
     */
    public String dodajParkiraliste() {
        try {
            Parkiralista p = new Parkiralista();
            p.setId(id);
            p.setNaziv(naziv);
            p.setAdresa(adresa);
            Lokacija lok = gmk.getGeoLocation(p.getAdresa());
            p.setLatitude(Float.parseFloat(lok.getLatitude()));
            p.setLongitude(Float.parseFloat(lok.getLongitude()));
            parkiralistaFacade.create(p);
            isprazniPolja();
            prikaziGumbUpisi = false;
        } catch (Exception e) {
            proslijediGresku("Dodavanje nije uspjelo!");
        }
        return "";
    }

    /**
     * Ažurira postojeće parkiralište u bazi temeljem upisanog id-a te prazni polja za unos.
     *
     * @return prazni string
     */
    public String upisiParkiraliste() {
        if (id != null) {
            try {
                Parkiralista p = new Parkiralista();
                p.setId(id);
                p.setNaziv(naziv);
                p.setAdresa(adresa);
                Lokacija lok = gmk.getGeoLocation(p.getAdresa());
                p.setLatitude(Float.parseFloat(lok.getLatitude()));
                p.setLongitude(Float.parseFloat(lok.getLongitude()));
                parkiralistaFacade.edit(p);
                isprazniPolja();
                prikaziGumbUpisi = false;
            } catch (Exception e) {
                prikaziGumbUpisi = true;
                proslijediGresku("Ažuriranje nije uspjelo!");
            }
        } else {
            prikaziGumbUpisi = true;
            proslijediGresku("Id nije unesen!");
        }
        ocistiListeIProvjeriGumbAzuriraj();
        return "";
    }

    /**
     * Prebacuje parkirališta iz postojećih u odabrana.
     *
     * @return prazni string
     */
    public String preuzmiParkiralista() {
        for (Izbornik i : popisPostojecihParkiralista) {
            if (listaOdabranihParkiralista.contains(i.getVrijednost())) {
                popisOdabranihParkiralista.add(i);
            }
        }
        provjeriGumbPrognoze();
        ocistiListeIProvjeriGumbAzuriraj();
        return "";
    }

    /**
     * Prebacuje parkirališta iz odabranih u postojeća te briše iz liste odabranih parkirališta ona koja su vraćena u postojeća.
     *
     * @return prazni string
     */
    public String vratiParkiralista() {
        Iterator it = popisOdabranihParkiralista.iterator();
        while (it.hasNext()) {
            Izbornik i = (Izbornik) it.next();
            if (listaOdabranihParkiralistaZaVracanje.contains(i.getVrijednost())) {
                popisPostojecihParkiralista.add(i);
                it.remove();
            }
        }
        provjeriGumbPrognoze();
        ocistiListeIProvjeriGumbAzuriraj();
        return "";
    }

    /**
     * Ako je broj odabranih postojećih parkirališta jednak 1, onda se podaci tog odabranog parkirališta prebacuju u polja za unos.
     *
     * @return prazni string
     */
    public String azurirajParkiralista() {
        if (listaOdabranihParkiralista.size() == 1) {
            int id = Integer.parseInt(listaOdabranihParkiralista.get(0));
            Parkiralista p = parkiralistaFacade.findById(id);
            this.id = p.getId();
            this.naziv = p.getNaziv();
            this.adresa = p.getAdresa();
            prikaziGumbUpisi = true;
        } else {
            prikaziGumbUpisi = false;
        }
        return "";
    }

    /**
     * Iterira kroz preuzete prognoze sa web servisa te ih dodaje u popisParkiralistaIMeteoPodataka. Podaci iz tog popisa prikazuju
     * korisniku u obliku tablice.
     *
     * @return prazni string
     */
    public String preuzmiPrognoze() {
        provjeriGumbPrognozeNaziv();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss EEEE");
        popisParkiralistaIMeteoPodataka.clear();

        if (prikaziPrognoze) {
            List<MeteoPrognoza[]> listaPrognoza = dohvatiPrognozu();

            for (int j = 0; j < listaPrognoza.size(); j++) {
                MeteoPrognoza[] mp = listaPrognoza.get(j);
                int id = mp[j].getId();
                Parkiralista p = parkiralistaFacade.findById(id);

                for (int i = 0; i < mp.length; i++) {
                    String datum = sdf.format(mp[i].getPrognoza().getLastUpdate());
                    ParkiralisteIPrognoza pip = new ParkiralisteIPrognoza(p.getId(), p.getNaziv(), p.getAdresa(),
                            mp[i].getPrognoza(), datum);
                    popisParkiralistaIMeteoPodataka.add(pip);
                }
            }
        }
        return "";
    }

    /**
     * Dohvaća 5-odnevnu prognozu za svaka 3 sata sa web servisa za odabrana parkirališta. Dodaje dohvaćenu prognozu u listu
     * listaPrognoza.
     *
     * @return lista prognoza za odabrana parkirališta
     */
    private List<MeteoPrognoza[]> dohvatiPrognozu() {
        List<MeteoPrognoza[]> listaPrognoza = new ArrayList<>();

        for (Izbornik i : popisOdabranihParkiralista) {
            int id = Integer.parseInt(i.getVrijednost());
            Parkiralista p = parkiralistaFacade.findById(id);
            meteoKlijentZrno.postaviKorisnickePodatke(apikey, gmapikey);
            MeteoPrognoza[] meteoPrognoze = meteoKlijentZrno.dajMeteoPrognoze(id, p.getAdresa());
            if (meteoPrognoze != null) {
                listaPrognoza.add(meteoPrognoze);
            }
        }
        return listaPrognoza;
    }

    /**
     * Getter za id parkirališta.
     *
     * @return id parkirališta
     */
    public Integer getId() {
        return id;
    }

    /**
     * Setter za id parkirališta.
     *
     * @param id id koji se postavlja
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter za naziv parkirališta.
     *
     * @return naziv parkirališta
     */
    public String getNaziv() {
        return naziv;
    }

    /**
     * Setter za naziv parkirališta.
     *
     * @param naziv naziv koji se postavlja
     */
    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    /**
     * Getter za adresu parkirališta.
     *
     * @return adresa parkirališta
     */
    public String getAdresa() {
        return adresa;
    }

    /**
     * Setter za adresu parkirališta.
     *
     * @param adresa adresa koja se postavlja
     */
    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    /**
     * Getter za popisPostojecihParkiralista. Dohvaća sva parkirališta iz baze te ih dodaje u popis popisPostojecihParkiralista. Ona
     * parkirališta koja se već nalaze u popisu popisOdabranihParkiralista se brišu iz popisa popisPostojecihParkiralista.
     *
     * @return popis popisPostojecihParkiralista
     */
    public List<Izbornik> getPopisPostojecihParkiralista() {
        popisPostojecihParkiralista.clear();
        for (Parkiralista p : parkiralistaFacade.findAll()) {
            Izbornik i = new Izbornik(p.getNaziv(), Integer.toString(p.getId()));
            popisPostojecihParkiralista.add(i);
        }

        if (!popisPostojecihParkiralista.isEmpty()) {
            for (Izbornik i : popisOdabranihParkiralista) {
                if (popisPostojecihParkiralista.contains(i)) {
                    popisPostojecihParkiralista.remove(i);
                }
            }
        }
        return popisPostojecihParkiralista;
    }

    /**
     * Setter za popisPostojecihParkiralista.
     *
     * @param popisPostojecihParkiralista popisPostojecihParkiralista koji se postavlja
     */
    public void setPopisPostojecihParkiralista(List<Izbornik> popisPostojecihParkiralista) {
        this.popisPostojecihParkiralista = popisPostojecihParkiralista;
    }

    /**
     * Getter za listaOdabranihParkiralista.
     *
     * @return listaOdabranihParkiralista
     */
    public List<String> getListaOdabranihParkiralista() {
        return listaOdabranihParkiralista;
    }

    /**
     * Setter za listaOdabranihParkiralista.
     *
     * @param listaOdabranihParkiralista listaOdabranihParkiralista koja postavlja
     */
    public void setListaOdabranihParkiralista(List<String> listaOdabranihParkiralista) {
        this.listaOdabranihParkiralista = listaOdabranihParkiralista;
    }

    /**
     * Getter za popisOdabranihParkiralista. Sortira popis popisOdabranihParkiralista uzlazno, case insensitive.
     *
     * @return popisOdabranihParkiralista
     */
    public List<Izbornik> getPopisOdabranihParkiralista() {
        Collections.sort(popisOdabranihParkiralista, (a, b) -> a.getLabela().compareToIgnoreCase(b.getLabela()));
        return popisOdabranihParkiralista;
    }

    /**
     * Setter za popisOdabranihParkiralista.
     *
     * @param popisOdabranihParkiralista popisOdabranihParkiralista koji se postavlja
     */
    public void setPopisOdabranihParkiralista(List<Izbornik> popisOdabranihParkiralista) {
        this.popisOdabranihParkiralista = popisOdabranihParkiralista;
    }

    /**
     * Getter za grešku.
     *
     * @return greška
     */
    public String getGreska() {
        return greska;
    }

    /**
     * Setter za grešku.
     *
     * @param greska greška koja se postavlja
     */
    public void setGreska(String greska) {
        this.greska = greska;
    }

    /**
     * Getter za listaOdabranihParkiralistaZaVracanje.
     *
     * @return listaOdabranihParkiralistaZaVracanje
     */
    public List<String> getListaOdabranihParkiralistaZaVracanje() {
        return listaOdabranihParkiralistaZaVracanje;
    }

    /**
     * Setter za listaOdabranihParkiralistaZaVracanje.
     *
     * @param listaOdabranihParkiralistaZaVracanje listaOdabranihParkiralistaZaVracanje koja se postavlja
     */
    public void setListaOdabranihParkiralistaZaVracanje(List<String> listaOdabranihParkiralistaZaVracanje) {
        this.listaOdabranihParkiralistaZaVracanje = listaOdabranihParkiralistaZaVracanje;
    }

    /**
     * Getter za prikaziGumbUpisi.
     *
     * @return prikaziGumbUpisi
     */
    public boolean isPrikaziGumbUpisi() {
        return prikaziGumbUpisi;
    }

    /**
     * Setter za prikaziGumbUpisi.
     *
     * @param prikaziGumbUpisi prikaziGumbUpisi koji se postavlja
     */
    public void setPrikaziGumbUpisi(boolean prikaziGumbUpisi) {
        this.prikaziGumbUpisi = prikaziGumbUpisi;
    }

    /**
     * Getter za prikaziPrognoze.
     *
     * @return prikaziPrognoze
     */
    public boolean isPrikaziPrognoze() {
        return prikaziPrognoze;
    }

    /**
     * Setter za prikaziPrognoze.
     *
     * @param prikaziPrognoze prikaziPrognoze koji se postavlja
     */
    public void setPrikaziPrognoze(boolean prikaziPrognoze) {
        this.prikaziPrognoze = prikaziPrognoze;
    }

    /**
     * Getter za prikaziGumbAzuriraj.
     *
     * @return prikaziGumbAzuriraj
     */
    public boolean isPrikaziGumbAzuriraj() {
        return prikaziGumbAzuriraj;
    }

    /**
     * Setter za prikaziGumbAzuriraj.
     *
     * @param prikaziGumbAzuriraj prikaziGumbAzuriraj koji se postavlja
     */
    public void setPrikaziGumbAzuriraj(boolean prikaziGumbAzuriraj) {
        this.prikaziGumbAzuriraj = prikaziGumbAzuriraj;
    }

    /**
     * Getter za gumbPrognozeNaziv.
     *
     * @return gumbPrognozeNaziv
     */
    public String getGumbPrognozeNaziv() {
        return gumbPrognozeNaziv;
    }

    /**
     * Setter za gumbPrognozeNaziv.
     *
     * @param gumbPrognozeNaziv gumbPrognozeNaziv koji se postavlja
     */
    public void setGumbPrognozeNaziv(String gumbPrognozeNaziv) {
        this.gumbPrognozeNaziv = gumbPrognozeNaziv;
    }

    /**
     * Getter za popisParkiralistaIMeteoPodataka.
     *
     * @return popisParkiralistaIMeteoPodataka
     */
    public List<ParkiralisteIPrognoza> getPopisParkiralistaIMeteoPodataka() {
        return popisParkiralistaIMeteoPodataka;
    }

    /**
     * Setter za popisParkiralistaIMeteoPodataka.
     *
     * @param popisParkiralistaIMeteoPodataka popisParkiralistaIMeteoPodataka koji se postavlja.
     */
    public void setPopisParkiralistaIMeteoPodataka(List<ParkiralisteIPrognoza> popisParkiralistaIMeteoPodataka) {
        this.popisParkiralistaIMeteoPodataka = popisParkiralistaIMeteoPodataka;
    }

    /**
     * Getter za prikaziGumbPrognoze.
     *
     * @return prikaziGumbPrognoze
     */
    public boolean isPrikaziGumbPrognoze() {
        return prikaziGumbPrognoze;
    }

    /**
     * Setter za prikaziGumbPrognoze.
     *
     * @param prikaziGumbPrognoze prikaziGumbPrognoze koji se postavlja
     */
    public void setPrikaziGumbPrognoze(boolean prikaziGumbPrognoze) {
        this.prikaziGumbPrognoze = prikaziGumbPrognoze;
    }

}
