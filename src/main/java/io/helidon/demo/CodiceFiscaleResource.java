package io.helidon.demo;

import java.lang.Exception;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/evalcf")
@RequestScoped
public class CodiceFiscaleResource {

  /**
   * Initialize the town codes from json file
   */
  private static JsonObject townCodes = Json.createObjectBuilder().build();
  static {
    try {
      // Read the json file directly from jar archive
      JsonReader reader = Json.createReader(  Main.class.getResourceAsStream("/comuni.json"));
      JsonObject jsonData = reader.readObject();
      reader.close();
      townCodes = jsonData;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Initialize the months codes
   */
  private static final char[] months = {'A', 'B', 'C', 'D', 'E', 'H', 'L', 'M', 'P', 'R', 'S', 'T'};

  /**
   * Initialize the controlChar array
   */
  private static final String controlChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  /**
   * Initialize the control char odd table
   */
  private static final Map<Character, Integer> controlCharOdd;
  static {
    Map<Character, Integer> _map = new HashMap<>();
    _map.put('0', 1);  _map.put('1', 0);  _map.put('2', 5);  _map.put('3', 7);  _map.put('4', 9);  _map.put('5', 13); _map.put('6', 15); _map.put('7', 17); _map.put('8', 19);
    _map.put('9', 21); _map.put('A', 1);  _map.put('B', 0);  _map.put('C', 5);  _map.put('D', 7);  _map.put('E', 9);  _map.put('F', 13); _map.put('G', 15); _map.put('H', 17);
    _map.put('I', 19); _map.put('J', 21); _map.put('K', 2);  _map.put('L', 4);  _map.put('M', 18); _map.put('N', 20); _map.put('O', 11); _map.put('P', 3);  _map.put('Q', 6);
    _map.put('R', 8);  _map.put('S', 12); _map.put('T', 14); _map.put('U', 16); _map.put('V', 10); _map.put('W', 22); _map.put('X', 25); _map.put('Y', 24); _map.put('Z', 23);
    controlCharOdd = Collections.unmodifiableMap(_map);
  };

  /**
   * Initialize the control char even table
   */
  private static final Map<Character, Integer> controlCharEven;
  static {
    Map<Character, Integer> _map = new HashMap<>();
    _map.put('0', 0);  _map.put('1', 1);  _map.put('2', 2);  _map.put('3', 3);  _map.put('4', 4);  _map.put('5', 5);  _map.put('6', 6);  _map.put('7', 7);  _map.put('8', 8);
    _map.put('9', 9);  _map.put('A', 0);  _map.put('B', 1);  _map.put('C', 2);  _map.put('D', 3);  _map.put('E', 4);  _map.put('F', 5);  _map.put('G', 6);  _map.put('H', 7);
    _map.put('I', 8);  _map.put('J', 9);  _map.put('K', 10); _map.put('L', 11); _map.put('M', 12); _map.put('N', 13); _map.put('O', 14); _map.put('P', 15); _map.put('Q', 16);
    _map.put('R', 17); _map.put('S', 18); _map.put('T', 19); _map.put('U', 20); _map.put('V', 21); _map.put('W', 22); _map.put('X', 23); _map.put('Y', 24); _map.put('Z', 25);
    controlCharEven = Collections.unmodifiableMap(_map);
  };

  private static Object getKeyFromValue(JsonObject hashMap, String value) {
    for (String o : hashMap.keySet()) {
      String _value = hashMap.getString(o);
      _value = _value.substring(0,_value.length()-5);
      if (_value.equals(value)) {
        return o;
      }
    }
    return null;
  }

  private static String getConsonants(String str)
  {
    return str.toUpperCase().replaceAll("[^BCDFGHJKLMNPQRSTVWXYZ]", "");
  }

  private static String getVowels(String str)
  {
    return str.toUpperCase().replaceAll("[^AEIOU]", "");
  }

  /**
   * Evaluate Control Char for Codice Fiscale (tax code)
   */
  private Character evalControlChar(String taxCode)
  {
    int val = 0;
    for (int i = 0; i < 15; i++) {
      char c = taxCode.charAt(i);
      if ( (i % 2) == 0 ) // Logica inversa: numeri pari i % 2 == 0 ma 0 e' false quindi sono dispari, oppure si inizia con 1
        //val += control_char_even.get(c);
        val += controlCharOdd.get(c);
      else
        //val += control_char_odd.get(c);
        val += controlCharEven.get(c);
    }
    val = val % 26;

    return controlChar.charAt(val);
  }

  private String evalSurnameCode(String surname)
    {
      String surnameCode = getConsonants(surname);
      surnameCode += getVowels(surname);
      surnameCode += "XXX";
      surnameCode = surnameCode.substring(0, 3);
      return surnameCode.toUpperCase();
    }

  private String evalNameCode(String name)
  {
    String nameCode = getConsonants(name);
    if (nameCode.length() >= 4) {
      nameCode =
        String.valueOf(nameCode.charAt(0)) +
        String.valueOf(nameCode.charAt(2)) +
        String.valueOf(nameCode.charAt(3));
    } else {
      nameCode += getVowels(name);
      nameCode += "XXX";
      nameCode = nameCode.substring(0, 3);
    }
    return nameCode.toUpperCase();
  }

  private String evalDateCode(int dd, int mm, int yy, char gender)
  {
    Calendar calendar = new GregorianCalendar(yy, mm, dd);
    int year       = calendar.get(Calendar.YEAR);
    int month      = calendar.get(Calendar.MONTH); // Jan = 0, dec = 11
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

    String _year = "0" + String.valueOf(year);
    _year = _year.substring(_year.length() - 2, _year.length());

    Character _month = months[month-1];

    if (Character.toUpperCase(gender) == 'F')
      dayOfMonth += 40;

    String day = "0" + String.valueOf(dayOfMonth);
    day = day.substring(day.length() - 2, day.length());

    return "" + _year + _month + day;
  }

  private String evalTownCode(String town)
  {
    //Inserire eventualmente gestione accenti
    return getKeyFromValue(townCodes, town.toUpperCase()).toString();
  }

  private String evalTaxCode(String name, String surname, char gender, int day, int month, int year, String town)
  {
    String taxCode =
      evalSurnameCode(surname) +
      evalNameCode(name) +
      evalDateCode(day, month, year, gender) +
      evalTownCode(town);
    taxCode += evalControlChar(taxCode);
    return taxCode;
  }

  /**
   * Using constructor injection to get a configuration property.
   * By default this gets the value from META-INF/microprofile-config
   * @param xxx the configured greeting message
   */
  @Inject
  public CodiceFiscaleResource() {
  }

  /**
   * Return a generated tax code.
   * @return {@link JsonObject}
   */
  @SuppressWarnings("checkstyle:designforextension")
  @Path("/")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject getTaxCode(final JsonObject jsonObject)
  {
    String name = jsonObject.getString("name");
    String surname = jsonObject.getString("surname");
    Character gender = jsonObject.getString("gender").charAt(0);
    int day = Integer.parseInt(jsonObject.getString("day"));
    int month = Integer.parseInt(jsonObject.getString("month"));
    int year = Integer.parseInt(jsonObject.getString("year"));
    String town = jsonObject.getString("town");

    String msg = evalTaxCode(name, surname, gender, day, month, year, town);
    JsonObject returnObject = Json.createObjectBuilder()
            .add("taxCode", msg)
            .build();
    return returnObject;
  }

  // Test with:
  // curl -H "Content-Type: application/json" -X POST -d "{ \"name\": \"Mario\", \"surname\": \"Rossi\", \"gender\": \"M\", \"day\": \"01\", \"month\": \"01\", \"year\": \"1970\", \"town\": \"Roma\" }" http://localhost:8080/evalcf

}
