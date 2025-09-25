package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        String result = "";
        
        try {
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> full = reader.readAll();
            JsonObject json = new JsonObject();

            String[] headers = full.get(0);
            json.put("ColHeadings", headers);
            
            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();

            for (int i = 1; i < full.size(); i++) {
                String[] row = full.get(i);
                prodNums.add(row[0]);
                
                JsonArray array = new JsonArray();
                for(int j = 1; j < row.length; j++) {
                    String header = headers[j];
                   if(header.equals("Season") || header.equals("Episode")){
                       array.add(Integer.valueOf(row[j]));
                   }       
                   else {
                       array.add(row[j]);
                   }
                }
                data.add(array);
            }
            json.put("ProdNums", prodNums);  
            json.put("Data", data);
            
            result = Jsoner.serialize(json).trim();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
            return result;    
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = "";
        
        try {
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);
            JsonObject json = (JsonObject) Jsoner.deserialize(jsonString);
            
            List<String[]> all = new ArrayList<>();
            JsonArray colHeadings = (JsonArray) json.get("ColHeadings");
            JsonArray prodNums = (JsonArray) json.get("ProdNums");
            JsonArray data = (JsonArray) json.get("Data");
            
            String[] headerRow = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++){
                headerRow[i] = colHeadings.get(i).toString();
            }
            all.add(headerRow);
            
            for (int i = 0; i < data.size(); i++){
                JsonArray row = (JsonArray) data.get(i);
                String[] strRow = new String[row.size() + 1];
                strRow[0] = prodNums.get(i).toString();
                for (int j = 0; j < row.size(); j++){
                   Object value = row.get(j);
                   String header = headerRow[j];
                   if (value instanceof Number){
                       if(header.equals("Season") || header.equals("Episode")){
                        strRow[j + 1] = String.format("%02d", ((Number) value).intValue());
                       }
                       else{
                        strRow[j + 1] = value.toString();
                       }
                        }
                   else{
                    strRow[j + 1] = value.toString();
                   }
                }
            all.add(strRow);
            }
            
            csvWriter.writeAll(all);
            csvWriter.close();
            result = writer.toString();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();        
    }
    
}
