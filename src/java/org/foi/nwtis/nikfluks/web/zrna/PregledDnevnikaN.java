package org.foi.nwtis.nikfluks.web.zrna;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.inject.Named;
import org.foi.nwtis.nikfluks.ejb.eb.Dnevnik;
import org.foi.nwtis.nikfluks.ejb.sb.DnevnikFacade;
import org.foi.nwtis.nikfluks.web.podaci.DnevnikIFormatiraniDatum;

/**
 * Klasa je Manage Bean, sesijskog dosega. Služi za obradu korisničkih zahtjeva.
 *
 * @author Nikola
 * @version 1
 */
@Named(value = "dp")
@SessionScoped
public class PregledDnevnikaN implements Serializable {

    @EJB
    private DnevnikFacade dnevnikFacade;

    private String ipAdresa;
    private String odDatum;
    private String doDatum;
    private String urlAdresa;
    private String trajanje;
    private List<Dnevnik> listaDnevnika = new ArrayList();
    private boolean prviPut = true;
    private Date odDat;
    private Date doDat;
    private List<DnevnikIFormatiraniDatum> listaDnevnikaFormatirana = new ArrayList<>();

    /**
     * Konstruktor klase.
     */
    public PregledDnevnikaN() {
    }

    /**
     * Proslijeđuje grešku koja se prikazuje na ekranu korisnika.
     *
     * @param greska greška koja se prikazuje
     */
    public void proslijediGresku(String greska) {
        FacesMessage facesGreska = new FacesMessage(greska);
        FacesContext.getCurrentInstance().addMessage(null, facesGreska);
    }

    /**
     * Dohvaća podatke iz tablice Dnevnik koji zadovoljavaju unesene filtere od korisnika. Najprije provjera je li pod input
     * trajanje unesen cijeli broj, a ne slovo, znak, decimalni broj i sl. Ako jest, još provjerava je li broj >= 0, jer trajanje
     * zahjeva ne može biti negativno, a ako nije baca iznimku NumberFormatException.
     *
     * @return prazni string
     */
    public String preuzmiPodatke() {
        try {
            listaDnevnika.clear();
            provjeriDatum();
            int traj = -1;
            if (trajanje != null && !trajanje.equals("")) {
                traj = Integer.parseInt(trajanje);
                if (traj < 0) {
                    proslijediGresku("Trajanje ne smije biti negativan broj!");
                } else {
                    listaDnevnika = dnevnikFacade.filtriraj(ipAdresa, odDat, doDat, urlAdresa, traj);
                    prebaciUNovuListu();
                }
            } else {
                listaDnevnika = dnevnikFacade.filtriraj(ipAdresa, odDat, doDat, urlAdresa, traj);
                prebaciUNovuListu();
            }
        } catch (Exception e) {
            proslijediGresku("Pogrešni ulazni podaci!");
        }
        return "";
    }

    /**
     * Za svaki dohvaćeni zapis iz dnevnika uzima se vrijeme i formatira se u HR stil kako bi se tako formatirano vrijeme onda i
     * prikazalo korisniku na ekranu u obliku tablice, uz ostale podatke iz dnevnika.
     */
    private void prebaciUNovuListu() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss EEEE");
        listaDnevnikaFormatirana.clear();

