package com.modyo.services.utils;

import com.modyo.services.exceptions.CustomValidationException;
import com.modyo.services.exceptions.dto.RejectionDto;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import lombok.Getter;

/**
 * Clase utilitaria para el Rol Unico Tributario (RUT) que implementa la logica de validación, generación de dígitos verificadores
 * y entrega de texto formateado.
 */
@Getter
public class Rut {

  private String numeroString;
  private Integer numeroInt;
  private String dv; // dígito verificador

  public Rut(String rut) {
    this(rut, true);
  }

  public Rut(String rut, Boolean includesDv) {
    String rutAux = rut
      .replace(".", "")
      .replace("-", "")
      .replaceAll("\\t", "")
      .replaceAll("\\n", "")
      .replaceAll(" ", "");
    if (includesDv) {
      setNumero(rutAux.substring(0, rutAux.length() - 1));
      this.dv = rutAux.substring(rutAux.length() - 1).toUpperCase();
    } else {
      setNumero(rutAux);
      this.dv = calcDigitoVerificador();
    }
    validate();
  }

  /**
   * @return RUT with format xxxxxxxxy
   */
  public String unformatted() {
    return numeroString + dv;
  }

  /**
   * @return RUT with format xxxxxxxx-y
   */
  public String formattedWithoutPoints() {
    return formatted(false);
  }

  /**
   * @return RUT with format xx.xxx.xxx-y
   */
  public String formattedWithPoints() {
    return formatted(true);
  }

  public boolean isJuridico() {
    return numeroInt >= 50000000;
  }

  public boolean isNatural() {
    return numeroInt < 50000000;
  }

  private void setNumero(String numero) {
    try {
      numeroString = numero;
      numeroInt = Integer.parseInt(numero);
    } catch (Exception e) {
      throwValidationException();
    }
  }

  private void validate() {
    Pattern pattern = Pattern.compile("^[1-9][0-9]{5,7}[0-9kK]{1}$");
    if (!pattern.matcher(unformatted()).matches() || numeroInt == 0 || !dv.equals(calcDigitoVerificador())) {
      throwValidationException();
    }
  }

  private String calcDigitoVerificador() {
    int m = 0;
    int s = 1;
    for (; numeroInt != 0; numeroInt /= 10) {
      s = (s + numeroInt % 10 * (9 - m++ % 6)) % 11;
    }
    return String.valueOf((char) (s != 0 ? s + 47 : 75)).toUpperCase();
  }

  private String formatted(boolean withPoints) {
    return (withPoints ? new DecimalFormat("###,###.###").format(numeroInt) : numeroString) + "-" + dv;
  }

  private void throwValidationException() {
    throw new CustomValidationException(
      RejectionDto.builder()
        .source("rut")
        .detail("rut inválido")
        .build()
    );
  }

}
