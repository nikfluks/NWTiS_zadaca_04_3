package org.foi.nwtis.nikfluks.web.podaci;

import org.foi.nwtis.nikfluks.ejb.eb.Dnevnik;

/**
 * Klasa služi za prikazivanje podataka iz tablice Dnevnik te ima poseban atribut datum koji je zapravo vrijednost iz stupca Vrijeme
 * iz tablice Dnevnik, ali formatiran u HR stilu.
 *
 * @author Nikola
 * @version 1
 */
public class DnevnikIFormatiraniDatum {

    private Dnevnik dnevnik;
    private String datum;

    /**
     * Prazan konstruktor klase.
     */
    public DnevnikIFormatiraniDatum() {
    }

    /**
     * Konstruktor sa 2 parametra.
     *
     * @param dnevnik parametar Dnevnik
     * @param datum parameter datum koji je formatiran u HR stilu
     */
    public DnevnikIFormatiraniDatum(Dnevnik dnevnik, String datum) {
        this.dnevnik = dnevnik;
        this.datum = datum;
    }

    /**
     * Getter za dnevnik.
     *
     * @return dnevnik
     */
    public Dnevnik getDnevnik() {
        return dnevnik;
    }

    /**
     * Setter za dnevnik.
     *
     * @param dnevnik dnevnik koji se postavlja
     */
    public void setDnevnik(Dnevnik dnevnik) {
        this.dnevnik = dnevnik;
    }

    /**
     * Getter za datum.
     *
     * @return datum
     */
    public String getDatum() {
        return datum;
    }

    /**
     * Setter za formatirani datum.
     *
     * @param datum formatirani datum koji se postavlja
     */
    public void setDatum(String datum) {
        this.datum = datum;
    }

}