        for (Dnevnik d : listaDnevnika) {
            String datum = sdf.format(d.getVrijeme());
            DnevnikIFormatiraniDatum dfd = new DnevnikIFormatiraniDatum(d, datum);
            listaDnevnikaFormatirana.add(dfd);
        }
    }

    /**
     * Provjerava jesu li uneseni datumi "od" i "do" i jesu li u zadanim formatima. Ako nije unesen "od" datum onda se uzima prvi
     * datum (1.1.1970 00:00:00). Ako jest unesen, onda se pokuša parsirati u jedan od zadanih oblika. Ako se ne uspije parsirati
     * niti u jedan baca se iznimka ParseException. Ako nije unesen "do" datum, uzima se trenutni datum (i vrijeme). Ako jest
     * unesen, onda se pokuša parsirati u jedan od zadanih oblika. Ako se ne uspije parsirati niti u jedan baca se iznimka
     * ParseException.
     *
     * @throws ParseException iznimka ParseException
     */
    private void provjeriDatum() throws ParseException {
        SimpleDateFormat sdfDat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat sdfDatVrij = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        sdfDat.setLenient(false);
        sdfDatVrij.setLenient(false);

        if (odDatum != null && !odDatum.equals("")) {
            try {
                odDat = sdfDatVrij.parse(odDatum);
            } catch (ParseException e) {
                odDat = sdfDat.parse(odDatum);
            }
        } else {
            odDat = new Date(0);
        }
        if (doDatum != null && !doDatum.equals("")) {
            try {
                doDat = sdfDatVrij.parse(doDatum);
            } catch (ParseException e) {
                doDat = sdfDat.parse(doDatum);
            }
        } else {
            doDat = new Date(System.currentTimeMillis());
        }
    }

    /**
     * Getter za IP adresu.
     *
     * @return IP adresa
     */
    public String getIpAdresa() {
        return ipAdresa;
    }

    /**
     * Setter za IP adresu.
     *
     * @param ipAdresa IP adresa koja se postavlja
     */
    public void setIpAdresa(String ipAdresa) {
        this.ipAdresa = ipAdresa;
    }

    /**
     * Getter za URL adresu.
     *
     * @return URL adresa
     */
    public String getUrlAdresa() {
        return urlAdresa;
    }

    /**
     * Setter za URL adresu.
     *
     * @param urlAdresa URL adresa koja se postavlja
     */
    public void setUrlAdresa(String urlAdresa) {
        this.urlAdresa = urlAdresa;
    }

    /**
     * Getter za odDatum.
     *
     * @return odDatum
     */
    public String getOdDatum() {
        return odDatum;
    }

    /**
     * Setter za odDatum.
     *
     * @param odDatum odDatum koji se postavlja
     */
    public void setOdDatum(String odDatum) {
        this.odDatum = odDatum;
    }

    /**
     * Getter za doDatum.
     *
     * @return doDatum
     */
    public String getDoDatum() {
        return doDatum;
    }

    /**
     * Setter za doDatum.
     *
     * @param doDatum doDatum koji se postavlja
     */
    public void setDoDatum(String doDatum) {
        this.doDatum = doDatum;
    }

    /**
     * Getter za listaDnevnika.
     *
     * @return listaDnevnika
     */
    public List<Dnevnik> getListaDnevnika() {
        return listaDnevnika;
    }

    /**
     * Setter za listaDnevnika.
     *
     * @param listaDnevnika listaDnevnika koja se postavlja
     */
    public void setListaDnevnika(List<Dnevnik> listaDnevnika) {
        this.listaDnevnika = listaDnevnika;
    }

    /**
     * Getter za listaDnevnikaFormatirana. Ako je korisnik prvi put došao na stranicu, pokazuju se svi podaci iz dnevnika.
     *
     * @return
     */
    public List<DnevnikIFormatiraniDatum> getListaDnevnikaFormatirana() {
        if (prviPut) {
            listaDnevnika.clear();
            listaDnevnika = dnevnikFacade.findAll();
            prviPut = false;
            prebaciUNovuListu();
        }
        return listaDnevnikaFormatirana;
    }

    /**
     * Setter za listaDnevnikaFormatirana.
     *
     * @param listaDnevnikaFormatirana listaDnevnikaFormatirana koja se postavlja
     */
    public void setListaDnevnikaFormatirana(List<DnevnikIFormatiraniDatum> listaDnevnikaFormatirana) {
        this.listaDnevnikaFormatirana = listaDnevnikaFormatirana;
    }

    /**
     * Getter za trajanje.
     *
     * @return trajanje
     */
    public String getTrajanje() {
        return trajanje;
    }

    /**
     * Setter za trajanje.
     *
     * @param trajanje trajanje koje se postavlja.
     */
    public void setTrajanje(String trajanje) {
        this.trajanje = trajanje;
    }

}
