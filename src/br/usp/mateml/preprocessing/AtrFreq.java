/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.mateml.preprocessing;

/**
 *
 * @author rafael
 */
public class AtrFreq {
    public String atributo;
    public Integer frequencia;

    public AtrFreq(String atr, Integer freq){
        atributo = new String(atr);
        frequencia = freq;
    }
}
